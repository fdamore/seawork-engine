package org.uario.seaworkengine.web.zkevent;

import java.util.Calendar;
import java.util.List;

import org.uario.seaworkengine.utility.BeansTag;
import org.uario.seaworkengine.web.services.IWebServiceController;
import org.uario.seaworkengine.web.services.handler.InitialSchedule;
import org.zkoss.spring.SpringUtil;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;

public class MobileComposer extends SelectorComposer<Component> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private IWebServiceController service;

	@Wire
	private Listbox users;

	@Override
	public void doFinally() throws Exception {
		this.service = (IWebServiceController) SpringUtil.getBean(BeansTag.WEBCONTROLLER);

		final List<InitialSchedule> model = this.service.selectInitialSchedule(Calendar.getInstance().getTime());
		this.users.setModel(new ListModelList<>(model));

	}

}
