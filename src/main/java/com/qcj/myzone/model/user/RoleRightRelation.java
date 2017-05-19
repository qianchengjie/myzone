package com.qcj.myzone.model.user;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class RoleRightRelation {

	@Id @GeneratedValue(strategy = GenerationType.AUTO)
	private int id;
	
	private int roleId;
	
	private int rightId;
	
	private int rtype;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getRightId() {
		return rightId;
	}

	public void setRightId(int rightId) {
		this.rightId = rightId;
	}

	public int getRtype() {
		return rtype;
	}

	public void setRtype(int rtype) {
		this.rtype = rtype;
	}
	
	
	
}
