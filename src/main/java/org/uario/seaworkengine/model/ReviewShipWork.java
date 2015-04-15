package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

public class ReviewShipWork implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private String				crane;

	private Date				date_request;

	private Integer				id_ship;

	private String				notedetail;

	private Integer				rif_sws;

	private Integer				shift;

	private String				shipname;

	private Double				time_work;

	private Integer				volume;

	private Integer				volumeunderboard;

	public String getCrane() {
		return this.crane;
	}

	public Date getDate_request() {
		return this.date_request;
	}

	public Integer getId_ship() {
		return this.id_ship;
	}

	public String getNotedetail() {
		return this.notedetail;
	}

	public Integer getRif_sws() {
		return this.rif_sws;
	}

	public Integer getShift() {
		return this.shift;
	}

	public String getShipname() {
		return this.shipname;
	}

	public Double getTime_work() {
		return this.time_work;
	}

	public Integer getVolume() {
		return this.volume;
	}

	public Integer getVolumeunderboard() {
		return this.volumeunderboard;
	}

	public void setCrane(final String crane) {
		this.crane = crane;
	}

	public void setDate_request(final Date date_request) {
		this.date_request = date_request;
	}

	public void setId_ship(final Integer id_ship) {
		this.id_ship = id_ship;
	}

	public void setNotedetail(final String notedetail) {
		this.notedetail = notedetail;
	}

	public void setRif_sws(final Integer rif_sws) {
		this.rif_sws = rif_sws;
	}

	public void setShift(final Integer shift) {
		this.shift = shift;
	}

	public void setShipname(final String shipname) {
		this.shipname = shipname;
	}

	public void setTime_work(final Double time_work) {
		this.time_work = time_work;
	}

	public void setVolume(final Integer volume) {
		this.volume = volume;
	}

	public void setVolumeunderboard(final Integer volumeunderboard) {
		this.volumeunderboard = volumeunderboard;
	}

}
