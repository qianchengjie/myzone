package com.qcj.myzone.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import com.qcj.myzone.model.blackboard.Reply;

public interface ReplyRepository extends PagingAndSortingRepository<Reply, Integer>{
	
	@Query("from Reply where floorId=:floorId")
	public Iterable<Reply> findAllReplyById(@Param("floorId")int floorId);
	
}
