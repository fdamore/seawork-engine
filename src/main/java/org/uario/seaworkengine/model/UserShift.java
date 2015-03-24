package org.uario.seaworkengine.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(namespace = "org.uario.seaworkengine.model")
public class UserShift implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private Boolean accident_shift = Boolean.FALSE;

	private Boolean break_shift = Boolean.FALSE;

	private String code;

	private Boolean daily_shift;

	private String description;

	private Boolean disease_shift = Boolean.FALSE;

	private Boolean forceable;

	private Integer id;

	private Boolean presence;

	private Boolean recorded;

	private Boolean standard_shift;

	private Boolean waitbreak_shift = Boolean.FALSE;

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj instanceof UserShift) {

			final UserShift itm = (UserShift) obj;
			return this.getCode().equals(itm.getCode());

		} else {
			return false;
		}
	}

	public Boolean getAccident_shift() {
		return this.accident_shift;
	}

	public Boolean getBreak_shift() {
		return this.break_shift;
	}

	public String getCode() {
		return this.code;
	}

	public Boolean getDaily_shift() {
		return this.daily_shift;
	}

	public String getDescription() {
		return this.description;
	}

	public Boolean getDisease_shift() {
		return this.disease_shift;
	}

	public Boolean getForceable() {
		return this.forceable;
	}

	public Integer getId() {
		return this.id;
	}

	public Boolean getPresence() {
		return this.presence;
	}

	public Boolean getRecorded() {
		return this.recorded;
	}

	public Boolean getStandard_shift() {
		return this.standard_shift;
	}

	public Boolean getWaitbreak_shift() {
		return this.waitbreak_shift;
	}

	public Boolean isDefault() {
		if (this.waitbreak_shift || this.disease_shift || this.break_shift || this.accident_shift || this.standard_shift
				|| this.daily_shift) {
			return true;
		}
		return false;
	}

	public Boolean isDefaultBreak() {
		if (this.waitbreak_shift || this.disease_shift || this.break_shift || this.accident_shift) {
			return true;
		}
		return false;
	}

	public void setAccident_shift(final Boolean accident_shift) {
		this.accident_shift = accident_shift;
	}

	public void setBreak_shift(final Boolean break_shift) {
		this.break_shift = break_shift;
	}

	public void setCode(final String code) {
		this.code = code;
	}

	public void setDaily_shift(final Boolean daily_shift) {
		this.daily_shift = daily_shift;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public void setDisease_shift(final Boolean disease_shift) {
		this.disease_shift = disease_shift;
	}

	public void setForceable(final Boolean forceable) {
		this.forceable = forceable;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setPresence(final Boolean presence) {
		this.presence = presence;

	}

	public void setRecorded(final Boolean recorded) {
		this.recorded = recorded;
	}

	public void setStandard_shift(final Boolean standard_shift) {
		this.standard_shift = standard_shift;
	}

	public void setWaitbreak_shift(final Boolean waitbreak_shift) {
		this.waitbreak_shift = waitbreak_shift;
	}

	@Override
	public String toString() {
		return "" + this.getCode() + " - " + this.getDescription();
	}

}