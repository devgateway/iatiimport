package org.devgateway.importtool.services;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface FieldMappingTemplateRepository extends PagingAndSortingRepository<FieldMappingTemplate, Long> {
	
	List<FieldMappingTemplate> findAll();
	FieldMappingTemplate findById(@Param("id") Long id);	
}
