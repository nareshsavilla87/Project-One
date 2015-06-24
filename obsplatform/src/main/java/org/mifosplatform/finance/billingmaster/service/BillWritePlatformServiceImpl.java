package org.mifosplatform.finance.billingmaster.service;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRuntimeException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.mifosplatform.finance.billingmaster.domain.BillDetail;
import org.mifosplatform.finance.billingmaster.domain.BillMaster;
import org.mifosplatform.finance.billingmaster.domain.BillMasterRepository;
import org.mifosplatform.finance.billingorder.exceptions.BillingOrderNoRecordsFoundException;
import org.mifosplatform.infrastructure.core.data.CommandProcessingResult;
import org.mifosplatform.infrastructure.core.service.FileUtils;
import org.mifosplatform.infrastructure.core.service.TenantAwareRoutingDataSource;
import org.mifosplatform.infrastructure.core.service.ThreadLocalContextUtil;
import org.mifosplatform.organisation.message.domain.BillingMessage;
import org.mifosplatform.organisation.message.domain.BillingMessageRepository;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplate;
import org.mifosplatform.organisation.message.domain.BillingMessageTemplateRepository;
import org.mifosplatform.organisation.message.exception.BillingMessageTemplateNotFoundException;
import org.mifosplatform.portfolio.client.domain.Client;
import org.mifosplatform.portfolio.client.domain.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author ranjith
 * 
 */
@Service
public class BillWritePlatformServiceImpl implements BillWritePlatformService {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(BillWritePlatformServiceImpl.class);
	private final BillMasterRepository billMasterRepository;
	private final TenantAwareRoutingDataSource dataSource;
    private final ClientRepository clientRepository;
    private final BillingMessageTemplateRepository messageTemplateRepository;
    private final BillingMessageRepository messageDataRepository;

	
	@Autowired
	public BillWritePlatformServiceImpl(final BillMasterRepository billMasterRepository,final TenantAwareRoutingDataSource dataSource,
			final ClientRepository clientRepository,final BillingMessageTemplateRepository messageTemplateRepository,
		    final BillingMessageRepository messageDataRepository) {

		this.dataSource = dataSource;
		this.billMasterRepository = billMasterRepository;
		this.clientRepository = clientRepository;
		this.messageTemplateRepository = messageTemplateRepository;
		this.messageDataRepository = messageDataRepository;
		
	}

	@Override
	public CommandProcessingResult updateBillMaster(final List<BillDetail> billDetails, final BillMaster billMaster, final BigDecimal clientBalance) {
		
		try{
		BigDecimal chargeAmount = BigDecimal.ZERO;
		BigDecimal adjustmentAmount = BigDecimal.ZERO;
		BigDecimal paymentAmount = BigDecimal.ZERO;
		BigDecimal dueAmount = BigDecimal.ZERO;
		BigDecimal taxAmount = BigDecimal.ZERO;
		BigDecimal oneTimeSaleAmount = BigDecimal.ZERO;
		BigDecimal serviceTransferAmount =BigDecimal.ZERO;
		
		for (final BillDetail billDetail : billDetails) {
			if ("SERVICE_CHARGES".equalsIgnoreCase(billDetail.getTransactionType())) {
				if (billDetail.getAmount() != null)
					chargeAmount = chargeAmount.add(billDetail.getAmount());
				
			} else if ("TAXES".equalsIgnoreCase(billDetail.getTransactionType())) {
				if (billDetail.getAmount() != null)
					taxAmount = taxAmount.add(billDetail.getAmount());

			} else if ("ADJUSTMENT".equalsIgnoreCase(billDetail.getTransactionType())) {
				if (billDetail.getAmount() != null)
					adjustmentAmount = adjustmentAmount.add(billDetail.getAmount());
				
			} else if (billDetail.getTransactionType().contains("PAYMENT")) {
				if (billDetail.getAmount() != null)
					paymentAmount = paymentAmount.add(billDetail.getAmount());

			} else if (billDetail.getTransactionType().contains("ONETIME_CHARGES")) {
				if (billDetail.getAmount() != null)
					oneTimeSaleAmount = oneTimeSaleAmount.add(billDetail.getAmount());

			}else if (billDetail.getTransactionType().contains("SERVICE_TRANSFER")) {
				if (billDetail.getAmount() != null)
					serviceTransferAmount = serviceTransferAmount.add(billDetail.getAmount());
			}
			
		}
	  dueAmount = chargeAmount.add(taxAmount).add(oneTimeSaleAmount).add(clientBalance)
			      .add(serviceTransferAmount).subtract(paymentAmount).subtract(adjustmentAmount);
	  billMaster.setChargeAmount(chargeAmount.add(oneTimeSaleAmount).add(serviceTransferAmount));
	  billMaster.setAdjustmentAmount(adjustmentAmount);
	  billMaster.setTaxAmount(taxAmount);
	  billMaster.setPaidAmount(paymentAmount);
	  billMaster.setDueAmount(dueAmount);
	  billMaster.setPreviousBalance(clientBalance);
	  this.billMasterRepository.save(billMaster);
	  return new CommandProcessingResult(billMaster.getId(),billMaster.getClientId());
	}catch(DataIntegrityViolationException dve){
		LOGGER.error("unable to retrieve data" + dve.getLocalizedMessage());
		return CommandProcessingResult.empty();
	}
}

