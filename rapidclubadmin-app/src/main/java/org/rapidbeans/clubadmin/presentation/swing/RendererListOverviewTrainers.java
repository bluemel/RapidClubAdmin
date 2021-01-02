/*
 * Rapid Beans Application RapidClubAdmin: RendererListOverviewTrainers.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 30.03.2007
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
public class RendererListOverviewTrainers<T extends Trainer> implements ListCellRenderer<Trainer> {

	@Override
	public Component getListCellRendererComponent(JList<? extends Trainer> list, Trainer trainer, int index,
			boolean isSelected, boolean cellHasFocus) {
		final StringBuffer buf = new StringBuffer(trainer.getLastname());
		buf.append(", ");
		buf.append(trainer.getFirstname());
		if (trainer.getMiddlename() != null) {
			buf.append(trainer.getMiddlename());
		}
		return RendererHelper.createLabel(buf.toString(), isSelected);
	}
}
