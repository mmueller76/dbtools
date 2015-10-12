package org.dbtools.ensembl;

import org.dbtools.DatabaseException;
import org.dbtools.MySqlDatabase;

import java.util.Map;


/**
 * Provides access to the Ensembl Mart schemas.
 *
 * Created by IntelliJ IDEA.<br>
 * User: Michael Mueller<br>
 * Date: 21-Sep-2007<br>
 * Time: 12:58:07<br>
 */
public class EnsemblMartDatabase extends MySqlDatabase {

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

    protected Map<Integer, String> ncbiTaxonId2SpeciesName;

    /**
     * Creates a Database object to connect to the Ensembl Mart schema.
     *
     * @throws DatabaseException if an error occurs while loading the database driver
     * @param host Ensembl Mart database host
     * @param port Ensembl Mart databse port
     * @param schema Ensembl Mart schema name
     */
    EnsemblMartDatabase(String host, int port, String schema) throws DatabaseException {
        super(host, port, schema);
    }

    /**
     * Returns the Ensembl Mart schema table prefix for the species of this EnsemblMartDatabase object.
     *
     * @return the database prefix
     */
    public String getTableNamePrefix() {

        return speciesName.split(" ")[1].substring(0, 1) + //the first character of the species name
                speciesName.split("_")[2];

    }

    /**
     * Returns the Ensembl Mart schema table prefix for the species identified by the NCBI taxon ID.
     *
     * @return the database prefix
     * @param ncbiTaxonId the NCBI taxon ID
     */
    public String getTableNamePrefix(int ncbiTaxonId) {

        String speciesName = ncbiTaxonId2SpeciesName.get(ncbiTaxonId);
        return speciesName.split(" ")[1].substring(0, 1) + //the first character of the species name
                speciesName.split("_")[2];

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
     * Returns the Ensembl Mart major version this Database object provides access to.
     *
     * @return the major version
     */
    public int getRelease() {
        return release;
    }

    /**
     * Returns the name of the species this Database object provides access to.
     *
     * @return the species name
     */
    public String getSpeciesName() {
        return speciesName;
    }
}