	@Transactional
	@Override
	public void generateStatementPdf(final Long billId)  {
		
		try {
			final String fileLocation = FileUtils.MIFOSX_BASE_DIR;
			/** Recursively create the directory if it does not exist **/
			if (!new File(fileLocation).isDirectory()) {
				new File(fileLocation).mkdirs();
			}
			BillMaster billMaster=this.billMasterRepository.findOne(billId);
			
			final String statementDetailsLocation = fileLocation + File.separator + "StatementPdfFiles"; 
			if (!new File(statementDetailsLocation).isDirectory()) {
				new File(statementDetailsLocation).mkdirs();
			}
			final String printStatementLocation = statementDetailsLocation + File.separator + "Bill_" + billMaster.getId() + ".pdf";
			final String jpath = fileLocation+File.separator+"jasper"; 
			final String tenant = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
			final String jfilepath =jpath+File.separator+"Statement_"+tenant+".jasper";
			final Connection connection = this.dataSource.getConnection();
		
			Map<String, Object> parameters = new HashMap<String, Object>();
			final Integer id = Integer.valueOf(billMaster.getId().toString());
			parameters.put("param1", id);
			parameters.put("SUBREPORT_DIR",jpath+""+File.separator);
			final JasperPrint jasperPrint = JasperFillManager.fillReport(jfilepath, parameters, connection);
			JasperExportManager.exportReportToPdfFile(jasperPrint, printStatementLocation);
			billMaster.setFileName(printStatementLocation);
			this.billMasterRepository.save(billMaster);
			connection.close();
			System.out.println("Filling report successfully...");
			
		} catch (final DataIntegrityViolationException ex) {
			
			 LOGGER.error("Filling report failed..." + ex.getLocalizedMessage());
			 System.out.println("Filling report failed...");
			 ex.printStackTrace();
		
		} catch (final JRException  | JRRuntimeException e) {
			
			LOGGER.error("Filling report failed..." + e.getLocalizedMessage());
			System.out.println("Filling report failed...");
			e.printStackTrace();
		
		} catch (final Exception e) {
			
			LOGGER.error("Filling report failed..." + e.getLocalizedMessage());
			System.out.println("Filling report failed...");
			e.printStackTrace();
		}
	}

