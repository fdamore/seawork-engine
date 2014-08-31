package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

public class TfrUser implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Integer				id;

	private String				tfr_destination;

	private Date				tfr_selection_date;

	public Integer getId() {
		return this.id;
	}

	public String getTfr_destination() {
		return this.tfr_destination;
	}

	public Date getTfr_selection_date() {
		return this.tfr_selection_date;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setTfr_destination(final String tfr_destination) {
		this.tfr_destination = tfr_destination;
	}

	public void setTfr_selection_date(final Date tfr_selection_date) {
		this.tfr_selection_date = tfr_selection_date;
	}

}