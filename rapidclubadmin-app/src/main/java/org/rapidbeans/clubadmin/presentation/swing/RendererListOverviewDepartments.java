/*
 * Rapid Beans Application RapidClubAdmin: RendererListOverviewDepartments.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 30.03.2007
 */
package org.rapidbeans.clubadmin.presentation.swing;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.presentation.swing.RendererHelper;

/**
 * @author Martin Bluemel
 */
public class RendererListOverviewDepartments<T extends Department> implements ListCellRenderer<Department> {

	private ViewOverview overview = null;

	public RendererListOverviewDepartments(final ViewOverview view) {
		this.overview = view;
	}

	public Component getListCellRendererComponent(final JList<? extends Department> list, final Department department, final int index,
			final boolean isSelected, final boolean cellHasFocus) {
		final StringBuffer buf = new StringBuffer();
		if (overview.getSelectedClubs().size() == 1) {
			buf.append(department.getName());
		} else {
			buf.append(department.getIdString());
		}
		return RendererHelper.createLabel(buf.toString(), isSelected);
	}
}
