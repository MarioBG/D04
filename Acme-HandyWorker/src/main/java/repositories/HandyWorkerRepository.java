
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.HandyWorker;

@Repository
public interface HandyWorkerRepository extends JpaRepository<HandyWorker, Integer> {

	@Query("select a.handyWorker from Application a where a.id = ?1")
	HandyWorker findByApplicationId(int applicationId);

	@Query("select a from HandyWorker a where a.userAccount.id = ?1")
	HandyWorker findByUserAccountId(int userAccountId);

	@Query("select a.handyWorker from Application a join a.fixUpTask f where f.id = ?1")
	HandyWorker findByFixUpTaskId(int fixUpTaskId);

	@Query("select a.handyWorker from Application a where a.status = 'ACCEPTED' group by a.handyWorker order by sum(a.fixUpTask.complaints.size)")
	Collection<HandyWorker> topThreeHandyWorkersInTermsOfComplaints();

	@Query("select a.handyWorker from Customer c join c.fixUpTasks f join f.applications a where a.status = 'ACCEPTED' and c.userAccount.id = ?1")
	Collection<HandyWorker> handyWorkersWorkedForCustomerWithUserAccountId(int id);

}
