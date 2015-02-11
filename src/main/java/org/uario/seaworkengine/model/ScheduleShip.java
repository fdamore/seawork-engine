package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlTransient;

public class ScheduleShip implements Serializable {
	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	public static long getSerialversionuid() {
		return ScheduleShip.serialVersionUID;
	}

	private Date	arrivaldate;

	private Date	departuredate;

	private Integer	id;

	private Integer	idship;

	/**
	 * Used only in view mode
	 */
	@XmlTransient
	private String	name;

	private String	note;

	private Double	volume;

	public Date getArrivaldate() {
		return this.arrivaldate;
	}

	public Date getDeparturedate() {
		return this.departuredate;
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

	public void setArrivaldate(final Date arrivaldate) {
		this.arrivaldate = arrivaldate;
	}

	public void setDeparturedate(final Date departuredate) {
		this.departuredate = departuredate;
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
