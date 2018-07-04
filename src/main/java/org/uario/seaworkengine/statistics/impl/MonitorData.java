package org.uario.seaworkengine.statistics.impl;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "org.uario.seaworkengine.model")
@XmlAccessorType(XmlAccessType.FIELD)
public class MonitorData implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private String				board;

	private String				crane;

	private Integer				id_ship;

	private String				name;

	private Integer				shift_no;

	private Date				time_from;

	private Date				time_to;

	private Integer				user;

	public String getBoard() {
		return this.board;
	}

	public String getCrane() {
		return this.crane;
	}

	public Integer getId_ship() {
		return this.id_ship;
	}

	public String getName() {
		return this.name;
	}

	public Integer getShift_no() {
		return this.shift_no;
	}

	public Date getTime_from() {
		return this.time_from;
	}

	public Date getTime_to() {
		return this.time_to;
	}

	public Integer getUser() {
		return this.user;
	}

	public void setBoard(final String board) {
		this.board = board;
	}

	public void setCrane(final String crane) {
		this.crane = crane;
	}

	public void setId_ship(final Integer id_ship) {
		this.id_ship = id_ship;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setShift_no(final Integer shift_no) {
		this.shift_no = shift_no;
	}

	public void setTime_from(final Date time_from) {
		this.time_from = time_from;
	}

	public void setTime_to(final Date time_to) {
		this.time_to = time_to;
	}

	public void setUser(final Integer user) {
		this.user = user;
	}

	@Override
	public String toString() {
		return this.getName();
	}

}
