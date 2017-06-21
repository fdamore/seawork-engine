package org.uario.seaworkengine.utility;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Years;

public class UserReport implements Serializable {

	private static final SimpleDateFormat	date_format			= new SimpleDateFormat("dd-MM-yyyy");

	public static final String				HEADER				= "COGNOME;NOME;STATUS;DIPARTIMENTO;NCFL;NASCITA;DATA NASCITA;ETA;INDIRIZZO;CITTÀ;ZIP;PROVINCIA;TELEFONO;CODICE FISCALE;STATO CIVILE;CARICHI FAMIGLIARI;MATRICOLA;PASS;BUDJE;IDENTIFICATIVO PERSONALE;EDUCAZIONE;PATENTE DI GUIDATA;RILASCIO PATENTE DI GUIDA;TEMPO RILASCIO;POSIZIONE CORRENTE;CENTRO DI COSTO;DA;A;SALARIO BASE;CONTINGENZA;MANSIONE PRINCIPALE;SCELTA TFR;DATA SCELTA TFR";

	/**
	 *
	 */
	private static final long				serialVersionUID	= 1L;

	private String							address;
	private Date							birth_date;
	private String							birth_place;
	private String							city;
	private String							contractual_level;
	private String							current_position;
	private String							department;
	private String							driving_license;
	private Date							driving_license_emission;
	private String							education;
	private String							employee_identification;
	private String							family_charge;
	private String							firstname;
	private String							fiscal_code;
	private Double							job_basicsalary;
	private Double							job_contingency;
	private Date							job_date_from;
	private Date							job_date_to;
	private Double							job_edr;
	private Double							job_shots;
	private String							jobcost;
	private String							lastname;
	private String							marital_status;
	private String							nbudge;
	private String							ncfl;
	private String							npass;
	private String							personal_code;
	private String							phone;
	private String							provincia;
	private String							status;
	private String							task_default;
	private String							tfr_destination;
	private Date							tfr_selection_date;

	private String							zip;

	public String getAddress() {
		return StringUtils.defaultString(this.address, "");

	}

	public Date getBirth_date() {
		return this.birth_date;
	}

	public String getBirth_place() {
		return StringUtils.defaultString(this.birth_place, "");

	}

	public String getCity() {
		return StringUtils.defaultString(this.city, "");
	}

	public String getContractual_level() {
		return StringUtils.defaultString(this.contractual_level, "");

	}

	public String getCurrent_position() {
		return StringUtils.defaultString(this.current_position, "");

	}

	public String getDepartment() {
		return StringUtils.defaultString(this.department, "");

	}

	public String getDriving_license() {
		return StringUtils.defaultString(this.driving_license, "");

	}

	public Date getDriving_license_emission() {
		return this.driving_license_emission;

	}

	public String getDrivingTimeEmission() {
		final Date start = this.getDriving_license_emission();
		if (start == null) {
			return "";
		}

		final DateTime dt_from = new DateTime(start);
		final DateTime dt_to = new DateTime(Calendar.getInstance().getTime());

		final Years y = Years.yearsBetween(dt_from, dt_to);
		final Months m = Months.monthsBetween(dt_from, dt_to);

		return "" + y.getYears() + " " + m.getMonths();
	}

	public String getEducation() {
		return StringUtils.defaultString(this.education, "");

	}

	public String getEmployee_identification() {
		return StringUtils.defaultString(this.employee_identification, "");

	}

	public String getEta() {
		final Date born = this.getBirth_date();
		if (born == null) {
			return "";
		}

		final DateTime dt_from = new DateTime(born);
		final DateTime dt_to = new DateTime(Calendar.getInstance().getTime());

		final Years y = Years.yearsBetween(dt_from, dt_to);
		final Months m = Months.monthsBetween(dt_from, dt_to);

		return "" + y.getYears() + " " + m.getMonths();
	}

	public String getFamily_charge() {
		return StringUtils.defaultString(this.family_charge, "");

	}

	public String getFirstname() {
		return StringUtils.defaultString(this.firstname, "");

	}

	public String getFiscal_code() {
		return StringUtils.defaultString(this.fiscal_code, "");

	}

	public Double getJob_basicsalary() {
		return this.job_basicsalary;
	}

	public String getJob_basicsalaryString() {
		if (this.job_basicsalary == null) {
			return "";
		} else {
			return "" + this.job_basicsalary;
		}

	}

	public Double getJob_contingency() {
		return this.job_contingency;
	}

	public String getJob_contingencyString() {
		if (this.job_contingency == null) {
			return "";
		} else {
			return "" + this.job_contingency;
		}

	}

	public Date getJob_date_from() {
		return this.job_date_from;
	}

	public Date getJob_date_to() {
		return this.job_date_to;
	}

	public Double getJob_edr() {
		return this.job_edr;
	}

	public Double getJob_shots() {
		return this.job_shots;
	}

	public String getJobcost() {
		return StringUtils.defaultString(this.jobcost, "");

	}

	public String getLastname() {
		return StringUtils.defaultString(this.lastname, "");

	}

	public String getMarital_status() {
		return StringUtils.defaultString(this.marital_status, "");

	}

	public String getNbudge() {
		return StringUtils.defaultString(this.nbudge, "");

	}

	public String getNcfl() {
		return StringUtils.defaultString(this.ncfl, "");

	}

