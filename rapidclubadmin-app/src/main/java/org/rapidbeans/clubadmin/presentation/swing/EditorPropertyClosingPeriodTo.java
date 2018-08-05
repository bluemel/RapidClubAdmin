/*
 * Rapid Beans Application RapidCLubAdmin: EditorPropertyClosingPeriodTo.java
 *
 * Copyright Martin Bluemel, 2011
 *
 * created 01/16/2011
 */

package org.rapidbeans.clubadmin.presentation.swing;

import org.rapidbeans.core.basic.Property;
import org.rapidbeans.core.exception.ValidationException;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.EditorBean;

/**
 * the bean editor GUI.
 *
 * @author Martin Bluemel
 */
public class EditorPropertyClosingPeriodTo extends org.rapidbeans.presentation.swing.EditorPropertyDateSwing {

	/**
	 * Constructor.
	 *
	 * @param client        the client
	 * @param bizBeanEditor the parent bean editor
	 * @param prop          the bean property to edit
	 * @param propBak       the bean property backup
	 */
	public EditorPropertyClosingPeriodTo(Application client, EditorBean bizBeanEditor, Property prop,
			Property propBak) {
		super(client, bizBeanEditor, prop, propBak);
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
		// "invalid value "Sun Dec 21 00:00:00 CET 200" for property "to" less than
		// property "from" = "Thu Dec 22 00:00:00 CET 2005""
		if (ex.getSignature().endsWith("incomplete")) {
			return super.checkLocalDate(false);
		} else {
			if (ex.getSignature().startsWith("invalid.prop.closingperiod")) {
				return false;
			} else {
				return super.isInputFieldValueCompleted();
			}
		}
	}
}
