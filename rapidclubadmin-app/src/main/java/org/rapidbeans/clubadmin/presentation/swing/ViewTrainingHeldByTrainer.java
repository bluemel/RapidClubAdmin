/*
 * Rapid Club Admin Application: ViewTrainingHeldByTrainer.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 09.08.2007
 */
package org.rapidbeans.clubadmin.presentation.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.domain.TrainerRole;
import org.rapidbeans.clubadmin.domain.Training;
import org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer;
import org.rapidbeans.clubadmin.domain.TrainingRegular;
import org.rapidbeans.clubadmin.domain.TrainingState;
import org.rapidbeans.clubadmin.domain.TrainingsList;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.event.PropertyChangeEvent;
import org.rapidbeans.core.exception.ValidationException;
import org.rapidbeans.core.type.TypePropertyCollection;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.datasource.event.AddedEvent;
import org.rapidbeans.datasource.event.ChangedEvent;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.swing.EditorPropertySwing;
import org.rapidbeans.presentation.swing.MainWindowSwing;
import org.rapidbeans.presentation.swing.ModelComboBoxCollection;
import org.rapidbeans.presentation.swing.RendererListCollection;

/**
 * Presents a single TrainingHeldByTrainer object which is a trainer that took
 * part on a certain training in a special role.
 *
 * @author Martin Bluemel
 */
public class ViewTrainingHeldByTrainer extends JPanel {

	/**
	 * serial.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * the object presented.
	 */
	private TrainingHeldByTrainer trainingHeldByTrainer = null;

	/**
	 * getter.
	 *
	 * @return the object presented
	 */
	public TrainingHeldByTrainer getTrainingHeldByTrainer() {
		return trainingHeldByTrainer;
	}

	/**
	 * the parent trainers view.
	 */
	private ViewTrainingHeldByTrainerList parentView = null;

	/**
	 * the combo box with the trainer's role.
	 */
	private JComboBox<TrainerRole> comboBoxTrainerRole = new JComboBox<>();

	/**
	 * the button with the trainer's symbol o portrait.
	 */
	private JButton buttonTrainer = new JButton();

	/**
	 * the normal background of the combo box with the trainer's name.
	 */
	private Color buttonTrainerBackground = null;

	/**
	 * the combo box with the trainer's name.
	 */
	private JComboBox<String> comboBoxTrainerName = new JComboBox<>();

	/**
	 * the normal backgound of the combo box with the trainer's name.
	 */
	private Color comboBoxTrainerNameBackground = null;

	/**
	 * the view's layout.
	 */
	private LayoutManager layout = new BorderLayout();

	/**
	 * Hack to ease coding the mouse listeners.
	 */
	private ViewTrainingHeldByTrainer thizz = null;

	/**
	 * the editor's pop up menu.
	 */
	private JPopupMenu popupMenu = new JPopupMenu();

	/**
	 * the editor's pop up new menu.
	 */
	private JMenuItem popupMenuItemNew = new JMenuItem();

	/**
	 * the editor's pop up delete menu.
	 */
	private JMenuItem popupMenuItemDelete = new JMenuItem();

