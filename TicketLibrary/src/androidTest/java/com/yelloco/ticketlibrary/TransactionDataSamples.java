package com.yelloco.ticketlibrary;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.redsys.paysys.Operative.Managers.RedCLSDccSelectionData;
import es.redsys.paysys.Operative.Managers.RedCLSTransactionData;

import static com.yelloco.ticketlibrary.CardType.CTLS_EMV;
import static com.yelloco.ticketlibrary.CardType.CTLS_MAGSTRIPE;
import static com.yelloco.ticketlibrary.CardType.CT_EMV;
import static com.yelloco.ticketlibrary.CardType.CT_MAGSTRIPE;
import static es.redsys.paysys.Operative.Managers.RedCLSTransactionData.RESULT_AUTHORIZED;
import static es.redsys.paysys.Operative.Managers.RedCLSTransactionData.RESULT_DENIED;
import static es.redsys.paysys.Operative.Managers.RedCLSTransactionData.TYPE_PAYMENT;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public enum TransactionDataSamples
{
    CT_MAGNETIC_STRIPE(
            CT_MAGSTRIPE,
            TYPE_PAYMENT,
            "454881******7779",
            "************7779",
            "0000",
            "1",
            978,
            "5356",
            "075002200519175828468199",
            "CRED",
            "F",
            RESULT_AUTHORIZED,
            "609135",
            "609135",
            "1",
            null,
            null,
            null,
            null,
            "2020-08-06 21:04:21.0"
    ),
    CTLS_MAGNETIC_STRIPE(
            CTLS_MAGSTRIPE,
            TYPE_PAYMENT,
            "454881******7779",
            "************7779",
            "0000",
            "1",
            978,
            "5356",
            "075002200519175828468199",
            "CRED",
            "F",
            RESULT_AUTHORIZED,
            "609135",
            "609135",
            "1",
            null,
            null,
            null,
            null,
            "2020-08-06 21:04:21.0"
    ),
    CONTACT_EMV(
            CT_EMV,
            TYPE_PAYMENT,
            "491801******2830",
            "************2830",
            "0000",
            "1",
            978,
            "5353",
            "075002200521135236469140",
            "DEB",
            "F",
            RESULT_AUTHORIZED,
            "364635",
            "364635",
            "1",
            "0000000000",
            "005326",
            "A0000000031010",
            "VISA CLASICA",
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0", Locale.getDefault()).format(new Date())
    ),
    CTLS_EMV_11(
            CTLS_EMV,
            TYPE_PAYMENT,
            "491801******2830",
            "************2830",
            "0000",
            "1",
            978,
            "5353",
            "075002200521135236469140",
            "DEB",
            "F",
            RESULT_AUTHORIZED,
            "364635",
            "364635",
            "1",
            "0000000000",
            "005326",
            "A0000000031010",
            "VISA CLASICA",
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0", Locale.getDefault()).format(new Date())
    ),
    CTLS_EMV_8_DECLINED(
            CTLS_EMV,
            TYPE_PAYMENT,
            "491801******2822",
            "************2822",
            "0000",
            "1",
            978,
            "5353",
            "075002200521132626469103",
            null,
            "F",
            RESULT_DENIED,
            "195",
            "195",
            "1",
            "0000000000",
            "001832",
            "A0000000031010",
            "VISA CLASICA",
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.0", Locale.getDefault()).format(new Date()));

    private CardType cartType;
    private String type;
    private String card;
    private String cardClient;
    private String expiration;
    private String cardBrand;
    private int currency;
    private String order;
    private String identifierRTS;
    private String rateApplied;
    private String state;
    private String result;
    private String responseCode;
    private String autorizationNumber;
    private String dataCardType;
    private String resVerification;
    private String contTrans;
    private String idApp;
    private String labelApp;
    private String operationDate;

    TransactionDataSamples(CardType cartType, String type, String card, String cardClient,
                            String expiration, String cardBrand, int currency, String order,
                            String identifierRTS, String rateApplied, String state, String result,
                            String responseCode, String autorizationNumber, String dataCardType,
                            String resVerification, String contTrans, String idApp, String labelApp, String operationDate)
    {
        this.cartType = cartType;
        this.type = type;
        this.card = card;
        this.cardClient = cardClient;
        this.expiration = expiration;
        this.cardBrand = cardBrand;
        this.currency = currency;
        this.order = order;
        this.identifierRTS = identifierRTS;
        this.rateApplied = rateApplied;
        this.state = state;
        this.result = result;
        this.responseCode = responseCode;
        this.autorizationNumber = autorizationNumber;
        this.dataCardType = dataCardType;
        this.resVerification = resVerification;
        this.contTrans = contTrans;
        this.idApp = idApp;
        this.labelApp = labelApp;
        this.operationDate = operationDate;
    }

    public RedCLSTransactionData getTransactionData(double amount, String invoice)
    {
        RedCLSTransactionData redCLSTransactionData = spy(RedCLSTransactionData.class);
        RedCLSDccSelectionData redCLSDccSelectionData = spy(RedCLSDccSelectionData.class);

        switch (cartType)
        {
            case CT_MAGSTRIPE:
                break;

            case CTLS_MAGSTRIPE:
                when(redCLSTransactionData.isContactlessOperation()).thenReturn(Boolean.TRUE);
                break;

            case CT_EMV:
                when(redCLSTransactionData.isEmvOperation()).thenReturn(Boolean.TRUE);
                break;

            case CTLS_EMV:
                when(redCLSTransactionData.isEmvOperation()).thenReturn(Boolean.TRUE);
                when(redCLSTransactionData.isContactlessOperation()).thenReturn(Boolean.TRUE);
                break;
        }

        when(redCLSTransactionData.getType()).thenReturn(type);
        when(redCLSTransactionData.getCard()).thenReturn(card);
        when(redCLSTransactionData.getCardClient()).thenReturn(cardClient);
        when(redCLSTransactionData.getExpiration()).thenReturn(expiration);
        when(redCLSTransactionData.getCardBrand()).thenReturn(cardBrand);
        when(redCLSTransactionData.getAmount()).thenReturn(String.valueOf(amount));
        when(redCLSTransactionData.getCurrency()).thenReturn(currency);
        when(redCLSTransactionData.getOrder()).thenReturn(order);
        when(redCLSTransactionData.getIdentifierRTS()).thenReturn(identifierRTS);
        when(redCLSTransactionData.getOperationDate()).thenReturn(operationDate);
        when(redCLSTransactionData.getRateApplied()).thenReturn(rateApplied);
        when(redCLSTransactionData.getState()).thenReturn(state);
        when(redCLSTransactionData.getResult()).thenReturn(result);
        when(redCLSTransactionData.getResponseCode()).thenReturn(responseCode);
        when(redCLSTransactionData.getAutorizationNumber()).thenReturn(autorizationNumber);
        when(redCLSTransactionData.getCardType()).thenReturn(dataCardType);
        when(redCLSTransactionData.getInvoice()).thenReturn(invoice);
        when(redCLSTransactionData.getEMVData()).thenReturn("");
        when(redCLSTransactionData.getResVerification()).thenReturn(resVerification);
        when(redCLSTransactionData.getContTrans()).thenReturn(contTrans);
        when(redCLSTransactionData.getIdApp()).thenReturn(idApp);
        when(redCLSTransactionData.getLabelApp()).thenReturn(labelApp);
        when(redCLSTransactionData.getArc()).thenReturn("00");
        redCLSDccSelectionData.setOriginalAmount(String.valueOf(amount));
        redCLSDccSelectionData.setOriginalCurrency(String.valueOf(currency));
        when(redCLSTransactionData.getDccSelectionData()).thenReturn(redCLSDccSelectionData);
        return redCLSTransactionData;
    }
    public String getDate(String fullDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {

            Date date = formatter.parse(fullDate);
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            assert date != null;
            return dateFormatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getTime(String fullDate) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try {
            Date date = formatter.parse(fullDate);
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
            assert date != null;
            return timeFormatter.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}