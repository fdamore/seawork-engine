package org.uario.seaworkengine.model;

import java.io.Serializable;

public class UserShift implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Boolean				break_shift;

	private String				code;
	private String				description;
	private Boolean				disease_shift;

	private Boolean				expectedbreak_shift;

	private Boolean				forceable;

	private Integer				id;

	private Boolean				injury_shift;

	private Boolean				presence;

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

	public Boolean getExpectedBreak_shift() {
		return this.expectedbreak_shift;
	}

	public Boolean getForceable() {
		return this.forceable;
	}

	public Integer getId() {
		return this.id;
	}

	public Boolean getInjury_shift() {
		return this.injury_shift;
	}

	public Boolean getPresence() {
		return this.presence;
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

	public void setExpectedBreak_shift(final Boolean expected_shift) {
		this.expectedbreak_shift = expected_shift;
	}

	public void setForceable(final Boolean forceable) {
		this.forceable = forceable;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setInjury_shift(final Boolean injury_shift) {
		this.injury_shift = injury_shift;
	}

	public void setPresence(final Boolean presence) {
		this.presence = presence;

	}

	@Override
	public String toString() {
		return "" + this.getCode() + " - " + this.getDescription();
	}

}