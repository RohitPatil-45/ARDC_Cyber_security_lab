package in.canaris.cloud.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.canaris.cloud.openstack.entity.RoleMenuTemplate;

public interface RoleMenuTemplateRepository extends JpaRepository<RoleMenuTemplate, Integer> {

	@Query("SELECT DISTINCT r.templateName FROM RoleMenuTemplate r ORDER BY r.templateName")
	List<String> findDistinctTemplateNames();

	@Query("SELECT rmt FROM RoleMenuTemplate rmt WHERE rmt.templateName = :templateName ORDER BY rmt.menu.sortOrder")
	List<RoleMenuTemplate> findByTemplateNameOrderByMenuSortOrder(@Param("templateName") String templateName);

	List<RoleMenuTemplate> findByTemplateName(String template);

	@Query("SELECT r.menu.url FROM RoleMenuTemplate r WHERE r.templateName = :templateName")
	List<String> findUrlsByTemplateName(@Param("templateName") String templateName);

	@Query("SELECT r.menu.moduleName FROM RoleMenuTemplate r WHERE r.templateName = :templateName")
	List<String> findModulesByTemplateName(@Param("templateName") String templateName);

}
