package in.canaris.cloud.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserRoledefine {
	@Id
	private Long id;
	private String roleName;
	private String userName;

	// getters and setters
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