	public String getNpass() {
		return StringUtils.defaultString(this.npass, "");

	}

	public String getPersonal_code() {
		return StringUtils.defaultString(this.personal_code, "");

	}

	public String getPhone() {
		return StringUtils.defaultString(this.phone, "");

	}

	public String getProvincia() {
		return StringUtils.defaultString(this.provincia, "");

	}

	public String getStatus() {
		return StringUtils.defaultString(this.status, "");
	}

	public String getTask_default() {
		return StringUtils.defaultString(this.task_default, "");
	}

	public String getTfr_destination() {
		return StringUtils.defaultString(this.tfr_destination, "");
	}

	public Date getTfr_selection_date() {
		return this.tfr_selection_date;
	}

	public String getZip() {
		return StringUtils.defaultString(this.zip, "");
	}

	private String parseDate(final Date target) {
		if (target == null) {
			return "";
		} else {
			return UserReport.date_format.format(target);
		}
	}

	public void setAddress(final String address) {
		this.address = address;
	}

	public void setBirth_date(final Date birth_date) {
		this.birth_date = birth_date;
	}

	public void setBirth_place(final String birth_place) {
		this.birth_place = birth_place;
	}

	public void setCity(final String city) {
		this.city = city;
	}

	public void setContractual_level(final String contractual_level) {
		this.contractual_level = contractual_level;
	}

	public void setCurrent_position(final String current_position) {
		this.current_position = current_position;
	}

	public void setDepartment(final String department) {
		this.department = department;
	}

	public void setDriving_license(final String driving_license) {
		this.driving_license = driving_license;
	}

	public void setDriving_license_emission(final Date driving_license_emission) {
		this.driving_license_emission = driving_license_emission;
	}

	public void setEducation(final String education) {
		this.education = education;
	}

	public void setEmployee_identification(final String employee_identification) {
		this.employee_identification = employee_identification;
	}

	public void setFamily_charge(final String family_charge) {
		this.family_charge = family_charge;
	}

	public void setFirstname(final String firstname) {
		this.firstname = firstname;
	}

	public void setFiscal_code(final String fiscal_code) {
		this.fiscal_code = fiscal_code;
	}

	public void setJob_basicsalary(final Double job_basicsalary) {
		this.job_basicsalary = job_basicsalary;
	}

	public void setJob_contingency(final Double job_contingency) {
		this.job_contingency = job_contingency;
	}

	public void setJob_date_from(final Date job_date_from) {
		this.job_date_from = job_date_from;
	}

	public void setJob_date_to(final Date job_date_to) {
		this.job_date_to = job_date_to;
	}

	public void setJob_edr(final Double job_edr) {
		this.job_edr = job_edr;
	}

	public void setJob_shots(final Double job_shots) {
		this.job_shots = job_shots;
	}

	public void setJobcost(final String jobcost) {
		this.jobcost = jobcost;
	}

	public void setLastname(final String lastname) {
		this.lastname = lastname;
	}

	public void setMarital_status(final String marital_status) {
		this.marital_status = marital_status;
	}

	public void setNbudge(final String nbudge) {
		this.nbudge = nbudge;
	}

	public void setNcfl(final String ncfl) {
		this.ncfl = ncfl;
	}

	public void setNpass(final String npass) {
		this.npass = npass;
	}

	public void setPersonal_code(final String personal_code) {
		this.personal_code = personal_code;
	}

	public void setPhone(final String phone) {
		this.phone = phone;
	}

	public void setProvincia(final String provincia) {
		this.provincia = provincia;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public void setTask_default(final String task_default) {
		this.task_default = task_default;
	}

	public void setTfr_destination(final String tfr_destination) {
		this.tfr_destination = tfr_destination;
	}

	public void setTfr_selection_date(final Date tfr_selection_date) {
		this.tfr_selection_date = tfr_selection_date;
	}

	public void setZip(final String zip) {
		this.zip = zip;
	}

	public String toCSVLine() {

		final String msg = this.getLastname() + ";" + this.getFirstname() + ";" + this.getStatus() + ";" + this.getDepartment() + ";" + this.getNcfl()
				+ ";" + this.getBirth_place() + ";" + this.parseDate(this.getBirth_date()) + ";" + this.getEta() + ";" + this.getAddress() + ";"
				+ this.getCity() + ";" + this.getZip() + ";" + this.getProvincia() + ";" + this.getPhone() + ";" + this.getFiscal_code() + ";"
				+ this.getMarital_status() + ";" + this.getFamily_charge() + ";" + this.getEmployee_identification() + ";" + this.getNpass() + ";"
				+ this.getNbudge() + ";" + this.getPersonal_code() + ";" + this.getEducation() + ";" + this.getDriving_license() + ";"
				+ this.parseDate(this.getDriving_license_emission()) + ";" + this.getDrivingTimeEmission() + ";" + this.getCurrent_position() + ";"
				+ this.getJobcost() + ";" + this.parseDate(this.getJob_date_from()) + ";" + this.parseDate(this.getJob_date_to()) + ";"
				+ this.getJob_basicsalaryString() + ";" + this.getJob_contingencyString() + ";" + this.getTask_default() + ";"
				+ this.getTfr_destination() + ";" + this.parseDate(this.getTfr_selection_date());

		return msg;
	}

}
