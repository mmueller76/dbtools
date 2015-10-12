package org.dbtools.ensembl;

import org.dbtools.*;

import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Statement;

import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.<br>
 * User: mmueller<br>
 * Date: 25-Sep-2007<br>
 * Time: 13:36:23<br>
 */
public class EnsemblDatabaseFactory {

    private String ensemblHost;

    private int ensemblPort;

    private String defaultSchema;

    /**
     * The regular expression to match Ensembl core database schema names
     */
    private static final String REGEX_SCHEMA_NAME = "(.*?)_(.*?)_ensemblDatabaseType_(\\d{2})_(.*)";

    /**
     * The SQL statement to fetch the NCBI taxon ID from a species core schema 'meta' table
     */
    private static final String SQL_SELECT_NCBI_TAXON_ID = "SELECT meta_value FROM :schemaName.meta WHERE meta_key = 'species.taxonomy_id'";

    /**
     * The prefix of Ensembl mart schemas
     */
    private static final String MART_SCHEMA_PREFIX = Configuration.getInstance().getProperty("ensembl.mart.schema.prefix");

    /**
     * Map of Ensembl species names, versions and variation database schema names
     */
    private Map<EnsemblDatabaseType, Map<String, SortedMap<Integer, String>>> databaseTypeSpeciesNamesAndVersions2VariationDatabasSchemaName = new HashMap<EnsemblDatabaseType, Map<String, SortedMap<Integer, String>>>();


    /**
     * NCBI taxon ID to species name mapping
     */
    private Map<Integer, String> ncbiTaxonId2SpeciesName = null;

    /**
     * the log4j Logger
     */
    private static Logger logger = Logger.getLogger(EnsemblDatabase.class);

    private Connection ensemblConnection;

    private boolean metaDataFetched = false;

    /**
     * Creates an Ensembl database factory based on a connection. The default schema
     * will be set to the schema of the connection.
     *
     * @param ensemblConnection connection to a Ensembl database server
     * @throws DatabaseException
     *          if an Exception occurs while accessing the connection meta data
     */
    public EnsemblDatabaseFactory(Connection ensemblConnection) throws DatabaseException {
        try {

            this.ensemblConnection = ensemblConnection;

            //get the schema name from the connection URL
            String url = ensemblConnection.getMetaData().getURL();
            String[] tokens = url.split("/");
            this.defaultSchema = tokens[tokens.length - 1];


        } catch (SQLException e) {
            throw new DatabaseException("Exception while constructing EnsemblDatabaseFactory using connection " + ensemblConnection.toString() + ".", e);
        }
    }

    /**
     * Creates an Ensembl database factory for the specified Ensembl database server.
     *
     * @param host          the Ensembl database host
     * @param port          the Ensembl database port
     * @param defaultSchema the schema for the initial connection to the Ensembl database server
     * @throws DatabaseException if an Exception occurs while communicating with the database
     */
    public EnsemblDatabaseFactory(String host, int port, String defaultSchema) throws DatabaseException {

        this.ensemblHost = host;
        this.ensemblPort = port;
        this.defaultSchema = defaultSchema;

        //get connection
        try {
            ensemblConnection = SimpleDatabaseFactory
                    .createMySQLDatabase(ensemblHost, ensemblPort, defaultSchema)
                    .getConnection();
        } catch (SQLException e) {

            throw new DatabaseException("Exception while establishing connection to Ensembl database.", e);

        }
    }

    /**
     * Creates an Ensembl database factory for the specified Ensembl database server.
     *
     * @param host the Ensembl database host
     * @param port the Ensembl database port
     * @throws DatabaseException if an Exception occurs while communicating with the database
     */
    public EnsemblDatabaseFactory(String host, int port) throws DatabaseException {

        this(host, port, Configuration.getInstance().getProperty("ensembl.db.default.schema"));

    }

    /**
     * Creates an Ensembl database factory using the default Ensembl database server.
     *
     * @throws DatabaseException
     */
    public EnsemblDatabaseFactory() throws DatabaseException {

        this(
                Configuration.getInstance().getProperty("ensembl.db.host"),
                new Integer(Configuration.getInstance().getProperty("ensembl.db.port")),
                Configuration.getInstance().getProperty("ensembl.db.default.schema")
        );

    }

