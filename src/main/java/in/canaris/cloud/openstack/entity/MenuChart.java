package in.canaris.cloud.openstack.entity;

import javax.persistence.*;

@Entity
@Table(name = "menu_chart")
public class MenuChart {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "menu_id", updatable = false, nullable = false)
	private Integer menuId;

	@Column(name = "module_id", nullable = true)
	private Integer moduleId;

	@Column(name = "module_name", nullable = true, length = 50)
	private String moduleName;

	@Column(name = "sub_module_id", nullable = true)
	private Integer subModuleId;

	@Column(name = "sub_module_name", nullable = true, length = 50)
	private String subModuleName;

	@Column(name = "url", nullable = true, length = 50)
	private String url;

	@Column(name = "sort_order", nullable = true)
	private Integer sortOrder;

	// Default constructor
	public MenuChart() {
	}

	// Parameterized constructor
	public MenuChart(Integer menuId, Integer moduleId, String moduleName, Integer subModuleId, String subModuleName,
			String url, Integer sortOrder) {
		this.menuId = menuId;
		this.moduleId = moduleId;
		this.moduleName = moduleName;
		this.subModuleId = subModuleId;
		this.subModuleName = subModuleName;
		this.url = url;
		this.sortOrder = sortOrder;
	}

	// Getters and Setters
	public Integer getMenuId() {
		return menuId;
	}

	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}

	public Integer getModuleId() {
		return moduleId;
	}

	public void setModuleId(Integer moduleId) {
		this.moduleId = moduleId;
	}

	public String getModuleName() {
		return moduleName;
	}

	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public Integer getSubModuleId() {
		return subModuleId;
	}

	public void setSubModuleId(Integer subModuleId) {
		this.subModuleId = subModuleId;
	}

	public String getSubModuleName() {
		return subModuleName;
	}

	public void setSubModuleName(String subModuleName) {
		this.subModuleName = subModuleName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	// toString method
	@Override
	public String toString() {
		return "MenuChart{" + "menuId=" + menuId + ", moduleId=" + moduleId + ", moduleName='" + moduleName + '\''
				+ ", subModuleId=" + subModuleId + ", subModuleName='" + subModuleName + '\'' + ", url='" + url + '\''
				+ ", sortOrder=" + sortOrder + '}';
	}
}