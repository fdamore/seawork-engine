package org.uario.seaworkengine.model;

import java.sql.Timestamp;

public class ScheduleShip {
	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	public static long getSerialversionuid() {
		return ScheduleShip.serialVersionUID;
	}

	private Timestamp	arrivalDate;
	private Integer		id;
	private Integer		idship;
	private String		note;
	private Double		volume;

	public Timestamp getArrivalDate() {
		return this.arrivalDate;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getIdship() {
		return this.idship;
	}

	public String getNote() {
		return this.note;
	}

	public Double getVolume() {
		return this.volume;
	}

	public void setArrivalDate(final Timestamp arrivalDate) {
		this.arrivalDate = arrivalDate;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setIdship(final Integer idship) {
		this.idship = idship;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setVolume(final Double volume) {
		this.volume = volume;
	}

}
