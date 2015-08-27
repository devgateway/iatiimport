package org.devgateway.importtool.services;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface ValueMappingTemplateRepository extends PagingAndSortingRepository<ValueMappingTemplate, Long> {
	
	List<ValueMappingTemplate> findAll();
	ValueMappingTemplate findById(@Param("id") Long id);	
}
