package org.dbtools.ensembl;

import org.dbtools.DatabaseException;
import org.dbtools.MySqlDatabase;

/**
 * Provides access to Ensembl core schemas.
 * <p/>
 * Created by IntelliJ IDEA.<br>
 * User: Michael Mueller<br>
 * Date: 21-Sep-2007<br>
 * Time: 12:58:18<br>
 */
public class EnsemblDatabase extends MySqlDatabase {

    /**
     * The name of the species for this Database object
     */
    protected String speciesName;

    /**
     * The NCBI taxon ID of the species for this Database object
     */
    protected int ncbiTaxonId;

    /**
     * The release version of the Ensembl core schema of this Database object
     */
    protected int release;


    /**
     * Creates a Database object to connect to the specified Ensembl core schema.
     *
     * @throws DatabaseException if an error occurs while loading the database driver
     * @param host Ensembl database host
     * @param port Ensembl databse port
     * @param schema Ensembl core schema name
     */
    public EnsemblDatabase(String host, int port, String schema) throws DatabaseException {
        super(host, port, schema);

    }


    ///////////////////
    //getters & setters

    /**
     * Returns the NCBI taxon ID of the species this Database object provides access to.
     *
     * @return the species NCBI taxon ID
     */
    public int getNcbiTaxonId() {
        return ncbiTaxonId;
    }


    /**
     * Returns the Ensembl release version this Database object provides access to.
     *
     * @return the release version
     */
    public int getRelease() {
        return release;
    }


    /**
     * Returns the species name for the core database represented by this Database object.
     *
     * @return the species name
     */
    public String getSpeciesName() {
        return speciesName;
    }

}
