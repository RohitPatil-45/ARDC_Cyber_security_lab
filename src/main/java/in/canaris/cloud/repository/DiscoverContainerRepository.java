package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import in.canaris.cloud.openstack.entity.DiscoverContainer;

public interface DiscoverContainerRepository extends JpaRepository<DiscoverContainer, Integer> {

	 @Query(value = "SELECT dc.id, dc.container_id, dc.image_name, dc.command, dc.created, dc.status, dc.ports, dc.container_name, dc.physical_server_ip, " +
            "ul.username, ul.template_name, ul.last_active_connection, ul.guacamole_id " +
            "FROM discover_container dc " +
            "LEFT JOIN user_lab ul ON dc.container_name = ul.instance_name", nativeQuery = true)
    List<Object[]> fetchContainerWithUserLab();

}
