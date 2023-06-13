package com.mfsys.DatabaseMonitoring.Entity;

public class Charges {
	private String pch_chrgcode;
	private String por_orgacode;
	private String pch_chrgdesc;
	private String pch_chrgshort;
	private String pel_elmtcode;
	private String ptr_trancode;
	private String pch_chrginterest;
	private String pch_chrgpenalty;
	private String pch_chrgprincipal;
	private String pch_chrgprofit;
	private String soc_charges;
	private String selected;
	public String getSelected() {
		return selected;
	}
	public void setSelected(String selected) {
		this.selected = selected;
	}
	public Charges(String pch_chrgcode, String por_orgacode, String pch_chrgdesc, String pch_chrgshort,
			String pel_elmtcode, String ptr_trancode, String pch_chrginterest, String pch_chrgpenalty,
			String pch_chrgprincipal, String pch_chrgprofit, String soc_charges) {
		super();
		this.pch_chrgcode = pch_chrgcode;
		this.por_orgacode = por_orgacode;
		this.pch_chrgdesc = pch_chrgdesc;
		this.pch_chrgshort = pch_chrgshort;
		this.pel_elmtcode = pel_elmtcode;
		this.ptr_trancode = ptr_trancode;
		this.pch_chrginterest = pch_chrginterest;
		this.pch_chrgpenalty = pch_chrgpenalty;
		this.pch_chrgprincipal = pch_chrgprincipal;
		this.pch_chrgprofit = pch_chrgprofit;
		this.soc_charges = soc_charges;
	}
	public Charges() {
		super();
	}
	public String getPch_chrgcode() {
		return pch_chrgcode;
	}
	public void setPch_chrgcode(String pch_chrgcode) {
		this.pch_chrgcode = pch_chrgcode;
	}
	public String getPor_orgacode() {
		return por_orgacode;
	}
	public void setPor_orgacode(String por_orgacode) {
		this.por_orgacode = por_orgacode;
	}
	public String getPch_chrgdesc() {
		return pch_chrgdesc;
	}
	public void setPch_chrgdesc(String pch_chrgdesc) {
		this.pch_chrgdesc = pch_chrgdesc;
	}
	public String getPch_chrgshort() {
		return pch_chrgshort;
	}
	public void setPch_chrgshort(String pch_chrgshort) {
		this.pch_chrgshort = pch_chrgshort;
	}
	public String getPel_elmtcode() {
		return pel_elmtcode;
	}
	public void setPel_elmtcode(String pel_elemtcode) {
		this.pel_elmtcode = pel_elemtcode;
	}
	public String getPtr_trancode() {
		return ptr_trancode;
	}
	public void setPtr_trancode(String ptr_trancode) {
		this.ptr_trancode = ptr_trancode;
	}
	public String getPch_chrginterest() {
		return pch_chrginterest;
	}
	public void setPch_chrginterest(String pch_chrginterest) {
		this.pch_chrginterest = pch_chrginterest;
	}
	public String getPch_chrgpenalty() {
		return pch_chrgpenalty;
	}
	public void setPch_chrgpenalty(String pch_chrgpenalty) {
		this.pch_chrgpenalty = pch_chrgpenalty;
	}
	public String getPch_chrgprincipal() {
		return pch_chrgprincipal;
	}
	public void setPch_chrgprincipal(String pch_chrgprincipal) {
		this.pch_chrgprincipal = pch_chrgprincipal;
	}
	public String getPch_chrgprofit() {
		return pch_chrgprofit;
	}
	public void setPch_chrgprofit(String chrgprofit) {
		this.pch_chrgprofit = chrgprofit;
	}
	public String getSoc_charges() {
		return soc_charges;
	}
	public void setSoc_charges(String soc_charges) {
		this.soc_charges = soc_charges;
	}
	
}
