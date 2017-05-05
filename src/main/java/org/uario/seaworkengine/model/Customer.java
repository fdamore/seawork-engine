package org.uario.seaworkengine.model;

import java.io.Serializable;

public class Customer implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Integer				id;

	private String				name;
	
	private String				note;

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Customer)) {
			return false;
		}

		final Customer custo_itm = (Customer) obj;

		return this.getId().equals(custo_itm.getId());

	}

	public Integer getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getNote() {
		return this.note;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String toString() {
		return this.getName();
	}

}
