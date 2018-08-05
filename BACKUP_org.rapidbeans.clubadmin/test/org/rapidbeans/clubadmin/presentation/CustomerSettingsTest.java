/*
 * RapidClubAdmin: CustomerSettingsTest.java
*
* Copyright Martin Bluemel, 2008
*
* June 8, 2008
*/
package org.rapidbeans.clubadmin.presentation;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.TestCase;

import org.rapidbeans.datasource.Document;
import org.rapidbeans.security.CryptoHelper;

/**
 * Unit tests for class Settings.
 *
 * @author Martin Bluemel
 */
public final class CustomerSettingsTest extends TestCase {

    /**
     * Test method for constructor Settings().
     * @throws MalformedURLException in case a invalid URL
     * @throws URISyntaxException in case of wrong URI syntax
     */
    public void testWriteSettings() throws MalformedURLException, URISyntaxException {
        CustomerSettings settings = new CustomerSettings();
        settings.setFtpuser(CryptoHelper.encrypt("U170101", RapidClubAdminClient.HEIDI));
        settings.setFtppwd(CryptoHelper.encrypt("1106829", RapidClubAdminClient.HEIDI));
        settings.setRootAppFtp("rapidclubadmin");
        Document doc = new Document(settings);
        doc.setUrl(new File("testdata/customersettings.xml").toURI().toURL());
        doc.save();
        new File(new URI(doc.getUrl().toString())).delete();
    }

    public void testReadSettings() {
        Document doc = new Document(new File("testdata/testcustomersettings.xml"));
        CustomerSettings settings = (CustomerSettings) doc.getRoot();
        assertEquals("bluemel", CryptoHelper.decrypt(settings.getFtpuser(), RapidClubAdminClient.HEIDI));
    }
}
