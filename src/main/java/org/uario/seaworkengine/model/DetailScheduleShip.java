package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(namespace = "org.uario.seaworkengine.model")
@XmlAccessorType(XmlAccessType.FIELD)
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
	private Date	arrivaldate;

	/**
	 * Used only in view mode
	 */
	private Date	departuredate;
	/**
	 * Used only in view mode
	 */
	@XmlTransient
	private String	firstname;

	/**
	 * Used only in view mode
	 */
	@XmlTransient
	private String	firstnameSecondUser;

	private Integer	handswork;

	private Integer	id;

	// ship id
	private Integer	id_ship;

	private Integer	idscheduleship;

	private Integer	idseconduser;

	private Integer	iduser;

	/**
	 * Used only in view mode
	 */
	@XmlTransient
	private String	lastname;
	/**
	 * Used only in view mode
	 */
	@XmlTransient
	private String	lastnameSecondUser;

	private Integer	menwork;

	// ship name
	private String	name;

	private String	note;

	private String	notedetail;

	private String	operation;

	private Integer	shift;

	private Date	shiftdate;

	@Override
	public int compareTo(final Person o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Date getArrivaldate() {
		return this.arrivaldate;
	}

	public Date getDeparturedate() {
		return this.departuredate;
	}

	public String getFirstname() {
		return this.firstname;
	}

	public String getFirstnameSecondUser() {
		return this.firstnameSecondUser;
	}

	public Integer getHandswork() {
		return this.handswork;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getId_ship() {
		return this.id_ship;
	}

	public Integer getIdscheduleship() {
		return this.idscheduleship;
	}

	public Integer getIdseconduser() {
		return this.idseconduser;
	}

	public Integer getIduser() {
		return this.iduser;
	}

	public String getLastnameSecondUser() {
		return this.lastnameSecondUser;
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

	public String getNotedetail() {
		return this.notedetail;
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

	public Date getShiftdate() {
		return this.shiftdate;
	}

	public void setArrivaldate(final Date shiparrivaldate) {
		this.arrivaldate = shiparrivaldate;
	}

	public void setDeparturedate(final Date shipdeparturedate) {
		this.departuredate = shipdeparturedate;
	}

	public void setFirstname(final String firstname) {
		this.firstname = firstname;
	}

	public void setFirstnameSecondUser(final String firstnameSecondUser) {
		this.firstnameSecondUser = firstnameSecondUser;
	}

	public void setHandswork(final Integer handswork) {
		this.handswork = handswork;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setId_ship(final Integer id_ship) {
		this.id_ship = id_ship;
	}

	public void setIdscheduleship(final Integer idscheduleship) {
		this.idscheduleship = idscheduleship;
	}

	public void setIdseconduser(final Integer idseconduser) {
		this.idseconduser = idseconduser;
	}

	public void setIduser(final Integer iduser) {
		this.iduser = iduser;
	}

	public void setLastnameSecondUser(final String lastnameSecondUser) {
		this.lastnameSecondUser = lastnameSecondUser;
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

	public void setNotedetail(final String notedetail) {
		this.notedetail = notedetail;
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

	public void setShiftdate(final Date shiftdate) {
		this.shiftdate = shiftdate;
	}

}
