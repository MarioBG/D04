
package services;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.HandyWorkerRepository;
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

		result = handyWorkerRepository.findAll();
		Assert.notNull(result);

		return result;
	}

	public boolean exists(final Integer arg0) {
		return handyWorkerRepository.exists(arg0);
	}

	public HandyWorker findOne(final int handyWorkerId) {
		Assert.isTrue(handyWorkerId != 0);

		HandyWorker result;

		result = handyWorkerRepository.findOne(handyWorkerId);
		Assert.notNull(result);

		return result;
	}

	public HandyWorker save(HandyWorker handyWorker) {
		Assert.notNull(handyWorker);

//		if (handyWorkerRepository.exists(handyWorker.getId())) {
//			HandyWorker saved = new HandyWorker();
//			saved.setAddress(handyWorker.getAddress());
//			saved.setName(handyWorker.getName());
//			saved.setSurname(handyWorker.getSurname());
//			saved.setMiddleName(handyWorker.getMiddleName());
//			saved.setEmail(handyWorker.getEmail());
//			saved.setPhoneNumber(handyWorker.getPhoneNumber());
//			saved.setPhoto(handyWorker.getPhoto());
//			saved.setMake(handyWorker.getMake());
//			saved.setBoxes(handyWorker.getBoxes());
//			saved.setSocialIdentity(handyWorker.getSocialIdentity());
//			saved.setUserAccount(handyWorker.getUserAccount());
//			saved.setCurriculum(handyWorker.getCurriculum());
//			saved.setFinder(handyWorker.getFinder());
//			
//			return handyWorkerRepository.save(saved);
//		} else
			return handyWorkerRepository.save(handyWorker);
	}

	public HandyWorker create() {
		HandyWorker res = new HandyWorker();
		String name = "";
		String middleName = "";
		String surname = "";
		String email = "";
		String photo = "";
		String phoneNumber = "";
		String address = "";
		String make = "";
		List<Box> boxes = new ArrayList<>();
		List<SocialIdentity> socialIdentities = new ArrayList<>();
		UserAccount userAccount = userAccountService.create();
		Curriculum curriculum = curriculumService.create();
		Finder finder = finderService.create();
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

	public void delete(HandyWorker handyWorker) {
		Assert.notNull(handyWorker);
		Assert.isTrue(handyWorker.getId() != 0);
		
		handyWorkerRepository.delete(handyWorker);
	}
	

}
