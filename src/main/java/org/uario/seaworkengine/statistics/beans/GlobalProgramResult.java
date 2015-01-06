package org.uario.seaworkengine.statistics.beans;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class GlobalProgramResult implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Integer				controller;

	private Date				date_schedule;

	private Integer				editor;

	private Timestamp			entry;

	private Timestamp			exit_out;

	private Integer				id_schedule;

	private String				note;

	private Integer				program_shift_no;

	private Integer				program_task;

	private Double				program_time;

	private Integer				review_shift_no;

	private Integer				review_task;

	private Double				review_time;

	private Integer				shift;

	private String				user;

	public Integer getController() {
		return this.controller;
	}

	public Date getDate_schedule() {
		return this.date_schedule;
	}

	public Integer getEditor() {
		return this.editor;
	}

	public Timestamp getEntry() {
		return this.entry;
	}

	public Timestamp getExit_out() {
		return this.exit_out;
	}

	public Integer getId_schedule() {
		return this.id_schedule;
	}

	public String getNote() {
		return this.note;
	}

	public Integer getProgram_shift_no() {
		return this.program_shift_no;
	}

	public Integer getProgram_task() {
		return this.program_task;
	}

	public Double getProgram_time() {
		return this.program_time;
	}

	public Integer getReview_shift_no() {
		return this.review_shift_no;
	}

	public Integer getReview_task() {
		return this.review_task;
	}

	public Double getReview_time() {
		return this.review_time;
	}

	public Integer getShift() {
		return this.shift;
	}

	public String getUser() {
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

	public void setEntry(final Timestamp entry) {
		this.entry = entry;
	}

	public void setExit_out(final Timestamp exit_out) {
		this.exit_out = exit_out;
	}

	public void setId_schedule(final Integer id_schedule) {
		this.id_schedule = id_schedule;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setProgram_shift_no(final Integer program_shift_no) {
		this.program_shift_no = program_shift_no;
	}

	public void setProgram_task(final Integer program_task) {
		this.program_task = program_task;
	}

	public void setProgram_time(final Double program_time) {
		this.program_time = program_time;
	}

	public void setReview_shift_no(final Integer review_shift_no) {
		this.review_shift_no = review_shift_no;
	}

	public void setReview_task(final Integer review_task) {
		this.review_task = review_task;
	}

	public void setReview_time(final Double review_time) {
		this.review_time = review_time;
	}

	public void setShift(final Integer shift) {
		this.shift = shift;
	}

	public void setUser(final String user) {
		this.user = user;
	}

}
