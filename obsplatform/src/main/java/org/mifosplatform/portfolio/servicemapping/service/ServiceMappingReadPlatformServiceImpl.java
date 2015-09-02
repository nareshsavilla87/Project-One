package org.mifosplatform.portfolio.servicemapping.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.mifosplatform.crm.clientprospect.service.SearchSqlQuery;
import org.mifosplatform.infrastructure.core.service.Page;
import org.mifosplatform.infrastructure.core.service.PaginationHelper;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.logistics.item.data.ItemData;
import org.mifosplatform.portfolio.servicemapping.data.ServiceCodeData;
import org.mifosplatform.portfolio.servicemapping.data.ServiceMappingData;
import org.mifosplatform.provisioning.provisioning.data.ServiceParameterData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceMappingReadPlatformServiceImpl implements
		ServiceMappingReadPlatformService {

	private final JdbcTemplate jdbcTemplate;
	private final PaginationHelper<ServiceMappingData> paginationHelper = new PaginationHelper<ServiceMappingData>();

	@Autowired
	public ServiceMappingReadPlatformServiceImpl(
			final TenantAwareRoutingDataSource tenantAwareRoutingDataSource) {
		this.jdbcTemplate = new JdbcTemplate(tenantAwareRoutingDataSource);
	}

	private static final class ServiceMappingMapper implements RowMapper<ServiceMappingData> {
		public String schema() {

			return " ps.id as id, bs.service_code as serviceCode, ps.service_identification as serviceIdentification, bs.status as status,ps.image as image, "
					+ "ps.category as category,ps.sub_category as subCategory,ps.provision_system as provisionSystem,ps.sort_by as sortBy," 
					+ "ps.item_id as itemId,im.item_description as itemDescription,ps.is_hw_req as isHwReq " +
					"  from b_service bs inner join b_prov_service_details ps on  ps.service_id=bs.id " +
					"  left join  b_item_master im on ps.item_id=im.id  where  bs.status='ACTIVE' and bs.is_deleted ='N' and ps.is_deleted='n' ";


		}

		@Override
		public ServiceMappingData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {
			Long id = rs.getLong("id");
			String serviceCode = rs.getString("serviceCode");
			String serviceIdentification = rs.getString("serviceIdentification");
			String status = rs.getString("status");
			String image = rs.getString("image");
			String category = rs.getString("category");
			String subCategory = rs.getString("subCategory");
			String provisionSystem = rs.getString("provisionSystem");
		    String sortBy = rs.getString("sortBy");
			Long itemId = rs.getLong("itemId");
			String isHwReq = rs.getString("isHwReq");
			String itemDescription=rs.getString("itemDescription");
			return new ServiceMappingData(id, serviceCode,serviceIdentification, status, image, category, subCategory,provisionSystem, sortBy,itemId,isHwReq,itemDescription);
		}
	}

	public Page<ServiceMappingData> getServiceMapping(SearchSqlQuery searchCodes) {
		ServiceMappingMapper mapper = new ServiceMappingMapper();
		String sql = "select SQL_CALC_FOUND_ROWS " + mapper.schema() + " ORDER BY ISNULL(ps.sort_by), ps.sort_by ASC ";
		StringBuilder sqlBuilder = new StringBuilder(200);
		sqlBuilder.append(sql);
		if (searchCodes.isLimited()) {
            sqlBuilder.append(" limit ").append(searchCodes.getLimit());
        }
        if (searchCodes.isOffset()) {
            sqlBuilder.append(" offset ").append(searchCodes.getOffset());
        }
		//return this.jdbcTemplate.query(sql, mapper, new Object[] {});
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()",sqlBuilder.toString(),
	            new Object[] {}, mapper);
	}

	private class ServiceCodeDataMapper implements RowMapper<ServiceCodeData> {
		public ServiceCodeData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {
			final Long id = rs.getLong("id");
			final String serviceCode = rs.getString("serviceCode");
			final String serviceType = rs.getString("serviceType");

			return new ServiceCodeData(id, serviceCode, serviceType);
		}
	}

	public List<ServiceCodeData> getServiceCode() {
		final String sql = "select bs.id as id,bs.service_code as serviceCode,bs.service_type as serviceType  from b_service bs "
				+ "where bs.status='ACTIVE' and bs.is_deleted ='N'  order by bs.id";
		ServiceCodeDataMapper rowMapper = new ServiceCodeDataMapper();
		return jdbcTemplate.query(sql, rowMapper);
	}

	private class ServiceMappingDataByIdRowMapper implements
			RowMapper<ServiceMappingData> {

		public String schema() {

			return " bs.id as serviceId,bs.service_code as serviceCode, ps.service_identification as serviceIdentification, bs.status as status,"
					+ "ps.image as image,ps.category as category,ps.sub_category as subCategory,ps.provision_system as provisionSystem ,ps.sort_by as sortBy,"
					+ "ps.item_id as itemId,im.item_description as itemDescription,ps.is_hw_req as isHwReq from b_service bs inner join b_prov_service_details ps on ps.service_id=bs.id " +
					  "left join b_item_master im on im.id=ps.item_id";

		}

		@Override
		public ServiceMappingData mapRow(ResultSet rs, int rowNum)
				throws SQLException {

			Long serviceId = rs.getLong("serviceId");
			String serviceCode = rs.getString("serviceCode");
			String serviceIdentification = rs.getString("serviceIdentification");
			String status = rs.getString("status");
			String image = rs.getString("image");
			String category = rs.getString("category");
			String subCategory = rs.getString("subCategory");
			String provisionSystem = rs.getString("provisionSystem");
		    String sortBy = rs.getString("sortBy");
			Long itemId = rs.getLong("itemId");
			String isHwReq = rs.getString("isHwReq");
			String itemDescription=rs.getString("itemDescription");
			return new ServiceMappingData(serviceId, serviceCode,serviceIdentification, status, image, category, subCategory, sortBy,provisionSystem,itemId,isHwReq,itemDescription);
		}
	}

	public ServiceMappingData getServiceMapping(Long serviceMappingId) {

		ServiceMappingDataByIdRowMapper rowMapper = new ServiceMappingDataByIdRowMapper();
		String sql = "select" + rowMapper.schema()
				+ " where ps.is_deleted='n' and  ps.id=?";
		return jdbcTemplate.queryForObject(sql, rowMapper,
				new Object[] { serviceMappingId });
	}

	private static final class ServiceParameterMapper implements
			RowMapper<ServiceParameterData> {

		public String schema() {
			return " sd.id AS id,sd.service_identification AS paramName,sd.image AS paramValue,sr.parameter_displayType as type "
					+ " FROM b_orders o,b_plan_master p,b_service s,b_plan_detail pd,b_prov_service_details sd,stretchy_parameter sr"
					+ " WHERE  p.id = o.plan_id AND pd.plan_id = p.id AND pd.service_code = s.service_code AND sd.service_id = s.id "
					+ " and sd.service_identification = sr.parameter_name ";
		}

		@Override
		public ServiceParameterData mapRow(final ResultSet rs, final int rowNum)
				throws SQLException {

			Long id = rs.getLong("id");
			String paramName = rs.getString("paramName");
			String paramValue = rs.getString("paramValue");
			String type = rs.getString("type");
			return new ServiceParameterData(id, paramName, paramValue, type);
		}
	}

	@Transactional
	@Override
	public List<ServiceParameterData> getSerivceParameters(Long orderId,
			Long serviceId) {

		try {
			ServiceParameterMapper mapper = new ServiceParameterMapper();
			String sql = "select " + mapper.schema() + " and o.id = " + orderId;
			if (serviceId != null) {
				sql = sql + " and sd.service_id=" + serviceId;
			}

			return this.jdbcTemplate.query(sql, mapper, new Object[] {});

		} catch (EmptyResultDataAccessException accessException) {
			return null;
		}

	}

	@Override
	public List<ServiceMappingData> retrieveOptionalServices(String serviceType) {

		try {
			ServiceMappingDataByIdRowMapper mapper = new ServiceMappingDataByIdRowMapper();

			String sql = "SELECT s.id as serviceId,s.service_code as serviceCode,ifnull(sp.category,'Others') as category,sp.sub_category as subcategory," +
					" sp.image as image,sp.service_identification as serviceIdentification,s.status as status,sp.provision_system as provisionSystem ," +
					" sp.sort_by as sortBy,sp.item_id AS itemId, sp.is_hw_req AS isHwReq,'' AS itemDescription FROM b_service s left join b_prov_service_details sp on s.id = sp.service_id where s.is_deleted = 'N' "; 
			
			if (serviceType != null) {
				sql = sql + " and s.is_optional= '"+serviceType+"'";
			}

			return this.jdbcTemplate.query(sql, mapper, new Object[] {});

		} catch (EmptyResultDataAccessException accessException) {
			return null;
		}

	}

	/* (non-Java doc)
	 * @see #retrieveItems()
	 */
	@Override
	public List<ItemData> retrieveItems() {

		final ItemDataMaper mapper = new ItemDataMaper();
		final String sql = "select " + mapper.schema();
		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class ItemDataMaper implements RowMapper<ItemData> {

		public String schema() {
			return " i.id as id,i.item_code as itemCode,i.item_description as itemDescription from b_item_master i where i.is_deleted='n'";
		}

		@Override
		public ItemData mapRow(final ResultSet rs, final int rowNum)throws SQLException {
			
			Long id = rs.getLong("id");
			String itemCode = rs.getString("itemCode");
			String itemDescription = rs.getString("itemDescription");
			return new ItemData(id, itemCode, itemDescription, null,BigDecimal.ZERO);

		}
	}
}


