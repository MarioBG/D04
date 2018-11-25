
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.CustomerRepository;
import security.Authority;
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

	@Autowired
	private FixUpTaskService	fixUpTaskService;


	// Simple CRUD methods ----------------------------------------------------

	public Collection<Customer> findAll() {
		Collection<Customer> result;

		result = this.customerRepository.findAll();
		Assert.notNull(result);

		return result;
	}

	public boolean exists(final Integer arg0) {
		return this.customerRepository.exists(arg0);
	}

	public Customer findOne(final int customerId) {
		Assert.isTrue(customerId != 0);

		Customer result;

		result = this.customerRepository.findOne(customerId);
		Assert.notNull(result);

		return result;
	}

	public Customer save(final Customer customer) {
		Customer result, saved;
		//UserAccount userAccount;
		Authority authority;
		Md5PasswordEncoder encoder;

		encoder = new Md5PasswordEncoder();
		authority = new Authority();
		authority.setAuthority("CUSTOMER");
		Assert.notNull(customer, "customer.not.null");
		//	userAccount = LoginService.getPrincipal();

		//Si el customer ya persiste vemos que el actor logeado sea el propio customer que se quiere modificar
		if (customer.getId() != 0) {
			//	Assert.isTrue(userAccount.equals(customer.getUserAccount()), "customer.notEqual.userAccount");
			saved = this.customerRepository.findOne(customer.getId());
			Assert.notNull(saved, "customer.not.null");
			Assert.isTrue(saved.getUserAccount().getUsername().equals(customer.getUserAccount().getUsername()), "customer.notEqual.username");
			Assert.isTrue(customer.getUserAccount().getPassword().equals(saved.getUserAccount().getPassword()), "customer.notEqual.password");
			//Assert.isTrue(customer.getUserAccount().isAccountNonLocked() == saved.getUserAccount().isAccountNonLocked() && customer.getSuspicious() == saved.getSuspicious(), "customer.notEqual.accountOrSuspicious");
		} else
			//	Assert.isTrue(userAccount.getAuthorities().contains(authority), "customer.authority.customer"); //Si no vemos que un administrador va a guardar a otro
			//	Assert.isTrue(customer.getSuspicious() == false, "customer.notSuspicious.false");
			customer.getUserAccount().setPassword(encoder.encodePassword(customer.getUserAccount().getPassword(), null));

		result = this.customerRepository.save(customer);
		return result;
	}

	public Customer create() {

		final Customer res = new Customer();
		final String name = "";
		final String middleName = "";
		final String surname = "";
		final String email = "";
		final String photo = "";
		final String phoneNumber = "";
		final String address = "";
		final UserAccount userAccount = this.userAccountService.create();
		final List<Box> boxes = new ArrayList<>();
		final List<Endorsement> endorsements = new ArrayList<>();
		final List<Complaint> complaints = new ArrayList<>();
		final List<FixUpTask> fixUpTasks = new ArrayList<>();
		final List<SocialIdentity> socialIdentities = new ArrayList<>();
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

	public void delete(final Customer customer) {
		Assert.notNull(customer);
		Assert.isTrue(this.customerRepository.exists(customer.getId()));
		this.customerRepository.delete(customer);
	}

}
