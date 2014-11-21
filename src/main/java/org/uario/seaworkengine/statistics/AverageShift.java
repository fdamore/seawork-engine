package org.uario.seaworkengine.statistics;

import java.io.Serializable;

public class AverageShift implements Serializable, Comparable<AverageShift> {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Double				avg_program_time;

	private Integer				shift;

	@Override
	public int compareTo(final AverageShift o) {
		try {
			return this.getAvg_program_time().compareTo(o.getAvg_program_time());
		}
		catch (final NullPointerException e) {
			return 0;
		}
	}

	public Double getAvg_program_time() {
		return this.avg_program_time;
	}

	public Integer getShift() {
		return this.shift;
	}

	public void setAvg_program_time(final Double avg_program_time) {
		this.avg_program_time = avg_program_time;
	}

	public void setShift(final Integer shift) {
		this.shift = shift;
	}

}