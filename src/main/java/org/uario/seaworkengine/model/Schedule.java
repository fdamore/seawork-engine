package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(namespace = "org.uario.seaworkengine.model")
@XmlAccessorType(XmlAccessType.FIELD)
public class Schedule implements Comparable<Schedule>, Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Integer				controller;

	private Date				date_schedule;

	@XmlTransient
	private Integer				editor;

	private Integer				id;

	// used form visualize
	@XmlTransient
	private String				name_user;

	private String				note;

	// used for visualize
	@XmlTransient
	private Double				program_time;

	// used for visualize
	@XmlTransient
	private Double				revision_time;

	@XmlTransient
	private Integer				shift;

	@XmlTransient
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

	public Double getProgram_time() {
		return this.program_time;
	}

	public Double getRevision_time() {
		return this.revision_time;
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

	public void setProgram_time(final Double program_time) {
		this.program_time = program_time;
	}

	public void setRevision_time(final Double revision_time) {
		this.revision_time = revision_time;
	}

	public void setShift(final Integer shift) {
		this.shift = shift;
	}

	public void setUser(final Integer user) {
		this.user = user;
	}

}
