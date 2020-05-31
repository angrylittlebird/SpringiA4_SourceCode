package spittr.jdbc;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.transaction.PlatformTransactionManager;
import spittr.db.SpitterRepository;
import spittr.db.SpittleRepository;
import spittr.db.jdbc.JdbcSpitterRepository;
import spittr.db.jdbc.JdbcSpittleRepository;

import javax.sql.DataSource;

@Configuration
public class JdbcConfig {
    @Value("classpath:spittr/db/jdbc/test-data_mysql.sql")
    private Resource dataScript;
    @Value("classpath:spittr/db/jdbc/schema_mysql.sql")
    private Resource schemaScript;

    @Profile("Mysql")
    @Bean
    public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) {//初始化数据库的数据
        final DataSourceInitializer initializer = new DataSourceInitializer();
        // 设置数据源
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(databasePopulator());
        return initializer;
    }

    // H2
    @Profile("H2")
    @Bean
    public DataSource h2DataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScripts("classpath:spittr/db/jdbc/schema.sql", "classpath:spittr/db/jdbc/test-data.sql")
                .build();
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    //  MySQL
    @Profile("Mysql")
    @Bean
    public DataSource mySqlDataSource() {
        //一、实例化BasicDataSource
        BasicDataSource bs = new BasicDataSource();

        //二、设置BasicDataSource属性
        //1、设置四个属性
        bs.setDriverClassName("com.mysql.cj.jdbc.Driver");
        bs.setUrl("jdbc:mysql://cdb-btmlbap0.bj.tencentcdb.com:10124/testdb?characterEncoding=UTF8");
        bs.setUsername("root");
        bs.setPassword("");//todo
        //2、设置连接是否默认自动提交
        bs.setDefaultAutoCommit(true);
        //3、设置初始后连接数
        bs.setInitialSize(3);
        //4、设置最大的连接数
//    bs.setMaxActive(5);
        //5、设置空闲等待时间，获取连接后没有操作开始计时，到达时间后没有操作回收链接
        bs.setMaxIdle(3000);

        return bs;
    }

    @Bean
    public SpitterRepository spitterRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcSpitterRepository(jdbcTemplate);
    }

    @Bean
    public SpittleRepository spittleRepository(JdbcTemplate jdbcTemplate) {
        return new JdbcSpittleRepository(jdbcTemplate);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    private DatabasePopulator databasePopulator() {
        System.out.println("==================sql脚本正在执行==================");
        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScripts(schemaScript);
        populator.addScripts(dataScript);
        System.out.println("==================sql脚本初始化完成==================");
        return populator;
    }

}
