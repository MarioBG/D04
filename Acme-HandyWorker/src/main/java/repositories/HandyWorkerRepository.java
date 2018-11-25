
package repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.HandyWorker;

@Repository
public interface HandyWorkerRepository extends JpaRepository<HandyWorker, Integer> {

	@Query("select a from HandyWorker a where a.userAccount.id = ?1")
	HandyWorker findByUserAccountId(int userAccountId);

	@Query("select c from HandyWorker c where c.id = ?1")
	HandyWorker findByHandyWorkerId(int handyWorkerId);

}
