package TestGenerator;

import java.util.Collection;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.Assert;

import domain.Customer;
import services.CustomerService;
import utilities.AbstractTest;

@ContextConfiguration(locations = { "classpath:spring/junit.xml", "classpath:spring/datasource.xml",
		"classpath:spring/config/packages.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class CustomerServiceTest extends AbstractTest {

	@Autowired
	private CustomerService customerService;

	@Test
	public void saveCustomerTest() {
		Customer customer, saved;
		Collection<Customer> customers;
		customer = customerService.findAll().iterator().next();
		customer.setName("nameEX");
		saved = customerService.save(customer);
		customers = customerService.findAll();
		Assert.isTrue(customers.contains(saved));
	}

	@Test
	public void findAllCustomerTest() {
		Collection<Customer> result;
		result = customerService.findAll();
		Assert.notNull(result);
	}

	@Test
	public void findOneCustomerTest() {
		Customer customer = customerService.findAll().iterator().next();
		int customerId = customer.getId();
		Assert.isTrue(customerId != 0);
		Customer result;
		result = customerService.findOne(customerId);
		Assert.notNull(result);
	}

	@Test
	public void deleteCustomerTest() {
		Customer customer = customerService.findAll().iterator().next();
		Assert.notNull(customer);
		Assert.isTrue(customer.getId() != 0);
		Assert.isTrue(this.customerService.exists(customer.getId()));
		this.customerService.delete(customer);
	}

}
