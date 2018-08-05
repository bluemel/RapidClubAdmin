/*
 * Partially generated code file: RapidClubAdminSettings.java
 * !!!Do only edit manually in marked sections!!!
 *
 * Rapid Beans bean generator, Copyright Martin Bluemel, 2008
 *
 * generated Java implementation of Rapid Beans bean type
 * org.rapidbeans.clubadmin.presentation.RapidClubAdminSettings
 * 
 * model:    model/org/rapidbeans/clubadmin/presentation/RapidClubAdminSettings.xml
 * template: codegentemplates/genBean.xsl
 */

package org.rapidbeans.clubadmin.presentation;

// BEGIN manual code section
// RapidClubAdminSettings.import
import org.rapidbeans.core.type.TypeRapidBean;

// END manual code section

/**
 * Rapid Bean class: RapidClubAdminSettings. Partially generated Java class
 * !!!Do only edit manually in marked sections!!!
 **/
public class RapidClubAdminSettings extends org.rapidbeans.presentation.settings.Settings {

	// BEGIN manual code section
	// RapidClubAdminSettings.classBody
	// END manual code section

	/**
	 * property "backupfolder".
	 */
	private org.rapidbeans.core.basic.PropertyFile backupfolder;

	/**
	 * property "exportfile".
	 */
	private org.rapidbeans.core.basic.PropertyFile exportfile;

	/**
	 * property "reportfolder".
	 */
	private org.rapidbeans.core.basic.PropertyFile reportfolder;

	/**
	 * property "defaultdatafileloadinitially".
	 */
	private org.rapidbeans.core.basic.PropertyBoolean defaultdatafileloadinitially;

	/**
	 * property "workingdepartment".
	 */
	private org.rapidbeans.core.basic.PropertyString workingdepartment;

	/**
	 * property "shareiconsoverweb".
	 */
	private org.rapidbeans.core.basic.PropertyBoolean shareiconsoverweb;

	/**
	 * property "pleasedontnag".
	 */
	private org.rapidbeans.core.basic.PropertyBoolean pleasedontnag;

	/**
	 * property references initialization.
	 */
	public void initProperties() {
		super.initProperties();
		this.backupfolder = (org.rapidbeans.core.basic.PropertyFile) this.getProperty("backupfolder");
		this.exportfile = (org.rapidbeans.core.basic.PropertyFile) this.getProperty("exportfile");
		this.reportfolder = (org.rapidbeans.core.basic.PropertyFile) this.getProperty("reportfolder");
		this.defaultdatafileloadinitially = (org.rapidbeans.core.basic.PropertyBoolean) this
				.getProperty("defaultdatafileloadinitially");
		this.workingdepartment = (org.rapidbeans.core.basic.PropertyString) this.getProperty("workingdepartment");
		this.shareiconsoverweb = (org.rapidbeans.core.basic.PropertyBoolean) this.getProperty("shareiconsoverweb");
		this.pleasedontnag = (org.rapidbeans.core.basic.PropertyBoolean) this.getProperty("pleasedontnag");
	}

	/**
	 * default constructor.
	 */
	public RapidClubAdminSettings() {
		super();
		// BEGIN manual code section
		// RapidClubAdminSettings.RapidClubAdminSettings()
		// END manual code section

	}

	/**
	 * constructor out of a string.
	 * 
	 * @param s the string
	 */
	public RapidClubAdminSettings(final String s) {
		super(s);
		// BEGIN manual code section
		// RapidClubAdminSettings.RapidClubAdminSettings(String)
		// END manual code section

	}

