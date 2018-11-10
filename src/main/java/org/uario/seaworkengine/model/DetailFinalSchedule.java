package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class DetailFinalSchedule implements Serializable, Comparable<DetailFinalSchedule> {

	private static final long	serialVersionUID	= 1L;

	private String				board;

	private Boolean				continueshift;

	// to show in review
	private String				controller;

	private String				crane;

	// to show in overview
	private Date				date_schedule;

	/**
	 * for review stat only
	 */
	private Integer				daywork_w;

	private String				defaultTask;

	private String				editor;

	// to show in overview
	private String				employee_identification;

	/**
	 * for review stat only
	 */
	private Integer				hourswork_w;

	private Integer				id;

	private Integer				id_schedule;

	private Integer				id_ship;

	// to show in overview on break shift
	private Integer				id_user;

	// to show in overview listbox
	private String				mobile_note;

	private Integer				mobile_user;

	// to show in overview listbox
	private String				note;

	private Boolean				reviewshift;

	private Integer				rif_sws;

	private Integer				shift;

	// to show in overview
	private Integer				shift_type;

	/**
	 * Used for CSV download in review user
	 */
	private Boolean				sign_user			= Boolean.FALSE;

	private Integer				task;

	private Double				time;

	private Timestamp			time_from;

	private Timestamp			time_to;

	private Double				time_vacation;

	// to show in overview
	private String				user;

	@Override
	public int compareTo(final DetailFinalSchedule o) {
		if (o == null) {
			return -1;
		}
		if (!(o instanceof DetailFinalSchedule)) {
			return -1;
		}
		if (this.getDate_schedule() == null) {
			return -1;
		}
		final DetailFinalSchedule item = o;
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

	public String getBoard() {
		return this.board;
	}

	public Boolean getContinueshift() {
		return this.continueshift;
	}

	public String getController() {
		return this.controller;
	}

	public String getCrane() {
		return this.crane;
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

	public Integer getDaywork_w() {
		return this.daywork_w;
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

	public Integer getHourswork_w() {
		return this.hourswork_w;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getId_schedule() {
		return this.id_schedule;
	}

	public Integer getId_ship() {
		return this.id_ship;
	}

	public Integer getId_user() {
		return this.id_user;
	}

	public String getMobile_note() {
		return this.mobile_note;
	}

	public Integer getMobile_user() {
		return this.mobile_user;
	}

	public String getNote() {
		return this.note;
	}

	public Boolean getReviewshift() {
		return this.reviewshift;
	}

	public Integer getRif_sws() {
		return this.rif_sws;
	}

	public Integer getShift() {
		return this.shift;
	}

	public Integer getShift_type() {
		return this.shift_type;
	}

	public Boolean getSign_user() {
		return this.sign_user;
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

	public void setBoard(final String board) {
		this.board = board;
	}

	public void setContinueshift(final Boolean continueshift) {
		this.continueshift = continueshift;
	}

	public void setController(final String controller) {
		this.controller = controller;
	}

	public void setCrane(final String crane) {
		this.crane = crane;
	}

	public void setDate_schedule(final Date date_schedule) {
		this.date_schedule = date_schedule;
	}

	public void setDaywork_w(final Integer daywork_w) {
		this.daywork_w = daywork_w;
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

	public void setHourswork_w(final Integer hourswork_w) {
		this.hourswork_w = hourswork_w;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setId_schedule(final Integer id_schedule) {
		this.id_schedule = id_schedule;
	}

	public void setId_ship(final Integer id_ship) {
		this.id_ship = id_ship;
	}

	public void setId_user(final Integer id_user) {
		this.id_user = id_user;
	}

	public void setMobile_note(final String mobile_note) {
		this.mobile_note = mobile_note;
	}

	public void setMobile_user(final Integer mobile_user) {
		this.mobile_user = mobile_user;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setReviewshift(final Boolean reviewshift) {
		this.reviewshift = reviewshift;
	}

	public void setRif_sws(final Integer rif_sws) {
		this.rif_sws = rif_sws;
	}

	public void setShift(final Integer shift) {
		this.shift = shift;
	}

	public void setShift_type(final Integer shift_type) {
		this.shift_type = shift_type;
	}

	public void setSign_user(final Boolean sign_user) {
		this.sign_user = sign_user;
	}

	public void setTask(final Integer task) {
		this.task = task;
	}

	public void setTime(final Double time) {
		this.time = time;
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
