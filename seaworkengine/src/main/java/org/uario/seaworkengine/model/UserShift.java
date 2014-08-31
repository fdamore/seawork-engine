package org.uario.seaworkengine.model;

import java.io.Serializable;

public class UserShift implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private String				code;

	private String				description;

	private Integer				id;

	private String				us_type;

	public String getCode() {
		return this.code;
	}

	public String getDescription() {
		return this.description;
	}

	public Integer getId() {
		return this.id;
	}

	public String getUs_type() {
		return this.us_type;
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

	public void setUs_type(final String us_type) {
		this.us_type = us_type;

	}

	@Override
	public String toString() {
		return this.getCode();
	}

}