package org.uario.seaworkengine.model;

import java.io.Serializable;

public class BillCenter implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private String				description;
	private Integer				id;

	public String getDescription() {
		return this.description;
	}

	public Integer getId() {
		return this.id;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

}
