package org.uario.seaworkengine.web.services.handler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "org.uario.seaworkengine.model")
@XmlAccessorType(XmlAccessType.FIELD)
public class WorkerShift implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private int					number;

	private List<Worker>		workers				= new ArrayList<Worker>();

	public int getNumber() {
		return this.number;
	}

	public List<Worker> getWorkers() {
		return this.workers;
	}

	public void setNumber(final int number) {
		this.number = number;
	}

	public void setWorkers(final List<Worker> workers) {
		this.workers = workers;
	}

}