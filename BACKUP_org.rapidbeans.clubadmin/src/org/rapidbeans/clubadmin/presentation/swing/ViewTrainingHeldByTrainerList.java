/*
 * Rapid Club Admin Application: ViewTrainingHeldByTrainerList.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 09.08.2007
 */
package org.rapidbeans.clubadmin.presentation.swing;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.TrainerRole;
import org.rapidbeans.clubadmin.domain.Training;
import org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer;
import org.rapidbeans.clubadmin.domain.TrainingRegular;
import org.rapidbeans.clubadmin.domain.TrainingSpecial;
import org.rapidbeans.clubadmin.domain.TrainingState;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.datasource.event.AddedEvent;
import org.rapidbeans.datasource.event.ChangedEvent;
import org.rapidbeans.datasource.event.RemovedEvent;
import org.rapidbeans.presentation.ApplicationManager;

/**
 * Presents the list of trainers who held a certain training.
 *
 * @author Martin Bluemel
 */
public class ViewTrainingHeldByTrainerList extends JPanel {

    /**
     * serial.
     */
    private static final long serialVersionUID = 1L;

    /**
     * the training which trainers are presented by this view.
     */
    private Training trainingToPresent = null;

    /**
     * the selected trainer button's presented object.
     */
    private TrainingHeldByTrainer selectedTrainingHeldByTrainer = null;

    /**
     * the editor's popup menu.
     */
    private JPopupMenu popupMenu = new JPopupMenu();

    /**
     * the editor's popup new menu.
     */
    private JMenuItem popupMenuItemNew = new JMenuItem();

    /**
     * getter.
     *
     * @return the selected trainer button's presented object
     */
    public TrainingHeldByTrainer getSelectedTrainingHeldByTrainer() {
        return this.selectedTrainingHeldByTrainer;
    }

//    /**
//     * Collection with the "Traing Held By Trainers" presented
//     */
//    private Collection<TrainingHeldByTrainer> trhbts =
//        new ArrayList<TrainingHeldByTrainer>();

    /**
     * Maps the "Training Held By Trainers" presented to their views
     */
    private Map<TrainingHeldByTrainer, ViewTrainingHeldByTrainer> trhbtViews =
        new HashMap<TrainingHeldByTrainer, ViewTrainingHeldByTrainer>();

    public ViewTrainingHeldByTrainer getTrhbtView(final TrainingHeldByTrainer trhbt) {
        return this.trhbtViews.get(trhbt);
    }

    /**
     * @return the training currently presented
     */
    public Training getTraining() {
        return this.trainingToPresent;
    }

    /**
     * setter to change the training to present.
     *
     * @param training the training to present
     */
    public void setTraining(final Training training) {
        this.trainingToPresent = training;
        this.updateUI();
    }

    /**
     * the view's layout.
     */
    private LayoutManager layout = new FlowLayout();

    private ViewTrainings parentView = null;

    public ViewTrainings getParentView() {
        return this.parentView;
    }

