/*
 * Rapid Beans Framework: DocumentSaveAs.java
 *
 * Copyright Martin Bluemel, 2010
 *
 * 26.04.2010
 */

package org.rapidbeans.clubadmin.service;

import org.rapidbeans.clubadmin.domain.RapidClubAdminBusinessLogicException;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.exception.ValidationException;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.DocumentController;
import org.rapidbeans.service.Action;

/**
 * The action to save the current document on the local file system.
 *
 * @author Martin Bluemel
 */
public class DocumentSaveAs extends Action {

	/**
	 * default constructor.
	 */
	public DocumentSaveAs() {
		super(DocumentSaveAs.class.getName());
	}

	/**
	 * Save as.
	 */
	public void execute() {
		final Application app = ApplicationManager.getApplication();
		final Document doc = app.getActiveDocument();
		if (doc != null && doc.getChanged()) {
			try {
				for (final RapidBean bean : doc.findBeansByType("org.rapidbeans.clubadmin.domain.Training")) {
					bean.validate();
				}
				DocumentController.saveAs();
			} catch (RapidClubAdminBusinessLogicException e) {
				app.messageError(e.getLocalizedMessage(app.getCurrentLocale()), "Fehler beim Abspeichern");
			} catch (ValidationException e) {
				app.messageError(e.getLocalizedMessage(app.getCurrentLocale()), "Fehler beim Abspeichern");
			}
		}
	}
}
