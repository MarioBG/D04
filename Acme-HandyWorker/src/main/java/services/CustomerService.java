
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.CustomerRepository;
import security.UserAccount;
import security.UserAccountService;
import domain.Box;
import domain.Complaint;
import domain.Customer;
import domain.Endorsement;
import domain.FixUpTask;
import domain.SocialIdentity;

@Service
@Transactional
public class CustomerService {

	// Managed repository -----------------------------------------------------

	@Autowired
	private CustomerRepository	customerRepository;

	// Supporting services ----------------------------------------------------

	@Autowired
	private UserAccountService	userAccountService;


	// Simple CRUD methods ----------------------------------------------------

	public Collection<Customer> findAll() {
		Collection<Customer> result;

		result = customerRepository.findAll();
		Assert.notNull(result);

		return result;
	}

	public boolean exists(final Integer arg0) {
		return customerRepository.exists(arg0);
	}

	public Customer findOne(final int customerId) {
		Assert.isTrue(customerId != 0);

		Customer result;

		result = customerRepository.findOne(customerId);
		Assert.notNull(result);

		return result;
	}

	public Customer save(final Customer customer) {
		Assert.notNull(customer);

		Customer result;

		result = customerRepository.save(customer);

		return result;
	}

	public Customer create() {

		Customer res = new Customer();
		String name = "";
		String middleName = "";
		String surname = "";
		String email = "";
		String photo = "";
		String phoneNumber = "";
		String address = "";
		UserAccount userAccount = userAccountService.create();
		List<Box> boxes = new ArrayList<>();
		List<Endorsement> endorsements = new ArrayList<>();
		List<Complaint> complaints = new ArrayList<>();
		List<FixUpTask> fixUpTasks = new ArrayList<>();
		List<SocialIdentity> socialIdentities = new ArrayList<>();
		res.setPhoto(photo);
		res.setPhoneNumber(phoneNumber);
		res.setAddress(address);
		res.setMiddleName(middleName);
		res.setSurname(surname);
		res.setEmail(email);
		res.setName(name);
		res.setUserAccount(userAccount);
		res.setBoxes(boxes);
		res.setComplaints(complaints);
		res.setEndorsements(endorsements);
		res.setFixUpTasks(fixUpTasks);
		res.setSocialIdentity(socialIdentities);
		return res;

	}

	public void delete(Customer customer) {
		Assert.notNull(customer);
		Assert.isTrue(this.customerRepository.exists(customer.getId()));
		customerRepository.delete(customer);
	}
	
	
}
