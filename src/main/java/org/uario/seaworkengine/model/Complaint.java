package org.uario.seaworkengine.model;

import java.io.Serializable;

public class Complaint implements Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private Integer				id;
	private Integer				id_customer;
	private Integer				month_comp;
	private Integer				numberofcomplaint;
	private Integer				year_comp;

	public Integer getId() {
		return this.id;
	}

	public Integer getId_customer() {
		return this.id_customer;
	}

	public Integer getMonth_comp() {
		return this.month_comp;
	}

	public Integer getNumberofcomplaint() {
		return this.numberofcomplaint;
	}

	public Integer getYear_comp() {
		return this.year_comp;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setId_customer(final Integer id_customer) {
		this.id_customer = id_customer;
	}

	public void setMonth_comp(final Integer month_comp) {
		this.month_comp = month_comp;
	}

	public void setNumberofcomplaint(final Integer numberofcomplaint) {
		this.numberofcomplaint = numberofcomplaint;
	}

	public void setYear_comp(final Integer year_comp) {
		this.year_comp = year_comp;
	}

}
