
package services;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.BoxRepository;
import domain.Actor;
import domain.Box;

@Service
@Transactional
public class BoxService {

	@Autowired
	private BoxRepository	boxrepository;
	@Autowired
	private ActorService	actorservice;


	public Box newBox(final Box name) {
		final Actor current = this.actorservice.findSelf();

		final Box saved = this.boxrepository.save(name);

		current.getBoxes().add(saved);

		this.actorservice.save(current);

		return saved;
	}

	public Box create(final String name, final boolean predefined) {
		final Box box = new Box();
		box.setName(name);
		box.setPredefined(predefined);
		return this.newBox(box);
	}

	public boolean exists(final Integer id) {
		return this.actorservice.exists(id);
	}

	public Collection<Box> defaultFolders() {

		final Collection<Box> result = new LinkedList<Box>();
		Box inbox, outbox, notificationbox, trashbox;

		inbox = this.create("INBOX", true);
		outbox = this.create("OUTBOX", true);
		notificationbox = this.create("SPAMBOX", true);
		trashbox = this.create("TRASHBOX", true);

		result.add(inbox);
		result.add(outbox);
		result.add(notificationbox);
		result.add(trashbox);

		return result;
	}

	public Box findInbox(final Actor a) {
		Assert.notNull(a);

		for (final Box b : a.getBoxes())
			if ("INBOX".equals(b.getName()))
				return b;

		return null;
	}

	public Box findOutbox(final Actor a) {
		Assert.notNull(a);

		for (final Box b : a.getBoxes())
			if ("OUTBOX".equals(b.getName()))
				return b;

		return null;
	}

	public Box findTashBox(final Actor a) {
		Assert.notNull(a);

		for (final Box b : a.getBoxes())
			if ("TRASHBOX".equals(b.getName()))
				return b;

		return null;
	}

	public Box getOutBoxFolderFromActorId(final int id) {
		return this.boxrepository.getOutBoxFolderFromActorId(id);
	}

	public Box getInBoxFolderFromActorId(final int id) {
		return this.boxrepository.getInBoxFolderFromActorId(id);
	}

	public Box getSpamBoxFolderFromActorId(final int id) {
		return this.boxrepository.getSpamBoxFolderFromActorId(id);
	}

	public Box getTrashBoxFolderFromActorId(final int id) {
		return this.boxrepository.getTrashBoxFolderFromActorId(id);
	}

	public List<Box> save(final Iterable<Box> entities) {
		return this.boxrepository.save(entities);
	}

	public Box save(final Box entity) {
		Assert.notNull(entity);
		return this.boxrepository.save(entity);
	}

	public Collection<Box> findBoxesByUserAccountId(final int userAccountId) {
		return this.boxrepository.findBoxesByUserAccountId(userAccountId);
	}

	public List<Box> findAll() {
		return this.boxrepository.findAll();
	}

	public Box findOne(final Integer id) {
		Assert.notNull(id);
		return this.boxrepository.findOne(id);
	}

	public void delete(final Box entity) {
		Assert.notNull(entity);
		Assert.isTrue(!"INBOX".equals(entity.getName()) && !"OUTBOX".equals(entity.getName()) && !"TRASHBOX".equals(entity.getName()) && !"SPAMBOX".equals(entity.getName()));
		this.boxrepository.delete(entity);
	}

}
