package com.yelloco.ticketlibrary;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import org.jsoup.nodes.Element;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TicketGenerationUT
{
    private final String MERCHANT_NAME = "PRUEBAS REDSYS";
    private final String FUC = "999160151";
    private final String TERMINAL_ID = "14";
    private static final String TAG = "TicketGenerationUT";

    @Test
    public void testVisibility_Contact_Magnetic() throws IOException
    {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        TicketGeneration ticketGeneration = new TicketGeneration(context);

        ticketGeneration.parse(OperationType.SALE,
                TicketCopy.MERCHANT_COPY,
                MERCHANT_NAME,
                FUC,
                TERMINAL_ID,
                TransactionDataSamples.CT_MAGNETIC_STRIPE.getTransactionData(10, ""));

        String a = ticketGeneration.getHTML();

        assertTrue(ticketGeneration.isAuthenticationSectionDisplayed());
        assertTrue(ticketGeneration.isSignatureBoxDisplayed());

        assertFalse(ticketGeneration.isCtlsLogoDisplayed());
        assertFalse(ticketGeneration.isEMVSectionDisplayed());
        assertFalse(ticketGeneration.isDCCSectionDisplayed());
        assertFalse(ticketGeneration.isPinAuthenticatedLiteralDisplayed());

        Log.d(TAG, a);
    }

    @Test
    public void testVisibility_CTLS_Magnetic() throws IOException
    {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        TicketGeneration ticketGeneration = new TicketGeneration(context);

        ticketGeneration.parse(OperationType.SALE,
                TicketCopy.MERCHANT_COPY,
                MERCHANT_NAME,
                FUC,
                TERMINAL_ID,
                TransactionDataSamples.CTLS_MAGNETIC_STRIPE.getTransactionData(10, ""));

        assertTrue(ticketGeneration.isCtlsLogoDisplayed());
        assertTrue(ticketGeneration.isAuthenticationSectionDisplayed());
        assertTrue(ticketGeneration.isSignatureBoxDisplayed());

        assertFalse(ticketGeneration.isEMVSectionDisplayed());
        assertFalse(ticketGeneration.isDCCSectionDisplayed());
        assertFalse(ticketGeneration.isPinAuthenticatedLiteralDisplayed());
    }

    @Test
    public void testVisibility_Contact_EMV() throws IOException
    {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        TicketGeneration ticketGeneration = new TicketGeneration(context);

        ticketGeneration.parse(OperationType.SALE,
                TicketCopy.MERCHANT_COPY,
                MERCHANT_NAME,
                FUC,
                TERMINAL_ID,
                TransactionDataSamples.CONTACT_EMV.getTransactionData(10, ""));

        assertTrue(ticketGeneration.isEMVSectionDisplayed());
        assertTrue(ticketGeneration.isAuthenticationSectionDisplayed());
        assertTrue(ticketGeneration.isSignatureBoxDisplayed());

        assertFalse(ticketGeneration.isCtlsLogoDisplayed());
        assertFalse(ticketGeneration.isDCCSectionDisplayed());
        assertFalse(ticketGeneration.isPinAuthenticatedLiteralDisplayed());
    }

    @Test
    public void testVisibility_CTLS_EMV() throws IOException
    {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        TicketGeneration ticketGeneration = new TicketGeneration(context);

        ticketGeneration.parse(OperationType.SALE,
                TicketCopy.MERCHANT_COPY,
                MERCHANT_NAME,
                FUC,
                TERMINAL_ID,
                TransactionDataSamples.CTLS_EMV_11.getTransactionData(10, ""));

        assertTrue(ticketGeneration.isCtlsLogoDisplayed());
        assertTrue(ticketGeneration.isEMVSectionDisplayed());
        assertTrue(ticketGeneration.isAuthenticationSectionDisplayed());
        assertTrue(ticketGeneration.isSignatureBoxDisplayed());

        assertFalse(ticketGeneration.isDCCSectionDisplayed());
        assertFalse(ticketGeneration.isPinAuthenticatedLiteralDisplayed());
    }
}