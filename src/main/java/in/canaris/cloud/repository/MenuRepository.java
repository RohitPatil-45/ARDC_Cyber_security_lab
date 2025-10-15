package in.canaris.cloud.repository;

import java.awt.Menu;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.MenuChart;


public interface MenuRepository extends JpaRepository<MenuChart, Integer> {

	 @Query("SELECT mc FROM MenuChart mc ORDER BY mc.sortOrder")
	    List<MenuChart> findAllOrderBySortOrder();

	

	
	

}
