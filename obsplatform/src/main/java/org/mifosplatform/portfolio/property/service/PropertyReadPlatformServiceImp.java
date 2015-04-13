package org.mifosplatform.portfolio.property.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.domain.JdbcSupport;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.mifosplatform.portfolio.property.data.PropertyDefinationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class PropertyReadPlatformServiceImp implements PropertyReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final PaginationHelper<PropertyDefinationData> paginationHelper = new PaginationHelper<PropertyDefinationData>();

	@Autowired
	public PropertyReadPlatformServiceImp(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public Page<PropertyDefinationData> retrieveAllProperties(SearchSqlQuery searchPropertyDetails) {

		try {
			context.authenticatedUser();
			final PropertyMapper mapper = new PropertyMapper();	
			StringBuilder sqlBuilder = new StringBuilder(200);
			sqlBuilder.append("select ");
			sqlBuilder.append( mapper.schema() +" where pd.is_deleted='N' ");
			String sqlSearch = searchPropertyDetails.getSqlSearch();
			String extraCriteria = "";
			if(sqlSearch != null) {
			    	sqlSearch=sqlSearch.trim();
			    	extraCriteria = " and (pd.property_code like '%"+sqlSearch+"%' OR" 
			    			+ " pd.precinct like '%"+sqlSearch+"%' OR"
			    			+ " pd.status like '%"+sqlSearch+"%' )";
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

	private static final class PropertyMapper implements RowMapper<PropertyDefinationData> {

		public String schema() {
			return  " pd.id as Id,pd.property_type_id as propertyTypeId,c.code_value as propertyType,pd.property_code as propertyCode,unit_code as unitCode,pd.floor as floor," +
					 " pd.building_code as buildingCode, pd.parcel as parcel,pd.street as street,pd.precinct as precinct,pd.po_box as poBox," +
					 " pd.state as state, pd.country as country, pd.status as status, ifnull(pd.client_id,'VACANT') AS clientId " +
					 " from b_property_defination pd left join m_code_value c on c.id=pd.property_type_id";

		}

		@Override
		public PropertyDefinationData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			final Long Id =rs.getLong("Id");
			final Long propertyTypeId = rs.getLong("propertyTypeId");
			final String propertyType = rs.getString("propertyType");
			final String propertyCode = rs.getString("propertyCode");
			final String unitCode = rs.getString("unitCode");
			final Long floor = rs.getLong("floor");
			final String buildingCode = rs.getString("buildingCode");
			final String parcel = rs.getString("parcel");
			final String precinct = rs.getString("precinct");
			final String street = rs.getString("street");
			final String poBox = rs.getString("poBox");
			final String state = rs.getString("state");
			final String country = rs.getString("country");
			final String status = rs.getString("status");
			final String clientId = rs.getString("clientId");
			
			return new PropertyDefinationData(Id,propertyTypeId,propertyType,propertyCode,unitCode,floor,buildingCode,parcel,
					precinct,street,poBox,state,country,status,clientId);
			
		}


	}

	@Override
	public List<PropertyDefinationData> retrieveAllPropertiesForSearch(final String propertyCode) {
		
            try{
			context.authenticatedUser();
			final PropertyMapper mapper = new PropertyMapper();
			final String sql = "SELECT " + mapper.schema() + " WHERE pd.client_id IS NULL AND pd.status='VACANT' AND (pd.property_code LIKE '%"+propertyCode+"%') ORDER BY pd.id  LIMIT 15" ;
			return this.jdbcTemplate.query(sql, mapper, new Object[] {});
            }catch (EmptyResultDataAccessException accessException) {
    			return null;
    		}
            
	}

	@Override
	public PropertyDefinationData retrievePropertyDetails(final Long propertyId) {
		
			try {
				context.authenticatedUser();
				final PropertyMapper mapper = new PropertyMapper();
				final String sql = "select " + mapper.schema() + " where pd.id = ? and pd.is_deleted='N'";
				return this.jdbcTemplate.queryForObject(sql, mapper, new Object[] {propertyId});
			} catch (EmptyResultDataAccessException accessException) {
				return null;
			}
		}

	@Override
	public Page<PropertyDefinationData> retrievePropertyHistory(SearchSqlQuery searchPropertyDetails) {
		
		try {
			context.authenticatedUser();
			final PropertyHistoryMapper mapper = new PropertyHistoryMapper();	
			StringBuilder sqlBuilder = new StringBuilder(200);
			sqlBuilder.append("select ");
			sqlBuilder.append( mapper.schema());
			String sqlSearch = searchPropertyDetails.getSqlSearch();
			String extraCriteria = "";
			if(sqlSearch != null) {
			    	sqlSearch=sqlSearch.trim();
			    	extraCriteria = " and (ph.property_code like '%"+sqlSearch+"%')";
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
	
	
	private static final class PropertyHistoryMapper implements RowMapper<PropertyDefinationData> {

		public String schema() {
			return  " pd.id as Id,ph.ref_id as refId,ph.transaction_date as transactionDate,ph.property_code as propertyCode ,ph.ref_desc as description," +
                     " ph.client_id as clientId,pd.status as status from b_property_history ph,b_property_defination pd where ph.ref_id =pd.id  ";

		}

		@Override
		public PropertyDefinationData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			final Long Id =rs.getLong("Id");
			final Long refId = rs.getLong("refId");
			final String description = rs.getString("description");
			final String propertyCode = rs.getString("propertyCode");
			final String status = rs.getString("status");
			final String clientId = rs.getString("clientId");
			final LocalDate transactionDate=JdbcSupport.getLocalDate(rs,"transactionDate");
			
			return new PropertyDefinationData(Id,refId,description,propertyCode,status,clientId,transactionDate);
			
		}


	}
	}

