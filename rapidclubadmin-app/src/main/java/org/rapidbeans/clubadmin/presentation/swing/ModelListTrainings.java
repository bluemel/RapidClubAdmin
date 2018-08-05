/*
 * Rapid Beans Application RapidClubAdmin: ModelListTrainings.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 30.03.2007
 */
package org.rapidbeans.clubadmin.presentation.swing;

import java.util.List;

import javax.swing.DefaultListModel;

import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.datasource.Document;

/**
 * The combo box model for bean collections.
 *
 * @author Martin Bluemel
 */
public final class ModelListTrainings extends DefaultListModel {

	/**
	 * serialization.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the trainings.
	 */
	private List<RapidBean> trainings = null;

	/**
	 * constructor.
	 *
	 * @param doc the document
	 */
	public ModelListTrainings(final Document doc) {
		this.trainings = doc.findBeansByQuery("org.rapidbeans.clubadmin.domain.Training");
	}

	/**
	 * @return the number of enum elements
	 */
	public int getSize() {
		return this.trainings.size();
	}

	/**
	 * @param index the index
	 * @return the enum of index
	 */
	public Object getElementAt(final int index) {
		return this.trainings.get(index);
	}
}
