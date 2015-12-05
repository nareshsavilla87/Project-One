package org.obsplatform.crm.ticketmaster.service;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.LocalDate;
import org.obsplatform.crm.clientprospect.service.SearchSqlQuery;
import org.obsplatform.crm.ticketmaster.data.ClientTicketData;
import org.obsplatform.crm.ticketmaster.data.TicketMasterData;
import org.obsplatform.crm.ticketmaster.data.UsersData;
import org.obsplatform.crm.ticketmaster.domain.PriorityType;
import org.obsplatform.crm.ticketmaster.domain.PriorityTypeEnum;
import org.obsplatform.crm.ticketmaster.domain.TicketDetail;
import org.obsplatform.crm.ticketmaster.domain.TicketHistory;
import org.obsplatform.infrastructure.core.data.EnumOptionData;
import org.obsplatform.infrastructure.core.domain.JdbcSupport;
import org.obsplatform.infrastructure.core.service.Page;
import org.obsplatform.infrastructure.core.service.PaginationHelper;
import org.obsplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.obsplatform.infrastructure.security.service.PlatformSecurityContext;
import org.obsplatform.useradministration.domain.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

@Service
public class TicketMasterReadPlatformServiceImpl  implements TicketMasterReadPlatformService{
	
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	private final PaginationHelper<ClientTicketData> paginationHelper = new PaginationHelper<ClientTicketData>();

