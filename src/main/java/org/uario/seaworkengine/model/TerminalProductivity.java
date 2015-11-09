package org.uario.seaworkengine.model;

import java.io.Serializable;

public class TerminalProductivity implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Integer				id;
	private Double				month_tp;
	private Double				productivity;
	private Double				year_tp;

	public Integer getId() {
		return this.id;
	}

	public Double getMonth_tp() {
		return this.month_tp;
	}

	public Double getProductivity() {
		return this.productivity;
	}

	public Double getYear_tp() {
		return this.year_tp;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setMonth_tp(final Double month_tp) {
		this.month_tp = month_tp;
	}

	public void setProductivity(final Double productivity) {
		this.productivity = productivity;
	}

	public void setYear_tp(final Double year_tp) {
		this.year_tp = year_tp;
	}

}
