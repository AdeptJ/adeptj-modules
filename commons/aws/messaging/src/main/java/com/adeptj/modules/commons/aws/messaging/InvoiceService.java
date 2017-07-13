package com.adeptj.modules.commons.aws.messaging;

/**
 * Generate payment invoice.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public interface InvoiceService {

    byte[] generateInvoice(String userId, String txnId);
}
