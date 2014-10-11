package org.uario.seaworkengine.model;

import java.io.Serializable;

public class Detail_Schedule implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;
	private int					id;
	private int					id_schedule;
	private int					shift;
	private int					task_final;
	private int					task_initial;
	private int					time_final;
	private int					time_initial;

	public int getId() {
		return this.id;
	}

	public int getId_schedule() {
		return this.id_schedule;
	}

	public int getShift() {
		return this.shift;
	}

	public int getTask_final() {
		return this.task_final;
	}

	public int getTask_initial() {
		return this.task_initial;
	}

	public int getTime_final() {
		return this.time_final;
	}

	public int getTime_initial() {
		return this.time_initial;
	}

	public void setId(final int id) {
		this.id = id;
	}

	public void setId_schedule(final int id_schedule) {
		this.id_schedule = id_schedule;
	}

	public void setShift(final int shift) {
		this.shift = shift;
	}

	public void setTask_final(final int task_final) {
		this.task_final = task_final;
	}

	public void setTask_initial(final int task_initial) {
		this.task_initial = task_initial;
	}

	public void setTime_final(final int time_final) {
		this.time_final = time_final;
	}

	public void setTime_initial(final int time_initial) {
		this.time_initial = time_initial;
	}

}
