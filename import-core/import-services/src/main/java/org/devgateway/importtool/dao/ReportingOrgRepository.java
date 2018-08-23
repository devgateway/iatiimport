package org.devgateway.importtool.dao;

import org.devgateway.importtool.model.File;
import org.devgateway.importtool.model.Project;
import org.devgateway.importtool.model.ReportingOrganization;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public  interface ReportingOrgRepository extends PagingAndSortingRepository<ReportingOrganization,Long> {
    List<ReportingOrganization> findAll();
    List<ReportingOrganization> findByNameStartingWith(String name);
    List<ReportingOrganization>groupingCriteriaWithUpdates();

}
