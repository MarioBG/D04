
package services;

import java.util.ArrayList;
import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

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


	//	@Autowired
	//	private Validator		validator;

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

	public Box findOne(final int boxId) {

		Box result = null;
		result = this.boxRepository.findOne(boxId);
		return result;
	}

	public Box findOneToEdit(final int boxId) {

		Box result = null;
		result = this.boxRepository.findOne(boxId);
		this.checkPrincipal(result);
		Assert.isTrue(!result.getPredefined(), "box.error.isPredefined");
		return result;
	}

	public Collection<Box> findAll() {

		Collection<Box> result = null;
		result = this.boxRepository.findAll();
		return result;
	}

	public Box save(final Box box) {

		Assert.notNull(box);
		Assert.isTrue(!box.getPredefined(), "box.error.isPredefined");

		Actor actor;
		Box saved;

		if (box.getId() == 0) {
			saved = this.boxRepository.save(box);
			actor = this.actorService.findByPrincipal();
			actor.getBoxes().add(saved);
		} else
			saved = this.boxRepository.save(box);
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
		outbox.setName("out box");
		trashbox.setName("trash box");
		notificationbox.setName("notification box");

		result.add(inbox);
		result.add(outbox);
		result.add(notificationbox);
		result.add(trashbox);

		return result;
	}

	public Box save(final Box box, final Actor actor) {
		Assert.notNull(box);
		Assert.notNull(actor);

		Box result = null;

		result = this.boxRepository.save(box);
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

	public Box findByBoxName(final int userAccountId, final String boxName) {

		Box result = null;
		result = this.boxRepository.findByBoxName(userAccountId, boxName);
		return result;
	}

	public void checkNotRepeat(final Box box) {

		Assert.notNull(box);

		final Actor actor = this.actorService.findByPrincipal();
		final Box boxActor = this.findByBoxName(actor.getUserAccount().getId(), box.getName());
		Assert.isNull(boxActor);
	}

	public void checkPrincipal(final Box box) {

		Assert.notNull(box);

		final Actor actor = this.actorService.findByPrincipal();
		final Collection<Box> boxesActor = this.boxRepository.findBoxesByUserAccountId(actor.getUserAccount().getId());
		Assert.isTrue(boxesActor.contains(box));
	}

	public Collection<Box> save(final Collection<Box> boxes) {
		return this.boxRepository.save(boxes);
	}

	public void delete(final Collection<Box> boxes) {
		this.boxRepository.delete(boxes);
	}

	public boolean exists(final int id) {
		return this.boxRepository.findOne(id) != null;
	}

}
