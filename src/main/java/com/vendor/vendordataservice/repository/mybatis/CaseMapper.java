package com.vendor.vendordataservice.repository.mybatis;

import com.vendor.vendordataservice.api.dto.CaseRecord;
import com.vendor.vendordataservice.api.dto.SearchRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * MyBatis mapper for court case data queries
 */
@Mapper
public interface CaseMapper {
    
    /**
     * Search for cases based on criteria
     * @param request Search criteria
     * @return List of case records
     */
    List<CaseRecord> searchCases(@Param("request") SearchRequest request);
    
    /**
     * Get case by ID with all nested objects
     * @param caseId Case identifier
     * @param includeCharges Include charges flag
     * @param includeDockets Include dockets flag
     * @param includeEvents Include events flag
     * @param includeDefendants Include defendants flag
     * @return Case record with nested objects
     */
    CaseRecord getCaseById(
        @Param("caseId") String caseId,
        @Param("includeCharges") boolean includeCharges,
        @Param("includeDockets") boolean includeDockets,
        @Param("includeEvents") boolean includeEvents,
        @Param("includeDefendants") boolean includeDefendants
    );
    
    /**
     * Count total cases matching criteria
     * @param request Search criteria
     * @return Total count
     */
    int countCases(@Param("request") SearchRequest request);
}
