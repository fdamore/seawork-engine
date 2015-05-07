package org.uario.seaworkengine.platform.persistence.dao;

import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.ReviewShipWork;
import org.uario.seaworkengine.model.Schedule;

public interface ISchedule {

	public void createDetailFinalSchedule(DetailFinalSchedule detail_schedule);

	public void createDetailInitialSchedule(DetailInitialSchedule detail_schedule);

	public Integer getFirstShift(Date date_scheduled, Integer user);

	public Integer getLastShift(Date date_scheduled, Integer user);

	public List<DetailFinalSchedule> loadDetailFinalScheduleByIdSchedule(Integer id_schedule);

	public List<DetailFinalSchedule> loadDetailFinalScheduleByIdScheduleAndShift(Integer id_schedule, Integer shift);

	public List<DetailInitialSchedule> loadDetailFinalScheduleForMobileByIdScheduleAndNoShift(Integer id_schedule, Integer no_shift);

	public List<DetailInitialSchedule> loadDetailInitialScheduleByIdSchedule(Integer id_schedule);

	public List<DetailInitialSchedule> loadDetailInitialScheduleByIdScheduleAndShift(Integer id_schedule, Integer shift);

	public List<DetailInitialSchedule> loadDetailInitialScheduleForMobileByIdScheduleAndNoShift(Integer id_schedule, Integer no_shift);

	public List<ReviewShipWork> loadReviewShipWork(Date date_from, Date date_to, String searchText, Integer rifSWS, Integer rifMCT, Integer shift,
			Integer invoicing_cycle, Integer working_cycle);

	public List<Schedule> loadSchedule(Date date_scheduled);

	public Schedule loadSchedule(Date date_scheduler, Integer id_user);

	public Schedule loadScheduleById(Integer id);

	public void removeAllDetailFinalScheduleBySchedule(Integer id_schedule);

	public void removeAllDetailFinalScheduleByScheduleAndShift(Integer id_schedule, Integer shift);

	public void removeAllDetailInitialScheduleBySchedule(Integer id_schedule);

	public void removeAllDetailInitialScheduleByScheduleAndShift(Integer id_schedule, Integer shift);

	public void removeSchedule(Date date_scheduler, Integer id_user);

	public void removeScheduleUser(Integer idUser, Date initialDate, Date finalDate);

	public void removeScheduleUserFired(Integer idUser, Date firedDate);

	public void saveListDetailFinalScheduler(Integer id_schedule, Integer shift, List<DetailFinalSchedule> details);

	public void saveListDetailInitialScheduler(Integer id_schedule, Integer shift, List<DetailInitialSchedule> details);

	public void saveOrUpdateSchedule(Schedule currentSchedule);

	public List<Schedule> selectAggregateSchedulersProgram(Date initial_date, Date final_date, String full_text_search);

	public List<Schedule> selectAggregateSchedulersProgram(Date firstDateInGrid, String full_text_search);

	public List<Schedule> selectAggregateSchedulersRevision(Date initial_date, Date final_date, String full_text_search);

	public List<Schedule> selectAggregateSchedulersRevision(Date firstDateInGrid, String full_text_search);

	public List<Schedule> selectScheduleInIntervalDateByUserId(Integer user, Date date_from, Date date_to);

	public List<Schedule> selectSchedulersForPreprocessing(Date initial_date, Date final_date, String my_full_text_search);

	public List<Schedule> selectSchedulersForPreprocessingOnUserId(Date initial_date, Date final_date, Integer userid);

	public void updateMobileSynch(Integer id_schedule, boolean synch_mobile, Integer shift_no);

	public void updateScheduleNote(Integer id_schedule, String note);

}
