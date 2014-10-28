package org.uario.seaworkengine.model;

import java.io.Serializable;

public class DetailFinalSchedule implements Serializable {

	private static final long	serialVersionUID	= 1L;
	private Integer				id;
	private Integer				id_schedule;
	private Integer				shift;
	private Integer				task;
	private Integer				time;

	public Integer getId() {
		return this.id;
	}

	public Integer getId_schedule() {
		return this.id_schedule;
	}

	public Integer getShift() {
		return this.shift;
	}

	public Integer getTask() {
		return this.task;
	}

	public Integer getTime() {
		return this.time;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setId_schedule(final Integer id_schedule) {
		this.id_schedule = id_schedule;
	}

	public void setShift(final Integer shift) {
		this.shift = shift;
	}

	public void setTask(final Integer task) {
		this.task = task;
	}

	public void setTime(final Integer time) {
		this.time = time;
	}

}
