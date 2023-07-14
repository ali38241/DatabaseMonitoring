package com.mfsys.DatabaseMonitoring.Entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Table;

@MappedSuperclass
public abstract class TransactionType {

	@EmbeddedId
	private TransactionId transId;
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

	public TransactionId getTransId() {
		return transId;
	}

	public void setTransId(TransactionId transId) {
		this.transId = transId;
	}

	public TransactionType(TransactionId transId, String pet_eventcode, String ptr_trandesc,
			String system_generated) {
		super();
		this.transId = transId;
		this.pet_eventcode = pet_eventcode;
		this.ptr_trandesc = ptr_trandesc;
		this.system_generated = system_generated;
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

	@Entity
	@Table(schema = "loan", name = "pr_gn_lt_loantransactiontype")
	public static class LoanTransaction extends TransactionType {
		public LoanTransaction() {

		}
	}

	@Entity
	@Table(schema = "deposit", name = "pr_gn_dt_deposittransactiontype")
	public static class DepositTransaction extends TransactionType {
		public DepositTransaction() {

		}
	}

	@Entity
	@Table(schema = "generalledger", name = "pr_gn_gt_gltransactiontype")
	public static class GeneralledgerTransaction extends TransactionType {
		public GeneralledgerTransaction() {

		}
	}

}
