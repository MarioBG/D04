
package services;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.CustomerRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Application;
import domain.Box;
import domain.Complaint;
import domain.CreditCard;
import domain.Customer;
import domain.Endorsement;
import domain.FixUpTask;
import domain.HandyWorker;
import domain.SocialIdentity;

@Service
@Transactional
public class CustomerService {

	// Managed repository -----------------------------------------------------

	@Autowired
	private CustomerRepository	customerRepository;

	// Supporting services ----------------------------------------------------

	@Autowired
	private FixUpTaskService	fixUpTaskService;

	@Autowired
	private HandyWorkerService	handyWorkerService;

	@Autowired
	private ApplicationService	applicationService;


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
		UserAccount logedUserAccount;
		Authority authority;
		Md5PasswordEncoder encoder;

		encoder = new Md5PasswordEncoder();
		authority = new Authority();
		authority.setAuthority("CUSTOMER");
		Assert.notNull(customer, "customer.not.null");

		if (this.exists(customer.getId())) {
			logedUserAccount = LoginService.getPrincipal();
			Assert.notNull(logedUserAccount, "customer.notLogged");
			Assert.isTrue(logedUserAccount.equals(customer.getUserAccount()), "customer.notEqual.userAccount");
			saved = this.customerRepository.findOne(customer.getId());
			Assert.notNull(saved, "customer.not.null");
			Assert.isTrue(saved.getUserAccount().getUsername().equals(customer.getUserAccount().getUsername()), "customer.notEqual.username");
			Assert.isTrue(customer.getUserAccount().getPassword().equals(saved.getUserAccount().getPassword()), "customer.notEqual.password");
			Assert.isTrue(customer.getUserAccount().isAccountNonLocked() == saved.getUserAccount().isAccountNonLocked() && customer.isSuspicious() == saved.isSuspicious(), "customer.notEqual.accountOrSuspicious");

		} else {
			Assert.isTrue(customer.isSuspicious() == false, "customer.notSuspicious.false");
			customer.getUserAccount().setPassword(encoder.encodePassword(customer.getUserAccount().getPassword(), null));
			customer.getUserAccount().setEnabled(true);

		}

		result = this.customerRepository.save(customer);

