/*
 * Rapid Club Admin Application: ViewTrainings.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 04.04.2007
 */

package org.rapidbeans.clubadmin.presentation.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolTip;
import javax.swing.ListSelectionModel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;

import org.rapidbeans.clubadmin.domain.Club;
import org.rapidbeans.clubadmin.domain.ClubadminUser;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.RapidClubAdminBusinessLogicException;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.domain.TrainerRole;
import org.rapidbeans.clubadmin.domain.Training;
import org.rapidbeans.clubadmin.domain.TrainingHeldByTrainer;
import org.rapidbeans.clubadmin.domain.TrainingRegular;
import org.rapidbeans.clubadmin.domain.TrainingState;
import org.rapidbeans.clubadmin.domain.TrainingsList;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.core.basic.BeanSorter;
import org.rapidbeans.core.basic.Property;
import org.rapidbeans.core.basic.PropertyCollection;
import org.rapidbeans.core.basic.PropertyDate;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.exception.PropValueNullException;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.core.type.TypeProperty;
import org.rapidbeans.core.util.SoundHelper;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.datasource.Filter;
import org.rapidbeans.datasource.event.AddedEvent;
import org.rapidbeans.datasource.event.ChangedEvent;
import org.rapidbeans.datasource.event.RemovedEvent;
import org.rapidbeans.datasource.query.Query;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.DocumentView;
import org.rapidbeans.presentation.EditorBean;
import org.rapidbeans.presentation.swing.EditorPropertySwing;
import org.rapidbeans.presentation.swing.MainWindowSwing;

/**
 * the complex custom Trainings view.
 * 
 * @author Martin Bluemel
 */
public final class ViewTrainings extends DocumentView {

	// private static final Logger log =
	// Logger.getLogger(ViewTrainings.class.getName());

	/**
	 * the widget.
	 */
	private JInternalFrame frame = new JInternalFrame();

	/**
	 * the split pane.
	 */
	private JSplitPane splitPane = new JSplitPane();

	/**
	 * the trainings table.
	 */
	private JTable trainingsTable = new JTable();

	/**
	 * internal getter.
	 * 
	 * @return the trainings table
	 */
	protected JTable getTrainingsTable() {
		return this.trainingsTable;
	}

	/**
	 * the table panel.
	 */
	private JPanel trainingsTablePanel = new JPanel();

	/**
	 * the trainings table model.
	 */
	private ModelTrainingsTable trainingsModel = null;

	/**
	 * internal getter.
	 * 
	 * @return the trainings table
	 */
	protected ModelTrainingsTable getTrainingsModel() {
		return this.trainingsModel;
	}

	/**
	 * the list scroll pane.
	 */
	private JScrollPane trainingsScrollPane = new JScrollPane();

	/**
	 * the training panel.
	 */
	private JPanel trainingPanel = new JPanel();

	/**
	 * the training buttons panel.
	 */
	private JPanel trainingButtonsPanel = new JPanel();

	/**
	 * the training panel.
	 */
	private JPanel trainingCenterPanel = new JPanel();

	/**
	 * the training panel.
	 */
	private JPanel trainingEditPanel = new JPanel();

	/**
	 * the training south panel.
	 */
	private JPanel trainingSouthPanel = new JPanel();

	/**
	 * the button for toggeling the cancelled state.
	 */
	private JButton trainingButtonToggleCancelled = new JButton("Toggle Cancelled");

	/**
	 * the button for toggelling the checked state.
	 */
	private JButton trainingButtonToggleChecked = new JButton("Toggle Checked");

	/**
	 * The Training's title field's label.
	 */
	private JLabel trainingTitleLabel = new JLabel();

	/**
	 * The Training's title field.
	 */
	private JTextField trainingTitleTextField = new JTextField();

	/**
	 * The Training's state field's label.
	 */
	private JLabel trainingPartcountLabel = new JLabel();

	/**
	 * The Training's title field.
	 */
	private JTextField trainingPartcountTextField = new JTextField();

	/**
	 * The Training's state field's label.
	 */
	private JLabel trainingNotesLabel = new JLabel();

	/**
	 * The Training's title field.
	 */
	private JTextArea trainingNotesTextArea = new JTextArea();

	/**
	 * The Training's state field's label.
	 */
	private JLabel trainingStateLabel = new JLabel();

	/**
	 * The Training's state field.
	 */
	private JTextField trainingStateTextField = new JTextField();

	/**
	 * The Training's last editor label
	 */
	private JLabel trainingLastEditedByLabel = new JLabel();

	/**
	 * The Training's last editor field.
	 */
	private JTextField trainingLastEditedByTextField = new JTextField();

	/**
	 * The Training's last edition label
	 */
	private JLabel trainingLastEditedAtLabel = new JLabel();

	/**
	 * The Training's last edition date field.
	 */
	private JTextField trainingLastEditedAtTextField = new JTextField();