    /**
     * constructor.
     *
     * @param training the training to present
     */
    ViewTrainingHeldByTrainerList(
            final ViewTrainings parentView,
            final Training training) {
        this.parentView = parentView;
        final RapidBeansLocale locale = ApplicationManager.getApplication().getCurrentLocale();
        ((FlowLayout) this.layout).setAlignment(FlowLayout.LEFT);
        this.setMinimumSize(new Dimension(500, 400));
        this.setLayout(this.layout);
        this.setTraining(training);
        this.popupMenuItemNew.setText(
                locale.getStringGui("commongui.text.new"));
        this.popupMenuItemNew.addActionListener(new ActionListener() {
            @SuppressWarnings({"synthetic-access","unqualified-field-access"})
            public void actionPerformed(final ActionEvent e) {
                addNewHeldByTrainer();
            }
        });
        this.popupMenu.addPopupMenuListener(new PopupMenuListener() {
            public void popupMenuCanceled(final PopupMenuEvent e) {
            }
            public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
            }
            public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
                updatePopupMenu();
            }
        });
        this.popupMenu.add(this.popupMenuItemNew);
        // the pop up menu does not work on this JPanel with Sun Java 5.0
        this.setComponentPopupMenu(this.popupMenu);
        this.setVisible(true);
    }

    public TrainingHeldByTrainer addNewHeldByTrainer() {
        popupMenu.setVisible(false);
        TrainingHeldByTrainer newTrhbt = new TrainingHeldByTrainer();
        trainingToPresent.addHeldbytrainer(newTrhbt);
        return newTrhbt;
    }

    /**
     * Select a trhbtView.
     *
     * @param trhbtView the view to select.
     */
    public void selectTrainingHeldByTrainer(final ViewTrainingHeldByTrainer trhbtView) {
        if (this.selectedTrainingHeldByTrainer != null) {
            final ViewTrainingHeldByTrainer trhbtViewSelBefore = this.trhbtViews.get(this.selectedTrainingHeldByTrainer);
            trhbtViewSelBefore.showNormal();
        }
        if (trhbtView == null) {
            this.selectedTrainingHeldByTrainer = null;            
        } else {
            if (!this.trhbtViews.containsValue(trhbtView)) {
                throw new RapidBeansRuntimeException("View not contained");
            }
            trhbtView.showSelected();
            this.selectedTrainingHeldByTrainer = trhbtView.getTrainingHeldByTrainer();
        }
    }

    /**
     * Update the training held by fields.
     */
    public void updateUI() {
        this.removeAll();
        if (this.trainingToPresent != null) {
            switch (this.trainingToPresent.getState()) {
            case asplanned:
            case modified:
            case checked:
                if (this.trainingToPresent != null) {
                    for (TrainingHeldByTrainer trhbt :
                        this.trainingToPresent.getHeldbytrainersSortedByValue()) {
                        final ViewTrainingHeldByTrainer trhbtView = new ViewTrainingHeldByTrainer(trhbt, this);
                        this.trhbtViews.put(trhbt, trhbtView);
                        this.add(trhbtView);
                    }
                }
                break;
            default:
                break;
            }
        }
        super.updateUI();
    }

    /**
     * event handler for bean added event.
     *
     * @param e the added event
     */
    public void beanAdded(final AddedEvent e) {
        if (e.getBean() instanceof TrainingHeldByTrainer) {
            final Training tr = (Training)
                ((TrainingHeldByTrainer) e.getBean()).getParentBean();
            if (tr.equals(this.trainingToPresent)) {
                this.updateUI();
            }
        } else if (e.getBean() instanceof TrainerRole) {
            for (ViewTrainingHeldByTrainer view : this.trhbtViews.values()) {
                view.beanAdded(e);
            }
        }
    }

    /**
     * event handler for bean removed event.
     *
     * @param e the removed event
     */
    public void bizBeanRemoved(final RemovedEvent e) {
        if (e.getBean() instanceof TrainingHeldByTrainer) {
            final TrainingHeldByTrainer trhbtDeleted = (TrainingHeldByTrainer) e.getBean();
            if (this.trhbtViews.containsKey(trhbtDeleted)) {
                this.updateUI();                
            }
        }
    }

    /**
     * event handler for bean changed event.
     *
     * @param e the changed event
     */
    public void bizBeanChanged(final ChangedEvent e) {
        if (e.getBean() instanceof TrainingHeldByTrainer) {
            final Training tr = (Training)
                ((TrainingHeldByTrainer) e.getBean()).getParentBean();
            if (tr.equals(this.trainingToPresent)) {
                this.trhbtViews.get((TrainingHeldByTrainer) e.getBean()).bizBeanChanged(e);
            }
        } else if (e.getBean() instanceof Department
                && this.trainingToPresent != null
                && this.trainingToPresent.getParentBean() != null) {
            Department dep = null;
            if (this.trainingToPresent instanceof TrainingRegular) {
                dep = (Department) this.trainingToPresent.getParentBean().getParentBean();
            } else if (this.trainingToPresent instanceof TrainingSpecial) {
                dep = (Department) this.trainingToPresent.getParentBean();
            }
            if (dep != null && e.getBean() == dep) {
                for (ViewTrainingHeldByTrainer view : this.trhbtViews.values()) {
                    view.bizBeanChanged(e);
                }
            }
        }
    }

    /**
     * update the pop up menu.
     */
    private void updatePopupMenu() {
        if (this.getTraining().getState() != TrainingState.cancelled
                && this.getTraining().getState() != TrainingState.checked
                && this.getTraining().getState() != TrainingState.closed) {
            this.popupMenuItemNew.setEnabled(true);
        } else {
            this.popupMenuItemNew.setEnabled(false);
        }
    }
}
