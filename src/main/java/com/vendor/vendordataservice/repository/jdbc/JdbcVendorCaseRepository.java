package com.vendor.vendordataservice.repository.jdbc;

import com.vendor.vendordataservice.repository.VendorCaseRepository;
import com.vendor.vendordataservice.repository.model.VendorCaseRow;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JdbcVendorCaseRepository implements VendorCaseRepository {

    private final NamedParameterJdbcTemplate jdbc;

    public JdbcVendorCaseRepository(NamedParameterJdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public List<VendorCaseRow> search(String ssnLast4, LocalDate dob, String firstName, String middleName, String lastName) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ")
           .append(" first_name, middle_name, last_name, suffix, dob, sex, race, dl_state, dl_number, ")
           .append(" county, state, case_number, charge, charge_type, disposition_type, disposition_date, file_date, offense_date ")
           .append(" FROM vendor_cases WHERE 1=1 ");

        MapSqlParameterSource params = new MapSqlParameterSource();

        if (ssnLast4 != null && !ssnLast4.isBlank()) {
            sql.append(" AND ssn_last4 = :ssnLast4 ");
            params.addValue("ssnLast4", ssnLast4);
        }
        if (dob != null) {
            sql.append(" AND dob = :dob ");
            params.addValue("dob", dob);
        }
        if (firstName != null && !firstName.isBlank()) {
            sql.append(" AND UPPER(first_name) LIKE UPPER(:firstName) ");
            params.addValue("firstName", firstName + "%");
        }
        if (middleName != null && !middleName.isBlank()) {
            sql.append(" AND UPPER(middle_name) LIKE UPPER(:middleName) ");
            params.addValue("middleName", middleName + "%");
        }
        if (lastName != null && !lastName.isBlank()) {
            sql.append(" AND UPPER(last_name) LIKE UPPER(:lastName) ");
            params.addValue("lastName", lastName + "%");
        }

        // Optional: sensible limit to avoid unbounded scans
        sql.append(" ORDER BY last_name, first_name, dob, case_number ");

        List<VendorCaseRow> rows = jdbc.query(sql.toString(), params, new VendorCaseRowMapper());
        return rows != null ? rows : new ArrayList<>();
    }

    static class VendorCaseRowMapper implements RowMapper<VendorCaseRow> {
        @Override
        public VendorCaseRow mapRow(ResultSet rs, int rowNum) throws SQLException {
            VendorCaseRow r = new VendorCaseRow();
            r.setFirstName(rs.getString("first_name"));
            r.setMiddleName(rs.getString("middle_name"));
            r.setLastName(rs.getString("last_name"));
            r.setSuffix(rs.getString("suffix"));
            r.setDob(rs.getObject("dob", LocalDate.class));
            r.setSex(rs.getString("sex"));
            r.setRace(rs.getString("race"));
            r.setDlState(rs.getString("dl_state"));
            r.setDlNumber(rs.getString("dl_number"));

            r.setCounty(rs.getString("county"));
            r.setState(rs.getString("state"));
            r.setCaseNumber(rs.getString("case_number"));
            r.setCharge(rs.getString("charge"));
            r.setChargeType(rs.getString("charge_type"));
            r.setDispositionType(rs.getString("disposition_type"));
            r.setDispositionDate(rs.getObject("disposition_date", LocalDate.class));
            r.setFileDate(rs.getObject("file_date", LocalDate.class));
            r.setOffenseDate(rs.getObject("offense_date", LocalDate.class));
            return r;
        }
    }
}
