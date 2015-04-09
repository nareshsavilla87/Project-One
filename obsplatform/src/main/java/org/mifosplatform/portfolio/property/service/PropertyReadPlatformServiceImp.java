package org.mifosplatform.portfolio.property.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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

	@Autowired
	public PropertyReadPlatformServiceImp(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<PropertyDefinationData> retrieveAllProperties() {

		try {
			context.authenticatedUser();
			final PropertyMapper mapper = new PropertyMapper();
			final String sql = "select " + mapper.schema();
			return this.jdbcTemplate.query(sql, mapper, new Object[] {});
		} catch (EmptyResultDataAccessException accessException) {
			return null;
		}

	}

	private static final class PropertyMapper implements RowMapper<PropertyDefinationData> {

		public String schema() {
			return  " pd.id as Id,pd.property_type_id as propertyTypeId,c.code_value as propertyType,pd.property_code as propertyCode,unit_code as unitCode,pd.floor as floor," +
					 " pd.building_code as buildingCode, pd.parcel as parcel,pd.street as street,pd.precinct as precinct,pd.po_box as poBox," +
					 " pd.state as state, pd.country as country, pd.status as status, pd.client_id as clientId " +
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
			final Long clientId = rs.getLong("clientId");
			
			return new PropertyDefinationData(Id,propertyTypeId,propertyType,propertyCode,unitCode,floor,buildingCode,parcel,
					precinct,street,poBox,state,country,status,clientId);
			
		}

	}
}
