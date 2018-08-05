/*
 * Rapid Beans Framework: EnablerOpenCurrentBillingPeriod.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * August 16, 2007
 */
package org.rapidbeans.clubadmin.presentation.enabler;

import org.rapidbeans.clubadmin.domain.BillingPeriod;
import org.rapidbeans.clubadmin.domain.MasterData;
import org.rapidbeans.clubadmin.presentation.RapidClubAdminClient;
import org.rapidbeans.presentation.ApplicationManager;

/**
 * enables / disables the view overview items.
 *
 * @author Martin Bluemel
 */
public class EnablerOpenCurrentBillingPeriod extends EnablerView {

	/**
	 * the enabling method of every enabler.
	 *
	 * @return if the menu is enable or not.
	 */
	public boolean getEnabled() {
		final MasterData md = ((RapidClubAdminClient) ApplicationManager.getApplication()).getMasterData();
		final BillingPeriod currentBp = md.getCurrentbillingperiod();
		if (currentBp == null) {
			return false;
		}
		return true;
	}
}