	/**
	 * constructor with the TrainingHeldByTrainer object to present.
	 *
	 * @param trhbt               the TrainingHeldByTrainer object to present
	 * @param parentTrainingsView the parent view
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ViewTrainingHeldByTrainer(final TrainingHeldByTrainer trhbt,
			final ViewTrainingHeldByTrainerList parentTrainingsView) {
		try {
			thizz = this;
			this.setUiEventLock();
			final RapidClubAdminClient client = (RapidClubAdminClient) ApplicationManager.getApplication();
			final RapidBeansLocale locale = client.getCurrentLocale();

			this.trainingHeldByTrainer = trhbt;
			this.parentView = parentTrainingsView;

			this.setLayout(this.layout);

			this.buttonTrainer.setSize(100, 100);
			this.buttonTrainer.setEnabled(false);
			this.buttonTrainerBackground = this.buttonTrainer.getBackground();
			this.buttonTrainer.addMouseListener(new MouseAdapter() {
				public void mouseClicked(final MouseEvent e) {
					parentView.selectTrainingHeldByTrainer(thizz);
				}
			});

			this.comboBoxTrainerName.setRenderer(new RendererListCollection(trhbt.getContainer(), locale));
			this.comboBoxTrainerNameBackground = this.comboBoxTrainerName.getBackground();
			this.comboBoxTrainerName.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					dataBindingChangeTrainer(e);
				}
			});

			if (trhbt.getContainer() != null && ((Document) trhbt.getContainer()).getRoot() != null) {
				this.comboBoxTrainerRole.setModel(new ModelComboBoxCollection(
						(TypePropertyCollection) trhbt.getProperty("role").getType(),
						(Collection<RapidBean>) (((Collection<?>) ((TrainingsList) ((Document) trhbt.getContainer())
								.getRoot()).getTrainerroles()))));
			}
			this.comboBoxTrainerRole.setRenderer(new RendererListCollection(trhbt.getContainer(), locale));
			this.comboBoxTrainerRole.setForeground(Color.BLACK);
			this.comboBoxTrainerRole.setBackground(EditorPropertySwing.COLOR_MANDATORY);
			this.updateComboBoxes();
			this.comboBoxTrainerRole.addMouseListener(new MouseAdapter() {
				public void mouseClicked(final MouseEvent e) {
					parentView.selectTrainingHeldByTrainer(thizz);
				}
			});
			this.comboBoxTrainerRole.addItemListener(new ItemListener() {
				public void itemStateChanged(final ItemEvent e) {
					dataBindingChangeRole(e);
				}
			});

			final Training parentTraining = (Training) trhbt.getParentBean();
			if (parentTraining != null) {
				final Department parentDepartment = parentTraining.getParentDepartment();
				if (parentDepartment != null) {
					final Trainer trainer = trhbt.getTrainer();
					ImageIcon icon = null;
					if (client.getTrainerIcons() == null) {
						icon = TrainerIconManager.getDefaultIcon();
					} else {
						icon = client.getTrainerIcons().get(trainer);
					}
					this.buttonTrainer.setIcon(icon);
					this.buttonTrainer.setDisabledIcon(icon);
					this.comboBoxTrainerName.setModel(
							new ModelComboBoxCollection((TypePropertyCollection) trhbt.getProperty("trainer").getType(),
									(Collection<RapidBean>) ((Collection<?>) parentDepartment.getTrainers())));
				}
			}

			this.popupMenuItemNew.setText(locale.getStringGui("commongui.text.new"));
			this.popupMenuItemNew.addActionListener(new ActionListener() {
				@SuppressWarnings({ "synthetic-access", "unqualified-field-access" })
				public void actionPerformed(final ActionEvent e) {
					popupMenu.setVisible(false);
					TrainingHeldByTrainer newTrhbt = new TrainingHeldByTrainer();
					((TrainingRegular) trainingHeldByTrainer.getParentBean()).addHeldbytrainer(newTrhbt);
				}
			});
			this.popupMenuItemDelete.setText(locale.getStringGui("commongui.text.delete"));
			this.popupMenuItemDelete.addActionListener(new ActionListener() {
				@SuppressWarnings({ "synthetic-access", "unqualified-field-access" })
				public void actionPerformed(final ActionEvent e) {
					popupMenu.setVisible(false);
					trainingHeldByTrainer.delete();
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
			this.popupMenu.add(this.popupMenuItemDelete);
			this.buttonTrainer.setComponentPopupMenu(this.popupMenu);
			this.comboBoxTrainerRole.setComponentPopupMenu(this.popupMenu);
			this.comboBoxTrainerName.setComponentPopupMenu(this.popupMenu);

			this.add(this.comboBoxTrainerRole, BorderLayout.NORTH);
			this.add(this.buttonTrainer, BorderLayout.CENTER);
			this.add(this.comboBoxTrainerName, BorderLayout.SOUTH);

			this.dataBindingUpdateUI();
			this.setVisible(true);
		} finally {
			this.releaseUIEventLock();
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
			if (this.trainingHeldByTrainer == parentView.getSelectedTrainingHeldByTrainer()) {
				this.popupMenuItemDelete.setEnabled(true);
			} else {
				this.popupMenuItemDelete.setEnabled(false);
			}
		} else {
			this.popupMenuItemNew.setEnabled(false);
			this.popupMenuItemDelete.setEnabled(false);
		}
	}

	/**
	 * update the combo boxes.
	 */
	private void dataBindingUpdateUI() {
		this.comboBoxTrainerName.setSelectedItem(this.trainingHeldByTrainer.getTrainer());
		final TrainerIconManager im = ((RapidClubAdminClient) ApplicationManager.getApplication()).getTrainerIcons();
		ImageIcon icon = null;
		if (im == null) {
			icon = TrainerIconManager.getDefaultIcon();
		} else {
			icon = im.get(this.trainingHeldByTrainer.getTrainer());
		}
		this.buttonTrainer.setIcon(icon);
		this.buttonTrainer.setDisabledIcon(icon);
		this.comboBoxTrainerRole.setSelectedItem(this.trainingHeldByTrainer.getRole());
	}

