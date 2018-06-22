package org.uario.seaworkengine.zkevent.bean;

import org.apache.commons.lang3.ObjectUtils;

public class MonitorDataStructure implements Comparable<MonitorDataStructure> {

	private String	crane;

	private Integer	id_ship;

	private String	name;

	private Integer	on_board	= 0;

	private Integer	out_board	= 0;

	private Integer	shift_no;

	@Override
	public int compareTo(final MonitorDataStructure o) {
		if (o == null) {
			return -1;
		}

		return this.getShift_no().compareTo(o.getShift_no());

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

	public Integer getOn_board() {
		return ObjectUtils.defaultIfNull(this.on_board, 0);
	}

	public Integer getOut_board() {
		return ObjectUtils.defaultIfNull(this.out_board, 0);
	}

	public Integer getShift_no() {
		return this.shift_no;
	}

	public Integer getTotal() {
		return this.getOn_board() + this.getOut_board();
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

	public void setOn_board(final Integer on_board) {
		this.on_board = on_board;
	}

	public void setOut_board(final Integer out_board) {
		this.out_board = out_board;
	}

	public void setShift_no(final Integer shift_no) {
		this.shift_no = shift_no;
	}

}
