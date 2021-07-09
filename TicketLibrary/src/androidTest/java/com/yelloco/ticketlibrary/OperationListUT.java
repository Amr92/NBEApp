package com.yelloco.ticketlibrary;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.redsys.paysys.Operative.Managers.RedCLSTransactionData;

import static es.redsys.paysys.Utils.RedCLSCifradoUtil.TAG;
import static org.junit.Assert.assertEquals;

public class OperationListUT {

    Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
    private final String MERCHANT_NAME = "PRUEBAS REDSYS";
    private final String FUC = "999160151";
    private final String TERMINAL_ID = "14";
    OperationListTicketGeneration operationListTicketGeneration = new OperationListTicketGeneration(context);


    public OperationListUT() throws IOException {
    }

    private List<RedCLSTransactionData> transactionDataSamples = new ArrayList<>();


    @Test
    public void TestOperationList(){
        RedCLSTransactionData magStripeTransactionData = TransactionDataSamples.CT_MAGNETIC_STRIPE.getTransactionData(10, "");
        RedCLSTransactionData ctlsTransactionData = TransactionDataSamples.CTLS_EMV_11.getTransactionData(10, "");

        transactionDataSamples.add(magStripeTransactionData);
        transactionDataSamples.add(ctlsTransactionData);

        operationListTicketGeneration.parse(
                MERCHANT_NAME,
                FUC,
                TERMINAL_ID,
                "2020-09-08 21:04:21.0",
                "2020-08-06 21:04:21.0",
                "2020-09-06 21:04:21.0",
                "2",
                "4",
                transactionDataSamples
        );
        Log.d(TAG, "TestOperationList: " + operationListTicketGeneration.getHTML());

        assertEquals(MERCHANT_NAME, operationListTicketGeneration.getMerchantName());
        assertEquals(FUC, operationListTicketGeneration.getMerchantNumber());
        assertEquals(TERMINAL_ID, operationListTicketGeneration.getTPVNumber());
        assertEquals("08.09.2020", operationListTicketGeneration.getTransactionDate());
        assertEquals("21:04", operationListTicketGeneration.getTransactionTime());
        assertEquals("Operation List", operationListTicketGeneration.getTicketLabel());
        assertEquals("06.08.2020", operationListTicketGeneration.getFromDate());
        assertEquals("06.09.2020", operationListTicketGeneration.getToDate());
        assertEquals("2", operationListTicketGeneration.getCurrentPage());
        assertEquals("4", operationListTicketGeneration.getTotalPages());

        //first card
        assertEquals(TransactionDataSamples.CT_MAGNETIC_STRIPE.getDate(magStripeTransactionData.getOperationDate()), operationListTicketGeneration.getAllOperationList().get(0).getOperationDate());
        assertEquals(TransactionDataSamples.CT_MAGNETIC_STRIPE.getTime(magStripeTransactionData.getOperationDate()), operationListTicketGeneration.getAllOperationList().get(0).getOperationTime());
        assertEquals(magStripeTransactionData.getOrder(),operationListTicketGeneration.getAllOperationList().get(0).getOrderId());
        assertEquals(OperationResult.AUTH.getResponse(), operationListTicketGeneration.getAllOperationList().get(0).getOperationType());
        assertEquals(magStripeTransactionData.getResult(), operationListTicketGeneration.getAllOperationList().get(0).getOperationResult());
        assertEquals(magStripeTransactionData.getAmount(), operationListTicketGeneration.getAllOperationList().get(0).getAmount());
        assertEquals(CurrencyCodeUtils.getCurrencyString(context, magStripeTransactionData.getCurrency()), operationListTicketGeneration.getAllOperationList().get(0).getCurrency());
        assertEquals(magStripeTransactionData.getCardClient(), operationListTicketGeneration.getAllOperationList().get(0).getCardNumber());
        assertEquals(magStripeTransactionData.getAutorizationNumber(), operationListTicketGeneration.getAllOperationList().get(0).getAuthNumber());
        assertEquals(magStripeTransactionData.getReferenciaOperacion(), operationListTicketGeneration.getAllOperationList().get(0).getReference());
        assertEquals(magStripeTransactionData.getResult(), operationListTicketGeneration.getAllOperationList().get(0).getOperationResult());

        //second card
        assertEquals(TransactionDataSamples.CTLS_EMV_11.getDate(ctlsTransactionData.getOperationDate()), operationListTicketGeneration.getAllOperationList().get(1).getOperationDate());
        assertEquals(TransactionDataSamples.CTLS_EMV_11.getTime(ctlsTransactionData.getOperationDate()), operationListTicketGeneration.getAllOperationList().get(1).getOperationTime());
        assertEquals(ctlsTransactionData.getOrder(),operationListTicketGeneration.getAllOperationList().get(1).getOrderId());
        assertEquals(OperationResult.AUTH.getResponse(), operationListTicketGeneration.getAllOperationList().get(1).getOperationType());
        assertEquals(ctlsTransactionData.getResult(), operationListTicketGeneration.getAllOperationList().get(1).getOperationResult());
        assertEquals(ctlsTransactionData.getAmount(), operationListTicketGeneration.getAllOperationList().get(1).getAmount());
        assertEquals(CurrencyCodeUtils.getCurrencyString(context, ctlsTransactionData.getCurrency()), operationListTicketGeneration.getAllOperationList().get(1).getCurrency());
        assertEquals(ctlsTransactionData.getCardClient(), operationListTicketGeneration.getAllOperationList().get(1).getCardNumber());
        assertEquals(ctlsTransactionData.getAutorizationNumber(), operationListTicketGeneration.getAllOperationList().get(1).getAuthNumber());
        assertEquals(ctlsTransactionData.getReferenciaOperacion(), operationListTicketGeneration.getAllOperationList().get(1).getReference());
        assertEquals(ctlsTransactionData.getResult(), operationListTicketGeneration.getAllOperationList().get(1).getOperationResult());
    }

}
