package org.uario.seaworkengine.statistics;

import java.io.Serializable;

public class ShipTotal  implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	
	private Integer				totalhands;
	private Integer totalmen;
	public Integer getTotalhands() {
		return totalhands;
	}
	public void setTotalhands(Integer totalhands) {
		this.totalhands = totalhands;
	}
	public Integer getTotalmen() {
		return totalmen;
	}
	public void setTotalmen(Integer totalmen) {
		this.totalmen = totalmen;
	}
	

}
