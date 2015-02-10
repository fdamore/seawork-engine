package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(namespace = "org.uario.seaworkengine.model")
@XmlAccessorType(XmlAccessType.FIELD)
public class DetailFinalSchedule implements Serializable, Comparable<DetailFinalSchedule> {

	@XmlTransient
	private static final long	serialVersionUID	= 1L;

	// to show in overview
	@XmlTransient
	private Date				date_schedule;

	private Integer				id;

	private Integer				id_schedule;

	@XmlElement(name = "no_shift")
	private Integer				shift;

	// to show in overview
	@XmlTransient
	private Integer				shift_type;

	private Integer				task;

	private Double				time;

	@XmlTransient
	private Timestamp			time_from;

	@XmlTransient
	private Timestamp			time_to;

	private Double				time_vacation;

	// to show in overview
	@XmlTransient
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
		return this.getDate_schedule().compareTo(item.getDate_schedule());
	}

	public Date getDate_schedule() {
		return this.date_schedule;
	}

	@XmlElement(name = "time_from")
	public Date getDateFrom() {
		if (this.time_from == null) {
			return null;
		}

		return new Date(this.time_from.getTime());

	}

	@XmlElement(name = "time_to")
	public Date getDateTo() {
		if (this.time_to == null) {
			return null;
		}

		return new Date(this.time_to.getTime());
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

	public void setShift_type(final Integer shift_type) {
		this.shift_type = shift_type;
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
