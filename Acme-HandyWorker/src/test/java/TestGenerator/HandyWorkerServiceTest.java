package TestGenerator;

import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.HandyWorker;
import services.HandyWorkerService;
import utilities.AbstractTest;

@ContextConfiguration(locations = { "classpath:spring/junit.xml", "classpath:spring/datasource.xml",
		"classpath:spring/config/packages.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class HandyWorkerServiceTest extends AbstractTest {

	@Autowired
	private HandyWorkerService handyworkerService;

	@Test
	public void saveHandyWorkerTest() {
		HandyWorker handyworker, saved;
		Collection<HandyWorker> handyworkers;
		handyworker = handyworkerService.findAll().iterator().next();
		handyworker.setName("Sample");
		saved = handyworkerService.save(handyworker);
		handyworkers = handyworkerService.findAll();
		Assert.isTrue(handyworkers.contains(saved));
	}

	@Test
	public void findAllHandyWorkerTest() {
		Collection<HandyWorker> result;
		result = handyworkerService.findAll();
		Assert.notNull(result);
	}

	@Test
	public void findOneHandyWorkerTest() {
		HandyWorker handyworker = handyworkerService.findAll().iterator().next();
		int handyworkerId = handyworker.getId();
		Assert.isTrue(handyworkerId != 0);
		HandyWorker result;
		result = handyworkerService.findOne(handyworkerId);
		Assert.notNull(result);
	}

	@Test
	public void deleteHandyWorkerTest() {
		HandyWorker handyworker = handyworkerService.findAll().iterator().next();
		Assert.notNull(handyworker);
		Assert.isTrue(handyworker.getId() != 0);
		Assert.isTrue(this.handyworkerService.exists(handyworker.getId()));
		this.handyworkerService.delete(handyworker);
	}

}
