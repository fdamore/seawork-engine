package org.uario.seaworkengine.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlTransient;

public class DetailFinalScheduleShip implements Serializable {

	@XmlTransient
	private static final long	serialVersionUID	= 1L;

	private Integer				crane;

	private Boolean				crane_gtw;

	// rif sws
	private Integer				id;

	private Integer				iddetailscheduleship;

	private String				notedetail;

	// using only for view, rif_mct in table scheduleship
	private Integer				rif_mct;

	private Double				timework;

	private Integer				volume;

	private Integer				volume_tw_mct;

	private Integer				volumeunderboard;

	public Integer getCrane() {
		return this.crane;
	}

	public Boolean getCrane_gtw() {
		return this.crane_gtw;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getIddetailscheduleship() {
		return this.iddetailscheduleship;
	}

	public String getNotedetail() {
		return this.notedetail;
	}

	public Integer getRif_mct() {
		return this.rif_mct;
	}

	public Double getTimework() {
		return this.timework;
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

	public void setCrane(final Integer crane) {
		this.crane = crane;
	}

	public void setCrane_gtw(final Boolean crane_gtw) {
		this.crane_gtw = crane_gtw;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setIddetailscheduleship(final Integer iddetailscheduleship) {
		this.iddetailscheduleship = iddetailscheduleship;
	}

	public void setNotedetail(final String notedetail) {
		this.notedetail = notedetail;
	}

	public void setRif_mct(final Integer rif_mct) {
		this.rif_mct = rif_mct;
	}

	public void setTimework(final Double timework) {
		this.timework = timework;
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

}
