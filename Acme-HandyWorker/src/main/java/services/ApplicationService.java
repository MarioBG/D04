package services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import domain.Application;
import repositories.ApplicationRepository;
import repositories.CustomerRepository;

@Service
@Transactional
public class ApplicationService {

	// Managed repository -----------------------------------------------------
	
	@Autowired
	private ApplicationRepository	applicationRepository;
	
	// Supporting services ----------------------------------------------------
	
	@Autowired
	private CustomerRepository	customerRepository;

	// Simple CRUD methods ----------------------------------------------------
	
	public boolean exists(Integer id) {
		return applicationRepository.exists(id);
	}

	public Application save(final Application application) {
		Assert.notNull(application);
		Assert.isTrue(application.getId() != 0);
		
		Application result;
		result = applicationRepository.save(application);
		
		return result;
	}

	public List<Application> findAll() {
		return applicationRepository.findAll();
	}

	public Application findOne(Integer id) {
		return applicationRepository.findOne(id);
	}

	public void delete(Application entity) {
		applicationRepository.delete(entity);
	}
	
	
	
}
