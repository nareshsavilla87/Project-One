/**
 * 
 */
package org.mifosplatform.finance.usagecharges.service;

import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.security.service.PlatformSecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Ranjith
 *
 */
@Service
public class UsageChargesReadPlatformServiceImpl implements UsageChargesReadPlatformService {
	
	private final JdbcTemplate jdbcTemplate;
	private final PlatformSecurityContext context;
	
	@Autowired
	public UsageChargesReadPlatformServiceImpl(final PlatformSecurityContext context,
			    final TenantAwareRoutingDataSource dataSource) {
		
		this.context = context;
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	
}
