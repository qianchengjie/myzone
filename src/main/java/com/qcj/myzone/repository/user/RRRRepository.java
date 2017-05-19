package com.qcj.myzone.repository.user;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.qcj.myzone.model.user.RoleRightRelation;

public interface RRRRepository  extends PagingAndSortingRepository<RoleRightRelation, Integer>{

	@Query("select rightId from RoleRightRelation where roleId=:roleId")
	public List<Integer> getRightId(@Param("roleId")int roleId);
	
	@Query("select rtype from RoleRightRelation where roleId=:roleId and rightId=:rightId")
	public int getRtype(@Param("roleId")int roleId,@Param("rightId")int rightId);
	
	@Query("select rightId from RoleRightRelation where roleId=:roleId and rtype=1")
	public List<Integer> getHaveRightId(@Param("roleId")int roleId);
	
}
