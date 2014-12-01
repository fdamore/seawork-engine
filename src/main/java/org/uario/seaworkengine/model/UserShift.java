package org.uario.seaworkengine.model;

import java.io.Serializable;

public class UserShift implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Boolean				accident_shift;

	private Boolean				break_shift;
	private String				code;
	private String				description;

	private Boolean				disease_shift;

	private Boolean				forceable;

	private Integer				id;

	private Boolean				presence;

	private Boolean				waitbreak_shift;

	public Boolean getAccident_shift() {
		return this.accident_shift;
	}

	public Boolean getBreak_shift() {
		return this.break_shift;
	}

	public String getCode() {
		return this.code;
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

	public Boolean getWaitBreak_shift() {
		return this.waitbreak_shift;
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

	public void setWaitbreak_shift(final Boolean waitbreak_shift) {
		this.waitbreak_shift = waitbreak_shift;
	}

	@Override
	public String toString() {
		return "" + this.getCode() + " - " + this.getDescription();
	}

}