package migration;

import liquibase.integration.spring.SpringLiquibase;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteJdbcThinDriver;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Controller;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class MigrationConfig {

    @Bean
    public SpringLiquibase liquibase(DataSource ds, ApplicationContext applicationContext){
        ApplicationContextHolder.set(applicationContext);
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(ds);
        liquibase.setChangeLog("classpath:changelog-master.xml");
        liquibase.setDefaultSchema("PUBLIC");
        return liquibase;
    }

    @Bean
    public DataSource dataSource(Ignite ignite) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(IgniteJdbcThinDriver.class.getName());
        dataSource.setUrl("jdbc:ignite:thin://127.0.0.1:10800/PUBLIC");
        dataSource.setSchema("PUBLIC");

//        dataSource.getConnection().createStatement().execute("select * from DATABASECHANGELOGLOCK");
        try {
            ignite.cache("tableTest");
            dataSource.getConnection().createStatement().executeUpdate("");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dataSource;
    }

}
