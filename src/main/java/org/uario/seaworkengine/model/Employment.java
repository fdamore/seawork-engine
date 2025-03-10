package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

public class Employment implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Integer				contractual_level;

	private Date				date_end;

	private Date				date_modified;

	private Integer				id;

	private Integer				id_user;

	private String				note;

	private String				status;

	public Integer getContractual_level() {
		return this.contractual_level;
	}

	public Date getDate_end() {
		return this.date_end;
	}

	public Date getDate_modified() {
		return this.date_modified;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getId_user() {
		return this.id_user;
	}

	public String getNote() {
		return this.note;
	}

	public String getStatus() {
		return this.status;
	}

	public void setContractual_level(final Integer contractual_level) {
		this.contractual_level = contractual_level;
	}

	public void setDate_end(final Date date_end) {
		this.date_end = date_end;
	}

	public void setDate_modified(final Date date_modified) {
		this.date_modified = date_modified;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setId_user(final Integer id_user) {
		this.id_user = id_user;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

}
