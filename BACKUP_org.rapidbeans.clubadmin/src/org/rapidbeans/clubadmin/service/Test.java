/*
 * Rapid Beans Framework: Test.java
 *
 * Copyright Martin Bluemel, 2010
 *
 * 28.02.2010
 */

package org.rapidbeans.clubadmin.service;

import java.io.IOException;
import java.net.MalformedURLException;

import org.rapidbeans.clubadmin.datasource.HttpClientPhp;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.service.Action;

/**
 * The action to restore all application data.
 * 
 * @author Martin Bluemel
 */
public class Test extends Action {

    /**
     * open the training editing view.
     */
    public void execute() {
        final RapidClubAdminClient app = (RapidClubAdminClient) ApplicationManager.getApplication();
        HttpClientPhp httpClient = new HttpClientPhp("http://" + app.getServer() + '/' + app.getRoot() + "/fileio.php",
                "musashi09");
        try {
            httpClient.sendMail("bluemel.martin@gmx.de", "RapidClubAdmin: neue Zugangsdaten",
                    "admin@budo-club-ismaning.de", "Neues Passwort: Musashi09");
        } catch (MalformedURLException e) {
            throw new RapidBeansRuntimeException(e);
        } catch (IOException e) {
            throw new RapidBeansRuntimeException(e);
        }
    }
}
