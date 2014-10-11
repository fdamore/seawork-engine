package org.uario.seaworkengine.platform.persistence.dao;

import java.util.List;

import org.uario.seaworkengine.model.Detail_Schedule;

public interface IDetail_Schedule {

	public void createDetail_Schedule(Detail_Schedule detail_schedule);

	public Detail_Schedule loadDetail_Schedule(Integer id);

	public List<Detail_Schedule> loadDetail_ScheduleByIdSchedule(Integer id_schedule);

	public void removeDetail_Schedule(Integer id_detail_schedule);

	public void updateDetailSchedule(Detail_Schedule detail_schedule);
}
