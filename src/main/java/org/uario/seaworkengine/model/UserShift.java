package org.uario.seaworkengine.model;

import java.io.Serializable;

public class UserShift implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private String				code;

	private String				description;

	private Boolean				forceable;

	private Integer				id;

	private Boolean				presence;

	public String getCode() {
		return this.code;
	}

	public String getDescription() {
		return this.description;
	}

	public Boolean getForceable() {
		return this.forceable;
	}

	public Integer getId() {
		return this.id;
	}

	public Boolean getPresence() {
		return this.presence;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setForceable(final Boolean forceable) {
		this.forceable = forceable;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setPresence(final Boolean presence) {
		this.presence = presence;

	}

	@Override
	public String toString() {
		return "" + this.getCode() + " - " + this.getDescription();
	}

}