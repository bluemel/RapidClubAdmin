/*
 * Rapid Beans Framework: ViewTrainings.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 16.08.2007
 */

package org.rapidbeans.clubadmin.service;

import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.DocumentView;
import org.rapidbeans.service.Action;

/**
 * The action switch to the trainings view.
 *
 * @author Martin Bluemel
 */
public class ViewOverviewAction extends Action {

	/**
	 * open the expert view.
	 */
	public void execute() {
		final Application client = ApplicationManager.getApplication();
		DocumentView activeView = client.getMainwindow().getActiveDocumentView();
		if (activeView == null) {
			client.messageInfo("no document view active");
			return;
		}
		final DocumentView newView = client.openDocumentView(activeView.getDocument(), "trainingslist", "overview");
		if (activeView != null && newView != activeView) {
			activeView.close();
		}
	}
}
