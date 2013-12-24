/*
 * Rapid Beans Framework: EnablerUsersForTrainers.java
 *
 * Copyright Martin Bluemel, 2010
 *
 * Feb 26, 2010
 */
package org.rapidbeans.clubadmin.presentation.enabler;

import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.presentation.ApplicationManager;


public class EnablerUsersForTrainers extends EnablerView {

    /**
     * the execute method of every Action.
     *
     * @return if the menu is enable or not.
     */
    public boolean getEnabled() {
        final RapidClubAdminClient app = (RapidClubAdminClient) ApplicationManager.getApplication();
        for (final Trainer trainer : app.getMasterData().getTrainers()) {
            if (trainer.getUser() == null) {
                return true;
            }
        }
        return false;
    }
}