    /**
     * Creates a Database object to connect to the specified Ensembl database.
     *
     * @param ncbiTaxonId         the NCBI taxon ID of the Ensembl species
     * @param release             the Ensembl release version
     * @param ensemblDatabaseType the Ensembl database type
     * @return the Database object
     * @throws DatabaseException if the species is not represented in Ensembl or if the release does
     *                           not exist for the specified Ensembl database type
     */
    private Database createDatabase(int ncbiTaxonId, int release, EnsemblDatabaseType ensemblDatabaseType) throws DatabaseException {

        //check if species in Ensembl
        if (!hasSpecies(ncbiTaxonId)) {

            throw new DatabaseException("Ensembl does not contain species identified by NCBI taxon ID " + ncbiTaxonId + ".");

        }

        //if release is '0' get current release version
        if (release == 0) {

            release = getCurrentReleaseVersion(ncbiTaxonId);

            //else check if release exists for species
        } else if (!hasRelease(ncbiTaxonId, release, ensemblDatabaseType)) {

            throw new DatabaseException("No release of database type '" + ensemblDatabaseType.toString() + "' version " + release + " does not exist for species identified by NCBI taxon ID " + ncbiTaxonId + ".");

        }

        //get parameters
        String host = Configuration.getInstance().getProperty("ensembl.db.host");
        int port = Integer.parseInt(Configuration.getInstance().getProperty("ensembl.db.port"));
        String schema = getSchemaName(ncbiTaxonId, release, ensemblDatabaseType);

        //create database object
        EnsemblDatabase retVal = new EnsemblDatabase(host, port, schema);
        retVal.release = release;
        retVal.speciesName = getSpeciesName(ncbiTaxonId);

        return retVal;

    }

    /**
     * Returns a Database object to access the most recent release of the specified Ensembl database schema for the specified species.
     *
     * @param ncbiTaxonId         NCBI taxon ID identifying the species
     * @param ensemblDatabaseType the Ensembl database type
     * @return the Database object
     * @throws DatabaseException if an exception occurs while communicating with the Ensembl MySQL database
     */
    public Database createEnsemblDatabase(int ncbiTaxonId, EnsemblDatabaseType ensemblDatabaseType) throws DatabaseException {

        return createDatabase(ncbiTaxonId, 0, ensemblDatabaseType);

    }

    /**
     * Returns a Database object to access the specified release of the specified Ensembl database schema for the specified species.
     *
     * @param ncbiTaxonId         NCBI taxon ID identifying the species
     * @param release             the release version
     * @param ensemblDatabaseType the Ensembl database type
     * @return the Database object
     * @throws DatabaseException if an exception occurs while communicating with the Ensembl MySQL database
     */
    public Database createEnsemblDatabase(int ncbiTaxonId, int release, EnsemblDatabaseType ensemblDatabaseType) throws DatabaseException {

        return createDatabase(ncbiTaxonId, release, ensemblDatabaseType);

    }


    /**
     * Returns a Database object to access the most recent release of the Ensembl mart database schema.
     * <p/>
     * Provides methodes to access species specific schema information.
     *
     * @param ncbiTaxonId NCBI taxon ID identifying the species
     * @return the Database object
     * @throws DatabaseException if an exception occurs while communicating with the Ensembl MySQL database
     */
    public Database createMartDatabase(int ncbiTaxonId) throws DatabaseException {

        //check if species in Ensembl
        if (!hasSpecies(ncbiTaxonId))
            throw new DatabaseException("Ensembl does not contain species identified by NCBI taxon ID " + ncbiTaxonId + ".");

        //get parameters
        String host = Configuration.getInstance().getProperty("ensembl.mart.host");
        int port = Integer.parseInt(Configuration.getInstance().getProperty("ensembl.mart.port"));
        int release = getCurrentReleaseVersion(ncbiTaxonId);
        String schema = getMartSchemaName(release);

        //create Database object
        EnsemblMartDatabase retVal = new EnsemblMartDatabase(host, port, schema);
        retVal.release = release;
        retVal.speciesName = getSpeciesName(ncbiTaxonId);
        retVal.ncbiTaxonId2SpeciesName = getNcbiTaxonId2SpeciesName();

        return retVal;
    }

