
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;

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

	public Message create() {

		Message message;
		Actor actor;
		final Box folder;

		message = new Message();
		message.setMoment(new Date(System.currentTimeMillis() - 1000));

		actor = this.actorService.findByPrincipal();
		message.setSender(actor);

		folder = this.boxService.findByBoxName(actor.getUserAccount().getId(), "OUTBOX");
		message.setBox(folder);

		return message;
	}

	public void delete(final Message message) {

		this.checkByPrincipal(message);

		Message saved;
		final Actor actor;
		Box trashbox;

		actor = this.actorService.findByPrincipal();
		trashbox = this.boxService.findByBoxName(actor.getUserAccount().getId(), "TRASHBOX");

		if (message.getBox() != trashbox) {
			message.setBox(trashbox);
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

	public Message save(final Message message) {

		Assert.notNull(message);
		this.boxService.checkPrincipal(message.getBox());

		Message saved, copy;
		Message savedCopy = null;
		Box outboxSender, inboxRecipient;

		if (message.getId() == 0) {
			Assert.isTrue(!(message.getRecipient() == null || message.getRecipient().getId() == 0), "message.error.needsRecipient");
			final Date newMoment = new Date(System.currentTimeMillis() - 1000);
			copy = this.copy(message);

			inboxRecipient = this.boxService.findByBoxName(copy.getRecipient().getUserAccount().getId(), "in box");
			copy.setBox(inboxRecipient);
			savedCopy = this.messageRepository.save(copy);
			savedCopy.setMoment(newMoment);
			inboxRecipient.getMessages().add(savedCopy);

			outboxSender = message.getBox();
			saved = this.messageRepository.save(message);
			saved.setMoment(newMoment);
			outboxSender.getMessages().add(saved);
		} else
			saved = this.messageRepository.save(message);
		if (!saved.getBox().getMessages().contains(saved))
			saved.getBox().getMessages().add(saved);

		return saved;
	}

	public Message broadcast(final Message message) {

		Assert.notNull(message);
		//		Assert.notNull(this.adminService.findByPrincipal());											//TODO Hacer el AdministratorService

		Message saved = null;
		Message copy, savedCopy;
		Box outboxSender = null;
		Box notificationboxRecipient;

		message.setMoment(new Date(System.currentTimeMillis() - 1000));
		outboxSender = this.boxService.findByBoxName(message.getSender().getUserAccount().getId(), "OUTBOX");
		message.setBox(outboxSender);
		final Collection<Actor> recipients = this.actorService.findAll();
		recipients.remove(this.actorService.findByPrincipal());
		for (final Actor recipient : recipients) {
			message.setRecipient(recipient);
			copy = this.copy(message);

			notificationboxRecipient = this.boxService.findByBoxName(recipient.getUserAccount().getId(), "INBOX");
			copy.setBox(notificationboxRecipient);
			saved = this.messageRepository.save(message);
			outboxSender.getMessages().add(saved);
			savedCopy = this.messageRepository.save(copy);
			notificationboxRecipient.getMessages().add(savedCopy);
		}

		return saved;
	}

	// Other business methods -------------------------------------------------

	public Message reconstruct(final Message messagePruned, final BindingResult binding) {

		Assert.notNull(messagePruned);
		final Actor principal = this.actorService.findByPrincipal();

		Message message;

		if (messagePruned.getId() != 0) {
			final Box destinationFolder = messagePruned.getBox();
			message = this.findOne(messagePruned.getId());
			this.boxService.checkPrincipal(destinationFolder);
			message.setBox(destinationFolder);
		} else {
			message = this.create();
			message.setMoment(new Date(System.currentTimeMillis() - 1000));
			message.setSender(principal);
			message.setBox(this.boxService.findByBoxName(principal.getUserAccount().getId(), "OUTBOX"));
			message.setSubject(messagePruned.getSubject());
			message.setBody(messagePruned.getBody());
			message.setPriority(messagePruned.getPriority());
			message.setRecipient(messagePruned.getRecipient());
		}

		//		if (binding != null)
		//			this.validator.validate(message, binding);

		return message;
	}
	public Message copy(final Message message) {

		Assert.notNull(message);

		Message result;

		result = this.create();
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

	public void moveMessageToFolder(final Message message, final Box folder) {
		Assert.notNull(folder);
		Assert.notNull(message);
		this.boxService.checkPrincipal(folder);
		Assert.isTrue(!folder.getMessages().contains(message));

		final Actor actor = this.actorService.findByPrincipal();

		Assert.isTrue(actor.getBoxes().contains(message.getBox()));

		final List<Message> messages = new ArrayList<Message>(folder.getMessages());
		final Box folderSource = message.getBox();
		final List<Message> messages2 = new ArrayList<Message>(folderSource.getMessages());

		messages.add(message);
		folder.setMessages(messages);
		messages2.remove(message);
		folderSource.setMessages(messages2);

		this.boxService.save(folder);
		message.setBox(folder);
		this.save(message);
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
