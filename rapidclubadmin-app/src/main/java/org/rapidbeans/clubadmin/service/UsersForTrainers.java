/*
 * RapidBeans Application RapidClubAdmin: UsersForTrainers.java
 *
 * Copyright Martin Bluemel, 2010
 *
 * 26.02.2010
 */

package org.rapidbeans.clubadmin.service;

import org.rapidbeans.clubadmin.domain.MasterData;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.service.Action;

/**
 * The action to start a new billing period.
 *
 * @author Martin Bluemel
 */
public class UsersForTrainers extends Action {

	public void execute() {
		final RapidClubAdminClient client = (RapidClubAdminClient) ApplicationManager.getApplication();
		final MasterData md = client.getMasterData();
		md.generateUsersForTrainers();
	}
}
