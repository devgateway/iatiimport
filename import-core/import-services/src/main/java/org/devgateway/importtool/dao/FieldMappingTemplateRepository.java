package org.devgateway.importtool.dao;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.devgateway.importtool.model.FieldMappingTemplate;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;


public interface FieldMappingTemplateRepository extends PagingAndSortingRepository<FieldMappingTemplate, Long> {
	
	List<FieldMappingTemplate> findAll();
	@Transactional
	Optional<FieldMappingTemplate> findById(@Param("id") Long id);
	@Transactional
	Optional<FieldMappingTemplate> findByName(@Param("name") String name);
	
}
