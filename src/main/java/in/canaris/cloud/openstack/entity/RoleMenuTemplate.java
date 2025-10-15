package in.canaris.cloud.openstack.entity;

import javax.persistence.*;

@Entity
@Table(name = "role_menu_template")
public class RoleMenuTemplate {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", updatable = false, nullable = false)
	private Integer id;

	@Column(name = "template_name", nullable = false, length = 50)
	private String templateName;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "menu_id", referencedColumnName = "menu_id", nullable = false)
	private MenuChart menu;

	// Default constructor
	public RoleMenuTemplate() {
	}

	// Parameterized constructor
	public RoleMenuTemplate(Integer id, String templateName, MenuChart menu) {
		this.id = id;
		this.templateName = templateName;
		this.menu = menu;
	}

	// Getters and Setters
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public MenuChart getMenu() {
		return menu;
	}

	public void setMenu(MenuChart menu) {
		this.menu = menu;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	// toString method
	@Override
	public String toString() {
		return "RoleMenuTemplate{" + "id=" + id + ", templateName='" + templateName + '\'' + ", menu="
				+ (menu != null ? menu.getMenuId() : null) + '}';
	}
}