    /**
     * Returns a Database object to access the specified release of the Ensembl mart database schema.
     * <p/>
     * Provides methodes to access species specific schema information.
     *
     * @param ncbiTaxonId NCBI taxon ID identifying the species
     * @param release     the release version
     * @return the Database object
     * @throws DatabaseException if an exception occurs while communicating with the Ensembl MySQL database
     */
    public Database createMartDatabase(int ncbiTaxonId, int release) throws DatabaseException {

        //check if species in Ensembl
        if (!hasSpecies(ncbiTaxonId))
            throw new DatabaseException("Ensembl does not contain species identified by NCBI taxon ID " + ncbiTaxonId + ".");

        //check if release exists of species
        if (!hasRelease(ncbiTaxonId, release, EnsemblDatabaseType.CORE))
            throw new DatabaseException("No release of database type '" + EnsemblDatabaseType.CORE.toString() + "' version " + release + " does not exist for species identified by NCBI taxon ID " + ncbiTaxonId + ".");

        //get parameters
        String host = Configuration.getInstance().getProperty("ensembl.mart.host");
        int port = Integer.parseInt(Configuration.getInstance().getProperty("ensembl.mart.port"));
        String schema = getMartSchemaName(release);

        //create database object
        EnsemblMartDatabase retVal = new EnsemblMartDatabase(host, port, schema);
        retVal.release = release;
        retVal.speciesName = getSpeciesName(ncbiTaxonId);
        retVal.ncbiTaxonId2SpeciesName = getNcbiTaxonId2SpeciesName();

        return retVal;
    }

    /**
     * Returns the names of species available in Ensembl.
     *
     * @return the species Names
     * @throws DatabaseException if an exception occures while accessing the Ensembl database.
     */
    public Set<String> getSpeciesNames() throws DatabaseException {

        Set<String> retVal = new TreeSet<String>();
        for (String speciesName : getNcbiTaxonId2SpeciesName().values())
            retVal.add(speciesName);

        return retVal;

    }

    /**
     * Returns the NCBI taxon IDs of species available in Ensembl.
     *
     * @return the NCBI taxon IDs
     * @throws DatabaseException if an exception occures while accessing the Ensembl database.
     */
    public Set<Integer> getNcbiTaxonIds() throws DatabaseException {

        return getNcbiTaxonId2SpeciesName().keySet();

    }

    /**
     * Checks if Ensembl contains the species identified by the NCBI taxon ID.
     *
     * @param ncbiTaxonId the NCBI taxon ID
     * @return true if Ensembl contains the species
     * @throws DatabaseException if an exception occurs while accessing the Ensembl MySQL database
     */
    private boolean hasSpecies(int ncbiTaxonId) throws DatabaseException {

        if (!metaDataFetched) {
            fetchMetaData();
        }

        return getNcbiTaxonId2SpeciesName().containsKey(ncbiTaxonId);

    }

    /**
     * Checks if Ensembl contains the Ensembl database type for specified release version and
     * the species identified by the NCBI taxon ID.
     *
     * @param ncbiTaxonId         the NCBI taxon ID
     * @param release             the release version
     * @param ensemblDatabaseType the Ensembl database type
     * @return true if Ensembl contains the species
     * @throws DatabaseException if an exception occurs while accessing the Ensembl MySQL database
     */
    private boolean hasRelease(int ncbiTaxonId, int release, EnsemblDatabaseType ensemblDatabaseType) throws DatabaseException {

        if (!metaDataFetched) {
            fetchMetaData();
        }

        return databaseTypeSpeciesNamesAndVersions2VariationDatabasSchemaName.containsKey(ensemblDatabaseType) &&
                databaseTypeSpeciesNamesAndVersions2VariationDatabasSchemaName.get(ensemblDatabaseType).containsKey(this.getNcbiTaxonId2SpeciesName().get(ncbiTaxonId)) &&
                databaseTypeSpeciesNamesAndVersions2VariationDatabasSchemaName
                        .get(ensemblDatabaseType)
                        .get(this.getNcbiTaxonId2SpeciesName().get(ncbiTaxonId)).containsKey(release);

    }

