package com.qcj.myzone.model.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class UserRight {

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	private int pId;
	
	private String rname;
	
	private String description;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getPId() {
		return pId;
	}

	public void setPId(int pId) {
		this.pId = pId;
	}

	public String getRname() {
		return rname;
	}

	public void setRname(String rname) {
		this.rname = rname;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	
	
}
