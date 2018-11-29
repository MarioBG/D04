
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.AdministratorRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Actor;
import domain.Administrator;
import domain.Box;
import domain.Customer;
import domain.Endorsement;
import domain.HandyWorker;
import domain.Message;
import domain.SocialIdentity;

@Service
@Transactional
public class AdministratorService {

	// Managed repository -----------------------------------------------------

	@Autowired
	private AdministratorRepository	administratorRepository;

	@Autowired
	private ActorService			actorservice;

	@Autowired
	private CustomerService			customerService;

	@Autowired
	private HandyWorkerService		handyWorkerService;

	@Autowired
	LoginService					loginservice;

	@Autowired
	private MessageService			messageservice;

	@Autowired
	private EndorsementService		endorsementService;


	// Supporting services ----------------------------------------------------

	// Simple CRUD methods ----------------------------------------------------

	public void sendAll(final Message message) {
		Assert.notNull(message);

		final Actor self = this.actorservice.findSelf();
		this.messageservice.sendMessage(this.actorservice.findAllUsername(self.getId()), message);
	}

	public Collection<Administrator> findAll() {
		Collection<Administrator> result;

		result = this.administratorRepository.findAll();
		Assert.notNull(result);

		return result;
	}

	public Collection<Actor> findSuspiciousActor() {
		final Collection<Actor> actors = new LinkedList<>();
		actors.addAll(this.actorservice.findSuspiciousActor());
		return actors;

	}

	public boolean exists(final Integer arg0) {
		return this.administratorRepository.exists(arg0);
	}

	public Administrator findOne(final int administratorId) {
		Assert.isTrue(administratorId != 0);

		Administrator result;

		result = this.administratorRepository.findOne(administratorId);
		Assert.notNull(result);

		return result;
	}

	public Administrator save(final Administrator administrator) {
		Administrator result, saved;
		final UserAccount logedUserAccount;
		Authority authority;
		Md5PasswordEncoder encoder;

		encoder = new Md5PasswordEncoder();
		authority = new Authority();
		authority.setAuthority("ADMINISTRATOR");
		Assert.notNull(administrator, "administrator.not.null");

		if (this.exists(administrator.getId())) {
			logedUserAccount = LoginService.getPrincipal();
			Assert.notNull(logedUserAccount, "administrator.notLogged ");
			Assert.isTrue(logedUserAccount.equals(administrator.getUserAccount()), "administrator.notEqual.userAccount");
			saved = this.administratorRepository.findOne(administrator.getId());
			Assert.notNull(saved, "administrator.not.null");
			Assert.isTrue(saved.getUserAccount().getUsername().equals(administrator.getUserAccount().getUsername()), "administrator.notEqual.username");
			Assert.isTrue(administrator.getUserAccount().getPassword().equals(saved.getUserAccount().getPassword()), "administrator.notEqual.password");
			Assert.isTrue(administrator.getUserAccount().isAccountNonLocked() == saved.getUserAccount().isAccountNonLocked() && administrator.isSuspicious() == saved.isSuspicious(), "administrator.notEqual.accountOrSuspicious");

		} else {
			Assert.isTrue(administrator.isSuspicious() == false, "administrator.notSuspicious.false");
			administrator.getUserAccount().setPassword(encoder.encodePassword(administrator.getUserAccount().getPassword(), null));
			administrator.getUserAccount().setEnabled(true);

		}

		result = this.administratorRepository.save(administrator);

		return result;

	}

	public Administrator create() {

		Administrator result;
		UserAccount userAccount;
		Authority authority;

		result = new Administrator();
		userAccount = new UserAccount();
		authority = new Authority();

		result.setSuspicious(false);

		authority.setAuthority("ADMINISTRATOR");
		userAccount.addAuthority(authority);
		userAccount.setEnabled(true);

		final Collection<Box> boxes = new LinkedList<>();
		result.setBoxes(boxes);
		final Collection<SocialIdentity> socialIdentity = new LinkedList<>();
		result.setSocialIdentity(socialIdentity);
		result.setUserAccount(userAccount);

		return result;

	}

	public void delete(final Administrator administrator) {
		Assert.notNull(administrator);
		Assert.isTrue(this.administratorRepository.exists(administrator.getId()));
		this.administratorRepository.delete(administrator);
	}

	public Map<Customer, Double> calculateCustomerScore() {

		final Pattern badPattern = Pattern.compile("");										//TODO bad words y good words
		final Pattern goodPattern = Pattern.compile("");
		Matcher good, bad = null;
		final Map<Customer, Double> ans = new HashMap<Customer, Double>();
		for (final Customer c : this.customerService.findAll()) {
			Double score = 0d;
			for (final Endorsement e : this.endorsementService.findAll())
				if (e.getCustomer() != null && e.getCustomer().equals(c)) {
					good = goodPattern.matcher(e.getComment());
					bad = badPattern.matcher(e.getComment());
					while (good.find())
						score++;
					while (bad.find())
						score--;
				}
		}
		final List<Double> values = new ArrayList<Double>(ans.values());
		Collections.sort(values);
		for (final Customer c : ans.keySet())
			ans.put(c, (-1 + ((ans.get(c) - values.get(0)) * (2))) / (values.get(values.size() - 1) - values.get(0)));
		return ans;
	}

	public Map<HandyWorker, Double> calculateHandyWorkerScore() {

		final Pattern badPattern = Pattern.compile("");										//TODO bad words y good words
		final Pattern goodPattern = Pattern.compile("");
		Matcher good, bad = null;
		final Map<HandyWorker, Double> ans = new HashMap<HandyWorker, Double>();
		for (final HandyWorker c : this.handyWorkerService.findAll()) {
			Double score = 0d;
			for (final Endorsement e : this.endorsementService.findAll())
				if (e.getCustomer() != null && e.getCustomer().equals(c)) {
					good = goodPattern.matcher(e.getComment());
					bad = badPattern.matcher(e.getComment());
					while (good.find())
						score++;
					while (bad.find())
						score--;
				}
		}
		final List<Double> values = new ArrayList<Double>(ans.values());
		Collections.sort(values);
		for (final HandyWorker c : ans.keySet())
			ans.put(c, (-1 + ((ans.get(c) - values.get(0)) * (2))) / (values.get(values.size() - 1) - values.get(0)));
		return ans;
	}
	public UserAccount changeEnabledActor(final UserAccount userAccount) {
		Assert.notNull(userAccount);

		userAccount.setEnabled(!userAccount.isEnabled());
		return this.loginservice.save(userAccount);
	}

}
