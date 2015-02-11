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

	private Integer				editor;

	private Integer				id;

	// used form visualize
	@XmlTransient
	private String				name_controller;

	// used form visualize
	@XmlTransient
	private String				name_editor;

	// used form visualize
	@XmlTransient
	private String				name_user;

	// used for visualize - number of shift (1,2,3,4)
	@XmlTransient
	private Integer				no_shift;

	private String				note;

	// used for visualize
	@XmlTransient
	private Double				program_time;

	// used for visualize
	@XmlTransient
	private Double				revision_time;

	private Integer				shift;

	@XmlTransient
	private Boolean				sync_mobile;

	private Integer				user;

	public Schedule() {

	}

	public Schedule(final Date date_schedule) {
		this.date_schedule = date_schedule;
	}

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

	public String getName_controller() {
		return this.name_controller;
	}

	public String getName_editor() {
		return this.name_editor;
	}

	public String getName_user() {
		return this.name_user;
	}

	public Integer getNo_shift() {
		return this.no_shift;
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

	public Boolean getSync_mobile() {
		return this.sync_mobile;
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

	public void setName_controller(final String name_controller) {
		this.name_controller = name_controller;
	}

	public void setName_editor(final String name_editor) {
		this.name_editor = name_editor;
	}

	public void setName_user(final String name_user) {
		this.name_user = name_user;
	}

	public void setNo_shift(final Integer no_shift) {
		this.no_shift = no_shift;
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

	public void setSync_mobile(final Boolean sync_mobile) {
		this.sync_mobile = sync_mobile;
	}

	public void setUser(final Integer user) {
		this.user = user;
	}

}
