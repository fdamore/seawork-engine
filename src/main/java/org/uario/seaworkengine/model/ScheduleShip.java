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

	private Integer	customer_id;

	private Date	departuredate;

	private Integer	id;

	private Integer	idship;

	/**
	 * Used only in view mode
	 */
	@XmlTransient
	private String	name;

	private String	note;

	private Integer	rif_mct;

	private Integer	volume;

	public Date getArrivaldate() {
		return this.arrivaldate;
	}

	public Integer getCustomer_id() {
		return this.customer_id;
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

	public Integer getRif_mct() {
		return this.rif_mct;
	}

	public Integer getVolume() {
		return this.volume;
	}

	public void setArrivaldate(final Date arrivaldate) {
		this.arrivaldate = arrivaldate;
	}

	public void setCustomer_id(final Integer customer_id) {
		this.customer_id = customer_id;
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

	public void setRif_mct(final Integer rif_mct) {
		this.rif_mct = rif_mct;
	}

	public void setVolume(final Integer volume) {
		this.volume = volume;
	}

}
