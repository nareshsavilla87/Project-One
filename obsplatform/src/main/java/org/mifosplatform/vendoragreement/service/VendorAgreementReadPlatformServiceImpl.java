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
	public List<VendorAgreementData> retrieveAllVendorAndLoyalties() {
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
			return " bvm.id as id, bvm.vendor_code as vendorCode, bvm.vendor_description as vendorDescription, "+
					"bvm.vendor_emailid as vendorEmailId, bvm.vendor_contact_name as contactName, bvm.vendor_mobileno as vendormobileNo, "+
					"bvm.vendor_telephoneno as vendorTelephoneNo, bvm.vendor_address as vendorAddress, bvm.agreement_status as agreementStatus, "+
					"bvm.vendor_country as vendorCountry, bvm.vendor_currency as vendorCurrency, bvm.agreement_startdate as agreementStartDate, "+
					"bvm.agreement_enddate as agreementEndDate, bvm.content_type as contentType from b_vendor_management bvm ";

		}

		@Override
		public VendorAgreementData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {
			
			Long id = rs.getLong("id");
			String vendorCode = rs.getString("vendorCode");
			String vendorDescription = rs.getString("vendorDescription");
			String vendorEmailId = rs.getString("vendorEmailId");
			String contactName = rs.getString("contactName");
			String vendormobileNo = rs.getString("vendormobileNo");
			String vendorTelephoneNo = rs.getString("vendorTelephoneNo");
			String vendorAddress = rs.getString("vendorAddress");
			String agreementStatus = rs.getString("agreementStatus");
			String vendorCountry = rs.getString("vendorCountry");
			String vendorCurrency = rs.getString("vendorCurrency");
			Date agreementStartDate = rs.getDate("agreementStartDate");
			Date agreementEndDate = rs.getDate("agreementEndDate");
			String contentType = rs.getString("contentType");
			
			return new VendorAgreementData(id, vendorCode, vendorDescription, vendorEmailId, contactName, vendormobileNo, vendorTelephoneNo, 
					vendorAddress, agreementStatus, vendorCountry, vendorCurrency, agreementStartDate, agreementEndDate, contentType);
		}
	}
	
	@Override
	public List<VendorAgreementData> retrieveVendor(Long vendorId) {
		try {
			context.authenticatedUser();
			String sql;
			RetrieveMapper mapper = new RetrieveMapper();
			sql = "SELECT  " + mapper.schema() +" where bvm.id = "+vendorId;

			return this.jdbcTemplate.query(sql, mapper, new Object[] { });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public List<VendorAgreementData> retrieveVendorDetails(Long vendorId) {
		try {
			context.authenticatedUser();
			String sql;
			RetrieveVendorDetailMapper mapper = new RetrieveVendorDetailMapper();
			sql = "SELECT  " + mapper.schema() +" where bvmd.id = "+vendorId;

			return this.jdbcTemplate.query(sql, mapper, new Object[] { });
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	private static final class RetrieveVendorDetailMapper implements RowMapper<VendorAgreementData> {

		public String schema() {
			return " bvmd.id as id, bvmd.vendor_id as vendorId, bvmd.content_code as contentCode, "+
					"bvmd.loyalty_type as loyaltyType, bvmd.loyalty_share as loyaltyShare, bvmd.price_region as priceRegion, "+
					"bvmd.content_cost as contentCost from b_vendor_management_detail bvmd ";

		}

		@Override
		public VendorAgreementData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {
			
			Long id = rs.getLong("id");
			Long vendorId = rs.getLong("vendorId");
			String contentCode = rs.getString("contentCode");
			String loyaltyType = rs.getString("loyaltyType");
			BigDecimal loyaltyShare = rs.getBigDecimal("loyaltyShare");
			Long priceRegion = rs.getLong("priceRegion");
			BigDecimal contentCost = rs.getBigDecimal("contentCost");
			
			return new VendorAgreementData(id, vendorId, contentCode, loyaltyType, loyaltyShare, priceRegion, contentCost);
		}
	}
	

}