	@Autowired
	public TicketMasterReadPlatformServiceImpl(final PlatformSecurityContext context,
			final TenantAwareRoutingDataSource dataSource) {
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Override
	public List<UsersData> retrieveUsers() {
		context.authenticatedUser();

		final UserMapper mapper = new UserMapper();

		final String sql = "select " + mapper.schema();

		return this.jdbcTemplate.query(sql, mapper, new Object[] {});
	}

	private static final class UserMapper implements
			RowMapper<UsersData> {

		public String schema() {
			return "u.id as id,u.username as username from m_appuser u where u.is_deleted=0";

		}

		@Override
		public UsersData mapRow(ResultSet resultSet, int rowNum)
				throws SQLException {

			final Long id = resultSet.getLong("id");
			final String username = resultSet.getString("username");

			final UsersData data = new UsersData(id, username);

			return data;

		}

	}
	
	@Override
	public Page<ClientTicketData> retrieveAssignedTicketsForNewClient(SearchSqlQuery searchTicketMaster, String statusType) {
		final AppUser user = this.context.authenticatedUser();
		
		final String hierarchy = user.getOffice().getHierarchy();
        
        
        final String hierarchySearchString = hierarchy + "%";
		
		final UserTicketsMapperForNewClient mapper = new UserTicketsMapperForNewClient();
				
		StringBuilder sqlBuilder = new StringBuilder(200);
        sqlBuilder.append("select ");
        sqlBuilder.append(mapper.userTicketSchema());
        sqlBuilder.append(" where tckt.id IS NOT NULL and o.hierarchy like ? ");
        
        String sqlSearch = searchTicketMaster.getSqlSearch();
        String extraCriteria = "";
	    if (sqlSearch != null) {
	    	sqlSearch = sqlSearch.trim();
	    	extraCriteria = " and ((select display_name from m_client where id = tckt.client_id) like '%"+sqlSearch+"%' OR" 
	    			+ " (select mcv.code_value from m_code_value mcv where mcv.id = tckt.problem_code) like '%"+sqlSearch+"%' OR"
	    			+ " tckt.status like '%"+sqlSearch+"%' OR"
	    			+ " (select user.username from m_appuser user where tckt.assigned_to = user.id) like '%"+sqlSearch+"%')";
	    }
	    if(statusType != null){
	    	extraCriteria =" and tckt.status='"+statusType+"'";
	    }
	    sqlBuilder.append(extraCriteria);
	    sqlBuilder.append(" order by tckt.id desc ");
	    
        if (searchTicketMaster.isLimited()) {
            sqlBuilder.append(" limit ").append(searchTicketMaster.getLimit());
        }

        if (searchTicketMaster.isOffset()) {
            sqlBuilder.append(" offset ").append(searchTicketMaster.getOffset());
        }
		
		return this.paginationHelper.fetchPage(this.jdbcTemplate, "SELECT FOUND_ROWS()", sqlBuilder.toString(),
	            new Object[] {hierarchySearchString}, mapper);
		
	}
	
	@Override
	public List<TicketMasterData> retrieveClientTicketDetails(final Long clientId) {
		try {
				final ClientTicketMapper mapper = new ClientTicketMapper();

				final String sql = "select " + mapper.clientOrderLookupSchema() + " and tckt.client_id= ? order by tckt.id DESC ";

				return jdbcTemplate.query(sql, mapper, new Object[] { clientId});
			} catch (EmptyResultDataAccessException e) {
				return null;
			  }

	}

	private static final class ClientTicketMapper implements RowMapper<TicketMasterData> {

		public String clientOrderLookupSchema() {
				
		return "tckt.id as id, tckt.priority as priority, tckt.ticket_date as ticketDate, tckt.assigned_to as userId,tckt.source_of_ticket as sourceOfTicket, "
					+" tckt.problem_code as problemCode, tckt.status_code as statusCode, tckt.due_date as dueDate, tckt.resolution_description as resolutionDescription," +
					"(SELECT code_value FROM m_code_value mcv WHERE  tckt.status_code = mcv.id) AS ticketstatus," +
					" tckt.issue as issue,tckt.description as description, "
			        + " (select code_value from m_code_value mcv where tckt.problem_code=mcv.id)as problemDescription," 
					+ " tckt.status as status, "
			        + " (select m_appuser.username from m_appuser "
                    +		" inner join b_ticket_details td on td.assigned_to = m_appuser.id"
                    + " where td.id = (select max(id) from b_ticket_details where b_ticket_details.ticket_id = tckt.id)) as assignedTo,"
			        + " (select comments FROM b_ticket_details details where details.ticket_id =tckt.id and "
			        + " details.id=(select max(id) from b_ticket_details where b_ticket_details.ticket_id = tckt.id)) as lastComment"
			        + " from b_ticket_master tckt, m_appuser user where tckt.assigned_to = user.id"; 
		}

		@Override
		public TicketMasterData mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

			final Long id = resultSet.getLong("id");
			final String priority = resultSet.getString("priority");
			final String status = resultSet.getString("status");
			final String LastComment = resultSet.getString("LastComment");
			final String problemDescription = resultSet.getString("problemDescription");
			final String assignedTo = resultSet.getString("assignedTo");
			final String usersId = resultSet.getString("userId");
			final LocalDate ticketDate = JdbcSupport.getLocalDate(resultSet, "ticketDate");
			final int userId = new Integer(usersId);
			final String sourceOfTicket = resultSet.getString("sourceOfTicket");
			final Date dueDate = resultSet.getTimestamp("dueDate");
			final String issue = resultSet.getString("issue");
			final String description = resultSet.getString("description");
			final Integer problemCode = resultSet.getInt("problemCode");
			final Integer statusCode = resultSet.getInt("statusCode");
			final String resolutionDescription = resultSet.getString("resolutionDescription");
			
			return new TicketMasterData(id, priority, status, userId, ticketDate, LastComment, problemDescription, assignedTo, sourceOfTicket,
					dueDate, description, issue, problemCode, statusCode,resolutionDescription);
		}
	}

	@Override
	public TicketMasterData retrieveSingleTicketDetails(final Long clientId, final Long ticketId) {
		try {
				final ClientTicketMapper mapper = new ClientTicketMapper();
				final String sql = "select " + mapper.clientOrderLookupSchema() + " and tckt.client_id= ? and tckt.id=?";
				return jdbcTemplate.queryForObject(sql, mapper, new Object[] {clientId, ticketId});
			} catch (EmptyResultDataAccessException e) {
					return null;
			}

	}

	@Override
	public List<EnumOptionData> retrievePriorityData() {
		EnumOptionData low = PriorityTypeEnum.priorityType(PriorityType.LOW);
		EnumOptionData medium = PriorityTypeEnum.priorityType(PriorityType.MEDIUM);
		EnumOptionData high = PriorityTypeEnum.priorityType(PriorityType.HIGH);
		List<EnumOptionData> priorityType = Arrays.asList(low, medium, high);
		return priorityType;
	}