	/**
	 * change the trainer.
	 *
	 * @param itemEvent the item state changed event
	 */
	private void dataBindingChangeTrainer(final ItemEvent itemEvent) {
		if (this.getUIEventLock()) {
			return;
		}
		if (itemEvent.getStateChange() != 1 && this.comboBoxTrainerName.getSelectedItem() != null) {
			return;
		}
		try {
			this.setUiEventLock();
			final Trainer selTrainer = (Trainer) this.comboBoxTrainerName.getSelectedItem();
			this.trainingHeldByTrainer.setTrainer(selTrainer);
			final TrainerRole selRole = (TrainerRole) this.comboBoxTrainerRole.getSelectedItem();
			if (selTrainer != null && selRole != null) {
				this.parentView.getParentView().showTooltipChecked();
			}
		} catch (ValidationException e) {
			final Trainer oldTrainer = this.trainingHeldByTrainer.getTrainer();
			this.comboBoxTrainerName.setSelectedItem(oldTrainer);
			final Application client = ApplicationManager.getApplication();
			final RapidBeansLocale locale = client.getCurrentLocale();
			client.messageError(e.getLocalizedMessage(locale),
					locale.getStringMessage("invalid.prop.trainingheldbytrainer.trainer.title"));
		} finally {
			this.releaseUIEventLock();
		}
	}

	/**
	 * change the trainer role.
	 *
	 * @param itemEvent the item state changed event
	 */
	private void dataBindingChangeRole(final ItemEvent itemEvent) {
		if (this.getUIEventLock()) {
			return;
		}
		if (itemEvent.getStateChange() != 1) {
			return;
		}
		try {
			this.setUiEventLock();
			this.trainingHeldByTrainer.setRole((TrainerRole) this.comboBoxTrainerRole.getSelectedItem());
			this.updateComboBoxes();
		} catch (ValidationException e) {
			final Application client = ApplicationManager.getApplication();
			final RapidBeansLocale locale = client.getCurrentLocale();
			client.messageError(e.getLocalizedMessage(locale),
					locale.getStringMessage("invalid.prop.trainingheldbytrainer.trainer.title"));
		} finally {
			this.releaseUIEventLock();
		}
	}

	public void showSelected() {
		try {
			this.setUiEventLock();
			this.buttonTrainer.setBackground(MainWindowSwing.COLOR_SELECTED_BACKGROUND);
			this.comboBoxTrainerName.setBackground(MainWindowSwing.COLOR_SELECTED_BACKGROUND);
		} finally {
			this.releaseUIEventLock();
		}
	}