    /**
     * Returns the version of the latest Ensembl release.
     *
     * @param ncbiTaxonId the NCBI taxon ID of an Ensembl species
     * @return the release version
     * @throws DatabaseException if an exception occurs while accessing the Ensembl database
     */
    public int getCurrentReleaseVersion(int ncbiTaxonId) throws DatabaseException {


        return databaseTypeSpeciesNamesAndVersions2VariationDatabasSchemaName.get(EnsemblDatabaseType.CORE).get(getNcbiTaxonId2SpeciesName().get(ncbiTaxonId)).lastKey();
    }


    /**
     * Fetches meta data required to create Ensembl database objects: names and NCBI taxon IDs of the
     * species available in Ensembl, release versions and database types available for the individual
     * species, and schema names.
     *
     * @throws DatabaseException if an exception occurs while accessing the Ensembl database
     */
    private void fetchMetaData() throws DatabaseException {

        logger.info("Fetching database mete data.");
        try {

            //fetch species names, release versions and schema names
            for (EnsemblDatabaseType type : EnsemblDatabaseType.values()) {

                Map<String, SortedMap<Integer, String>> speciesNamesVersionsSchemaNames = fetchSpeciesNamesReleaseVersionsAndDatabaseSchemaNames(type);
                databaseTypeSpeciesNamesAndVersions2VariationDatabasSchemaName.put(type, speciesNamesVersionsSchemaNames);

            }

            //fetch NCBI taxon IDs of Ensembl species
            ncbiTaxonId2SpeciesName = fetchNcbiTaxonIds();

            metaDataFetched = true;

        } catch (DatabaseException e) {
            throw new DatabaseException("Exception while fetching meta information from Ensembl database.", e);
        }
    }

    /**
     * Fetches the NCBI taxon ID for Ensembl species from the 'meta' table of the species core schema.
     *
     * @return a map of NCBI taxon IDs and species names
     * @throws DatabaseException if an exception occurs while accessing the Ensembl database
     */
    private Map<Integer, String> fetchNcbiTaxonIds() throws DatabaseException {

        Map<String, SortedMap<Integer, String>> speciesName2Release2SchemaName = databaseTypeSpeciesNamesAndVersions2VariationDatabasSchemaName.get(EnsemblDatabaseType.CORE);

        Map<Integer, String> retVal = new TreeMap<Integer, String>();

        try {

            //create statement
            Statement s = ensemblConnection.createStatement();

            //for each species query 'meta' table for NCBI taxon ID
            for (String speciesName : speciesName2Release2SchemaName.keySet()) {

                int currentVersion = speciesName2Release2SchemaName.get(speciesName).lastKey();
                String schemaName = speciesName2Release2SchemaName.get(speciesName).get(currentVersion);

                String query = SqlUtil.setParameter(SQL_SELECT_NCBI_TAXON_ID, "schemaName", schemaName, false);
                ResultSet rs = s.executeQuery(query);
                if (rs.next()) {

                    String metaValue = rs.getString("meta_value");

                    //parse integer value
                    try {
                        int ncbiTaxonId = Integer.parseInt(metaValue);
                        retVal.put(ncbiTaxonId, speciesName);
                    } catch (NumberFormatException e) {
                        logger.warn(e);
                    }

                }

            }

            //clean up
            s.close();

        } catch (SQLException e) {
            throw new DatabaseException("Exception while fetching meta information from Ensembl database.", e);
        }

        return retVal;

    }

    /**
     * Returns the species available in Ensembl as well as the available Ensembl versions for each species.
     *
     * @param databaseType the Ensembl database type
     * @return a map of species names and sets of version numbers
     * @throws DatabaseException if an exception occures while accessing the Ensembl database.
     */
    private Map<String, SortedMap<Integer, String>> fetchSpeciesNamesReleaseVersionsAndDatabaseSchemaNames(EnsemblDatabaseType databaseType) throws DatabaseException {


        Map<String, SortedMap<Integer, String>> retVal = new TreeMap<String, SortedMap<Integer, String>>();

        try {

            String regex = REGEX_SCHEMA_NAME.replace("ensemblDatabaseType", databaseType.toString().toLowerCase());

            //compile regex pattern
            Pattern pattern = Pattern.compile(regex);

            //get core database species and versions
            ResultSet rs = ensemblConnection.getMetaData().getCatalogs();

            while (rs.next()) {

                //match core database schema names
                String schemaName = rs.getString(1);
                Matcher matcher = pattern.matcher(schemaName);

                if (matcher.find()) {

                    //get species name
                    String species = matcher.group(1) + " " + matcher.group(2);

                    //parse version
                    String versionString = matcher.group(3);
                    Integer version = 0;

                    try {
                        version = new Integer(versionString);
                    } catch (NumberFormatException e) {
                        logger.warn("Exception while parsing version '" + versionString + "'.");
                    }

                    //species and version to return value
                    if (version != 0) {

                        if (!retVal.containsKey(species))
                            retVal.put(species, new TreeMap<Integer, String>());

                        retVal.get(species).put(version, schemaName);

                    }

                }

            }

        } catch (SQLException e) {
            throw new DatabaseException("Exception while fetching meta information from Ensembl database.", e);
        }

        return retVal;

    }

