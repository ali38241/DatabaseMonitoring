package com.mfsys.DatabaseMonitoring.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.mfsys.DatabaseMonitoring.Entity.TransactionType;

public interface TransactionRepository<T extends TransactionType, ID> extends JpaRepository<T, ID> {

}
