package org.mifosplatform.portfolio.propertycode.master.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.propertycode.master.data.PropertyCodeMasterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class PropertyCodeMasterReadPlatformServiceImpl implements PropertyCodeMasterReadPlatformService {
	
	private final PlatformSecurityContext context;
	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<PropertyCodeMasterData> paginationHelper = new PaginationHelper<PropertyCodeMasterData>();

	@Autowired
	public PropertyCodeMasterReadPlatformServiceImpl(final PlatformSecurityContext context,
													final TenantAwareRoutingDataSource dataSource) {
		this.context=context;
		jdbcTemplate=new JdbcTemplate(dataSource);
	}

	@Override
	public Page<PropertyCodeMasterData> retrieveAllPropertyCodeMasterData(SearchSqlQuery searchPropertyDetails) {
		try {
			context.authenticatedUser();
			final PropertyCodeMasterMapper mapper = new PropertyCodeMasterMapper();	
			StringBuilder sqlBuilder = new StringBuilder(200);
			sqlBuilder.append("select ");
			sqlBuilder.append( mapper.schema());
			String sqlSearch = searchPropertyDetails.getSqlSearch();
			String extraCriteria = "";
			if(sqlSearch != null) {
			    	sqlSearch=sqlSearch.trim();
			    	extraCriteria = " where (pm.property_code_type like '%"+sqlSearch+"%' OR" 
			    			+ " pm.code like '%"+sqlSearch+"%' )";
			    }
			
			sqlBuilder.append(extraCriteria);
			
		   if (searchPropertyDetails.isLimited()) {
		            sqlBuilder.append(" limit ").append(searchPropertyDetails.getLimit());
		        }

		   if (searchPropertyDetails.isOffset()) {
		            sqlBuilder.append(" offset ").append(searchPropertyDetails.getOffset());
		        }

	    	return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(), new Object[] {}, mapper);
			    
		} catch (EmptyResultDataAccessException accessException) {
			return null;
		}
	}
	

	private static final class PropertyCodeMasterMapper implements RowMapper<PropertyCodeMasterData>{

		public String schema(){
			return " pm.id as id,pm.property_code_type as  propertyCodeType,pm.code as code,pm.description as description,pm.reference_value as referenceValue from b_property_master pm ";

		}

		@Override
		public PropertyCodeMasterData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {

			final Long id = rs.getLong("id");
			final String propertyCodeType = rs.getString("propertyCodeType");
			final String code = rs.getString("code");
			final String description = rs.getString("description");
			final String referenceValue = rs.getString("referenceValue");

			return new PropertyCodeMasterData(id,propertyCodeType,code,description,referenceValue);
		}
	}


	@Override
	public List<PropertyCodeMasterData> retrievPropertyType(final String propertyType) {
		
		try {
			context.authenticatedUser();
			final PropertyCodeMasterMapper mapper = new PropertyCodeMasterMapper();
			final String sql = "select "+ mapper.schema()+ " where pm.property_code_type like '%"+propertyType+"%' order by pm.id LIMIT 15";
			return this.jdbcTemplate.query(sql, mapper, new Object[] {});
		} catch (final EmptyResultDataAccessException e) {
			return null;
		}
	}
}
