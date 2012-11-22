package uk.ac.ox.oucs.log4j.loader;

import org.apache.log4j.PropertyConfigurator;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.component.cover.ComponentManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Context listener in charge of loading the log4j.properties file.
 * <p>
 * This is done to avoid having the {@link org.sakaiproject.log.impl.Log4jConfigurationManager} in charge of loading
 * the properties file.<br />
 * This could cause problems in the case where a web-app includes log4j as a library (in addition to the one provided
 * in the common folder of tomcat).<br />
 * In those cases the class loader of tomcat tends to load log4j twice and isn't able to load log4j.properties correctly.<br />
 * </p>
 *
 * @author Colin Hebert
 */
public class Log4jLoader implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServerConfigurationService serverConfigurationService = (ServerConfigurationService) ComponentManager.get(ServerConfigurationService.class);
        String log4jConfigFilePath = serverConfigurationService.getSakaiHomePath() + "log4j.properties";
        PropertyConfigurator.configureAndWatch(log4jConfigFilePath);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
