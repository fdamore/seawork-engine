package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.xml.bind.annotation.XmlTransient;

public class DetailInitialSchedule implements Serializable, Comparable<DetailInitialSchedule> {

	/**
	 *
	 */
	@XmlTransient
	private static final long	serialVersionUID	= 1L;

	private Boolean				continueshift		= false;

	// to show in review
	private String				controller;

	// to show in overview
	@XmlTransient
	private Date				date_schedule;

	// to show in overview
	private String				defaultTask;

	// to show in review
	private String				editor;

	// to show in overview
	@XmlTransient
	private String				employee_identification;

	private Integer				id;

	private Integer				id_schedule;

	// to show in overview
	private Integer				id_user;

	// to show in overview listbox
	private String				mobile_note;

	// to show in overview listbox
	private String				note;

	private Integer				shift;

	// to show in overview
	private Integer				shift_type;

	private Integer				task;

	private Double				time;

	private Timestamp			time_from;

	private Timestamp			time_to;

	private Double				time_vacation;

	// to show in overview
	private String				user;

	@Override
	public int compareTo(final DetailInitialSchedule o) {
		if (o == null) {
			return -1;
		}
		if (!(o instanceof DetailInitialSchedule)) {
			return -1;
		}
		if (this.getDate_schedule() == null) {
			return -1;
		}
		final DetailInitialSchedule item = o;
		if (item.getDate_schedule() == null) {
			return 1;
		}

		if ((item.getDate_schedule().getTime() - this.getDate_schedule().getTime()) == 0) {
			if (this.getTime_from() == null) {
				return -1;
			}

			if (item.getTime_from() == null) {
				return 1;
			}

			return this.getTime_from().compareTo(item.getTime_from());
		}

		return this.getDate_schedule().compareTo(item.getDate_schedule());
	}

	public Boolean getContinueshift() {
		return this.continueshift;
	}

	public String getController() {
		return this.controller;
	}

	public Date getDate_schedule() {
		return this.date_schedule;
	}

	public Date getDateFrom() {
		if (this.time_from == null) {
			return null;
		}

		return new Date(this.time_from.getTime());

	}

	public Date getDateTo() {
		if (this.time_to == null) {
			return null;
		}

		return new Date(this.time_to.getTime());
	}

	public String getDefaultTask() {
		return this.defaultTask;
	}

	public String getEditor() {
		return this.editor;
	}

	public String getEmployee_identification() {
		return this.employee_identification;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getId_schedule() {
		return this.id_schedule;
	}

	public Integer getId_user() {
		return this.id_user;
	}

	public String getMobile_note() {
		return this.mobile_note;
	}

	public String getNote() {
		return this.note;
	}

	public Integer getShift() {
		return this.shift;
	}

	public Integer getShift_type() {
		return this.shift_type;
	}

	public Integer getTask() {
		return this.task;
	}

	public Double getTime() {
		return this.time;
	}

	public Timestamp getTime_from() {
		return this.time_from;
	}

	public Timestamp getTime_to() {
		return this.time_to;
	}

	public Double getTime_vacation() {
		return this.time_vacation;
	}

	public String getUser() {
		return this.user;
	}

	public void setContinueshift(final Boolean continueshift) {
		this.continueshift = continueshift;
	}

	public void setController(final String controller) {
		this.controller = controller;
	}

	public void setDate_schedule(final Date date_schedule) {
		this.date_schedule = date_schedule;
	}

	public void setDefaultTask(final String defaultTask) {
		this.defaultTask = defaultTask;
	}

	public void setEditor(final String editor) {
		this.editor = editor;
	}

	public void setEmployee_identification(final String employee_identification) {
		this.employee_identification = employee_identification;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setId_schedule(final int id_schedule) {
		this.id_schedule = id_schedule;
	}

	public void setId_user(final Integer id_user) {
		this.id_user = id_user;
	}

	public void setMobile_note(final String mobile_note) {
		this.mobile_note = mobile_note;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setShift(final int shift) {
		this.shift = shift;
	}

	public void setShift_type(final Integer shift_type) {
		this.shift_type = shift_type;
	}

	public void setTask(final int task_initial) {
		this.task = task_initial;
	}

	public void setTime(final Double time_initial) {
		this.time = time_initial;
	}

	public void setTime_from(final Timestamp time_from) {
		this.time_from = time_from;
	}

	public void setTime_to(final Timestamp time_to) {
		this.time_to = time_to;
	}

	public void setTime_vacation(final Double time_vacation) {
		this.time_vacation = time_vacation;
	}

	public void setUser(final String user) {
		this.user = user;
	}

}
