package org.uario.seaworkengine.model;

import java.io.Serializable;

public class WorkerStatus implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private String				description;

	private Integer				id;

	private String				note;

	public WorkerStatus() {
		super();
	}
	
	public WorkerStatus(String status) {
		this.description = status;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof WorkerStatus)) {
			return false;
		}

		final WorkerStatus sts_itm = (WorkerStatus) obj;

		if (this.getDescription().equals(sts_itm.getDescription())) {
			return true;
		}

		return false;
	}
	
	public String getDescription() {
		return this.description;
	}

	public Integer getId() {
		return this.id;
	}

	public String getNote() {
		return this.note;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String toString() {
		
		return this.description;
	}
	
}
