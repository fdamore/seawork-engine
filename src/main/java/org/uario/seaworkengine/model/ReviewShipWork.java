package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

public class ReviewShipWork implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	// used only for view
	private String				crane;

	private Boolean				crane_gtw;

	private Date				date_request;

	private Date				first_down;

	private Integer				id_ship;

	private Integer				invoicing_cycle;

	private Date				last_down;

	private String				notedetail;

	private String				rain;

	private String				rif_mct;

	private Integer				rif_sws;

	private Integer				shift;

	private String				shipname;

	private String				sky;

	private String				temperature;

	private Double				time_work;

	private Integer				volume;

	private Integer				volume_tw_mct;

	private Integer				volumeunderboard;

	private Integer				volumeunderboard_sws;

	private String				wind;

	public String getCrane() {
		return this.crane;
	}

	public Boolean getCrane_gtw() {
		return this.crane_gtw;
	}

	public Date getDate_request() {
		return this.date_request;
	}

	public Date getFirst_down() {
		return this.first_down;
	}

	public Integer getId_ship() {
		return this.id_ship;
	}

	public Integer getInvoicing_cycle() {
		return this.invoicing_cycle;
	}

	public Date getLast_down() {
		return this.last_down;
	}

	public String getNotedetail() {
		return this.notedetail;
	}

	public String getRain() {
		return this.rain;
	}

	public String getRif_mct() {
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

	public String getSky() {
		return this.sky;
	}

	public String getTemperature() {
		return this.temperature;
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

	public String getWind() {
		return this.wind;
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

	public void setFirst_down(final Date first_down) {
		this.first_down = first_down;
	}

	public void setId_ship(final Integer id_ship) {
		this.id_ship = id_ship;
	}

	public void setInvoicing_cycle(final Integer invoicing_cycle) {
		this.invoicing_cycle = invoicing_cycle;
	}

	public void setLast_down(final Date last_down) {
		this.last_down = last_down;
	}

	public void setNotedetail(final String notedetail) {
		this.notedetail = notedetail;
	}

	public void setRain(final String rain) {
		this.rain = rain;
	}

	public void setRif_mct(final String rif_mct) {
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

	public void setSky(final String sky) {
		this.sky = sky;
	}

	public void setTemperature(final String temperature) {
		this.temperature = temperature;
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

	public void setWind(final String wind) {
		this.wind = wind;
	}

}
