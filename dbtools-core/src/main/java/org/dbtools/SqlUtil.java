package org.dbtools;

import java.util.Iterator;
import java.util.Set;

/**
 * Provides methods to manipulate SQL statements.
 * <p/>
 * Created by IntelliJ IDEA.<br>
 * User: Michael Mueller<br>
 * Date: 09-Jun-2006<br>
 * Time: 14:29:41<br>
 */
public abstract class SqlUtil {

    /**
     * Sets the specified string parameter of an SQL statement.
     * <p/>
     * The parameter placeholder in the SQL statement needs to be of
     * the format :<parameterName> (e.g. :parameterName).
     *
     * @param sqlStatement   the SQL statement to set the parameter in
     * @param parameterName  the parameter to set
     * @param parameterValue the value of the parameter
     * @return the SQL statement with the parameter place holder replaced by the parameter value
     */
    public static String setParameter(String sqlStatement, String parameterName, String parameterValue) {
        return setParameter(sqlStatement, parameterName, parameterValue, true);
    }

    /**
     * Sets the specified string parameter of an SQL statement.
     * <p/>
     * The parameter placeholder in the SQL statement needs to be of
     * the format :<parameterName> (e.g. :parameterName). The boolean
     * parameter specifies wether or not the parameter will be enclosed
     * by quotes.
     *
     * @param sqlStatement   the SQL statement to set the parameter in
     * @param parameterName  the parameter to set
     * @param parameterValue the value of the parameter
     * @param quotes         if true the string parameter will be enclosed by single quotes (')
     * @return the SQL statement with the parameter place holder replaced by the parameter value
     */
    public static String setParameter(String sqlStatement, String parameterName, String parameterValue, boolean quotes) {

        if (quotes) {
            return sqlStatement.replace(":" + parameterName, "'" + parameterValue + "'");
        } else {
            return sqlStatement.replace(":" + parameterName, parameterValue);
        }
    }

    /**
     * Sets the specified integer parameter of an SQL statement.
     * <p/>
     * The parameter placeholder in the SQL statement needs to be of
     * the format :<parameterName> (e.g. :parameterName).
     *
     * @param sqlStatement   the SQL statement to set the parameter in
     * @param parameterName  the parameter to set
     * @param parameterValue the value of the parameter
     * @return the SQL statement with the parameter place holder replaced by the parameter value
     */
    public static String setParameter(String sqlStatement, String parameterName, int parameterValue) {

        return sqlStatement.replace(":" + parameterName, "" + parameterValue);

    }


    /**
     * Sets the specified parameter set of an SQL statement.
     * <p/>
     * The parameter placeholder in the SQL statement needs to be of
     * the format :<parameterName> (e.g. :parameterName). If the
     * parameter sets are instances of String the values will be
     * enclosed in quotes ('<code>'</code>') in the returned statement. 
     *
     * @param sqlStatement    the SQL statement to set the parameter in
     * @param parameterName   the parameter to set
     * @param parameterValues the values of the parameter
     * @return the SQL statement with the parameter place holder replaced by the parameter value
     */
    public static String setParameterSet(String sqlStatement, String parameterName, Set parameterValues) {

        StringBuffer sb = new StringBuffer();

        for (Iterator idIterator = parameterValues.iterator(); idIterator.hasNext();) {

            Object value = idIterator.next();

            //if string value...
            if (value instanceof String)
                //...surround parameter value with quote
                sb.append("'").append(value).append("'");

                //if non-string value...
            else
                //...don't use quotes
                sb.append(value);

            //if more parameter to come appand comma
            if (idIterator.hasNext())
                sb.append(", ");

        }

        return sqlStatement.replace(":" + parameterName, sb.toString());
    }

}
