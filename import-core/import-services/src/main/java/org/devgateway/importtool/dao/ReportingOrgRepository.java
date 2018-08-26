package org.devgateway.importtool.dao;

import org.devgateway.importtool.model.ReportingOrganization;
import org.springframework.data.repository.PagingAndSortingRepository;


import javax.transaction.Transactional;
import java.util.List;

public  interface ReportingOrgRepository extends PagingAndSortingRepository<ReportingOrganization,Long> {
    List<ReportingOrganization> findAll();
    List<ReportingOrganization> findByNameStartingWithIgnoreCase(String name);
    List<ReportingOrganization>groupingCriteriaWithUpdates();
    @Transactional
    ReportingOrganization save(ReportingOrganization ro);
    @Transactional
    void deleteAll();

}
