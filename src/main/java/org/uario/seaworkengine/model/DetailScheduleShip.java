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
	private static final long serialVersionUID = 1L;

	public static long getSerialversionuid() {
		return DetailScheduleShip.serialVersionUID;
	}

	private Date activity_end;

	private Date activity_start;

	/**
	 * Used only in view mode
	 */
	private Date arrivaldate;

	// only for view
	private Integer customer_id;

	/**
	 * Used only in view mode
	 */
	private Date departuredate;

	private Date first_down;

	/**
	 * Used only for visualization purpose *
	 */
	private String firstOperativeName;

	private Integer handswork;

	private Integer id;

	// ship id
	private Integer id_ship;

	private Integer idscheduleship;

	private Integer idseconduser;

	private Integer iduser;

	private Date last_down;

	private Integer menwork;

	// ship name
	private String name;

	private String note;

	private String notedetail;

	private String operation;

	private String rain;

	/**
	 * Used only for visualization purpose *
	 */
	private String secondOperativeName;

	private Integer shift;

	private Date shiftdate;

	private String sky;

	private String temperature;

	private String wind;

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

	public Date getFirst_down() {
		return this.first_down;
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

	public Date getLast_down() {
		return this.last_down;
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

	public String getRain() {
		return this.rain;
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

	public String getSky() {
		return this.sky;
	}

	public String getTemperature() {
		return this.temperature;
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

	public void setArrivaldate(final Date shiparrivaldate) {
		this.arrivaldate = shiparrivaldate;
	}

	public void setCustomer_id(final Integer customer_id) {
		this.customer_id = customer_id;
	}

	public void setDeparturedate(final Date shipdeparturedate) {
		this.departuredate = shipdeparturedate;
	}

	public void setFirst_down(final Date first_down) {
		this.first_down = first_down;
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

	public void setLast_down(final Date last_down) {
		this.last_down = last_down;
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

	public void setRain(final String rain) {
		this.rain = rain;
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

	public void setSky(final String sky) {
		this.sky = sky;
	}

	public void setTemperature(final String temperature) {
		this.temperature = temperature;
	}

	public void setWind(final String wind) {
		this.wind = wind;
	}

}