	/**
	 * the trainers list scroll pane.
	 */
	private JScrollPane trainersScrollPane = new JScrollPane();

	/**
	 * the training notes text area scroll pane.
	 */
	private JScrollPane trainingNotesScrollPane = new JScrollPane();

	/**
	 * The Training's trainers view.
	 */
	private ViewTrainingHeldByTrainerList trainersView = new ViewTrainingHeldByTrainerList(this, null);

	/**
	 * Getter for (white box) test purposes.
	 * 
	 * @return the trainers list view
	 */
	public ViewTrainingHeldByTrainerList getTrainersView() {
		return this.trainersView;
	}

	/**
	 * @return the JTreeView.
	 */
	public Object getWidget() {
		return this.frame;
	}

	private ControllerViewTainingsKeys keyController = null;

	/**
	 * constructor.
	 * 
	 * @param client       the client
	 * @param doc          the document to show
	 * @param docconfname  the view's document configuration name
	 * @param viewconfname the view's configuration name
	 * @param filter       the filter
	 */
	public ViewTrainings(final Application client, final Document doc, final String docconfname,
			final String viewconfname, final Filter filter) {
		super(client, doc, docconfname, viewconfname, filter);

		ImageIcon icon = null;
		final Club club = ((TrainingsList) this.getDocument().getRoot()).getForSingleClub();
		final Department department = ((TrainingsList) this.getDocument().getRoot()).getForSingleDepartment();
		final Trainer dummyTrainer = TrainerIconManager.createDepartmentIconDummyTrainer(club, department);
		if (((RapidClubAdminClient) client).getTrainerIcons() != null) {
			icon = ((RapidClubAdminClient) client).getTrainerIcons().get(dummyTrainer);
		}
		if (icon == null) {
			icon = ((MainWindowSwing) client.getMainwindow()).getIconManager()
					.getIcon("view.trainingslist.trainings.icon");
		}
		if (icon != null) {
			this.frame.setFrameIcon(icon);
		}

		final RapidBeansLocale locale = client.getCurrentLocale();

		this.keyController = new ControllerViewTainingsKeys(this);
		trainingsModel = new ModelTrainingsTable(this.getDocument());
		this.frame.setLayout(new BorderLayout());
		this.frame.setMaximizable(true);
		this.frame.setClosable(true);
		this.frame.setIconifiable(true);
		this.frame.setResizable(true);
		this.updateTitle();

		this.trainingsTable.setModel(this.trainingsModel);

		this.trainingsTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		this.trainingsTable.getColumn("Tag").setPreferredWidth(32);
		this.trainingsTable.getColumn("Datum").setPreferredWidth(74);
		this.trainingsTable.getColumn("Beginn").setPreferredWidth(50);
		this.trainingsTable.getColumn("Training").setPreferredWidth(164);
		this.trainingsTable.getColumn("Zustand").setPreferredWidth(56);

		this.trainingsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		final TableCellRenderer cr = new RendererTrainings();
		this.trainingsTable.getColumn("Tag").setCellRenderer(cr);
		this.trainingsTable.getColumn("Datum").setCellRenderer(cr);
		this.trainingsTable.getColumn("Beginn").setCellRenderer(cr);
		this.trainingsTable.getColumn("Training").setCellRenderer(cr);
		this.trainingsTable.getColumn("Zustand").setCellRenderer(cr);
		this.trainingsTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(final ListSelectionEvent e) {
				trainingSelectionChanged();
			}
		});
		this.trainingsTable.setRowHeight(36);
		this.trainingsTable.addKeyListener(this.keyListener);
		this.trainingsTablePanel.setLayout(new BorderLayout());

		if (client == null || !client.getTestMode()) {
			this.frame.setVisible(true);
		} else {
			this.frame.setVisible(true);
		}
		this.markAsChanged(doc.getChanged());
		Dimension mainFrameSize = ((JFrame) this.getClient().getMainwindow().getWidget()).getSize();
		this.frame.setSize(new Dimension(mainFrameSize.width - 10, mainFrameSize.height - 50));
		this.frame.setDefaultCloseOperation(JInternalFrame.DO_NOTHING_ON_CLOSE);
		this.frame.addInternalFrameListener(new InternalFrameListener() {

			public void internalFrameActivated(final InternalFrameEvent e) {
			}

			public void internalFrameClosed(final InternalFrameEvent e) {
			}

			public void internalFrameClosing(final InternalFrameEvent e) {
				close();
			}

			public void internalFrameDeactivated(final InternalFrameEvent e) {
			}

			public void internalFrameDeiconified(final InternalFrameEvent e) {
			}

			public void internalFrameIconified(final InternalFrameEvent e) {
			}

			public void internalFrameOpened(final InternalFrameEvent e) {
			}
		});
		this.trainingPanel.setLayout(new BorderLayout());
		this.trainingButtonsPanel.setLayout(new GridBagLayout());
		this.trainingButtonToggleCancelled.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				toggleCancelled();
			}
		});
		this.trainingButtonToggleCancelled.addKeyListener(this.keyListener);
		this.trainingButtonToggleChecked.addActionListener(new ActionListener() {
			public void actionPerformed(final ActionEvent e) {
				toggleChecked();
			}
		});
		this.trainingButtonToggleChecked.addKeyListener(this.keyListener);
		this.trainingEditPanel.setLayout(new GridBagLayout());
		this.trainingCenterPanel.setLayout(new GridBagLayout());
		this.trainingSouthPanel.setLayout(new GridBagLayout());
		this.trainingTitleLabel.setText(locale.getStringGui("bean.org.rapidbeans.clubadmin.domain.training"));
		this.trainingTitleTextField.setBackground(EditorPropertySwing.COLOR_KEY);
		this.trainingTitleTextField.setEditable(false);
		this.trainingTitleTextField.addKeyListener(this.keyListener);
		this.trainingPartcountLabel
				.setText(locale.getStringGui("bean.org.rapidbeans.clubadmin.domain.training.prop.partipiciantscount"));
		this.trainingPartcountTextField.addKeyListener(new KeyListener() {
			public void keyPressed(final KeyEvent e) {
			}

			public void keyTyped(final KeyEvent e) {
			}

			public void keyReleased(final KeyEvent keyEvent) {
				switch (keyEvent.getKeyCode()) {
				case KeyEvent.VK_LEFT:
				case KeyEvent.VK_RIGHT:
				case KeyEvent.VK_LEFT_PARENTHESIS:
				case KeyEvent.VK_RIGHT_PARENTHESIS:
				case KeyEvent.VK_UP:
				case KeyEvent.VK_DOWN:
					return;
				}
				final List<Training> selTrainings = getSelectedTrainings();
				if (selTrainings != null & selTrainings.size() > 0) {
					final Training training = selTrainings.get(0);
					if (training != null) {
						final String s = trainingPartcountTextField.getText();
						if (s.length() == 0) {
							training.setPropValue("partipiciantscount", null);
						} else {
							String before = null;
							int caretPosition = -1;
							try {
								before = Integer.toString(training.getPartipiciantscount());
								caretPosition = trainingPartcountTextField.getCaretPosition();
							} catch (PropValueNullException e) {
								before = "";
								caretPosition = 0;
							}
							try {
								final int n = Integer.parseInt(s);
								training.setPartipiciantscount(n);
								switch (keyEvent.getKeyCode()) {
								case KeyEvent.VK_DELETE:
									break;
								default:
									if (caretPosition < s.length()) {
										caretPosition++;
									}
								}
								trainingPartcountTextField.setCaretPosition(caretPosition);
								showTooltipChecked();
							} catch (RuntimeException e) {
								SoundHelper.play(Application.class.getResourceAsStream("sounds/error.wav"));
								trainingPartcountTextField.setText(before);
								if (caretPosition > 0) {
									caretPosition--;
								}
								trainingPartcountTextField.setCaretPosition(caretPosition);
							}
						}
					}
				}
			}
		});
		this.trainingStateLabel
				.setText(locale.getStringGui("bean.org.rapidbeans.clubadmin.domain.training.prop.state"));
		this.trainingLastEditedByLabel
				.setText(locale.getStringGui("bean.org.rapidbeans.clubadmin.domain.training.prop.checkedbyuser"));
		this.trainingLastEditedAtLabel
				.setText(locale.getStringGui("bean.org.rapidbeans.clubadmin.domain.training.prop.checkeddate"));
		this.trainingStateTextField.setBackground(EditorPropertySwing.COLOR_KEY);
		this.trainingStateTextField.setEditable(false);
		this.trainingStateTextField.addKeyListener(this.keyListener);
		this.trainingLastEditedByTextField.setBackground(EditorPropertySwing.COLOR_KEY);
		this.trainingLastEditedByTextField.setEditable(false);
		this.trainingLastEditedByTextField.addKeyListener(this.keyListener);
		this.trainingLastEditedAtTextField.setBackground(EditorPropertySwing.COLOR_KEY);
		this.trainingLastEditedAtTextField.setEditable(false);
		this.trainingLastEditedAtTextField.addKeyListener(this.keyListener);
		this.trainingNotesLabel
				.setText(locale.getStringGui("bean.org.rapidbeans.clubadmin.domain.training.prop.notes"));
		this.trainingNotesTextArea.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				try {
					eventLock = true;
					final List<Training> selTrainings = getSelectedTrainings();
					if (selTrainings != null) {
						if (selTrainings.size() > 0) {
							final Training selTraining = getSelectedTrainings().get(0);
							selTraining.setNotes(trainingNotesTextArea.getText());
						}
					}
				} finally {
					eventLock = false;
				}
			}
		});
		this.splitPane.setDividerLocation(400);

		updateButtons(null);

		this.trainingsScrollPane.getViewport().add(this.trainingsTable);
		this.splitPane.add(this.trainingsScrollPane, JSplitPane.LEFT);
		this.trainingButtonsPanel.add(this.trainingButtonToggleCancelled, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.trainingButtonsPanel.add(this.trainingButtonToggleChecked, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.trainingEditPanel.add(this.trainingTitleLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.trainingEditPanel.add(this.trainingTitleTextField, new GridBagConstraints(1, 0, 1, 1, 1.0, 0.0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.trainingEditPanel.add(this.trainingPartcountLabel, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.trainingEditPanel.add(this.trainingPartcountTextField, new GridBagConstraints(1, 1, 1, 1, 1.0, 0.0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.trainingEditPanel.add(this.trainingStateLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.trainingEditPanel.add(this.trainingStateTextField, new GridBagConstraints(1, 2, 1, 1, 1.0, 0.0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.trainingEditPanel.add(this.trainingLastEditedByLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.trainingEditPanel.add(this.trainingLastEditedByTextField, new GridBagConstraints(1, 3, 1, 1, 1.0, 0.0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.trainingEditPanel.add(this.trainingLastEditedAtLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.trainingEditPanel.add(this.trainingLastEditedAtTextField, new GridBagConstraints(1, 4, 1, 1, 1.0, 0.0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.trainingPanel.add(this.trainingEditPanel, BorderLayout.NORTH);
		this.trainingNotesScrollPane.getViewport().add(this.trainingNotesTextArea);
		this.trainingCenterPanel.add(this.trainingNotesLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.trainingCenterPanel.add(this.trainingNotesScrollPane, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		this.trainingPanel.add(this.trainingCenterPanel, BorderLayout.CENTER);
		this.trainersScrollPane.getViewport().add(this.trainersView);
		this.trainingSouthPanel.add(this.trainersScrollPane, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		this.trainingSouthPanel.add(this.trainingButtonsPanel, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0,
				GridBagConstraints.NORTH, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
		this.trainingPanel.add(this.trainingSouthPanel, BorderLayout.SOUTH);
		this.splitPane.add(this.trainingPanel, JSplitPane.RIGHT);
		this.frame.add(this.splitPane, BorderLayout.CENTER);
		selectFirstTrainingToWorkOn(true);
	}

	private int toolTipPopUpCount = 0;

	/**
	 * Pop up an extra tool tip for the checked button the first ten times.
	 */
	protected void showTooltipChecked() {
		if (toolTipPopUpCount < 10 && trainingButtonToggleChecked.isShowing()) {
			new Thread() {
				public void run() {
					final RapidBeansLocale locale = ApplicationManager.getApplication().getCurrentLocale();
					final JToolTip toolTip = trainingButtonToggleChecked.createToolTip();
					toolTip.setTipText(locale.getStringGui("trainingslist.trainings.button.check.tooltip.popup"));
					final PopupFactory popupFactory = PopupFactory.getSharedInstance();
					final Popup toolTipPopup = popupFactory.getPopup(trainingButtonToggleChecked, toolTip,
							trainingButtonToggleChecked.getLocationOnScreen().x - 490,
							trainingButtonToggleChecked.getLocationOnScreen().y + 5);
					toolTipPopup.show();
					try {
						Thread.sleep(4000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					toolTipPopup.hide();
				}
			}.start();
			toolTipPopUpCount++;
		}
	}

	/**
	 * handler for selected beans.
	 * 
	 * @param keys  the tree paths to identify the edited object
	 * @param beans the selected beans
	 * 
	 * @return the bean editor of the last bean edited
	 */
	public EditorBean editBeans(final Object[] keys, final RapidBean[] beans) {
		EditorBean editor = null;
		return editor;
	}

	/**
	 * Update the document view's title.
	 */
	protected void updateTitle() {
		this.frame.setTitle(this.getTitle());
	}

	/**
	 * create a bean.
	 * 
	 * @param key               the tree path
	 * @param parentBeanColProp the parent bean of the new bean
	 * 
	 * @return the bean editor just created
	 */
	public EditorBean createBean(final Object key, final PropertyCollection parentBeanColProp) {
		return null;
	}

	/**
	 * handler for closed bean editors.
	 * 
	 * @param editor the editor to close
	 */
	public void editorClosed(final EditorBean editor) {
	}

	/**
	 * @return the title of the selected tab
	 */
	protected String getSelectedEditorKey() {
		return null;
	}

	/**
	 * mark / unmark the document as changed.
	 * 
	 * @param changed if changed or unchanged
	 */
	public void markAsChanged(final boolean changed) {
		if (changed) {
			if (!this.frame.getTitle().startsWith("*")) {
				this.frame.setTitle("*" + this.frame.getTitle());
			}
		} else {
			if (this.frame.getTitle().startsWith("*")) {
				final String s = this.frame.getTitle();
				this.frame.setTitle(s.substring(1, s.length()));
			}
		}
	}

	/**
	 * close the document view.
	 * 
	 * @return if cancelling is desired
	 */
	public boolean close() {
		boolean cancel = super.close();
		if (!cancel) {
			this.frame.dispose();
		}
		return cancel;
	}

	/**
	 * get the selected trainings.
	 * 
	 * @return the selected trainings
	 */
	private List<Training> getSelectedTrainings() {
		List<Training> selTrainings = new ArrayList<Training>();
		for (int selRowIndex : this.trainingsTable.getSelectedRows()) {
			selTrainings.add(this.trainingsModel.getTrainingAt(selRowIndex));
		}
		return selTrainings;
	}

	/**
	 * toggle the cancelled state of the selected training.
	 */
	private void toggleCancelled() {
		final int selRowCount = this.trainingsTable.getSelectedRowCount();
		if (selRowCount == 1) {
			Training selTraining = this.getSelectedTrainings().get(0);
			selTraining.toggleCancelled();
		} else if (selRowCount == 0) {
			ApplicationManager.getApplication().messageError("No Training selected");
		} else {
			ApplicationManager.getApplication().messageError("More than one Training selected");
		}
	}

	/**
	 * toggle the checked state of the selected training.
	 */
	public void toggleChecked() {
		final int selRowCount = this.trainingsTable.getSelectedRowCount();
		if (selRowCount == 1) {
			final Training selTraining = this.getSelectedTrainings().get(0);
			try {
				selTraining.toggleChecked();
			} catch (RapidClubAdminBusinessLogicException e) {
				if (e.getSignature().equals("invalid.training.state.transition.checked.nullrole")) {
					final TrainingHeldByTrainer trhbt = (TrainingHeldByTrainer) e.getMessageArgs()[0];
					final ViewTrainingHeldByTrainer trhbtView = this.trainersView.getTrhbtView(trhbt);
					this.trainersView.selectTrainingHeldByTrainer(trhbtView);
					trhbtView.focusRoleMenu();
					RapidClubAdminBusinessLogicException e1;
					if (trhbt.getTrainer() != null) {
						e1 = new RapidClubAdminBusinessLogicException(
								"invalid.training.state.transition.checked.nullrole",
								"Trainings held by trainers must have a trainer role set.", new Object[] {
										trhbt.getTrainer().getLastname() + ',' + trhbt.getTrainer().getFirstname() });
					} else {
						e1 = new RapidClubAdminBusinessLogicException(
								"invalid.training.state.transition.checked.nullrole",
								"Trainings held by trainers must have a trainer role set.", new Object[] { "-" });
					}
					e1.present();
				} else {
					e.present();
				}
			}
			Integer value = null;
			if (selTraining.getState() == TrainingState.checked) {
				final String sNumber = this.trainingPartcountTextField.getText().trim();
				if (!sNumber.equals("")) {
					value = Integer.valueOf(sNumber);
				}
				selTraining.setPropValue("partipiciantscount", value);
				selTraining.setNotes(this.trainingNotesTextArea.getText());
			}
		} else if (selRowCount == 0) {
			ApplicationManager.getApplication().messageError("No Training selected");
		} else {
			ApplicationManager.getApplication().messageError("More than one Training selected");
		}
	}

	/**
	 * handle a training selection changed event.
	 */
	private void trainingSelectionChanged() {
		final int selRowCount = this.trainingsTable.getSelectedRowCount();
		if (selRowCount == 1 && this.trainingsTable.getModel().getRowCount() > 0) {
			try {
				final Training selTraining = this.getSelectedTrainings().get(0);
				updateTraining(selTraining);
				updateButtons(selTraining);
			} catch (IndexOutOfBoundsException e) {
				resetTraining();
				updateButtons(null);
			}
		} else {
			resetTraining();
			updateButtons(null);
		}
		// ApplicationManager.getApplication().getMainwindow().getFooter().clearMessage();
	}

	/**
	 * Present the selected training.
	 * 
	 * @param training the selected Training
	 */
	private void updateTraining(final Training training) {
		final RapidBeansLocale locale = ApplicationManager.getApplication().getCurrentLocale();
		final StringBuffer buf = new StringBuffer();
		buf.append(training.getDayofweek().toStringGuiShort(locale));
		buf.append(',');
		buf.append(PropertyDate.formatDate(training.getDate(), locale));
		buf.append(',');
		buf.append(training.getName());
		buf.append(',');
		buf.append(training.getLocation().getName());
		this.trainingTitleTextField.setText(buf.toString());
		Property propPartcount = training.getProperty("partipiciantscount");
		this.trainingPartcountTextField.setEnabled(true);
		this.trainingNotesTextArea.setEnabled(true);
		switch (training.getState()) {
		case cancelled:
		case checked:
		case closed:
		default:
			this.trainingPartcountTextField.setEditable(false);
			this.trainingNotesTextArea.setEditable(false);
			break;
		case asplanned:
		case modified:
			this.trainingPartcountTextField.setEditable(true);
			this.trainingNotesTextArea.setEditable(true);
			break;
		}
		if (propPartcount.getValue() == null) {
			this.trainingPartcountTextField.setText("");
		} else {
			this.trainingPartcountTextField.setText(((Integer) propPartcount.getValue()).toString());
		}
		this.trainingStateTextField.setText(training.getState().toStringGui(locale));
		final ClubadminUser user = training.getCheckedByUser();
		if (user == null) {
			this.trainingLastEditedByTextField.setText("");
			this.trainingLastEditedAtTextField.setText("");
		} else {
			this.trainingLastEditedByTextField.setText(user.getLastname() + ", " + user.getFirstname());
			final Date chdate = training.getCheckedDate();
			this.trainingLastEditedAtTextField
					.setText(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(chdate));
		}
		this.trainingNotesTextArea.setText(training.getNotes());
		this.trainersView.setTraining(training);
	}

	/**
	 * Do not present a training.
	 */
	private void resetTraining() {
		this.trainingTitleTextField.setText("");
		this.trainingPartcountTextField.setEnabled(false);
		this.trainingPartcountTextField.setText("");
		this.trainingNotesTextArea.setEnabled(false);
		this.trainingNotesTextArea.setText("");
	}

	/**
	 * Update the training view's buttons: enabling & text.
	 * 
	 * @param trainingShown the training currently presented
	 */
	private void updateButtons(final Training trainingShown) {
		String buttonToggleCancelledLabelKey = null;
		String buttonToggleCancelledTooltipKey = null;
		String buttonToggleCheckedLabelKey = null;
		String buttonToggleCheckedTooltipKey = null;
		if (trainingShown != null) {
			switch (trainingShown.getState()) {
			case asplanned:
				buttonToggleCancelledLabelKey = "trainingslist.trainings.button.cancel.label";
				buttonToggleCancelledTooltipKey = "trainingslist.trainings.button.cancel.tooltip";
				buttonToggleCheckedLabelKey = "trainingslist.trainings.button.check.label";
				if (!trainingShown.checkFutureChangeOk()) {
					buttonToggleCheckedTooltipKey = "trainingslist.trainings.button.check.tooltip.notinfuture";
				} else {
					buttonToggleCheckedTooltipKey = "trainingslist.trainings.button.check.tooltip";
				}
				break;
			case modified:
				buttonToggleCancelledLabelKey = "trainingslist.trainings.button.cancel.label";
				buttonToggleCancelledTooltipKey = "trainingslist.trainings.button.cancel.tooltip";
				buttonToggleCheckedLabelKey = "trainingslist.trainings.button.check.label";
				if (!trainingShown.checkFutureChangeOk()) {
					buttonToggleCheckedTooltipKey = "trainingslist.trainings.button.check.tooltip.notinfuture";
				} else {
					buttonToggleCheckedTooltipKey = "trainingslist.trainings.button.check.tooltip";
				}
				break;
			case checked:
				buttonToggleCancelledLabelKey = "trainingslist.trainings.button.cancel.label";
				buttonToggleCancelledTooltipKey = "trainingslist.trainings.button.cancel.tooltip";
				buttonToggleCheckedLabelKey = "trainingslist.trainings.button.uncheck.label";
				if (!trainingShown.checkChangingAllowedOk()) {
					buttonToggleCheckedTooltipKey = "trainingslist.trainings.button.check.tooltip.notallowed";
				} else {
					buttonToggleCheckedTooltipKey = "trainingslist.trainings.button.uncheck.tooltip";
				}
				break;
			case cancelled:
				buttonToggleCancelledLabelKey = "trainingslist.trainings.button.uncancel.label";
				if (!trainingShown.checkChangingAllowedOk()) {
					buttonToggleCancelledTooltipKey = "trainingslist.trainings.button.check.tooltip.notallowed";
				} else {
					buttonToggleCancelledTooltipKey = "trainingslist.trainings.button.uncancel.tooltip";
				}
				buttonToggleCheckedLabelKey = "trainingslist.trainings.button.check.label";
				buttonToggleCheckedTooltipKey = "trainingslist.trainings.button.check.tooltip";
				break;
			case closed:
				buttonToggleCancelledLabelKey = "trainingslist.trainings.button.cancel.label";
				buttonToggleCancelledTooltipKey = "trainingslist.trainings.button.cancel.tooltip";
				buttonToggleCheckedLabelKey = "trainingslist.trainings.button.check.label";
				buttonToggleCheckedTooltipKey = "trainingslist.trainings.button.check.tooltip";
				break;
			default:
				throw new RapidBeansRuntimeException(
						"Unexpected TrainigState" + ", order = " + trainingShown.getState().ordinal());
			}
		} else {
			buttonToggleCancelledLabelKey = "trainingslist.trainings.button.cancel.label";
			buttonToggleCancelledTooltipKey = "trainingslist.trainings.button.cancel.tooltip";
			buttonToggleCheckedLabelKey = "trainingslist.trainings.button.check.label";
			buttonToggleCheckedTooltipKey = "trainingslist.trainings.button.check.tooltip";
		}
		final RapidBeansLocale locale = this.getClient().getCurrentLocale();
		this.trainingButtonToggleCancelled.setText(locale.getStringGui(buttonToggleCancelledLabelKey));
		this.trainingButtonToggleCancelled.setToolTipText(locale.getStringGui(buttonToggleCancelledTooltipKey));
		this.trainingButtonToggleChecked.setText(locale.getStringGui(buttonToggleCheckedLabelKey));
		this.trainingButtonToggleChecked.setToolTipText(locale.getStringGui(buttonToggleCheckedTooltipKey));

		boolean buttonToggleCancelledEnabled = false;
		boolean buttonToggleCheckedEnabled = false;
		if (trainingShown != null) {
			switch (trainingShown.getState()) {
			case asplanned:
				buttonToggleCancelledEnabled = true;
				if (trainingShown.checkFutureChangeOk()) {
					buttonToggleCheckedEnabled = true;
				} else {
					buttonToggleCheckedEnabled = false;
				}
				break;
			case modified:
				buttonToggleCancelledEnabled = true;
				if (trainingShown.checkFutureChangeOk()) {
					buttonToggleCheckedEnabled = true;
				} else {
					buttonToggleCheckedEnabled = false;
				}
				break;
			case checked:
				buttonToggleCancelledEnabled = false;
				if (trainingShown.checkChangingAllowedOk()) {
					buttonToggleCheckedEnabled = true;
				} else {
					buttonToggleCheckedEnabled = false;
				}
				break;
			case cancelled:
				if (trainingShown.checkChangingAllowedOk()) {
					buttonToggleCancelledEnabled = true;
				} else {
					buttonToggleCancelledEnabled = false;
				}
				buttonToggleCheckedEnabled = false;
				break;
			case closed:
				buttonToggleCancelledEnabled = false;
				buttonToggleCheckedEnabled = false;
				break;
			default:
				throw new RapidBeansRuntimeException(
						"Unexpected TrainigState" + ", order = " + trainingShown.getState().ordinal());
			}
		}
		if (this.getDocument().getReadonly()) {
			buttonToggleCancelledEnabled = false;
			buttonToggleCheckedEnabled = false;
		}
		this.trainingButtonToggleCancelled.setEnabled(buttonToggleCancelledEnabled);
		this.trainingButtonToggleChecked.setEnabled(buttonToggleCheckedEnabled);
	}

	/**
	 * select the first Training to work on.
	 * 
	 * @param scrollTo scroll to this Training if set to true
	 */
	private void selectFirstTrainingToWorkOn(final boolean scrollTo) {
		// find the first Training which has a "yellow" state
		final Query query = new Query(
				"org.rapidbeans.clubadmin.domain.Training[" + "state = 'asplanned' | state = 'modified']");
		// sort by date
		query.setSorter(new BeanSorter(new TypeProperty[] { new TrainingRegular().getProperty("date").getType() }));
		final List<RapidBean> trainingsToWorkOn = this.getDocument().findBeansByQuery(query);
		if (trainingsToWorkOn.size() > 0) {
			final Training firstTrainingToWorkOn = (Training) trainingsToWorkOn.iterator().next();
			if (firstTrainingToWorkOn != null) {
				final ModelTrainingsTable tableModel = (ModelTrainingsTable) this.trainingsTable.getModel();
				final int rowOfFirstTrainingToWorkOn = tableModel.findRow(firstTrainingToWorkOn);
				this.trainingsTable.getSelectionModel().setSelectionInterval(rowOfFirstTrainingToWorkOn,
						rowOfFirstTrainingToWorkOn);
				if (scrollTo) {
					int rowToScroll = rowOfFirstTrainingToWorkOn;
					if (rowToScroll > 1) {
						rowToScroll -= 2;
					}
					final Rectangle cellRectangle = this.trainingsTable.getCellRect(rowToScroll, 0, true);
					this.trainingsTable.scrollRectToVisible(cellRectangle);
				}
			}
		}
	}

	/**
	 * the view internal keyListener.
	 */
	private KeyListener keyListener = new KeyListener() {

		/**
		 * the component on which a key has been pressed lately.
		 */
		private Component componentPressed = null;

		/**
		 * time last pressed.
		 */
		private long timeLastPressed = 0;

		/**
		 * key pressed.
		 * 
		 * @param e the event
		 */
		public void keyPressed(final KeyEvent e) {
			this.componentPressed = e.getComponent();
			this.timeLastPressed = System.currentTimeMillis();
		}

		/**
		 * key released.
		 * 
		 * @param e the event
		 */
		public void keyReleased(final KeyEvent e) {
			if (this.componentPressed != null && this.componentPressed == e.getComponent()
					&& System.currentTimeMillis() - timeLastPressed <= 1000) {
				this.keyTyped(e);
			}
			this.componentPressed = null;
		}

		/**
		 * key typed.
		 * 
		 * @param e the event
		 */
		public void keyTyped(final KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
				keyController.typedUp(e);
				break;
			case KeyEvent.VK_DOWN:
				keyController.typedDown(e);
				break;
			case KeyEvent.VK_LEFT:
				keyController.typedLeft(e);
				break;
			case KeyEvent.VK_RIGHT:
				keyController.typedRight(e);
				break;
			case KeyEvent.VK_ENTER:
				keyController.typedEnter(e);
				break;
			case KeyEvent.VK_S:
				keyController.typedS(e);
				break;
			default:
				break;
			}
		}
	};

	private boolean eventLock = false;

	/**
	 * event handler for bean pre add event.
	 * 
	 * @param e the added event
	 */
	public void beanAddPre(final AddedEvent e) {
		if (this.eventLock) {
			return;
		}
	}

	/**
	 * event handler for bean added event.
	 * 
	 * @param e the added event
	 */
	public void beanAdded(final AddedEvent e) {
		markAsChanged(true);
		if (this.eventLock) {
			return;
		}
		if (e.getBean() instanceof Training) {
			this.trainingsModel.fireTableRowInserted();
		} else if (e.getBean() instanceof TrainingHeldByTrainer) {
			this.trainersView.beanAdded(e);
		} else if (e.getBean() instanceof TrainerRole) {
			this.trainersView.beanAdded(e);
		}
	}

	/**
	 * event handler for bean changed event.
	 * 
	 * @param e the changed event
	 */
	public void beanChanged(final ChangedEvent e) {
		markAsChanged(true);
		if (this.eventLock) {
			return;
		}
		if (e.getBean() instanceof Training) {
			Training tr = (Training) e.getBean();
			int rowIndex = this.trainingsModel.findRow(tr);
			if (rowIndex > -1) {
				for (int i = 0; i < 5; i++) {
					this.trainingsModel.fireTableCellUpdated(rowIndex, i);
				}
			}
			if (this.trainingsTable.getSelectedRowCount() == 1) {
				Training selTraining = this.getSelectedTrainings().get(0);
				if (selTraining.equals(e.getBean())) {
					this.updateTraining(selTraining);
					this.updateButtons(selTraining);
				}
			} else {
				this.updateButtons(null);
			}
		} else if (e.getBean() instanceof TrainingHeldByTrainer) {
			this.trainersView.bizBeanChanged(e);
		} else if (e.getBean() instanceof Department) {
			this.trainersView.bizBeanChanged(e);
		}
	}

	/**
	 * the index of the removed bean's row.
	 */
	private int bizBeanRemovedPreIndex = -1;

	/**
	 * event handler for bean pre remove event.
	 * 
	 * @param e the removed event
	 */
	public void beanRemovePre(final RemovedEvent e) {
		if (this.eventLock) {
			return;
		}
		if (!(e.getBean() instanceof Training)) {
			return;
		}
		Training tr = (Training) e.getBean();
		this.bizBeanRemovedPreIndex = this.trainingsModel.findRow(tr);
	}

	/**
	 * event handler for bean removed event.
	 * 
	 * @param e the removed event
	 */
	public void beanRemoved(final RemovedEvent e) {
		markAsChanged(true);
		if (this.eventLock) {
			return;
		}
		if (e.getBean() instanceof Training) {
			boolean selChanged = false;
			if (this.trainingsTable.getSelectedRowCount() == 1) {
				try {
					final Training selTraining = this.getSelectedTrainings().get(0);
					if (selTraining == e.getBean()) {
						selChanged = true;
					}
				} catch (IndexOutOfBoundsException ex) {
					selChanged = true;
				}
			}
			if (this.bizBeanRemovedPreIndex > -1) {
				this.trainingsModel.fireTableRowsDeleted(this.bizBeanRemovedPreIndex, this.bizBeanRemovedPreIndex);
				this.bizBeanRemovedPreIndex = -1;
			}
			if (selChanged) {
				this.trainingSelectionChanged();
			}
		} else if (e.getBean() instanceof TrainingHeldByTrainer) {
			this.trainersView.bizBeanRemoved(e);
		} else if (e.getBean() instanceof TrainerRole) {
			this.trainersView.bizBeanRemoved(e);
		}
	}
}
