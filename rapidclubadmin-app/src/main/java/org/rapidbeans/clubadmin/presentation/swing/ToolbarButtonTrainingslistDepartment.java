/*
 * Rapid Beans Framework: ToolbarButtonSwing.java
 *
 * Copyright (C) 2010 Martin Bluemel
 *
 * Creation Date: 02/12/2010
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 * You should have received a copies of the GNU Lesser General Public License and the
 * GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package org.rapidbeans.clubadmin.presentation.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.MainWindow;
import org.rapidbeans.presentation.config.ConfigToolbarButton;
import org.rapidbeans.presentation.swing.ToolbarButtonSwing;

/**
 * A ToolbarButton encapsulating a Swing JButton.
 *
 * @author Martin Bluemel
 */
public class ToolbarButtonTrainingslistDepartment extends ToolbarButtonSwing {

	/**
	 * constructor.
	 *
	 * @param department     the department
	 * @param client         the client
	 * @param mainWindow     the main window
	 * @param menuItemConfig the menu item configuration
	 * @param resourcePath   the resource path
	 */
	public ToolbarButtonTrainingslistDepartment(final Department department, final ConfigToolbarButton cfg,
			final Application client, final MainWindow mainWindow, final String resourcePath) {
		super(cfg, client, mainWindow, resourcePath);
		final RapidClubAdminClient rclient = (RapidClubAdminClient) client;
		final Trainer departmentIconDummyTrainer = TrainerIconManager.createDepartmentIconDummyTrainer(department);
		final List<Trainer> trainers = new ArrayList<Trainer>();
		trainers.add(departmentIconDummyTrainer);
		rclient.getTrainerIcons().updateIcons(trainers, false);
		final ImageIcon icon = rclient.getTrainerIcons().get(departmentIconDummyTrainer);
		if (icon != null && icon != TrainerIconManager.getDefaultIcon()) {
			this.getButton().setIcon(icon);
			this.getButton().setText(null);
		}
		this.getButton().setToolTipText("Aktuelle Trainingsliste \"" + department.getIdString() + "\" �ffnen");
	}
}
