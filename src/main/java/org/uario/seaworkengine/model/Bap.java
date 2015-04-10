package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "org.uario.seaworkengine.model")
@XmlAccessorType(XmlAccessType.FIELD)
public class Bap implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Integer				crane;

	private Date				date_request;

	private Integer				id;

	private Integer				id_ship;

	/**
	 * used for visualization only
	 */
	private String				ship_name;

	private Integer				shit_no;

	private Integer				volume;

	public Integer getCrane() {
		return this.crane;
	}

	public Date getDate_request() {
		return this.date_request;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getId_ship() {
		return this.id_ship;
	}

	public String getShip_name() {
		return this.ship_name;
	}

	public Integer getShit_no() {
		return this.shit_no;
	}

	public Integer getVolume() {
		return this.volume;
	}

	public void setCrane(final Integer crane) {
		this.crane = crane;
	}

	public void setDate_request(final Date date_request) {
		this.date_request = date_request;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setId_ship(final Integer id_ship) {
		this.id_ship = id_ship;
	}

	public void setShip_name(final String ship_name) {
		this.ship_name = ship_name;
	}

	public void setShit_no(final Integer shit_no) {
		this.shit_no = shit_no;
	}

	public void setVolume(final Integer volume) {
		this.volume = volume;
	}

}
