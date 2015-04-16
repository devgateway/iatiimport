package org.devgateway.importtool.services;

import java.io.File;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

@ComponentScan
@Configuration
@EnableJpaRepositories
/***
 * Sample Service configuration
 * Remove if not needed
 *
 */
public class ServiceConfiguration {

    public static final String TOOL_NAME = "importer";

    public static final File STORAGE_DIRECTORY = new File(
            System.getProperty("user.home"), TOOL_NAME);

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
        emf.setJpaVendorAdapter(adapter);
        return emf;
    }

    @Bean
    PlatformTransactionManager transactionManager( EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
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

