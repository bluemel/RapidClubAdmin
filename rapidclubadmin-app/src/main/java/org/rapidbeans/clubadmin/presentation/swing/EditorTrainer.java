/*
 * RapidBeans Application RapidClubAdmin: EditorTrainer.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 07.10.2007
 */
package org.rapidbeans.clubadmin.presentation.swing;

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
public class EditorTrainer extends EditorBeanSwing {

	/**
	 * @param client               the client
	 * @param docView              the document view
	 * @param bizBean              the bean
	 * @param newBeanParentColProp the parent collection property
	 */
	public EditorTrainer(final Application client, final DocumentView docView, final RapidBean bizBean,
			final PropertyCollection newBeanParentColProp) {
		super(client, docView, bizBean, newBeanParentColProp);
	}
}
