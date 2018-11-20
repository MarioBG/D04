
package repositories;

import java.util.Collection;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import domain.Box;

@Repository
public interface BoxRepository extends JpaRepository<Box, Integer> {

	@Query("select b from Actor a join a.boxes b where a.id = ?1 and b.name='OUTBOX'")
	Box getOutBoxFolderFromActorId(int id);

	@Query("select b from Actor a join a.boxes b where a.id = ?1 and b.name='INBOX'")
	Box getInBoxFolderFromActorId(int id);

	@Query("select b from Actor a join a.boxes b where a.id = ?1 and b.name='SPAMBOX'")
	Box getSpamBoxFolderFromActorId(int id);

	@Query("select b from Actor a join a.boxes b where a.id = ?1 and b.name='TRASHBOX'")
	Box getTrashBoxFolderFromActorId(int id);

	@Query("select b from Box b where b.actor.id=?1 and (select count(caja) from Box caja where b member of caja.children)=0")
	Collection<Box> getFirstLevelFoldersFromActorId(int actorId);

	@Query("select b from Box b join b.messages m where m.id=?1")
	Box getFolderFromMessageId(int messageId);

	@Query("select b.children from Box b where b.id=?1")
	Collection<Box> getChildFolders(int boxId);

}
