package com.mfsys.DatabaseMonitoring.Entity;

public class EventEntity {

	private String dmp_prodcode;
	private String pet_eventcode;
	private String por_orgacode;
	private String ptr_trancode;
	private String pet_eventseqnum;
	private String pca_glaccredit;
	private String pca_glacdebit;
	private String pcr_currcode;
	private String selected;
	
	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
	public EventEntity(String dmp_prodcode, String pet_eventcode, String por_orgacode, String ptr_trancode,
			String pet_eventseqnum, String pca_glaccredit, String pca_glacdebit, String pcr_currcode) {
		super();
		this.dmp_prodcode = dmp_prodcode;
		this.pet_eventcode = pet_eventcode;
		this.por_orgacode = por_orgacode;
		this.ptr_trancode = ptr_trancode;
		this.pet_eventseqnum = pet_eventseqnum;
		this.pca_glaccredit = pca_glaccredit;
		this.pca_glacdebit = pca_glacdebit;
		this.pcr_currcode = pcr_currcode;
	}
	public EventEntity() {
		super();
	}
	public String getPcr_currcode() {
		return pcr_currcode;
	}
	public void setPcr_currcode(String pcr_currcode) {
		this.pcr_currcode = pcr_currcode;
	}
	public String getDmp_prodcode() {
		return dmp_prodcode;
	}
	public void setDmp_prodcode(String dmp_prodcode) {
		this.dmp_prodcode = dmp_prodcode;
	}
	public String getPet_eventcode() {
		return pet_eventcode;
	}
	public void setPet_eventcode(String pet_eventcode) {
		this.pet_eventcode = pet_eventcode;
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
	public String getPet_eventseqnum() {
		return pet_eventseqnum;
	}
	public void setPet_eventseqnum(String pet_eventseqnum) {
		this.pet_eventseqnum = pet_eventseqnum;
	}
	public String getPca_glaccredit() {
		return pca_glaccredit;
	}
	public void setPca_glaccredit(String pca_glaccredit) {
		this.pca_glaccredit = pca_glaccredit;
	}
	public String getPca_glacdebit() {
		return pca_glacdebit;
	}
	public void setPca_glacdebit(String pca_glacdebit) {
		this.pca_glacdebit = pca_glacdebit;
	}
}
