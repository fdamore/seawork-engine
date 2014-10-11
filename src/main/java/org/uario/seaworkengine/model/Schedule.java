package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

public class Schedule implements Comparable<Schedule>, Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;
	private Integer				controller;
	private Date				date_schedule;
	private Integer				editor;
	private Date				from_time;
	private Integer				id;
	private String				note;
	private Date				to_time;
	private Integer				user;

	@Override
	public int compareTo(final Schedule o) {
		if (o == null) {
			return -1;
		}
		if (!(o instanceof Schedule)) {
			return -1;
		}
		if (this.getDate_schedule() == null) {
			return -1;
		}
		final Schedule item = o;
		if (item.getDate_schedule() == null) {
			return 1;
		}
		return this.getDate_schedule().compareTo(item.getDate_schedule());
	}

	public Integer getController() {
		return this.controller;
	}

	public Date getDate_schedule() {
		return this.date_schedule;
	}

	public Integer getEditor() {
		return this.editor;
	}

	public Date getFrom_time() {
		return this.from_time;
	}

	public Integer getId() {
		return this.id;
	}

	public String getNote() {
		return this.note;
	}

	public Date getTo_time() {
		return this.to_time;
	}

	public Integer getUser() {
		return this.user;
	}

	public void setController(final Integer controller) {
		this.controller = controller;
	}

	public void setDate_schedule(final Date date_schedule) {
		this.date_schedule = date_schedule;
	}

	public void setEditor(final Integer editor) {
		this.editor = editor;
	}

	public void setFrom_time(final Date from_time) {
		this.from_time = from_time;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setTo_time(final Date to_time) {
		this.to_time = to_time;
	}

	public void setUser(final Integer user) {
		this.user = user;
	}

}
