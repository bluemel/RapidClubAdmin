/*
 * RapidClubAdminApplication: SalaryTest.java
 *
 * Copyright Martin Bluemel, 2007
 *
 * 12.08.2007
 */
package org.rapidbeans.clubadmin.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.rapidbeans.domain.finance.Money;
import org.rapidbeans.domain.math.Time;

public class SalaryTest {

	/**
	 * test the comparison operator.
	 */
	@Test
	public void testCompareToSalaryGreaterMagEqualUnit() {
		Salary sal1 = new Salary();
		sal1.setMoney(new Money("1.02 euro"));
		sal1.setTime(new Time("1 h"));
		Salary sal2 = new Salary();
		sal2.setMoney(new Money("1.00 euro"));
		sal2.setTime(new Time("1 h"));
		assertEquals(1, sal1.compareValues(sal2));
	}

	/**
	 * test the comparison operator.
	 */
	@Test
	public void testCompareToSalaryLesserMagEqualUnit() {
		Salary sal1 = new Salary();
		sal1.setMoney(new Money("1 euro"));
		sal1.setTime(new Time("1 h"));
		Salary sal2 = new Salary();
		sal2.setMoney(new Money("2 euro"));
		sal2.setTime(new Time("1 h"));
		assertEquals(-1, sal1.compareValues(sal2));
	}

	/**
	 * test the comparison operator.
	 */
	@Test
	public void testCompareToSalaryEqualMagAndUnit() {
		Salary sal1 = new Salary();
		sal1.setMoney(new Money("1.02 euro"));
		sal1.setTime(new Time("1 h"));
		Salary sal2 = new Salary();
		sal2.setMoney(new Money("1.02 euro"));
		sal2.setTime(new Time("1 h"));
		assertEquals(0, sal1.compareValues(sal2));
	}

	/**
	 * test the comparison operator.
	 */
	@Test
	public void testCompareToSalaryEqualMagDiffUnitEqual() {
		Salary sal1 = new Salary();
		sal1.setMoney(new Money("1.02 euro"));
		sal1.setTime(new Time("1 h"));
		Salary sal2 = new Salary();
		sal2.setMoney(new Money("1.02 euro"));
		sal2.setTime(new Time("60 min"));
		assertEquals(0, sal1.compareValues(sal2));
	}

	/**
	 * test the comparison operator.
	 */
	@Test
	public void testCompareToSalaryEqualMagDiffUnitGreater() {
		Salary sal1 = new Salary();
		sal1.setMoney(new Money("1.02 euro"));
		sal1.setTime(new Time("1 h"));
		Salary sal2 = new Salary();
		sal2.setMoney(new Money("1.02 euro"));
		sal2.setTime(new Time("90 min"));
		assertEquals(1, sal1.compareValues(sal2));
	}

	/**
	 * test the comparison operator.
	 */
	@Test
	public void testCompareToSalaryEqualMagDiffUnitLess() {
		Salary sal1 = new Salary();
		sal1.setMoney(new Money("1.02 euro"));
		sal1.setTime(new Time("1 h"));
		Salary sal2 = new Salary();
		sal2.setMoney(new Money("1.02 euro"));
		sal2.setTime(new Time("45 min"));
		assertEquals(-1, sal1.compareValues(sal2));
	}

	/**
	 * - A component automatically gets the time of the parent salary.
	 */
	@Test
	public void testSalaryAddComponent() {
		Salary sal = new Salary();
		sal.setMoney(new Money("7 euro"));
		sal.setTime(new Time("30 min"));
		SalaryComponent sc1 = new SalaryComponent();
		sal.addComponent(sc1);
		assertEquals(new Time("30 min"), sc1.getTime());
	}

	/**
	 * - The composite Salary automatically adapts to the sum of the components
	 * salaries.
	 */
	@Test
	public void testSalaryWithComponentsChangeComponentMoney() {
		Salary sal = new Salary();
		sal.setMoney(new Money("7 euro"));
		sal.setTime(new Time("30 min"));
		assertEquals("7 euro", sal.getMoney().toString());

		SalaryComponent sc1 = new SalaryComponent();
		SalaryComponent sc2 = new SalaryComponent();
		SalaryComponent sc3 = new SalaryComponent();

		sc1.setMoney(new Money("4 euro"));
		sal.addComponent(sc1);
		assertEquals("4 euro", sal.getMoney().toString());
		sc2.setMoney(new Money("2 euro"));
		sal.addComponent(sc2);
		assertEquals("6 euro", sal.getMoney().toString());
		sc3.setMoney(new Money("1 euro"));
		sal.addComponent(sc3);
		assertEquals("7 euro", sal.getMoney().toString());
		sc2.setMoney(new Money("1 euro"));
		assertEquals("6 euro", sal.getMoney().toString());
		sal.removeComponent(sc1);
		assertEquals("2 euro", sal.getMoney().toString());
	}

	/**
	 * - All components automatically adapt their time if the composite Salary
	 * changes its time.
	 */
	@Test
	public void testSalaryWithComponentsChangeCompositeTime() {
		Salary sal = new Salary();
		sal.setMoney(new Money("7 euro"));
		sal.setTime(new Time("31 min"));
		assertEquals("7 euro", sal.getMoney().toString());

		SalaryComponent sc1 = new SalaryComponent();
		SalaryComponent sc2 = new SalaryComponent();
		SalaryComponent sc3 = new SalaryComponent();

		sc1.setMoney(new Money("4 euro"));
		sal.addComponent(sc1);
		assertEquals(new Time("31 min"), sc1.getTime());
		sc2.setMoney(new Money("2 euro"));
		sal.addComponent(sc2);
		assertEquals(new Time("31 min"), sc2.getTime());
		sc3.setMoney(new Money("1 euro"));
		sal.addComponent(sc3);
		assertEquals(new Time("31 min"), sc3.getTime());

		sal.setTime(new Time("30 min"));
		assertEquals(new Time("30 min"), sal.getTime());
		assertEquals(new Time("30 min"), sc1.getTime());
		assertEquals(new Time("30 min"), sc2.getTime());
		assertEquals(new Time("30 min"), sc3.getTime());
	}
}
