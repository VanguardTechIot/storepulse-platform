package com.vanguardtech.storepulse.platform.propertycommunication.domain.services;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.TenantAssignment;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetActiveAssignmentByTenantIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetActiveAssignmentByUnitIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetAssignmentByIdQuery;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetAssignmentsByAdministratorIdQuery;
import java.util.List;
import java.util.Optional;

public interface TenantAssignmentQueryService {
    List<TenantAssignment> handle(GetAssignmentsByAdministratorIdQuery query);
    Optional<TenantAssignment> handle(GetAssignmentByIdQuery query);
    Optional<TenantAssignment> handle(GetActiveAssignmentByUnitIdQuery query);
    Optional<TenantAssignment> handle(GetActiveAssignmentByTenantIdQuery query);
}
