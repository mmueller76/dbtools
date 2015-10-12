package org.dbtools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.PrintWriter;

/**
 * This class provides an implementation of the Database interface.
 * It loads the database driver required to connect to the specified
 * RDBMS. JDBC connections to the database can be established using
 * either using username and password or anonymously.
 * <p/>
 * Username and password can be specified as arguments in the constructor.
 * All calls to the <code>getConnection()</code> method will then return
 * a Connection established using that username and password.
 * <p/>
 * If no username and password are provided during object instanciation
 * the <code>getConnection()</code> method will try to establish an
 * anoymous JDBC connection using 'anonymous' as username and and empty
 * string as password. Non-anonymous conncetions can still be established
 * using the <code>getConnection(String user, String password)</code>
 * method.
 * <p/>
 * Created by IntelliJ IDEA.<br>
 * User: Michael Mueller<br>
 * Date: 22-Jul-2005<br>
 * Time: 16:43:30<br>
 */
public abstract class AbstractDatabase implements Database {

    ////////
    //fields

    /**
     * the RDBMS vendor
     */
    protected String vendor;

    /**
     * the class path of the driver class
     */
    protected String driver;

    /**
     * the database host name/IP address
     */
    protected String host;

    /**
     * the database port
     */
    protected int port;

    /**
     * the database schema to connect to
     */
    protected String schema;

    /**
     * the name of the user on whose behalf a connection is established (default is 'anonymous')
     */
    protected String user;

    /**
     * the password of the respective user
     */
    protected char[] password;


    //////////////
    //constructors

    /**
     * Creates a Database object to connect to the specified RDBMS
     *
     * @param vendor of the RDBMS
     * @param driver name of the driver class
     * @param host   the database server host
     * @param port   the database server port
     * @param schema the schema to initialy connect to
     * @throws DatabaseException if an exception occurs while loading the JDBC driver
     */
    protected AbstractDatabase(String vendor, String driver, String host, int port, String schema) throws DatabaseException {

        this.vendor = vendor;
        this.host = host;
        this.port = port;
        this.schema = schema;
        this.user = "anonymous";
        this.password = "".toCharArray();
        //load JDBC driver
        loadDriver(driver);

    }

    /**
     * Creates a Database object to connect to the specified RDBMS
     *
     * @param vendor   of the RDBMS
     * @param driver   name of the driver class
     * @param host     the database server host
     * @param port     the database server port
     * @param schema   the schema to initialy connect to
     * @param user     the name of the user on whose behalf a connection is established (default is 'anonymous')
     * @param password the password for the respective user
     * @throws DatabaseException if an exception occurs while loading the JDBC driver
     */
    protected AbstractDatabase(String vendor, String driver, String host, int port, String schema, String user, char[] password) throws DatabaseException {

        this(vendor, driver, host, port, schema);

        if (user == null)
            throw new IllegalArgumentException("User cannot be null.");
        this.user = user;

        if (password == null)
            throw new IllegalArgumentException("Password cannot be null.");

        this.password = password;

    }

    /**
     * Loads the JDBC driver class
     *
     * @param driver the class path of the driver class
     * @throws DatabaseException if an exception occurs while loading the driver class
     */
    protected void loadDriver(String driver) throws DatabaseException {

        try {
            Class.forName(driver).newInstance();
        } catch (InstantiationException e) {
            throw new DatabaseException("Unable to load database driver '" + driver + "'.'", e);
        } catch (IllegalAccessException e) {
            throw new DatabaseException("Unable to load database driver '" + driver + "'.'", e);
        } catch (ClassNotFoundException e) {
            throw new DatabaseException("Unable to load database driver '" + driver + "'.'", e);
        }

    }

    ///////////////////
    //getters & setters

    /**
     * Returns the class path of the JDBC driver class.
     * @return JDBC driver class
     */
    protected String getDriver() {
        return driver;
    }

