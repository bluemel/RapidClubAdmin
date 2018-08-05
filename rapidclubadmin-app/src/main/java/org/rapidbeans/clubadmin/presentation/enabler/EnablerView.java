/*
 * Rapid Beans Framework: EnablerViewExpert.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 16.08.2007
 */
package org.rapidbeans.clubadmin.presentation.enabler;

import org.rapidbeans.clubadmin.domain.TrainingsList;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.DocumentView;
import org.rapidbeans.presentation.enabler.Enabler;

/**
 * enables / disable the view expert menu item.
 *
 * @author Martin Bluemel
 */
public abstract class EnablerView extends Enabler {

	/**
	 * the enabling method of every enabler.
	 *
	 * @return if the menu is enabled or not.
	 */
	public boolean getEnabled() {
		final Application client = ApplicationManager.getApplication();
		final Document activeDocument = client.getMainwindow().getActiveDocument();
		if (activeDocument == null) {
			return false;
		}
		if (!(activeDocument.getRoot() instanceof TrainingsList)) {
			return false;
		}
		final DocumentView activeView = client.getMainwindow().getActiveDocumentView();
		if (activeView == null) {
			return false;
		}
		return true;
	}
}
