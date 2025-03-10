package org.uario.seaworkengine.mobile.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Schedule;

@XmlRootElement(namespace = "org.uario.seaworkengine.model")
@XmlAccessorType(XmlAccessType.FIELD)
public class InitialSchedule {

	private List<MobileUserDetail>	detail_schedule;

	private Person					person;

	private Schedule				schedule;

	public List<MobileUserDetail> getDetail_schedule() {
		return this.detail_schedule;
	}

	public Person getPerson() {
		return this.person;
	}

	public Schedule getSchedule() {
		return this.schedule;
	}

	public void setDetail_schedule(final List<MobileUserDetail> detail_schedule) {
		this.detail_schedule = detail_schedule;
	}

	public void setPerson(final Person person) {
		this.person = person;
	}

	public void setSchedule(final Schedule schedule) {
		this.schedule = schedule;
	}

}
