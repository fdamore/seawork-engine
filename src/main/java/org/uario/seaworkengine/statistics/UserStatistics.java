package org.uario.seaworkengine.statistics;

import java.util.Date;

import org.uario.seaworkengine.model.Person;

public class UserStatistics {

	private Date	date;

	private Person	person;
	private Double	saturation;
	private Double	saturation_month;

	private String	shift_perc_1;
	private String	shift_perc_2;
	private String	shift_perc_3;
	private String	shift_perc_4;
	private String	userRoles;
	private String	work_current_month;
	private String	work_current_week;
	private String	work_current_year;
	private String	work_holiday_perc;

	private String	work_sunday_perc;

	private String	working_series;

	public Date getDate() {
		return this.date;
	}

	public Person getPerson() {
		return this.person;
	}

	public Double getSaturation() {
		return this.saturation;
	}

	public Double getSaturation_month() {
		return this.saturation_month;
	}

	public String getShift_perc_1() {
		return this.shift_perc_1;
	}

	public String getShift_perc_2() {
		return this.shift_perc_2;
	}

	public String getShift_perc_3() {
		return this.shift_perc_3;
	}

	public String getShift_perc_4() {
		return this.shift_perc_4;
	}

	public String getUserRoles() {
		return this.userRoles;
	}

	public String getWork_current_month() {
		return this.work_current_month;
	}

	public String getWork_current_week() {
		return this.work_current_week;
	}

	public String getWork_current_year() {
		return this.work_current_year;
	}

	public String getWork_holiday_perc() {
		return this.work_holiday_perc;
	}

	public String getWork_sunday_perc() {
		return this.work_sunday_perc;
	}

	public String getWorking_series() {
		return this.working_series;
	}

	public void setDate(final Date date) {
		this.date = date;
	}

	public void setPerson(final Person person) {
		this.person = person;
	}

	public void setSaturation(final Double saturation) {
		this.saturation = saturation;
	}

	public void setSaturation_month(final Double saturation_month) {
		this.saturation_month = saturation_month;
	}

	public void setShift_perc_1(final String shift_perc_1) {
		this.shift_perc_1 = shift_perc_1;
	}

	public void setShift_perc_2(final String shift_perc_2) {
		this.shift_perc_2 = shift_perc_2;
	}

	public void setShift_perc_3(final String shift_perc_3) {
		this.shift_perc_3 = shift_perc_3;
	}

	public void setShift_perc_4(final String shift_perc_4) {
		this.shift_perc_4 = shift_perc_4;
	}

	public void setUserRoles(final String userRoles) {
		this.userRoles = userRoles;
	}

	public void setWork_current_month(final String work_current_month) {
		this.work_current_month = work_current_month;
	}

	public void setWork_current_week(final String work_current_week) {
		this.work_current_week = work_current_week;
	}

	public void setWork_current_year(final String work_current_year) {
		this.work_current_year = work_current_year;
	}

	public void setWork_holiday_perc(final String work_holiday_perc) {
		this.work_holiday_perc = work_holiday_perc;
	}

	public void setWork_sunday_perc(final String work_sunday_perc) {
		this.work_sunday_perc = work_sunday_perc;
	}

	public void setWorking_series(final String working_series) {
		this.working_series = working_series;
	}

}
