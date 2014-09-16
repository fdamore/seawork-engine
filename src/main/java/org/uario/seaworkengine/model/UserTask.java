package org.uario.seaworkengine.model;

import java.io.Serializable;

public class UserTask implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private String				code;

	private String				description;

	private Integer				id;

	public String getCode() {
		return this.code;
	}

	public String getDescription() {
		return this.description;
	}

	public Integer getId() {
		return this.id;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "" + this.getCode() + " - " + this.getDescription();
	}

}