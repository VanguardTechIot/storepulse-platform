package com.vanguardtech.storepulse.platform.dashboardanalytics.interfaces.rest.controllers;

import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.model.queries.GetDashboardSummaryQuery;
import com.vanguardtech.storepulse.platform.dashboardanalytics.domain.services.DashboardQueryService;
import com.vanguardtech.storepulse.platform.dashboardanalytics.interfaces.rest.resources.DashboardSummaryResource;
import com.vanguardtech.storepulse.platform.dashboardanalytics.interfaces.rest.transform.DashboardSummaryResourceFromSummaryAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard")
public class DashboardController {

    private final DashboardQueryService dashboardQueryService;

    @Operation(summary = "Get the summary of alerts and consumption of the current month")
    @GetMapping("/summary")
    public DashboardSummaryResource getSummary() {
        return DashboardSummaryResourceFromSummaryAssembler.toResourceFromSummary(
                dashboardQueryService.handle(new GetDashboardSummaryQuery()));
    }
}