    /**
     * Returns the schema name for the specified Ensembl Mart schema.
     *
     * @param version the Mart version
     * @return the schema name
     */
    private String getMartSchemaName(int version) {
        return MART_SCHEMA_PREFIX + "_" + version;
    }

    /**
     * Returns the name of the species identified by the NCBI taxon ID
     *
     * @param ncbiTaxonId NCBI taxon ID of the species
     * @return the species name
     * @throws DatabaseException if an exception occurs while accessing the Ensembl MySQL database
     */
    private String getSpeciesName(int ncbiTaxonId) throws DatabaseException {

        return getNcbiTaxonId2SpeciesName().get(ncbiTaxonId);

    }

    /**
     * Returns the schema name for the species, release version and Ensembl database type if such a schema
     * exists.
     *
     * @param ncbiTaxonId the NCBI taxon ID identifying the species
     * @param release     the Ensembl release version
     * @param type        the Ensembl database type
     * @return the schema name or <code>null</code> if no such schema exists
     * @throws DatabaseException
     *          if an Exception occurs while fetching meta data from the database
     */
    public String getSchemaName(int ncbiTaxonId, int release, EnsemblDatabaseType type) throws DatabaseException {

        if (!metaDataFetched) {
            try {
                fetchMetaData();
            } catch (DatabaseException e) {
                logger.error("Exception fetching schema name from database.", e);
            }
        }

        String retVal = null;

        if (hasRelease(ncbiTaxonId, release, type)) {

            retVal = databaseTypeSpeciesNamesAndVersions2VariationDatabasSchemaName
                    .get(type)
                    .get(this.getNcbiTaxonId2SpeciesName().get(ncbiTaxonId))
                    .get(release);

        }

        return retVal;

    }

    /**
     * Returns all release versions for the specified species and Ensembl database type
     * available on the Ensembl database server.
     *
     * @param ncbiTaxonId         the NCBI taxon ID of the species
     * @param ensemblDatabaseType the Ensembl database type
     * @return the release versions
     * @throws DatabaseException
     *          if the specified species in not in Ensembl or
     *          an exception occurs while communicating with the database server
     */
    public Set<Integer> getReleaseVersions(int ncbiTaxonId, EnsemblDatabaseType ensemblDatabaseType) throws DatabaseException {

        if (!hasSpecies(ncbiTaxonId)) {

            throw new DatabaseException("Ensembl does not contain species identified by NCBI taxon ID " + ncbiTaxonId + ".");

        }

        if (!metaDataFetched) {
            try {
                fetchMetaData();
            } catch (DatabaseException e) {
                logger.error("Exception while fetching meta information from Ensembl database.", e);
            }
        }


        return databaseTypeSpeciesNamesAndVersions2VariationDatabasSchemaName
                .get(ensemblDatabaseType)
                .get(this.getNcbiTaxonId2SpeciesName().get(ncbiTaxonId)).keySet();


    }

    public Map<Integer, String> getNcbiTaxonId2SpeciesName() {

        if (!metaDataFetched) {
            try {
                fetchMetaData();
            } catch (DatabaseException e) {
                logger.error("Exception while fetching meta information from Ensembl database.", e);
            }
        }

        return ncbiTaxonId2SpeciesName;
    }

    public String getEnsemblHost() {
        return ensemblHost;
    }

    public int getEnsemblPort() {
        return ensemblPort;
    }

    public String getDefaultSchema() {
        return defaultSchema;
    }
}
