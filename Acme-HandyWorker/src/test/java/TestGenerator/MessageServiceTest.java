
package TestGenerator;

import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import services.BoxService;
import services.MessageService;
import utilities.AbstractTest;
import domain.Message;

@ContextConfiguration(locations = {
	"classpath:spring/junit.xml", "classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class MessageServiceTest extends AbstractTest {

	@Autowired
	private MessageService	messageService;
	@Autowired
	private BoxService		boxService;


	@Test
	public void saveMessageTest() {
		this.authenticate("handyWorker1");
		Message message, saved;
		Collection<Message> messages;
		message = this.boxService.findByPrincipal().iterator().next().getMessages().iterator().next();
		message.setVersion(1337);
		saved = this.messageService.save(message);
		messages = this.messageService.findAll();
		Assert.isTrue(messages.contains(saved));
		this.unauthenticate();
	}

	@Test
	public void findAllMessageTest() {
		Collection<Message> result;
		result = this.messageService.findAll();
		Assert.notNull(result);
	}

	@Test
	public void findOneMessageTest() {
		final Message message = this.messageService.findAll().iterator().next();
		final int messageId = message.getId();
		Assert.isTrue(messageId != 0);
		Message result;
		result = this.messageService.findOne(messageId);
		Assert.notNull(result);
	}

	@Test
	public void deleteMessageTest() {
		this.authenticate("handyWorker1");
		final Message message = this.boxService.findByPrincipal().iterator().next().getMessages().iterator().next();
		Assert.notNull(message);
		Assert.isTrue(message.getId() != 0);
		Assert.isTrue(this.messageService.exists(message.getId()));
		this.messageService.delete(message);
	}

}
