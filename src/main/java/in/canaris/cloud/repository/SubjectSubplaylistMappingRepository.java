package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import in.canaris.cloud.openstack.entity.SubjectSubplaylistMapping;

public interface SubjectSubplaylistMappingRepository extends JpaRepository<SubjectSubplaylistMapping, Integer> {

    @Query("SELECT DISTINCT s.subPlaylistId FROM SubjectSubplaylistMapping s WHERE s.subject IN :subjectIds")
    List<Integer> findSubPlaylistIdsBySubjectIds(@Param("subjectIds") List<Integer> subjectIds);

    @Query("SELECT s.subPlaylistId FROM SubjectSubplaylistMapping s WHERE s.subject = :subjectId")
    List<Integer> findSubPlaylistIdsBySubjectId(@Param("subjectId") Integer subjectId);

    @Modifying
    @Transactional
    void deleteBySubject(Integer subjectId);

    List<SubjectSubplaylistMapping> findBySubject(Integer subjectId);
}