	@Override
	public List<TicketMasterData> retrieveClientTicketHistory(final Long ticketId, final String historyParam) {
		context.authenticatedUser();
		
		if("comment".equalsIgnoreCase(historyParam)){
		
			final TicketDataMapper mapper = new TicketDataMapper();
			String sql = "select " + mapper.schema() + " where t.ticket_id=tm.id and t.ticket_id=? and t.comments is not null order by t.id DESC";
			return this.jdbcTemplate.query(sql, mapper, new Object[] { ticketId});
		}else{
			final TickethistoryMapper mapper = new TickethistoryMapper();
			String sql = "select " + mapper.historyschema() + " where th.ticket_id = tm.id and th.ticket_id=?  and td.ticket_id = ? group by th.id order by th.id DESC";
			return this.jdbcTemplate.query(sql, mapper, new Object[] { ticketId,ticketId});
		}
	}
	private static final class TickethistoryMapper implements
			RowMapper<TicketMasterData> {

		public String historyschema() {
			return " th.id AS id, th.created_date AS createDate, user.username AS assignedTo, th.assign_from as assignFrom, th.status as status,"
					+ " td.attachments as attachements from ( b_ticket_master tm, b_ticket_history th, b_ticket_details td) inner join m_appuser user ON " +
					"user.id = th.assigned_to";

		}

		@Override
		public TicketMasterData mapRow(ResultSet resultSet, int rowNum)throws SQLException {

			final Long id = resultSet.getLong("id");
			final LocalDate createdDate = JdbcSupport.getLocalDate(resultSet,"createDate");
			final String assignedTo = resultSet.getString("assignedTo");
			final String assignFrom = resultSet.getString("assignFrom");
			final String status = resultSet.getString("status");
			String fileName = null;
			final TicketMasterData data = new TicketMasterData(id, createdDate,assignedTo, null, fileName, assignFrom, status, null);
			return data;
		}
	}

	private static final class TicketDataMapper implements
					RowMapper<TicketMasterData> {

		public String schema() {
				return " t.id AS id,t.created_date AS createDate,user.username AS assignedTo,t.comments as description, t.assign_from as assignFrom, " +
						" t.attachments AS attachments, t.status as status, t.username as username  FROM b_ticket_master tm , b_ticket_details t  "
						+" inner join m_appuser user on user.id = t.assigned_to ";

		}
		@Override
		public TicketMasterData mapRow(ResultSet resultSet, int rowNum)
						throws SQLException {

			final Long id = resultSet.getLong("id");
			final LocalDate createdDate = JdbcSupport.getLocalDate(resultSet, "createDate");
			final String assignedTo = resultSet.getString("assignedTo");
			final String description = resultSet.getString("description");
			final String attachments = resultSet.getString("attachments");
			final String assignFrom = resultSet.getString("assignFrom");
			final String status =resultSet.getString("status");
			final String username = resultSet.getString("username");
			String fileName=null;
			if(attachments!=null){
				File file=new File(attachments);
				fileName=file.getName();
			}
			final TicketMasterData data = new TicketMasterData(id, createdDate, assignedTo, description, fileName,assignFrom,status, username);

			return data;
		}
		

	}
				
	private static final class UserTicketsMapperForNewClient implements RowMapper<ClientTicketData> {
				
		public String userTicketSchema() {
					
			return " SQL_CALC_FOUND_ROWS tckt.id AS id,tckt.client_id AS clientId,mct.display_name as clientName,tckt.priority AS priority,"+
							"tckt.status AS status,tckt.ticket_date AS ticketDate,tckt.description as shortdescription,"+
							"(SELECT user.username FROM m_appuser user WHERE tckt.createdby_id = user.id) AS created_user,"+
							"tckt.assigned_to AS userId,"+
							"(SELECT mcv.code_value FROM m_code_value mcv WHERE mcv.id = tckt.problem_code) AS problemDescription,"+
							"(SELECT user.username FROM m_appuser user WHERE tckt.assigned_to = user.id) AS assignedTo,"+
							"CONCAT(TIMESTAMPDIFF(day, tckt.ticket_date, Now()), ' d ', MOD(TIMESTAMPDIFF(hour, tckt.ticket_date, Now()), 24), ' hr ',"+
							"MOD(TIMESTAMPDIFF(minute, tckt.ticket_date, Now()), 60), ' min ') AS timeElapsed,"+
							"IFNull((SELECT user.username FROM m_appuser user WHERE tckt.lastmodifiedby_id = user.id),'Null') AS closedby_user "+
							"FROM b_ticket_master tckt left join m_client mct on mct.id = tckt.client_id "+
							"left join m_office o on o.id = mct.office_id ";
      
		}

