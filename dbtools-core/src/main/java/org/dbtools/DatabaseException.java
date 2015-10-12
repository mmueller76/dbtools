package org.dbtools;

/**
 * This is an exception that is thrown when an error occurs during a JDBC operation.
 * <p/>
 * Created by IntelliJ IDEA.<br>
 * User: Michael Mueller<br>
 * Date: 22-Jul-2005<br>
 * Time: 15:11:31<br>
 */
public class DatabaseException extends Exception{

    /**
     * {@inheritDoc}
     */
    public DatabaseException(){
        super();
    }

    /**
     * {@inheritDoc}
     */
    public DatabaseException(String message){
        super(message);
    }

    /**
     * {@inheritDoc}
     */
    public DatabaseException(String message, Throwable cause){
        super(message, cause);
    }

    /**
     * {@inheritDoc}
     */
    public DatabaseException(Throwable cause){
        super(cause);
    }

}