	/**
	 * constructor out of a string array.
	 * 
	 * @param sa the string array
	 */
	public RapidClubAdminSettings(final String[] sa) {
		super(sa);
		// BEGIN manual code section
		// RapidClubAdminSettings.RapidClubAdminSettings(String[])
		// END manual code section
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(RapidClubAdminSettings.class);

	/**
	 * @return the Biz Bean's type
	 */
	public TypeRapidBean getType() {
		return type;
	}

	/**
	 * @return value of Property 'backupfolder'
	 */
	public java.io.File getBackupfolder() {
		try {
			return (java.io.File) this.backupfolder.getValue();
		} catch (NullPointerException e) {
			throw new org.rapidbeans.core.exception.PropNotInitializedException("backupfolder");
		}
	}

	/**
	 * setter for Property 'backupfolder'.
	 * 
	 * @param argValue value of Property 'backupfolder' to set
	 */
	public void setBackupfolder(final java.io.File argValue) {
		this.backupfolder.setValue(argValue);
	}

	/**
	 * @return value of Property 'exportfile'
	 */
	public java.io.File getExportfile() {
		try {
			return (java.io.File) this.exportfile.getValue();
		} catch (NullPointerException e) {
			throw new org.rapidbeans.core.exception.PropNotInitializedException("exportfile");
		}
	}

	/**
	 * setter for Property 'exportfile'.
	 * 
	 * @param argValue value of Property 'exportfile' to set
	 */
	public void setExportfile(final java.io.File argValue) {
		this.exportfile.setValue(argValue);
	}

	/**
	 * @return value of Property 'reportfolder'
	 */
	public java.io.File getReportfolder() {
		try {
			return (java.io.File) this.reportfolder.getValue();
		} catch (NullPointerException e) {
			throw new org.rapidbeans.core.exception.PropNotInitializedException("reportfolder");
		}
	}

	/**
	 * setter for Property 'reportfolder'.
	 * 
	 * @param argValue value of Property 'reportfolder' to set
	 */
	public void setReportfolder(final java.io.File argValue) {
		this.reportfolder.setValue(argValue);
	}

	/**
	 * @return value of Property 'defaultdatafileloadinitially'
	 */
	public boolean getDefaultdatafileloadinitially() {
		try {
			return ((org.rapidbeans.core.basic.PropertyBoolean) this.defaultdatafileloadinitially).getValueBoolean();
		} catch (NullPointerException e) {
			throw new org.rapidbeans.core.exception.PropNotInitializedException("defaultdatafileloadinitially");
		}
	}

	/**
	 * setter for Property 'defaultdatafileloadinitially'.
	 * 
	 * @param argValue value of Property 'defaultdatafileloadinitially' to set
	 */
	public void setDefaultdatafileloadinitially(final boolean argValue) {
		this.defaultdatafileloadinitially.setValue(new Boolean(argValue));
	}

	/**
	 * @return value of Property 'workingdepartment'
	 */
	public String getWorkingdepartment() {
		try {
			return (String) this.workingdepartment.getValue();
		} catch (NullPointerException e) {
			throw new org.rapidbeans.core.exception.PropNotInitializedException("workingdepartment");
		}
	}

	/**
	 * setter for Property 'workingdepartment'.
	 * 
	 * @param argValue value of Property 'workingdepartment' to set
	 */
	public void setWorkingdepartment(final String argValue) {
		this.workingdepartment.setValue(argValue);
	}

	/**
	 * @return value of Property 'shareiconsoverweb'
	 */
	public boolean getShareiconsoverweb() {
		try {
			return ((org.rapidbeans.core.basic.PropertyBoolean) this.shareiconsoverweb).getValueBoolean();
		} catch (NullPointerException e) {
			throw new org.rapidbeans.core.exception.PropNotInitializedException("shareiconsoverweb");
		}
	}

	/**
	 * setter for Property 'shareiconsoverweb'.
	 * 
	 * @param argValue value of Property 'shareiconsoverweb' to set
	 */
	public void setShareiconsoverweb(final boolean argValue) {
		this.shareiconsoverweb.setValue(new Boolean(argValue));
	}

	/**
	 * @return value of Property 'pleasedontnag'
	 */
	public boolean getPleasedontnag() {
		try {
			return ((org.rapidbeans.core.basic.PropertyBoolean) this.pleasedontnag).getValueBoolean();
		} catch (NullPointerException e) {
			throw new org.rapidbeans.core.exception.PropNotInitializedException("pleasedontnag");
		}
	}

	/**
	 * setter for Property 'pleasedontnag'.
	 * 
	 * @param argValue value of Property 'pleasedontnag' to set
	 */
	public void setPleasedontnag(final boolean argValue) {
		this.pleasedontnag.setValue(new Boolean(argValue));
	}
}
