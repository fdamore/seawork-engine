package org.uario.seaworkengine.web.login;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.uario.seaworkengine.model.LockTable;
import org.uario.seaworkengine.platform.persistence.dao.LockTableDAO;
import org.uario.seaworkengine.utility.TableTag;

public class LoginSuccessHandlerImpl implements AuthenticationSuccessHandler {

	public final static String	MOBILE_TAG		= "MOBILE_TAG";

	private final String		APPLICATION_URL	= "application/index.zul";

	private Integer				hours_loked;

	private LockTableDAO		lockTable		= null;

	private final String		MOBILE_URL		= "mobile/index.zul";

	/**
	 * Check the lock, if each table locked more that hours_loked, return true, else
	 * false
	 *
	 * @return
	 */
	private boolean checkLockProgram() {

		final LockTable userLockTable = this.lockTable.loadLockTableByTableType(TableTag.PROGRAM_TABLE);

		if (userLockTable != null) {
			final Calendar cal = Calendar.getInstance();
			cal.getTime();

			final long startLock = TimeUnit.MILLISECONDS.toMinutes(cal.getTime().getTime() - userLockTable.getTime_start().getTime());

			if (startLock > (this.hours_loked * 60)) {
				return true;
			}

		}

		return false;
	}

	/**
	 * Check the lock, if each table locked more that hours_loked, return true, else
	 * false
	 *
	 * @return
	 */
	private boolean checkLockRevision() {

		final LockTable userLockTable = this.lockTable.loadLockTableByTableType(TableTag.REVIEW_TABLE);

		if (userLockTable != null) {
			final Calendar cal = Calendar.getInstance();
			cal.getTime();

			final long startLock = TimeUnit.MILLISECONDS.toMinutes(cal.getTime().getTime() - userLockTable.getTime_start().getTime());

			if (startLock > (this.hours_loked * 60)) {
				return true;
			}

		}

		return false;
	}

	public Integer getHours_loked() {
		return this.hours_loked;
	}

	public LockTableDAO getLockTable() {
		return this.lockTable;
	}

	@Override
	public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication)
			throws IOException, ServletException {

		try {

			final boolean check_table_program = this.checkLockProgram();

			if (check_table_program) {
				this.unlockTableProgram();
			}

			final boolean check_table_revision = this.checkLockRevision();

			if (check_table_revision) {
				this.unlockTableRevision();
			}

			// define redirect

			final HttpSession session = request.getSession();

			final String mobile_tag = (String) session.getAttribute(LoginSuccessHandlerImpl.MOBILE_TAG);
			if (StringUtils.equals(mobile_tag, LoginSuccessHandlerImpl.MOBILE_TAG)) {
				session.setAttribute(LoginSuccessHandlerImpl.MOBILE_TAG, "ciao");
				response.sendRedirect(this.MOBILE_URL);

			} else {
				response.sendRedirect(this.APPLICATION_URL);
			}

			return;
		} finally {
			request.setAttribute(LoginSuccessHandlerImpl.MOBILE_TAG, null);
		}

	}

	public void setHours_loked(final Integer hours_loked) {
		this.hours_loked = hours_loked;
	}

	public void setLockTable(final LockTableDAO lockTable) {
		this.lockTable = lockTable;
	}

	/**
	 * Unlock all table (with double checking)
	 */
	private synchronized void unlockTableProgram() {

		final boolean check_table = this.checkLockProgram();

		if (!check_table) {
			return;
		}

		final LockTable userLockTable = this.lockTable.loadLockTableByTableType(TableTag.PROGRAM_TABLE);

		if (userLockTable != null) {
			userLockTable.setTime_to(new Timestamp(Calendar.getInstance().getTime().getTime()));
			userLockTable.setId_user_closer(null);
			this.lockTable.updateLockTable(userLockTable);
		}
		return;

	}

	/**
	 * Unlock all table (with double checking)
	 */
	private synchronized void unlockTableRevision() {

		final boolean check_table = this.checkLockRevision();

		if (!check_table) {
			return;
		}

		final LockTable userLockTable = this.lockTable.loadLockTableByTableType(TableTag.REVIEW_TABLE);

		if (userLockTable != null) {
			userLockTable.setTime_to(new Timestamp(Calendar.getInstance().getTime().getTime()));
			userLockTable.setId_user_closer(null);
			this.lockTable.updateLockTable(userLockTable);
		}
		return;
	}
}
