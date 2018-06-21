package org.uario.seaworkengine.utility;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Months;
import org.joda.time.Years;

public class UserReport implements Serializable {

	private static final SimpleDateFormat	date_format			= new SimpleDateFormat("dd-MM-yyyy");

	private static final SimpleDateFormat	date_format_month	= new SimpleDateFormat("MM/yyyy");

	public static final String				HEADER				= "COGNOME;NOME;STATUS;DA;A;REPARTO;NCFL;LUOGO DI NASCITA;DATA NASCITA;ETA;INDIRIZZO;"
			+ "CITTA;CAP;PROV.;TELEFONO;CODICE FISCALE;STATO CIVILE;CARICHI FAMIGLIARI;MATR.;PASS DI ACCESSO;BADGE;ID PERSONALE;"
			+ "ISTRUZIONE;TIPO PATENTE;RILASCIA IL;TEMPO RILASCIO;QUALIFICA CORRENTE;LIVELLO CORRENTE;CENTRO DI COSTO;DA;A;TOT. RETRIB. LORDA;"
			+ "PREMI/ALTRO;MANSIONE PRINCIPALE;SCELTA TFR;DATA SCELTA TFR;NOME SIDACATO;REGISTRAZIONE SINDACATO;CANCELLAZIONE SINDACATO;"
			+ "DATA ULTIMA CONTESTAZIONE DISCIPLINARE;PROT. CONT.NE DISCIPLINARE;DATA SANZIONE;PROTOCOLLO SANZIONE;"
			+ "RICORSO;TIPO SANZIONE;DURATA SANSIONE;DA;A;BP DI RIF.TO;DATA VISITA MEDICA;RISULTATI;PRESCRIZIONE;PRISSIMA VISITA";

	private static final long				serialVersionUID	= 1L;

	private String							address;

	private Date							birth_date;

	private String							birth_place;

	private String							city;

	private Date							con_date_contestation;

	private Date							con_date_penality;

	private Date							con_datebp;

	private String							con_prot;

	private String							con_prot_penalty;

	private Boolean							con_recall;

	private Date							con_stop_from;

	private Date							con_stop_to;

	private String							con_typ;

	private String							contractual_level;

	private String							current_position;

	private String							department;

	private String							driving_license;

	private Date							driving_license_emission;

	private String							education;

	private String							employee_identification;

	private String							family_charge;

	private Date							fc_control_date;

	private Date							fc_request_date;

	private String							fc_result;

	private String							fc_result_type;

	private String							fc_sede_inps;

	private Date							fc_sikness_from;

	private Date							fc_sikness_to;

	private String							firstname;

	private String							fiscal_code;

	private Double							job_awards;

	private Double							job_basicsalary;
	private Double							job_contingency;
	private Date							job_date_from;
	private Date							job_date_to;
	private Double							job_edr;
	private Double							job_shots;
	private String							jobcost;

	private String							lastname;

	private String							marital_status;
	private Date							me_date_examination;
	private Date							me_next_date_examination;
	private String							me_prescriptions;
	private String							me_result_examination;
	private String							nbudge;

	private String							ncfl;

	private String							npass;
	private String							personal_code;

	private String							phone;
	private String							provincia;
	private String							status;
	private String							status_from;
	private String							status_to;
	private String							task_default;
	private Date							tc_certificate_date;
	private Double							tc_duration;
	private Date							tc_end_class;

	private Date							tc_expiration_date;

	private Date							tc_start_class;

	private String							tc_title;

	private String							tc_trainer;

	private String							tc_trainer_type;

	private String							tc_training_level;

	private String							tc_training_task;

	private String							tc_tutor;

	private String							tc_typ;

	private String							tfr_destination;

	private Date							tfr_selection_date;

	private Date							tu_cancellation;

	private String							tu_name;

	private Date							tu_registration;

	private Date							uc_date_submit;

	private Double							uc_time_comp;

	private String							zip;

	public String getAddress() {
		return "\"" + StringUtils.defaultString(this.address, "") + "\"";

	}

	public Date getBirth_date() {
		return this.birth_date;
	}

	public String getBirth_place() {
		return StringUtils.defaultString(this.birth_place, "");

	}

	public String getCity() {
		return "\"" + StringUtils.defaultString(this.city, "") + "\"";
	}

	public Date getCon_date_contestation() {
		return this.con_date_contestation;
	}

	public Date getCon_date_penality() {
		return this.con_date_penality;
	}

	public Date getCon_datebp() {
		return this.con_datebp;
	}

	public String getCon_prot() {
		return StringUtils.defaultString(this.con_prot, "");
	}

