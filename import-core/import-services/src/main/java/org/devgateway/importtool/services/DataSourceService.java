package org.devgateway.importtool.services;

import org.apache.commons.lang.StringUtils;
import org.devgateway.importtool.dao.DataSourceRepository;
import org.devgateway.importtool.model.CustomDataSource;
import org.devgateway.importtool.model.DataSource;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Service
public class DataSourceService {
	@Autowired
	private DataSourceRepository dataSourceRepository;	
	public DataSource save(DataSource ds) {
		return dataSourceRepository.save(ds);
	}
	
	public String getDataSourceURL(String reportingOrgId) {
		String url = "";
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
}
