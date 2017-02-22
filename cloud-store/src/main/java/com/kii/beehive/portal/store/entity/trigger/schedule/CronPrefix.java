package com.kii.beehive.portal.store.entity.trigger.schedule;

public class CronPrefix implements SchedulePrefix {

	private String cron;

	@Override
	public String getType() {
		return "Cron";
	}

	public String getCron() {
		return cron;
	}

	public void setCron(String cron) {
		this.cron = cron;
	}
}
