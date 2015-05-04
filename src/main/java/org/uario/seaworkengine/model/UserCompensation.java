package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

public class UserCompensation implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Date				date_submit;

	private Integer				id;

	private Integer				id_user;

	private Double				time_comp;

	public Date getDate_submit() {
		return this.date_submit;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getId_user() {
		return this.id_user;
	}

	public Double getTime_comp() {
		return this.time_comp;
	}

	public void setDate_submit(final Date date_submit) {
		this.date_submit = date_submit;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setId_user(final Integer id_user) {
		this.id_user = id_user;
	}

	public void setTime_comp(final Double time_comp) {
		this.time_comp = time_comp;
	}

}
