package org.uario.seaworkengine.web.services.handler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "org.uario.seaworkengine.web.services.synchmodel")
@XmlAccessorType(XmlAccessType.FIELD)
public class Worker implements Serializable {

	/**
	 *
	 */
	private static final long		serialVersionUID	= 1L;

	private final List<Badge>		badgeList			= new ArrayList<Badge>();

	private String					entrata;

	private int						shift;

	private final List<TaskRunner>	tasks				= new ArrayList<TaskRunner>();

	private String					uscita;

	private Integer					utente;

	public List<Badge> getBadgeList() {
		return this.badgeList;
	}

	public String getEntrata() {
		return this.entrata;
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
