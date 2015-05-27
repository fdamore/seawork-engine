package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Date;

public class JobCost implements Comparable<Person>, Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private Double awards;

	private Double basicsalary;

	private Integer bill_center;

	// used only in listbox
	private String billcenterDescription;

	private Double business_job_cost;

	private Double contingency;

	private String contractual_level;

	private Date date_from;

	private Date date_to;

	private Double edr;

	private Double final_job_cost;

	private Integer id;

	private Integer id_user;

	private String note;

	private Double shots;

	// using in listbox
	private Double total;

	@Override
	public int compareTo(final Person arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public Double getAwards() {
		return this.awards;
	}

	public Double getBasicsalary() {
		return this.basicsalary;
	}

	public Integer getBill_center() {
		return this.bill_center;
	}

	public String getBillcenterDescription() {
		return this.billcenterDescription;
	}

	public Double getBusiness_job_cost() {
		return this.business_job_cost;
	}

	public Double getContingency() {
		return this.contingency;
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

	public Double getEdr() {
		return this.edr;
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

	public String getNote() {
		return this.note;
	}

	public Double getShots() {
		return this.shots;
	}

	public Double getTotal() {
		return this.total;
	}

	public void setAwards(final Double awards) {
		this.awards = awards;
	}

	public void setBasicsalary(final Double basicsalary) {
		this.basicsalary = basicsalary;
	}

	public void setBill_center(final Integer bill_center) {
		this.bill_center = bill_center;
	}

	public void setBillcenterDescription(final String billcenterDescription) {
		this.billcenterDescription = billcenterDescription;
	}

	public void setBusiness_job_cost(final Double business_job_cost) {
		this.business_job_cost = business_job_cost;
	}

	public void setContingency(final Double contingency) {
		this.contingency = contingency;
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

	public void setEdr(final Double edr) {
		this.edr = edr;
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

	public void setNote(final String note) {
		this.note = note;
	}

	public void setShots(final Double shots) {
		this.shots = shots;
	}

	public void setTotal(final Double total) {
		this.total = total;
	}

}
