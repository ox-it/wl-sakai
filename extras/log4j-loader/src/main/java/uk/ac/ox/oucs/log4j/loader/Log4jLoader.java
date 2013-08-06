package uk.ac.ox.oucs.log4j.loader;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.helpers.FileWatchdog;
import org.sakaiproject.component.api.ServerConfigurationService;
import org.sakaiproject.component.cover.ComponentManager;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Arrays;

/**
 * Context listener in charge of loading the log4j.properties file.
 * <p>
 * This is done to avoid having the {@link org.sakaiproject.log.impl.Log4jConfigurationManager} in charge of loading
 * the properties file.<br />
 * This could cause problems in the case where a web-app includes log4j as a library (in addition to the one provided
 * in the common folder of tomcat).<br />
 * In those cases the class loader of tomcat tends to load log4j twice and isn't able to load log4j.properties correctly.<br />
 * </p>
 * <p>
 * This webapp can't be reloaded as there isn't any way to stop the PropertyWatchdog, because of this when a reload
 * happens the old one is left running and then when the file changes all the classloaders are gone and it can't load
 * the classes.
 * </p>
 *
 * @author Colin Hebert
 */
public class Log4jLoader implements ServletContextListener {
    private static Logger logger = Logger.getLogger(Log4jLoader.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServerConfigurationService serverConfigurationService = (ServerConfigurationService) ComponentManager.get(ServerConfigurationService.class);
        String log4jConfigFilePath = serverConfigurationService.getSakaiHomePath() + "log4j.properties";
        // There's no way to shut it down, so just start going.
        new PropertyWatchdog(log4jConfigFilePath).start();

        /**
         * Sakai configuration shouldn't contain any log settings
         */
        String[] logConfigs = serverConfigurationService.getStrings("log.config");
        if (logConfigs != null) {
            logger.warn("Log configuration was found in the server configuration. Every log configuration should be in log4j.properties. " + Arrays.toString(logConfigs));
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    /**
     * This is very similar to the class in PropertyConfigurator except that it logs when reloading is happening.
     */
    class PropertyWatchdog extends FileWatchdog {

        PropertyWatchdog(String filename) {
            super(filename);
            setDelay(10000);
        }

        /**
         Call {@link PropertyConfigurator#configure(String)} with the
         <code>filename</code> to reconfigure log4j. */
        public
        void doOnChange() {
            logger.info("Started reloading log4j configuration.");
            new PropertyConfigurator().doConfigure(filename,
                    LogManager.getLoggerRepository());
            logger.info("Completed reloading log4j configuration.");
        }
    }
}