	@Transactional
	@Override
	public String generateInovicePdf(final Long invoiceId) {
		
		final String fileLocation = FileUtils.MIFOSX_BASE_DIR ;
		/** Recursively create the directory if it does not exist **/
		if (!new File(fileLocation).isDirectory()) {
			new File(fileLocation).mkdirs();
		}
		final String InvoiceDetailsLocation = fileLocation + File.separator +"InvoicePdfFiles";
		if (!new File(InvoiceDetailsLocation).isDirectory()) {
			 new File(InvoiceDetailsLocation).mkdirs();
		}
		final String printInvoiceLocation = InvoiceDetailsLocation +File.separator + "Invoice_" + invoiceId + ".pdf";
		try {
			
			final String jpath = fileLocation+File.separator+"jasper"; 
			final String tenant = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
			final String jasperfilepath =jpath+File.separator+"Invoicereport_"+tenant+".jasper";
			final Integer id = Integer.valueOf(invoiceId.toString());
			final Connection connection = this.dataSource.getConnection();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("param1", id);
		   final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperfilepath, parameters, connection);
		   JasperExportManager.exportReportToPdfFile(jasperPrint,printInvoiceLocation);
	       connection.close();
	       System.out.println("Filling report successfully...");
	       
		   }catch (final DataIntegrityViolationException ex) {
			 LOGGER.error("Filling report failed..." + ex.getLocalizedMessage());
			 System.out.println("Filling report failed...");
			 ex.printStackTrace();
		   } catch (final JRException  | JRRuntimeException e) {
			LOGGER.error("Filling report failed..." + e.getLocalizedMessage());
			System.out.println("Filling report failed...");
		 	e.printStackTrace();
		  } catch (final Exception e) {
			LOGGER.error("Filling report failed..." + e.getLocalizedMessage());
			System.out.println("Filling report failed...");
			e.printStackTrace();
		}
		return printInvoiceLocation;	
   }
	
	@Transactional
	@Override
	public String generatePaymentPdf(final Long paymentId)  {
		
		final String fileLocation = FileUtils.MIFOSX_BASE_DIR ;
		/** Recursively create the directory if it does not exist **/
		if (!new File(fileLocation).isDirectory()) {
			new File(fileLocation).mkdirs();
		}
		final String PaymentDetailsLocation = fileLocation + File.separator +"PaymentPdfFiles";
		if (!new File(PaymentDetailsLocation).isDirectory()) {
			 new File(PaymentDetailsLocation).mkdirs();
		}
		final String printPaymentLocation = PaymentDetailsLocation +File.separator + "Payment_" + paymentId + ".pdf";
		try {
			
			final String jpath = fileLocation+File.separator+"jasper"; 
			final String tenant = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
			final String jasperfilepath =jpath+File.separator+"Paymentreport_"+tenant+".jasper";
			final Integer id = Integer.valueOf(paymentId.toString());
			final Connection connection = this.dataSource.getConnection();
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("param1", id);
		   final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperfilepath, parameters, connection);
		   JasperExportManager.exportReportToPdfFile(jasperPrint,printPaymentLocation);
	       connection.close();
	       System.out.println("Filling report successfully...");
	       
		   }catch (final DataIntegrityViolationException ex) {
			 LOGGER.error("Filling report failed..." + ex.getLocalizedMessage());
			 System.out.println("Filling report failed...");
			 ex.printStackTrace();
		   } catch (final JRException  | JRRuntimeException e) {
			LOGGER.error("Filling report failed..." + e.getLocalizedMessage());
			System.out.println("Filling report failed...");
		 	e.printStackTrace();
		  } catch (final Exception e) {
			LOGGER.error("Filling report failed..." + e.getLocalizedMessage());
			System.out.println("Filling report failed...");
			e.printStackTrace();
		}
		return printPaymentLocation;	
	}
	
	@Transactional
	@Override
	public void sendPdfToEmail(final String printFileName, final Long clientId,final String templateName) {
		
		//context.authenticatedUser();
		final Client client = this.clientRepository.findOne(clientId);
		final String clientEmail = client.getEmail();
		if(clientEmail == null){
			final String msg = "Please provide email first";
			throw new BillingOrderNoRecordsFoundException(msg, client);
		}
		final BillingMessageTemplate messageTemplate = this.messageTemplateRepository.findByTemplateDescription(templateName);
		if(messageTemplate !=null){
		  String header = messageTemplate.getHeader().replace("<PARAM1>", client.getDisplayName().isEmpty()?client.getFirstname():client.getDisplayName());
		  BillingMessage  billingMessage = new BillingMessage(header, messageTemplate.getBody(), messageTemplate.getFooter(), clientEmail, clientEmail, 
		    		messageTemplate.getSubject(), "N", messageTemplate, messageTemplate.getMessageType(), printFileName);
		    this.messageDataRepository.save(billingMessage);
	    }else{
	    	throw new BillingMessageTemplateNotFoundException(templateName);
	    }
	  }
	}

