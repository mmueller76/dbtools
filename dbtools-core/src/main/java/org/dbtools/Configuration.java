package org.dbtools;

import org.apache.log4j.Logger;

import java.util.Properties;
import java.io.IOException;

/**
 * A singleton to provide global access to the configuration properties.
 * <p/>
 * Created by IntelliJ IDEA.<br>
 * User: Michael Mueller<br>
 * Date: 12-Sep-2007<br>
 * Time: 14:24:51<br>
 */
public class Configuration {

    /**
     * the singleton instance
     */
    private static Configuration ourInstance;

    /**
     * the configuration properties
     */
    private static Properties properties = new Properties();

    /**
     * the log4j logger
     */
    private static Logger logger = Logger.getLogger(Configuration.class);

    /**
     * Returns the Configuration instance.
     *
     * @return the Configuration
     */
    public static Configuration getInstance() {
        if (ourInstance == null)
            ourInstance = new Configuration();

        return ourInstance;
    }

    /**
     * Constructs a singleton object to access the configurtation properties.
     */
    private Configuration() {

        //load properties
        try {
            properties.load(this.getClass().getResourceAsStream("/dbtools.properties"));
        } catch (IOException e) {
            logger.error(e);
        }

    }

    /**
     * Returns a property value.
     *
     * @param key the propery key
     * @return the property value
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * Sets a property value.
     *
     * @param key   the property key
     * @param value the property value
     * @return the previous value of the property, null if the property wasn't set before
     */
    public String setProperty(String key, String value) {

        return (String) properties.setProperty(key, value);

    }
}
