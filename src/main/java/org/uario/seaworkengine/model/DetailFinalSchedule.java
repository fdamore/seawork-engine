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

	private String				board;

	private String				crane;

	// to show in overview
	@XmlTransient
	private Date				date_schedule;

	// to show in overview
	@XmlTransient
	private String				employee_identification;

	private Integer				id;

	private Integer				id_schedule;

	private Integer				id_ship;

	// to show in overview on break shift
	private Integer				id_user;

	private Boolean				reviewshift;

	@XmlElement(name = "no_shift")
	private Integer				shift;

	// to show in overview
	@XmlTransient
	private Integer				shift_type;

	private Integer				task;

	@XmlTransient
	private Double				time;

	@XmlTransient
	private Timestamp			time_from;

	@XmlTransient
	private Timestamp			time_to;

	@XmlTransient
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

	public String getBoard() {
		return this.board;
	}

	public String getCrane() {
		return this.crane;
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

	public String getEmployee_identification() {
		return this.employee_identification;
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

	public Boolean getReviewshift() {
		return this.reviewshift;
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

	public void setBoard(final String board) {
		this.board = board;
	}

	public void setCrane(final String crane) {
		this.crane = crane;
	}

	public void setDate_schedule(final Date date_schedule) {
		this.date_schedule = date_schedule;
	}

	public void setEmployee_identification(final String employee_identification) {
		this.employee_identification = employee_identification;
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

	public void setReviewshift(final Boolean reviewshift) {
		this.reviewshift = reviewshift;
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
