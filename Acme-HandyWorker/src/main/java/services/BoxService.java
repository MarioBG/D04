
package services;

import java.util.ArrayList;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.validation.Validator;

import repositories.BoxRepository;
import security.LoginService;
import security.UserAccount;
import domain.Actor;
import domain.Box;
import domain.Message;

@Service
@Transactional
public class BoxService {																			//TODO Revisar los casos de uso de este año

	// Managed repository -----------------------------------------------------

	@Autowired
	private BoxRepository	boxRepository;

	// Supporting services ----------------------------------------------------

	@Autowired
	private ActorService	actorService;

	@Autowired
	private MessageService	messageService;

	@Autowired
	private Validator		validator;


	// Constructors -----------------------------------------------------------

	public BoxService() {
		super();
	}

	// Simple CRUD methods ----------------------------------------------------

	public Box create(final boolean predefined) {

		Box result = null;

		result = new Box();

		result.setPredefined(predefined);
		result.setMessages(new ArrayList<Message>());

		return result;
	}

	public Box findOne(final int folderId) {

		Box result = null;
		result = this.boxRepository.findOne(folderId);
		return result;
	}

	public Box findOneToEdit(final int folderId) {

		Box result = null;
		result = this.boxRepository.findOne(folderId);
		this.checkPrincipal(result);
		Assert.isTrue(!result.getPredefined(), "box.error.isPredefined");
		return result;
	}

	public Collection<Box> findAll() {

		Collection<Box> result = null;
		result = this.boxRepository.findAll();
		return result;
	}

	public Box save(final Box folder) {

		Assert.notNull(folder);
		Assert.isTrue(!folder.getPredefined(), "box.error.isPredefined");

		Actor actor;
		Box saved;

		if (folder.getId() == 0) {
			saved = this.boxRepository.save(folder);
			actor = this.actorService.findByPrincipal();
			actor.getBoxes().add(saved);
		} else
			saved = this.boxRepository.save(folder);
		return saved;
	}

	public void delete(final Box box) {

		Assert.notNull(box);
		Assert.isTrue(!box.getPredefined(), "box.error.isPredefined");
		this.checkPrincipal(box);

		Actor actor;

		actor = this.actorService.findByPrincipal();

		this.messageService.deleteByFolder(box);
		this.boxRepository.delete(box);
	}

	public Collection<Box> defaultFolders() {

		final Collection<Box> result = new ArrayList<Box>();
		Box inbox, outbox, notificationbox, trashbox;

		inbox = this.create(true);
		outbox = this.create(true);
		notificationbox = this.create(true);
		trashbox = this.create(true);

		inbox.setName("in box");
		inbox.setMessages(new ArrayList<Message>());
		outbox.setName("out box");
		outbox.setMessages(new ArrayList<Message>());
		trashbox.setName("trash box");
		trashbox.setMessages(new ArrayList<Message>());
		notificationbox.setName("notification box");
		notificationbox.setMessages(new ArrayList<Message>());

		result.add(inbox);
		result.add(outbox);
		result.add(notificationbox);
		result.add(trashbox);

		return result;
	}

	public Box save(final Box folder, final Actor actor) {
		Assert.notNull(folder);
		Assert.notNull(actor);

		Box result = null;

		result = this.boxRepository.save(folder);
		actor.getBoxes().add(result);
		this.actorService.save(actor);

		return result;
	}

	public Collection<Box> findByPrincipal() {

		Collection<Box> result = null;
		final UserAccount userAccount = LoginService.getPrincipal();
		result = this.findByUserAccountId(userAccount.getId());
		return result;
	}

	public Collection<Box> findByUserAccountId(final int userAccountId) {

		Collection<Box> result = null;
		result = this.boxRepository.findBoxesByUserAccountId(userAccountId);
		return result;
	}

	public Box findByBoxName(final int userAccountId, final String folderName) {

		Box result = null;
		result = this.boxRepository.findByBoxName(userAccountId, folderName);
		return result;
	}

	public void checkNotRepeat(final Box folder) {

		Assert.notNull(folder);

		final Actor actor = this.actorService.findByPrincipal();
		final Box folderActor = this.findByBoxName(actor.getUserAccount().getId(), folder.getName());
		Assert.isNull(folderActor);
	}

	public void checkPrincipal(final Box folder) {

		Assert.notNull(folder);

		final Actor actor = this.actorService.findByPrincipal();
		final Collection<Box> foldersActor = this.boxRepository.findBoxesByUserAccountId(actor.getUserAccount().getId());
		Assert.isTrue(foldersActor.contains(folder));
	}

	public Collection<Box> save(final Collection<Box> folders) {
		return this.boxRepository.save(folders);
	}

	public void delete(final Collection<Box> folders) {
		this.boxRepository.delete(folders);
	}

}
