
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.MessageRepository;
import domain.Actor;
import domain.Box;
import domain.Message;

@Service
@Transactional
public class MessageService {															//TODO AdministratorService, comprobar funcionalidad de este año

	// Managed repository -----------------------------------------------------

	@Autowired
	private MessageRepository	messageRepository;

	// Supporting services ----------------------------------------------------

	@Autowired
	private BoxService			boxService;

	//	@Autowired
	//	private AdministratorService	adminService;

	@Autowired
	private ActorService		actorService;


	//	@Autowired
	//	private Validator			validator;

	// Constructors -----------------------------------------------------------

	public MessageService() {
		super();
	}

	// Simple CRUD methods ----------------------------------------------------

	public Message create(final Actor recipient) {

		Message message;
		Actor actor;
		final Box folder;

		message = new Message();
		message.setMoment(new Date(System.currentTimeMillis() - 1000));

		actor = this.actorService.findByPrincipal();
		message.setSender(actor);
		message.setRecipient(recipient);

		folder = this.boxService.findByBoxName(actor.getUserAccount().getId(), "OUTBOX");
		final Collection<Box> boxes = new ArrayList<Box>();
		boxes.add(folder);
		message.setBoxes(boxes);

		return message;
	}

	public void delete(final Message message) {

		this.checkByPrincipal(message);

		Message saved;
		final Actor actor;
		Box trashbox;

		actor = this.actorService.findByPrincipal();
		trashbox = this.boxService.findByBoxName(actor.getUserAccount().getId(), "TRASHBOX");

		if (!message.getBoxes().contains(trashbox)) {
			final Collection<Box> newContent = new ArrayList<Box>();
			newContent.add(trashbox);
			message.setBoxes(newContent);
			saved = this.messageRepository.save(message);
			trashbox.getMessages().add(saved);
		} else
			this.messageRepository.delete(message);
	}

	public Message findOne(final int id) {

		Message result;

		result = this.messageRepository.findOne(id);

		return result;
	}

	public Message findOneToEdit(final int id) {

		Message result;

		result = this.findOne(id);
		this.checkByPrincipal(result);

		return result;
	}

	public Collection<Message> findAll() {
		return this.messageRepository.findAll();
	}

	public Message save(final Message message, Collection<Box> boxesToAdd, Collection<Box> boxesToRemove) {

		Assert.notNull(message);
		Assert.isTrue(message.getBoxes().equals(this.messageRepository.findOne(message.getId()).getBoxes()), "message.error.boxesEditedElsewhere");
		if (boxesToRemove == null)
			boxesToRemove = new ArrayList<Box>();
		if (boxesToAdd == null)
			boxesToAdd = new ArrayList<Box>();
		for (final Box b : boxesToRemove)
			this.boxService.checkPrincipal(b);

		Message saved = new Message();
		Box outboxSender, inboxRecipient;

		if (message.getId() == 0) {
			Assert.isTrue(!(message.getRecipient() == null || message.getRecipient().getId() == 0), "message.error.needsRecipient");
			final Date newMoment = new Date(System.currentTimeMillis() - 1000);

			inboxRecipient = this.boxService.findByBoxName(message.getRecipient().getUserAccount().getId(), "INBOX");
			outboxSender = this.boxService.findByBoxName(this.actorService.findByPrincipal().getUserAccount().getId(), "OUTBOX");
			saved.getBoxes().add(inboxRecipient);
			saved.getBoxes().add(outboxSender);
			saved = this.messageRepository.save(message);
			saved.setMoment(newMoment);
			inboxRecipient.getMessages().add(saved);
			outboxSender.getMessages().add(saved);
		} else {
			saved = this.messageRepository.save(message);
			for (final Box b : boxesToAdd) {
				b.getMessages().add(saved);
				message.getBoxes().add(b);
			}
			for (final Box b : boxesToRemove) {
				b.getMessages().remove(saved);
				message.getBoxes().remove(b);
			}
		}

		return saved;
	}

