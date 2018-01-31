package Serversynchronization;

import java.io.Serializable;
import java.util.UUID;

public class User implements Comparable<User>, Serializable {
	private static final long serialVersionUID = 1L;
	private UUID uuid;
	private String id;
	private String name;
	private PlayerInformation info;

	public User(String id, String name, int level, int score) {
		this(id, name);
		info = new PlayerInformation(level, score);
	}

	public User(String id, String name, UUID uuid) {
		this(id, name);
		this.uuid = uuid;
	}

	public User(String id, String name) {
		this.id = id;
		this.name = name;
		uuid = UUID.randomUUID();
	}

	public User() {
	}

	public String getID() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public PlayerInformation getInfo() {
		return info;
	}

	public void setInfo(PlayerInformation info) {
		this.info = info;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
	}

	public boolean equals(User user) {
		return (uuid.equals(user.uuid)) ? true : false;
	}

	public int compareTo(User user) {

		return info.getScore() - user.info.getScore();
	}

}
