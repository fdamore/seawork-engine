package org.uario.seaworkengine.web.zkevent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.uario.seaworkengine.model.DetailInitialSchedule;
import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.web.services.IWebServiceController;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;
import org.uario.seaworkengine.web.services.handler.InitialScheduleSingleDetail;
import org.zkoss.bind.annotation.AfterCompose;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.ContextParam;
import org.zkoss.bind.annotation.ContextType;
import org.zkoss.bind.annotation.NotifyChange;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;

public class MobileComposer {

	private IWebServiceController service;

	private List<InitialScheduleSingleDetail> users;

	public List<InitialScheduleSingleDetail> getUsers() {
		return this.users;
	}

	@AfterCompose
	public void init(@ContextParam(ContextType.COMPONENT) final Component component) throws Exception {
		this.service = (IWebServiceController) SpringUtil.getBean(BeansTag.WEBCONTROLLER);

		this.refresh(null);

	}

	@Command
	@NotifyChange({ "users" })
	public void refresh(@BindingParam("shift_no") final Integer shift_no) {
		if (this.users != null) {
			this.users.clear();
		} else {
			this.users = new ArrayList<>();
		}

		final List<InitialSchedule> list = this.service.selectInitialSchedule(Calendar.getInstance().getTime());

		// POST PROCESSING
		for (final InitialSchedule insch : list) {
			if (insch.getDetail_schedule() == null) {
				continue;
			}

			for (final DetailInitialSchedule detail : insch.getDetail_schedule()) {

				if (detail.getShift() == null) {
					continue;
				}

				// filter on shift
				if (shift_no != null) {
					if (!shift_no.equals(detail.getShift())) {
						continue;
					}
				}

				final InitialScheduleSingleDetail itm = new InitialScheduleSingleDetail();
				itm.setDetail_schedule(detail);
				itm.setPerson(insch.getPerson());
				itm.setSchedule(insch.getSchedule());

				this.users.add(itm);

			}

		}
	}

}
