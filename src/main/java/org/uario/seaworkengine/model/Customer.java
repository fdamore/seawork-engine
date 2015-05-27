package org.uario.seaworkengine.model;

import java.io.Serializable;

public class Customer implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Integer				id;

	private String				name;

	public Integer getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

}
