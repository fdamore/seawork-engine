package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

public class Contestation implements Comparable<Contestation>, Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Date				date_bp;

	private Date				date_contestation;

	private Date				date_penalty;

	private String				description;

	private String				file_name;

	private int					id;

	private int					id_user;

	private String				note;

	private String				prot;

	private String				prot_penalty;

	private Boolean				recall;

	private Date				stop_from;

	private Date				stop_to;

	private String				typ;

	@Override
	public int compareTo(final Contestation o) {
		if (o == null) {
			return -1;
		}
		if (!(o instanceof Contestation)) {
			return -1;
		}
		if (this.getDate_contestation() == null) {
			return -1;
		}
		final Contestation item = o;
		if (item.getDate_contestation() == null) {
			return 1;
		}
		return this.getDate_contestation().compareTo(item.getDate_contestation());
	}

	public Date getDate_bp() {
		return this.date_bp;
	}

	public Date getDate_contestation() {
		return this.date_contestation;

	}

	public Date getDate_penalty() {
		return this.date_penalty;

	}

	public String getDescription() {
		return this.description;
	}

	public String getFile_name() {
		return this.file_name;
	}

	public int getId() {
		return this.id;
	}

	public int getId_user() {
		return this.id_user;
	}

	public String getNote() {
		return this.note;

	}

	public String getProt() {
		return this.prot;

	}

	public String getProt_penalty() {
		return this.prot_penalty;
	}

	public Boolean getRecall() {
		return this.recall;
	}

	public Date getStop_from() {
		return this.stop_from;
	}

	public Date getStop_to() {
		return this.stop_to;
	}

	public String getTyp() {
		return this.typ;
	}

	public void setDate_bp(final Date date_bp) {
		this.date_bp = date_bp;
	}

	public void setDate_contestation(final Date date_contestation) {
		this.date_contestation = date_contestation;
	}

	public void setDate_penalty(final Date date_penalty) {
		this.date_penalty = date_penalty;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setFile_name(final String file_name) {
		this.file_name = file_name;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setId_user(final int id_user) {
		this.id_user = id_user;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setProt(final String prot) {
		this.prot = prot;
	}

	public void setProt_penalty(final String prot_penalty) {
		this.prot_penalty = prot_penalty;
	}

	public void setRecall(final Boolean recall) {
		this.recall = recall;
	}

	public void setStop_from(final Date stop_from) {
		this.stop_from = stop_from;
	}

	public void setStop_to(final Date stop_to) {
		this.stop_to = stop_to;
	}

	public void setTyp(final String typ) {
		this.typ = typ;
	}

}
