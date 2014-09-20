package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

public class Scheduler implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Integer				controller;

	private Date				date_scheduler;

	private Integer				editor;

	private String				employee_identification;

	private Integer				final_shift_1;

	private Integer				final_shift_2;

	private Integer				final_shift_3;

	private Integer				final_shift_4;

	private Integer				final_time_1;

	private Integer				final_time_2;

	private Integer				final_time_3;

	private Integer				final_time_4;

	private Date				from_ts;

	private Integer				id;

	private Integer				initial_shift_1;

	private Integer				initial_shift_2;

	private Integer				initial_shift_3;

	private Integer				initial_shift_4;

	private Integer				initial_time_1;

	private Integer				initial_time_2;

	private Integer				initial_time_3;

	private Integer				initial_time_4;

	// used to visualize it
	private String				name_scheduled;

	private String				note;

	private Integer				scheduled;

	private Date				to_ts;

	public Integer getController() {
		return this.controller;
	}

	public Date getDate_scheduler() {
		return this.date_scheduler;
	}

	public Integer getEditor() {
		return this.editor;
	}

	public String getEmployee_identification() {
		return this.employee_identification;
	}

	public Integer getFinal_shift_1() {
		return this.final_shift_1;
	}

	public Integer getFinal_shift_2() {
		return this.final_shift_2;
	}

	public Integer getFinal_shift_3() {
		return this.final_shift_3;
	}

	public Integer getFinal_shift_4() {
		return this.final_shift_4;
	}

	public Integer getFinal_time_1() {
		return this.final_time_1;
	}

	public Integer getFinal_time_2() {
		return this.final_time_2;
	}

	public Integer getFinal_time_3() {
		return this.final_time_3;
	}

	public Integer getFinal_time_4() {
		return this.final_time_4;
	}

	public Date getFrom_ts() {
		return this.from_ts;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getInitial_shift_1() {
		return this.initial_shift_1;
	}

	public Integer getInitial_shift_2() {
		return this.initial_shift_2;
	}

	public Integer getInitial_shift_3() {
		return this.initial_shift_3;
	}

	public Integer getInitial_shift_4() {
		return this.initial_shift_4;
	}

	public Integer getInitial_time_1() {
		return this.initial_time_1;
	}

	public Integer getInitial_time_2() {
		return this.initial_time_2;
	}

	public Integer getInitial_time_3() {
		return this.initial_time_3;
	}

	public Integer getInitial_time_4() {
		return this.initial_time_4;
	}

	public String getName_scheduled() {
		return this.name_scheduled;
	}

	public String getNote() {
		return this.note;
	}

	public Integer getScheduled() {
		return this.scheduled;
	}

	public Date getTo_ts() {
		return this.to_ts;
	}

	public void setController(final Integer controller) {
		this.controller = controller;
	}

	public void setDate_scheduler(final Date date_scheduler) {
		this.date_scheduler = date_scheduler;
	}

	public void setEditor(final Integer editor) {
		this.editor = editor;
	}

	public void setEmployee_identification(final String employee_identification) {
		this.employee_identification = employee_identification;
	}

	public void setFinal_shift_1(final Integer final_shift_1) {
		this.final_shift_1 = final_shift_1;
	}

	public void setFinal_shift_2(final Integer final_shift_2) {
		this.final_shift_2 = final_shift_2;
	}

	public void setFinal_shift_3(final Integer final_shift_3) {
		this.final_shift_3 = final_shift_3;
	}

	public void setFinal_shift_4(final Integer final_shift_4) {
		this.final_shift_4 = final_shift_4;
	}

	public void setFinal_time_1(final Integer final_time_1) {
		this.final_time_1 = final_time_1;
	}

	public void setFinal_time_2(final Integer final_time_2) {
		this.final_time_2 = final_time_2;
	}

	public void setFinal_time_3(final Integer final_time_3) {
		this.final_time_3 = final_time_3;
	}

	public void setFinal_time_4(final Integer final_time_4) {
		this.final_time_4 = final_time_4;
	}

	public void setFrom_ts(final Date from_ts) {
		this.from_ts = from_ts;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setInitial_shift_1(final Integer initial_shift_1) {
		this.initial_shift_1 = initial_shift_1;
	}

	public void setInitial_shift_2(final Integer initial_shift_2) {
		this.initial_shift_2 = initial_shift_2;
	}

	public void setInitial_shift_3(final Integer initial_shift_3) {
		this.initial_shift_3 = initial_shift_3;
	}

	public void setInitial_shift_4(final Integer initial_shift_4) {
		this.initial_shift_4 = initial_shift_4;
	}

	public void setInitial_time_1(final Integer initial_time_1) {
		this.initial_time_1 = initial_time_1;
	}

	public void setInitial_time_2(final Integer initial_time_2) {
		this.initial_time_2 = initial_time_2;
	}

	public void setInitial_time_3(final Integer initial_time_3) {
		this.initial_time_3 = initial_time_3;
	}

	public void setInitial_time_4(final Integer initial_time_4) {
		this.initial_time_4 = initial_time_4;
	}

	public void setName_scheduled(final String name_scheduled) {
		this.name_scheduled = name_scheduled;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setScheduled(final Integer scheduled) {
		this.scheduled = scheduled;
	}

	public void setTo_ts(final Date to_ts) {
		this.to_ts = to_ts;
	}

}