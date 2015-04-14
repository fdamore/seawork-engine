package org.uario.seaworkengine.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlTransient;

public class DetailFinalScheduleShip implements Serializable {

	@XmlTransient
	private static final long	serialVersionUID	= 1L;

	private Integer				crane;

	private Integer				id;

	private Integer				iddetailscheduleship;

	private Double				timework;

	private Integer				volume;

	public Integer getCrane() {
		return this.crane;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getIddetailscheduleship() {
		return this.iddetailscheduleship;
	}

	public Double getTimework() {
		return this.timework;
	}

	public Integer getVolume() {
		return this.volume;
	}

	public void setCrane(final Integer crane) {
		this.crane = crane;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setIddetailscheduleship(final Integer iddetailscheduleship) {
		this.iddetailscheduleship = iddetailscheduleship;
	}

	public void setTimework(final Double timework) {
		this.timework = timework;
	}

	public void setVolume(final Integer volume) {
		this.volume = volume;
	}

}
