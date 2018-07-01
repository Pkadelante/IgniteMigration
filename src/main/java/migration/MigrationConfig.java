package migration;

import liquibase.integration.spring.SpringLiquibase;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteJdbcThinDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.sql.SQLException;

import static java.util.Objects.isNull;

@Configuration
public class MigrationConfig {

    @Value("${storage.igniteJdbcUrl}")
    private String igniteJdbcUrl;

    private static final int TABLE_EXIST_ERROR_CODE = 955;
    private static final String DEFAULT_SCHEMA = "PUBLIC";

    @Bean
    public SpringLiquibase liquibase(ApplicationContext applicationContext, javax.sql.DataSource ds) {
        ApplicationContextHolder.set(applicationContext);
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog("classpath:db/changelog/changelog-master.xml");
        liquibase.setDataSource(ds);
        liquibase.setDefaultSchema(DEFAULT_SCHEMA);
        return liquibase;
    }

    @LiquibaseDataSource
    @Bean
    public javax.sql.DataSource configureDataSource(Ignite ignite) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(igniteJdbcUrl);
        dataSource.setDriverClassName(IgniteJdbcThinDriver.class.getName());
        dataSource.setSchema(DEFAULT_SCHEMA);

        try {
            if (isNull(ignite.cache("SQL_PUBLIC_DATABASECHANGELOGLOCK"))) {
                dataSource.getConnection().createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS PUBLIC.DATABASECHANGELOGLOCK (ID INT PRIMARY KEY , LOCKED INT, LOCKEDBY VARCHAR(255), LOCKGRANTED datetime)  with \"TEMPLATE=PARTITIONED, backups=3\"");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() != TABLE_EXIST_ERROR_CODE) {
                throw new RuntimeException("Не удалось создать таблицу SQL_PUBLIC_DATABASECHANGELOGLOCK для Liquibase", e);
            }
        }

        try {
            if (isNull(ignite.cache("SQL_PUBLIC_DATABASECHANGELOG"))) {
                dataSource.getConnection().createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS PUBLIC.DATABASECHANGELOG (ID VARCHAR(255) NOT NULL PRIMARY KEY , AUTHOR VARCHAR(255) NOT NULL, FILENAME VARCHAR(255) NOT NULL, DATEEXECUTED datetime NOT NULL, ORDEREXECUTED INT NOT NULL, EXECTYPE VARCHAR(10) NOT NULL, MD5SUM VARCHAR(35), DESCRIPTION VARCHAR(255), COMMENTS VARCHAR(255), TAG VARCHAR(255), LIQUIBASE VARCHAR(20), CONTEXTS VARCHAR(255), LABELS VARCHAR(255), DEPLOYMENT_ID VARCHAR(10))  with \"TEMPLATE=PARTITIONED, backups=3\"");
            }
        } catch (SQLException e) {
            if (e.getErrorCode() != TABLE_EXIST_ERROR_CODE) {
                throw new RuntimeException("Не удалось создать таблицу DATABASECHANGELOG для Liquibase", e);
            }
        }

        return dataSource;
    }

}