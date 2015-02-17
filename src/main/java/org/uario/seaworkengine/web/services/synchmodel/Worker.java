package org.uario.seaworkengine.web.services.synchmodel;

import java.util.ArrayList;
import java.util.List;

public class Worker {

	private final List<Badged>		badgeList	= new ArrayList<Badged>();

	private String					entrata;

	private String					gru;

	private String					mansione;

	public String					nave;

	private String					note;

	private int						shift;

	private final List<TaskRunner>	tasks		= new ArrayList<TaskRunner>();

	private String					uscita;

	private Integer					utente;

	public List<Badged> getBadgeList() {
		return this.badgeList;
	}

	public String getEntrata() {
		return this.entrata;
	}

	public String getGru() {
		return this.gru;
	}

	public String getMansione() {
		return this.mansione;
	}

	public String getNave() {
		return this.nave;
	}

	public String getNote() {
		return this.note;
	}

	public int getShift() {
		return this.shift;
	}

	public List<TaskRunner> getTasks() {
		return this.tasks;
	}

	public String getUscita() {
		return this.uscita;
	}

	public Integer getUtente() {
		return this.utente;
	}

	public void setEntrata(final String entrata) {
		this.entrata = entrata;
	}

	public void setGru(final String gru) {
		this.gru = gru;
	}

	public void setMansione(final String mansione) {
		this.mansione = mansione;
	}

	public void setNave(final String nave) {
		this.nave = nave;
	}

	public void setNote(final String note) {
		this.note = note;
	}

	public void setShift(final int shift) {
		this.shift = shift;
	}

	public void setUscita(final String uscita) {
		this.uscita = uscita;
	}

	public void setUtente(final Integer utente) {
		this.utente = utente;
	}

}
