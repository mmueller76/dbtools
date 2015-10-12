package org.dbtools;


/**
 * Implementation of {@link AbstractDatabase} to access an HSQLDB database.
 * <p/>
 * Created by IntelliJ IDEA.<br>
 * User: Michael Mueller<br>
 * Date: 22-Jul-2005<br>
 * Time: 16:12:58<br>
 */
public class HSqlDatabase extends AbstractDatabase {

    /**
     * the driver class path
     */
    private static String driver = Configuration.getInstance().getProperty("hsql.driver");

    /**
     * the HSQLDB "well known" port
     */
    private static int defaultPort = Integer.parseInt(Configuration.getInstance().getProperty("hsql.default.port"));

    /**
     * the HTTP default port
     */
    private static final int HTTP_DEFAULT_PORT = 80;

    /**
     * the HSQLDB connection type (server, webserver, transient in process, persistent in process)
     */
    private HSqlConnectionType connectionType = HSqlConnectionType.SERVER;


    /**
     * Constructs a Database object representing a HSQLDB database.
     * <p/>
     * The default port and default connection type (server connection)
     * will be used for database connections.
     *
     * @param hostOrDirectory   name/IP address of the HSQLDB server or
     *                          path to database directory in case of a
     *                          persistent in process database
     * @param schema initial schema to connect to
     * @param connectionType the HSQLDB connection type (server, webserver, transient in process, persistent in process)
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public HSqlDatabase(String hostOrDirectory, String schema, HSqlConnectionType connectionType) throws DatabaseException {
        super(VENDOR_HSQL, driver, hostOrDirectory, defaultPort, schema);
        this.connectionType=connectionType;
        if(connectionType == HSqlConnectionType.WEBSERVER)
            this.port = HTTP_DEFAULT_PORT;
    }

    /**
     * Constructs a Database object representing a HSQLDB database.
     * The default port will be used for database connections.
     *
    * @param hostOrDirectory   name/IP address of the HSQLDB server or
     *                          path to database directory in case of a
     *                          persistent in process database
     * @param schema   initial schema to connect to
     * @param user     username to be used when connecting to the database
     * @param password password to be used when connecting to the database
     * @param connectionType the HSQLDB connection type (server, webserver, transient in process, persistent in process)
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public HSqlDatabase(String hostOrDirectory, String schema, String user, char[] password, HSqlConnectionType connectionType) throws DatabaseException {
        super(VENDOR_HSQL, driver, hostOrDirectory, defaultPort, schema, user, password);
        this.connectionType=connectionType;
        if(connectionType == HSqlConnectionType.WEBSERVER)
            this.port = HTTP_DEFAULT_PORT;
    }

    /**
     * Constructs a Database object representing a HSQLDB database.
     *
     * @param hostOrDirectory   name/IP address of the HSQLDB server or
     *                          path to database directory in case of a
     *                          persistent in process database
     * @param port     port on HSQLDB server
     * @param schema   initial schema to connect to
     * @param user     username to be used when connecting to the database
     * @param password password to be used when connecting to the database
     * @param connectionType the HSQLDB connection type (server, webserver, transient in process, persistent in process)
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public HSqlDatabase(String hostOrDirectory, int port, String schema, String user, char[] password, HSqlConnectionType connectionType) throws DatabaseException {
        super(VENDOR_HSQL, driver, hostOrDirectory, port, schema, user, password);
        this.connectionType=connectionType;
    }

    /**
     * Constructs a Database object representing a HSQLDB database.
     *
     * @param hostOrDirectory   name/IP address of the HSQLDB server or
     *                          path to database directory in case of a
     *                          persistent in process database
     * @param port   port on HSQLDB server
     * @param schema initial schema to connect to
     * @param connectionType the HSQLDB connection type (server, webserver, transient in process, persistent in process)
     * @throws DatabaseException if an exception occurs when loading the JDBC driver class
     */
    public HSqlDatabase(String hostOrDirectory, int port, String schema, HSqlConnectionType connectionType) throws DatabaseException {
        super(VENDOR_HSQL, driver, hostOrDirectory, port, schema);
        this.connectionType=connectionType;
    }

    /**
     * {@inheritDoc}
     */
    protected String buildURL() {

        //formats:
        //server               : jdbc:hsqldb:hsql://host[:port][/<alias>][<key-value-pairs>]
        //webserver            : jdbc:hsqldb:http://host[:port][/<alias>][<key-value-pairs>]
        //in process transient : jdbc:hsqldb:mem:<alias>[<key-value-pairs>]
        //in process persistent: jdbc:hsqldb:file:<path>[<key-value-pairs>]

        String retVal = "";

        switch (connectionType) {

            case SERVER: retVal = "jdbc:hsqldb:hsql://" + host + ":" + port + "/" + schema;
					     break;

			case WEBSERVER: retVal = "jdbc:hsqldb:http://" + host + ":" + port + "/" + schema;
					     break;

			case IN_PROCESS_TRANSIENT: retVal = "jdbc:hsqldb:mem:" + schema;
                        break;

            case IN_PROCESS_PERSISTENT: retVal = "jdbc:hsqldb:file:" + host + "/" + schema;
					     break;
					     			
		}

        return retVal;

    }

}
