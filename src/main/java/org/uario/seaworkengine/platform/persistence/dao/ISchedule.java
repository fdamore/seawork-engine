package org.uario.seaworkengine.platform.persistence.dao;

import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.DaySchedule;
import org.uario.seaworkengine.model.DetailSchedule;
import org.uario.seaworkengine.model.Schedule;

public interface ISchedule {

	public void createDetailSchedule(DetailSchedule detail_schedule);

	public List<DaySchedule> loadDaySchedule(Date date_scheduled);

	public List<DetailSchedule> loadDetailScheduleByIdScheduleAndShift(Integer id_schedule, Integer shift);

	public Schedule loadSchedule(Date date_scheduler, Integer id_user);

	public void saveListDetailScheduler(Integer id_schedule, Integer shift, List<DetailSchedule> details);

	public void saveOrUpdateDaySchedule(DaySchedule day_schedule);

	public void saveOrUpdateSchedule(Schedule currentSchedule);

	public List<DaySchedule> selectDaySchedulers(Date initial_date, Date final_date);

	public List<Schedule> selectSchedulers(Date initial_date, Date final_date);

}
