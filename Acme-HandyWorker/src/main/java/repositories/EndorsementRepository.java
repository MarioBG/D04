
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Endorsement;

@Repository
public interface EndorsementRepository extends JpaRepository<Endorsement, Integer> {

	@Query("select e from Customer c join c.endorsements e where c.userAccount.id = ?1")
	public Collection<Endorsement> findEndorsementsByCustomerUserAccountId(int id);

	@Query("select e from Handyworker hw join hw.endorsements e where hw.userAccount.id = ?1")
	public Collection<Endorsement> findEndorsementsByHandyWorkerUserAccountId(int id);

	@Query("select e.customer from Endorsement e where e.customer.userAccount.id = ?1")
	public Collection<Endorsement> findReceivedEndorsementsByCustomerUserAccountId(int id);

	@Query("select e.handyWorker from Endorsement e where e.handyWorker.userAccount.id = ?1")
	public Collection<Endorsement> findReceivedEndorsementsByHandyWorkerUserAccountId(int id);

}
