package org.uario.seaworkengine.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlTransient;

public class DetailScheduleShip implements Comparable<Person>, Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	public static long getSerialversionuid() {
		return DetailScheduleShip.serialVersionUID;
	}

	/**
	 * Used only in view mode
	 */
	@XmlTransient
	private String	firstname;

	private Integer	handswork;
	private Integer	id;
	private Integer	idscheduleship;
	private Integer	iduser;
	private String	operation;

	/**
	 * Used only in view mode
	 */
	@XmlTransient
	private String	secondname;

	private Integer	shift;

	@Override
	public int compareTo(final Person o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getFirstname() {
		return this.firstname;
	}

	public Integer getHandswork() {
		return this.handswork;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getIdscheduleship() {
		return this.idscheduleship;
	}

	public Integer getIduser() {
		return this.iduser;
	}

	public String getOperation() {
		return this.operation;
	}

	public String getSecondname() {
		return this.secondname;
	}

	public Integer getShift() {
		return this.shift;
	}

	public void setFirstname(final String firstname) {
		this.firstname = firstname;
	}

	public void setHandswork(final Integer handswork) {
		this.handswork = handswork;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setIdscheduleship(final Integer idscheduleship) {
		this.idscheduleship = idscheduleship;
	}

	public void setIduser(final Integer iduser) {
		this.iduser = iduser;
	}

	public void setOperation(final String operation) {
		this.operation = operation;
	}

	public void setSecondname(final String secondname) {
		this.secondname = secondname;
	}

	public void setShift(final Integer shift) {
		this.shift = shift;
	}

}
