/*
 * RapidBeans Application RapidClubAdmin: EditorBillingPeriod.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 30.03.2008
 */
package org.rapidbeans.clubadmin.presentation.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;

import org.rapidbeans.clubadmin.RapidClubAdmin;
import org.rapidbeans.clubadmin.domain.AllTrainingState;
import org.rapidbeans.clubadmin.domain.BackupMode;
import org.rapidbeans.clubadmin.domain.BillingPeriod;
import org.rapidbeans.clubadmin.domain.DataFileType;
import org.rapidbeans.clubadmin.domain.Department;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.domain.TrainingsList;
import org.rapidbeans.clubadmin.domain.report.Evidence;
import org.rapidbeans.clubadmin.domain.report.Overview;
import org.rapidbeans.clubadmin.domain.report.Overview2;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.clubadmin.presentation.enabler.EnablerOpenCurrentBillingPeriod;
import org.rapidbeans.clubadmin.service.Backup;
import org.rapidbeans.clubadmin.service.CreateTrainingsList;
import org.rapidbeans.clubadmin.service.OpenCurrentBillingPeriod;
import org.rapidbeans.clubadmin.service.Restore;
import org.rapidbeans.core.basic.Container;
import org.rapidbeans.core.basic.PropertyCollection;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.common.RapidBeansLocale;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.core.exception.ValidationException;
import org.rapidbeans.core.util.OperatingSystemFamily;
import org.rapidbeans.core.util.PlatformHelper;
import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.DocumentView;
import org.rapidbeans.presentation.EditorProperty;
import org.rapidbeans.presentation.MessageDialog;
import org.rapidbeans.presentation.config.ApplicationGuiType;
import org.rapidbeans.presentation.swing.EditorBeanSwing;
import org.rapidbeans.presentation.swing.EditorPropertyListSwing;
import org.rapidbeans.presentation.swing.MainWindowSwing;
import org.rapidbeans.presentation.swing.RendererListCollection;
import org.rapidbeans.service.ActionArgument;

/**
 * Extends the standard bean editor by plugging in some update actions to the OK
 * and apply buttons.
 * 
 * @author Martin Bluemel
 */
public class EditorBillingPeriod extends EditorBeanSwing {

	private JButton buttonGenerateTrainingsLists = new JButton("Neue Trainingslisten");

	private JButton buttonPrintCollectedReportOverview = new JButton("Sammeldruck Abrechnungen");

	private JButton buttonPrintCollectedReportEvidence = new JButton("Sammeldruck Nachweise");

	private JButton buttonExportAsList = new JButton("Listenexport");

	private JButton buttonCloseBillingPeriod = new JButton("Abschluss");

	private JButton buttonBackup = new JButton("Lokale Sicherung");

	private JButton buttonRestore = new JButton("Wiederherstellen...");

	private JPanel buttonsPanel = new JPanel(new GridBagLayout());

	private static final Logger log = Logger.getLogger(EditorBillingPeriod.class.getName());

	private Map<String, DocumentView> trainingslists = null;

