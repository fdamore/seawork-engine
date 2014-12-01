package org.uario.seaworkengine.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(namespace = "org.uario.seaworkengine.model")
@XmlAccessorType(XmlAccessType.FIELD)
public class DetailInitialSchedule implements Serializable {

	/**
	 *
	 */
	@XmlTransient
	private static final long	serialVersionUID	= 1L;

	private Integer				id;
	private Integer				id_schedule;
	private Integer				shift;
	private Integer				task;
	private Double				time;

	// to show in overview
	@XmlTransient
	private String				user;

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

	public Double getTime() {
		return this.time;
	}

	public String getUser() {
		return this.user;
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

	public void setTask(final int task_initial) {
		this.task = task_initial;
	}

	public void setTime(final Double time_initial) {
		this.time = time_initial;
	}

	public void setUser(final String user) {
		this.user = user;
	}

}
