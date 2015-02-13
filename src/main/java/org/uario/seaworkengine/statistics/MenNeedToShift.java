package org.uario.seaworkengine.statistics;

import java.io.Serializable;

public class MenNeedToShift implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;
	private Integer				menwork;
	private Integer				shift;

	public Integer getMenwork() {
		return this.menwork;
	}

	public Integer getShift() {
		return this.shift;
	}

	public void setMenwork(final Integer menwork) {
		this.menwork = menwork;
	}

	public void setShift(final Integer shift) {
		this.shift = shift;
	}

}
