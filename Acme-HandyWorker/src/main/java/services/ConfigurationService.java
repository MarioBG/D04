package services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import domain.Configuration;
import repositories.ConfigurationRepository;

@Service
@Transactional
public class ConfigurationService {

	@Autowired
	private ConfigurationRepository configurationRepository;

	public Configuration save(Configuration entity) {
		return configurationRepository.save(entity);
	}

	public List<Configuration> findAll() {
		return configurationRepository.findAll();
	}

	public Configuration findOne(Integer id) {
		return configurationRepository.findOne(id);
	}

	public boolean exists(Integer id) {
		return configurationRepository.exists(id);
	}

	public void delete(Configuration entity) {
		configurationRepository.delete(entity);
	}

}
