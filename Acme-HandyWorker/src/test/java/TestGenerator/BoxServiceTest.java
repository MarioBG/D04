
package TestGenerator;

import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.ActorService;
import services.BoxService;
import utilities.AbstractTest;
import domain.Box;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml", "classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class BoxServiceTest extends AbstractTest {

	@Autowired
	private BoxService		boxService;
	@Autowired
	private ActorService	actorService;


	@Test
	public void saveBoxTest() {
		this.authenticate("handyWorker1");
		final Box box;
		Box saved;
		Collection<Box> boxs;
		box = this.boxService.findByBoxName(this.actorService.findByPrincipal().getUserAccount().getId(), "Caja de patatas");
		box.setVersion(57);
		saved = this.boxService.save(box);
		boxs = this.boxService.findAll();
		Assert.isTrue(boxs.contains(saved));
		this.unauthenticate();
	}

	@Test
	public void findAllBoxTest() {
		Collection<Box> result;
		result = this.boxService.findAll();
		Assert.notNull(result);
	}

	@Test
	public void findOneBoxTest() {
		final Box box = this.boxService.findAll().iterator().next();
		final int boxId = box.getId();
		Assert.isTrue(boxId != 0);
		Box result;
		result = this.boxService.findOne(boxId);
		Assert.notNull(result);
	}

	@Test
	public void deleteBoxTest() {
		this.authenticate("handyWorker1");
		final Box box = this.boxService.findByBoxName(this.actorService.findByPrincipal().getUserAccount().getId(), "Caja de patatas");
		Assert.notNull(box);
		Assert.isTrue(box.getId() != 0);
		Assert.isTrue(this.boxService.exists(box.getId()));
		this.boxService.delete(box);
		this.unauthenticate();
	}

}
