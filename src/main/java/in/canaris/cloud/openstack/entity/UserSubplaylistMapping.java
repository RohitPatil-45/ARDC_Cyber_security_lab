package in.canaris.cloud.openstack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_subplaylist_mapping")
public class UserSubplaylistMapping {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", updatable = false, nullable = false)
	private int Id;

	@Column(name = "user_name", nullable = false, length = 255)
	private String UserName;

	@Column(name = "sub_playlistid", nullable = false, length = 255)
	private int SubPlaylistId;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public String getUserName() {
		return UserName;
	}

	public void setUserName(String userName) {
		UserName = userName;
	}

	public int getSubPlaylistId() {
		return SubPlaylistId;
	}

	public void setSubPlaylistId(int subPlaylistId) {
		SubPlaylistId = subPlaylistId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	

}
