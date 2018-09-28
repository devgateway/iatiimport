package org.devgateway.importtool.services;

import java.io.File;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.devgateway.importtool.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.beans.factory.annotation.Value;



@ComponentScan
@Configuration
@EnableJpaRepositories(basePackages = {"org.devgateway.importtool.dao"})
@ConfigurationProperties
/***
 * Sample Service configuration
 * Remove if not needed
 *
 */
public class ServiceConfiguration {
    private Log log = LogFactory.getLog(getClass());

    public static final String TOOL_NAME = "importer";

    public static final File STORAGE_DIRECTORY = new File(
            System.getProperty("user.home"), TOOL_NAME);

    @Value("${spring.dataSource.url}")
    private String dataSourceURL;

    @Value("${spring.dataSource.username}")
    private String dataSourceUsername;
    @Value("${spring.dataSource.password}")
    private String dataSourcePassword;

    @Value("${spring.dataSource.driverClassName}")
    private String driverClassName;    
    
    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;    
    
       
    public static final File STORAGE_UPLOADS_DIRECTORY = new File(STORAGE_DIRECTORY, "uploads");

    @PostConstruct
    protected void setupStorage() throws Throwable {
        File[] files = {STORAGE_DIRECTORY, STORAGE_UPLOADS_DIRECTORY};
        for (File f : files) {
            if (!f.exists() && !f.mkdirs()) {
                String msg = String.format("Create storage directory ('%s') ", f.getAbsolutePath());
                throw new RuntimeException(msg);
            }
        }
    }

    @Bean
    LocalContainerEntityManagerFactoryBean entityManagerFactory(  JpaVendorAdapter adapter, DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setPackagesToScan(User.class.getPackage().getName());
        emf.setDataSource(dataSource);
        emf.setJpaProperties(getHibernateProperties());
        emf.setJpaVendorAdapter(adapter);
        return emf;
    }

    @Bean
    PlatformTransactionManager transactionManager( EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
   @Bean
    public DataSource getDataSource() {
            DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
            dataSourceBuilder.driverClassName(driverClassName);
            dataSourceBuilder.url(dataSourceURL);
            dataSourceBuilder.username(dataSourceUsername);
            dataSourceBuilder.password(dataSourcePassword);
            return dataSourceBuilder.build();
    }
    Properties getHibernateProperties(){    	
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", ddlAuto);       
        return properties;
    }
    
    @Configuration
    @Profile({"default", "test"})
    static class DefaultDataSourceConfiguration {

        private Log log = LogFactory.getLog(getClass());

        @PostConstruct
        protected void setupThings() throws Exception {
        	log.info("Passed");
        	
        	
        }

    }
}

