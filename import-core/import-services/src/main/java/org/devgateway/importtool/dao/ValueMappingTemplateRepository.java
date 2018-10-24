package org.devgateway.importtool.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.devgateway.importtool.model.ValueMappingTemplate;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface ValueMappingTemplateRepository extends PagingAndSortingRepository<ValueMappingTemplate, Long> {
	
	List<ValueMappingTemplate> findAll();
	@Transactional
	ValueMappingTemplate findById(@Param("id") Long id);
	@Transactional
	ValueMappingTemplate findByName(@Param("name") String name);	
}
