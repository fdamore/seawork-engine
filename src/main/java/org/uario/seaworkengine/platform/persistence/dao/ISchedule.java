package org.uario.seaworkengine.platform.persistence.dao;

import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.Schedule;

public interface ISchedule {

	public void createSchedule(Schedule schedule);

	public Schedule loadSchedule(Integer id_schedule);

	public List<Schedule> loadScheduleByDate(Date date_schedule);

	public void removeSchedule(Integer id);

	public void updateSchedule(Schedule schedule);

}
