/*
 * Rapid Club Admin Application: ViewOverview.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 17.08.2007
 */

package org.rapidbeans.clubadmin.presentation.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.rapidbeans.clubadmin.domain.Club;
import org.rapidbeans.clubadmin.domain.ClubadminUser;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.Role;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.domain.report.Evidence;
import org.rapidbeans.clubadmin.domain.report.Overview;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.core.basic.PropertyCollection;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.core.util.OperatingSystemFamily;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.datasource.Filter;
import org.rapidbeans.datasource.event.AddedEvent;
import org.rapidbeans.datasource.event.RemovedEvent;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.DocumentView;
import org.rapidbeans.presentation.EditorBean;
import org.rapidbeans.presentation.ThreadLocalEventLock;
import org.rapidbeans.presentation.swing.MainWindowSwing;

/**
 * the Trainer salary overview.
 * 
 * @author Martin Bluemel
 */
public final class ViewOverview extends DocumentView {

	/**
	 * the left scroll pane.
	 */
	private JScrollPane leftScrollPane = new JScrollPane();

	/**
	 * left Panel's layout
	 */
	private LayoutManager leftPanelLayout = new GridBagLayout();

	/**
	 * the widget.
	 */
	private JInternalFrame frame = new JInternalFrame();

	/**
	 * the split pane.
	 */
	private JSplitPane splitPane = new JSplitPane();

	/**
	 * left Panel.
	 */
	private JPanel leftPanel = new JPanel();

	/**
	 * Title for the trainers table.
	 */
	private JLabel clubsLabel = new JLabel();

	/**
	 * the trainier's table.
	 */
	private JList<RapidBean> clubsList = new JList<>();

	/**
	 * the trainers table model.
	 */
	private ModelListClubs<Club> clubsModel = null;

	/**
	 * Title for the trainers table.
	 */
	private JLabel departmentsLabel = new JLabel();

	/**
	 * the trainier's table.
	 */
	private JList<RapidBean> departmentsList = new JList<>();

	/**
	 * the trainers table model.
	 */
	private ModelListDepartments departmentsModel = null;

	/**
	 * Title for the trainers table.
	 */
	private JLabel trainersLabel = new JLabel();

	/**
	 * the trainier's list.
	 */
	private JList<RapidBean> trainersList = new JList<>();

	/**
	 * @return the trainier's list
	 */
	public JList<RapidBean> getTrainersList() {
		return this.trainersList;
	}

	/**
	 * the trainers table model.
	 */
	private ModelListTrainers trainersModel = null;

	/**
	 * right Panel.
	 */
	private JPanel rightPanel = new JPanel();

	/**
	 * the right scroll pane.
	 */
	private JScrollPane rightScrollPane = new JScrollPane();

	/**
	 * right Panel's layout
	 */
	private LayoutManager rightPanelLayout = new BorderLayout();

	/**
	 * the right scroll pane.
	 */
	private JTextArea overviewTextArea = new JTextArea();

	/**
	 * Only for test reasons.
	 * 
	 * @return the text presented in the text area.
	 */
	public String getText() {
		return this.overviewTextArea.getText();
	}

	/**
	 * the left panel's button panel.
	 */
	private JPanel rightButtonsPanel = new JPanel();

	/**
	 * the left button panel's layout.
	 */
	private LayoutManager rightButtonsPanelLayout = new GridBagLayout();

	/**
	 * the print button.
	 */
	private JButton buttonPrintReportEvidence = new JButton();

	/**
	 * @return the JTreeView.
	 */
	public Object getWidget() {
		return this.frame;
	}

