package org.uario.seaworkengine.statistics;

import java.io.Serializable;

public class ReviewShipWorkAggregate implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Integer				id_ship;

	private String				rif_mct;

	private Integer				rif_sws;

	private String				shipname;

	private Double				time_work;

	private Integer				volume;

	private Integer				volume_tw_mct;

	private Integer				volumeunderboard;

	private Integer				volumeunderboard_sws;

	public Integer getId_ship() {
		return this.id_ship;
	}

	public String getRif_mct() {
		return this.rif_mct;
	}

	public Integer getRif_sws() {
		return this.rif_sws;
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

	public void setId_ship(final Integer id_ship) {
		this.id_ship = id_ship;
	}

	public void setRif_mct(final String rif_mct) {
		this.rif_mct = rif_mct;
	}

	public void setRif_sws(final Integer rif_sws) {
		this.rif_sws = rif_sws;
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
