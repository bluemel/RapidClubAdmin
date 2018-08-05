/*
 * EasyBiz Application RapidClubAdmin: DomainException.java
 *
 * Copyright Martin Bluemel, 2006
 *
 * 29.10.2006
 */
package org.rapidbeans.clubadmin.domain;

import org.rapidbeans.core.exception.BusinessLogicException;

/**
 * Business constraint violated exception.
 *
 * @author Martin Bluemel
 */
@SuppressWarnings("serial")
public class RapidClubAdminBusinessLogicException extends BusinessLogicException {

	/**
	 * constructor.
	 *
	 * @param sig     signature
	 * @param message the error message
	 */
	public RapidClubAdminBusinessLogicException(final String sig, final String message) {
		super(sig, message);
	}

	/**
	 * constructor.
	 *
	 * @param sig     signature
	 * @param message the error message
	 * @param args    arguments
	 */
	public RapidClubAdminBusinessLogicException(final String sig, final String message, final Object[] args) {
		super(sig, message, args);
	}
}
