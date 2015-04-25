package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

public class ReviewShipWork implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private String				crane;

	private Boolean				crane_gtw;

	private Date				date_request;

	private Integer				id_ship;

	private Integer				invoicing_cycle;

	private String				notedetail;

	private Integer				rif_mct;

	private Integer				rif_sws;

	private Integer				shift;

	private String				shipname;

	private Double				time_work;

	private Integer				volume;

	private Integer				volume_tw_mct;

	private Integer				volumeunderboard;

	private Integer				volumeunderboard_sws;

	public String getCrane() {
		return this.crane;
	}

	public Boolean getCrane_gtw() {
		return this.crane_gtw;
	}

	public Date getDate_request() {
		return this.date_request;
	}

	public Integer getId_ship() {
		return this.id_ship;
	}

	public Integer getInvoicing_cycle() {
		return this.invoicing_cycle;
	}

	public String getNotedetail() {
		return this.notedetail;
	}

	public Integer getRif_mct() {
		return this.rif_mct;
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

	public Integer getVolume_tw_mct() {
		return this.volume_tw_mct;
	}

	public Integer getVolumeunderboard() {
		return this.volumeunderboard;
	}

	public Integer getVolumeunderboard_sws() {
		return this.volumeunderboard_sws;
	}

	public void setCrane(final String crane) {
		this.crane = crane;
	}

	public void setCrane_gtw(final Boolean crane_gtw) {
		this.crane_gtw = crane_gtw;
	}

	public void setDate_request(final Date date_request) {
		this.date_request = date_request;
	}

	public void setId_ship(final Integer id_ship) {
		this.id_ship = id_ship;
	}

	public void setInvoicing_cycle(final Integer invoicing_cycle) {
		this.invoicing_cycle = invoicing_cycle;
	}

	public void setNotedetail(final String notedetail) {
		this.notedetail = notedetail;
	}

	public void setRif_mct(final Integer rif_mct) {
		this.rif_mct = rif_mct;
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

	public void setVolume_tw_mct(final Integer volume_tw_mct) {
		this.volume_tw_mct = volume_tw_mct;
	}

	public void setVolumeunderboard(final Integer volumeunderboard) {
		this.volumeunderboard = volumeunderboard;
	}

	public void setVolumeunderboard_sws(final Integer volumeunderboard_sws) {
		this.volumeunderboard_sws = volumeunderboard_sws;
	}

}
