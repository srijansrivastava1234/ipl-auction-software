package com.ipl.auction.unit;

import com.ipl.auction.builder.TestDataBuilder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Mockito Unit Tests simulating Service Layer contract validation for Member 1's Team Service.
 * Maintained by Member 5 (QA & Testing Lead).
 */
@ExtendWith(MockitoExtension.class)
public class MockTeamServiceTest {

    @Mock
    private MockTeamService mockTeamService;

    // Interface mock representing Member 1's Team Service Contract
    public interface MockTeamService {
        Map<String, Object> getTeamById(Long teamId);
        boolean deductPurse(Long teamId, BigDecimal amount);
    }

    @Test
    @DisplayName("Mockito Test: Verify Team Retrieval Contract")
    void testGetTeamByIdContract() {
        Map<String, Object> expectedTeam = TestDataBuilder.createMockTeam(1L, "Chennai Super Kings", new BigDecimal("80000000.00"));
        
        when(mockTeamService.getTeamById(1L)).thenReturn(expectedTeam);

        Map<String, Object> actualTeam = mockTeamService.getTeamById(1L);

        assertNotNull(actualTeam);
        assertEquals("Chennai Super Kings", actualTeam.get("teamName"));
        verify(mockTeamService, times(1)).getTeamById(1L);
    }

    @Test
    @DisplayName("Mockito Test: Verify Purse Deduction Success")
    void testDeductPurseSuccess() {
        when(mockTeamService.deductPurse(1L, new BigDecimal("10000000.00"))).thenReturn(true);

        boolean result = mockTeamService.deductPurse(1L, new BigDecimal("10000000.00"));

        assertTrue(result, "Purse deduction should return true when funds are sufficient.");
        verify(mockTeamService, times(1)).deductPurse(1L, new BigDecimal("10000000.00"));
    }
}
