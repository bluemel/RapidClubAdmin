/*
 * EasyBiz Application RapidClubAdmin: RendererListTrainers.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 10.12.2007
 */
package org.rapidbeans.clubadmin.presentation.swing;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.presentation.swing.RendererHelper;

/**
 * @author Martin Bluemel
 */
public class RendererListOverviewTrainers implements ListCellRenderer {

	public Component getListCellRendererComponent(final JList list, final Object value, final int index,
			final boolean isSelected, final boolean cellHasFocus) {
		final Trainer trainer = (Trainer) value;
		final StringBuffer buf = new StringBuffer(trainer.getLastname());
		buf.append(", ");
		buf.append(trainer.getFirstname());
		if (trainer.getMiddlename() != null) {
			buf.append(trainer.getMiddlename());
		}
		return RendererHelper.createLabel(buf.toString(), isSelected);
	}
}
