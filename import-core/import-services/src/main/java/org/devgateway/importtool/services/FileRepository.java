package org.devgateway.importtool.services;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface FileRepository extends PagingAndSortingRepository<File, Long> {
	File findById(@Param("id") Long id);
	List<File> findByAuthor(@Param("author") String author);
}
