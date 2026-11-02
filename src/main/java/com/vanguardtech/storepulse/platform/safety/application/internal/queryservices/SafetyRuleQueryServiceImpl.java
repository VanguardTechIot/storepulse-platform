package com.vanguardtech.storepulse.platform.safety.application.internal.queryservices;

import com.vanguardtech.storepulse.platform.safety.domain.model.aggregates.SafetyRule;
import com.vanguardtech.storepulse.platform.safety.domain.model.queries.GetAllSafetyRulesQuery;
import com.vanguardtech.storepulse.platform.safety.domain.model.queries.GetSafetyRuleByIdQuery;
import com.vanguardtech.storepulse.platform.safety.domain.services.SafetyRuleQueryService;
import com.vanguardtech.storepulse.platform.safety.infrastructure.persistence.jpa.repositories.SafetyRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SafetyRuleQueryServiceImpl implements SafetyRuleQueryService {

    private final SafetyRuleRepository safetyRuleRepository;

    @Override
    public List<SafetyRule> handle(GetAllSafetyRulesQuery query) {
        return safetyRuleRepository.findAll();
    }

    @Override
    public Optional<SafetyRule> handle(GetSafetyRuleByIdQuery query) {
        return safetyRuleRepository.findById(query.safetyRuleId());
    }
}
