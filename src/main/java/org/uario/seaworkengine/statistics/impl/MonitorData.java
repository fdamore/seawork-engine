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
	private static final long serialVersionUID = 1L;

	private String crane;

	private Integer id_ship;

	private String name;

	private String note;

	private Integer on_board;

	private Integer out_board;

	private Integer shift_no;

	public String getCrane() {
		return this.crane;
	}

	public Integer getId_ship() {
		return this.id_ship;
	}

	public String getName() {
		return this.name;
	}

	public String getNote() {
		return this.note;
	}

	public Integer getOn_board() {
		return this.on_board;
	}

	public Integer getOut_board() {
		return this.out_board;
	}

	public Integer getShift_no() {
		return this.shift_no;
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

	public void setNote(final String note) {
		this.note = note;
	}

	public void setOn_board(final Integer on_board) {
		this.on_board = on_board;
	}

	public void setOut_board(final Integer out_board) {
		this.out_board = out_board;
	}

	public void setShift_no(final Integer shift_no) {
		this.shift_no = shift_no;
	}

	@Override
	public String toString() {
		return this.getName();
	}

}
