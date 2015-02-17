package org.uario.seaworkengine.web.services.synchmodel;

public class TaskRunner {

	private String		descrizione;
	private String		entrata;
	private Integer		ID;
	private WorkerShift	shift;
	private String		uscita;

	public String getDescrizione() {
		return this.descrizione;
	}

	public String getEntrata() {
		return this.entrata;
	}

	public Integer getID() {
		return this.ID;
	}

	public WorkerShift getShift() {
		return this.shift;
	}

	public String getUscita() {
		return this.uscita;
	}

	public void setDescrizione(final String descrizione) {
		this.descrizione = descrizione;
	}

	public void setEntrata(final String entrata) {
		this.entrata = entrata;
	}

	public void setID(final Integer iD) {
		this.ID = iD;
	}

	public void setShift(final WorkerShift shift) {
		this.shift = shift;
	}

	public void setUscita(final String uscita) {
		this.uscita = uscita;
	}

}
