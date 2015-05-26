package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

public class MedicalExamination implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private Date date_examination;
	private Integer id;
	private Integer iduser;
	private Date next_date_examination;
	private String note_examination;
	private String prescriptions;
	private String result_examination;

	public Date getDate_examination() {
		return this.date_examination;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getIduser() {
		return this.iduser;
	}

	public Date getNext_date_examination() {
		return this.next_date_examination;
	}

	public String getNote_examination() {
		return this.note_examination;
	}

	public String getPrescriptions() {
		return this.prescriptions;
	}

	public String getResult_examination() {
		return this.result_examination;
	}

	public void setDate_examination(final Date date_examination) {
		this.date_examination = date_examination;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setIduser(final Integer iduser) {
		this.iduser = iduser;
	}

	public void setNext_date_examination(final Date next_date_examination) {
		this.next_date_examination = next_date_examination;
	}

	public void setNote_examination(final String note_examination) {
		this.note_examination = note_examination;
	}

	public void setPrescriptions(final String prescriptions) {
		this.prescriptions = prescriptions;
	}

	public void setResult_examination(final String result_examination) {
		this.result_examination = result_examination;
	}

}
