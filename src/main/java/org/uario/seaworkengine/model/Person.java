package org.uario.seaworkengine.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.uario.seaworkengine.utility.UserTag;

/*
┌────────────┬─────────────────────────────────────────────────────┐
│ USER ROLES │ Meaning                                             │
├────────────┼─────────────────────────────────────────────────────┤
│ UserTag.ROLE_BACKOFFICE, "UFFICIO");
  UserTag.ROLE_OPERATIVE, "PREPOSTO");
  UserTag.ROLE_USER, "UTENTE");
  UserTag.ROLE_VIEWER, "VISUALIZZATORE");
  UserTag.ROLE_SUPERVISOR, "AMMINISTRATORE");
└────────────┴─────────────────────────────────────────────────────┘
*/

public class Person implements Comparable<Person>, UserDetails, Serializable {

	public static final Person	NULL				= new Person();

	private static final long	serialVersionUID	= 1L;

	private String				address;

	private String				asd					= "asd";

	private String				authority;

	private java.util.Date		birth_date;

	private String				birth_place;

	private String				birth_province;

	private String				city;

	private Integer				contractual_level;

	private String				country;

	private String				current_position;

	private Boolean				dailyemployee;

	private Integer				daywork_w;

	private String				department;

	private String				driving_license;

	private java.util.Date		driving_license_emission;

	private String				education;

	private String				email;

	/**
	 * MATRICOLA
	 */
	private String				employee_identification;

	private Boolean				enabled;

	private String				family_charge;

	private String				firstname;

	private String				fiscal_code;

	private Integer				hourswork_w;

	private Integer				id;

	private String				lastname;

	private String				marital_status;

	private String				nbudge;

	private String				ncfl;

	private String				npass;

	private Boolean				out_schedule;

	private Boolean				part_time;

	private String				password;

	private String				personal_code;

	private String				phone;

	private String				provincia;

	private Boolean				sex;

	private String				status;

	private String				tasksPerson;

	/**
	 * this list of user task is used only bu mobile app
	 */
	private List<UserTask>		userTaskForMobile;

	private String				zip;

	public Person() {

	}

	public Person(final String name) {
		this.setFirstname(name);
	}

