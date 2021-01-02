/*
 * Rapid Club Admin Application: ControllerViewTainingsKeys.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 08.08.2007
 */
package org.rapidbeans.clubadmin.presentation.swing;

import java.awt.event.KeyEvent;

import org.rapidbeans.clubadmin.domain.RapidClubAdminBusinessLogicException;
import org.rapidbeans.clubadmin.domain.Training;
import org.rapidbeans.clubadmin.domain.TrainingState;
import org.rapidbeans.service.ActionDocumentSave;

public class ControllerViewTainingsKeys {

	ViewTrainings view = null;

	/**
	 * constructor.
	 *
	 * @param parentView the parent trainings view
	 */
	public ControllerViewTainingsKeys(final ViewTrainings parentView) {
		this.view = parentView;
	}

	/**
	 * key typed up event handler.
	 *
	 * @param e the key event
	 */
	public void typedUp(final KeyEvent e) {
		// move the cursor in case the the down key has not been
		// type while the focus was on the trainings table
		// (in this case it is done anyway).
		if (this.view.getTrainingsTable().getSelectedRows().length == 1) {
			final int iRowBefore = this.view.getTrainingsTable().getSelectedRow();
			int iRowAfter = iRowBefore;
			if (e.getComponent() != this.view.getTrainingsTable()) {
				iRowAfter -= 1;
			}
			if (iRowAfter >= 0) {
				this.view.getTrainingsTable().changeSelection(iRowAfter, -1, false, false);
			}
		}
	}

	/**
	 * key typed down event handler.
	 *
	 * @param e the key event
	 */
	public void typedDown(final KeyEvent e) {
		// move the cursor in case the the down key has not been
		// type while the focus was on the trainings table
		// (in this case it is done anyway).
		if (this.view.getTrainingsTable().getSelectedRows().length == 1) {
			final int iRowBefore = this.view.getTrainingsTable().getSelectedRow();
			int iRowAfter = iRowBefore;
			if (e.getComponent() != this.view.getTrainingsTable()) {
				iRowAfter += 1;
			}
			if (iRowAfter < (this.view.getTrainingsTable().getRowCount() - 1)) {
				this.view.getTrainingsTable().changeSelection(iRowAfter, -1, false, false);
			}
		}
	}

	/**
	 * key typed left event handler.
	 *
	 * @param e the key event
	 */
	public void typedLeft(final KeyEvent e) {
	}

	/**
	 * key typed right event handler.
	 *
	 * @param e the key event
	 */
	public void typedRight(final KeyEvent e) {
	}

	/**
	 * key typed "S" event handler.
	 *
	 * @param e the key event
	 */
	public void typedS(final KeyEvent e) {
		if (e.getModifiersEx() == 2) {
			(new ActionDocumentSave()).execute();
		}
	}

	/**
	 * key typed enter event handler.
	 *
	 * @param e the key event
	 */
	public void typedEnter(final KeyEvent e) {
		// move the cursor in case the the enter key has not been
		// typed while the focus was on the trainings table
		// (in this case it is done anyway).
		if (this.view.getTrainingsTable().getSelectedRows().length == 1) {
			final int iRowBefore = this.view.getTrainingsTable().getSelectedRow();
			int iRowAfter = iRowBefore;
			int iRowBeforePred = iRowBefore - 1;
			if (iRowBeforePred < 0) {
				iRowBeforePred = this.view.getTrainingsTable().getModel().getRowCount() - 1;
			}
			TrainingState state = this.view.getTrainingsModel().getTrainingAt(iRowBeforePred).getState();
			switch (state) {
			case asplanned:
			case modified:
				final Training training = this.view.getTrainingsModel().getTrainingAt(iRowBeforePred);
				if (training.checkFutureChangeOk()) {
					training.setState(TrainingState.checked);
				}
				break;
			case cancelled:
			case checked:
			case closed:
				break;
			default:
				throw new RapidClubAdminBusinessLogicException("a.b.c", "illegal state " + state.toString());
			}
			if (e.getComponent() != this.view.getTrainingsTable()) {
				iRowAfter += 1;
			}
			if (iRowAfter < (this.view.getTrainingsTable().getRowCount() - 1)) {
				this.view.getTrainingsTable().changeSelection(iRowAfter, -1, false, false);
			}
		}
	}
}
