
package TestGenerator;

import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.HandyWorkerService;
import utilities.AbstractTest;
import domain.HandyWorker;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml", "classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class HandyWorkerServiceTest extends AbstractTest {

	@Autowired
	private HandyWorkerService	handyworkerService;


	@Test
	public void saveHandyWorkerTest() {
		HandyWorker created;
		HandyWorker saved;
		HandyWorker copyCreated;

		created = this.handyworkerService.findAll().iterator().next();
		System.out.println(created);
		copyCreated = this.copyHandyWorker(created);
		copyCreated.setName("TestHandyWorker");
		saved = this.handyworkerService.save(copyCreated);
		Assert.isTrue(this.handyworkerService.findAll().contains(saved));
		Assert.isTrue(saved.getName().equals("TestHandyWorker"));

	}

	@Test
	public void findAllHandyWorkerTest() {
		Collection<HandyWorker> result;
		result = this.handyworkerService.findAll();
		Assert.notNull(result);
	}

	@Test
	public void findOneHandyWorkerTest() {
		final HandyWorker handyworker = this.handyworkerService.findAll().iterator().next();
		final int handyworkerId = handyworker.getId();
		Assert.isTrue(handyworkerId != 0);
		HandyWorker result;
		result = this.handyworkerService.findOne(handyworkerId);
		Assert.notNull(result);
	}

	@Test
	public void deleteHandyWorkerTest() {
		final HandyWorker handyworker = this.handyworkerService.findAll().iterator().next();
		Assert.notNull(handyworker);
		Assert.isTrue(handyworker.getId() != 0);
		Assert.isTrue(this.handyworkerService.exists(handyworker.getId()));
		this.handyworkerService.delete(handyworker);
	}

	@Test
	public void testCreate() {
		HandyWorker handyWorker;

		handyWorker = this.handyworkerService.create();
		Assert.isNull(handyWorker.getAddress());
		Assert.isNull(handyWorker.getEmail());
		Assert.isNull(handyWorker.getName());
		Assert.isNull(handyWorker.getSurname());
		Assert.isNull(handyWorker.getPhoneNumber());
		Assert.isNull(handyWorker.getPhoto());
		Assert.isNull(handyWorker.getMake());
		Assert.isNull(handyWorker.getMiddleName());
	}

	private HandyWorker copyHandyWorker(final HandyWorker handyWorker) {
		HandyWorker result;

		result = new HandyWorker();
		result.setAddress(handyWorker.getAddress());
		result.setEmail(handyWorker.getEmail());
		result.setId(handyWorker.getId());
		result.setName(handyWorker.getName());
		result.setMiddleName(handyWorker.getMiddleName());
		result.setPhoneNumber(handyWorker.getPhoneNumber());
		result.setSurname(handyWorker.getSurname());
		result.setMake(handyWorker.getMake());
		result.setFinder(handyWorker.getFinder());
		result.setApplications(handyWorker.getApplications());
		result.setBoxes(handyWorker.getBoxes());
		result.setCurriculum(handyWorker.getCurriculum());
		result.setPhoto(handyWorker.getPhoto());
		result.setSocialIdentity(handyWorker.getSocialIdentity());
		result.setTutorials(handyWorker.getTutorials());
		result.setEndorsements(handyWorker.getEndorsements());
		result.setSuspicious(handyWorker.isSuspicious());
		result.setUserAccount(handyWorker.getUserAccount());
		result.setVersion(handyWorker.getVersion());

		return result;
	}
}
