/*
 * EasyBiz Application RapidClubAdmin: RapidClubAdminApplet.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 04.09.2007
 */

package org.rapidbeans.clubadmin;

import java.util.Properties;
import java.util.logging.Logger;

import javax.swing.JApplet;

import org.rapidbeans.presentation.Appl;
import org.rapidbeans.presentation.ApplicationManager;


/**
 * The main class.
 *
 * @author Martin Bluemel
 */
public final class RapidClubAdminApplet extends JApplet
    implements Appl {

    /**
     * Hopefully never have to change this serial version UID.
     */
    private static final long serialVersionUID = 1L;

    private static final Logger log = Logger.getLogger(
            RapidClubAdminApplet.class.getName()); 

    /**
     * main method for this application.
     *
     * @param args arguments
     */
    public void start() {
        super.start();
        log.info("start of RapidClubAdminApplet: CLASSPATH = " + System.getProperty("java.class.path"));
        ApplicationManager.start(RapidClubAdminApplet.class, "Application.xml", this);
    }

    /**
     * standard constructor.
     */
    public RapidClubAdminApplet() {
        super();
    }

    private Properties options = new Properties();

    public Properties getOptions() {
        return this.options;
    }
}
