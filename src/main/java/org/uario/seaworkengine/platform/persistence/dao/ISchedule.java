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

	public List<DetailFinalSchedule> loadDetailFinalScheduleByIdSchedule(Integer id_schedule);

	public List<DetailFinalSchedule> loadDetailFinalScheduleByIdScheduleAndShift(Integer id_schedule, Integer shift);

	public List<DetailInitialSchedule> loadDetailInitialScheduleByIdSchedule(Integer id_schedule);

	public List<DetailInitialSchedule> loadDetailInitialScheduleByIdScheduleAndShift(Integer id_schedule, Integer shift);

	public Schedule loadSchedule(Date date_scheduler, Integer id_user);

	public void removeAllDetailFinalScheduleByScheduleAndShift(Integer id_schedule, Integer shift);

	public void removeAllDetailInitialScheduleByScheduleAndShift(Integer id_schedule, Integer shift);

	public void removeDayScheduleUserFired(Integer idUser, Date firedDate);

	public void removeDayScheduleUserSuspended(Integer idUser, Date initialDate, Date finalDate);

	public void removeScheduleUserFired(Integer idUser, Date firedDate);

	public void removeScheduleUserSuspended(Integer idUser, Date initialDate, Date finalDate);

	public void saveListDetailFinalScheduler(Integer id_schedule, Integer shift, List<DetailFinalSchedule> details);

	public void saveListDetailInitialScheduler(Integer id_schedule, Integer shift, List<DetailInitialSchedule> details);

	public void saveOrUpdateDaySchedule(DaySchedule day_schedule);

	public void saveOrUpdateSchedule(Schedule currentSchedule);

	public List<Schedule> selectAggregateSchedulersProgram(Date firstDateInGrid, String full_text_search);

	public List<Schedule> selectAggregateSchedulersProgram(Date initial_date, Date final_date, String full_text_search);

	public List<Schedule> selectAggregateSchedulersRevision(Date firstDateInGrid, String full_text_search);

	List<Schedule> selectAggregateSchedulersRevision(Date initial_date, Date final_date, String full_text_search);

	public List<DaySchedule> selectDaySchedulers(Date initial_date, Date final_date, String my_full_text_search);

}
