package com.vanguardtech.storepulse.platform;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import java.util.List;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Keeps the layers and the context boundaries of subscriptions-payments: the build fails when one
 * of them is broken.
 */
@AnalyzeClasses(packages = "com.vanguardtech.storepulse.platform", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    private static final List<String> CONTEXTS = List.of("assets", "dashboardanalytics", "iam",
            "monitoring", "profilespreferences", "propertycommunication", "propertymanagement", "safety",
            "subscriptionspayments", "utilitybilling");

    @ArchTest
    static final ArchRule domainDoesNotDependOnOuterLayers = noClasses()
            .that().resideInAPackage("..subscriptionspayments.domain..")
            .should().dependOnClassesThat().resideInAnyPackage("..application..", "..infrastructure..", "..interfaces..");

    @ArchTest
    static final ArchRule applicationUsesDomainContractsInsteadOfInfrastructure = noClasses()
            .that().resideInAPackage("..subscriptionspayments.application..")
            .should().dependOnClassesThat().resideInAnyPackage("..infrastructure..");

    @ArchTest
    static final ArchRule controllersDoNotUseRepositories = noClasses()
            .that().resideInAPackage("..subscriptionspayments.interfaces..")
            .should().dependOnClassesThat().resideInAnyPackage("..repositories..");

    @ArchTest
    static final ArchRule reachesOtherContextsOnlyThroughTheirFacades = noClasses()
            .that().resideInAPackage("..subscriptionspayments..")
            .should().dependOnClassesThat().resideInAnyPackage(internalPackagesOfContextsOtherThan("subscriptionspayments"));

    /** Everything of the other contexts except their interfaces.acl facades. */
    private static String[] internalPackagesOfContextsOtherThan(String context) {
        return CONTEXTS.stream()
                .filter(other -> !other.equals(context))
                .flatMap(other -> List.of("domain", "application", "infrastructure", "interfaces.rest").stream()
                        .map(layer -> "..platform." + other + "." + layer + ".."))
                .toArray(String[]::new);
    }
}