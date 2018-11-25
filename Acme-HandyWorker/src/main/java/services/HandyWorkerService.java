
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.HandyWorkerRepository;
import security.Authority;
import security.UserAccount;
import security.UserAccountService;
import domain.Box;
import domain.Curriculum;
import domain.Finder;
import domain.HandyWorker;
import domain.SocialIdentity;

@Service
@Transactional
public class HandyWorkerService {

	// Managed repository -----------------------------------------------------

	@Autowired
	private HandyWorkerRepository	handyWorkerRepository;

	// Supporting services ----------------------------------------------------

	@Autowired
	private UserAccountService		userAccountService;

	@Autowired
	private FinderService			finderService;

	@Autowired
	private CurriculumService		curriculumService;


	// Simple CRUD methods ----------------------------------------------------

	public Collection<HandyWorker> findAll() {
		Collection<HandyWorker> result;

		result = this.handyWorkerRepository.findAll();
		Assert.notNull(result);

		return result;
	}

	public boolean exists(final Integer arg0) {
		return this.handyWorkerRepository.exists(arg0);
	}

	public HandyWorker findOne(final int handyWorkerId) {
		Assert.isTrue(handyWorkerId != 0);

		HandyWorker result;

		result = this.handyWorkerRepository.findOne(handyWorkerId);
		Assert.notNull(result);

		return result;
	}

	public HandyWorker save(final HandyWorker handyWorker) {
		HandyWorker result, saved;
		//UserAccount userAccount;
		Authority authority;
		Md5PasswordEncoder encoder;

		encoder = new Md5PasswordEncoder();
		authority = new Authority();
		authority.setAuthority("HANDYWORKER");
		Assert.notNull(handyWorker, "handyWorker.not.null");
		//	userAccount = LoginService.getPrincipal();

		//Si el handyworker ya persiste vemos que el actor logeado sea el propio handyworker que se quiere modificar
		if (handyWorker.getId() != 0) {
			//	Assert.isTrue(userAccount.equals(handyWorker.getUserAccount()), "handyWorker.notEqual.userAccount");
			saved = this.handyWorkerRepository.findOne(handyWorker.getId());
			Assert.notNull(saved, "handyWorker.not.null");
			Assert.isTrue(saved.getUserAccount().getUsername().equals(handyWorker.getUserAccount().getUsername()), "handyWorker.notEqual.username");
			Assert.isTrue(handyWorker.getUserAccount().getPassword().equals(saved.getUserAccount().getPassword()), "handyWorker.notEqual.password");
			//Assert.isTrue(handyWorker.getUserAccount().isAccountNonLocked() == saved.getUserAccount().isAccountNonLocked() && handyWorker.getSuspicious() == saved.getSuspicious(), "handyWorker.notEqual.accountOrSuspicious");
		} else
			//	Assert.isTrue(userAccount.getAuthorities().contains(authority), "handyWorker.authority.handyWorker"); //Si no vemos que un administrador va a guardar a otro
			//	Assert.isTrue(handyWorker.getSuspicious() == false, "handyWorker.notSuspicious.false");
			handyWorker.getUserAccount().setPassword(encoder.encodePassword(handyWorker.getUserAccount().getPassword(), null));

		result = this.handyWorkerRepository.save(handyWorker);
		return result;
	}

	public HandyWorker create() {
		final HandyWorker res = new HandyWorker();
		final String name = "";
		final String middleName = "";
		final String surname = "";
		final String email = "";
		final String photo = "";
		final String phoneNumber = "";
		final String address = "";
		final String make = "";
		final List<Box> boxes = new ArrayList<>();
		final List<SocialIdentity> socialIdentities = new ArrayList<>();
		final UserAccount userAccount = this.userAccountService.create();
		final Curriculum curriculum = this.curriculumService.create();
		final Finder finder = this.finderService.create();
		res.setPhoto(photo);
		res.setPhoneNumber(phoneNumber);
		res.setAddress(address);
		res.setMiddleName(middleName);
		res.setSurname(surname);
		res.setEmail(email);
		res.setName(name);
		res.setMake(make);
		res.setUserAccount(userAccount);
		res.setBoxes(boxes);
		res.setSocialIdentity(socialIdentities);
		res.setCurriculum(curriculum);
		res.setFinder(finder);
		return res;
	}

	public void delete(final HandyWorker handyWorker) {
		Assert.notNull(handyWorker);
		Assert.isTrue(handyWorker.getId() != 0);

		this.handyWorkerRepository.delete(handyWorker);
	}

	public HandyWorker findByUserAccountId(final int userAccountId) {
		HandyWorker result;

		Assert.isTrue(userAccountId != 0);

		result = this.handyWorkerRepository.findByUserAccountId(userAccountId);

		return result;
	}

}
