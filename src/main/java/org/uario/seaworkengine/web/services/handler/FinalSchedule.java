package org.uario.seaworkengine.web.services.handler;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Schedule;

@XmlRootElement(namespace = "org.uario.seaworkengine.model")
@XmlAccessorType(XmlAccessType.FIELD)
public class FinalSchedule {

	private DetailFinalSchedule	detail_schedule;

	private Person				person;

	private Schedule			schedule;

	public DetailFinalSchedule getDetail_schedule() {
		return this.detail_schedule;
	}

	public Person getPerson() {
		return this.person;
	}

	public Schedule getSchedule() {
		return this.schedule;
	}

	public void setDetail_schedule(final DetailFinalSchedule detail_schedule) {
		this.detail_schedule = detail_schedule;
	}

	public void setPerson(final Person person) {
		this.person = person;
	}

	public void setSchedule(final Schedule schedule) {
		this.schedule = schedule;
	}

}