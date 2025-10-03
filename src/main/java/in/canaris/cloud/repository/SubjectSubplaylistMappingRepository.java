package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.SubjectSubplaylistMapping;

public interface SubjectSubplaylistMappingRepository extends JpaRepository<SubjectSubplaylistMapping, Integer> {

//	List<Integer> findSubPlaylistIdsBySubjectIds(List<Integer> userSubjectIds);
	
	   @Query("SELECT DISTINCT s.subPlaylistId FROM SubjectSubplaylistMapping s WHERE s.subject IN :subjectIds")
	    List<Integer> findSubPlaylistIdsBySubjectIds(@Param("subjectIds") List<Integer> subjectIds);

	   @Query("SELECT s.subPlaylistId FROM SubjectSubplaylistMapping s WHERE s.subject IN :subjectIds")
	List<Integer> findSubject(@Param("subjectIds") Integer subjectIds);




}
