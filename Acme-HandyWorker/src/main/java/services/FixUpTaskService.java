
package services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.FixUpTaskRepository;
import domain.FixUpTask;

@Service
@Transactional
public class FixUpTaskService {

	// Managed repository -----------------------------------------------------

	@Autowired
	private FixUpTaskRepository	fixUpTaskRepository;


	// Supporting services ----------------------------------------------------

	// Simple CRUD methods ----------------------------------------------------

	public Collection<FixUpTask> findAll() {
		Collection<FixUpTask> result;

		result = fixUpTaskRepository.findAll();
		Assert.notNull(result);

		return result;
	}

	public FixUpTask findOne(final int fixUpTaskId) {
		Assert.isTrue(fixUpTaskId != 0);

		FixUpTask result;

		result = fixUpTaskRepository.findOne(fixUpTaskId);
		Assert.notNull(result);

		return result;
	}

	public FixUpTask save(final FixUpTask fixUpTask) {
		Assert.notNull(fixUpTask);

		FixUpTask result;

		result = fixUpTaskRepository.save(fixUpTask);

		return result;
	}

	public void delete(final FixUpTask fixUpTask) {
		Assert.notNull(fixUpTask);
		Assert.isTrue(fixUpTask.getId() != 0);
		Assert.isTrue(fixUpTaskRepository.exists(fixUpTask.getId()));

		fixUpTaskRepository.delete(fixUpTask);
	}
}
