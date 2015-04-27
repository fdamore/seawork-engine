package org.uario.seaworkengine.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "org.uario.seaworkengine.model")
public class UserTask implements Serializable, Comparable<UserTask> {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private String				code;

	private String				description;

	private Boolean				hiddenoperative;

	private Integer				id;

	private Boolean				isabsence;

	private Boolean				recorded;

	// used in join with user
	private Boolean				task_default;

	@Override
	public int compareTo(final UserTask o) {
		if (o == null) {
			return -1;
		}

		if (this == o) {
			return 0;
		}

		if (o.isabsence) {
			return -1;
		}

		if (this.isabsence) {
			return 1;
		}

		return this.getCode().compareTo(o.getCode());

	}

	@Override
	public boolean equals(final Object obj) {

		if (obj == null) {
			return false;
		}

		if (this == obj) {
			return true;
		}

		if (!(obj instanceof UserTask)) {
			return false;
		}

		final UserTask task = (UserTask) obj;

		return this.getId().equals(task.getId());

	}

	public String getCode() {
		return this.code;
	}

	public String getDescription() {
		return this.description;
	}

	public Boolean getHiddenoperative() {
		return this.hiddenoperative;
	}

	public Integer getId() {
		return this.id;
	}

	public Boolean getIsabsence() {
		return this.isabsence;
	}

	public Boolean getRecorded() {
		return this.recorded;
	}

	public Boolean getTask_default() {
		if (this.task_default == null) {
			return Boolean.FALSE;
		}
		return this.task_default;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setHiddenoperative(final Boolean hiddenoperative) {
		this.hiddenoperative = hiddenoperative;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setIsabsence(final Boolean isabsence) {
		this.isabsence = isabsence;
	}

	public void setRecorded(final Boolean recorded) {
		this.recorded = recorded;
	}

	public void setTask_default(final Boolean task_default) {
		this.task_default = task_default;
	}

	@Override
	public String toString() {
		return "" + this.getCode() + " - " + this.getDescription();
	}

}