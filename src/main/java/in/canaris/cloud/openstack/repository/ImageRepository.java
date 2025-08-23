package in.canaris.cloud.openstack.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.openstack.entity.Image;

@Repository
@Transactional
public interface ImageRepository extends JpaRepository<Image, Integer> {

	List<Image> findByimgName(String image);

	@Query("SELECT DISTINCT i.imageId, i.imgName FROM Image i")
	List<Object[]> findDistinctImageName();

	void deleteByImageId(String imageId);

}
