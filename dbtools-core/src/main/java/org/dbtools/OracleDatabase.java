package org.dbtools;

/**
 * Implementation of {@link AbstractDatabase} to access an Oracle database.
 * <p/>
 * Created by IntelliJ IDEA.<br>
 * User: Michael Mueller<br>
 * Date: 22-Jul-2005<br>
 * Time: 16:14:22<br>
 */
public class OracleDatabase extends AbstractDatabase {

    /**
     * the driver class path
     */
    private static String driver = Configuration.getInstance().getProperty("oracle.driver");

    /**
     * the Oracle "well known" port
     */
    private static int defaultPort = Integer.parseInt(Configuration.getInstance().getProperty("oracle.default.port"));

    /**
     * thr Oracle instance
     */
    private String instance;


    /////////////
    //constuctors

    /**
     * Constructs a Database object representing an Oracle database.
     * The default port will be used for database connections.
     *
     * @param host     name/IP address of the Oracle server
     * @param instance the Oracle instance
     * @param schema   initial schema to connect to
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public OracleDatabase(String host, String instance, String schema) throws DatabaseException {
        super(Database.VENDOR_ORACLE, driver, host, defaultPort, schema);
        this.instance = instance;
    }

    /**
     * Constructs a Database object representing an Oracle database.
     * The default port will be used for database connections.
     *
     * @param host     name/IP address of the Oracle server
     * @param instance the Oracle instance
     * @param schema   initial schema to connect to
     * @param user     username to be used when connecting to the database
     * @param password password to be used when connecting to the database
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public OracleDatabase(String host, String instance, String schema, String user, char[] password) throws DatabaseException {
        super(Database.VENDOR_ORACLE, driver, host, defaultPort, schema, user, password);
        this.instance = instance;
    }

    /**
     * Constructs a Database object representing an Oracle database.
     *
     * @param host     name/IP address of the Oracle server
     * @param instance the Oracle instance
     * @param port     port on server
     * @param schema   initial schema to connect to
     * @param user     username to be used when connecting to the database
     * @param password password to be used when connecting to the database
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public OracleDatabase(String host, int port, String instance, String schema, String user, char[] password) throws DatabaseException {
        super(Database.VENDOR_ORACLE, driver, host, port, schema, user, password);
        this.instance = instance;
    }

    /**
     * Constructs a Database object representing an Oracle database.
     *
     * @param host   name/IP address of the Oracle server
     * @param port   port on server
     * @param instance the Oracle instance
     * @param schema initial schema to connect to
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public OracleDatabase(String host, int port, String instance, String schema) throws DatabaseException {
        super(Database.VENDOR_ORACLE, driver, host, port, schema);
        this.instance = instance;
    }


    ///////////////////
    //getters & setters

    /**
     * Returns the Oracle instance.
     *
     * @return the instance
     */
    public String getInstance() {
        return instance;
    }

    /**
     * Sets the Oracle instance.
     *
     * @param instance the instance
     */
    public void setInstance(String instance) {
        this.instance = instance;
    }

    /**
     * {@inheritDoc}
     */
    protected String buildURL() {
        //format: jdbc:oracle:thin:@<host>:<port>:<instance>/<schema>
        return "jdbc:oracle:thin:@" + host + ":" + port + ":" + instance;
    }

}
