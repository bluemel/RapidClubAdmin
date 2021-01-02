/*
 * Rapid Club Admin Application: DocumentViewBillingPeriod.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 08.08.2007
 */
package org.rapidbeans.clubadmin.presentation.swing;

import javax.swing.JInternalFrame;

import org.rapidbeans.datasource.Document;
import org.rapidbeans.datasource.Filter;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.swing.DocumentViewSwing;

/**
 * the bean editor GUI.
 *
 * @author Martin Bluemel
 */
public class DocumentViewBillingPeriod extends DocumentViewSwing {

	/**
	 * constructor.
	 *
	 * @param client       the client
	 * @param doc          the document to show
	 * @param docconfname  the view's document configuration name
	 * @param viewconfname the view's configuration name
	 * @param filter       the filter
	 */
	public DocumentViewBillingPeriod(final Application client, final Document doc, final String docconfname,
			final String viewconfname, final Filter filter) {
		super(client, doc, docconfname, viewconfname, filter);
		((JInternalFrame) this.getWidget()).setTitle(ApplicationManager.getApplication().getCurrentLocale()
				.getStringGui("view.masterdata.billingperiod.title"));
	}
}
