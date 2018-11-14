package org.uario.seaworkengine.platform.persistence.dao;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.DetailFinalScheduleShip;
import org.uario.seaworkengine.model.DetailScheduleShip;
import org.uario.seaworkengine.model.ScheduleShip;
import org.uario.seaworkengine.model.Ship;

public interface IScheduleShip {

	public void createDetailFinalScheduleShip(DetailFinalScheduleShip detailFinalScheduleShip);

	public void createDetailScheduleShip(DetailScheduleShip detailscheduleShip);

	public void createScheduleShip(ScheduleShip scheduleShip);

	public void deleteDetailFinalScheduleShipById(Integer id);

	public void deleteDetailScheduleShip(Integer id_detailScheduleShip);

	public void deleteScheduleShip(Integer id_scheduleShip);

	/**
	 * @param id_schedule
	 * @return
	 */
	public String getDetailScheduleShipNote(Integer id_schedule);

	public List<DetailFinalScheduleShip> loadDetailFinalScheduleShipByIdDetailScheduleShip(Integer idDetailScheduleShip);

	public List<DetailScheduleShip> loadDetailScheduleShipByIdSchedule(Integer id_scheduleShip);

	public ScheduleShip loadScheduleShip(Integer id_scheduleShip);

	public List<ScheduleShip> loadScheduleShipByIdShipAndArrivalDate(Integer idship, Date arrivaldate);

	public List<Ship> loadShipInDate(Timestamp arrivalDate);

	/**
	 * If dateshift is not null, the query will use only dateshift
	 *
	 * @param datefrom
	 * @param dateto
	 * @param dateshift
	 *            TODO
	 * @param period_on_dateshift
	 *            TODO
	 * @param full_text_search
	 * @param shift
	 * @param idCustomer
	 * @param nowork
	 * @param activityh
	 * @param worked
	 * @param serviceId
	 * @return
	 */
	public List<DetailScheduleShip> searchDetailScheduleShip(Date datefrom, Date dateto, Date dateshift, Boolean period_on_dateshift,
	        String full_text_search, Integer shift, Integer idCustomer, Boolean nowork, Boolean activityh, Boolean worked, Integer serviceId,
	        String shipType, String shipLine, String shipCondition, String operation, String invoice_period);

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
	public List<DetailScheduleShip> searchDetailScheduleShip(Date datefrom, Date dateto, Date dateshift, Boolean period_on_dateshift,
	        String full_text_search, Integer shift, Integer idCustomer, Boolean nowork, Boolean activityh, Boolean worked, Integer serviceId,
	        String shipType, String shipLine, String shipCondition, String operation, String invoice_period, boolean invoice);

	public List<DetailScheduleShip> searchDetailScheduleShipByDateshit(Date shiftdate, String full_text_search, Integer shift, Integer idCustomer,
	        Boolean nowork, Boolean activityh, Boolean worked, Integer serviceId);

	public List<DetailScheduleShip> searchDetailScheduleShipRif_MCT_SWS(Integer rif_sws, String rif_mct);

	public List<ScheduleShip> searchScheduleShip(Date datefrom, Date dateto, Integer sws, String mct, Integer idCustomer, Integer idService,
	        String textSearch, String shipType, String shipLine, String shipCondition, Boolean intial_support);

	public void updateDetailFinalScheduleShip(DetailFinalScheduleShip detailFinalScheduleShip);

	public void updateDetailScheduleShip(DetailScheduleShip detailScheduleShip);

	public void updateDetailScheduleShipForMobile(Integer id, Integer handswork);

	public void updateDetailScheduleShipNote(Integer id_schedule, String note);

	public void updateRifMCT(Integer id, String rif_mct);

	public void updateScheduleShip(ScheduleShip scheduleShip);

}
