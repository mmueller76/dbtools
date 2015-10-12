package org.dbtools.ensembl;

import org.junit.Test;

import java.util.Set;

import junit.framework.TestCase;
import org.dbtools.DatabaseException;

/**
 * Created by IntelliJ IDEA.<br>
 * User: mmueller<br>
 * Date: 25-Sep-2007<br>
 * Time: 14:12:43<br>
 */
public class EnsemblDatabaseFactoryTest extends TestCase {


    @Test
    public void testGetCoreDatabaseReleaseVersions() {

        Set<Integer> releases = null;
        try {
            releases = new EnsemblDatabaseFactory().getReleaseVersions(9606, EnsemblDatabaseType.CORE);
        } catch (DatabaseException e) {
            fail(e.toString());
        }

        assertTrue(releases.size() > 0);

        assertTrue(releases.contains(46));

    }

    @Test
    public void testGetNcbiTaxonIds() {

        Set<Integer> ids = null;

        try {

            ids = new EnsemblDatabaseFactory().getNcbiTaxonIds();

        } catch (DatabaseException e) {
            fail(e.toString());
        }

        assertTrue(ids.size() > 0);

        assertTrue(ids.contains(9606));

    }

    @Test
    public void testGetSpeciesNames() {

        Set<String> species = null;
        try {
            species = new EnsemblDatabaseFactory().getSpeciesNames();
        } catch (DatabaseException e) {
            fail(e.toString());
        }

        assertTrue(species.size() > 0);

        assertTrue(species.contains("homo sapiens"));

    }

    public static void main(String args[]) {
        org.junit.runner.JUnitCore.main("org.dbtools.ensembl.EnsemblDatabase");
    }

}
