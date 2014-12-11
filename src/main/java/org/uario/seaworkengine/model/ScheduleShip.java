package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.xml.bind.annotation.XmlTransient;

public class ScheduleShip implements Serializable {
	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	public static long getSerialversionuid() {
		return ScheduleShip.serialVersionUID;
	}

	private Timestamp	arrivaldate;

	private Integer		id;

	private Integer		idship;

	/**
	 * Used only in view mode
	 */
	@XmlTransient
	private String		name;

	private String		note;

	private Double		volume;

	public Timestamp getArrivaldate() {
		return this.arrivaldate;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getIdship() {
		return this.idship;
	}

	public String getName() {
		return this.name;
	}

	public String getNote() {
		return this.note;
	}

	public Double getVolume() {
		return this.volume;
	}

	public void setArrivaldate(final Timestamp arrivaldate) {
		this.arrivaldate = arrivaldate;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setIdship(final Integer idship) {
		this.idship = idship;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setVolume(final Double volume) {
		this.volume = volume;
	}

}
