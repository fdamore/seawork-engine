package org.uario.seaworkengine.statistics;

import java.io.Serializable;

public class AverageShift implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private Double avg_program_time;

	private Integer shift;

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