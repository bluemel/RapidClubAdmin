/*
 * RapidBeans Application RapidClubAdmin: ExportJobResultEntry.java
 *
 * Copyright Martin Bluemel, 2008
 *
 * 18.07.2008
 */
package org.rapidbeans.clubadmin.domain.export;

import java.util.HashMap;
import java.util.Map;

import org.rapidbeans.core.exception.RapidBeansRuntimeException;

/**
 * DB export business logic. Exports to MS Access VERDAT.mdb.
 * 
 * @author Martin Bluemel
 */
public final class ExportJobResultEntry {

	private ExportJobResultEntryModificationType modificationType = null;

	private String entityType = null;

	private String entityId = null;

	private Map<String, String> attributes = new HashMap<String, String>();

	public ExportJobResultEntry(final ExportJobResultEntryModificationType modtype, final String enType,
			final String enId) {
		this.modificationType = modtype;
		this.entityType = enType;
		this.entityId = enId;
	}

	/**
	 * @return the modificationType
	 */
	public ExportJobResultEntryModificationType getModificationType() {
		return modificationType;
	}

	/**
	 * @return the attributes
	 */
	public Map<String, String> getAttributes() {
		return attributes;
	}

	public String getAttributeValue(final String name) {
		return this.attributes.get(name);
	}

	public void addAttribute(final String name, final String value) {
		if (this.attributes.get(name) != null) {
			throw new RapidBeansRuntimeException("Attribute \"" + name + "\" is already added.");
		}
		this.attributes.put(name, value);
	}

	/**
	 * @return the entityType
	 */
	public String getEntityType() {
		return entityType;
	}

	/**
	 * @return the entityId
	 */
	public String getEntityId() {
		return entityId;
	}
}
