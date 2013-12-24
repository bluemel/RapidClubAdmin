/*
 * RapidBeans Application RapidClubAdmin: RapidClubAdmin.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 08.12.2005
 */

package org.rapidbeans.clubadmin;

import java.io.File;
import java.util.List;
import java.util.Properties;

import org.rapidbeans.core.util.StringHelper;
import org.rapidbeans.datasource.Document;
import org.rapidbeans.presentation.ApplicationManager;


/**
 * The main class.
 *
 * @author Martin Bluemel
 */
// TODO RapidClubAdmin 22) restore for billing periods
// TODO RapidClubAdmin 50) prevent new training(s) to be created on wrong dates in expert view
// TODO RapidClubAdmin 51) prevent training(s) to be deleted in expert view
public final class RapidClubAdmin {

    /**
     * main method for this application.
     *
     * @param args arguments
     */
    public static void main(final String[] args) {
        final org.rapidbeans.clubadmin.presentation.RapidClubAdminClient appl =
            new org.rapidbeans.clubadmin.presentation.RapidClubAdminClient();
        final Properties options = appl.getOptions();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-server")) {
                options.put("server", args[i + 1]);
                i++;
            } else if (args[i].equals("-root")) {
                options.put("root", args[i + 1]);
                i++;
            } else if (args[i].equals("-loaddocuments")) {
                options.put("loaddocuments", args[i + 1]);
                i++;
            }
        }
        ApplicationManager.start(RapidClubAdmin.class, "Application.xml", appl);
        if (options.get("loaddocuments") != null) {
            final List<String> docstoloada = StringHelper.split((String) options.get("loaddocuments"), File.pathSeparator);
            for (String docfilename : docstoloada) {
                final File docfile = new File(docfilename);
                final Document doc = new Document(docfile);
                ApplicationManager.getApplication().openDocumentView(
                        doc, "trainingslist", "trainings");
            }
        }
    }
}
