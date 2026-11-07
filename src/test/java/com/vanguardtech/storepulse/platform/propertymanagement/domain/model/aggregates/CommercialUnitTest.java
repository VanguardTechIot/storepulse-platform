package com.vanguardtech.storepulse.platform.propertymanagement.domain.model.aggregates;

import com.vanguardtech.storepulse.platform.propertymanagement.domain.exceptions.PropertyManagementError;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects.UnitStatus;
import com.vanguardtech.storepulse.platform.propertymanagement.domain.model.valueobjects.UnitType;
import com.vanguardtech.storepulse.platform.shared.domain.exceptions.BusinessRuleException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import static org.junit.jupiter.api.Assertions.*;

class CommercialUnitTest {

    private static CommercialUnit store(String code) {
        return new CommercialUnit(1L, UnitType.STORE, code, "1", 20.456, "  ");
    }

    private static PropertyManagementError errorOf(Executable action) {
        var exception = assertThrows(BusinessRuleException.class, action);
        return PropertyManagementError.valueOf(exception.getMessage().toUpperCase());
    }

    @Test
    void registersAnAvailableStoreWithNormalizedData() {
        var unit = new CommercialUnit(1L, UnitType.STORE, " a-10 ", "1", 20.456, "  ");

        assertEquals("A-10", unit.getCode());
        assertEquals(20.46, unit.getAreaSquareMeters());
        assertNull(unit.getBusinessName());
        assertEquals(UnitStatus.AVAILABLE, unit.getStatus());
        assertTrue(unit.isActive());
        assertTrue(unit.canReceiveTenant());
    }

    @Test
    void registersACommonAreaWithoutBusinessNameNorTenant() {
        var area = new CommercialUnit(1L, UnitType.COMMON_AREA, "Patio de comidas", "2", 120.0, "Ignored");

        assertEquals("Patio de comidas", area.getCode());
        assertNull(area.getBusinessName());
        assertFalse(area.canReceiveTenant());
        assertEquals(PropertyManagementError.COMMON_AREA_WITHOUT_TENANT, errorOf(area::assignTenant));
    }

    @Test
    void rejectsInvalidData() {
        assertEquals(PropertyManagementError.INVALID_UNIT_CODE, errorOf(() -> store(" ")));
        assertEquals(PropertyManagementError.INVALID_UNIT_CODE, errorOf(() -> store("A-00000000001")));
        assertEquals(PropertyManagementError.INVALID_FLOOR,
                errorOf(() -> new CommercialUnit(1L, UnitType.STORE, "A-10", "", 20.0, null)));
        assertEquals(PropertyManagementError.INVALID_AREA,
                errorOf(() -> new CommercialUnit(1L, UnitType.STORE, "A-10", "1", 0.0, null)));
        assertEquals(PropertyManagementError.INVALID_BUSINESS_NAME,
                errorOf(() -> new CommercialUnit(1L, UnitType.STORE, "A-10", "1", 20.0, "x".repeat(81))));
    }

    @Test
    void comparesCodesIgnoringCaseAndSpaces() {
        assertTrue(store("A-03").hasCode(" a-03 "));
        assertFalse(store("A-03").hasCode("A-04"));
    }

    @Test
    void changesOccupancyAndMaintenance() {
        var unit = store("A-10");
        unit.assignTenant();
        assertEquals(UnitStatus.OCCUPIED, unit.getStatus());
        assertEquals(PropertyManagementError.UNIT_NOT_AVAILABLE, errorOf(unit::assignTenant));
        assertEquals(PropertyManagementError.UNIT_OCCUPIED, errorOf(unit::markUnderMaintenance));

        unit.vacate();
        unit.markUnderMaintenance();
        assertEquals(UnitStatus.MAINTENANCE, unit.getStatus());
        unit.changeStatus(UnitStatus.AVAILABLE);
        assertEquals(UnitStatus.AVAILABLE, unit.getStatus());
    }

    @Test
    void isRemovedLogicallyOnlyWhenNobodyOccupiesIt() {
        var occupied = store("A-03");
        occupied.assignTenant();
        assertEquals(PropertyManagementError.UNIT_OCCUPIED, errorOf(occupied::remove));

        var free = store("A-10");
        free.remove();
        assertFalse(free.isActive());
        assertFalse(free.canReceiveTenant());
    }

    @Test
    void keepsCurrentValuesForNullFieldsOnUpdate() {
        var unit = store("A-10");
        unit.update(null, "2", null, "Calzados Roma");

        assertEquals("A-10", unit.getCode());
        assertEquals("2", unit.getFloor());
        assertEquals(20.46, unit.getAreaSquareMeters());
        assertEquals("Calzados Roma", unit.getBusinessName());
    }
}
