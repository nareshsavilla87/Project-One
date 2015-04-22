package org.mifosplatform.billing.servicetransfer.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.organisation.feemaster.data.FeeMasterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class ServiceTransferReadPlatformServiceImpl implements ServiceTransferReadPlatformService {

	
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	
	@Autowired
	public ServiceTransferReadPlatformServiceImpl(final PlatformSecurityContext context,final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	
	@Override
	public List<FeeMasterData> retrieveSingleFeeDetails(Long clientId) {
		try {
			context.authenticatedUser();
			FeeMasterDataMapper mapper = new FeeMasterDataMapper();
			String sql ;
				sql = "select " + mapper.schemaWithClientId(clientId)+" where  fm.is_deleted='N'  group by fm.id"; 
		
			return this.jdbcTemplate.query(sql, mapper, new Object[] {});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	private static final class FeeMasterDataMapper implements RowMapper<FeeMasterData> {
		
		public String schemaWithClientId(final Long clientId) {
			
			return "fm.id AS id,fm.fee_code AS feeCode,fm.fee_description AS feeDescription, fm.transaction_type AS transactionType,"
				+"fm.charge_code AS chargeCode,ifnull(round(fd.amount , 2),fm.default_fee_amount ) as amount FROM b_fee_master fm "
				+"left join b_client_address ca on ca.client_id = "+clientId+" "
                +"left join b_state s on s.state_name = ca.state " 
                +"left join b_priceregion_detail pd on (pd.state_id = s.id or (pd.state_id = 0 and pd.country_id = s.parent_code ) ) " 
                +"left join b_priceregion_master prm ON prm.id = pd.priceregion_id "
                +"left join b_fee_detail fd on (fd.fee_id = fm.id and fd.region_id = prm.id and fd.is_deleted='N' ) ";
		}

		@Override
		public FeeMasterData mapRow(ResultSet rs, int rowNum)
				throws SQLException {
		
			final Long id = rs.getLong("id");
			final String feeCode = rs.getString("feeCode");
			final String feeDescription = rs.getString("feeDescription");
			final String transactionType = rs.getString("transactionType");
			final String chargeCode = rs.getString("chargeCode");
			final BigDecimal amount = rs.getBigDecimal("amount");
			return new FeeMasterData(id,feeCode,feeDescription,transactionType,chargeCode,amount);
		
		
		}
}

}
