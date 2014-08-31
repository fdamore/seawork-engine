package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

public class FiscalControl implements Serializable {

	/**
	 * sss
	 */
	private static final long	serialVersionUID	= 1L;

	private Date				control_date;

	private Integer				id;

	private String				note;

	private Date				request_date;

	private String				result;

	private String				result_comunication_type;

	private String				sede_inps;

	private Date				sikness_from;

	private Date				sikness_to;

	public Date getControl_date() {
		return this.control_date;
	}

	public Integer getId() {
		return this.id;
	}

	public String getNote() {
		return this.note;
	}

	public Date getRequest_date() {
		return this.request_date;
	}

	public String getResult() {
		return this.result;
	}

	public String getResult_comunication_type() {
		return this.result_comunication_type;
	}

	public String getSede_inps() {
		return this.sede_inps;
	}

	public Date getSikness_from() {
		return this.sikness_from;
	}

	public Date getSikness_to() {
		return this.sikness_to;
	}

	public void setControl_date(final Date control_date) {
		this.control_date = control_date;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setRequest_date(final Date request_date) {
		this.request_date = request_date;
	}

	public void setResult(final String result) {
		this.result = result;
	}

	public void setResult_comunication_type(final String result_comunication_type) {
		this.result_comunication_type = result_comunication_type;
	}

	public void setSede_inps(final String sede_inps) {
		this.sede_inps = sede_inps;
	}

	public void setSikness_from(final Date sikness_from) {
		this.sikness_from = sikness_from;
	}

	public void setSikness_to(final Date sikness_to) {
		this.sikness_to = sikness_to;
	}

}