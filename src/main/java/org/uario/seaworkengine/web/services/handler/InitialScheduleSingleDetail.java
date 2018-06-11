package org.uario.seaworkengine.web.services.handler;

import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Schedule;

public class InitialScheduleSingleDetail {

	private DetailInitialSchedule	detail_schedule;

	private Person					person;

	private Schedule				schedule;

	public DetailInitialSchedule getDetail_schedule() {
		return this.detail_schedule;
	}

	public Person getPerson() {
		return this.person;
	}

	public Schedule getSchedule() {
		return this.schedule;
	}

	public void setDetail_schedule(final DetailInitialSchedule detail_schedule) {
		this.detail_schedule = detail_schedule;
	}

	public void setPerson(final Person person) {
		this.person = person;
	}

	public void setSchedule(final Schedule schedule) {
		this.schedule = schedule;
	}

}
