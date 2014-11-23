package org.uario.seaworkengine.statistics;

import java.io.Serializable;

public class RateShift implements Serializable, Comparable<RateShift> {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Double				rate;

	private Integer				shift;

	@Override
	public int compareTo(final RateShift o) {
		try {
			return this.getRate().compareTo(o.getRate());
		} catch (final NullPointerException e) {
			return 0;
		}
	}

	public Double getRate() {
		return this.rate;
	}

	public Integer getShift() {
		return this.shift;
	}

	public void setRate(final Double rate) {
		this.rate = rate;
	}

	public void setShift(final Integer shift) {
		this.shift = shift;
	}

}