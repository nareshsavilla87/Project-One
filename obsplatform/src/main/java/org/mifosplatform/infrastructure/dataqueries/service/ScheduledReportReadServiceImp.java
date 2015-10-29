/**
 * 
 */
package org.mifosplatform.infrastructure.dataqueries.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.dataqueries.data.ReportData;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

/**
 * @author hugo
 *
 */
@Service
public class ScheduledReportReadServiceImp implements ScheduledReportReadService {
	
	 private final static Logger logger = LoggerFactory.getLogger(ScheduledReportReadServiceImp.class);

	    private final JdbcTemplate jdbcTemplate;
	    private final DataSource dataSource;
	    private final PlatformSecurityContext context;
	
 @Autowired	
 public ScheduledReportReadServiceImp(final PlatformSecurityContext context, final TenantAwareRoutingDataSource dataSource){
	    	
	    this.context = context;
	    this.dataSource = dataSource;
	    this.jdbcTemplate = new JdbcTemplate(this.dataSource);
	    }
	/* (non-Javadoc)
	 * @see retrieveAllReportList()
	 */
	@Override
	public Collection<ReportData> retrieveAllReportNames() {
		
		try{
	        ScheduleReportMapper srm = new ScheduleReportMapper();
	        String sql = srm.schema() +" where core_report=0 AND use_report=1 group by id"; 
	        return this.jdbcTemplate.query(sql, srm, new Object[] {});
			}catch(EmptyResultDataAccessException accessException){
				return null;
			}
	}
	
	 private static final class ScheduleReportMapper implements RowMapper<ReportData> {

		/* (non-Javadoc)
		 * @see mapRow(java.sql.ResultSet, int)
		 */
		@Override
		public ReportData mapRow(ResultSet rs, int rowNum) throws SQLException {
			
			final String reportName = rs.getString("reportName");
			final String reportCategory = rs.getString("reportCategory");
			
			return new ReportData(reportName,reportCategory);
		}
	
		public String schema(){
			
			return "select report_category as reportCategory, report_name as reportName from  stretchy_report";
		
		}
		
	 }

	/* (non-Javadoc)
	 * @see retrieveReportCategories()
	 */
	@Override
	public List<String> retrieveReportCategories() {
		
		try{
	        ScheduleReportCategory srm = new ScheduleReportCategory();
	        String sql = "select report_category as reportCategory from  stretchy_report where core_report=0 AND use_report=1 group by report_category"; 
	        return this.jdbcTemplate.query(sql, srm, new Object[] {});
			}catch(EmptyResultDataAccessException accessException){
				return null;
			}
	}
	
	
	private static final class ScheduleReportCategory implements RowMapper<String> {
		
		@Override
		public String mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getString("reportCategory");
		}

   }
}