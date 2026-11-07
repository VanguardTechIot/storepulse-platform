package com.vanguardtech.storepulse.platform.propertycommunication.domain.services;

import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.aggregates.CommunicationLog;
import com.vanguardtech.storepulse.platform.propertycommunication.domain.model.queries.GetCommunicationLogsByAdministratorIdQuery;
import java.util.List;

public interface CommunicationLogQueryService {
    List<CommunicationLog> handle(GetCommunicationLogsByAdministratorIdQuery query);
}
