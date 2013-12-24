/*
 * Partially generated code file: Settings.java
 * !!!Do only edit manually in marked sections!!!
 *
 * Rapid Beans bean generator, Copyright Martin Bluemel, 2008
 *
 * generated Java implementation of Rapid Beans bean type
 * org.rapidbeans.clubadmin.presentation.Settings
 * 
 * model:    model/org/rapidbeans/clubadmin/presentation/Settings.xml
 * template: codegentemplates/genBean.xsl
 */

package org.rapidbeans.clubadmin.presentation;



// BEGIN manual code section
// Settings.import
import org.rapidbeans.core.basic.Link;
import org.rapidbeans.core.basic.LinkFrozen;
import org.rapidbeans.core.exception.UnresolvedLinkException;
import org.rapidbeans.core.type.TypeRapidBean;

// END manual code section


/**
 * Rapid Bean class: Settings.
 * Partially generated Java class
 * !!!Do only edit manually in marked sections!!!
 **/
public class Settings extends org.rapidbeans.presentation.settings.SettingsAll {

    // BEGIN manual code section
    // Settings.classBody
    // END manual code section


    /**
     * property "settings".
     */
    private org.rapidbeans.core.basic.PropertyAssociationend settings;

    /**
     * property "basic".
     */
    private org.rapidbeans.core.basic.PropertyAssociationend basic;

    /**
     * property references initialization.
     */
    public void initProperties() {
        super.initProperties();
        this.settings = (org.rapidbeans.core.basic.PropertyAssociationend)
            this.getProperty("settings");
        this.basic = (org.rapidbeans.core.basic.PropertyAssociationend)
            this.getProperty("basic");
    }



    /**
     * default constructor.
     */
    public Settings() {
        super();
        // BEGIN manual code section
        // Settings.Settings()
        // END manual code section

    }

    /**
     * constructor out of a string.
     * @param s the string
     */
    public Settings(final String s) {
        super(s);
        // BEGIN manual code section
        // Settings.Settings(String)
        // END manual code section

    }

    /**
     * constructor out of a string array.
     * @param sa the string array
     */
    public Settings(final String[] sa) {
        super(sa);
        // BEGIN manual code section
        // Settings.Settings(String[])
        // END manual code section
    }

    /**
     * the bean's type (class variable).
     */
    private static TypeRapidBean type = TypeRapidBean.createInstance(Settings.class);
	

    /**
     * @return the Biz Bean's type
     */
    public TypeRapidBean getType() {
        return type;
    }

    /**
     * @return value of Property 'settings'
     */
    @SuppressWarnings("unchecked")
    public org.rapidbeans.clubadmin.presentation.RapidClubAdminSettings getSettings() {
        try {
            org.rapidbeans.core.common.ReadonlyListCollection<org.rapidbeans.clubadmin.presentation.RapidClubAdminSettings> col
                = (org.rapidbeans.core.common.ReadonlyListCollection<org.rapidbeans.clubadmin.presentation.RapidClubAdminSettings>) this.settings.getValue();
            if (col == null || col.size() == 0) {
                return null;
            } else {
                Link link = (Link) col.iterator().next();
                if (link instanceof LinkFrozen) {
                    throw new UnresolvedLinkException("unresolved link to \""
                            + "org.rapidbeans.clubadmin.presentation.RapidClubAdminSettings"
                            + "\" \"" + link.getIdString() + "\"");
                } else {
                    return (org.rapidbeans.clubadmin.presentation.RapidClubAdminSettings) col.iterator().next();
                }
            }
        } catch (NullPointerException e) {
            throw new org.rapidbeans.core.exception.PropNotInitializedException("settings");
        }
    }

    /**
     * setter for Property 'settings'.
     * @param argValue value of Property 'settings' to set
     */
    public void setSettings(
        final org.rapidbeans.clubadmin.presentation.RapidClubAdminSettings argValue) {
        this.settings.setValue(argValue);
    }

    /**
     * @return value of Property 'basic'
     */
    @SuppressWarnings("unchecked")
    public org.rapidbeans.clubadmin.presentation.SettingsBasic getBasic() {
        try {
            org.rapidbeans.core.common.ReadonlyListCollection<org.rapidbeans.clubadmin.presentation.SettingsBasic> col
                = (org.rapidbeans.core.common.ReadonlyListCollection<org.rapidbeans.clubadmin.presentation.SettingsBasic>) this.basic.getValue();
            if (col == null || col.size() == 0) {
                return null;
            } else {
                Link link = (Link) col.iterator().next();
                if (link instanceof LinkFrozen) {
                    throw new UnresolvedLinkException("unresolved link to \""
                            + "org.rapidbeans.clubadmin.presentation.SettingsBasic"
                            + "\" \"" + link.getIdString() + "\"");
                } else {
                    return (org.rapidbeans.clubadmin.presentation.SettingsBasic) col.iterator().next();
                }
            }
        } catch (NullPointerException e) {
            throw new org.rapidbeans.core.exception.PropNotInitializedException("basic");
        }
    }

    /**
     * setter for Property 'basic'.
     * @param argValue value of Property 'basic' to set
     */
    public void setBasic(
        final org.rapidbeans.clubadmin.presentation.SettingsBasic argValue) {
        this.basic.setValue(argValue);
    }
}
