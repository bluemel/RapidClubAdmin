/*
 * RapidBeans Application RapidClubAdmin: EditorUser.java
 *
 * Copyright Martin Bluemel, 2009
 *
 * 26.02.2009
 */
package org.rapidbeans.clubadmin.presentation.swing;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.rapidbeans.clubadmin.domain.ClubadminUser;
import org.rapidbeans.clubadmin.domain.Trainer;
import org.rapidbeans.core.basic.PropertyCollection;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.DocumentView;
import org.rapidbeans.presentation.swing.EditorBeanSwing;


/**
 * Extends the standard bean editor.
 *
 * @author Martin Bluemel
 */
public class EditorUser extends EditorBeanSwing {

    /**
     * @param client the client
     * @param docView the document view
     * @param bean the bean
     * @param newBeanParentColProp the parent collection property
     */
    public EditorUser(final Application client,
            final DocumentView docView,
            final RapidBean bean,
            final PropertyCollection newBeanParentColProp) {
        super(client, docView, bean, newBeanParentColProp);
        final ClubadminUser user = (ClubadminUser) bean;
        if (user.getIsalsotrainer() != null) {
            final JPanel panel = this.getPanelProps();
            final JButton trainerButton =
                new JButton("Trainereigenschaften ...");
            trainerButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    editTrainer();
                }
            });
            panel.add(trainerButton, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.NONE,
                    new Insets(5, 5, 5, 5), 0, 0));
        }
    }

    private void editTrainer() {
        final Trainer trainer = ((ClubadminUser) this.getBean()).getIsalsotrainer();
        this.getDocumentView().editBean(trainer);
    }
}
