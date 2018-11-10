package org.uario.seaworkengine.platform.persistence.dao;

import java.util.Date;
import java.util.List;

import org.uario.seaworkengine.mobile.model.Badge;
import org.uario.seaworkengine.mobile.model.MobileUserDetail;
import org.uario.seaworkengine.model.DetailFinalSchedule;
import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.model.Schedule;

public interface ISchedule {

	void createBadge(Badge badge);

	void createDetailFinalSchedule(DetailFinalSchedule detail_schedule);

	void createDetailInitialSchedule(DetailInitialSchedule detail_schedule);

	void deleteBadge(Integer id_badge);

	Integer getFirstShift(Date date_scheduled, Integer user);

	Integer getLastShift(Date date_scheduled, Integer user);

	List<Badge> loadBadgeByScheduleId(Integer id_schedule);

	List<DetailFinalSchedule> loadDetailFinalScheduleByIdSchedule(Integer id_schedule);

	List<DetailFinalSchedule> loadDetailFinalScheduleByIdScheduleAndShift(Integer id_schedule, Integer shift);

	List<DetailInitialSchedule> loadDetailInitialScheduleByIdSchedule(Integer id_schedule);

	List<DetailInitialSchedule> loadDetailInitialScheduleByIdScheduleAndShift(Integer id_schedule, Integer shift);

	List<MobileUserDetail> loadMobileUserFinalDetail(Integer id_schedule, Integer no_shift);

	List<MobileUserDetail> loadMobileUserInitialDetail(Integer id_schedule, Integer no_shift);

	List<Schedule> loadSchedule(Date date_scheduled);

	Schedule loadSchedule(Date date_scheduler, Integer id_user);

	Schedule loadScheduleById(Integer id);

	Schedule loadScheduleByIdForMobile(Integer id);

	void removeAllDetailFinalScheduleBySchedule(Integer id_schedule);

	void removeAllDetailFinalScheduleByScheduleAndShift(Integer id_schedule, Integer shift);

	void removeAllDetailInitialScheduleBySchedule(Integer id_schedule);

	void removeAllDetailInitialScheduleByScheduleAndShift(Integer id_schedule, Integer shift);

	void removeDetailFinalSchedule(Integer id);

	void removeSchedule(Date date_scheduler, Integer id_user);

	void removeScheduleUser(Integer idUser, Date initialDate, Date finalDate);

	void saveListDetailFinalScheduler(Integer id_schedule, Integer shift, List<DetailFinalSchedule> details);

	void saveListDetailInitialScheduler(Integer id_schedule, Integer shift, List<DetailInitialSchedule> details);

	void saveOrUpdateSchedule(Schedule currentSchedule);

	List<Schedule> selectAggregateSchedulersProgram(Date initial_date, Date final_date, String full_text_search);

	List<Schedule> selectAggregateSchedulersProgram(Date firstDateInGrid, String full_text_search);

	List<Schedule> selectAggregateSchedulersRevision(Date initial_date, Date final_date, String full_text_search);

	List<Schedule> selectAggregateSchedulersRevision(Date firstDateInGrid, String full_text_search);

	List<Schedule> selectScheduleInIntervalDateByUserId(Integer user, Date date_from, Date date_to);

	List<Schedule> selectSchedulersForPreprocessing(Date initial_date, Date final_date, String my_full_text_search);

	List<Schedule> selectSchedulersForPreprocessingOnUserId(Date initial_date, Date final_date, Integer userid);

	void updateBreakEx(Integer id, Boolean break_ex);

	void updateBreakForce(Integer id, Boolean break_force);

	void updateMobileUserNote(Integer id, String note);

	void updateScheduleNote(Integer id_schedule, String note);

}
