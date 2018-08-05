/*
 * Rapid Club Admin Application: RendererTrainings.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 04.04.2007
 */

package org.rapidbeans.clubadmin.presentation.swing;

import java.awt.Component;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellRenderer;

import org.rapidbeans.clubadmin.RapidClubAdmin;
import org.rapidbeans.clubadmin.domain.TrainingState;
import org.rapidbeans.core.basic.PropertyDate;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.domain.math.DayOfWeek;
import org.rapidbeans.domain.math.TimeOfDay;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.swing.MainWindowSwing;

/**
 * @author Martin Bluemel
 */
public class RendererTrainings implements TableCellRenderer {

	/**
	 * Symbol for: training has been held by default trainer(s).
	 */
	private static final ImageIcon ICON_0_TRAINING_DEFAULT = new ImageIcon(
			RapidClubAdmin.class.getResource("presentation/pictures/training0Default.png"));

	/**
	 * Symbol for: training has been modified (is not default).
	 */
	private static final ImageIcon ICON_1_TRAINING_MODIFIED = new ImageIcon(
			RapidClubAdmin.class.getResource("presentation/pictures/training1InWork.png"));

	/**
	 * Symbol for: training has been checked (is O.K.).
	 */
	private static final ImageIcon ICON_2_TRAINING_CHECKED = new ImageIcon(
			RapidClubAdmin.class.getResource("presentation/pictures/training2Checked.png"));

	/**
	 * Symbol for: training has been cancelled.
	 */
	private static final ImageIcon ICON_3_TRAINING_CANCELLED = new ImageIcon(
			RapidClubAdmin.class.getResource("presentation/pictures/training3Cancelled.png"));

	/**
	 * Symbol for: the training has been not scheduled because the location was
	 * closed.
	 */
	private static final ImageIcon ICON_4_TRAINING_CLOSED = new ImageIcon(
			RapidClubAdmin.class.getResource("presentation/pictures/training4Closed.png"));

	/**
	 * @param table      the table
	 * @param value      the value
	 * @param isSelected if currently selected
	 * @param hasFocus   if the cell has a focus
	 * @param row        the row
	 * @param column     the column
	 * @return the component used for rendering a day of week cell
	 */
	public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected,
			final boolean hasFocus, final int row, final int column) {
		Component comp = null;
		final RapidBeansLocale locale = ApplicationManager.getApplication().getCurrentLocale();
		switch (column) {
		case 0:
			final DayOfWeek dow = (DayOfWeek) value;
			comp = new JTextField(dow.toStringGuiShort(locale));
			break;
		case 1:
			final Date dt = (Date) value;
			comp = new JTextField(PropertyDate.formatDate(dt, locale));
			break;
		case 2:
			final TimeOfDay timestart = (TimeOfDay) value;
			comp = new JTextField(timestart.toString());
			break;
		case 4:
			JButton button = new JButton();
			final TrainingState state = (TrainingState) value;
			ImageIcon icon;
			switch (state) {
			case asplanned:
				icon = ICON_0_TRAINING_DEFAULT;
				break;
			case modified:
				icon = ICON_1_TRAINING_MODIFIED;
				break;
			case checked:
				icon = ICON_2_TRAINING_CHECKED;
				break;
			case cancelled:
				icon = ICON_3_TRAINING_CANCELLED;
				break;
			case closed:
				icon = ICON_4_TRAINING_CLOSED;
				break;
			default:
				throw new RapidBeansRuntimeException("Unexpected TrainigState" + ", order = " + state.ordinal());
			}
			button.setIcon(icon);
			comp = button;
			break;
		default:
			final String str = value.toString();
			comp = new JTextField(str);
			break;
		}
		if (isSelected && column != 4) {
			comp.setBackground(MainWindowSwing.COLOR_SELECTED_BACKGROUND);
		}
		return comp;
	}
}
