/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.vendoragreement.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.vendoragreement.data.VendorAgreementData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class VendorAgreementReadPlatformServiceImpl implements VendorAgreementReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PlatformSecurityContext context;

    @Autowired
    public VendorAgreementReadPlatformServiceImpl(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource) {
        this.context = context;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

	@Override
	public List<VendorAgreementData> retrieveAllVendorAgreements() {
		try {
			context.authenticatedUser();
			String sql;
			RetrieveMapper mapper = new RetrieveMapper();
			sql = "SELECT  " + mapper.schema();

			return this.jdbcTemplate.query(sql, mapper, new Object[] { });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	private static final class RetrieveMapper implements RowMapper<VendorAgreementData> {

		public String schema() {
			return " bva.id as id, bva.vendor_id as vendorId, bva.vendor_agmt_status as agreementStatus, "+
					"bva.vendor_agmt_startdate as startDate, bva.vendor_agmt_enddate as endDate, bva.content_type as contentType,"+
					"bva.vendor_agmt_document as documentLocation from b_vendor_agreement bva  ";

		}

		@Override
		public VendorAgreementData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {
			
			Long id = rs.getLong("id");
			Long vendorId = rs.getLong("vendorId");
			String agreementStatus = rs.getString("agreementStatus");
			Date agreementStartDate = rs.getDate("startDate");
			Date agreementEndDate = rs.getDate("endDate");
			String contentType = rs.getString("contentType");
			String documentLocation = rs.getString("documentLocation");
			
			return new VendorAgreementData(id, vendorId, agreementStatus, agreementStartDate, agreementEndDate, contentType, documentLocation);
		}
	}
	
	@Override
	public VendorAgreementData retrieveVendorAgreement(Long vendorAgreementId) {
		try {
			context.authenticatedUser();
			String sql;
			RetrieveMapper mapper = new RetrieveMapper();
			sql = "SELECT  " + mapper.schema() +" where bva.id = "+vendorAgreementId;

			return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] { });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<VendorAgreementData> retrieveVendorAgreementDetails(Long vendorAgreementId) {
		try {
			context.authenticatedUser();
			String sql;
			RetrieveVendorDetailMapper mapper = new RetrieveVendorDetailMapper();
			sql = "SELECT  " + mapper.schema() +" where bvad.vendor_agmt_id = "+vendorAgreementId+" and bvad.is_deleted = 'N'";

			return this.jdbcTemplate.query(sql, mapper, new Object[] { });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	private static final class RetrieveVendorDetailMapper implements RowMapper<VendorAgreementData> {

		public String schema() {
			return " bvad.id as id, bvad.vendor_agmt_id as vendorAgreementId, bvad.content_code as contentCode,"+
					"bvad.loyalty_type as loyaltyType, bvad.loyalty_share as loyaltyShare, bvad.price_region as priceRegion, "+
					"bvad.content_cost as contentCost from b_vendor_agmt_detail bvad ";

		}

		@Override
		public VendorAgreementData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {
			
			Long id = rs.getLong("id");
			Long vendorAgreementId = rs.getLong("vendorAgreementId");
			Long contentCode = rs.getLong("contentCode");
			String loyaltyType = rs.getString("loyaltyType");
			BigDecimal loyaltyShare = rs.getBigDecimal("loyaltyShare");
			Long priceRegion = rs.getLong("priceRegion");
			BigDecimal contentCost = rs.getBigDecimal("contentCost");
			
			return new VendorAgreementData(id, vendorAgreementId, contentCode, loyaltyType, loyaltyShare, priceRegion, contentCost);
			
		}
	}

	@Override
	public List<VendorAgreementData> retrieveRespectiveAgreementData(Long vendorId) {
		try {
			context.authenticatedUser();
			String sql;
			RetrieveMapper mapper = new RetrieveMapper();
			sql = "SELECT  " + mapper.schema()+" where bva.vendor_id = ?";

			return this.jdbcTemplate.query(sql, mapper, new Object[] { vendorId });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	

}
