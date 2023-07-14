package com.mfsys.DatabaseMonitoring.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mfsys.DatabaseMonitoring.Entity.EventEntity;

@Repository
public interface EventRepository<T extends EventEntity, ID> extends JpaRepository<T, ID> {

}

interface LoanEventRepository extends EventRepository<EventEntity.LoanEvent, Integer> {

}

interface DepositEventRepository extends EventRepository<EventEntity.DepositEvent, Integer> {

}

interface GeneralledgerEventRepository extends EventRepository<EventEntity.GeneralledgerEvent, Integer> {

}
