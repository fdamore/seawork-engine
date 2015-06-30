package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(namespace = "org.uario.seaworkengine.model")
@XmlAccessorType(XmlAccessType.FIELD)
public class DetailFinalScheduleShip implements Serializable {

	@XmlTransient
	private static final long	serialVersionUID	= 1L;

	private Date				activity_end;

	private Date				activity_start;

	private Integer				crane;

	private Boolean				crane_gtw;

	@XmlTransient
	private Date				datetime;

	@XmlTransient
	private Date				first_down;

	// rif sws
	private Integer				id;

	private Integer				iddetailscheduleship;

	@XmlTransient
	private Integer				invoicing_cycle;

	@XmlTransient
	private Date				last_down;

	private String				notedetail;

	@XmlTransient
	private String				rain;

	// using only for view, rif_mct in table scheduleship
	@XmlTransient
	private Integer				rif_mct;

	// using only for view, rif_mct in table scheduleship
	@XmlTransient
	private Integer				rif_sws;

	@XmlTransient
	private String				sky;

	@XmlTransient
	private String				temperature;

	private Double				timework;

	private Integer				volume;

	private Integer				volume_tw_mct;

	private Integer				volumeunderboard;

	@XmlTransient
	private Integer				volumeunderboard_sws;

	@XmlTransient
	private String				wind;

	public Date getActivity_end() {
		return this.activity_end;
	}

	public Date getActivity_start() {
		return this.activity_start;
	}

	public Integer getCrane() {
		return this.crane;
	}

	public Boolean getCrane_gtw() {
		return this.crane_gtw;
	}

	public Date getDatetime() {
		return this.datetime;
	}

	public Date getFirst_down() {
		return this.first_down;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getIddetailscheduleship() {
		return this.iddetailscheduleship;
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

	public Integer getRif_mct() {
		return this.rif_mct;
	}

	public Integer getRif_sws() {
		return this.rif_sws;
	}

	public String getSky() {
		return this.sky;
	}

	public String getTemperature() {
		return this.temperature;
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

	public Integer getVolumeunderboard_sws() {
		return this.volumeunderboard_sws;
	}

	public String getWind() {
		return this.wind;
	}

	public void setActivity_end(final Date activity_end) {
		this.activity_end = activity_end;
	}

	public void setActivity_start(final Date activity_start) {
		this.activity_start = activity_start;
	}

	public void setCrane(final Integer crane) {
		this.crane = crane;
	}

	public void setCrane_gtw(final Boolean crane_gtw) {
		this.crane_gtw = crane_gtw;
	}

	public void setDatetime(final Date datetime) {
		this.datetime = datetime;
	}

	public void setFirst_down(final Date first_down) {
		this.first_down = first_down;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setIddetailscheduleship(final Integer iddetailscheduleship) {
		this.iddetailscheduleship = iddetailscheduleship;
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

	public void setRif_mct(final Integer rif_mct) {
		this.rif_mct = rif_mct;
	}

	public void setRif_sws(final Integer rif_sws) {
		this.rif_sws = rif_sws;
	}

	public void setSky(final String sky) {
		this.sky = sky;
	}

	public void setTemperature(final String temperature) {
		this.temperature = temperature;
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

	public void setVolumeunderboard_sws(final Integer volumeunderboard_sws) {
		this.volumeunderboard_sws = volumeunderboard_sws;
	}

	public void setWind(final String wind) {
		this.wind = wind;
	}

}
