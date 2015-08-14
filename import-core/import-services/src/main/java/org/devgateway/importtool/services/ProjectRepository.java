package org.devgateway.importtool.services;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepository extends PagingAndSortingRepository<Project, Long> {
	List<Project> findAll();
	Page<Project> findByFileId(Long id, Pageable pageable);
}
