/*
 * Rapid Beans Clubadmin Application: HttpStoreTest.java
 *
 * Copyright Martin Bluemel, 2009
 *
 * Jun 7, 2008
 */
package org.rapidbeans.clubadmin.datasource;

import java.io.IOException;

import junit.framework.TestCase;

/**
 * Test for the {@link HttpClientPhp} and {@link WebFileManager} classes.
 * 
 * @author Martin Bluemel
 */
public class WebFileManagerTest extends TestCase {

    /** The HTTP store used. */
    private HttpClientPhp httpClientFileio = new HttpClientPhp(
            "http://trainer.budo-club-ismaning.de/rapidclubadminTest/fileio.php",
            "musashi09");

    /** Tests simple and read. */
    public void testHttpUploadBCI() throws IOException {
        httpClientFileio.write("dummy.txt", "BCI");
        assertEquals("BCI", httpClientFileio.read("dummy.txt").trim());
        httpClientFileio.delete("dummy.txt");
    }

    /** Test preservation of line feeds. */
    public void testHttpUploadBCIWithLinefeed() throws IOException {
        httpClientFileio.write("dummy.txt", "BCI\nBCI");
        assertEquals("BCI\nBCI", httpClientFileio.read("dummy.txt").trim());
        httpClientFileio.delete("dummy.txt");
    }

    /** Test preservation of umlauts. */
    public void testHttpUploadBCIWithUmlauts() throws IOException {
        httpClientFileio.write("dummy.txt", "äöüÄÖÜß!?");
        assertEquals("äöüÄÖÜß!?", httpClientFileio.read("dummy.txt").trim());
        httpClientFileio.delete("dummy.txt");
    }

    /** Test preservation of quotes. */
    public void testHttpUploadBCIWithQuotes() throws IOException {
        httpClientFileio.write("dummy.txt", "\"test\"");
        assertEquals("\"test\"", httpClientFileio.read("dummy.txt").trim());
        httpClientFileio.delete("dummy.txt");
    }

    /** Test checking existence of a remote file */
    public void testExistsFileNormal() throws IOException {
        assertTrue(httpClientFileio.exists("masterdata.xml"));
    }

    /** Test checking existence of a remote file */
    public void testExistsFileDir() throws IOException {
        assertTrue(httpClientFileio.exists("current/Aikido"));
    }

    /** Test checking existence of a non existent remote file */
    public void testExistsFileNon() throws IOException {
        assertFalse(httpClientFileio.exists("fileio.php"));
    }

    /**
     * Test creating a simple remote directory with
     * existing parent directory.
     */
    public void testMkdirsSimple() throws IOException {
        assertFalse(httpClientFileio.exists("testdir"));
        httpClientFileio.mkdirs("testdir");
        assertTrue(httpClientFileio.exists("testdir"));
        httpClientFileio.rmdir("testdir");
    }

// TODO implement
//    /**
//     * Test creating a simple remote directory with
//     * existing parent directory.
//     */
//    public void testMkdirsDeep() throws IOException {
//        assertFalse(httpClientFileio.exists("testdir"));
//        httpClientFileio.mkdirs("testdir/test1");
//        assertTrue(httpClientFileio.exists("testdir/test1"));
//        httpClientFileio.rmdir("testdir");
//    }

    /** test listing a directory's content */
    public void testList() throws IOException {
        String[] list = httpClientFileio.list("data/history");
        assertTrue(list.length > 0);
    }

//    public void testEncrypt() {
//        System.out.println(CryptoHelper.encrypt("<text to encrypt>", RapidClubAdminClient.HEIDI));
//    }
}
