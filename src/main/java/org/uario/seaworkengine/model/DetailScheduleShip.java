package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

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

	private String	crane;

	/**
	 * Used only in view mode
	 */
	private Date	departuredate;

	/**
	 * Used only for visualization purpose *
	 */
	private String	firstOperativeName;

	private Integer	handswork;

	private Integer	id;

	// ship id
	private Integer	id_ship;

	private Integer	idscheduleship;

	private Integer	idseconduser;

	private Integer	iduser;

	private Integer	menwork;

	// ship name
	private String	name;

	private String	note;

	private String	notedetail;

	private String	operation;

	/**
	 * Used only for visualization purpose *
	 */
	private String	secondOperativeName;

	private Integer	shift;

	private Date	shiftdate;

	private Double	timework;

	private Double	volume;

	@Override
	public int compareTo(final Person o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Date getArrivaldate() {
		return arrivaldate;
	}

	public String getCrane() {
		return crane;
	}

	public Date getDeparturedate() {
		return departuredate;
	}

	public String getFirstOperativeName() {
		return firstOperativeName;
	}

	public Integer getHandswork() {
		return handswork;
	}

	public Integer getId() {
		return id;
	}

	public Integer getId_ship() {
		return id_ship;
	}

	public Integer getIdscheduleship() {
		return idscheduleship;
	}

	public Integer getIdseconduser() {
		return idseconduser;
	}

	public Integer getIduser() {
		return iduser;
	}

	public Integer getMenwork() {
		return menwork;
	}

	public String getName() {
		return name;
	}

	public String getNote() {
		return note;
	}

	public String getNotedetail() {
		return notedetail;
	}

	public String getOperation() {
		return operation;
	}

	public String getSecondOperativeName() {
		return secondOperativeName;
	}

	public Integer getShift() {
		return shift;
	}

	public Date getShiftdate() {
		return shiftdate;
	}

	public Double getTimework() {
		return timework;
	}

	public Double getVolume() {
		return volume;
	}

	public void setArrivaldate(final Date shiparrivaldate) {
		arrivaldate = shiparrivaldate;
	}

	public void setCrane(final String crane) {
		this.crane = crane;
	}

	public void setDeparturedate(final Date shipdeparturedate) {
		departuredate = shipdeparturedate;
	}

	public void setFirstOperativeName(final String firstOperativeName) {
		this.firstOperativeName = firstOperativeName;
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

	public void setMenwork(final Integer menwork) {
		this.menwork = menwork;
	}

	public void setName(final String shipname) {
		name = shipname;
	}

	public void setNote(final String shipnote) {
		note = shipnote;
	}

	public void setNotedetail(final String notedetail) {
		this.notedetail = notedetail;
	}

	public void setOperation(final String operation) {
		this.operation = operation;
	}

	public void setSecondOperativeName(final String secondOperativeName) {
		this.secondOperativeName = secondOperativeName;
	}

	public void setShift(final Integer shift) {
		this.shift = shift;
	}

	public void setShiftdate(final Date shiftdate) {
		this.shiftdate = shiftdate;
	}

	public void setTimework(final Double timework) {
		this.timework = timework;
	}

	public void setVolume(final Double volume) {
		this.volume = volume;
	}

}
