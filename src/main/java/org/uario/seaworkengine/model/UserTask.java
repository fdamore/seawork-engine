package org.uario.seaworkengine.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "org.uario.seaworkengine.model")
public class UserTask implements Serializable, Comparable<UserTask> {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Boolean				changeshift;

	private String				code;

	private Boolean				delayoperation;

	private String				description;

	private Boolean				endoperation;

	private Boolean				hiddentask;

	private Integer				id;

	private Boolean				internal;

	private Boolean				isabsence;

	private Boolean				isPP;

	private Boolean				isRZ;

	private Boolean				justificatory;

	private Boolean				overflow;

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

	public Boolean getChangeshift() {
		return this.changeshift;
	}

	public String getCode() {
		return this.code;
	}

	public Boolean getDelayoperation() {
		return this.delayoperation;
	}

	public String getDescription() {
		return this.description;
	}

	public Boolean getEndoperation() {
		return this.endoperation;
	}

	public Boolean getHiddentask() {
		return this.hiddentask;
	}

	public Integer getId() {
		return this.id;
	}

	public Boolean getInternal() {
		return this.internal;
	}

	public Boolean getIsabsence() {
		return this.isabsence;
	}

	public Boolean getIsPP() {
		return this.isPP;
	}

	public Boolean getIsRZ() {
		return this.isRZ;
	}

	public Boolean getJustificatory() {
		return this.justificatory;
	}

	public Boolean getOverflow() {
		return this.overflow;
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

	public void setChangeshift(final Boolean changeshift) {
		this.changeshift = changeshift;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public void setDelayoperation(final Boolean delayoperation) {
		this.delayoperation = delayoperation;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setEndoperation(final Boolean endoperation) {
		this.endoperation = endoperation;
	}

	public void setHiddentask(final Boolean hiddentask) {
		this.hiddentask = hiddentask;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setInternal(final Boolean internal) {
		this.internal = internal;
	}

	public void setIsabsence(final Boolean isabsence) {
		this.isabsence = isabsence;
	}

	public void setIsPP(final Boolean isPP) {
		this.isPP = isPP;
	}

	public void setIsRZ(final Boolean isRZ) {
		this.isRZ = isRZ;
	}

	public void setJustificatory(final Boolean justificatory) {
		this.justificatory = justificatory;
	}

	public void setOverflow(final Boolean overflow) {
		this.overflow = overflow;
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