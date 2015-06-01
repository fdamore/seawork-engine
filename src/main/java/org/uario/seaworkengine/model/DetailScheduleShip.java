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

	private Date	activity_end;

	private Date	activity_start;

	/**
	 * Used only in view mode
	 */
	private Date	arrivaldate;

	// only for view
	private Integer	customer_id;

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

	@Override
	public int compareTo(final Person o) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Date getActivity_end() {
		return this.activity_end;
	}

	public Date getActivity_start() {
		return this.activity_start;
	}

	public Date getArrivaldate() {
		return this.arrivaldate;
	}

	public Integer getCustomer_id() {
		return this.customer_id;
	}

	public Date getDeparturedate() {
		return this.departuredate;
	}

	public String getFirstOperativeName() {
		return this.firstOperativeName;
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

	public String getSecondOperativeName() {
		return this.secondOperativeName;
	}

	public Integer getShift() {
		return this.shift;
	}

	public Date getShiftdate() {
		return this.shiftdate;
	}

	public void setActivity_end(final Date activity_end) {
		this.activity_end = activity_end;
	}

	public void setActivity_start(final Date activity_start) {
		this.activity_start = activity_start;
	}

	public void setArrivaldate(final Date shiparrivaldate) {
		this.arrivaldate = shiparrivaldate;
	}

	public void setCustomer_id(final Integer customer_id) {
		this.customer_id = customer_id;
	}

	public void setDeparturedate(final Date shipdeparturedate) {
		this.departuredate = shipdeparturedate;
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

	public void setSecondOperativeName(final String secondOperativeName) {
		this.secondOperativeName = secondOperativeName;
	}

	public void setShift(final Integer shift) {
		this.shift = shift;
	}

	public void setShiftdate(final Date shiftdate) {
		this.shiftdate = shiftdate;
	}

}
