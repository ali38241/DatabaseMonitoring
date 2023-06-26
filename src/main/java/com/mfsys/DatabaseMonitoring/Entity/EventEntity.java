package com.mfsys.DatabaseMonitoring.Entity;

public class EventEntity {

	private int pet_eventcode;
	private String pet_eventdesc;
	private int system_generated;
	
	
	public int getPet_eventcode() {
		return pet_eventcode;
	}
	public void setPet_eventcode(int pet_eventcode) {
		this.pet_eventcode = pet_eventcode;
	}
	public String getPet_eventdesc() {
		return pet_eventdesc;
	}
	public void setPet_eventdesc(String pet_eventdesc) {
		this.pet_eventdesc = pet_eventdesc;
	}
	public int getSystem_gen() {
		return system_generated;
	}
	public void setSystem_generated(int system_generated) {
		this.system_generated = system_generated;
	}
	public EventEntity(int pet_eventcode, String pet_eventdesc, int system_gen) {
		super();
		this.pet_eventcode = pet_eventcode;
		this.pet_eventdesc = pet_eventdesc;
		this.system_generated = system_gen;
	}
	public EventEntity() {
		super();
	}
	
	
}
