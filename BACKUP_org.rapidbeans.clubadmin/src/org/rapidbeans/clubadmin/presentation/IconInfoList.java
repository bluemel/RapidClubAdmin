/*
 * Partially generated code file: IconInfoList.java
 * !!!Do only edit manually in marked sections!!!
 *
 * Rapid Beans bean generator, Copyright Martin Bluemel, 2008
 *
 * generated Java implementation of Rapid Beans bean type
 * org.rapidbeans.clubadmin.presentation.IconInfoList
 * 
 * model:    model/org/rapidbeans/clubadmin/presentation/IconInfoList.xml
 * template: codegentemplates/genBean.xsl
 */

package org.rapidbeans.clubadmin.presentation;



// BEGIN manual code section
// IconInfoList.import
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.Logger;

import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.core.basic.RapidBeanImplStrict;
import org.rapidbeans.core.type.TypeRapidBean;

// END manual code section


/**
 * Rapid Bean class: IconInfoList.
 * Partially generated Java class
 * !!!Do only edit manually in marked sections!!!
 **/
public class IconInfoList extends RapidBeanImplStrict {

    // BEGIN manual code section
    // IconInfoList.classBody
    private static final Logger log = Logger.getLogger(
            IconInfoList.class.getName()); 

    private static DateFormat logDateFormat = DateFormat.getDateTimeInstance(
            DateFormat.MEDIUM, DateFormat.MEDIUM);

    public void markIconUpdate(final Trainer trainer) {
        log.fine("markIconUpdate: \"" + trainer.getIdString() + "\"");
        IconInfo info = this.getIconInfo(trainer);
         if (info == null) {
            info = new IconInfo(new String[]{trainer.getIdString()});
            this.addIcon(info);
            log.fine("markIconUpdate: created new info");
        } else {
            log.fine("markIconUpdate: found info, last upload: " +
                    logDateFormat.format(info.getLastupload()) + "\"");
        }
        info.setLastupload(new Date());
        log.fine("markIconUpdate: updated info, last upload: " +
                logDateFormat.format(info.getLastupload()));
    }

    public IconInfo getIconInfo(final Trainer trainer) {
        if (this.getIcons() != null) {
            for (IconInfo info : this.getIcons()) {
                if (info.getTrainerid().equals(trainer.getIdString())) {
                    return info;
                }
            }
        }
        return null;
    }
    // END manual code section


    /**
     * property "icons".
     */
    private org.rapidbeans.core.basic.PropertyAssociationend icons;

    /**
     * property references initialization.
     */
    public void initProperties() {
        this.icons = (org.rapidbeans.core.basic.PropertyAssociationend)
            this.getProperty("icons");
    }



    /**
     * default constructor.
     */
    public IconInfoList() {
        super();
        // BEGIN manual code section
        // IconInfoList.IconInfoList()
        // END manual code section

    }

    /**
     * constructor out of a string.
     * @param s the string
     */
    public IconInfoList(final String s) {
        super(s);
        // BEGIN manual code section
        // IconInfoList.IconInfoList(String)
        // END manual code section

    }

    /**
     * constructor out of a string array.
     * @param sa the string array
     */
    public IconInfoList(final String[] sa) {
        super(sa);
        // BEGIN manual code section
        // IconInfoList.IconInfoList(String[])
        // END manual code section
    }

    /**
     * the bean's type (class variable).
     */
    private static TypeRapidBean type = TypeRapidBean.createInstance(IconInfoList.class);
	

    /**
     * @return the Biz Bean's type
     */
    public TypeRapidBean getType() {
        return type;
    }

    /**
     * @return value of Property 'icons'
     */
    @SuppressWarnings("unchecked")
    public org.rapidbeans.core.common.ReadonlyListCollection<org.rapidbeans.clubadmin.presentation.IconInfo> getIcons() {
        try {
            return (org.rapidbeans.core.common.ReadonlyListCollection<org.rapidbeans.clubadmin.presentation.IconInfo>)
            this.icons.getValue();
        } catch (NullPointerException e) {
            throw new org.rapidbeans.core.exception.PropNotInitializedException("icons");
        }
    }

    /**
     * setter for Property 'icons'.
     * @param argValue value of Property 'icons' to set
     */
    public void setIcons(
        final java.util.Collection<org.rapidbeans.clubadmin.presentation.IconInfo> argValue) {
        this.icons.setValue(argValue);
    }

    /**
     * add method for Property 'icons'.
     * @param bean the bean to add
     */
    public void addIcon(final org.rapidbeans.clubadmin.presentation.IconInfo bean) {
        ((org.rapidbeans.core.basic.PropertyCollection) this.icons).addLink(bean);
    }

    /**
     * remove method for Property 'icons'.
     * @param bean the bean to add
     */
    public void removeIcon(final org.rapidbeans.clubadmin.presentation.IconInfo bean) {
        ((org.rapidbeans.core.basic.PropertyCollection) this.icons).removeLink(bean);
    }
}
