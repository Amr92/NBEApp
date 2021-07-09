package com.yelloco.ticketlibrary;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.redsys.paysys.Operative.Managers.RedCLSTransactionData;

import static android.content.ContentValues.TAG;
import static org.junit.Assert.assertEquals;

public class DateAndTimeUT
{
    private final String MERCHANT_NAME = "PRUEBAS REDSYS";
    private final String FUC = "999160151";
    private final String TERMINAL_ID = "14";

    @Test
    public void testDataAndTime() throws IOException
    {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        TicketGeneration ticketGeneration = new TicketGeneration(context);

        ticketGeneration.parse(OperationType.SALE,
                TicketCopy.MERCHANT_COPY,
                MERCHANT_NAME,
                FUC,
                TERMINAL_ID,
                TransactionDataSamples.CT_MAGNETIC_STRIPE.getTransactionData(10, ""));

        assertEquals("06.08.2020", ticketGeneration.getTransactionDate());
        assertEquals("21:04", ticketGeneration.getTransactionTime());
    }
}