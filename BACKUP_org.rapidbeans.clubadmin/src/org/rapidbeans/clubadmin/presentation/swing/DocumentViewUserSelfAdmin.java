/*
 * RapidBeans Application RapidClubAdmin: DocumentViewUserSelfAdmin.java
 *
 * Copyright Martin Bluemel, 2009
 *
 * 03.02.2009
 */
package org.rapidbeans.clubadmin.presentation.swing;

import org.rapidbeans.datasource.Document;
import org.rapidbeans.datasource.Filter;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.EditorBean;
import org.rapidbeans.presentation.swing.DocumentViewSwing;

/**
 * @author Martin Bluemel
 */
public class DocumentViewUserSelfAdmin extends DocumentViewSwing {

    /**
     * constructor.
     *
     * @param client the client
     * @param doc the document to show
     * @param docconfname the view's document configuration name
     * @param viewconfname the view's configuration name
     * @param filter the filter
     */
    public DocumentViewUserSelfAdmin(final Application client, final Document doc,
            final String docconfname, final String viewconfname,
            final Filter filter) {
        super(client, doc, docconfname, viewconfname, filter);
    }

    /**
     * If the (one and only) bean editor for user self admin
     * is closed automatically also close this surrounding
     * document view.
     *
     * @param editor the editor to close
     */
    public void editorClosed(final EditorBean editor) {
        super.editorClosed(editor);
        if (this.getOpenEditorsNumber() == 0) {
            this.close();
        }
    }
}