		return result;

	}

	public void addToCustomerEndorsements(final Customer customer, final Endorsement e) {
		final Authority authority = new Authority();
		authority.setAuthority(Authority.CUSTOMER);
		Assert.notNull(customer, "customer.not.null");
		Assert.notNull(e, "customer.endorsement.not.null");
		final UserAccount logedUserAccount = LoginService.getPrincipal();
		Assert.notNull(logedUserAccount, "customer.notLogged");
		Assert.isTrue(logedUserAccount.getAuthorities().contains(authority));
		final HandyWorker handyWorker = this.handyWorkerService.findByUserAccountId(e.getHandyWorker().getUserAccount().getId());
		Assert.isTrue(this.handyWorkerService.findByCustomerUserAccountId(customer.getUserAccount().getId()).contains(handyWorker));
		final Collection<Endorsement> endorsements = customer.getEndorsements();
		endorsements.add(e);
		customer.setEndorsements(endorsements);
		this.customerRepository.save(customer);
	}

	public Customer create() {

		Customer result;
		UserAccount userAccount;
		Authority authority;

		result = new Customer();
		userAccount = new UserAccount();
		authority = new Authority();

		result.setSuspicious(false);

		authority.setAuthority("CUSTOMER");
		userAccount.addAuthority(authority);
		userAccount.setEnabled(true);

		final Collection<FixUpTask> fixUpTasks = new LinkedList<>();
		result.setFixUpTasks(fixUpTasks);
		final Collection<Box> boxes = new LinkedList<>();
		result.setBoxes(boxes);
		final Collection<Endorsement> endorsements = new LinkedList<>();
		result.setEndorsements(endorsements);
		final Collection<SocialIdentity> socialIdentity = new LinkedList<>();
		result.setSocialIdentity(socialIdentity);
		final Collection<Complaint> complaints = new LinkedList<>();
		result.setComplaints(complaints);
		result.setUserAccount(userAccount);

		return result;

	}

	public void delete(final Customer customer) {
		Assert.notNull(customer);
		Assert.isTrue(this.customerRepository.exists(customer.getId()));
		this.customerRepository.delete(customer);
	}

	public Customer findCustomerByApplication(final Application application) {
		Assert.notNull(application);
		Assert.isTrue(application.getId() != 0);
		final Customer res = this.customerRepository.findCustomerByApplicationId(application.getId());
		return res;
	}

	public Collection<Customer> findByHandyWorkerUserAccountId(final int id) {
		return this.customerRepository.getCustomersForHandyWorkerWithUserAccountId(id);
	}

	public Customer findCustomerByUserAccount(final UserAccount userAccount) {
		Assert.notNull(userAccount);
		Assert.isTrue(userAccount.getId() != 0);
		final Customer res = this.customerRepository.findByUserAccountId(userAccount.getId());
		return res;
	}

	public Customer findByPrincipal() {
		Customer res;
		UserAccount userAccount;
		userAccount = LoginService.getPrincipal();
		if (userAccount == null)
			res = null;
		else
			res = this.customerRepository.findByUserAccountId(userAccount.getId());
		return res;
	}

	public Customer findCustomerByFixUpTask(final FixUpTask fixUpTask) {
		Assert.notNull(fixUpTask);
		Assert.isTrue(fixUpTask.getId() != 0);
		final Customer res = this.customerRepository.findCustomerByFixUpTaskId(fixUpTask.getId());

		return res;
	}

	public FixUpTask findOneFixUptask(final int fixUpTaskId) {
		Assert.isTrue(fixUpTaskId != 0);
		final UserAccount logedUserAccount;
		Authority authority;
		FixUpTask result;
		authority = new Authority();
		authority.setAuthority("CUSTOMER");
		logedUserAccount = LoginService.getPrincipal();

		Assert.isTrue(logedUserAccount.getAuthorities().contains(authority));

		result = this.fixUpTaskService.findOne(fixUpTaskId);
		Assert.notNull(result);
		Assert.isTrue(this.findCustomerByFixUpTask(result).getUserAccount().equals(logedUserAccount));

		return result;
	}

	public List<FixUpTask> findAllFixUpTask() {
		return this.fixUpTaskService.findAll();
	}

	public FixUpTask saveCustomerFixUpTask(final FixUpTask fixUpTask) {
		FixUpTask result, saved;
		final UserAccount logedUserAccount;
		Authority authority;

		authority = new Authority();
		authority.setAuthority("CUSTOMER");
		Assert.notNull(fixUpTask, "fixUpTask.not.null");
		final Customer customer = this.findCustomerByFixUpTask(fixUpTask);

		if (this.exists(fixUpTask.getId())) {
			logedUserAccount = LoginService.getPrincipal();
			Assert.notNull(logedUserAccount, "customer.notLogged ");
			Assert.isTrue(logedUserAccount.equals(customer.getUserAccount()), "customer.notEqual.userAccount");
			saved = this.fixUpTaskService.findOne(fixUpTask.getId());
			Assert.notNull(saved, "fixUpTask.not.null");
			Assert.isTrue(customer.getUserAccount().isAccountNonLocked() && !(customer.isSuspicious()), "customer.notEqual.accountOrSuspicious");
			result = this.fixUpTaskService.save(fixUpTask);
			Assert.notNull(result);

		} else {
			result = this.fixUpTaskService.save(fixUpTask);
			Assert.notNull(result);
		}
		return result;

	}

	public void deleteFixUpTask(final FixUpTask fixUpTask) {
		Assert.isTrue(fixUpTask.getId() != 0);
		UserAccount logedUserAccount;
		Authority authority;
		authority = new Authority();
		authority.setAuthority("CUSTOMER");
		logedUserAccount = LoginService.getPrincipal();
		Assert.isTrue(logedUserAccount.getAuthorities().contains(authority));
		Assert.isTrue(this.findCustomerByFixUpTask(fixUpTask).getUserAccount().equals(logedUserAccount));
		this.fixUpTaskService.delete(fixUpTask);
	}

	public Application saveCustomerApplication(final Application application, final String comment, final CreditCard creditCard) {
		final Application result, saved;
		Assert.notNull(application);
		Assert.isTrue(application.getId() != 0);
		final UserAccount userAccount = LoginService.getPrincipal();
		final Date currentMoment = new Date(System.currentTimeMillis() - 1);
		final Authority authority;
		final UserAccount logedUserAccount;
		authority = new Authority();
		authority.setAuthority("CUSTOMER");

		if (this.exists(application.getId()) && application.getStatus().equals("PENDING") && userAccount.getAuthorities().contains(authority)
			&& this.applicationService.findApplicationsByCustomer(this.findCustomerByApplication(application)).contains(application)) {
			logedUserAccount = LoginService.getPrincipal();
			Assert.notNull(logedUserAccount, "customer.notLogged ");
			Assert.isTrue(logedUserAccount.equals(this.findCustomerByApplication(application).getUserAccount()), "customer.notEqual.userAccount");
			if (application.getApplicationMoment().compareTo(currentMoment) < 0) {
				saved = this.applicationService.findOne(application.getId());
				Assert.notNull(saved, "application.not.null");
				application.getComments().add(logedUserAccount.getUsername() + ": - " + comment);
				application.setStatus("REJECTED");
				result = this.applicationService.save(application);
				return result;
			} else {
				saved = this.applicationService.findOne(application.getId());
				Assert.notNull(saved, "application.not.null");
				if (!comment.equals(null))
					application.getComments().add(logedUserAccount.getUsername() + ": - " + comment);
				application.setCreditCard(creditCard);
				application.setStatus("ACCEPTED");
				result = this.applicationService.save(application);
				return result;
			}
		} else {

			result = this.applicationService.save(application);
			return result;
		}
	}

	public Customer findByUserAccountId(final int id) {
		return this.customerRepository.findByUserAccountId(id);
	}
}
