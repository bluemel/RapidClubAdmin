/*
 * EasyBiz Application RapidClubAdmin: ModelTrainingsTable.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 10.04.2007
 */
package org.rapidbeans.clubadmin.presentation.swing;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.rapidbeans.clubadmin.domain.Training;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.datasource.query.Query;


/**
 * @author Martin Bluemel
 */
public class ModelTrainingsTable extends AbstractTableModel {

    /**
     * serial id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * the column headers.
     */
    private String[] columnNames = {"Tag", "Datum", "Beginn", "Training", "Zustand"};

    /**
     * the document to query for trainings.
     */
    private Document document = null;

    /**
     * retrieve a certain column name.
     *
     * @param col the collection to render
     *
     * @return the rendered "Wochentag"
     */
    public String getColumnName(final int col) {
        return this.columnNames[col];
    }

    /**
     * the trainings.
     */
    private List<RapidBean> trainings = null;

    /**
     * constructor.
     *
     * @param doc the document
     */
    public ModelTrainingsTable(final Document doc) {
        this.document = doc;
        updateTrainings();
    }

    /**
     * @return the column count
     *
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return 5;
    }

    /**
     * @return the row count
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return this.trainings.size();
    }

    /**
     * @param rowIndex the row index
     * @param columnIndex the column index
     * @return the cell value
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        Object ret = null;
        final Training tr = (Training) this.trainings.get(rowIndex);
        switch (columnIndex) {
        case 0:
            ret = tr.getDayofweek();
            break;
        case 1:
            ret = tr.getDate();
            break;
        case 2:
            ret = tr.getTimestart();
            break;
        case 3:
            ret = tr.getName();
            break;
        case 4:
            ret = tr.getState();
            break;
        default:
            throw new RapidBeansRuntimeException("undefinded column " + columnIndex);
        }
        return ret;
    }

    /**
     * returns the training at the given row.
     *
     * @param rowIndex the row index
     * @return the training at the given row
     */
    public Training getTrainingAt(final int rowIndex) {
        return (Training) this.trainings.get(rowIndex);
    }

    /**
     * determine the row of a given training.
     *
     * @param tr the Training to find
     * @return the index of the row or -1 if not found
     */
    public int findRow(final Training tr) {
        int row = -1;
        int i = 0;
        for (RapidBean bean : this.trainings) {
            if (bean == tr) {
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
        this.updateTrainings();
        super.fireTableRowsInserted(0, this.trainings.size());
    }

    /**
     * fire a table rows deleted event.
     *
     * @param firstRow index of first row deleted.
     * @param lastRow index of last row deleted.
     */
    public void fireTableRowsDeleted(final int firstRow, final int lastRow) {
        this.updateTrainings();
        super.fireTableRowsDeleted(firstRow, lastRow);
    }

    /**
     * update the trainings after Trainings were added or deleted or the
     * filter has been changed.
     */
    private void updateTrainings() {
        final Query query = new Query(
                "org.rapidbeans.clubadmin.domain.Training >date >timestart");
        this.trainings = this.document.findBeansByQuery(query);
    }
}
