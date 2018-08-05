/*
 * Rapid Beans Framework: EnablerEditTrainings.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * Dec 27, 2006
 */
package org.rapidbeans.clubadmin.presentation.enabler;

import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.DocumentView;

/**
 * enables / disable the view trainings menu item.
 *
 * @author Martin Bluemel
 */
public class EnablerViewTrainings extends EnablerView {

	/**
	 * the execute method of every Action.
	 *
	 * @return if the menu is enable or not.
	 */
	public boolean getEnabled() {
		if (!super.getEnabled()) {
			return false;
		}
		final Application client = ApplicationManager.getApplication();
		DocumentView activeView = client.getMainwindow().getActiveDocumentView();
		if (activeView.getName().endsWith("trainings")) {
			return false;
		}
		return true;
	}
}
