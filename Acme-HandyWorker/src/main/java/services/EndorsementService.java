
package services;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.EndorsementRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Customer;
import domain.Endorsement;
import domain.HandyWorker;

@Service
@Transactional
public class EndorsementService {

	@Autowired
	private EndorsementRepository	endorsementRepository;

	@Autowired
	private HandyWorkerService		handyWorkerService;

	@Autowired
	private CustomerService			customerService;


	public Endorsement create() {
		Endorsement result;
		result = new Endorsement();
		result.setMoment(new Date(System.currentTimeMillis() - 1));
		return result;
	}

	public Endorsement save(final Endorsement entity) {
		Assert.notNull(entity);
		UserAccount logedUserAccount;
		logedUserAccount = LoginService.getPrincipal();
		final Authority authority = new Authority();
		authority.setAuthority(Authority.CUSTOMER);
		final Authority authority2 = new Authority();
		authority2.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(logedUserAccount.getAuthorities().contains(authority) || logedUserAccount.getAuthorities().contains(authority2));
		if (entity.getId() == 0) {
			entity.setMoment(new Date(System.currentTimeMillis() - 1));
			if (this.customerService.findByPrincipal() != null) {
				Assert.notNull(entity.getHandyWorker());
				Assert.notNull(this.handyWorkerService.findByUserAccountId(entity.getHandyWorker().getUserAccount().getId()));
				final Customer c = this.customerService.findByPrincipal();
				this.customerService.addToCustomerEndorsements(c, entity);
			} else {
				Assert.notNull(entity.getCustomer());
				Assert.notNull(this.customerService.findByUserAccountId(entity.getCustomer().getUserAccount().getId()));
				final HandyWorker c = this.handyWorkerService.findByPrincipal();
				this.handyWorkerService.addToHandyWorkerEndorsements(c, entity);
			}
		}
		return this.endorsementRepository.save(entity);
	}
	public Endorsement findOne(final Integer id) {
		Assert.notNull(id);
		return this.endorsementRepository.findOne(id);
	}

	public Collection<Endorsement> findReceivedEndorsementsByHandyWorkerUserAccountId(final int id) {
		return this.endorsementRepository.findReceivedEndorsementsByHandyWorkerUserAccountId(id);
	}

	public Collection<Endorsement> findReceivedEndorsementsByCustomerUserAccountId(final int id) {
		return this.endorsementRepository.findReceivedEndorsementsByCustomerUserAccountId(id);
	}

	public Collection<Endorsement> findEndorsementsByHandyWorkerUserAccountId(final int id) {
		return this.endorsementRepository.findEndorsementsByHandyWorkerUserAccountId(id);
	}

	public Collection<Endorsement> findEndorsementsByCustomerUserAccountId(final int id) {
		return this.endorsementRepository.findEndorsementsByCustomerUserAccountId(id);
	}

	public boolean exists(final Integer id) {
		Assert.notNull(id);
		return this.endorsementRepository.exists(id);
	}

	public void delete(final Integer id) {
		Assert.notNull(id);
		final UserAccount logedUserAccount = LoginService.getPrincipal();
		final Authority authority = new Authority();
		authority.setAuthority(Authority.CUSTOMER);
		final Authority authority2 = new Authority();
		authority2.setAuthority(Authority.HANDYWORKER);
		Assert.isTrue(logedUserAccount.getAuthorities().contains(authority) || logedUserAccount.getAuthorities().contains(authority2));
		this.endorsementRepository.delete(id);
	}

	public List<Endorsement> findAll() {
		return this.endorsementRepository.findAll();
	}

}
