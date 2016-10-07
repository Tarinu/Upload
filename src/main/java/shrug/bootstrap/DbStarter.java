package shrug.bootstrap;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DbStarter implements ApplicationListener<ContextRefreshedEvent>{
    private JdbcTemplate jdbcTemplate;
    
    private static final Logger logger = Logger.getLogger(DbStarter.class);
    
    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Creates needed database tables at application startup. Using postgres syntax.
     * @param event does nothing atm, its needed by the interface /shrug
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logger.info("Create files table");
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS files(" +
                "id serial PRIMARY KEY, filename varchar(100), location varchar(150))");
    }
}
