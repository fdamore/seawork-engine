package org.uario.seaworkengine.web.services.handler;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "org.uario.seaworkengine.web.services.synchmodel")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaskRunner implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private String				board;

	private String				crane;

	private String				descrizione;

	private Date				entrata;

	private Integer				ID;

	private WorkerShift			shift;

	private Integer				ship_id;

	private Date				uscita;

	public String getBoard() {
		return this.board;
	}

	public String getCrane() {
		return this.crane;
	}

	public String getDescrizione() {
		return this.descrizione;
	}

	public Date getEntrata() {
		return this.entrata;
	}

	public Integer getID() {
		return this.ID;
	}

	public WorkerShift getShift() {
		return this.shift;
	}

	public Integer getShip_id() {
		return this.ship_id;
	}

	public Date getUscita() {
		return this.uscita;
	}

	public void setBoard(final String board) {
		this.board = board;
	}

	public void setCrane(final String crane) {
		this.crane = crane;
	}

	public void setDescrizione(final String descrizione) {
		this.descrizione = descrizione;
	}

	public void setEntrata(final Date entrata) {
		this.entrata = entrata;
	}

	public void setID(final Integer iD) {
		this.ID = iD;
	}

	public void setShift(final WorkerShift shift) {
		this.shift = shift;
	}

	public void setShip_id(final Integer ship_id) {
		this.ship_id = ship_id;
	}

	public void setUscita(final Date uscita) {
		this.uscita = uscita;
	}

}
