/*
 * Rapid Beans Framework: EnablerViewExpert.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 16.08.2007
 */
package org.rapidbeans.clubadmin.presentation.enabler;

import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.DocumentView;

/**
 * enables / disable the view expert menu item.
 *
 * @author Martin Bluemel
 */
public class EnablerViewExpert extends EnablerView {

	/**
	 * the enabling method of every enabler.
	 *
	 * @return if the menu is enabled or not.
	 */
	public boolean getEnabled() {
		if (!super.getEnabled()) {
			return false;
		}
		final Application client = ApplicationManager.getApplication();
		DocumentView activeView = client.getMainwindow().getActiveDocumentView();
		if (activeView.getName().endsWith("expert")) {
			return false;
		}
		return true;
	}
}
