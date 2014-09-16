package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

public class Scheduler implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Date				date_scheduler;

	private String				employee_identification;
	private Integer				final_task_1;
	private Integer				final_task_2;
	private Integer				final_task_3;
	private Integer				final_task_4;
	private Integer				final_time_task_1;
	private Integer				final_time_task_2;
	private Integer				final_time_task_3;
	private Integer				final_time_task_4;
	private Date				from_ts;
	private Integer				id;
	private Integer				id_controller;
	private Integer				id_editor;
	private Integer				id_scheduled;
	private Integer				initial_task_1;
	private Integer				initial_task_2;
	private Integer				initial_task_3;
	private Integer				initial_task_4;
	private Integer				initial_time_task_1;
	private Integer				initial_time_task_2;
	private Integer				initial_time_task_3;
	private Integer				initial_time_task_4;
	// used to visualize it
	private String				name_scheduled;
	private String				note;
	private Date				to_ts;

	public Date getDate_scheduler() {
		return this.date_scheduler;
	}

	public String getEmployee_identification() {
		return this.employee_identification;
	}

	public Integer getFinal_task_1() {
		return this.final_task_1;
	}

	public Integer getFinal_task_2() {
		return this.final_task_2;
	}

	public Integer getFinal_task_3() {
		return this.final_task_3;
	}

	public Integer getFinal_task_4() {
		return this.final_task_4;
	}

	public Integer getFinal_time_task_1() {
		return this.final_time_task_1;
	}

	public Integer getFinal_time_task_2() {
		return this.final_time_task_2;
	}

	public Integer getFinal_time_task_3() {
		return this.final_time_task_3;
	}

	public Integer getFinal_time_task_4() {
		return this.final_time_task_4;
	}

	public Date getFrom_ts() {
		return this.from_ts;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getId_controller() {
		return this.id_controller;
	}

	public Integer getId_editor() {
		return this.id_editor;
	}

	public Integer getId_scheduled() {
		return this.id_scheduled;
	}

	public Integer getInitial_task_1() {
		return this.initial_task_1;
	}

	public Integer getInitial_task_2() {
		return this.initial_task_2;
	}

	public Integer getInitial_task_3() {
		return this.initial_task_3;
	}

	public Integer getInitial_task_4() {
		return this.initial_task_4;
	}

	public Integer getInitial_time_task_1() {
		return this.initial_time_task_1;
	}

	public Integer getInitial_time_task_2() {
		return this.initial_time_task_2;
	}

	public Integer getInitial_time_task_3() {
		return this.initial_time_task_3;
	}

	public Integer getInitial_time_task_4() {
		return this.initial_time_task_4;
	}

	public String getName_scheduled() {
		return this.name_scheduled;
	}

	public String getNote() {
		return this.note;
	}

	public Date getTo_ts() {
		return this.to_ts;
	}

	public void setDate_scheduler(final Date date_scheduler) {
		this.date_scheduler = date_scheduler;
	}

	public void setEmployee_identification(final String employee_identification) {
		this.employee_identification = employee_identification;
	}

	public void setFinal_task_1(final Integer final_task_1) {
		this.final_task_1 = final_task_1;
	}

	public void setFinal_task_2(final Integer final_task_2) {
		this.final_task_2 = final_task_2;
	}

	public void setFinal_task_3(final Integer final_task_3) {
		this.final_task_3 = final_task_3;
	}

	public void setFinal_task_4(final Integer final_task_4) {
		this.final_task_4 = final_task_4;
	}

	public void setFinal_time_task_1(final Integer final_time_task_1) {
		this.final_time_task_1 = final_time_task_1;
	}

	public void setFinal_time_task_2(final Integer final_time_task_2) {
		this.final_time_task_2 = final_time_task_2;
	}

	public void setFinal_time_task_3(final Integer final_time_task_3) {
		this.final_time_task_3 = final_time_task_3;
	}

	public void setFinal_time_task_4(final Integer final_time_task_4) {
		this.final_time_task_4 = final_time_task_4;
	}

	public void setFrom_ts(final Date from_ts) {
		this.from_ts = from_ts;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setId_controller(final Integer id_controller) {
		this.id_controller = id_controller;
	}

	public void setId_editor(final Integer id_editor) {
		this.id_editor = id_editor;
	}

	public void setId_scheduled(final Integer id_scheduled) {
		this.id_scheduled = id_scheduled;
	}

	public void setInitial_task_1(final Integer initial_task_1) {
		this.initial_task_1 = initial_task_1;
	}

	public void setInitial_task_2(final Integer initial_task_2) {
		this.initial_task_2 = initial_task_2;
	}

	public void setInitial_task_3(final Integer initial_task_3) {
		this.initial_task_3 = initial_task_3;
	}

	public void setInitial_task_4(final Integer initial_task_4) {
		this.initial_task_4 = initial_task_4;
	}

	public void setInitial_time_task_1(final Integer initial_time_task_1) {
		this.initial_time_task_1 = initial_time_task_1;
	}

	public void setInitial_time_task_2(final Integer initial_time_task_2) {
		this.initial_time_task_2 = initial_time_task_2;
	}

	public void setInitial_time_task_3(final Integer initial_time_task_3) {
		this.initial_time_task_3 = initial_time_task_3;
	}

	public void setInitial_time_task_4(final Integer initial_time_task_4) {
		this.initial_time_task_4 = initial_time_task_4;
	}

	public void setName_scheduled(final String name_scheduled) {
		this.name_scheduled = name_scheduled;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setTo_ts(final Date to_ts) {
		this.to_ts = to_ts;
	}

	@Override
	public String toString() {
		return "" + this.getId();
	}

}