	public String getCon_prot_penalty() {
		return StringUtils.defaultString(this.con_prot_penalty, "");

	}

	public Boolean getCon_recall() {
		return BooleanUtils.toBooleanDefaultIfNull(this.con_recall, false);

	}

	public Date getCon_stop_from() {
		return this.con_stop_from;
	}

	public Date getCon_stop_to() {
		return this.con_stop_to;
	}

	public String getCon_typ() {
		return StringUtils.defaultString(this.con_typ, "");
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

		final int m_tot = m.getMonths();
		final int year = m_tot / 12;
		final int month = m_tot % 12;

		return "" + year + " " + month;

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

		final Months m = Months.monthsBetween(dt_from, dt_to);

		final int m_tot = m.getMonths();
		final int year = m_tot / 12;
		final int month = m_tot % 12;

		return "" + year + " " + month;
	}

	public String getFamily_charge() {
		return StringUtils.defaultString(this.family_charge, "");

	}

	public Date getFc_control_date() {
		return this.fc_control_date;
	}

	public Date getFc_request_date() {
		return this.fc_request_date;
	}

	public String getFc_result() {
		return StringUtils.defaultString(this.fc_result, "");
	}

	public String getFc_result_type() {
		return StringUtils.defaultString(this.fc_result_type, "");
	}

	public String getFc_sede_inps() {
		return StringUtils.defaultString(this.fc_sede_inps, "");
	}

	public Date getFc_sikness_from() {
		return this.fc_sikness_from;
	}

	public Date getFc_sikness_to() {
		return this.fc_sikness_to;
	}

	public String getFirstname() {
		return StringUtils.defaultString(this.firstname, "");

	}

	public String getFiscal_code() {
		return StringUtils.defaultString(this.fiscal_code, "");

	}

	public Double getJob_awards() {
		return this.job_awards;
	}

