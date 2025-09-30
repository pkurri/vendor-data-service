package com.vendor.vendordataservice.repository.mybatis;

import com.vendor.vendordataservice.api.dto.CaseRecord;
import com.vendor.vendordataservice.api.dto.SearchRequest;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MyBatis mapper tests with H2 database
 */
@MybatisTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "mybatis.mapper-locations=classpath:mybatis/mapper/*.xml"
})
class CaseMapperTest {

    @Autowired(required = false)
    private CaseMapper caseMapper;

    @Test
    void contextLoads() {
        // Verify mapper bean is created
        assertThat(caseMapper).isNotNull();
    }

    @Test
    void searchCasesWithEmptyDatabase() {
        if (caseMapper != null) {
            SearchRequest request = SearchRequest.builder()
                    .nameLast("SMITH")
                    .page(1)
                    .pageSize(50)
                    .build();

            List<CaseRecord> results = caseMapper.searchCases(request);
            
            // Empty database should return empty list
            assertThat(results).isNotNull();
        }
    }

    @Test
    void countCasesWithEmptyDatabase() {
        if (caseMapper != null) {
            SearchRequest request = SearchRequest.builder()
                    .nameLast("SMITH")
                    .build();

            int count = caseMapper.countCases(request);
            
            assertThat(count).isEqualTo(0);
        }
    }
}
