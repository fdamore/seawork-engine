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

	// rif sws
	private Integer				id;

	private Integer				id_crane;

	private Integer				iddetailscheduleship;

	@XmlTransient
	private Integer				invoicing_cycle;

	private Integer				menwork_activityh;

	private String				notedetail;

	// using only for view, rif_mct in table scheduleship
	@XmlTransient
	private String				rif_mct;

	// using only for view, rif_mct in table scheduleship
	@XmlTransient
	private Integer				rif_sws;

	private Double				timework;

	private Integer				volume;

	private Integer				volume_tw_mct;

	private Integer				volumeunderboard;

	@XmlTransient
	private Integer				volumeunderboard_sws;

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

	public Integer getId() {
		return this.id;
	}

	public Integer getId_crane() {
		return this.id_crane;
	}

	public Integer getIddetailscheduleship() {
		return this.iddetailscheduleship;
	}

	public Integer getInvoicing_cycle() {
		return this.invoicing_cycle;
	}

	public Integer getMenwork_activityh() {
		return this.menwork_activityh;
	}

	public String getNotedetail() {
		return this.notedetail;
	}

	public String getRif_mct() {
		return this.rif_mct;
	}

	public Integer getRif_sws() {
		return this.rif_sws;
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

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setId_crane(final Integer id_crane) {
		this.id_crane = id_crane;
	}

	public void setIddetailscheduleship(final Integer iddetailscheduleship) {
		this.iddetailscheduleship = iddetailscheduleship;
	}

	public void setInvoicing_cycle(final Integer invoicing_cycle) {
		this.invoicing_cycle = invoicing_cycle;
	}

	public void setMenwork_activityh(final Integer menwork_activityh) {
		this.menwork_activityh = menwork_activityh;
	}

	public void setNotedetail(final String notedetail) {
		this.notedetail = notedetail;
	}

	public void setRif_mct(final String rif_mct) {
		this.rif_mct = rif_mct;
	}

	public void setRif_sws(final Integer rif_sws) {
		this.rif_sws = rif_sws;
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

}
