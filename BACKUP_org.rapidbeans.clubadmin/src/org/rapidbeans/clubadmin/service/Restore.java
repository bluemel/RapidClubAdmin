/*
 * Rapid Beans Framework: Restore.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 11.09.2008
 */

package org.rapidbeans.clubadmin.service;

import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.service.Action;


/**
 * The action to restore all application data.
 *
 * @author Martin Bluemel
 */
public class Restore extends Action {

    /**
     * open the training editing view.
     */
    public void execute() {
        final RapidClubAdminClient app = (RapidClubAdminClient) ApplicationManager.getApplication();
        app.messageInfo("No yet implemented!!!");
    }
}
