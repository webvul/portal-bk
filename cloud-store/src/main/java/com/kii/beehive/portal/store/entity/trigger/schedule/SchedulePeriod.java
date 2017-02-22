package com.kii.beehive.portal.store.entity.trigger.schedule;

public class SchedulePeriod implements TriggerValidPeriod {

	private String startCron;

	private String endCron;


	public String getStartCron() {
		return startCron;
	}

	public void setStartCron(String startCron) {
		this.startCron = startCron;
	}

	public String getEndCron() {
		return endCron;
	}

	public void setEndCron(String endCron) {
		this.endCron = endCron;
	}
	@Override
	public String getType(){
		return "cron";
	};
}
