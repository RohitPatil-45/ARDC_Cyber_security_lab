package in.canaris.cloud.repository;


import java.util.List;

import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import in.canaris.cloud.entity.FloatingIP;



@Repository
@Transactional
public interface FloatingIPRepository extends JpaRepository<FloatingIP, Integer> {
	
//	@Query("SELECT  f.id,f.public_ip FROM FloatingIP f")
//	List<Object[]> getAllPublicIP();
//	
	@Query(value = "SELECT fim.id, fim.public_ip FROM floating_ip_master fim LEFT JOIN floating_ip_assign fia ON fim.id = fia.public_ip WHERE fia.public_ip IS NULL", nativeQuery = true)
	List<Object[]> getAllPublicIP();
	
//	@Query("SELECT f.publicIp FROM FloatingIP f LEFT JOIN f.floatingIPAssign fa WHERE fa.publicIp IS NULL")
//	List<Object[]> getAllPublicIP();
}