	public void showNormal() {
		try {
			this.setUiEventLock();
			this.buttonTrainer.setBackground(this.buttonTrainerBackground);
			this.comboBoxTrainerName.setBackground(this.comboBoxTrainerNameBackground);
		} finally {
			this.releaseUIEventLock();
		}
	}

	/**
	 * event handler for bean added event.
	 *
	 * @param e the added event
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void beanAdded(final AddedEvent e) {
		if (e.getBean() instanceof TrainerRole) {
			this.comboBoxTrainerRole.setModel(new ModelComboBoxCollection(
					(TypePropertyCollection) this.trainingHeldByTrainer.getProperty("role").getType(),
					(Collection<RapidBean>) (((Collection<?>) ((TrainingsList) ((Document) this.trainingHeldByTrainer
							.getContainer()).getRoot()).getTrainerroles()))));
			this.dataBindingUpdateUI();
		}
	}

	/**
	 * event handler for bean changed event.
	 *
	 * @param e the added event
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void bizBeanChanged(final ChangedEvent e) {
		try {
			this.setUiEventLock();
			if (e.getBean() instanceof TrainingHeldByTrainer) {
				if (e.getBean().equals(this.trainingHeldByTrainer)) {
					this.dataBindingUpdateUI();
				}
			} else if (e.getBean() instanceof Department) {
				final Training training = (Training) this.trainingHeldByTrainer.getParentBean();
				if (training != null) {
					final Department dep = training.getParentDepartment();
					if (e.getBean() == dep) {
						for (PropertyChangeEvent pce : e.getPropertyEvents()) {
							if (pce.getProperty() == dep.getProperty("trainers")) {
								this.comboBoxTrainerName.setModel(new ModelComboBoxCollection(
										(TypePropertyCollection) this.trainingHeldByTrainer.getProperty("trainer")
												.getType(),
										(Collection<RapidBean>) ((Collection<?>) dep.getTrainers())));
								this.dataBindingUpdateUI();
							}
						}
					}
				}
			}
		} finally {
			this.releaseUIEventLock();
		}
	}

	/**
	 * Update the enabled state of the combo boxes.
	 */
	private void updateComboBoxes() {
		if (((Training) this.trainingHeldByTrainer.getParentBean()).getState() == TrainingState.checked) {
			this.comboBoxTrainerRole.setEnabled(false);
			this.comboBoxTrainerRole.setComponentPopupMenu(this.popupMenu);
			this.comboBoxTrainerName.setEnabled(false);
			this.comboBoxTrainerName.setComponentPopupMenu(this.popupMenu);
		} else {
			this.comboBoxTrainerRole.setEnabled(true);
			this.comboBoxTrainerRole.setComponentPopupMenu(null);
			this.comboBoxTrainerName.setEnabled(true);
			this.comboBoxTrainerName.setComponentPopupMenu(null);
		}
	}

	/**
	 * @return the parent training of the associated training held by trainer
	 */
	private TrainingRegular getTraining() {
		return (TrainingRegular) this.trainingHeldByTrainer.getParentBean();
	}

	/**
	 * the editor's UI event lock to avoid input event feedback during updating the
	 * UI according to a bean's contents.
	 */
	private int uiEventLock = 0;

	/**
	 * @return the UI event lock
	 */
	private boolean getUIEventLock() {
		return this.uiEventLock > 0;
	}

	/**
	 * increase the UI event lock.
	 */
	private void setUiEventLock() {
		this.uiEventLock++;
	}

	/**
	 * decrease the UI event lock.
	 */
	private void releaseUIEventLock() {
		if (this.uiEventLock > 0) {
			this.uiEventLock--;
		}
	}

	public void focusRoleMenu() {
		this.comboBoxTrainerRole.requestFocus();
	}
}
