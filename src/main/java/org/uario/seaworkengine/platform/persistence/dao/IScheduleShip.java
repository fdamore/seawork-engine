package org.uario.seaworkengine.platform.persistence.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.DetailFinalScheduleShip;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.ScheduleShip;
import org.uario.seaworkengine.model.Ship;
import org.uario.seaworkengine.statistics.ShipTotal;

public interface IScheduleShip {

	public ShipTotal calculateHandsWorkAndMensByArrivalDateAndShipName(Date arrivaldate, String full_text_search, Integer shift);

	public ShipTotal calculateHandsWorkInDate(Timestamp dateFrom, Timestamp dateTo, Integer shift);

	public Integer calculateNumberOfShipByArrivalDateAndShipName(Date arrivaldate, String full_text_search, Integer shift);

	public Integer calculateNumberOfShipInDate(Timestamp dateFrom, Timestamp dateTo, Integer shift);

	public Integer calculateVolumeByArrivalDateAndShipName(Date arrivaldate, String full_text_search, Integer shift);

	public Integer calculateVolumeInDate(Timestamp dateFrom, Timestamp dateTo, Integer shift);

	public void createDetailFinalScheduleShip(DetailFinalScheduleShip detailFinalScheduleShip);

	public void createDetailScheduleShip(DetailScheduleShip detailscheduleShip);

	public void createScheduleShip(ScheduleShip scheduleShip);

	public void deleteDetailFinalScheduleShipById(Integer id);

	public void deleteDetailScheduleShip(Integer id_detailScheduleShip);

	public void deleteScheduleShip(Integer id_scheduleShip);

	public void deteleDetailSchedueleShipByIdSchedule(Integer id_scheduleShip);

	public DetailFinalScheduleShip loadDetailFinalScheduleShipById(Integer id);

	public List<DetailFinalScheduleShip> loadDetailFinalScheduleShipByIdDetailScheduleShip(Integer idDetailScheduleShip);

	public List<DetailScheduleShip> loadDetailScheduleShipByIdSchedule(Integer id_scheduleShip);

	public ScheduleShip loadScheduleShip(Integer id_scheduleShip);

	public List<ScheduleShip> loadScheduleShipByArrivalDate(Timestamp arrivaldate);

	public List<ScheduleShip> loadScheduleShipByIdShipAndArrivalDate(Integer idship, Date arrivaldate);

	public List<Ship> loadShipInDate(Timestamp arrivalDate);

	public List<DetailScheduleShip> searchDetailScheduleShip(Date datefrom, Date dateto, String full_text_search, Integer shift, Integer idCustomer,
			Boolean nowork, Boolean activityh);

	public List<DetailScheduleShip> searchDetailScheduleShip(Date shiftdate, String full_text_search, Integer shift, Integer idCustomer,
			Boolean nowork, Boolean activityh);

	public List<ScheduleShip> searchScheduleShip(Date datefrom, Date dateto, Integer sws, String mct);

	public List<ScheduleShip> selectAllScheduleShipFulltextSearchLike(String full_text_search);

	public void updateDetailFinalScheduleShip(DetailFinalScheduleShip detailFinalScheduleShip);

	public void updateDetailScheduleShip(DetailScheduleShip detailScheduleShip);

	public void updateDetailScheduleShipForMobile(DetailScheduleShip sch);

	public void updateRifMCT(Integer id, String rif_mct);

	public void updateScheduleShip(ScheduleShip scheduleShip);
}
