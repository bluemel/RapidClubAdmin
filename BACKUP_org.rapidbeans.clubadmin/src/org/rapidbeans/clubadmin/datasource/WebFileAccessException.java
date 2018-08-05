/*
 * EasyBiz Application RapidClubAdmin: DomainException.java
 *
 * Copyright Martin Bluemel, 2006
 *
 * 29.10.2006
 */
package org.rapidbeans.clubadmin.datasource;

import org.rapidbeans.core.exception.RapidBeansRuntimeException;


/**
 * Business constraint violated exception.
 *
 * @author Martin Bluemel
 */
@SuppressWarnings("serial")
public class WebFileAccessException extends RapidBeansRuntimeException {

    /**
     * constructor.
     *
     * @param sig signature
     * @param message the error message
     */
    public WebFileAccessException(final String message) {
        super(message);
    }
}
