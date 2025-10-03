package in.canaris.cloud.openstack.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "subject_subplaylist_maping")
public class SubjectSubplaylistMapping {
	
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "Id", updatable = false, nullable = false)
	private int Id;

	@Column(name = "subject", nullable = false, length = 255)
	private int subject;

	@Column(name = "sub_playlistid", nullable = false, length = 255)
	private int subPlaylistId;

	public int getId() {
		return Id;
	}

	public void setId(int id) {
		Id = id;
	}

	public int getSubject() {
		return subject;
	}

	public void setSubject(int subject) {
		this.subject = subject;
	}

	public int getSubPlaylistId() {
		return subPlaylistId;
	}

	public void setSubPlaylistId(int subPlaylistId) {
		this.subPlaylistId = subPlaylistId;
	}

	
	
	
	

}
