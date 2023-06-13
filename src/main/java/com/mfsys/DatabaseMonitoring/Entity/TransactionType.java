package com.mfsys.DatabaseMonitoring.Entity;

public class TransactionType {

	private String por_orgacode;
	private String ptr_trancode;
	private String pet_eventcode;
	private String ptr_trandesc;
	private String system_generated;
private String selected;
	
	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
	public TransactionType() {
		super();
	}
	public TransactionType(String por_orgacode, String ptr_trancode, String pet_eventcode, String ptr_trandesc,
			String system_generated) {
		super();
		this.por_orgacode = por_orgacode;
		this.ptr_trancode = ptr_trancode;
		this.pet_eventcode = pet_eventcode;
		this.ptr_trandesc = ptr_trandesc;
		this.system_generated = system_generated;
	}
	public String getPor_orgacode() {
		return por_orgacode;
	}
	public void setPor_orgacode(String por_orgacode) {
		this.por_orgacode = por_orgacode;
	}
	public String getPtr_trancode() {
		return ptr_trancode;
	}
	public void setPtr_trancode(String ptr_trancode) {
		this.ptr_trancode = ptr_trancode;
	}
	public String getPet_eventcode() {
		return pet_eventcode;
	}
	public void setPet_eventcode(String pet_eventcode) {
		this.pet_eventcode = pet_eventcode;
	}
	public String getPtr_trandesc() {
		return ptr_trandesc;
	}
	public void setPtr_trandesc(String ptr_trandesc) {
		this.ptr_trandesc = ptr_trandesc;
	}
	public String getSystem_generated() {
		return system_generated;
	}
	public void setSystem_generated(String system_generated) {
		this.system_generated = system_generated;
	}
	
	
	
}