		@Override
		public ClientTicketData mapRow(ResultSet resultSet, int rowNum) throws SQLException {
					
			final Long id = resultSet.getLong("id");
			final String priority = resultSet.getString("priority");
			final String status = resultSet.getString("status");
			final Long userId = resultSet.getLong("userId");
			final LocalDate ticketDate = JdbcSupport.getLocalDate(resultSet, "ticketDate");
			final String shortdescription = resultSet.getString("shortdescription");
			final String problemDescription = resultSet.getString("problemDescription");
			final String assignedTo = resultSet.getString("assignedTo");
			final Long clientId = resultSet.getLong("clientId");
			final String timeElapsed = resultSet.getString("timeElapsed");
			final String clientName = resultSet.getString("clientName");
			final String createUser = resultSet.getString("created_user");
			final String closedByuser = resultSet.getString("closedby_user");
					
			return new ClientTicketData(id, priority, status, userId, ticketDate, shortdescription, problemDescription,
					assignedTo, clientId, timeElapsed, clientName, createUser, closedByuser);
		}
				
	}
			
	@Override
	public TicketMasterData retrieveTicket(final Long clientId, final Long ticketId) {

		try {
				final ClientTicketMapper mapper = new ClientTicketMapper();
				final String sql = "select " + mapper.clientOrderLookupSchema() + " and tckt.client_id= ? and tckt.id=?";
				
				return jdbcTemplate.queryForObject(sql, mapper, new Object[] { clientId, ticketId});
			} catch (EmptyResultDataAccessException e) {
					return null;
			}
	}
	
	@Override
	public TicketDetail retrieveTicketDetail(final Long ticketId){
		

		try {
				final TicketDetailMapper mapper = new TicketDetailMapper();
				final String sql = "select " + mapper.ticketdetailSchema() + " where td.id=(select max(t.id) as id from b_ticket_details t where t.ticket_id=?)";
				return jdbcTemplate.queryForObject(sql, mapper, new Object[] {ticketId});
			} catch (EmptyResultDataAccessException e) {
					return null;
			}

	}
	
	private static final class TicketDetailMapper implements RowMapper<TicketDetail> {

		public String ticketdetailSchema() {
				
		return "max(td.id) as id, td.Assign_from as assignFrom, td.assigned_to as assignedTo from b_ticket_details td"; 
		}

		@Override
		public TicketDetail mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

			final Long id = resultSet.getLong("id");
			final String assignfrom = resultSet.getString("assignFrom");
			final Long assignedTo = resultSet.getLong("assignedTo");
			
			return new TicketDetail(id,  assignedTo,assignfrom);
		}
	}
	@Override
	public TicketHistory retrieveTickethistory(final Long ticketId){
		

		try {
				final TicketHistoryMapper mapper = new TicketHistoryMapper();
				final String sql = "select " + mapper.tickethistorySchema() + " where th.id=(select max(t.id) as id from b_ticket_history t where t.ticket_id=?)";
				return jdbcTemplate.queryForObject(sql, mapper, new Object[] {ticketId});
			} catch (EmptyResultDataAccessException e) {
					return null;
			}

	}
	
	private static final class TicketHistoryMapper implements RowMapper<TicketHistory> {

		public String tickethistorySchema() {
				
		return "max(th.id) as id, th.status as status, th.assigned_to as assignedTo from b_ticket_history th"; 
		}

		@Override
		public TicketHistory mapRow(final ResultSet resultSet, final int rowNum) throws SQLException {

			final Long id = resultSet.getLong("id");
			final String status = resultSet.getString("status");
			final Long assignedTo = resultSet.getLong("assignedTo");
			
			return new TicketHistory(id,assignedTo,status);
		}
	}
	
}