    /**
     * {@inheritDoc}
     */
    public String getUser() {
        return user;
    }

    /**
     * {@inheritDoc}
     */
    public String getVendor() {
        return vendor;
    }

    /**
     * Returns the JDBC connection  URL string.
     *
     * @return database URL
     */
    public String getUrl() {
        return this.buildURL();
    }

    /**
     * {@inheritDoc}
     */
    public String getHost() {
        return host;
    }

    /**
     * {@inheritDoc}
     */
    public int getPort() {
        return port;
    }

    /**
     * Returns the schema to connect to.
     * @return the schema name
     */
    public String getSchema(){
        return schema;
    }

    /**
     * Sets the schema to connect to.
     * @param schema the schema name
     */
    public void setSchema(String schema){
        this.schema = schema;
    }

    /**
     * Factory method to create the JDBC connection URL string.
     *
     * @return the JDBC connection URL
     */
    protected abstract String buildURL();


    ///////////////////////////////////////
    //implementations of Datasource methods

    /**
     * {@inheritDoc}
     */
    public Connection getConnection() throws SQLException {

        String url = this.buildURL();

        return DriverManager.getConnection(url, user, new String(password));

    }

    /**
     * @throws SQLException
     */
    public Connection getConnection(String user, String password) throws SQLException {

        if (user == null)
            throw new IllegalArgumentException("User cannot be null.");
        if (password == null)
            throw new IllegalArgumentException("Password cannot be null.");

        String url = this.buildURL();
        return DriverManager.getConnection(url, user, password);

    }

    /**
     * {@inheritDoc}
     */
    public PrintWriter getLogWriter() throws SQLException {
        return DriverManager.getLogWriter();  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * {@inheritDoc}
     */
    public void setLogWriter(PrintWriter printWriter) throws SQLException {
        DriverManager.setLogWriter(printWriter);
    }

    /**
     * {@inheritDoc}
     */
    public void setLoginTimeout(int i) throws SQLException {
        DriverManager.setLoginTimeout(i);
    }

    /**
     * {@inheritDoc}
     */
    public int getLoginTimeout() throws SQLException {
        return DriverManager.getLoginTimeout();
    }

    ///////////////////////////////////////
    //implementations of Wrapper methods

    /**
     * Returns an object that implements the given interface to allow access to
     * non-standard methods, or standard methods not exposed by the proxy.
     * <p/>
     * If the receiver implements the interface then the result is the receiver
     * or a proxy for the receiver. If the receiver is a wrapper
     * and the wrapped object implements the interface then the result is the
     * wrapped object or a proxy for the wrapped object. Otherwise return the
     * the result of calling <code>unwrap</code> recursively on the wrapped object
     * or a proxy for that result. If the receiver is not a
     * wrapper and does not implement the interface, then an <code>SQLException</code> is thrown.
     *
     * @param iface A Class defining an interface that the result must implement.
     * @return an object that implements the interface. May be a proxy for the actual implementing object.
     * @throws java.sql.SQLException If no object found that implements the interface
     * @since 1.6
     */
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Returns true if this either implements the interface argument or is directly or indirectly a wrapper
     * for an object that does. Returns false otherwise. If this implements the interface then return true,
     * else if this is a wrapper then return the result of recursively calling <code>isWrapperFor</code> on the wrapped
     * object. If this does not implement the interface and is not a wrapper, return false.
     * This method should be implemented as a low-cost operation compared to <code>unwrap</code> so that
     * callers can use this method to avoid expensive <code>unwrap</code> calls that may fail. If this method
     * returns true then calling <code>unwrap</code> with the same argument should succeed.
     *
     * @param iface a Class defining an interface.
     * @return true if this implements the interface or directly or indirectly wraps an object that does.
     * @throws java.sql.SQLException if an error occurs while determining whether this is a wrapper
     *                               for an object with the given interface.
     * @since 1.6
     */
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}

