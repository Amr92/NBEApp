package com.yelloco.ticketlibrary;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import java.io.IOException;

import es.redsys.paysys.Operative.Managers.RedCLSTotalsGroup;
import es.redsys.paysys.Operative.Managers.RedCLSTotalsQueryResponse;

import static es.redsys.paysys.Utils.RedCLSCifradoUtil.TAG;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class TotalsUt {

    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private final String MERCHANT_NAME = "PRUEBAS REDSYS";
    private final String FUC = "999160151";
    private final String TERMINAL_ID = "14";
    TotalsTicketGenaration totalsTicketGenaration = new TotalsTicketGenaration(context);

    public TotalsUt() throws IOException {
    }


    @Test
    public void testTotals(){
        RedCLSTotalsGroup redCLSAuthTotalsGroup = spy(RedCLSTotalsGroup.class);
        RedCLSTotalsGroup redCLSPreAuthTotalsGroup = spy(RedCLSTotalsGroup.class);
        RedCLSTotalsGroup redCLSRefundTotalsGroup = spy(RedCLSTotalsGroup.class);
        RedCLSTotalsGroup redCLSConfirmationTotalsGroup = spy(RedCLSTotalsGroup.class);
        RedCLSTotalsQueryResponse redCLSTotalsQueryResponse = spy(RedCLSTotalsQueryResponse.class);

        when(redCLSAuthTotalsGroup.getAmountAuthorizations()).thenReturn(175.5);
        when(redCLSAuthTotalsGroup.getAuthorizedTransactions()).thenReturn(9);
        when(redCLSAuthTotalsGroup.getRefusalsTransactions()).thenReturn(2);
        when(redCLSAuthTotalsGroup.getDtoAuthorizations()).thenReturn(2.0);

        when(redCLSPreAuthTotalsGroup.getAmountAuthorizations()).thenReturn(50.0);
        when(redCLSPreAuthTotalsGroup.getAuthorizedTransactions()).thenReturn(5);
        when(redCLSPreAuthTotalsGroup.getRefusalsTransactions()).thenReturn(1);
        when(redCLSPreAuthTotalsGroup.getDtoAuthorizations()).thenReturn(5.0);

        when(redCLSRefundTotalsGroup.getAmountAuthorizations()).thenReturn(75.5);
        when(redCLSRefundTotalsGroup.getAuthorizedTransactions()).thenReturn(7);
        when(redCLSRefundTotalsGroup.getRefusalsTransactions()).thenReturn(3);
        when(redCLSRefundTotalsGroup.getDtoAuthorizations()).thenReturn(10.0);

        when(redCLSConfirmationTotalsGroup.getAmountAuthorizations()).thenReturn(210.0);
        when(redCLSConfirmationTotalsGroup.getAuthorizedTransactions()).thenReturn(11);

        when(redCLSTotalsQueryResponse.getAuthorizations()).thenReturn(redCLSAuthTotalsGroup);
        when(redCLSTotalsQueryResponse.getPreauthorizations()).thenReturn(redCLSPreAuthTotalsGroup);
        when(redCLSTotalsQueryResponse.getRefunds()).thenReturn(redCLSRefundTotalsGroup);
        when(redCLSTotalsQueryResponse.getConfirmations()).thenReturn(redCLSConfirmationTotalsGroup);

        when(redCLSTotalsQueryResponse.getCurrency()).thenReturn("978");
        when(redCLSTotalsQueryResponse.getCurrency_format()).thenReturn("EUR");

        totalsTicketGenaration.parse(
                MERCHANT_NAME,
                FUC,
                TERMINAL_ID,
                TransactionDataSamples.CT_MAGNETIC_STRIPE.getTransactionData(10, "").getOperationDate(),
                redCLSTotalsQueryResponse
        );
        Log.d(TAG, "TotalsUt: HTML body" + totalsTicketGenaration.getHTML());

        assertEquals(MERCHANT_NAME, totalsTicketGenaration.getMerchantName());
        assertEquals(FUC, totalsTicketGenaration.getMerchantNumber());
        assertEquals(TERMINAL_ID, totalsTicketGenaration.getTPVNumber());
        assertEquals("2100", totalsTicketGenaration.getENTNumber());
        assertEquals("TOTALS QUERY", totalsTicketGenaration.getTicketLabel());
        assertEquals("06.08.2020", totalsTicketGenaration.getTransactionDate());
        assertEquals("21:04", totalsTicketGenaration.getTransactionTime());
        assertEquals("175.5", totalsTicketGenaration.getAuthAmount());
        assertEquals("9", totalsTicketGenaration.getAuthNumOfOp());
        assertEquals("50.0", totalsTicketGenaration.getPreAuthorizationAmount());
        assertEquals("5", totalsTicketGenaration.getPreAuthorizationNumOfOperations());
        assertEquals("75.5", totalsTicketGenaration.getRefundsAmount());
        assertEquals("7", totalsTicketGenaration.getRefundsNumOfOperations());
        assertEquals("210.0", totalsTicketGenaration.getConfirmationsAmount());
        assertEquals("11",totalsTicketGenaration.getConfirmationsNumOfOperations());
        assertEquals("511.0", totalsTicketGenaration.getTotalAmount());
        assertEquals("34", totalsTicketGenaration.getTotalNumOfOperations());
        assertEquals("EUR", totalsTicketGenaration.getCurrency());
    }
}
