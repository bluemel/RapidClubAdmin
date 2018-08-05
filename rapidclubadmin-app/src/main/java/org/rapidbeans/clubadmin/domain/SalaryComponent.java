/*
 * Partially generated code file: SalaryComponent.java
 * !!!Do only edit manually in marked sections!!!
 *
 * Rapid Beans bean generator, Copyright Martin Bluemel, 2008
 *
 * generated Java implementation of Rapid Beans bean type
 * org.rapidbeans.clubadmin.domain.SalaryComponent
 * 
 * model:    model/org/rapidbeans/clubadmin/domain/SalaryComponent.xml
 * template: codegentemplates/genBean.xsl
 */

package org.rapidbeans.clubadmin.domain;

// BEGIN manual code section
// SalaryComponent.import
import org.rapidbeans.core.basic.Link;
import org.rapidbeans.core.basic.LinkFrozen;
import org.rapidbeans.core.basic.RapidBean;
import org.rapidbeans.core.basic.RapidBeanImplStrict;
import org.rapidbeans.core.exception.UnresolvedLinkException;
import org.rapidbeans.core.type.TypeRapidBean;

// END manual code section

/**
 * Rapid Bean class: SalaryComponent. Partially generated Java class !!!Do only
 * edit manually in marked sections!!!
 **/
public class SalaryComponent extends RapidBeanImplStrict {

	// BEGIN manual code section
	// SalaryComponent.classBody
	/**
	 * setter for the parent (composite) bean used internally when adding or
	 * removing a bean reference to / from a collection property of type
	 * composition. Also used when validating properties in the editor
	 *
	 * @param newParent the new parent bean
	 */
	public void setParentBean(final RapidBean newParent) {
		super.setParentBean(newParent);
		if (newParent != null) {
			this.setTime(((AbstractSalary) newParent).getTime());
		}
	}
	// END manual code section

	/**
	 * property "salaryComponentType".
	 */
	private org.rapidbeans.core.basic.PropertyAssociationend salaryComponentType;

	/**
	 * property "description".
	 */
	private org.rapidbeans.core.basic.PropertyString description;

	/**
	 * property "money".
	 */
	private org.rapidbeans.core.basic.PropertyQuantity money;

	/**
	 * property "time".
	 */
	private org.rapidbeans.core.basic.PropertyQuantity time;

	/**
	 * property references initialization.
	 */
	public void initProperties() {
		this.salaryComponentType = (org.rapidbeans.core.basic.PropertyAssociationend) this
				.getProperty("salaryComponentType");
		this.description = (org.rapidbeans.core.basic.PropertyString) this.getProperty("description");
		this.money = (org.rapidbeans.core.basic.PropertyQuantity) this.getProperty("money");
		this.time = (org.rapidbeans.core.basic.PropertyQuantity) this.getProperty("time");
	}

	/**
	 * default constructor.
	 */
	public SalaryComponent() {
		super();
		// BEGIN manual code section
		// SalaryComponent.SalaryComponent()
		// END manual code section

	}

	/**
	 * constructor out of a string.
	 * 
	 * @param s the string
	 */
	public SalaryComponent(final String s) {
		super(s);
		// BEGIN manual code section
		// SalaryComponent.SalaryComponent(String)
		// END manual code section

	}

	/**
	 * constructor out of a string array.
	 * 
	 * @param sa the string array
	 */
	public SalaryComponent(final String[] sa) {
		super(sa);
		// BEGIN manual code section
		// SalaryComponent.SalaryComponent(String[])
		// END manual code section
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(SalaryComponent.class);

	/**
	 * @return the Biz Bean's type
	 */
	public TypeRapidBean getType() {
		return type;
	}

	/**
	 * @return value of Property 'salaryComponentType'
	 */
	@SuppressWarnings("unchecked")
	public SalaryComponentType getSalaryComponentType() {
		try {
			org.rapidbeans.core.common.ReadonlyListCollection<SalaryComponentType> col = (org.rapidbeans.core.common.ReadonlyListCollection<SalaryComponentType>) this.salaryComponentType
					.getValue();
			if (col == null || col.size() == 0) {
				return null;
			} else {
				Link link = (Link) col.iterator().next();
				if (link instanceof LinkFrozen) {
					throw new UnresolvedLinkException(
							"unresolved link to \"" + "SalaryComponentType" + "\" \"" + link.getIdString() + "\"");
				} else {
					return (SalaryComponentType) col.iterator().next();
				}
			}
		} catch (NullPointerException e) {
			throw new org.rapidbeans.core.exception.PropNotInitializedException("salaryComponentType");
		}
	}

	/**
	 * setter for Property 'salaryComponentType'.
	 * 
	 * @param argValue value of Property 'salaryComponentType' to set
	 */
	public void setSalaryComponentType(final SalaryComponentType argValue) {
		this.salaryComponentType.setValue(argValue);
	}

	/**
	 * @return value of Property 'description'
	 */
	public String getDescription() {
		try {
			return (String) this.description.getValue();
		} catch (NullPointerException e) {
			throw new org.rapidbeans.core.exception.PropNotInitializedException("description");
		}
	}

	/**
	 * setter for Property 'description'.
	 * 
	 * @param argValue value of Property 'description' to set
	 */
	public void setDescription(final String argValue) {
		this.description.setValue(argValue);
	}

	/**
	 * @return value of Property 'money'
	 */
	public org.rapidbeans.domain.finance.Money getMoney() {
		try {
			return (org.rapidbeans.domain.finance.Money) this.money.getValue();
		} catch (NullPointerException e) {
			throw new org.rapidbeans.core.exception.PropNotInitializedException("money");
		}
	}

	/**
	 * setter for Property 'money'.
	 * 
	 * @param argValue value of Property 'money' to set
	 */
	public void setMoney(final org.rapidbeans.domain.finance.Money argValue) {
		this.money.setValue(argValue);
	}

	/**
	 * @return value of Property 'time'
	 */
	public org.rapidbeans.domain.math.Time getTime() {
		try {
			return (org.rapidbeans.domain.math.Time) this.time.getValue();
		} catch (NullPointerException e) {
			throw new org.rapidbeans.core.exception.PropNotInitializedException("time");
		}
	}

	/**
	 * setter for Property 'time'.
	 * 
	 * @param argValue value of Property 'time' to set
	 */
	public void setTime(final org.rapidbeans.domain.math.Time argValue) {
		this.time.setValue(argValue);
	}
}
