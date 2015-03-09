package org.mifosplatform.portfolio.order.domain;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.joda.time.LocalDate;
import org.mifosplatform.infrastructure.core.api.JsonCommand;
import org.mifosplatform.infrastructure.core.domain.AbstractAuditableCustom;
import org.mifosplatform.infrastructure.core.service.DateUtils;
import org.mifosplatform.portfolio.order.data.OrderStatusEnumaration;
import org.mifosplatform.useradministration.domain.AppUser;

@Entity
@Table(name = "rm_services")
public class RadServiceTemp extends AbstractAuditableCustom<AppUser, Long> {

	@Column(name = "srvid")
	private Long clientId;
	
	@Column(name = "srvname")
	private String srvname;

	@Column(name = "downrate")
	private Long downrate;

	@Column(name = "uprate")
	private Long uprate;
	
	@Column(name = "limitdl", nullable = false)
    private boolean limitdl;
	
	@Column(name = "limitul", nullable = false)
    private boolean limitul;
	
	@Column(name = "limitcomb", nullable = false)
    private boolean limitcomb;
	
	@Column(name = "limitexpiration", nullable = false)
    private boolean limitexpiration;
	
	@Column(name = "limituptime", nullable = false)
    private boolean limituptime;

	@Column(name = "poolname")
	private String poolname;

	@Column(name = "unitprice")
	private BigDecimal unitprice;

	@Column(name = "unitpriceadd")
	private BigDecimal unitpriceadd;
	
	@Column(name = "timebaseexp", nullable = false)
    private boolean timebaseexp;
	
	@Column(name = "timebaseonline", nullable = false)
    private boolean timebaseonline;
	
	@Column(name = "timeunitexp")
	private Long timeunitexp;
	
	@Column(name = "timeunitonline")
	private Long timeunitonline;
	
	@Column(name = "trafficunitdl")
	private Long trafficunitdl;
	
	@Column(name = "trafficunitul")
	private Long trafficunitul;
	
	@Column(name = "trafficunitcomb")
	private Long trafficunitcomb;
	
	@Column(name = "inittimeexp")
	private Long inittimeexp;
	
	@Column(name = "inittimeonline")
	private Long inittimeonline;
	
	@Column(name = "initdl")
	private Long initdl;
	
	@Column(name = "initul")
	private Long initul;
	
	@Column(name = "inittotal")
	private Long inittotal;

	@Column(name = "srvtype", nullable = false)
    private boolean srvtype;
	
	@Column(name = "timeaddmodeexp", nullable = false)
    private boolean timeaddmodeexp;
	
	@Column(name = "timeaddmodeonline", nullable = false)
    private boolean timeaddmodeonline;
	
	@Column(name = "trafficaddmode", nullable = false)
    private boolean trafficaddmode;
	
	@Column(name = "monthly", nullable = false)
    private boolean monthly;
	
	@Column(name = "enaddcredits", nullable = false)
    private boolean enaddcredits;
	
	@Column(name = "minamount")
	private Long minamount;
	
	@Column(name = "minamountadd")
	private Long minamountadd;
	
	@Column(name = "resetcounters", nullable = false)
    private boolean resetcounters;
	
	@Column(name = "pricecalcdownload", nullable = false)
    private boolean pricecalcdownload;
	
	@Column(name = "pricecalcupload", nullable = false)
    private boolean pricecalcupload;
	
	@Column(name = "pricecalcuptime", nullable = false)
    private boolean pricecalcuptime;
	
	@Column(name = "unitpricetax")
	private BigDecimal unitpricetax;
	
	@Column(name = "unitpriceaddtax")
	private BigDecimal unitpriceaddtax;
	
	@Column(name = "enableburst", nullable = false)
    private boolean enableburst;
	
	@Column(name = "dlburstlimit")
	private Long dlburstlimit;
	
	@Column(name = "ulburstlimit")
	private Long ulburstlimit;
	
	@Column(name = "dlburstthreshold")
	private Long dlburstthreshold;
	
	@Column(name = "ulburstthreshold")
	private Long ulburstthreshold;
	
	
	@Column(name = "dlbursttime")
	private Long dlbursttime;
	
	@Column(name = "ulbursttime")
	private Long ulbursttime;
	
	
	@Column(name = "enableservice")
	private Long enableservice;
	
	@Column(name = "dlquota")
	private Long dlquota;
	
	
	@Column(name = "ulquota")
	private Long ulquota;
	
	@Column(name = "combquota")
	private Long combquota;
	
	
	@Column(name = "timequota")
	private Long timequota;
	
	@Column(name = "priority")
	private Long priority;
	
	
	@Column(name = "nextsrvid")
	private Long nextsrvid;
	
	@Column(name = "dailynextsrvid")
	private Long dailynextsrvid;
	
	@Column(name = "availucp", nullable = false)
    private boolean availucp;
	
	@Column(name = "renew", nullable = false)
    private boolean renew;
	
	@Column(name = "policymapdl")
	private String policymapdl;
	
	@Column(name = "policymapul")
	private String policymapul;
	
	@Column(name = "custattr")
	private String custattr;
	

	 public RadServiceTemp() {
		// TODO Auto-generated constructor stub
			
	}
 
	
	
}
