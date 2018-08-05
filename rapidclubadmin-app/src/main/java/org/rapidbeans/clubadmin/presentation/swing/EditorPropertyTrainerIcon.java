/*
 * Rapid Beans Framework: EditorPropertyFileSwing.java
 *
 * Copyright Martin Bluemel, 2006
 *
 * 22.12.2006
 */

package org.rapidbeans.clubadmin.presentation.swing;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;

import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.core.basic.Property;
import org.rapidbeans.core.basic.PropertyDate;
import org.rapidbeans.core.exception.RapidBeansRuntimeException;
import org.rapidbeans.core.exception.ValidationException;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.ApplicationManager;
import org.rapidbeans.presentation.EditorBean;
import org.rapidbeans.presentation.swing.EditorPropertySwing;
import org.rapidbeans.presentation.swing.ExampleFileFilter;

/**
 * the bean editor GUI.
 *
 * @author Martin Bluemel
 */
public class EditorPropertyTrainerIcon extends EditorPropertySwing {

	/**
	 * the text field.
	 */
	private JButton buttonIcon = new JButton();

	/**
	 * the text field.
	 */
	private JButton button = new JButton("+");

	/**
	 * the text field.
	 */
	private JButton buttonReset = new JButton("-");

	/**
	 * the text field.
	 */
	private JPanel panel = new JPanel();

	/**
	 * the layout manager.
	 */
	private LayoutManager layout = new GridBagLayout();

	/**
	 * @return the editor's widget
	 */
	public Object getWidget() {
		return this.panel;
	}

	/**
	 * constructor.
	 *
	 * @param prop          the bean property to edit
	 * @param propBak       the bean property backup
	 * @param bizBeanEditor the parent bean editor
	 * @param client        the client
	 */
	public EditorPropertyTrainerIcon(final Application client, final EditorBean bizBeanEditor, final Property prop,
			final Property propBak) {
		super(client, bizBeanEditor, prop, propBak);
		final RapidClubAdminClient rclient = (RapidClubAdminClient) client;
		if (!(prop instanceof PropertyDate)) {
			throw new RapidBeansRuntimeException("invalid property for the trainer icon editor");
		}
		super.initColors();
		ImageIcon icon = null;
		if (rclient.getTrainerIcons() != null) {
			icon = rclient.getTrainerIcons().get((Trainer) prop.getBean());
		} else {
			icon = TrainerIconManager.getDefaultIcon();
		}
		this.buttonIcon.setIcon(icon);
		this.buttonIcon.setDisabledIcon(icon);
		this.buttonIcon.setEnabled(false);
		this.button.setText(client.getCurrentLocale()
				.getStringGui("editor.org.rapidbeans.clubadmin.domain.trainer.icon.button.set"));
		this.button.addActionListener(new ActionListener() {
			/**
			 * @param e the event
			 */
			public void actionPerformed(final ActionEvent e) {
				chooseFile();
			}
		});
		this.buttonReset.setText(client.getCurrentLocale()
				.getStringGui("editor.org.rapidbeans.clubadmin.domain.trainer.icon.button.reset"));
		this.buttonReset.addActionListener(new ActionListener() {
			/**
			 * @param e the event
			 */
			public void actionPerformed(final ActionEvent e) {
				resetTrainerIcon();
			}
		});
		this.panel.setLayout(this.layout);
		this.panel.add(this.buttonIcon, new GridBagConstraints(0, 0, 1, 2, 1.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
		this.panel.add(this.button, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.panel.add(this.buttonReset, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		this.updateUI();
	}

	/**
	 * update the string presented in the editor.
	 */
	public void updateUI() {
		try {
			this.setUIEventLock();
			RapidClubAdminClient client = (RapidClubAdminClient) ApplicationManager.getApplication();
			ImageIcon icon = null;
			if (client.getTrainerIcons() == null) {
				icon = TrainerIconManager.getDefaultIcon();
			} else {
				icon = client.getTrainerIcons().get((Trainer) this.getProperty().getBean());
			}
			this.buttonIcon.setIcon(icon);
			this.buttonIcon.setDisabledIcon(icon);
			if (this.getBeanEditor().isInNewMode()) {
				this.button.setEnabled(false);
				this.buttonReset.setEnabled(false);
			} else {
				this.button.setEnabled(true);
				this.buttonReset.setEnabled(true);
			}
		} finally {
			this.releaseUIEventLock();
		}
	}

	/**
	 * @return the Text field's content
	 */
	public Object getInputFieldValue() {
		Trainer trainer = (Trainer) this.getProperty().getBean();
		return trainer.getIcon();
//        TrainerIconManager iconMan = ((RapidClubAdminClient)
//                ApplicationManager.getClient()).getTrainerIcons();
//        if (iconMan.get(trainer) == iconMan.getDefaultIcon()) {
//            return null;
//        } else {
//            return new Date(iconMan.getIconFile(trainer.getIdString()).lastModified());
//        }
	}

	/**
	 * @return the input field value as string.
	 */
	public String getInputFieldValueString() {
		Trainer trainer = (Trainer) this.getProperty().getBean();
		if (trainer.getIcon() == null) {
			return null;
		} else {
			return PropertyDate.formatDate(trainer.getIcon(), ApplicationManager.getApplication().getCurrentLocale());
		}
	}

	/**
	 * validate an input field.
	 * 
	 * @return if the string in the input field is valid or at least could at least
	 *         get after appending additional characters.
	 *
	 * @param ex the validation exception
	 */
	protected boolean hasPotentiallyValidInputField(final ValidationException ex) {
		if (ex.getSignature().startsWith("invalid.prop.integer")) {
			if (ex.getSignature().endsWith("lower")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * open a file chooser dialog and choose the file.
	 */
	private void chooseFile() {
		final RapidClubAdminClient client = (RapidClubAdminClient) ApplicationManager.getApplication();
		final JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setCurrentDirectory(client.getSettings().getBasic().getFolderdoc());
		ExampleFileFilter filter = new ExampleFileFilter();
		filter.addExtension("jpg");
		filter.setDescription("JPEG Image");
		chooser.setFileFilter(filter);
		chooser.setDialogTitle(this.getLocale().getStringGui("commongui.text.choose") + ": "
				+ this.getProperty().getNameGui(this.getLocale()));
		int returnVal = chooser.showDialog(
				(Component) this.getBeanEditor().getDocumentView().getClient().getMainwindow().getWidget(),
				this.getLocale().getStringGui("commongui.text.choose"));
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			final File file = chooser.getSelectedFile();
			client.getTrainerIcons().importIcon((Trainer) this.getProperty().getBean(), file);
			((Trainer) this.getProperty().getBean()).setIcon(new Date());
			this.fireInputFieldChanged();
		}
	}

	private void resetTrainerIcon() {
		final RapidClubAdminClient client = (RapidClubAdminClient) ApplicationManager.getApplication();
		client.getTrainerIcons().deleteIcon((Trainer) this.getProperty().getBean());
		((Trainer) this.getProperty().getBean()).setIcon(null);
		this.fireInputFieldChanged();
	}
}
