/*
 * RapidBeans Application RapidClubAdmin: EditorUser.java
 *
 * Copyright Martin Bluemel, 2009
 *
 * 26.02.2009
 */
package org.rapidbeans.clubadmin.presentation.swing;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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
import org.rapidbeans.presentation.EditorProperty;
import org.rapidbeans.presentation.EditorPropertyListener;
import org.rapidbeans.presentation.swing.EditorBeanSwing;

/**
 * Extends the standard bean editor.
 * 
 * @author Martin Bluemel
 */
public class EditorUser extends EditorBeanSwing {

    private JButton trainerButton = new JButton("Trainereigenschaften ...");

    /**
     * @param client
     *            the client
     * @param docView
     *            the document view
     * @param bean
     *            the bean
     * @param newBeanParentColProp
     *            the parent collection property
     */
    public EditorUser(final Application client, final DocumentView docView, final RapidBean bean,
            final PropertyCollection newBeanParentColProp) {
        super(client, docView, bean, newBeanParentColProp);
        trainerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editTrainer();
            }
        });
        if (getPropEditor("isalsotrainer") != null) {
            final JPanel extendedPropEditorIsalsotrainerPanel = new JPanel();
            extendedPropEditorIsalsotrainerPanel.setLayout(new GridBagLayout());
            extendedPropEditorIsalsotrainerPanel.add((Component) getPropEditor("isalsotrainer").getWidget(),
                    new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST,
                            GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 0, 0));
            extendedPropEditorIsalsotrainerPanel.add(trainerButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 5, 5, 5), 0, 0));
            final JPanel propsPanel = this.getPanelProps();
            propsPanel.remove((Component) getPropEditor("isalsotrainer").getWidget());
            propsPanel.add(extendedPropEditorIsalsotrainerPanel, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
            getPropEditor("isalsotrainer").addPropertyEditorListener(new EditorPropertyListener() {
                @Override
                public void inputFieldChanged(EditorProperty propEditor) {
                    updatePropEditorIsalsotrainer();
                }
            });
            updatePropEditorIsalsotrainer();
        }
    }

    private void updatePropEditorIsalsotrainer() {
        if (((ClubadminUser) this.getBean()).getIsalsotrainer() != null) {
            this.trainerButton.setVisible(true);
        } else {
            this.trainerButton.setVisible(false);
        }
    }

    private void editTrainer() {
        final Trainer trainer = ((ClubadminUser) this.getBean()).getIsalsotrainer();
        this.getDocumentView().editBean(trainer);
    }
}
