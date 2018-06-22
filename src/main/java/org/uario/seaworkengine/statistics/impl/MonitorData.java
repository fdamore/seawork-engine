package org.uario.seaworkengine.statistics.impl;

import java.io.Serializable;

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

	@Override
	public String toString() {
		return this.getName();
	}

}
