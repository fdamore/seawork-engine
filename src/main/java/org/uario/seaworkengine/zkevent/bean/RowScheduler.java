package org.uario.seaworkengine.zkevent.bean;

import org.uario.seaworkengine.model.Scheduler;

public class RowScheduler {

	private String		employee_identification;

	// used to visualize it
	private String		name_scheduled;

	private Integer		scheduled;

	private Scheduler	scheduler_1	= new Scheduler();

	private Scheduler	scheduler_2	= new Scheduler();

	private Scheduler	scheduler_3	= new Scheduler();

	private Scheduler	scheduler_4	= new Scheduler();

	private Scheduler	scheduler_5	= new Scheduler();

	private Scheduler	scheduler_6	= new Scheduler();

	private Scheduler	scheduler_7	= new Scheduler();

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Scheduler)) {
			return false;
		}

		final RowScheduler item = (RowScheduler) obj;

		if ((item.scheduled == null) || (this.scheduled == null)) {
			return false;
		}

		return this.scheduled.equals(item.scheduled);

	}

	public String getEmployee_identification() {
		return this.employee_identification;
	}

	public String getName_scheduled() {
		return this.name_scheduled;
	}

	public Integer getScheduled() {
		return this.scheduled;
	}

	public Scheduler getScheduler_1() {
		return this.scheduler_1;
	}

	public Scheduler getScheduler_2() {
		return this.scheduler_2;
	}

	public Scheduler getScheduler_3() {
		return this.scheduler_3;
	}

	public Scheduler getScheduler_4() {
		return this.scheduler_4;
	}

	public Scheduler getScheduler_5() {
		return this.scheduler_5;
	}

	public Scheduler getScheduler_6() {
		return this.scheduler_6;
	}

	public Scheduler getScheduler_7() {
		return this.scheduler_7;
	}

	public void setEmployee_identification(final String employee_identification) {
		this.employee_identification = employee_identification;
	}

	public void setName_scheduled(final String name_scheduled) {
		this.name_scheduled = name_scheduled;
	}

	public void setScheduled(final Integer scheduled) {
		this.scheduled = scheduled;
	}

	public void setScheduler_1(final Scheduler scheduler_1) {
		this.scheduler_1 = scheduler_1;
	}

	public void setScheduler_2(final Scheduler scheduler_2) {
		this.scheduler_2 = scheduler_2;
	}

	public void setScheduler_3(final Scheduler scheduler_3) {
		this.scheduler_3 = scheduler_3;
	}

	public void setScheduler_4(final Scheduler scheduler_4) {
		this.scheduler_4 = scheduler_4;
	}

	public void setScheduler_5(final Scheduler scheduler_5) {
		this.scheduler_5 = scheduler_5;
	}

	public void setScheduler_6(final Scheduler scheduler_6) {
		this.scheduler_6 = scheduler_6;
	}

	public void setScheduler_7(final Scheduler scheduler_7) {
		this.scheduler_7 = scheduler_7;
	}

}
