package org.uario.seaworkengine.platform.persistence.dao;

import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.DaySchedule;
import org.uario.seaworkengine.model.DetailSchedule;
import org.uario.seaworkengine.model.Schedule;

public interface ISchedule {

	public void createDaySchedule(DaySchedule daySchedule);

	public void createDetailSchedule(DetailSchedule detail_schedule);

	public void createSchedule(Schedule schedule);

	public DaySchedule loadDaySchedule(Integer id);

	public DaySchedule loadDaySchedule(Integer id_user, Date date_scheduled);

	public DetailSchedule loadDetailSchedule(Integer id);

	public List<DetailSchedule> loadDetailScheduleByIdScheduleAndShift(Integer id_schedule, Integer shift);

	public Schedule loadSchedule(Date date_scheduler, Integer id_user);

	public Schedule loadSchedule(Integer id_schedule);

	public void removeDaySchedule(Integer id);

	public void removeDetailSchedule(Integer id_detail_schedule);

	public void removeSchedule(Integer id);

	public void saveListDetailScheduler(Integer id_schedule, Integer shift, List<DetailSchedule> details);

	public void saveOrUpdate(Schedule currentSchedule);

	public List<DaySchedule> selectDaySchedulers(Date initial_date, Date final_date);

	public List<Schedule> selectSchedulers(Date initial_date, Date final_date);

	public void updateDaySchedule(DaySchedule daySchedule);

	public void updateDetailSchedule(DetailSchedule detail_schedule);

	public void updateSchedule(Schedule schedule);

}
