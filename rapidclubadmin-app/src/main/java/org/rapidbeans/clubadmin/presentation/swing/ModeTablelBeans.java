/*
 * EasyBiz Application RapidClubAdmin: ModelDepartmentsTable.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 22.08.2007
 */
package org.rapidbeans.clubadmin.presentation.swing;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.datasource.Document;

/**
 * @author Martin Bluemel
 */
public class ModeTablelBeans<T> extends AbstractTableModel {

	/**
	 * serial.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the type name.
	 */
	private String typename = null;

	/**
	 * the column headers.
	 */
	private String[] columnNames = null;

	/**
	 * the document to query for trainers.
	 */
	private Document document = null;

	/**
	 * retrieve a certain column name.
	 *
	 * @param col the collection to render
	 *
	 * @return the column name
	 */
	public String getColumnName(final int col) {
		return this.columnNames[col];
	}

	/**
	 * the trainers.
	 */
	private List<RapidBean> beans = null;

	/**
	 * constructor.
	 *
	 * @param doc the document
	 */
	public ModeTablelBeans(final Document doc, final String typenam, final String[] colNames) {
		this.typename = typenam;
		this.document = doc;
		this.columnNames = colNames;
		updateGUI();
	}

	/**
	 * @return the column count
	 *
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return this.columnNames.length;
	}

	/**
	 * @return the row count
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return this.beans.size();
	}

	/**
	 * @param rowIndex    the row index
	 * @param columnIndex the column index
	 * @return the cell value
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(final int rowIndex, final int columnIndex) {
		Object ret = null;
		final RapidBean bean = this.beans.get(rowIndex);
		if (bean instanceof Trainer) {
			Trainer tr = (Trainer) bean;
			switch (columnIndex) {
			case 0:
				ret = tr.getLastname();
				break;
			case 1:
				ret = tr.getFirstname();
				break;
			default:
				throw new RapidBeansRuntimeException(
						"Error in getValueAt(" + rowIndex + ", " + columnIndex + "): undefinded column " + columnIndex
								+ "\n" + "Defined colums" + this.columnNames.toString());
			}
		} else if (bean instanceof Department) {
			switch (columnIndex) {
			case 0:
				ret = bean.getIdString();
//                ret = ((Department) bean).getName();
				break;
			default:
				throw new RapidBeansRuntimeException(
						"Error in getValueAt(" + rowIndex + ", " + columnIndex + "): undefinded column " + columnIndex
								+ "\n" + "Defined colums" + this.columnNames.toString());
			}
		} else {
			switch (columnIndex) {
			case 0:
				ret = bean.getIdString();
				break;
			default:
				throw new RapidBeansRuntimeException(
						"Error in getValueAt(" + rowIndex + ", " + columnIndex + "): undefinded column " + columnIndex
								+ "\n" + "Defined colums" + this.columnNames.toString());
			}
		}
		return ret;
	}

	/**
	 * returns the trainer at the given row.
	 *
	 * @param rowIndex the row index
	 *
	 * @return the trainer at the given row
	 */
	@SuppressWarnings("unchecked")
	public T getBeanAt(final int rowIndex) {
		return (T) this.beans.get(rowIndex);
	}

	/**
	 * determine the row of a given training.
	 *
	 * @param dep the Trainer to find
	 * @return the index of the row or -1 if not found
	 */
	public int findRow(final T dep) {
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
	 * fire a table rows inserted event.
	 */
	public void fireTableRowInserted() {
		this.updateGUI();
		super.fireTableRowsInserted(0, this.beans.size());
	}

	/**
	 * fire a table rows deleted event.
	 *
	 * @param firstRow index of first row deleted.
	 * @param lastRow  index of last row deleted.
	 */
	public void fireTableRowsDeleted(final int firstRow, final int lastRow) {
		this.updateGUI();
		super.fireTableRowsDeleted(firstRow, lastRow);
	}

	/**
	 * update the trainers after Trainers were added or deleted or the filter has
	 * been changed.
	 */
	private void updateGUI() {
		this.beans = this.document.findBeansByType(this.typename);
	}
}
