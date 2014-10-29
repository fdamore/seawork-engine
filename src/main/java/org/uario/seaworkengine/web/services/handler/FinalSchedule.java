package org.uario.seaworkengine.web.services.handler;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Schedule;

@XmlRootElement(namespace = "org.uario.seaworkengine.model")
@XmlAccessorType(XmlAccessType.FIELD)
public class FinalSchedule {

	private List<DetailFinalSchedule>	detail_chedule;

	private Person						person;

	private Schedule					schedule;

	public List<DetailFinalSchedule> getDetail_chedule() {
		return this.detail_chedule;
	}

	public Person getPerson() {
		return this.person;
	}

	public Schedule getSchedule() {
		return this.schedule;
	}

	public void setDetail_chedule(final List<DetailFinalSchedule> detail_chedule) {
		this.detail_chedule = detail_chedule;
	}

	public void setPerson(final Person person) {
		this.person = person;
	}

	public void setSchedule(final Schedule schedule) {
		this.schedule = schedule;
	}

}