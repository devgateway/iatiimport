package org.devgateway.importtool.dao;

import java.util.List;

import org.devgateway.importtool.model.FieldMappingTemplate;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface FieldMappingTemplateRepository extends PagingAndSortingRepository<FieldMappingTemplate, Long> {
	
	List<FieldMappingTemplate> findAll();
	FieldMappingTemplate findById(@Param("id") Long id);	
}
