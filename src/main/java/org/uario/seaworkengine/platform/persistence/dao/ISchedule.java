package org.uario.seaworkengine.platform.persistence.dao;

import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.DetailSchedule;
import org.uario.seaworkengine.model.Schedule;

public interface ISchedule {

	public void createDetailSchedule(DetailSchedule detail_schedule);

	public void createSchedule(Schedule schedule);

	public DetailSchedule loadDetailSchedule(Integer id);

	public List<DetailSchedule> loadDetailScheduleByIdSchedule(Integer id_schedule);

	public Schedule loadSchedule(Date date_scheduler, Integer id_user);

	public Schedule loadSchedule(Integer id_schedule);

	public List<Schedule> loadScheduleByDate(Date date_schedule);

	public void removeDetailSchedule(Integer id_detail_schedule);

	public void removeSchedule(Integer id);

	public void saveListDetailScheduler(Integer id_schedule, List<DetailSchedule> details);

	public void saveOrUpdate(Schedule currentSchedule);

	public List<Schedule> selectSchedulers(Date initial_date, Date final_date);

	public void updateDetailSchedule(DetailSchedule detail_schedule);

	public void updateSchedule(Schedule schedule);

}
