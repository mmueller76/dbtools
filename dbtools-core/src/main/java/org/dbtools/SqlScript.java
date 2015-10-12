package org.dbtools;

import org.apache.log4j.Logger;

import java.io.*;
import java.sql.*;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * A class to access an SQL script and execute it via JDBC.
 * <p/>
 * Created by IntelliJ IDEA.<br>
 * User: Michael Mueller<br>
 * Date: 07-Sep-2007<br>
 * Time: 10:38:59<br>
 */


public class SqlScript {

    private static final char QUERY_ENDS = ';';
    private static final String COMMENT = "--";
    private static final String WHITE_SPACE = "\\s*";
    private URL script;

    /**
     * the log4j Logger
     */
    private static Logger logger = Logger.getLogger(SqlScript.class);


    /**
     * Constructs a class representing an SQL script.
     *
     * @param filename path to the SQL script
     */
    public SqlScript(String filename) {
        try {
            this.script = new File(filename).toURL();
        } catch (MalformedURLException e) {
            logger.error(e);
        }
    }

    /**
     * Constructs a class representing an SQL script.
     *
     * @param fileURL URL of the SQL script
     */
    public SqlScript(URL fileURL) {
        this.script = fileURL;
    }

    /**
     * Returns statements of the SQL script as a list of strings.
     *
     * @return a list of SQL statements in the order of appearance in the script file
     * @throws IOException thrown if an error occurs while accessing the SQL script file
     */
    public List<String> getStatements() throws IOException {

        List<String> retVal = new LinkedList<String>();
        for (Iterator<String> statements = this.getStatementIterator(); statements.hasNext();) {
            retVal.add(statements.next());
        }
        return retVal;
    }

    /**
     * Executes the statements in the SQL script on a database.
     *
     * @param jdbcConnection JDBC connection to the database the script is to be executed on
     * @throws IOException  if an error occurs while accessing the SQL script file
     * @throws SQLException if an error occurs while executing the SQL statements
     */
    public void execute(Connection jdbcConnection) throws IOException, SQLException {

        Statement statement = jdbcConnection.createStatement();

        for (Iterator<String> statements = this.getStatementIterator(); statements.hasNext();) {
            statement.execute(statements.next());
        }

        statement.close();

    }

    /**
     * Prints the SQL statements in the script to stdout.
     *
     * @throws IOException if an error occurs while accessing the SQL script file
     */
    public void printStatements() throws IOException {

        for (Iterator<String> statements = this.getStatementIterator(); statements.hasNext();)
            System.out.println(statements.next());

    }

    /**
     * Checks if the line contains an end of statement mark
     *
     * @param line string to check for end of statement
     * @return true if the line contains an end of statement mark (';')
     */
    private boolean statementEnds(String line) {
        return (line.indexOf(QUERY_ENDS) != -1);
    }

    /**
     * Checks if the line is a comment line
     *
     * @param line string to check for comment mark
     * @return true if the line starts witn a comment mark ('--')
     */
    private boolean isComment(String line) {
        return (line != null) && (line.length() > 0) && line.replaceFirst(WHITE_SPACE, "").startsWith(COMMENT);
    }

    /**
     * Returns an iterator for the SQL statements in the SQL script
     *
     * @return string iterator
     * @throws IOException if an error occurs while accessing the SQL script file
     */
    public Iterator<String> getStatementIterator() throws IOException {
        return new StatementIterator();
    }

    /**
     * Implementation of the Iterator interface to iterate of the statements in the SQL script.
     */
    class StatementIterator implements Iterator<String> {

        /**
         * the BufferedReader to read the script file
         */
        private BufferedReader br;

        /**
         * the next statement parsed from the script file
         */
        private String nextStatement;

        /**
         * Constructs an Iterator to access the SQL statements in the script file.
         *
         * @throws IOException if an exception occurs while opening the script file
         */
        public StatementIterator() throws IOException {

            br = new BufferedReader(new InputStreamReader(script.openStream()));
            nextStatement = parseStatement();

        }

        /**
         * Returns whether the script contains more SQL statements.
         *
         * @return true of there are more statements
         */
        public boolean hasNext() {
            return nextStatement != null;
        }

        /**
         * Returns the next SQL statement.
         *
         * @return the statement
         */
        public String next() {

            String statement = nextStatement;
            nextStatement = parseStatement();
            return statement;

        }

        /**
         * Method not implemented.
         * @throws RuntimeException if called because it is not implemented
         */
        public void remove() {
            throw new RuntimeException("Method not implemented.");
        }

        /**
         * Parses a statement from the script file accessed through the BufferedReader.
         *
         * @return the SQL statement
         */
        private String parseStatement() {

            String retVal = null;

            try {

                String line;
                StringBuffer statement = new StringBuffer();

                boolean statementEnds;

                //read lines until end of statement is reached
                while ((line = br.readLine()) != null) {

                    if (isComment(line))
                        continue;

                    statementEnds = statementEnds(line);
                    statement.append(line);

                    if (statementEnds) {
                        retVal = statement.toString();
                        return retVal;
                    }

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return retVal;

        }
    }

}