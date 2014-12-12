package org.uario.seaworkengine.platform.persistence.dao;

import java.sql.Timestamp;
import java.util.List;

import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.ScheduleShip;

public interface IScheduleShip {

	public void createDetailScheduleShip(DetailScheduleShip detailscheduleShip);

	public void createScheduleShip(ScheduleShip scheduleShip);

	public void deleteDetailScheduleShip(Integer id_detailScheduleShip);

	public void deleteScheduleShip(Integer id_scheduleShip);

	public List<ScheduleShip> loadAllScheduleShip();

	public DetailScheduleShip loadDetailScheduleShip(Integer id_detailScheduleShip);

	public List<DetailScheduleShip> loadDetailScheduleShipByIdSchedule(Integer id_scheduleShip);

	public ScheduleShip loadScheduleShip(Integer id_scheduleShip);

	public ScheduleShip loadScheduleShipByIdShipAndArrivalDate(Integer idship, Timestamp arrivaldate);

	public List<ScheduleShip> selectAllScheduleShipFulltextSearchLike(String full_text_search);

	public void updateDetailScheduleShip(DetailScheduleShip detailScheduleShip);

	public void updateScheduleShip(ScheduleShip scheduleShip);
}
