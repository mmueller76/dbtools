package org.dbtools;

/**
 * Implementation of {@link AbstractDatabase} to access a MySQL database.
 * <p/>
 * Created by IntelliJ IDEA.<br>
 * User: Michael Mueller<br>
 * Date: 22-Jul-2005<br>
 * Time: 16:12:58<br>
 */
public class MySqlDatabase extends AbstractDatabase {

    /**
     * the driver class path
     */
    private static String driver = Configuration.getInstance().getProperty("mysql.driver");

    /**
     * the MySQL "well known" port
     */
    private static int defaultPort = Integer.parseInt(Configuration.getInstance().getProperty("mysql.default.port"));

    /**
     * Constructs a Database object representing a MySQL database.
     * The default port will be used for database connections.
     *
     * @param host   name/IP address of the MySQL server
     * @param schema initial schema to connect to
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public MySqlDatabase(String host, String schema) throws DatabaseException {
        super(VENDOR_MYSQL, driver, host, defaultPort, schema);
    }

    /**
     * Constructs a Database object representing a MySQL database.
     * The default port will be used for database connections.
     *
     * @param host     name/IP address of the MySQL server
     * @param schema   initial schema to connect to
     * @param user     username to be used when connecting to the database
     * @param password password to be used when connecting to the database
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public MySqlDatabase(String host, String schema, String user, char[] password) throws DatabaseException {
        super(VENDOR_MYSQL, driver, host, defaultPort, schema, user, password);
    }

    /**
     * Constructs a Database object representing a MySQL database.
     *
     * @param host     name/IP address of the MySQL server
     * @param port     port on MySQL server
     * @param schema   initial schema to connect to
     * @param user     username to be used when connecting to the database
     * @param password password to be used when connecting to the database
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public MySqlDatabase(String host, int port, String schema, String user, char[] password) throws DatabaseException {
        super(VENDOR_MYSQL, driver, host, port, schema, user, password);
    }

    /**
     * Constructs a Database object representing a MySQL database.
     *
     * @param host   name/IP address of the MySQL server
     * @param port   port on MySQL server
     * @param schema initial schema to connect to
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public MySqlDatabase(String host, int port, String schema) throws DatabaseException {
        super(VENDOR_MYSQL, driver, host, port, schema);
    }

    /**
     * {@inheritDoc}
     */
    protected String buildURL() {
        //format: jdbc:mysql://<host>:<port>/schema
        return "jdbc:mysql://" + host + ":" + port + "/" + schema;
    }

}
