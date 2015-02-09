package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.sql.Timestamp;

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
	private Timestamp	arrivaldate;

	/**
	 * Used only in view mode
	 */
	private Timestamp	departuredate;
	/**
	 * Used only in view mode
	 */
	@XmlTransient
	private String		firstname;

	private Integer		handswork;
	private Integer		id;
	private Integer		idscheduleship;
	private Integer		iduser;

	/**
	 * Used only in view mode
	 */
	@XmlTransient
	private String		lastname;
	private Integer		menwork;
	private String		name;
	private String		note;

	private String		operation;

	private Integer		shift;

	private Timestamp	shiftdate;

	@Override
	public int compareTo(final Person o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Timestamp getArrivaldate() {
		return this.arrivaldate;
	}

	public Timestamp getDeparturedate() {
		return this.departuredate;
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

	public Integer getMenwork() {
		return this.menwork;
	}

	public String getName() {
		return this.name;
	}

	public String getNote() {
		return this.note;
	}

	public String getOperation() {
		return this.operation;
	}

	public String getSecondname() {
		return this.lastname;
	}

	public Integer getShift() {
		return this.shift;
	}

	public Timestamp getShiftdate() {
		return this.shiftdate;
	}

	public void setArrivaldate(final Timestamp shiparrivaldate) {
		this.arrivaldate = shiparrivaldate;
	}

	public void setDeparturedate(final Timestamp shipdeparturedate) {
		this.departuredate = shipdeparturedate;
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

	public void setMenwork(final Integer menwork) {
		this.menwork = menwork;
	}

	public void setName(final String shipname) {
		this.name = shipname;
	}

	public void setNote(final String shipnote) {
		this.note = shipnote;
	}

	public void setOperation(final String operation) {
		this.operation = operation;
	}

	public void setSecondname(final String secondname) {
		this.lastname = secondname;
	}

	public void setShift(final Integer shift) {
		this.shift = shift;
	}

	public void setShiftdate(final Timestamp shiftdate) {
		this.shiftdate = shiftdate;
	}

}
