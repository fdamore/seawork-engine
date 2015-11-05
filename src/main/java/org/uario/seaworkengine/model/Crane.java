package org.uario.seaworkengine.model;

import java.io.Serializable;

public class Crane implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private String				description;
	private Integer				id;
	private String				name;
	private Integer				number;

	public String getDescription() {
		return this.description;
	}

	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public Integer getNumber() {
		return this.number;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setNumber(final Integer number) {
		this.number = number;
	}

}