	/**
	 * constructor.
	 * 
	 * @param client       the client
	 * @param doc          the document to show
	 * @param docconfname  the view's document configuration name
	 * @param viewconfname the view's configuration name
	 * @param filter       the filter
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ViewOverview(final Application client, final Document doc, final String docconfname,
			final String viewconfname, final Filter filter) {
		super(client, doc, docconfname, viewconfname, filter);

		ImageIcon icon = ((MainWindowSwing) client.getMainwindow()).getIconManager()
				.getIcon("view.trainingslist.overview.icon");
		this.frame.setFrameIcon(icon);

		final RapidBeansLocale locale = client.getCurrentLocale();

		this.clubsModel = new ModelListClubs<>(this.getDocument());
		this.departmentsModel = new ModelListDepartments(this.getDocument());
		this.trainersModel = new ModelListTrainers(this.getDocument());

		this.frame.setLayout(new BorderLayout());
		this.frame.setMaximizable(true);
		this.frame.setClosable(true);
		this.frame.setIconifiable(true);
		this.frame.setResizable(true);
		this.updateTitle();

		this.clubsLabel.setText(locale.getStringGui("view.trainingslist.overview.clubs.label"));
		this.clubsList.setModel(this.clubsModel);
		this.clubsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.clubsList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(final ListSelectionEvent e) {
				clubSelectionChanged(e);
			}
		});

		this.departmentsLabel.setText(locale.getStringGui("view.trainingslist.overview.departments.label"));
		this.departmentsList.setModel(this.departmentsModel);
		this.departmentsList.setCellRenderer(new RendererListOverviewDepartments(this));
		this.departmentsList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.departmentsList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(final ListSelectionEvent e) {
				departmentSelectionChanged(e);
			}
		});

		this.trainersLabel.setText(locale.getStringGui("view.trainingslist.overview.trainers.label"));
		this.trainersList.setModel(this.trainersModel);
		this.trainersList.setCellRenderer(new RendererListOverviewTrainers());
		this.trainersList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.trainersList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(final ListSelectionEvent e) {
				trainerSelectionChanged(e);
			}
		});

		this.leftPanel.setLayout(this.leftPanelLayout);
		this.rightPanel.setLayout(this.rightPanelLayout);
		this.overviewTextArea.setFont(new Font("monospaced", 12, 10));
		if (client == null || !client.getTestMode()) {
			this.frame.setVisible(true);
		} else {
			this.frame.setVisible(true);
		}
		this.rightButtonsPanel.setLayout(this.rightButtonsPanelLayout);
		this.buttonPrintReportEvidence.setText(locale.getStringGui("view.trainingslist.overview.button.trainerreport"));
		this.buttonPrintReportEvidence.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				printReportEvidence();
			}

		});
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
		this.splitPane.setDividerLocation(210);

		this.leftPanel.add(this.clubsLabel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.leftPanel.add(this.clubsList, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.leftPanel.add(this.departmentsLabel, new GridBagConstraints(0, 2, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.leftPanel.add(this.departmentsList, new GridBagConstraints(0, 3, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.leftPanel.add(this.trainersLabel, new GridBagConstraints(0, 4, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.leftPanel.add(this.trainersList, new GridBagConstraints(0, 5, 1, 1, 1.0, 1.0, GridBagConstraints.NORTH,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.rightScrollPane.getViewport().add(this.overviewTextArea);
		this.rightPanel.add(this.rightScrollPane, BorderLayout.CENTER);
		this.rightPanel.add(this.rightButtonsPanel, BorderLayout.SOUTH);
		this.rightButtonsPanel.add(this.buttonPrintReportEvidence, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.leftScrollPane.getViewport().add(this.leftPanel);
		this.splitPane.add(this.leftScrollPane, JSplitPane.LEFT);
		this.splitPane.add(this.rightPanel, JSplitPane.RIGHT);
		this.frame.add(this.splitPane, BorderLayout.CENTER);
		selectDefault();
	}

	/**
	 * perform a default selection according to the authenticated user.
	 */
	private void selectDefault() {
		final RapidClubAdminClient client = (RapidClubAdminClient) ApplicationManager.getApplication();
		final ClubadminUser user = (ClubadminUser) client.getAuthenticatedUser();
		final Role role = user.getRole();
		Club clubToSelect = null;
		Department departmentToSelect = null;
		Trainer trainerToSelect = null;
		String wdId = client.getSettingsRapidClubAdmin().getWorkingdepartment();

		// preferably select the working department
		if (wdId != null) {
			departmentToSelect = (Department) client.getMasterDoc()
					.findBean("org.rapidbeans.clubadmin.domain.Department", wdId);
		}
		if (user.getIsalsotrainer() != null) {
			trainerToSelect = user.getIsalsotrainer();
			if (departmentToSelect == null) {
				if (trainerToSelect.getDepartments() != null) {
					departmentToSelect = user.getIsalsotrainer().getDepartments().iterator().next();
				}
			}
		} else if (departmentToSelect == null) {
			final Set<Department> authDeps = user.getAuthorizedDepartments();
			if (role == Role.DepartmentAdministrator && authDeps != null && authDeps.size() > 0) {
				departmentToSelect = authDeps.iterator().next();
			} else if (role == Role.SuperAdministrator) {
				final List<RapidBean> deps = this.getDocument()
						.findBeansByType("org.rapidbeans.clubadmin.domain.Department");
				if (deps.size() > 0) {
					departmentToSelect = (Department) deps.get(0);
				}
			}
		}
		if (departmentToSelect != null && ((Club) departmentToSelect.getParentBean()) != null) {
			clubToSelect = (Club) departmentToSelect.getParentBean();
		}
		if (clubToSelect != null) {
			selectSingleClub(clubToSelect);
		}
		if (departmentToSelect != null) {
			selectSingleDepartment(departmentToSelect);
		}
		if (trainerToSelect != null) {
			selectSingleTrainer(trainerToSelect);
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
	 * @return if canceling is desired
	 */
	public boolean close() {
		final boolean cancel = super.close();
		if (!cancel) {
			this.frame.dispose();
		}
		return cancel;
	}

	/**
	 * get the selected departments.
	 * 
	 * @return the selected clubs
	 */
	protected List<Club> getSelectedClubs() {
		final List<Club> sels = new ArrayList<Club>();
		for (RapidBean selValue : this.clubsList.getSelectedValuesList()) {
			sels.add((Club) selValue);
		}
		return sels;
	}

	/**
	 * get the selected departments.
	 * 
	 * @return the selected departments
	 */
	private List<Department> getSelectedDepartments() {
		final List<Department> sels = new ArrayList<Department>();
		for (RapidBean selValue : this.departmentsList.getSelectedValuesList()) {
			sels.add((Department) selValue);
		}
		return sels;
	}

	/**
	 * get the selected trainers.
	 * 
	 * @return the selected trainers
	 */
	private List<Trainer> getSelectedTrainers() {
		final List<Trainer> sels = new ArrayList<Trainer>();
		for (RapidBean selValue : this.trainersList.getSelectedValuesList()) {
			sels.add((Trainer) selValue);
		}
		return sels;
	}

	/**
	 * Select a single club by the program.
	 * 
	 * @param clubToSelect the Club to select
	 */
	private void selectSingleClub(final Club clubToSelect) {
		final int rowToSelect = this.clubsModel.findRow(clubToSelect);
		this.clubsList.getSelectionModel().setSelectionInterval(rowToSelect, rowToSelect);
		this.clubSelectionChanged(null);
	}

	/**
	 * Select a single Department by the program.
	 * 
	 * @param departmentToSelect the Department to select
	 */
	private void selectSingleDepartment(final Department departmentToSelect) {
		final int rowToSelect = this.departmentsModel.findRow(departmentToSelect);
		this.departmentsList.getSelectionModel().setSelectionInterval(rowToSelect, rowToSelect);
		this.departmentSelectionChanged(null);
	}

	/**
	 * Select a single Trainer by the program.
	 * 
	 * @param trainerToSelect the Trainer to select
	 */
	private void selectSingleTrainer(final Trainer trainerToSelect) {
		final int rowToSelect = this.trainersModel.findRow(trainerToSelect);
		this.trainersList.getSelectionModel().setSelectionInterval(rowToSelect, rowToSelect);
		this.trainerSelectionChanged(null);
	}

	/**
	 * handle a club selection changed event.
	 */
	private void clubSelectionChanged(final ListSelectionEvent e) {
		if (ThreadLocalEventLock.get()) {
			return;
		}
		try {
			ThreadLocalEventLock.set(null);
			if (this.selectionChanged(e)) {
				this.departmentsList.clearSelection();
				this.departmentsModel.extendFilter(this.getSelectedClubs());
				this.trainersList.clearSelection();
				this.trainersModel.extendFilterClubs(this.getSelectedClubs());
			}
		} finally {
			ThreadLocalEventLock.release();
		}
	}

	/**
	 * handle a department selection changed event.
	 */
	private void departmentSelectionChanged(final ListSelectionEvent e) {
		if (ThreadLocalEventLock.get()) {
			return;
		}
		try {
			ThreadLocalEventLock.set(null);
			if (this.selectionChanged(e)) {
				this.trainersList.clearSelection();
				this.trainersModel.extendFilterDepartments(this.getSelectedDepartments());
			}
		} finally {
			ThreadLocalEventLock.release();
		}
	}

	/**
	 * handle a trainer selection changed event.
	 */
	private void trainerSelectionChanged(final ListSelectionEvent e) {
		if (ThreadLocalEventLock.get()) {
			return;
		}
		try {
			ThreadLocalEventLock.set(null);
			this.selectionChanged(e);
		} finally {
			ThreadLocalEventLock.release();
		}
	}

	/**
	 * flag to suppress reacting on hos event too often.
	 */
	private int suppressEventTwice = 0;

	/**
	 * handle a selection changed event.
	 * 
	 * @param e the list selection event.
	 * 
	 * @return false if the event should be suppressed, true otherwise
	 */
	private boolean selectionChanged(final ListSelectionEvent e) {
		switch (this.suppressEventTwice) {
		case 0:
			this.suppressEventTwice = 1;
			break;
		case 1:
			this.suppressEventTwice = 0;
			return false;
		}
		final RapidBeansLocale locale = ApplicationManager.getApplication().getCurrentLocale();
		final String overview = Overview.asString(this.getSelectedTrainers(), this.getSelectedDepartments(), locale);
		this.overviewTextArea.setText(overview);
		return true;
	}

	/**
	 * event handler for bean pre add event.
	 * 
	 * @param e the added event
	 */
	public void beanAddPre(final AddedEvent e) {
	}

	/**
	 * event handler for bean added event.
	 * 
	 * @param e the added event
	 */
	public void beanAdded(final AddedEvent e) {
		markAsChanged(true);
		if (e.getBean() instanceof Trainer) {
			this.trainersModel.fireContentsChanged();
		} else if (e.getBean() instanceof Department) {
			this.departmentsModel.fireContentsChanged();
		}
	}

	/**
	 * the index of the removed bean's row.
	 */
	private int bizBeanRemovedPreIndexTrainer = -1;

	/**
	 * the index of the removed bean's row.
	 */
	private int bizBeanRemovedPreIndexDepartment = -1;

	/**
	 * event handler for bean pre remove event.
	 * 
	 * @param e the removed event
	 */
	public void beanRemovePre(final RemovedEvent e) {
		if (e.getBean() instanceof Trainer) {
			Trainer tr = (Trainer) e.getBean();
			this.bizBeanRemovedPreIndexTrainer = this.trainersModel.findRow(tr);
		} else if (e.getBean() instanceof Department) {
			Department dep = (Department) e.getBean();
			this.bizBeanRemovedPreIndexDepartment = this.departmentsModel.findRow(dep);
		}
	}

	/**
	 * event handler for bean removed event.
	 * 
	 * @param e the removed event
	 */
	public void beanRemoved(final RemovedEvent e) {
		markAsChanged(true);
		if (e.getBean() instanceof Trainer) {
			boolean selChanged = false;
			if (this.trainersList.getSelectedValuesList().size() == 1) {
				Trainer sel = this.getSelectedTrainers().get(0);
				if (sel == e.getBean()) {
					selChanged = true;
				}
			}
			if (this.bizBeanRemovedPreIndexTrainer > -1) {
				this.trainersModel.fireContentsChanged();
				// this.bizBeanRemovedPreIndexTrainer,
				// this.bizBeanRemovedPreIndexTrainer);
				this.bizBeanRemovedPreIndexTrainer = -1;
			}
			if (selChanged) {
				this.trainerSelectionChanged(null);
			}
		} else if (e.getBean() instanceof Department) {
			boolean selChanged = false;
			if (this.trainersList.getSelectedValuesList().size() == 1) {
				Department sel = this.getSelectedDepartments().get(0);
				if (sel == e.getBean()) {
					selChanged = true;
				}
			}
			if (this.bizBeanRemovedPreIndexDepartment > -1) {
				this.trainersModel.fireContentsChanged();
				// this.bizBeanRemovedPreIndexDepartment,
				// this.bizBeanRemovedPreIndexDepartment);
				this.bizBeanRemovedPreIndexDepartment = -1;
			}
			if (selChanged) {
				this.trainerSelectionChanged(null);
			}
		}
	}

	/**
	 * Print a simple ASCII Trainer report.
	 */
	private void printReportEvidence() {
		if (this.getSelectedTrainers().size() != 1) {
			return;
		}
		if (this.getSelectedDepartments().size() != 1) {
			return;
		}
		try {
			final File evidenceFile = File.createTempFile("Nachweis_" + this.getSelectedTrainers().get(0).getLastname(),
					".rtf");
			final File templateFile = Evidence.findEvidenceReportTemplateFile(false);

			// generate the evidence report
			Evidence.printReportEvidence(evidenceFile, templateFile, this.getSelectedDepartments().get(0),
					this.getSelectedTrainers().get(0), true);
			// pop up the evidence report

			if (PlatformHelper.getOsfamily() == OperatingSystemFamily.windows) {
				Runtime.getRuntime().exec("cmd.exe /C " + evidenceFile.getAbsolutePath());
			} else {
				ApplicationManager.getApplication()
						.messageInfo("Automatic Pop-Up not yet implemented for operating system\n\""
								+ PlatformHelper.getOsName() + "\".\n" + "Please open file\n\""
								+ evidenceFile.getAbsolutePath() + "\"\nyourself.");
			}
		} catch (IOException e) {
			throw new RapidBeansRuntimeException(e);
		}
	}
}