	/**
	 * @param client               the client
	 * @param docView              the document view
	 * @param bizBean              the bean
	 * @param newBeanParentColProp the parent collection property
	 */
	public EditorBillingPeriod(final Application client, final DocumentView docView, final RapidBean bizBean,
			final PropertyCollection newBeanParentColProp) {
		super(client, docView, bizBean, newBeanParentColProp);
		final Application app = ApplicationManager.getApplication();
		final RapidBeansLocale loc = app.getCurrentLocale();
		this.getDepartmentsList().setCellRenderer(new DepartmentListCellRenderer(docView.getDocument(), loc));
		this.buttonGenerateTrainingsLists.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				generateTrainingslists(true, false);
				// reopen the training list dialog
				close();
				new OpenCurrentBillingPeriod().execute();
			}
		});
		this.buttonPrintCollectedReportOverview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				printCollectedOverviewReport();
			}
		});
		this.buttonPrintCollectedReportEvidence.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				printCollectedEvidenceReport();
			}
		});
		this.buttonExportAsList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportDataAsList();
			}
		});
		this.buttonCloseBillingPeriod.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeBillingPeriod();
			}
		});
		this.buttonBackup.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backupIntemediate();
			}
		});
		this.buttonRestore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				restore();
			}
		});
		this.buttonBackup.setEnabled(new EnablerOpenCurrentBillingPeriod().getEnabled());
		this.buttonRestore.setEnabled(true);
		this.buttonsPanel.add(buttonGenerateTrainingsLists, new GridBagConstraints(0, 0, 1, 1, 0.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.buttonsPanel.add(buttonPrintCollectedReportOverview, new GridBagConstraints(0, 1, 1, 1, 0.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.buttonsPanel.add(buttonPrintCollectedReportEvidence, new GridBagConstraints(0, 2, 1, 1, 0.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.buttonsPanel.add(buttonExportAsList, new GridBagConstraints(0, 4, 1, 1, 0.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.buttonsPanel.add(buttonCloseBillingPeriod, new GridBagConstraints(0, 5, 1, 1, 0.0, 1.0,
				GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.buttonsPanel.add(buttonBackup, new GridBagConstraints(0, 6, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		this.buttonsPanel.add(buttonRestore, new GridBagConstraints(0, 7, 1, 1, 0.0, 1.0, GridBagConstraints.CENTER,
				GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
		((JPanel) this.getWidget()).add(this.buttonsPanel, BorderLayout.EAST);
		this.checkTrainingslistsState();
		this.selectAllDepartments();
	}

	/**
	 * action handler for OK button.
	 */
	public void handleActionOk() {
		super.handleActionOk();
		newModeOkApply();
		this.getDocumentView().close();
	}

	/**
	 * action handler for Apply button.
	 */
	public void handleActionApply() {
		// it's important to perform handleActionOk
		// and not handleActionApply here
		super.handleActionOk();
		newModeOkApply();
		// afterwards reopen the bean editor
		new OpenCurrentBillingPeriod().execute();
	}

	/**
	 * action handler for Close / Cancel button.
	 */
	public void handleActionClose() {
		super.handleActionClose();
		this.getDocumentView().close();
	}

	private void newModeOkApply() {
		final RapidClubAdminClient app = (RapidClubAdminClient) ApplicationManager.getApplication();
		if (this.isInNewMode()) {
			if (this.getBillingPeriod().getDepartments() == null
					|| this.getBillingPeriod().getDepartments().size() == 0) {
				if (app.messageYesNo("Alle Abteilungen zuordnen", "Neuer Abrechnungszeitraum")) {
					this.addAllDepartments();
				}
			}
			if (app.messageYesNo("Neue Trainingslisten anlegen", "Neuer Abrechnungszeitraum")) {
				generateTrainingslists(false, true);
			}
		}
	}

	/**
	 * Cast the edited bean to a billing period
	 * 
	 * @return the billing period
	 */
	private BillingPeriod getBillingPeriod() {
		return (BillingPeriod) this.getBean();
	}

	private JList<?> getDepartmentsList() {
		return ((EditorPropertyListSwing) this.getPropEditor("departments")).getWidgetList();
	}

	private List<Department> getAllDepartments() {
		final List<Department> deps = new ArrayList<Department>();
		for (Department dep : this.getBillingPeriod().getDepartments()) {
			deps.add(dep);
		}
		return deps;
	}

	private List<Department> getSelectedDepartments() {
		final ArrayList<Department> deps = new ArrayList<Department>();
		for (Object obj : this.getDepartmentsList().getSelectedValuesList()) {
			deps.add((Department) obj);
		}
		return deps;
	}

	private Department[] getSelectedDepartmentsWithTrainingsLists() {
		final List<Department> selDeps = this.getSelectedDepartments();
		final int selDepsSize = selDeps.size();
		final Department[] deps = new Department[selDepsSize];
		for (int i = 0; i < selDepsSize; i++) {
			final Department selDep = selDeps.get(i);
			final Document trlistDoc = this.trainingslists.get(selDep.getIdString()).getDocument();
			deps[i] = (Department) trlistDoc.findBean(Department.class.getName(), selDep.getIdString());
			if (deps[i] == null) {
				throw new RapidBeansRuntimeException(
						"departement \"" + selDep.getIdString() + "\" not found over TrainingsList");
			}
		}
		return deps;
	}

	private void selectAllDepartments() {
		final JList<?> list = this.getDepartmentsList();
		list.setSelectionInterval(0, list.getModel().getSize() - 1);
	}

	/**
	 * Update the parent Billing Period.
	 */
	private void addAllDepartments() {
		final BillingPeriod bp = (BillingPeriod) this.getBean().getContainer()
				.findBean("org.rapidbeans.clubadmin.domain.BillingPeriod", this.getBillingPeriod().getIdString());
		for (RapidBean bean : this.getBean().getContainer()
				.findBeansByType("org.rapidbeans.clubadmin.domain.Department")) {
			final Department dep = (Department) bean;
			if (bp.getDepartments() == null || !bp.getDepartments().contains(dep)) {
				bp.addDepartment(dep);
			}
		}
	}

	/**
	 * Generate new TrainingsLists for all selected departments.
	 * 
	 * @param confirm        drives interactive confirmation
	 * @param allDepartments if true training lists for all departments will be
	 *                       generated otherwise only for selected departments
	 */
	private void generateTrainingslists(final boolean confirm, final boolean allDepartments) {
		final RapidClubAdminClient app = (RapidClubAdminClient) ApplicationManager.getApplication();
		final RapidBeansLocale locale = app.getCurrentLocale();
		if (confirm && (!app.messageYesNo(locale.getStringMessage("question.saveas.file.trainingslist.knowwhatyoudo"),
				locale.getStringMessage("question.saveas.file.title")))) {
			app.messageInfo("Neue Traininglisten anlegen abgebrochen");
			return;
		}
		List<Department> deps = null;
		if (allDepartments) {
			deps = this.getAllDepartments();
		} else {
			deps = this.getSelectedDepartments();
		}
		for (Department department : deps) {
			// close current training list view if opened
			if (app.getTrainingslistView(null, department) != null) {
				app.getTrainingslistView(null, department).close();
			}
			// create new training list
			final Document trlistdoc = CreateTrainingsList.createNewTrainingsList(app.getMasterDoc(),
					department.getIdString());
			final TrainingsList trlist = (TrainingsList) trlistdoc.getRoot();
			trlist.setFrom(this.getBillingPeriod().getFrom());
			trlist.setTo(this.getBillingPeriod().getTo());
			trlist.updateTrainings(TrainingsList.UPDATE_MODE_PROPS, null);
			trlist.updateTrainingsClosing();
			URL url = null;
			final DataFileType filetype = app.getRunMode();
			switch (filetype) {
			case local:
				try {
					final File file = app.getCurrentTrainingsDataFileLocal(null, department);
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					if (file != null) {
						url = file.toURI().toURL();
					}
				} catch (MalformedURLException e) {
					throw new RapidBeansRuntimeException(e);
				}
				break;
			default:
				url = app.getWebFileManager().getUploadUrl(RapidClubAdminClient.FILE_NAME_TRAININGS_LIST, null,
						department);
				// ensure that directory data/current/<department> exists
				final String parentUrl = StringHelper.strip(StringHelper.splitBeforeLast(url.getFile(), "/"), '/');
				app.getWebFileManager().mkdirs(parentUrl);
				break;
			}
			if (url == null) {
				final String message = "error.save.file." + filetype.toString() + ".trainingslist.nourl";
				final String title = "error.save.file." + filetype.toString() + ".trainingslist.title";
				app.messageError(locale.getStringMessage(message), locale.getStringMessage(title));
			} else {
				trlistdoc.setUrl(url);
				app.save(trlistdoc);
			}
		}
	}

	private void checkTrainingslistsState() {
		loadTrainingslists();
		boolean one_modified = false;
		boolean all_checked = true;
		for (DocumentView trlistView : this.trainingslists.values()) {
			final Document trlistDoc = trlistView.getDocument();
			final AllTrainingState allTrState = ((TrainingsList) trlistDoc.getRoot()).getTrainingsState();
			switch (allTrState) {
			case all_asplanned:
				all_checked = false;
				break;
			case one_checkormodified:
				one_modified = true;
				all_checked = false;
				break;
			case all_checked:
				break;
			default:
				throw new RapidBeansRuntimeException("Unexpected state \"" + allTrState.name() + "\"");
			}
			if (one_modified && (!all_checked)) {
				break;
			}
		}
		if (one_modified) {
			log.info("one training modified");
			this.buttonPrintCollectedReportOverview.setEnabled(false);
			this.buttonPrintCollectedReportEvidence.setEnabled(false);
		} else if (all_checked) {
			log.info("all trainings checked");
			this.buttonPrintCollectedReportOverview.setEnabled(true);
			this.buttonPrintCollectedReportEvidence.setEnabled(true);
		} else {
			log.info("all trainings as planned");
			this.buttonPrintCollectedReportOverview.setEnabled(false);
			this.buttonPrintCollectedReportEvidence.setEnabled(false);
		}
		this.buttonBackup.setEnabled(new EnablerOpenCurrentBillingPeriod().getEnabled());
		this.buttonRestore.setEnabled(true);
	}

	private void loadTrainingslists() {
		final RapidClubAdminClient app = (RapidClubAdminClient) ApplicationManager.getApplication();
		final DocumentView masterDataView = app.getMainwindow().getActiveDocumentView();
		if (this.trainingslists == null) {
			this.trainingslists = new HashMap<String, DocumentView>();
			if (this.getBillingPeriod() != null && this.getBillingPeriod().getDepartments() != null) {
				for (Department dep : this.getBillingPeriod().getDepartments()) {
					log.info("start loading trainings list \"" + dep.getIdString() + "\"...");
					final DocumentView trlistView = app.openCurrentTrainingsList(dep.getIdString());
					this.trainingslists.put(dep.getIdString(), trlistView);
					log.info("finished loading trainings list \"" + dep.getIdString() + "\"");
				}
			}
		}
		app.getMainwindow().putToFront(masterDataView);
	}

	private void printCollectedOverviewReport() {
		FileWriter wr = null;
		try {
			final RapidClubAdminClient app = (RapidClubAdminClient) ApplicationManager.getApplication();
			final RapidBeansLocale locale = app.getCurrentLocale();

			// determine report output file
			File overviewFile = null;
			if (app.getSettingsRapidClubAdmin().getReportfolder() != null) {
				overviewFile = new File(app.getSettingsRapidClubAdmin().getReportfolder(),
						"SammeldruckAbrechnungszeitraum_" + getBillingPeriod().getIdString() + ".txt");
			} else {
				overviewFile = File.createTempFile(
						"SammeldruckAbrechnungszeitraum_" + getBillingPeriod().getIdString() + "_", ".txt");
			}

			final Department[] departments = this.getSelectedDepartmentsWithTrainingsLists();
			final List<Department> depList = new ArrayList<Department>();
			final Collection<Trainer> trainers = new TreeSet<Trainer>();
			for (Department dep : departments) {
				depList.add(dep);
				for (Trainer tr : dep.getTrainers()) {
					if (!trainers.contains(tr)) {
						trainers.add(tr);
					}
				}
			}

			wr = new FileWriter(overviewFile);

			for (Trainer tr : trainers) {
				final List<Trainer> trs = new ArrayList<Trainer>();
				trs.add(tr);
				wr.write(Overview.asString(trs, depList, locale));
				wr.write(PlatformHelper.getLineFeed());
				wr.write(PlatformHelper.getLineFeed());
			}

			// pop up the evidence report
			if (PlatformHelper.getOsfamily() == OperatingSystemFamily.windows) {
				Runtime.getRuntime().exec("cmd.exe /C " + overviewFile.getAbsolutePath());
			} else {
				ApplicationManager.getApplication()
						.messageInfo("Automatic Pop-Up not yet implemented for operating system\n\""
								+ PlatformHelper.getOsName() + "\".\n" + "Please open file\n\""
								+ overviewFile.getAbsolutePath() + "\"\nyourself.");
			}
		} catch (IOException e) {
			throw new RapidBeansRuntimeException(e);
		} finally {
			if (wr != null) {
				try {
					wr.close();
				} catch (IOException e) {
					throw new RapidBeansRuntimeException(e);
				}
			}
		}
	}

	private void printCollectedEvidenceReport() {
		try {
			final RapidClubAdminClient app = (RapidClubAdminClient) ApplicationManager.getApplication();

			// determine report output file
			File evidenceFile = null;
			if (app.getSettingsRapidClubAdmin().getReportfolder() != null) {
				evidenceFile = new File(app.getSettingsRapidClubAdmin().getReportfolder(),
						"SammeldruckNachweiseAbrechnungszeitraum_" + getBillingPeriod().getIdString() + ".rtf");
			} else {
				evidenceFile = File.createTempFile(
						"SammeldruckNachweiseAbrechnungszeitraum_" + getBillingPeriod().getIdString() + "_", ".rtf");
			}

			// load the template file
			final File templateFile = Evidence.findEvidenceReportTemplateFile(false);

			// generate the evidence report
			Evidence.printReportEvidence(evidenceFile, templateFile, this.getSelectedDepartmentsWithTrainingsLists(),
					true);

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

	/** Simple export as plain text list (currently to stdout). */
	private void exportDataAsList() {
		// TODO (BH): This is nearly a copy of the
		// printCollectedOverviewReport() method and should be cleaned up
		FileWriter wr = null;
		try {
			final RapidClubAdminClient app = (RapidClubAdminClient) ApplicationManager.getApplication();
			final RapidBeansLocale locale = app.getCurrentLocale();

			// determine report output file
			File overviewFile = null;
			if (app.getSettingsRapidClubAdmin().getReportfolder() != null) {
				overviewFile = new File(app.getSettingsRapidClubAdmin().getReportfolder(),
						"SammeldruckAbrechnungszeitraum_" + getBillingPeriod().getIdString() + ".txt");
			} else {
				overviewFile = File.createTempFile(
						"SammeldruckAbrechnungszeitraum_" + getBillingPeriod().getIdString() + "_", ".txt");
			}

			final Department[] departments = this.getSelectedDepartmentsWithTrainingsLists();
			final List<Department> depList = new ArrayList<Department>();
			final Collection<Trainer> trainers = new TreeSet<Trainer>();
			for (Department dep : departments) {
				depList.add(dep);
				for (Trainer tr : dep.getTrainers()) {
					if (!trainers.contains(tr)) {
						trainers.add(tr);
					}
				}
			}

			wr = new FileWriter(overviewFile);

			for (Trainer tr : trainers) {
				final List<Trainer> trs = new ArrayList<Trainer>();
				trs.add(tr);
				wr.write(Overview2.asString(trs, depList, locale));
				wr.write(PlatformHelper.getLineFeed());
				wr.write(PlatformHelper.getLineFeed());
			}

			// pop up the evidence report
			if (PlatformHelper.getOsfamily() == OperatingSystemFamily.windows) {
				Runtime.getRuntime().exec("cmd.exe /C " + overviewFile.getAbsolutePath());
			} else {
				ApplicationManager.getApplication()
						.messageInfo("Automatic Pop-Up not yet implemented for operating system\n\""
								+ PlatformHelper.getOsName() + "\".\n" + "Please open file\n\""
								+ overviewFile.getAbsolutePath() + "\"\nyourself.");
			}

			if (this.getBillingPeriod().getDateExportFirst() == null) {
				this.getBillingPeriod().setDateExportFirst(Date.from(Instant.now()));
				this.validateAndUpdateButtons(this.getPropEditor("dateExportFirst"));
			}
		} catch (IOException e) {
			throw new RapidBeansRuntimeException(e);
		} finally {
			if (wr != null) {
				try {
					wr.close();
				} catch (IOException e) {
					throw new RapidBeansRuntimeException(e);
				}
			}
		}
	}

	private void closeBillingPeriod() {
		if (getBillingPeriod().getDateClosing() != null) {
			MessageDialog.createInstance(ApplicationGuiType.swing).messageError(
					"Dieser Abrechnungszeitraum ist bereits abgeschlossen",
					"Fehler beim Abschuss des Abrechnungszeitraums");
		} else {
			getBillingPeriod().setDateClosing(Date.from(Instant.now()));
			this.validateAndUpdateButtons(this.getPropEditor("dateClosing"));
		}
	}

	public void backupIntemediate() {
		final Backup backupAction = new Backup();
		final ActionArgument arg = new ActionArgument();
		arg.setName("mode");
		arg.setValue(BackupMode.intermediate.toString());
		backupAction.addArgument(arg);
		backupAction.execute();
	}

	public void restore() {
		new Restore().execute();
	}

	class DepartmentListCellRenderer extends RendererListCollection {

		/**
		 * constructor.
		 * 
		 * @param doc the document
		 * @param loc the locale
		 */
		public DepartmentListCellRenderer(final Container doc, final RapidBeansLocale loc) {
			super(doc, loc);
		}

		/**
		 * @param list         the list
		 * @param value        the value
		 * @param index        the index
		 * @param isSelected   flag that marks if the cell is selected
		 * @param cellHasFocus if the call has focus
		 * 
		 * @return the rendered component
		 * 
		 * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList,
		 *      java.lang.Object, int, boolean, boolean)
		 */
		public Component getListCellRendererComponent(final JList<?> list, final Object value, final int index,
				final boolean isSelected, final boolean cellHasFocus) {
			final JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			final JLabel label = new JLabel();
			if (value == null) {
				label.setText("-");
			} else {
				final RapidBean bean = (RapidBean) value;
				label.setText(this.getIdString(bean));
			}
			if (isSelected) {
				label.setOpaque(true);
				label.setBackground(MainWindowSwing.COLOR_SELECTED_BACKGROUND);
			}
			final JButton button = new JButton();
			ImageIcon icon = null;
			final Department department = (Department) value;
			final DocumentView docview = trainingslists.get(department.getIdString());
			if (docview != null) {
				final Document trlistDoc = docview.getDocument();
				final AllTrainingState allTrState = ((TrainingsList) trlistDoc.getRoot()).getTrainingsState();
				switch (allTrState) {
				case all_asplanned:
					icon = new ImageIcon(
							RapidClubAdmin.class.getResource("presentation/pictures/training0Default.gif"));
					break;
				case one_checkormodified:
					icon = new ImageIcon(RapidClubAdmin.class.getResource("presentation/pictures/training1InWork.gif"));
					break;
				case all_checked:
					icon = new ImageIcon(
							RapidClubAdmin.class.getResource("presentation/pictures/training2Checked.gif"));
					break;
				default:
					throw new RapidBeansRuntimeException("Unexpected state \"" + allTrState.name() + "\"");
				}
			}
			button.setIcon(icon);
			panel.add(label, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			panel.add(button, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
			return panel;
		}
	}

	/**
	 * @param propEditor the editor that notified the change.
	 */
	public void inputFieldChanged(final EditorProperty propEditor) {
		try {
			super.inputFieldChanged(propEditor);
		} catch (ValidationException e) {
			if (!this.isInNewMode()) {
				throw e;
			}
		}
	}
}
