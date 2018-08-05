/*
 * Rapid Beans Framework: EnablerDocumentSaveAsCurrentTrainingsList.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * Sep 15, 2007
 */
package org.rapidbeans.clubadmin.presentation.enabler;

import org.rapidbeans.datasource.Document;
import org.rapidbeans.presentation.enabler.Enabler;

/**
 * enable / disable the document save as / current menu item.
 *
 * @author Martin Bluemel
 */
public class EnablerDocumentSaveAsCurrentTrainingsList extends Enabler {

	/**
	 * the enabling method of every enabler.
	 *
	 * @return if the menu is enable or not.
	 */
	public boolean getEnabled() {
		Document doc = this.getClient().getActiveDocument();
		if (doc == null) {
			return false;
		} else if (doc.getUrl() != null && doc.getUrl().getProtocol().equals("http")) {
			return false;
		} else if (!(doc.getName().startsWith("trainingslist"))) {
			return false;
		}
		return true;
	}
}
