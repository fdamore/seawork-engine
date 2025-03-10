package org.uario.seaworkengine.platform.persistence.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.uario.seaworkengine.model.Complaint;
import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.ReviewShipWork;
import org.uario.seaworkengine.model.Schedule;
import org.uario.seaworkengine.model.TerminalProductivity;
import org.uario.seaworkengine.statistics.RateShift;
import org.uario.seaworkengine.statistics.ReviewShipWorkAggregate;
import org.uario.seaworkengine.statistics.ShipOverview;
import org.uario.seaworkengine.statistics.ShipTotal;
import org.uario.seaworkengine.statistics.impl.MonitorData;

public interface IStatistics {

	public HashMap<Integer, ShipTotal> countComplaintByCustomer(Integer year, Integer id_customer);

	public void createComplaint(Complaint complaint);

	public void createTerminalProductivity(TerminalProductivity terminalProductivity);

	/**
	 * days to remove in sat procedure
	 *
	 * @param user_id
	 * @param date_from
	 * @param date_to
	 * @return
	 */
	public Integer daysToRemoveFromSaturation(Integer user_id, Date date_from, Date date_to);

	public void deleteComplaint(Integer id);

	public void deleteTerminalProductivity(Integer id);

	/**
	 * Get average for shit giving user and date
	 *
	 * @param user
	 *            the user to calculate average
	 * @param date
	 *            date
	 * @return Averages
	 */
	public RateShift[] getAverageForShift(Integer user, Date date, Date date_from);

	/**
	 * Get average for shit giving user and date. Using program data
	 *
	 * @param user
	 *            the user to calculate average
	 * @param date
	 *            date
	 * @return Averages
	 */
	public RateShift[] getAverageForShiftOnProgram(Integer user, Date date, Date date_from);

	/**
	 * Get counting sunday work for shift
	 *
	 * @param user
	 *            the user to calculate average
	 * @param date
	 *            date
	 * @return Averages
	 */
	public RateShift[] getCountSundayForShift(Integer user, Date date, Date date_from);

	/**
	 * Get date at work
	 *
	 * @param id_user
	 * @param date_from
	 * @param date_to
	 * @return
	 */
	public List<Date> getDateAtWork(Integer id_user, Date date_from, Date date_to);

	/**
	 * Get Date break for a user in period
	 *
	 * @param id_user
	 * @param date_from
	 * @param date_to
	 * @return
	 */
	public Date getDatesBreak(Integer id_user, Date date_from, Date date_to);

	/**
	 * Get percentage working sunday and holidays
	 *
	 * @param id_user
	 * @param date_to
	 *
	 * @return
	 */
	public Integer getHolidaysWork(Integer id_user, Date date_from, Date date_to);

	/**
	 * Get data monitor
	 *
	 * @param date_request
	 * @return
	 */
	public List<MonitorData> getMonitorData(Date date_request);

	/**
	 * Monitor detail
	 * 
	 * @param date_request
	 * @param shift
	 * @param idShip
	 * @return
	 */
	public List<DetailFinalSchedule> getMonitorDetail(Date date_request, Integer shift, Integer idShip);

	/**
	 * Get ship Number
	 *
	 * @param operation
	 * @param year
	 * @param by_invoice
	 * @return
	 */
	public List<ShipTotal> getShipNumber(String operation, Integer year, Boolean by_invoice);

	/**
	 * Get percentage working sunday
	 *
	 * @param id_user
	 * @param date_to
	 *
	 * @return
	 */
	public Integer getSundayWork(Integer id_user, Date date_from, Date date_to);

	/**
	 * Get How many hours user work in period
	 *
	 * @param user
	 * @param date_from
	 * @param date_to
	 * @return
	 */
	public Double getTimeWorkCountByUser(Integer user, Date date_from, Date date_to);

	/**
	 * Get Time Worked
	 *
	 * @param id_user
	 * @param date_from
	 * @param date_to
	 * @return
	 */
	public Double getTimeWorked(Integer id_user, Date date_from, Date date_to);

