package org.uario.seaworkengine.platform.persistence.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.DetailFinalScheduleShip;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.ScheduleShip;
import org.uario.seaworkengine.model.Ship;

public interface IScheduleShip {

	void createDetailFinalScheduleShip(DetailFinalScheduleShip detailFinalScheduleShip);

	void createDetailScheduleShip(DetailScheduleShip detailscheduleShip);

	void createScheduleShip(ScheduleShip scheduleShip);

	void deleteDetailFinalScheduleShipById(Integer id);

	void deleteDetailScheduleShip(Integer id_detailScheduleShip);

	void deleteScheduleShip(Integer id_scheduleShip);

	/**
	 * @param id_schedule
	 * @return
	 */
	String getDetailScheduleShipNote(Integer id_schedule);

	List<DetailFinalScheduleShip> loadDetailFinalScheduleShipByIdDetailScheduleShip(Integer idDetailScheduleShip);

	List<DetailScheduleShip> loadDetailScheduleShipByIdSchedule(Integer id_scheduleShip);

	ScheduleShip loadScheduleShip(Integer id_scheduleShip);

	List<ScheduleShip> loadScheduleShipByIdShipAndArrivalDate(Integer idship, Date arrivaldate);

	List<Ship> loadShipInDate(Timestamp arrivalDate);

	/**
	 * If dateshift is not null, the query will use only dateshift
	 *
	 * @param datefrom
	 * @param dateto
	 * @param dateshift           TODO
	 * @param period_on_dateshift TODO
	 * @param full_text_search
	 * @param shift
	 * @param idCustomer
	 * @param nowork
	 * @param activityh
	 * @param worked
	 * @param serviceId
	 * @return
	 */
	List<DetailScheduleShip> searchDetailScheduleShip(Date datefrom, Date dateto, Date dateshift, Boolean period_on_dateshift,
							String full_text_search, Integer shift, Integer idCustomer, Boolean nowork, Boolean activityh, Boolean worked,
							Integer serviceId, String shipType, String shipLine, String shipCondition, String operation, String invoice_period);

	/**
	 * @param datefrom
	 * @param dateto
	 * @param dateshift
	 * @param period_on_dateshift
	 * @param full_text_search
	 * @param shift
	 * @param idCustomer
	 * @param nowork
	 * @param activityh
	 * @param worked
	 * @param serviceId
	 * @param shipType
	 * @param shipLine
	 * @param shipCondition
	 * @param invoice
	 * @return
	 */
	List<DetailScheduleShip> searchDetailScheduleShip(Date datefrom, Date dateto, Date dateshift, Boolean period_on_dateshift,
							String full_text_search, Integer shift, Integer idCustomer, Boolean nowork, Boolean activityh, Boolean worked,
							Integer serviceId, String shipType, String shipLine, String shipCondition, String operation, String invoice_period,
							boolean invoice);

	List<DetailScheduleShip> searchDetailScheduleShipByDateshit(Date shiftdate, String full_text_search, Integer shift, Integer idCustomer,
							Boolean nowork, Boolean activityh, Boolean worked, Integer serviceId);

	List<DetailScheduleShip> searchDetailScheduleShipRif_MCT_SWS(Integer rif_sws, String rif_mct);

	List<ScheduleShip> searchScheduleShip(Date datefrom, Date dateto, Integer sws, String mct, Integer idCustomer, Integer idService,
							String textSearch, String shipType, String shipLine, String shipCondition, Boolean intial_support);

	void updateDetailFinalScheduleShip(DetailFinalScheduleShip detailFinalScheduleShip);

	void updateDetailScheduleShip(DetailScheduleShip detailScheduleShip);

	void updateDetailScheduleShipForMobile(Integer id, Integer handswork, Integer manwork, Boolean worked);

	void updateDetailScheduleShipNote(Integer id_schedule, String note);

	void updateRifMCT(Integer id, String rif_mct);

	void updateScheduleShip(ScheduleShip scheduleShip);

}
