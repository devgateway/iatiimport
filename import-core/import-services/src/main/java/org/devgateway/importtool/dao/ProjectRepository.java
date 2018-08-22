package org.devgateway.importtool.dao;

import java.util.Date;
import java.util.List;

import org.devgateway.importtool.model.Project;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface ProjectRepository extends PagingAndSortingRepository<Project, Long> {
	List<Project> findAll();
	Page<Project> findByFileId(Long id, Pageable pageable);
	List<Project> deleteByFileId(Long id);
	List<Project> findProjectLastSyncedDate();
	@Modifying
	@Query("update Project p set p.lastUpdatedOn = ?1  where p.projectIdentifier = ?2 and p.lastSyncedOn = (select " +
			"p1.lastSyncedOn from Project p1 where p1.projectIdentifier = p.projectIdentifier)")
	@Transactional
	void updateLastUpdatedDateByProjectIdentifier(Date lastUpdatedOn, String projectIdentifier);
	List<Project> findProjectUpdated();
}
