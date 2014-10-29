package org.uario.seaworkengine.platform.persistence.dao;

import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.DaySchedule;
import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.Schedule;

public interface ISchedule {

	public void createDetailFinalSchedule(DetailFinalSchedule detail_schedule);

	public void createDetailInitialSchedule(DetailInitialSchedule detail_schedule);

	public List<DaySchedule> loadDaySchedule(Date date_scheduled);

	public List<DetailFinalSchedule> loadDetailFinalScheduleByIdScheduleAndShift(Integer id_schedule, Integer shift);

	public List<DetailInitialSchedule> loadDetailInitialScheduleByIdScheduleAndShift(Integer id_schedule, Integer shift);

	public Schedule loadSchedule(Date date_scheduler, Integer id_user);

	public void removeAllDetailFinalScheduleByScheduleAndShift(Integer id_schedule, Integer shift);

	public void removeAllDetailInitialScheduleByScheduleAndShift(Integer id_schedule, Integer shift);

	public void saveListDetailFinalScheduler(Integer id_schedule, Integer shift, List<DetailFinalSchedule> details);

	public void saveListDetailInitialScheduler(Integer id_schedule, Integer shift, List<DetailInitialSchedule> details);

	public void saveOrUpdateDaySchedule(DaySchedule day_schedule);

	public void saveOrUpdateSchedule(Schedule currentSchedule);

	public List<DaySchedule> selectDaySchedulers(Date initial_date, Date final_date);

	public List<Schedule> selectSchedulers(Date initial_date, Date final_date);

}
