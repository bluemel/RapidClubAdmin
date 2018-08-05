/*
 * Rapid Beans Framework: DocumentSaveCurrentTrainingsList.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 01.09.2007
 */

package org.rapidbeans.clubadmin.service;

import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.service.Action;


/**
 * The action to save current Billing Period document as the file
 * defined in the program settings
 * web (central) or local (decentral).<br/>
 *
 * This action potentially overwrites data.
 *
 * @author Martin Bluemel
 */
public class DocumentSaveAsCurrentTrainingsList extends Action {

    /**
     * Drives the action to save the active document as the
     * current billing period.
     *
     * This action potentially overwrites data.
     */
    public void execute() {
        final RapidClubAdminClient client = (RapidClubAdminClient) ApplicationManager.getApplication();
        client.saveAsCurrent("trainingslist");
    }
}
