/*
 * RapidBeans Application RapidClubAdmin: CreditInstitute
 *
 * Copyright Martin Bluemel, 2008
 *
 * 13.12.2008
 */

package org.rapidbeans.clubadmin.domain;

import org.rapidbeans.core.type.TypeRapidBean;

/**
 * Specific operations of RapidBeans class CreditInstitute.
 *
 * @author Martin Bluemel
 */
public class CreditInstitute extends RapidBeanBaseCreditInstitute {

//    /**
//     * prevent deletion if in a document and trainers are associated.
//     */
//    public void delete() {
//        final Document doc = this.getContainer();
//        if (doc != null) {
//            // since we currently have no navigation from CreditInstitutes to
//            // trainers we use a query to find out the trainer using
//            // this credit institute
//            List<RapidBean> usingTrainers = doc.findBeansByQuery("org.rapidbeans.clubadmin.domain.Trainer"
//                    + "[creditinstitute[name='" + this.getName() + "']]");
//            final int usingTrainersNumber = usingTrainers.size();
//            if (usingTrainersNumber > 0) {
//                final StringBuffer sb1 = new StringBuffer();
//                final StringBuffer sb2 = new StringBuffer();
//                for (int i = 0; i < usingTrainersNumber && i <= 5; i++) {
//                    final Trainer tr = (Trainer) usingTrainers.get(i);
//                    if (i > 0) {
//                        sb1.append(", ");
//                        sb2.append(",\n");
//                    }
//                    if (i == 5 && usingTrainersNumber > 5) {
//                        sb1.append("...");
//                        sb2.append("...");
//                    } else {
//                        sb1.append(tr.getIdString());
//                        if (ApplicationManager.getClient() != null) {
//                            sb2.append(tr.getStringGuiId(
//                                    ApplicationManager.getClient().getLocaleManager().getCurrent()));
//                        } else {
//                            sb2.append(tr.getIdString());
//                        }
//                    }
//                }
//                Object[] args = {this.getName(), sb2.toString()};
//                switch (usingTrainers.size()) {
//                case 1:
//                    throw new RapidClubAdminBusinessLogicException(
//                            "error.bizrule.creditinstitute.del.used.one",
//                            "Can't delete credit institute \""
//                            + this.getIdString() + ": "
//                            + "It is used by trainer "
//                            + sb1.toString(), args);
//                default:
//                    throw new RapidClubAdminBusinessLogicException(
//                            "error.bizrule.creditinstitute.del.used.more",
//                            "Can't delete credit institute \""
//                            + this.getIdString() + ": "
//                            + "It is used by the following trainers: "
//                            + sb1.toString(), args);
//                }
//            }
//        }
//        super.delete();
//    }

	/**
	 * default constructor.
	 */
	public CreditInstitute() {
		super();
	}

	/**
	 * constructor out of a string.
	 * 
	 * @param s the string
	 */
	public CreditInstitute(final String s) {
		super(s);
	}

	/**
	 * constructor out of a string array.
	 * 
	 * @param sa the string array
	 */
	public CreditInstitute(final String[] sa) {
		super(sa);
	}

	/**
	 * the bean's type (class variable).
	 */
	private static TypeRapidBean type = TypeRapidBean.createInstance(CreditInstitute.class);

	/**
	 * @return the bean's type
	 */
	public TypeRapidBean getType() {
		return type;
	}
}
