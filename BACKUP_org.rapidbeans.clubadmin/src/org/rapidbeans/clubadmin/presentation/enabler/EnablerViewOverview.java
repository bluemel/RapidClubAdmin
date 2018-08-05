/*
 * Rapid Beans Framework: EnablerEditOverview.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * August 16, 2007
 */
package org.rapidbeans.clubadmin.presentation.enabler;

import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.DocumentView;


/**
 * enables / disable the view overview items.
 *
 * @author Martin Bluemel
 */
public class EnablerViewOverview extends EnablerView {

    /**
     * the enabling method of every enabler.
     *
     * @return if the menu is enable or not.
     */
    public boolean getEnabled() {
        if (!super.getEnabled()) {
            return false;
        }
        final Application client =  ApplicationManager.getApplication();
        DocumentView activeView =
            client.getMainwindow().getActiveDocumentView();
        if (activeView.getName().endsWith("overview")) {
            return false;
        }
        return true;
    }
}
