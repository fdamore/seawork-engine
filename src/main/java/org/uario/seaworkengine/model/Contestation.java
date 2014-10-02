package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

public class Contestation implements Comparable<Contestation>, Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Date				date_contestation;

	private int					id;

	private int					id_user;

	private String				note;

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
