package org.uario.seaworkengine.mobile.model;

import java.io.Serializable;
import java.util.Date;

public class MobileUserDetail implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private String				board;

	private Boolean				continueshift;

	private String				crane;

	private Integer				id;

	private Integer				id_schedule;

	private Integer				id_ship;

	private Boolean				revised;
	private Integer				shift;
	private Integer				task;
	private Double				time;
	private Date				time_from;

	private Date				time_to;

	private Double				time_vacation;

	public String getBoard() {
		return this.board;
	}

	public Boolean getContinueshift() {
		return this.continueshift;
	}

	public String getCrane() {
		return this.crane;
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

	public Boolean getRevised() {
		return this.revised;
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

	public Date getTime_from() {
		return this.time_from;
	}

	public Date getTime_to() {
		return this.time_to;
	}

	public Double getTime_vacation() {
		return this.time_vacation;
	}

	public void setBoard(final String board) {
		this.board = board;
	}

	public void setContinueshift(final Boolean continueshift) {
		this.continueshift = continueshift;
	}

	public void setCrane(final String crane) {
		this.crane = crane;
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

	public void setRevised(final Boolean revised) {
		this.revised = revised;
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

	public void setTime_from(final Date time_from) {
		this.time_from = time_from;
	}

	public void setTime_to(final Date time_to) {
		this.time_to = time_to;
	}

	public void setTime_vacation(final Double time_vacation) {
		this.time_vacation = time_vacation;
	}

}
