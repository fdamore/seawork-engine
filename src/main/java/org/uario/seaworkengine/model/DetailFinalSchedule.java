package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(namespace = "org.uario.seaworkengine.model")
@XmlAccessorType(XmlAccessType.FIELD)
public class DetailFinalSchedule implements Serializable {

	@XmlTransient
	private static final long	serialVersionUID	= 1L;

	// to show in overview
	@XmlTransient
	private Date				date_schedule;

	private Integer				id;

	private Integer				id_schedule;

	private Integer				shift;

	private Integer				task;

	private Double				time;
	private Timestamp			time_from;
	private Timestamp			time_to;
	// to show in overview
	@XmlTransient
	private String				user;

	public Date getDate_schedule() {
		return this.date_schedule;
	}

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

	public Timestamp getTime_from() {
		return this.time_from;
	}

	public Timestamp getTime_to() {
		return this.time_to;
	}

	public String getUser() {
		return this.user;
	}

	public void setDate_schedule(final Date date_schedule) {
		this.date_schedule = date_schedule;
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

	public void setTime(final Double time) {
		this.time = time;
	}

	public void setTime_from(final Timestamp time_from) {
		this.time_from = time_from;
	}

	public void setTime_to(final Timestamp time_to) {
		this.time_to = time_to;
	}

	public void setUser(final String user) {
		this.user = user;
	}

}
