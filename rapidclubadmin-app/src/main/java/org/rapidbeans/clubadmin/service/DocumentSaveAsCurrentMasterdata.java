/*
 * Rapid Beans Framework: DocumentSaveCurrent.java
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
 * The action to save current document as the file defined in the program
 * settings.<br/>
 * Web (central) or local (decentral).<br/>
 * Billing Period or Masterdata.<br/>
 * This action potentially overwrites data.
 *
 * @author Martin Bluemel
 */
public class DocumentSaveAsCurrentMasterdata extends Action {

	/**
	 * Drives the action to save the masterdata current document to local file or
	 * central web store.
	 *
	 * This action potentially overwrites data
	 */
	public void execute() {
		final RapidClubAdminClient client = (RapidClubAdminClient) ApplicationManager.getApplication();
		client.saveAsCurrent("masterdata");
	}
}
