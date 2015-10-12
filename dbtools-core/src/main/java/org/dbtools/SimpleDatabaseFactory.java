package org.dbtools;

/**
 * A factory to create Database objects for different vendors.
 * <p/>
 * Database objects can be created providing different levels
 * of detail on the connection. Where no information is provided
 * default values will be used, e.g. for the database server
 * port.
 * <p/>
 * Created by IntelliJ IDEA.<br>
 * User: Michael Mueller<br>
 * Date: 09-Jun-2006<br>
 * Time: 16:03:28<br>
 */
public class SimpleDatabaseFactory {

    /**
     * Creates a Database object for a MySQL database.
     *
     * @param host     name/IP address of the database server
     * @param port     port the database server is listening on
     * @param schema   schema to connect to
     * @param username name of the user on whose behalf the JDBC connection is established
     * @param password password the user is identified by
     * @return a Database object
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public static Database createMySQLDatabase(String host, int port, String schema, String username, char[] password) throws DatabaseException {
        return new MySqlDatabase(host, port, schema, username, password);
    }

    /**
     * Creates a Database object for a MySQL.
     *
     * @param host     name/IP address of the database server
     * @param schema   schema to connect to
     * @param username name of the user on whose behalf the JDBC connection is established
     * @param password password the user is identified by
     * @return a Database object
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public static Database createMySQLDatabase(String host, String schema, String username, char[] password) throws DatabaseException {
        return new MySqlDatabase(host, schema, username, password);
    }

    /**
     * Creates a Database object for a MySQL database.
     *
     * @param host   name/IP address of the database server
     * @param schema schema to connect to
     * @return a Database object
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public static Database createMySQLDatabase(String host, String schema) throws DatabaseException {
        return new MySqlDatabase(host, schema);
    }

    /**
     * Creates a Database object for a MySQL database.
     *
     * @param host   name/IP address of the database server
     * @param schema schema to connect to
     * @param port   port the database server is listening on
     * @return a Database object
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public static Database createMySQLDatabase(String host, int port, String schema) throws DatabaseException {
        return new MySqlDatabase(host, port, schema);
    }

    /**
     * Creates a Database object for an Oracle database.
     *
     * @param host     name/IP address of the database server
     * @param port     port the database server is listening on
     * @param instance the name of the Oracle instance
     * @param schema   schema to connect to
     * @param username name of the user on whose behalf the JDBC connection is established
     * @param password password the user is identified by
     * @return a Database object
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public static Database createOracleDatabase(String host, int port, String instance, String schema, String username, char[] password) throws DatabaseException {
        return new OracleDatabase(host, port, instance, schema, username, password);
    }

    /**
     * Creates a Database object for an Oracle database.
     *
     * @param host     name/IP address of the database server
     * @param instance the name of the Oracle instance
     * @param schema   schema to connect to
     * @param username name of the user on whose behalf the JDBC connection is established
     * @param password password the user is identified by
     * @return a Database object
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public static Database createOracleDatabase(String host, String instance, String schema, String username, char[] password) throws DatabaseException {
        return new OracleDatabase(host, instance, schema, username, password);
    }

    /**
     * Creates a Database object for an Oracle database.
     *
     * @param host     name/IP address of the database server
     * @param instance the name of the Oracle instance
     * @param schema   schema to connect to
     * @return a Database object
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public static Database createOracleDatabase(String host, String instance, String schema) throws DatabaseException {
        return new OracleDatabase(host, instance, schema);
    }

    /**
     * Creates a Database object for an Oracle database.
     *
     * @param host     name/IP address of the database server
     * @param instance the name of the Oracle instance
     * @param schema   schema to connect to
     * @param port     port the database server is listening on
     * @return a Database object
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public static Database createOracleDatabase(String host, int port, String instance, String schema) throws DatabaseException {
        return new OracleDatabase(host, port, instance, schema);
    }

    public static Database createHSqlTransientInProcessDatabase(String schema) throws DatabaseException {
        return new HSqlDatabase("", schema, "sa", "".toCharArray(), HSqlConnectionType.IN_PROCESS_TRANSIENT);
    }

    public static Database createHSqlPersistentInProcessDatabase(String directoryPath, String schema) throws DatabaseException {
        return new HSqlDatabase(directoryPath, schema, "sa", "".toCharArray(), HSqlConnectionType.IN_PROCESS_PERSISTENT);
    }

    /**
     * Creates a Database object for a HSQLDB database.
     *
     * @param host     name/IP address of the database server
     * @param port     port the database server is listening on
     * @param schema   schema to connect to
     * @param username name of the user on whose behalf the JDBC connection is established
     * @param password password the user is identified by
     * @return a Database object
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public static Database createHSqlDatabase(String host, int port, String schema, String username, char[] password) throws DatabaseException {
        return new HSqlDatabase(host, port, schema, username, password, HSqlConnectionType.SERVER);
    }

    /**
     * Creates a Database object for a HSQLDB database.
     *
     * @param host     name/IP address of the database server
     * @param schema   schema to connect to
     * @param username name of the user on whose behalf the JDBC connection is established
     * @param password password the user is identified by
     * @return a Database object
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public static Database createHSqlDatabase(String host, String schema, String username, char[] password) throws DatabaseException {
        return new HSqlDatabase(host, schema, username, password, HSqlConnectionType.SERVER);
    }

    /**
     * Creates a Database object for a HSQLDB database.
     *
     * @param host   name/IP address of the database server
     * @param schema schema to connect to
     * @return a Database object
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public static Database createHSqlDatabase(String host, String schema) throws DatabaseException {
        return new HSqlDatabase(host, schema, HSqlConnectionType.SERVER);
    }

    /**
     * Creates a Database object for a HSQLDB database.
     *
     * @param host   name/IP address of the database server
     * @param schema schema to connect to
     * @param port   port the server is listening on
     * @return a Database object
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public static Database createHSqlDatabase(String host, int port, String schema) throws DatabaseException {
        return new HSqlDatabase(host, port, schema, HSqlConnectionType.SERVER);
    }

    /**
     * Creates a Database object for a HSQLDB WebServer database.
     *
     * @param host     name/IP address of the HTTP server
     * @param port     port the server is listening on
     * @param schema   schema to connect to
     * @param username name of the user on whose behalf the JDBC connection is established
     * @param password password the user is identified by
     * @return a Database object
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public static Database createHSqlWebServerDatabase(String host, int port, String schema, String username, char[] password) throws DatabaseException {
        return new HSqlDatabase(host, port, schema, username, password, HSqlConnectionType.WEBSERVER);
    }

    /**
     * Creates a Database object for a HSQLDB WebServer database. Uses the HTTP
     * default port (80) for connections.
     *
     * @param host     name/IP address of the HTTP server
     * @param schema   schema to connect to
     * @param username name of the user on whose behalf the JDBC connection is established
     * @param password password the user is identified by
     * @return a Database object
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public static Database createHSqlWebServerDatabase(String host, String schema, String username, char[] password) throws DatabaseException {
        return new HSqlDatabase(host, schema, username, password, HSqlConnectionType.WEBSERVER);
    }

    /**
     * Creates a Database object for a HSQLDB WebServer database. Uses the HTTP
     * default port (80) for connections.
     *
     * @param host   name/IP address of the HTTP server
     * @param schema schema to connect to
     * @return a Database object
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public static Database createHSqlWebServerDatabase(String host, String schema) throws DatabaseException {
        return new HSqlDatabase(host, schema, HSqlConnectionType.WEBSERVER);
    }

    /**
     * Creates a Database object for a HSQLDB WebServer database.
     *
     * @param host   name/IP address of the HTTP server
     * @param schema schema to connect to
     * @param port   port the server is listening on
     * @return a Database object
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public static Database createHSqlWebServerDatabase(String host, int port, String schema) throws DatabaseException {
        return new HSqlDatabase(host, port, schema, HSqlConnectionType.WEBSERVER);
    }


}
