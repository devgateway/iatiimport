package org.devgateway.importtool.dao;

import org.devgateway.importtool.model.ReportingOrganization;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;


import javax.transaction.Transactional;
import java.util.List;

public  interface ReportingOrgRepository extends PagingAndSortingRepository<ReportingOrganization,Long> {
    List<ReportingOrganization> findAll();
    List<ReportingOrganization> findByNameContainsIgnoreCase(String name);
    List<ReportingOrganization> groupingCriteriaWithUpdates();
    @Transactional
    ReportingOrganization save(ReportingOrganization ro);
    @Transactional
    void deleteAll();
    @Query("SELECT distinct p.groupingCriteria " +
            "FROM  Project p " +
            "where p.groupingCriteria is not null")
    List<String> getSyncedGroupingCriteria();
    List<ReportingOrganization> findByOrgIdIgnoreCase(String orgId);

}