	public String getJob_awardsString() {
		if (this.job_awards == null) {
			return "";
		} else {

			return new Double(Utility.roundTwo(this.job_awards)).toString();
		}

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

	public String getJobTotal() {
		final Double jb = ObjectUtils.defaultIfNull(this.job_basicsalary, 0.0);
		final Double jb_con = ObjectUtils.defaultIfNull(this.job_contingency, 0.0);
		final Double jb_shots = ObjectUtils.defaultIfNull(this.job_shots, 0.0);
		final Double jb_edr = ObjectUtils.defaultIfNull(this.job_edr, 0.0);

		final Double sum = jb + jb_con + jb_shots + jb_edr;

		final Double info = Utility.roundTwo(sum);
		return info.toString();
	}

	public String getLastname() {
		return StringUtils.defaultString(this.lastname, "");

	}

	public String getMarital_status() {
		return StringUtils.defaultString(this.marital_status, "");

	}

	public Date getMe_date_examination() {
		return this.me_date_examination;
	}

	public Date getMe_next_date_examination() {
		return this.me_next_date_examination;
	}

	public String getMe_prescriptions() {
		return StringUtils.defaultString(this.me_prescriptions, "");
	}

	public String getMe_result_examination() {
		final String ret = StringUtils.defaultString(this.me_result_examination, "");
		return ret;
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

	public String getStatus_from() {
		return StringUtils.defaultString(this.status_from, "");
	}

	public String getStatus_to() {
		return StringUtils.defaultString(this.status_to, "");
	}

	public String getStopPeriod() {

		final Date from = this.getCon_stop_from();

		final Date to = this.getCon_stop_to();

		if ((from == null) || (to == null)) {
			return "";
		}

		final int day = Utility.getDayBetweenDate(from, to);

		return "" + day;

	}

	public String getTask_default() {
		return StringUtils.defaultString(this.task_default, "");
	}

	public Date getTc_certificate_date() {
		return this.tc_certificate_date;
	}

	public Double getTc_duration() {

		return this.tc_duration;
	}

	public String getTc_duration_string() {

		if (this.tc_duration == null) {
			return "";
		} else {
			return "" + this.tc_duration;
		}

	}

	public Date getTc_end_class() {
		return this.tc_end_class;
	}

	public Date getTc_expiration_date() {
		return this.tc_expiration_date;
	}

	public Date getTc_start_class() {
		return this.tc_start_class;
	}

	public String getTc_title() {
		return StringUtils.defaultString(this.tc_title, "");
	}

	public String getTc_trainer() {
		return StringUtils.defaultString(this.tc_trainer, "");
	}

	public String getTc_trainer_type() {
		return StringUtils.defaultString(this.tc_trainer_type, "");
	}

	public String getTc_training_level() {
		return StringUtils.defaultString(this.tc_training_level, "");
	}

	public String getTc_training_task() {
		return StringUtils.defaultString(this.tc_training_task, "");
	}

	public String getTc_tutor() {
		return StringUtils.defaultString(this.tc_tutor, "");
	}

	public String getTc_typ() {
		return StringUtils.defaultString(this.tc_typ, "");
	}

	public String getTfr_destination() {
		return StringUtils.defaultString(this.tfr_destination, "");
	}

	public Date getTfr_selection_date() {
		return this.tfr_selection_date;
	}

	public Date getTu_cancellation() {
		return this.tu_cancellation;
	}

	public String getTu_name() {
		return StringUtils.defaultString(this.tu_name, "");
	}

	public Date getTu_registration() {
		return this.tu_registration;
	}

	public Date getUc_date_submit() {
		return this.uc_date_submit;
	}

	public Double getUc_time_comp() {

		return this.uc_time_comp;
	}

	public String getUc_time_comp_string() {

		if (this.uc_time_comp == null) {
			return "";
		} else {
			return "" + this.uc_time_comp;
		}
	}

	public String getZip() {
		return "\"" + StringUtils.defaultString(this.zip, "") + "\"";
	}

	public String infoRecall() {
		final boolean rec = this.getCon_recall();
		if (rec) {
			return "SI";
		} else {
			return "NO";
		}
	}

	private String parseDate(final Date target) {
		if (target == null) {
			return "";
		} else {
			return UserReport.date_format.format(target);
		}
	}

	private String parseDateMonth(final Date target) {
		if (target == null) {
			return "";
		} else {
			return UserReport.date_format_month.format(target);
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

	public void setCon_date_contestation(final Date con_date_contestation) {
		this.con_date_contestation = con_date_contestation;
	}

	public void setCon_date_penality(final Date con_date_penality) {
		this.con_date_penality = con_date_penality;
	}

	public void setCon_datebp(final Date con_datebp) {
		this.con_datebp = con_datebp;
	}

	public void setCon_prot(final String con_prot) {
		this.con_prot = con_prot;
	}

	public void setCon_prot_penalty(final String con_prot_penalty) {
		this.con_prot_penalty = con_prot_penalty;
	}

	public void setCon_recall(final Boolean con_recall) {
		this.con_recall = con_recall;
	}

	public void setCon_stop_from(final Date con_stop_from) {
		this.con_stop_from = con_stop_from;
	}

	public void setCon_stop_to(final Date con_stop_to) {
		this.con_stop_to = con_stop_to;
	}

	public void setCon_typ(final String con_typ) {
		this.con_typ = con_typ;
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

	public void setFc_control_date(final Date fc_control_date) {
		this.fc_control_date = fc_control_date;
	}

	public void setFc_request_date(final Date fc_request_date) {
		this.fc_request_date = fc_request_date;
	}

	public void setFc_result(final String fc_result) {
		this.fc_result = fc_result;
	}

	public void setFc_result_type(final String fc_result_type) {
		this.fc_result_type = fc_result_type;
	}

	public void setFc_sede_inps(final String fc_sede_inps) {
		this.fc_sede_inps = fc_sede_inps;
	}

	public void setFc_sikness_from(final Date fc_sikness_from) {
		this.fc_sikness_from = fc_sikness_from;
	}

	public void setFc_sikness_to(final Date fc_sikness_to) {
		this.fc_sikness_to = fc_sikness_to;
	}

	public void setFirstname(final String firstname) {
		this.firstname = firstname;
	}

	public void setFiscal_code(final String fiscal_code) {
		this.fiscal_code = fiscal_code;
	}

	public void setJob_awards(final Double job_awards) {
		this.job_awards = job_awards;
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

	public void setMe_date_examination(final Date me_date_examination) {
		this.me_date_examination = me_date_examination;
	}

	public void setMe_next_date_examination(final Date me_next_date_examination) {
		this.me_next_date_examination = me_next_date_examination;
	}

	public void setMe_prescriptions(final String me_prescriptions) {
		this.me_prescriptions = me_prescriptions;
	}

	public void setMe_result_examination(final String me_result_examination) {
		this.me_result_examination = me_result_examination;
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

	public void setStatus_from(final String status_from) {
		this.status_from = status_from;
	}

	public void setStatus_to(final String status_to) {
		this.status_to = status_to;
	}

	public void setTask_default(final String task_default) {
		this.task_default = task_default;
	}

	public void setTc_certificate_date(final Date tc_certificate_date) {
		this.tc_certificate_date = tc_certificate_date;
	}

	public void setTc_duration(final Double tc_duration) {
		this.tc_duration = tc_duration;
	}

	public void setTc_end_class(final Date tc_end_class) {
		this.tc_end_class = tc_end_class;
	}

	public void setTc_expiration_date(final Date tc_expiration_date) {
		this.tc_expiration_date = tc_expiration_date;
	}

	public void setTc_start_class(final Date tc_start_class) {
		this.tc_start_class = tc_start_class;
	}

	public void setTc_title(final String tc_title) {
		this.tc_title = tc_title;
	}

	public void setTc_trainer(final String tc_trainer) {
		this.tc_trainer = tc_trainer;
	}

	public void setTc_trainer_type(final String tc_trainer_type) {
		this.tc_trainer_type = tc_trainer_type;
	}

	public void setTc_training_level(final String tc_training_level) {
		this.tc_training_level = tc_training_level;
	}

	public void setTc_training_task(final String tc_training_task) {
		this.tc_training_task = tc_training_task;
	}

	public void setTc_tutor(final String tc_tutor) {
		this.tc_tutor = tc_tutor;
	}

	public void setTc_typ(final String tc_typ) {
		this.tc_typ = tc_typ;
	}

	public void setTfr_destination(final String tfr_destination) {
		this.tfr_destination = tfr_destination;
	}

	public void setTfr_selection_date(final Date tfr_selection_date) {
		this.tfr_selection_date = tfr_selection_date;
	}

	public void setTu_cancellation(final Date tu_cancellation) {
		this.tu_cancellation = tu_cancellation;
	}

	public void setTu_name(final String tu_name) {
		this.tu_name = tu_name;
	}

	public void setTu_registration(final Date tu_registration) {
		this.tu_registration = tu_registration;
	}

	public void setUc_date_submit(final Date uc_date_submit) {
		this.uc_date_submit = uc_date_submit;
	}

	public void setUc_time_comp(final Double uc_time_comp) {
		this.uc_time_comp = uc_time_comp;
	}

	public void setZip(final String zip) {
		this.zip = zip;
	}

	public String toCSVLine() {

		String msg = this.getLastname() + ";" + this.getFirstname() + ";" + this.getStatus() + ";" + this.getStatus_from() + ";" + this.getStatus_to()
				+ ";" + this.getDepartment() + ";" + this.getNcfl() + ";" + this.getBirth_place() + ";" + this.parseDate(this.getBirth_date()) + ";"
				+ this.getEta() + ";" + this.getAddress() + ";" + this.getCity() + ";" + this.getZip() + ";" + this.getProvincia() + ";"
				+ this.getPhone() + ";" + this.getFiscal_code() + ";" + this.getMarital_status() + ";" + this.getFamily_charge() + ";"
				+ this.getEmployee_identification() + ";" + this.getNpass() + ";" + this.getNbudge() + ";" + this.getPersonal_code() + ";"
				+ this.getEducation() + ";" + this.getDriving_license() + ";" + this.parseDate(this.getDriving_license_emission()) + ";"
				+ this.getDrivingTimeEmission() + ";" + this.getCurrent_position() + ";" + this.getContractual_level() + ";" + this.getJobcost() + ";"
				+ this.parseDate(this.getJob_date_from()) + ";" + this.parseDate(this.getJob_date_to()) + ";" + this.getJobTotal() + ";"
				+ this.getJob_awardsString() + ";" + this.getTask_default() + ";" + this.getTfr_destination() + ";"
				+ this.parseDate(this.getTfr_selection_date()) + ";" + this.getTu_name() + ";" + this.parseDate(this.getTu_registration()) + ";"
				+ this.parseDate(this.getTu_cancellation()) + ";" + this.parseDate(this.getCon_date_contestation()) + ";" + this.getCon_prot() + ";"
				+ this.parseDate(this.getCon_date_penality()) + ";" + this.getCon_prot_penalty() + ";" + this.infoRecall() + ";" + this.getCon_typ()
				+ ";" + this.getStopPeriod() + ";" + this.parseDate(this.getCon_stop_from()) + ";" + this.parseDate(this.getCon_stop_to()) + ";"
				+ this.parseDateMonth(this.getCon_datebp()) + ";" + this.parseDate(this.getMe_date_examination()) + ";"
				+ this.getMe_result_examination() + ";" + this.getMe_prescriptions() + ";" + this.parseDate(this.me_next_date_examination);

		msg = msg.replace("\n", " ");

		return msg;
	}

}
