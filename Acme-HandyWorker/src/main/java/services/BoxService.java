
package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.BoxRepository;
import domain.Box;

@Service
@Transactional
public class BoxService {

	// Managed repository -----------------------------------------------------

	@Autowired
	private BoxRepository	boxRepository;


	// Supporting services ----------------------------------------------------

	// Simple CRUD methods ----------------------------------------------------

	public Collection<Box> findAll() {
		Collection<Box> result;

		result = boxRepository.findAll();
		Assert.notNull(result);

		return result;
	}

	public Box findOne(final int boxId) {
		Assert.isTrue(boxId != 0);

		Box result;

		result = boxRepository.findOne(boxId);
		Assert.notNull(result);

		return result;
	}

	public Box save(Box box) {
		Assert.notNull(box);

		if (boxRepository.exists(box.getId())) {
			Box saved = new Box();
			saved.setName(box.getName());
			saved.setPredefined(false);
			saved.setMessages(box.getMessages());
			saved.setChildren(box.getChildren());
			return boxRepository.save(saved);
		} else
			return boxRepository.save(box);
	}

	public void delete(final Box box) {
		Assert.notNull(box);
		Assert.isTrue(box.getId() != 0);
		Assert.isTrue(boxRepository.exists(box.getId()));
		if (box.getPredefined() == false)
			boxRepository.delete(box);
		throw new IllegalArgumentException("A predefined box cant be erased");
	}
}
