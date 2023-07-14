package com.mfsys.DatabaseMonitoring.Entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;
@Embeddable
public class TransactionId implements Serializable {
	
		private static final long serialVersionUID = 1L;
	    private int por_orgacode;
	    private int ptr_trancode;

	    // default constructor
	    
	    public TransactionId(int por_orgacode, int ptr_trancode) {
	        this.por_orgacode = por_orgacode;
	        this.ptr_trancode = ptr_trancode;
	    }

		public int getPor_orgacode() {
			return por_orgacode;
		}

		public void setPor_orgacode(int por_orgacode) {
			this.por_orgacode = por_orgacode;
		}

		public int getPtr_trancode() {
			return ptr_trancode;
		}

		public void setPtr_trancode(int ptr_trancode) {
			this.ptr_trancode = ptr_trancode;
		}

		@Override
		public int hashCode() {
			return Objects.hash(por_orgacode, ptr_trancode);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TransactionId other = (TransactionId) obj;
			return por_orgacode == other.por_orgacode && ptr_trancode == other.ptr_trancode;
		}
	    
	    // getters, equals() and hashCode() methods
	}