	@Override
	public int compareTo(final Person o) {
		if (o == null) {
			return -1;
		}
		if (!(o instanceof Person)) {
			return -1;
		}
		if (this.getIndividualName() == null) {
			return -1;
		}
		final Person item = o;
		if (item.getIndividualName() == null) {
			return 1;
		}
		return this.getIndividualName().compareTo(item.getIndividualName());

	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Person)) {
			return false;
		}
		final Person compare = (Person) obj;
		return this.getId().equals(compare.getId());

	}

	public String getAddress() {
		return this.address;
	}

	public String getAsd() {
		return this.asd;
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		final Set<GrantedAuthority> auth = new HashSet<>();
		final String auth_string = this.getAuthority();
		final String[] items = auth_string.split(",");
		for (final String item : items) {
			final String myitem = item.trim();
			final GrantedAuthorityImpl authority_impl = new GrantedAuthorityImpl(myitem);
			auth.add(authority_impl);
		}

		return auth;

	}

	public String getAuthority() {
		return this.authority;
	}

	public java.util.Date getBirth_date() {
		return this.birth_date;
	}

	public String getBirth_place() {
		return this.birth_place;
	}

	public String getBirth_province() {
		return this.birth_province;
	}

	public String getCity() {
		return this.city;
	}

	public Integer getContractual_level() {
		return this.contractual_level;
	}

	public String getCountry() {
		return this.country;
	}

	public String getCurrent_position() {
		return this.current_position;
	}

	public Boolean getDailyemployee() {
		return this.dailyemployee;
	}

	public Integer getDaywork_w() {
		return this.daywork_w;
	}

	public String getDepartment() {
		return this.department;
	}

	public String getDriving_license() {
		return this.driving_license;
	}

	public java.util.Date getDriving_license_emission() {
		return this.driving_license_emission;
	}

	public String getEducation() {
		return this.education;
	}

	public String getEmail() {
		return this.email;
	}

	public String getEmployee_identification() {
		return this.employee_identification;
	}

	public String getFamily_charge() {
		return this.family_charge;
	}

	public String getFirstname() {
		if ((this.firstname == null) || this.firstname.equals("")) {

			return UserTag.USER_NONAME;

		} else {
			return this.firstname;
		}

	}

	public String getFiscal_code() {
		return this.fiscal_code;
	}

	public Integer getHourswork_w() {
		return this.hourswork_w;
	}

	public Integer getId() {
		return this.id;
	}

	public String getIndividualName() {
		final String ret = this.getLastname() + " " + this.getFirstname();
		if (ret.trim().equals("")) {
			return this.getEmail();
		} else {
			return ret;
		}
	}

	public String getLastname() {

		if ((this.lastname == null) || this.lastname.equals("")) {

			return UserTag.USER_NOSURNAME;

		} else {
			return this.lastname;
		}

	}

	public String getMarital_status() {
		return this.marital_status;
	}

	public String getNbudge() {
		return this.nbudge;
	}

	public String getNcfl() {
		return this.ncfl;
	}

	public String getNpass() {
		return this.npass;
	}

	public Boolean getOut_schedule() {
		return this.out_schedule;
	}

	public Boolean getPart_time() {
		return this.part_time;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	public String getPersonal_code() {
		return this.personal_code;
	}

	public String getPhone() {
		return this.phone;
	}

	public String getProvincia() {
		return this.provincia;
	}

	// return a description of user roles
	public String getRolesDescription() {
		String roles = "";
		if (this.isAdministrator()) {
			roles = "Amministratore";
		}
		if (this.isOperative()) {
			if (roles != "") {
				roles += ", Preposto";
			} else {
				roles += "Preposto";
			}

		}
		if (this.isBackoffice()) {
			if (roles != "") {
				roles += ", Programmatore";
			} else {
				roles += "Programmatore";
			}

		}
		if (this.isViewer()) {
			if (roles != "") {
				roles += ", Visore";
			} else {
				roles += "Visore";
			}

		}
		if (this.getDailyemployee()) {
			if (roles != "") {
				roles += ", Giornaliero";
			} else {
				roles += "Giornaliero";
			}

		}
		if (this.getPart_time()) {
			if (roles != "") {
				roles += ", Part Time";
			} else {
				roles += "Part Time";
			}

		}

		return roles;
	}

	public Boolean getSex() {
		return this.sex;
	}

	public String getStatus() {
		return this.status;
	}

	public String getTasksPerson() {
		return this.tasksPerson;
	}

	@Override
	public String getUsername() {
		return this.getEmail();

	}

	public List<UserTask> getUserTaskForMobile() {
		return this.userTaskForMobile;
	}

	public String getZip() {
		return this.zip;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;

	}

	@Override
	public boolean isAccountNonLocked() {
		return true;

	}

	/**
	 * return true if user is a supervisor..
	 *
	 * @return
	 */
	public boolean isAdministrator() {
		final Collection<GrantedAuthority> auts = this.getAuthorities();
		for (final Object element : auts) {
			final GrantedAuthority grantedAuthority = (GrantedAuthority) element;
			final String item = grantedAuthority.getAuthority();
			if (item.equals(UserTag.ROLE_SUPERVISOR)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * return true if user is a supervisor..
	 *
	 * @return
	 */
	public boolean isBackoffice() {
		final Collection<GrantedAuthority> auts = this.getAuthorities();
		for (final Object element : auts) {
			final GrantedAuthority grantedAuthority = (GrantedAuthority) element;
			final String item = grantedAuthority.getAuthority();
			if (item.equals(UserTag.ROLE_BACKOFFICE)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;

	}

	@Override
	public boolean isEnabled() {
		if ((this.password == null) || this.password.equals("")) {
			return false;
		}

		return this.enabled;
	}

	// Check if a user is a Daily Employee or a BackOffice or a Operative
	// Employee
	public boolean isInOffice() {
		if (this.dailyemployee) {
			return true;
		}
		final Collection<GrantedAuthority> auts = this.getAuthorities();
		for (final Object element : auts) {
			final GrantedAuthority grantedAuthority = (GrantedAuthority) element;
			final String item = grantedAuthority.getAuthority();
			if (item.equals(UserTag.ROLE_BACKOFFICE) || item.equals(UserTag.ROLE_OPERATIVE)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * return true if user is a supervisor.. (PREPOSTO)
	 *
	 * @return
	 */
	public boolean isOperative() {
		final Collection<GrantedAuthority> auts = this.getAuthorities();
		for (final Object element : auts) {
			final GrantedAuthority grantedAuthority = (GrantedAuthority) element;
			final String item = grantedAuthority.getAuthority();
			if (item.equals(UserTag.ROLE_OPERATIVE)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * return true if user is a supervisor..
	 *
	 * @return
	 */
	public boolean isViewer() {
		final Collection<GrantedAuthority> auts = this.getAuthorities();
		for (final GrantedAuthority grantedAuthority : auts) {
			final String item = grantedAuthority.getAuthority();
			if (item.equals(UserTag.ROLE_VIEWER)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Only viewer
	 *
	 * @return
	 */
	public boolean isViewerOnly() {
		if (!this.isViewer()) {
			return false;
		}

		if (this.isAdministrator() || this.isBackoffice()) {
			return false;
		}

		return true;
	}

	public void setAddress(final String address) {
		this.address = address;
	}

	public void setAsd(final String asd) {
		this.asd = asd;
	}

	public void setAuthority(final String authority) {
		this.authority = authority;
	}

	public void setBirth_date(final java.util.Date birth_date) {
		this.birth_date = birth_date;
	}

	public void setBirth_place(final String birth_place) {
		this.birth_place = birth_place;
	}

	public void setBirth_province(final String birth_province) {
		this.birth_province = birth_province;
	}

	public void setCity(final String city) {
		this.city = city;
	}

	public void setContractual_level(final Integer contractual_level) {
		this.contractual_level = contractual_level;
	}

	public void setCountry(final String country) {
		this.country = country;
	}

	public void setCurrent_position(final String current_position) {
		this.current_position = current_position;
	}

	public void setDailyemployee(final Boolean dailyemployee) {
		this.dailyemployee = dailyemployee;
	}

	public void setDaywork_w(final Integer daywork_w) {
		this.daywork_w = daywork_w;
	}

	public void setDepartment(final String department) {
		this.department = department;
	}

	public void setDriving_license(final String driving_license) {
		this.driving_license = driving_license;
	}

	public void setDriving_license_emission(final java.util.Date driving_license_emission) {
		this.driving_license_emission = driving_license_emission;
	}

	public void setEducation(final String education) {
		this.education = education;
	}

	public void setEmail(final String email) {
		this.email = email;
	}

	public void setEmployee_identification(final String employee_identification) {
		this.employee_identification = employee_identification;
	}

	public void setEnabled(final Boolean checked) {
		this.enabled = checked;

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

	public void setHourswork_w(final Integer hourswork_w) {
		this.hourswork_w = hourswork_w;
	}

	public void setId(final Integer id) {
		this.id = id;
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

	public void setOut_schedule(final Boolean out_schedule) {
		this.out_schedule = out_schedule;
	}

	public void setPart_time(final Boolean part_time) {
		this.part_time = part_time;
	}

	public void setPassword(final String password) {
		this.password = password;
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

	public void setSex(final Boolean sex) {
		this.sex = sex;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public void setTasksPerson(final String tasksPerson) {
		this.tasksPerson = tasksPerson;
	}

	public void setUserTaskForMobile(final List<UserTask> userTaskForMobile) {
		this.userTaskForMobile = userTaskForMobile;
	}

	public void setZip(final String zip) {
		this.zip = zip;
	}

	@Override
	public String toString() {
		if (this.getIndividualName() == null) {
			return "";
		} else {
			return this.getIndividualName();
		}
	}

}
