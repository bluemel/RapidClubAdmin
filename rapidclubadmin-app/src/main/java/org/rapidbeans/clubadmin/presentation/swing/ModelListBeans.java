/*
 * Rapid Beans Application RapidClubAdmin: ModelListBeans.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 08.12.2007
 */
package org.rapidbeans.clubadmin.presentation.swing;

import java.util.List;

import javax.swing.AbstractListModel;

import org.rapidbeans.clubadmin.domain.ClubadminUser;
import org.rapidbeans.clubadmin.domain.Role;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.type.TypeRapidBean;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;

/**
 * @author Martin Bluemel
 */
public abstract class ModelListBeans<T extends RapidBean> extends AbstractListModel<RapidBean> {

	/**
	 * for serialization
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the type to present
	 */
	private TypeRapidBean filterType = null;

	/**
	 * @return the type to present
	 */
	protected TypeRapidBean getFilterType() {
		return this.filterType;
	}

	/**
	 * the filter query.
	 */
	private String filter = null;

	/**
	 * setter.
	 *
	 * @param newFilter the new filter value.
	 */
	protected void setFilter(final String newFilter) {
		this.filter = newFilter;
	}

	/**
	 * fill the authorization filter part according to the user and its role.
	 */
	protected abstract void fillAuthorizationFilterPart(ClubadminUser user, Role role, StringBuffer buf);

	/**
	 * @return the authorization part of the filter or null if there is no
	 *         authorization restriction
	 */
	protected String getAuthorizationFilterPart() {
		final StringBuffer buf = new StringBuffer();
		final Application client = ApplicationManager.getApplication();
		final ClubadminUser user = (ClubadminUser) client.getAuthenticatedUser();
		if (user == null) {
			// filter all by filtering for a not existent id
			buf.append("id = '<filter all>'");
		}
		final Role role = user.getRole();
		if (role == null) {
			// filter all by filtering for a not existent id
			buf.append("id = '<filter all>'");
		} else {
			this.fillAuthorizationFilterPart(user, role, buf);
		}
		if (buf.length() == 0) {
			return null;
		} else {
			return buf.toString();
		}
	}

	/**
	 * Reset the filter.
	 */
	public void resetFilter() {
		StringBuffer buf = new StringBuffer();
		buf.append(this.filterType.getName());
		buf.append('[');
		final String authnPart = getAuthorizationFilterPart();
		if (authnPart != null) {
			buf.append(authnPart);
		}
		buf.append(']');
		this.filter = buf.toString();
		this.updateGUI();
	}

	/**
	 * the document to query for trainers.
	 */
	private Document document = null;

	/**
	 * the trainers.
	 */
	private List<RapidBean> beans = null;

	/**
	 * constructor.
	 *
	 * @param doc the document
	 */
	public ModelListBeans(final Document doc, final String filterTypename) {
		this.document = doc;
		this.filterType = TypeRapidBean.forName(filterTypename);
		this.resetFilter();
	}

	/**
	 * @return the row count
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getSize() {
		return this.beans.size();
	}

	/**
	 * returns the trainer at the given row.
	 *
	 * @param rowIndex the row index
	 *
	 * @return the trainer at the given row
	 */
	public RapidBean getElementAt(final int rowIndex) {
		return this.beans.get(rowIndex);
	}

	/**
	 * determine the row of a given training.
	 *
	 * @param dep the Trainer to find
	 *
	 * @return the index of the row or -1 if not found
	 */
	public int findRow(final Object dep) {
		int row = -1;
		int i = 0;
		for (RapidBean bean : this.beans) {
			if (bean.equals(dep)) {
				row = i;
				break;
			}
			i++;
		}
		return row;
	}

	/**
	 * update the model after beans were added or deleted or the filter has been
	 * changed.
	 */
	protected void updateGUI() {
		this.beans = this.document.findBeansByQuery(filter);
		super.fireContentsChanged(this, 0, this.beans.size());
	}

	/**
	 * fire a table rows inserted event.
	 */
	public void fireContentsChanged() {
		super.fireContentsChanged(this, 0, this.beans.size());
	}
}
