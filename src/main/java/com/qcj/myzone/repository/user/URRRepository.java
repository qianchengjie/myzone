package com.qcj.myzone.repository.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.qcj.myzone.model.user.UserRoleRelation;

public interface URRRepository extends PagingAndSortingRepository<UserRoleRelation, Integer>{

	
	@Query("select rId from UserRoleRelation where uId=:uId")
	public int getrId(@Param("uId")int uId);
	
}
