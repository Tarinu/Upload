package shrug.config;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class DbInitializer implements ApplicationListener<ContextRefreshedEvent>{
    
    @Resource
    private JdbcTemplate jdbcTemplate;
    private static final Logger logger = Logger.getLogger(DbInitializer.class);
    
    /**
     * Creates needed database tables at application startup. Using postgres syntax.
     * @param event does nothing atm, its needed by the interface /shrug
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("Create files table");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS files(" +
                "id serial PRIMARY KEY," +
                "filename varchar(10) not null UNIQUE," +
                "type varchar(5) not null," +
                "location varchar(100) not null," +
                "created timestamp not null default now()" +
                ");");
        logger.info("Create comments table");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS comments(" +
                "id serial primary key," +
                "username varchar(30) not null DEFAULT 'anon'," +
                "comment text not null," +
                "timestamp timestamp not null DEFAULT now()," +
                "picture_id integer REFERENCES files ON UPDATE CASCADE ON DELETE CASCADE" +
                ");");
    }
}
