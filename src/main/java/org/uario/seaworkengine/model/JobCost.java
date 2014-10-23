package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

public class JobCost implements Comparable<Person>, Serializable {

	/**
	 *
	 */
	private static final long	serialVersionUID	= 1L;

	private String				bill_center;

	private Double				business_job_cost;

	private String				contractual_level;

	private Date				date_from;

	private Date				date_to;

	private Double				final_job_cost;

	private Integer				id;

	private Integer				id_user;

	@Override
	public int compareTo(final Person arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getBill_center() {
		return this.bill_center;
	}

	public Double getBusiness_job_cost() {
		return this.business_job_cost;
	}

	public String getContractual_level() {
		return this.contractual_level;
	}

	public Date getDate_from() {
		return this.date_from;
	}

	public Date getDate_to() {
		return this.date_to;
	}

	public Double getFinal_job_cost() {
		return this.final_job_cost;
	}

	public Integer getId() {
		return this.id;
	}

	public Integer getId_user() {
		return this.id_user;
	}

	public void setBill_center(final String bill_center) {
		this.bill_center = bill_center;
	}

	public void setBusiness_job_cost(final Double business_job_cost) {
		this.business_job_cost = business_job_cost;
	}

	public void setContractual_level(final String contractual_level) {
		this.contractual_level = contractual_level;
	}

	public void setDate_from(final Date date_from) {
		this.date_from = date_from;
	}

	public void setDate_to(final Date date_to) {
		this.date_to = date_to;
	}

	public void setFinal_job_cost(final Double final_job_cost) {
		this.final_job_cost = final_job_cost;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public void setId_user(final Integer id_user) {
		this.id_user = id_user;
	}

}
