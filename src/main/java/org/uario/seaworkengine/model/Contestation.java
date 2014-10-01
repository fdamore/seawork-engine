package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

public class Contestation implements Comparable<Contestation>, Serializable {

	private Date	date_contestation;

	private int		id;

	private int		id_user;

	private String	note;

	private Date	stop_from;

	private Date	stop_to;

	private String	typ;

	public Contestation(final int id, final String typ, final Date date_contestation, final int id_user) {
		this.id = id;
		this.typ = typ;
		this.date_contestation = date_contestation;
		this.id_user = id_user;
	}

	@Override
	public int compareTo(final Contestation arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Date getDate_contestation() {
		return this.date_contestation;
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

	public Date getStop_from() {
		return this.stop_from;
	}

	public Date getStop_to() {
		return this.stop_to;
	}

	public String getTyp() {
		return this.typ;
	}

	public void setDate_contestation(final Date date_contestation) {
		this.date_contestation = date_contestation;
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
