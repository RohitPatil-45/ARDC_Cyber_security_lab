package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.SubjectPlaylistMapping;

public interface SubjectPlaylistMappingRepository extends JpaRepository<SubjectPlaylistMapping, Integer> {

    @Query("SELECT DISTINCT s.playlistId FROM SubjectPlaylistMapping s WHERE s.subject IN :subjectIds")
    List<Integer> findPlaylistIdsBySubjectIds(@Param("subjectIds") List<Integer> subjectIds);

	
    @Query("SELECT s.playlistId FROM SubjectPlaylistMapping s WHERE s.subject IN :subjectIds")
	List<Integer> findSubject(@Param("subjectIds")Integer subjectIds);

	


}
