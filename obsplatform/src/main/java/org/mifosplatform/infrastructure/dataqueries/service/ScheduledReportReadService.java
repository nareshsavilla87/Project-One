/**
 * 
 */
package org.mifosplatform.infrastructure.dataqueries.service;

import java.util.Collection;
import java.util.List;

import org.mifosplatform.infrastructure.dataqueries.data.ReportData;

/**
 * @author hugo
 *
 */
public interface ScheduledReportReadService {

	Collection<ReportData> retrieveAllReportNames();

	List<String> retrieveReportCategories();

}
