package com.yelloco.ticketlibrary;


import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.redsys.paysys.Operative.Managers.RedCLSQueryData;
import es.redsys.paysys.Operative.Managers.RedCLSQueryManager;
import es.redsys.paysys.Operative.Managers.RedCLSRefundData;
import es.redsys.paysys.Operative.Managers.RedCLSRefundManager;
import es.redsys.paysys.Operative.Managers.RedCLSRefundResponse;
import es.redsys.paysys.Operative.Managers.RedCLSTerminalData;
import es.redsys.paysys.clientServicesSSM.RedCLSMerchantData;

import static es.redsys.paysys.Utils.RedCLSCifradoUtil.TAG;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


public class RefundUT {
    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private final String MERCHANT_NAME = "PRUEBAS REDSYS";
    private final String FUC = "999160151";
    private final String TERMINAL_ID = "14";
    RefundTicketGeneration refundTicketGeneration = new RefundTicketGeneration(context);

    public RefundUT() throws IOException {
    }

    @Test
    public void testRefund(){
        RedCLSRefundResponse redCLSRefundResponse = spy(RedCLSRefundResponse.class);
        when(redCLSRefundResponse.getOperationDate()).thenReturn("2020-08-06 21:04:21.0");
        when(redCLSRefundResponse.getOrder()).thenReturn("8306");
        when(redCLSRefundResponse.getAmount()).thenReturn("10");
        when(redCLSRefundResponse.getCurrency()).thenReturn("978");
        refundTicketGeneration.parse(
                MERCHANT_NAME,
                FUC,
                TERMINAL_ID,
                TransactionDataSamples.CT_MAGNETIC_STRIPE.getTransactionData(10, ""),
                redCLSRefundResponse
        );
        Log.d(TAG, "RefundUT: " + refundTicketGeneration.getHTML());
        assertEquals("EUR", refundTicketGeneration.getCurrencyLabel());
        assertEquals("10", refundTicketGeneration.getAmount());
        assertEquals("609135", refundTicketGeneration.getAuthorizationCode());
        assertEquals("************7779", refundTicketGeneration.getCardNumber());
        assertEquals(MERCHANT_NAME, refundTicketGeneration.getMerchantName());
        assertEquals(FUC, refundTicketGeneration.getMerchantNumber());
        assertEquals(TERMINAL_ID, refundTicketGeneration.getTPVNumber());
        assertEquals("06.08.2020", refundTicketGeneration.getTransactionDate());
        assertEquals("21:04", refundTicketGeneration.getTransactionTime());
        assertEquals("REFUND", refundTicketGeneration.getTicketLabel());
        assertEquals("8306", refundTicketGeneration.getOperation());
    }

}
