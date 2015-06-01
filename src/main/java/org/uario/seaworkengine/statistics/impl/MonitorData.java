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

	private Integer				id_ship;

	private String				name;

	private String				on_board;

	private String				out_board;

	private Integer				shift_no;

	public Integer getId_ship() {
		return this.id_ship;
	}

	public String getName() {
		return this.name;
	}

	public String getOn_board() {
		return this.on_board;
	}

	public String getOut_board() {
		return this.out_board;
	}

	public Integer getShift_no() {
		return this.shift_no;
	}

	public void setId_ship(final Integer id_ship) {
		this.id_ship = id_ship;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setOn_board(final String on_board) {
		this.on_board = on_board;
	}

	public void setOut_board(final String out_board) {
		this.out_board = out_board;
	}

	public void setShift_no(final Integer shift_no) {
		this.shift_no = shift_no;
	}

}
