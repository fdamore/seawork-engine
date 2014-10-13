package org.uario.seaworkengine.platform.persistence.dao;

import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.Detail_Schedule;
import org.uario.seaworkengine.model.Schedule;

public interface ISchedule {

	public void createDetail_Schedule(Detail_Schedule detail_schedule);

	public void createSchedule(Schedule schedule);

	public Detail_Schedule loadDetail_Schedule(Integer id);

	public List<Detail_Schedule> loadDetail_ScheduleByIdSchedule(Integer id_schedule);

	public Schedule loadSchedule(Integer id_schedule);

	public List<Schedule> loadScheduleByDate(Date date_schedule);

	public void removeDetail_Schedule(Integer id_detail_schedule);

	public void removeSchedule(Integer id);

	public void saveOrUpdate(Schedule currentSchedule);

	public List<Schedule> selectSchedulers(Date initial_date, Date final_date);

	public void updateDetailSchedule(Detail_Schedule detail_schedule);

	public void updateSchedule(Schedule schedule);

}
