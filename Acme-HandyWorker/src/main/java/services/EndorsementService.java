
package services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.EndorsementRepository;
import domain.Endorsement;

@Service
@Transactional
public class EndorsementService {

	@Autowired
	private EndorsementRepository	endorsementrepository;


	public Endorsement save(final Endorsement entity) {
		Assert.notNull(entity);
		return this.endorsementrepository.save(entity);
	}

	public Endorsement findOne(final Integer id) {
		Assert.notNull(id);
		return this.endorsementrepository.findOne(id);
	}

	public boolean exists(final Integer id) {
		Assert.notNull(id);
		return this.endorsementrepository.exists(id);
	}

	public void delete(final Integer id) {
		Assert.notNull(id);
		this.endorsementrepository.delete(id);
	}

	public List<Endorsement> findAll() {
		return this.endorsementrepository.findAll();
	}

}
