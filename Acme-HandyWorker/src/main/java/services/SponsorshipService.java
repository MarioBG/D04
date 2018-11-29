
package services;

import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.SponsorshipRepository;
import security.Authority;
import security.LoginService;
import security.UserAccount;
import domain.Sponsorship;
import domain.Tutorial;

@Service
@Transactional
public class SponsorshipService {

	@Autowired
	private SponsorshipRepository	sponsorshipRepository;

	@Autowired
	private CreditCardService		creditCardService;

	@Autowired
	private SponsorService			sponsorService;


	public Sponsorship save(final Sponsorship sponsorship) {
		Sponsorship result, saved;
		UserAccount loggedUserAccount;
		Authority authority;
		authority = new Authority();
		authority.setAuthority(Authority.SPONSOR);
		loggedUserAccount = LoginService.getPrincipal();

		if (this.exists(sponsorship.getId()) && (loggedUserAccount.getAuthorities().contains(authority))) {
			saved = this.sponsorshipRepository.findOne(sponsorship.getId());
			Assert.notNull(saved);
		}

		Assert.notNull(this.creditCardService.save(sponsorship.getCreditCard()));

		result = this.sponsorshipRepository.save(sponsorship);
		Assert.notNull(result);
		return result;
	}

	public List<Sponsorship> findAll() {
		return this.sponsorshipRepository.findAll();
	}

	public Sponsorship findOne(final Integer sponsorshipId) {
		Assert.isTrue(this.exists(sponsorshipId));
		return this.sponsorshipRepository.findOne(sponsorshipId);
	}

	public boolean exists(final Integer sponsorshipId) {
		return this.sponsorshipRepository.exists(sponsorshipId);
	}

	public void delete(final Sponsorship sponsorship) {
		UserAccount loggedUserAccount;
		Authority authority;
		authority = new Authority();
		authority.setAuthority(Authority.SPONSOR);
		loggedUserAccount = LoginService.getPrincipal();
		Assert.isTrue(loggedUserAccount.getAuthorities().contains(authority));
		Assert.isTrue(this.sponsorService.findByPrincipal().getSponsorships().contains(sponsorship));
		this.sponsorshipRepository.delete(sponsorship);
	}

	public Sponsorship findRandomForTutorial(final Tutorial tutorial) {
		final Random rnd = new Random();
		final Collection<Sponsorship> from = this.sponsorshipRepository.findAll();
		for (final Sponsorship s : from)
			if (!s.getTutorial().equals(tutorial))
				from.remove(s);
		return (Sponsorship) from.toArray()[rnd.nextInt(from.size())];
	}
}
