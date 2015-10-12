package org.dbtools;

import javax.sql.DataSource;

/**
 * A Database object provides access to a relational database.
 * <p/>
 * Created by IntelliJ IDEA.<br>
 * User: Michael Mueller<br>
 * Date: 09-Jun-2006<br>
 * Time: 15:20:05<br>
 */
public interface Database extends DataSource {

    final String VENDOR_MYSQL = Configuration.getInstance().getProperty("mysql.vendor.name");
    final String VENDOR_ORACLE = Configuration.getInstance().getProperty("oracle.vendor.name");
    final String VENDOR_POSTGRESQL = Configuration.getInstance().getProperty("postgresql.vendor.name");
    final String VENDOR_HSQL = Configuration.getInstance().getProperty("hsql.vendor.name");

    /**
     * Returns the name of the user on whose behalf
     * a connection to the database will be estabished.
     * @return the username
     */
    String getUser();

    /**
     * Returns the name/IP address of the database server
     * @return the database server host
     */
    String getHost();

    /**
     * Returns the database port on the server
     * @return the database port
     */
    int getPort();

    /**
     * Returns the RDBMS vendor name
     * @return the vendor name
     */
    String getVendor();

}
