package in.canaris.cloud.repository;


import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.Announcement;
import in.canaris.cloud.entity.Discount;

@Repository
@Transactional
public interface AnnouncementRepository extends JpaRepository<Announcement, Integer> {

}
