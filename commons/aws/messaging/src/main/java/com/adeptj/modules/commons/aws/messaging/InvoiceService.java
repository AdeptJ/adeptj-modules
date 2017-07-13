package com.adeptj.modules.commons.aws.messaging;

import java.io.ByteArrayOutputStream;

/**
 * Generate payment invoice.
 *
 * @author Rakesh.Kumar, AdeptJ
 */
public interface InvoiceService {

    ByteArrayOutputStream generateInvoice(String userId, String txnId);
}
