package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

public class Scheduler implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Date				date_scheduler;

	private Date				from_tsamp;

	private Integer				id;

	private Integer				id_controller;

	private Integer				id_editor;

	// matricola
	private String				id_employee_scheduled;

	private Integer				id_final_shift;

	private Integer				id_final_task;

	private Integer				id_initial_shift;

	private Integer				id_initial_task;

	private Integer				id_scheduled;

	private String				name_scheduled;

	private String				note;

	private Date				to_tstamp;

	public Date getDate_scheduler() {
		return this.date_scheduler;
	}

	public Date getFrom_tsamp() {
		return this.from_tsamp;
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

	public String getId_employee_scheduled() {
		return this.id_employee_scheduled;
	}

	public Integer getId_final_shift() {
		return this.id_final_shift;
	}

	public Integer getId_final_task() {
		return this.id_final_task;
	}

	public Integer getId_initial_shift() {
		return this.id_initial_shift;
	}

	public Integer getId_initial_task() {
		return this.id_initial_task;
	}

	public Integer getId_scheduled() {
		return this.id_scheduled;
	}

	public String getName_scheduled() {
		return this.name_scheduled;
	}

	public String getNote() {
		return this.note;
	}

	public Date getTo_tstamp() {
		return this.to_tstamp;
	}

	public void setDate_scheduler(final Date date_scheduler) {
		this.date_scheduler = date_scheduler;
	}

	public void setFrom_tsamp(final Date from_tsamp) {
		this.from_tsamp = from_tsamp;
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

	public void setId_employee_scheduled(final String id_employee_scheduled) {
		this.id_employee_scheduled = id_employee_scheduled;
	}

	public void setId_final_shift(final Integer id_final_shift) {
		this.id_final_shift = id_final_shift;
	}

	public void setId_final_task(final Integer id_final_task) {
		this.id_final_task = id_final_task;
	}

	public void setId_initial_shift(final Integer id_initial_shift) {
		this.id_initial_shift = id_initial_shift;
	}

	public void setId_initial_task(final Integer id_initial_task) {
		this.id_initial_task = id_initial_task;
	}

	public void setId_scheduled(final Integer id_scheduled) {
		this.id_scheduled = id_scheduled;
	}

	public void setName_scheduled(final String name_scheduled) {
		this.name_scheduled = name_scheduled;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setTo_tstamp(final Date to_tstamp) {
		this.to_tstamp = to_tstamp;
	}

}