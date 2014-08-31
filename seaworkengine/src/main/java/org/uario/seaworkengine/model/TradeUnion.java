package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

public class TradeUnion implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Date				cancellation;

	private Integer				id;

	private String				name;

	private String				note;

	private Date				registration;

	public Date getCancellation() {
		return this.cancellation;
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

	public Date getRegistration() {
		return this.registration;
	}

	public void setCancellation(final Date cancellation) {
		this.cancellation = cancellation;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setRegistration(final Date registration) {
		this.registration = registration;
	}

}