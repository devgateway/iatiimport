package org.devgateway.importtool.dao;

import java.util.List;

import org.devgateway.importtool.model.Project;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProjectRepository extends PagingAndSortingRepository<Project, Long> {
	List<Project> findAll();
	Page<Project> findByFileId(Long id, Pageable pageable);
	List<Project> deleteByFileId(Long id);
}
