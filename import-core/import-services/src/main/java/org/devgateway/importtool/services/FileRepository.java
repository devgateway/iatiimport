package org.devgateway.importtool.services;

import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FileRepository extends PagingAndSortingRepository<File, Long> {
	File findById(@Param("id") Long id);
	List<File> findByAuthor(@Param("author") String author);
	List<File> findBySessionId(@Param("session_id") UUID sessionId);
	List<File> findAll();
	Page<File> findAll(Pageable pageable);
}
