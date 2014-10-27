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

	private Integer				id;

	// used form visualize
	private String				name_user;

	private String				note;

	private Integer				program_time;

	private Integer				shift;

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

	public Integer getId() {
		return this.id;
	}

	public String getName_user() {
		return this.name_user;
	}

	public String getNote() {
		return this.note;
	}

	public Integer getProgram_time() {
		return this.program_time;
	}

	public Integer getShift() {
		return this.shift;
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

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setName_user(final String name_user) {
		this.name_user = name_user;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setProgram_time(final Integer program_time) {
		this.program_time = program_time;
	}

	public void setShift(final Integer shift) {
		this.shift = shift;
	}

	public void setUser(final Integer user) {
		this.user = user;
	}

}
