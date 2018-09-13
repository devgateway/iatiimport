package org.devgateway.importtool.services;


import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.dao.DataSourceRepository;
import org.devgateway.importtool.model.CustomDataSource;
import org.devgateway.importtool.model.DataSource;
import org.devgateway.importtool.model.File;
import org.devgateway.importtool.services.processor.helper.ReportingOrganizationHelper;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Optional;

@org.springframework.stereotype.Service
public class DataSourceService {
	@Autowired
	private DataSourceRepository dataSourceRepository;
	private Log log = LogFactory.getLog(getClass());

	public DataSource save(DataSource ds) {
		DataSource dataSource = getDataSource();
		dataSource.setCustomDataSources(ds.getCustomDataSources());		
		dataSource.setDefaultUrl(ds.getDefaultUrl());
		//we delete the files for the reporting orgs so they are fetched again
		Optional.ofNullable(dataSource.getCustomDataSources()).orElse(Collections.emptyList()).stream().forEach(cd-> {
			try {
				Files.delete(Paths.get(ReportingOrganizationHelper.getFileName(cd.getReportingOrgId())));
			} catch (IOException e) {
				//we just ignore and we let the record to be saved
				log.error("cannot delete a provider org file", e);
			}
		});
		return dataSourceRepository.save(dataSource);
	}
	
	public String getDataSourceURL(String reportingOrgId) {
		String url = null;
		Iterable<DataSource> dataSources = dataSourceRepository.findAll();
		
		if (dataSources.iterator().hasNext()) {
			DataSource ds = dataSources.iterator().next();
			CustomDataSource cDataSource = ds.getCustomDataSources().stream().filter(customDataSource -> {
				return reportingOrgId.equals(customDataSource.getReportingOrgId());
			}).findFirst().orElse(null);
			
			if (cDataSource != null) {
				url = cDataSource.getUrl();
			}
			
			if (StringUtils.isEmpty(url)) {
				url = ds.getDefaultUrl();
			}
		}
		
		return url;			
	}
	
	public DataSource getDataSource() {
		DataSource ds = null;	
        Iterable<DataSource> dataSources = dataSourceRepository.findAll();		
		if (dataSources.iterator().hasNext()) {
			ds = dataSources.iterator().next();
		} else {
			ds = new DataSource();
		}
		
		return ds;			
	}
}
