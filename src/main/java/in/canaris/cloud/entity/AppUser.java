package in.canaris.cloud.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "app_user")
//@Table(name = "App_User", //
//		uniqueConstraints = { //
//				@UniqueConstraint(name = "APP_USER_UK", columnNames = "User_Name") })
public class AppUser implements Serializable {

	private static final long serialVersionUID = -2264642949863409860L;

//	@Id
//	@GeneratedValue
//	@Column(name = "User_Id", nullable = false)

	private static final long PASSWORD_EXPIRATION_TIME = 30L * 24L * 60L * 60L * 1000L; // 30 days

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "User_Id", updatable = false, nullable = false)
	private Long userId;

	@Column(name = "User_Name", length = 36, nullable = false)
	private String userName;

	@Column(name = "Encryted_Password", length = 128, nullable = false)
	private String encrytedPassword;

	@Column(name = "Enabled", length = 1, nullable = false)
	private boolean enabled;

	@Column
	@Email
	private String email;

	@Column(name = "mobile_no")
	private String mobileNo;
	@Column
	private String name;

	@Column(name = "confirm_password")
	private String confirmPassword;

	@Column(name = "generation_type")
	private String generationType;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "switch_id", nullable = false)
	@OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonIgnore
	private Switch switch_id;

	@Column(name = "group_name")
	private String groupName;

	@Column(nullable = false)
	private Boolean isFirstTimeLogin = true;

	@Column(name = "password_changed_time")
	private Date passwordChangedTime;

	public boolean isPasswordExpired() {
		if (this.passwordChangedTime == null)
			return false;

		long currentTime = System.currentTimeMillis();
		long lastChangedTime = this.passwordChangedTime.getTime();

		return currentTime > lastChangedTime + PASSWORD_EXPIRATION_TIME;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public void setConfirmPassword(String confirmPassword) {
		this.confirmPassword = confirmPassword;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEncrytedPassword() {
		return encrytedPassword;
	}

	public void setEncrytedPassword(String encrytedPassword) {
		this.encrytedPassword = encrytedPassword;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getGenerationType() {
		return generationType;
	}

	public void setGenerationType(String generationType) {
		this.generationType = generationType;
	}

	public Switch getSwitch_id() {
		return switch_id;
	}

	public void setSwitch_id(Switch switch_id) {
		this.switch_id = switch_id;
	}

	public Boolean getIsFirstTimeLogin() {
		return isFirstTimeLogin;
	}

	public void setIsFirstTimeLogin(Boolean isFirstTimeLogin) {
		this.isFirstTimeLogin = isFirstTimeLogin;
	}

	public Date getPasswordChangedTime() {
		return passwordChangedTime;
	}

	public void setPasswordChangedTime(Date passwordChangedTime) {
		this.passwordChangedTime = passwordChangedTime;
	}
	
}