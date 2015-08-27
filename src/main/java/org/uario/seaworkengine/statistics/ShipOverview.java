package org.uario.seaworkengine.statistics;

import java.io.Serializable;

public class ShipOverview implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private String board;

	private Integer id_ship;

	private String name;

	private Double time;

	private Double time_vacation;

	public String getBoard() {
		return this.board;
	}

	public Integer getId_ship() {
		return this.id_ship;
	}

	public String getName() {
		return this.name;
	}

	public Double getTime() {
		return this.time;
	}

	public Double getTime_vacation() {
		return this.time_vacation;
	}

	public void setBoard(final String board) {
		this.board = board;
	}

	public void setId_ship(final Integer id_ship) {
		this.id_ship = id_ship;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setTime(final Double time) {
		this.time = time;
	}

	public void setTime_vacation(final Double time_vacation) {
		this.time_vacation = time_vacation;
	}

}