	public List<ShipTotal> getTotalContainer(Integer year, Integer idService, Boolean by_invoice);

	/**
	 * @param year
	 * @param shift
	 * @param idService
	 * @param by_invoice
	 * @return
	 */
	public List<ShipTotal> getTotalHandsMen(final Integer year, final Integer shift, Integer idService, final Boolean by_invoice);

	public List<ShipTotal> getTotalHoursByTask(final Integer year);

	/**
	 * @param year
	 * @param idService
	 * @param by_invoice
	 * @return
	 */
	public List<ShipTotal> getTotalService(Integer year, Integer idService, Boolean by_invoice);

	/**
	 * get info about volume
	 *
	 * @param year
	 * @param idService
	 * @param by_invoice
	 * @return
	 */
	public List<ShipTotal> getTotalVolume(Integer year, Integer idService, Boolean by_invoice);

	/**
	 * List detail initial schedule
	 *
	 * @param full_text_search
	 * @param shift_number
	 * @param shift_type
	 *
	 * @param date_from
	 * @param date_to
	 *
	 * @return
	 */
	public List<DetailFinalSchedule> listDetailFinalSchedule(String full_text_search, Integer shift_number, Integer shift_type, Integer task_id,
	        Date date_from, Date date_to, Boolean reviewshift, Integer idShip, String crane, Integer rf_sws);

	/**
	 * List detail initial schedule
	 *
	 * @param full_text_search
	 * @param shift_number
	 * @param shift_type
	 *            * @param date_from
	 * @param date_to
	 *
	 * @return
	 */
	public List<DetailInitialSchedule> listDetailInitialSchedule(String full_text_search, Integer shift_number, Integer shift_type, Integer task_id,
	        Date date_from, Date date_to);

	/**
	 * List detail initial schedule
	 *
	 * @param full_text_search
	 *
	 * @param shift_number
	 *
	 * @param date_from
	 *
	 * @param date_to
	 *
	 * @return
	 */
	public List<Schedule> listSchedule(String full_text_search, Integer shift, Date date_from, Date date_to);

	public Complaint loadComplaint(Integer idCustomer, Integer year, Integer month);

	public Complaint loadComplaintById(Integer id);

	public List<Complaint> loadComplaintByYear(Integer year);

	/**
	 * @param date_from
	 * @param date_to
	 * @param searchText
	 * @param rifSWS
	 * @param rifMCT
	 * @param shift
	 * @param invoicing_cycle
	 * @param working_cycle
	 * @return
	 */
	public List<ReviewShipWork> loadReviewShipWork(Date date_from, Date date_to, String searchText, Integer rifSWS, String rifMCT, Integer shift,
	        Integer invoicing_cycle, Integer serviceId, String shipType, String shipLine, String shipCondition);

	/**
	 * @param date_from
	 * @param date_to
	 * @param searchText
	 * @param rifSWS
	 * @param rifMCT
	 * @param shift
	 * @param invoicing_cycle
	 * @param working_cycle
	 * @return
	 */
	public List<ReviewShipWorkAggregate> loadReviewShipWorkAggregate(Date date_from, Date date_to, Integer rifSWS, String rifMCT,
	        Integer invoicing_cycle, String searchText, Integer serviceId, String shipType, String shipLine, String shipCondition);

	public TerminalProductivity loadTerminalProductivity(Integer id);

	public List<TerminalProductivity> loadTerminalProductivityYear(Integer year);

	/**
	 * Get overview abount ship
	 *
	 * @param text_search
	 * @param date_from
	 * @param date_to
	 * @return
	 */
	public List<ShipOverview> overviewFinalScheduleByShip(String text_search, Date date_from, Date date_to, String shipType, String shipLine,
	        String shipCondition);

	public void updateComplaint(Complaint complaint);

	public void updateTerminalProductivity(TerminalProductivity terminalProductivity);

}
