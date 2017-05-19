package com.qcj.myzone.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.qcj.myzone.model.FilesInfo;

@Repository
public interface FileInfoRepository extends PagingAndSortingRepository<FilesInfo, Integer>{

}
