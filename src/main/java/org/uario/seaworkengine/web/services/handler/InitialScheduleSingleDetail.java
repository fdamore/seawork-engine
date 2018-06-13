package org.uario.seaworkengine.web.services.handler;

import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.Person;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.UserTask;

public class InitialScheduleSingleDetail {

	private DetailInitialSchedule detail_schedule;

	private Person person;

	private Schedule schedule;

	private UserTask user_task;

	public DetailInitialSchedule getDetail_schedule() {
		return this.detail_schedule;
	}

	public Person getPerson() {
		return this.person;
	}

	public Schedule getSchedule() {
		return this.schedule;
	}

	public UserTask getUser_task() {
		return this.user_task;
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

	public void setUser_task(final UserTask user_task) {
		this.user_task = user_task;
	}

}
