
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
		result.setChildren(new ArrayList<Box>());
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

	public Box save(final Box parent, final Box folder) {

		Assert.notNull(folder);
		Assert.isTrue(!folder.getPredefined(), "box.error.isPredefined");
		if (parent != null)
			this.checkPrincipal(parent);

		Actor actor;
		Box saved;

		if (folder.getId() == 0) {
			saved = this.boxRepository.save(folder);
			actor = this.actorService.findByPrincipal();
			actor.getBoxes().add(saved);
			if (parent != null)
				parent.getChildren().add(saved);
		} else {
			saved = this.boxRepository.save(folder);
			if (parent != null)
				if (!parent.getChildren().contains(saved))
					parent.getChildren().add(saved);
		}
		return saved;
	}

	public void delete(final Box box) {

		Assert.notNull(box);
		Assert.isTrue(!box.getPredefined(), "box.error.isPredefined");
		this.checkPrincipal(box);

		Actor actor;
		Box parent;

		actor = this.actorService.findByPrincipal();
		parent = this.boxRepository.findByChildId(box.getId());

		this.messageService.deleteByFolder(box);
		if (parent != null)
			parent.getChildren().remove(box);
		this.boxRepository.delete(box);
	}

	// Other business methods -------------------------------------------------

	//	public FolderForm construct(final Folder folder) {
	//
	//		Assert.notNull(folder);
	//
	//		FolderForm folderForm;
	//
	//		folderForm = new FolderForm();
	//
	//		folderForm.setId(folder.getId());
	//		if (folder.getParent() == null)
	//			folderForm.setParentId(null);
	//		else
	//			folderForm.setParentId(folder.getParent().getId());
	//		folderForm.setName(folder.getName());
	//
	//		return folderForm;
	//	}

	//	public Folder reconstruct(final FolderForm folderForm, final BindingResult binding) {
	//
	//		Assert.notNull(folderForm);
	//
	//		Folder folder;
	//
	//		if (folderForm.getId() != 0)
	//			folder = this.findOne(folderForm.getId());
	//		else
	//			folder = this.create(false, null);
	//
	//		folder.setParent(this.findOne(folderForm.getParentId()));
	//		folder.setName(folderForm.getName());
	//
	//		if (binding != null)
	//			this.validator.validate(folder, binding);
	//
	//		return folder;
	//	}

	public Collection<Box> defaultFolders() {

		final Collection<Box> result = new ArrayList<Box>();
		Box inbox, outbox, notificationbox, trashbox;

		inbox = this.create(true);
		outbox = this.create(true);
		notificationbox = this.create(true);
		trashbox = this.create(true);

		inbox.setName("in box");
		inbox.setMessages(new ArrayList<Message>());
		inbox.setChildren(new ArrayList<Box>());
		outbox.setName("out box");
		outbox.setMessages(new ArrayList<Message>());
		outbox.setChildren(new ArrayList<Box>());
		trashbox.setName("trash box");
		trashbox.setMessages(new ArrayList<Message>());
		trashbox.setChildren(new ArrayList<Box>());
		notificationbox.setName("notification box");
		notificationbox.setMessages(new ArrayList<Message>());
		notificationbox.setChildren(new ArrayList<Box>());

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

	public Collection<Box> findByBoxId(final Integer folderId) {

		Collection<Box> result;
		Box folder;
		Actor actor;

		actor = this.actorService.findByPrincipal();

		if (folderId == null)
			result = this.findBoxesWithoutParent(actor.getUserAccount().getId());
		else {
			folder = this.findOne(folderId);
			this.checkPrincipal(folder);
			result = folder.getChildren();
		}

		return result;
	}

	public Collection<Box> findBoxesWithoutParent(final int userAccountId) {

		final Collection<Box> boxes = this.boxRepository.findBoxesByUserAccountId(userAccountId);
		for (final Box b : boxes)
			for (final Box c : boxes)
				if (b.getChildren().contains(c)) {
					boxes.remove(b);
					break;
				}
		return boxes;
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
