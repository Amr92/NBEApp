package com.yelloco.ticketlibrary;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import java.io.IOException;

import es.redsys.paysys.Operative.Managers.RedCLSConfirmationResponse;
import es.redsys.paysys.Operative.Managers.RedCLSTransactionData;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PreAuthorizationConfirmationTicketUT
{
    private final String MERCHANT_NAME = "PRUEBAS REDSYS";
    private final String FUC = "999160151";
    private final String TPV = "20999160151";
    private final String TERMINAL_ID = "14";

    @Test
    public void testPreAuthorizationConfirmationTicket() throws IOException
    {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        PreAuthorizationConfirmationTicket preAuthorizationConfirmationTicket = new PreAuthorizationConfirmationTicket(context);
        preAuthorizationConfirmationTicket.parse(TicketCopy.CUSTOMER_COPY,
                MERCHANT_NAME,
                FUC,
                TPV,
                mockRedCLSTransactionData(),
                mockRedCLSConfirmationResponse());

        assertEquals(context.getString(TicketCopy.CUSTOMER_COPY.getResId()), preAuthorizationConfirmationTicket.getTicketVersion());
        assertEquals(MERCHANT_NAME, preAuthorizationConfirmationTicket.getMerchantName());
        assertEquals(FUC, preAuthorizationConfirmationTicket.getMerchantNumber());
        assertEquals(TPV, preAuthorizationConfirmationTicket.getTPV());
        assertEquals("************2841", preAuthorizationConfirmationTicket.getCardNumber());
        assertEquals(context.getString(OperationType.CONFIRMATION.getResId()), preAuthorizationConfirmationTicket.getOperationType());
        assertEquals("9101", preAuthorizationConfirmationTicket.getOriginalOrder());
        assertEquals("05.09.2020", preAuthorizationConfirmationTicket.getTransactionDate());
        assertEquals("05:49", preAuthorizationConfirmationTicket.getTransactionTime());
        assertEquals("9102", preAuthorizationConfirmationTicket.getOrderNumber());
        assertEquals("10", preAuthorizationConfirmationTicket.getAmount());
        assertEquals("EUR", preAuthorizationConfirmationTicket.getCurrency());
        assertEquals("Yello", preAuthorizationConfirmationTicket.getReference());
    }

    private RedCLSTransactionData mockRedCLSTransactionData()
    {
        RedCLSTransactionData redCLSTransactionData = mock(RedCLSTransactionData.class);
        when(redCLSTransactionData.getCard()).thenReturn("************2841");
        when(redCLSTransactionData.getInvoice()).thenReturn("Yello");
        when(redCLSTransactionData.isContactlessOperation()).thenReturn(false);
        return redCLSTransactionData;

    }

    private RedCLSConfirmationResponse mockRedCLSConfirmationResponse()
    {
        RedCLSConfirmationResponse redCLSConfirmationResponse = mock(RedCLSConfirmationResponse.class);
        when(redCLSConfirmationResponse.getAmount()).thenReturn("10");
        when(redCLSConfirmationResponse.getCurrency()).thenReturn("978");
        when(redCLSConfirmationResponse.getMerchant()).thenReturn(FUC);
        when(redCLSConfirmationResponse.getTerminal()).thenReturn("14");
        when(redCLSConfirmationResponse.getOrder()).thenReturn("9102");
        when(redCLSConfirmationResponse.getBaseOrder()).thenReturn("9101");
        when(redCLSConfirmationResponse.getInvoice()).thenReturn("Yello");
        when(redCLSConfirmationResponse.getIdentifierRTS()).thenReturn("075002200909153630214466");
        when(redCLSConfirmationResponse.getCardType()).thenReturn("1");
        when(redCLSConfirmationResponse.getOperationDate()).thenReturn("2020-09-05 05:49:51.0");
        when(redCLSConfirmationResponse.getState()).thenReturn("Finalizada");
        when(redCLSConfirmationResponse.getResult()).thenReturn("AUTORIZADA");
        return redCLSConfirmationResponse;
    }
}
