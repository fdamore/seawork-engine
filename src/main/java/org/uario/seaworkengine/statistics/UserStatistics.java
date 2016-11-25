package org.uario.seaworkengine.statistics;

import java.util.Date;

import org.uario.seaworkengine.model.Person;

public class UserStatistics implements Comparable<UserStatistics> {

	private Date	date;

	private String	department;
	private Person	person;
	private Double	saturation;

	private Double	saturation_month;

	private Double	saturation_prec_month;

	private String	shift_perc_1;

	private String	shift_perc_1_base;

	private String	shift_perc_2;

	private String	shift_perc_2_base;

	private String	shift_perc_3;

	private String	shift_perc_3_base;

	private String	shift_perc_4;

	private String	shift_perc_4_base;

	private String	userRoles;

	private String	work_current;

	private String	work_current_month;

	private String	work_current_week;

	private String	work_current_year;

	private String	work_holiday;

	private String	work_holiday_perc;

	private String	work_sunday;

	private String	work_sunday_perc;

	private String	working_series;

	@Override
	public int compareTo(final UserStatistics o) {
		if (o == null) {
			return -1;
		}
		if (!(o instanceof UserStatistics)) {
			return -1;
		}
		if (this.person.getLastname() == null) {
			return -1;
		}
		final UserStatistics item = o;

		if (item.getPerson() == null) {
			return -1;
		}
		if (item.getPerson().getLastname() == null) {
			return 1;
		}
		return this.person.getLastname().compareTo(item.getPerson().getLastname());
	}

	public Date getDate() {
		return this.date;
	}

	public String getDepartment() {
		return this.department;
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

	public Double getSaturation_prec_month() {
		return this.saturation_prec_month;
	}

	public String getShift_perc_1() {
		return this.shift_perc_1;
	}

	public String getShift_perc_1_base() {
		return this.shift_perc_1_base;
	}

	public String getShift_perc_2() {
		return this.shift_perc_2;
	}

	public String getShift_perc_2_base() {
		return this.shift_perc_2_base;
	}

	public String getShift_perc_3() {
		return this.shift_perc_3;
	}

	public String getShift_perc_3_base() {
		return this.shift_perc_3_base;
	}

	public String getShift_perc_4() {
		return this.shift_perc_4;
	}

	public String getShift_perc_4_base() {
		return this.shift_perc_4_base;
	}

	public String getUserRoles() {
		return this.userRoles;
	}

	public String getWork_current() {
		return this.work_current;
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

	public String getWork_holiday() {
		return this.work_holiday;
	}

	public String getWork_holiday_perc() {
		return this.work_holiday_perc;
	}

	public String getWork_sunday() {
		return this.work_sunday;
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

	public void setDepartment(final String department) {
		this.department = department;
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

	public void setSaturation_prec_month(final Double saturation_prec_month) {
		this.saturation_prec_month = saturation_prec_month;
	}

	public void setShift_perc_1(final String shift_perc_1) {
		this.shift_perc_1 = shift_perc_1;
	}

	public void setShift_perc_1_base(String shift_perc_1_base) {
		this.shift_perc_1_base = shift_perc_1_base;
	}

	public void setShift_perc_2(final String shift_perc_2) {
		this.shift_perc_2 = shift_perc_2;
	}

	public void setShift_perc_2_base(String shift_perc_2_base) {
		this.shift_perc_2_base = shift_perc_2_base;
	}

	public void setShift_perc_3(final String shift_perc_3) {
		this.shift_perc_3 = shift_perc_3;
	}

	public void setShift_perc_3_base(String shift_perc_3_base) {
		this.shift_perc_3_base = shift_perc_3_base;
	}

	public void setShift_perc_4(final String shift_perc_4) {
		this.shift_perc_4 = shift_perc_4;
	}

	public void setShift_perc_4_base(String shift_perc_4_base) {
		this.shift_perc_4_base = shift_perc_4_base;
	}

	public void setUserRoles(final String userRoles) {
		this.userRoles = userRoles;
	}

	public void setWork_current(final String work_current) {
		this.work_current = work_current;
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

	public void setWork_holiday(String work_holiday) {
		this.work_holiday = work_holiday;
	}

	public void setWork_holiday_perc(final String work_holiday_perc) {
		this.work_holiday_perc = work_holiday_perc;
	}

	public void setWork_sunday(String work_sunday) {
		this.work_sunday = work_sunday;
	}

	public void setWork_sunday_perc(final String work_sunday_perc) {
		this.work_sunday_perc = work_sunday_perc;
	}

	public void setWorking_series(final String working_series) {
		this.working_series = working_series;
	}

}