	public Message broadcast(final Message message) {

		Assert.notNull(message);
		//		Assert.notNull(this.adminService.findByPrincipal());											//TODO Hacer el AdministratorService

		Message saved = null;
		Box outboxSender = null;
		Box inboxRecipient;

		message.setMoment(new Date(System.currentTimeMillis() - 1000));
		outboxSender = this.boxService.findByBoxName(message.getSender().getUserAccount().getId(), "OUTBOX");
		final Collection<Box> tempBoxes = message.getBoxes();
		tempBoxes.add(outboxSender);
		message.setBoxes(tempBoxes);
		final Collection<Actor> recipients = this.actorService.findAll();
		recipients.remove(this.actorService.findByPrincipal());
		for (final Actor recipient : recipients) {
			message.setRecipient(recipient);

			inboxRecipient = this.boxService.findByBoxName(recipient.getUserAccount().getId(), "INBOX");
			tempBoxes.add(inboxRecipient);
			message.setBoxes(tempBoxes);
			saved = this.messageRepository.save(message);
			outboxSender.getMessages().add(saved);
			inboxRecipient.getMessages().add(saved);
		}

		return saved;
	}

	// Other business methods -------------------------------------------------

	//	public Message reconstruct(final Message messagePruned, final BindingResult binding) {
	//
	//		Assert.notNull(messagePruned);
	//		final Actor principal = this.actorService.findByPrincipal();
	//
	//		Message message;
	//
	//		if (messagePruned.getId() != 0) {
	//			final Box destinationFolder = messagePruned.getBox();
	//			message = this.findOne(messagePruned.getId());
	//			this.boxService.checkPrincipal(destinationFolder);
	//			message.setBox(destinationFolder);
	//		} else {
	//			message = this.create(null);
	//			message.setMoment(new Date(System.currentTimeMillis() - 1000));
	//			message.setSender(principal);
	//			message.setBox(this.boxService.findByBoxName(principal.getUserAccount().getId(), "OUTBOX"));
	//			message.setSubject(messagePruned.getSubject());
	//			message.setBody(messagePruned.getBody());
	//			message.setPriority(messagePruned.getPriority());
	//			message.setRecipient(messagePruned.getRecipient());
	//		}
	//
	//		//		if (binding != null)
	//		//			this.validator.validate(message, binding);
	//
	//		return message;
	//	}
	public Message copy(final Message message) {

		Assert.notNull(message);

		Message result;

		result = this.create(null);
		result.setSubject(message.getSubject());
		result.setBody(message.getBody());
		result.setMoment(message.getMoment());
		result.setPriority(message.getPriority());
		result.setRecipient(message.getRecipient());
		result.setSender(message.getSender());

		return result;
	}

	public Collection<Message> findByFolderId(final int folderId) {

		Collection<Message> result;

		final Box folder = this.boxService.findOne(folderId);
		this.boxService.checkPrincipal(folder);
		result = folder.getMessages();

		return result;
	}

	public void deleteByFolder(final Box folder) {

		final Collection<Message> messages = folder.getMessages();
		this.messageRepository.delete(messages);
	}

	public void moveMessageToFolder(final Message message, final Box source, final Box destination) {
		Assert.notNull(source);
		Assert.notNull(destination);
		Assert.notNull(message);
		this.boxService.checkPrincipal(source);
		this.boxService.checkPrincipal(destination);
		Assert.isTrue(!source.getMessages().contains(message));

		final Actor actor = this.actorService.findByPrincipal();

		Assert.isTrue(actor.getBoxes().contains(source));
		Assert.isTrue(actor.getBoxes().contains(destination));

		final List<Message> messages = new ArrayList<Message>(destination.getMessages());
		final List<Message> messages2 = new ArrayList<Message>(source.getMessages());

		messages.add(message);
		destination.setMessages(messages);
		messages2.remove(message);
		source.setMessages(messages2);

		this.boxService.save(destination);
		message.getBoxes().add(destination);
		this.save(message, null, null);
	}

	public void checkByPrincipal(final Message message) {

		Assert.notNull(message);

		final Actor actor = this.actorService.findByPrincipal();
		Assert.isTrue(message.getRecipient().equals(actor) || message.getSender().equals(actor));
	}

	public void flush() {
		this.messageRepository.flush();
	}

	public boolean exists(final int id) {
		return this.messageRepository.findOne(id) != null;
	}

}
