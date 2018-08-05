/*
 * Rapid Beans Framework: EditorPropertySalaryMoney.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * created 20.03.2008
 */

package org.rapidbeans.clubadmin.presentation.swing;

import java.math.BigDecimal;

import org.rapidbeans.clubadmin.domain.SalaryComponent;
import org.rapidbeans.core.basic.Property;
import org.rapidbeans.domain.finance.Currency;
import org.rapidbeans.domain.finance.Money;
import org.rapidbeans.presentation.Application;
import org.rapidbeans.presentation.EditorBean;
import org.rapidbeans.presentation.swing.EditorPropertyMoneySwing;

/**
 * the bean editor GUI.
 *
 * @author Martin Bluemel
 */
public class EditorPropertySalaryComponentMoney extends EditorPropertyMoneySwing {

	/**
	 * Constructor.
	 *
	 * @param client        the client
	 * @param bizBeanEditor the parent bean editor
	 * @param prop          the bean property to edit
	 * @param propBak       the bean property backup
	 */
	public EditorPropertySalaryComponentMoney(Application client, EditorBean bizBeanEditor, Property prop,
			Property propBak) {
		super(client, bizBeanEditor, prop, propBak);
	}

	/**
	 * Adapt the SalaryComponent's money currency according to the future parent
	 * Salary's currency.
	 */
	protected void updateScMoney() {
		if (this.getBeanEditor().isInNewMode()) {
			final SalaryComponent sc = (SalaryComponent) this.getBeanEditor().getBean();
			final Money newMoney = new Money(BigDecimal.ZERO, Currency.euro);
			sc.setMoney(newMoney);
			updateUI();
		}
	}
}
