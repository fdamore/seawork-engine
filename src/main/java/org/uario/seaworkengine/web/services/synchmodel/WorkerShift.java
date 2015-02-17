package org.uario.seaworkengine.web.services.synchmodel;

import java.util.ArrayList;
import java.util.List;

public class WorkerShift {

	private int				number;

	private List<Worker>	workers	= new ArrayList<Worker>();

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