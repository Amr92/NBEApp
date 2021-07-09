package com.yelloco.ticketlibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.izettle.html2bitmap.Html2Bitmap;
import com.izettle.html2bitmap.content.WebViewContent;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import es.redsys.paysys.Operative.Managers.RedCLSConfirmationResponse;
import es.redsys.paysys.Operative.Managers.RedCLSTransactionData;

import static es.redsys.paysys.Utils.RedCLSCifradoUtil.TAG;

public class PreAuthorizationConfirmationTicket
{
    private final String TAG = TicketGeneration.class.getSimpleName();
    private Context context;
    private Document document;
    private Element htmlBody;

    public PreAuthorizationConfirmationTicket(Context context) throws IOException
    {
        this.context = context;
        InputStream inputStream = context.getAssets().open("confirmation_of_pre-auth_ticket.html");
        int size = inputStream.available();
        byte[] buffer = new byte[size];
        inputStream.read(buffer);
        String str = new String(buffer);
        document = Jsoup.parse(str, "UTF-8");
        htmlBody = document.body();
    }

    public void parse(TicketCopy ticketCopy, String merchantName, String merchantNumber, String tpv,
                      RedCLSTransactionData redCLSTransactionData, RedCLSConfirmationResponse redCLSConfirmationResponse)
    {
        setTicketVersion(context.getString(ticketCopy.getResId()));
        setMerchantName(merchantName);
        setMerchantNumber(merchantNumber);
        setTPV(tpv);
        setCardNumber(redCLSTransactionData.getCard());
        setOperationType(context.getString(OperationType.CONFIRMATION.getResId()));
        setOriginalOrder(redCLSConfirmationResponse.getBaseOrder());
        setTransactionDate(getDate(redCLSConfirmationResponse.getOperationDate()));
        setTransactionTime(getTime(redCLSConfirmationResponse.getOperationDate()));
        setOrderNumber(redCLSConfirmationResponse.getOrder());
        setAmount(redCLSConfirmationResponse.getAmount());
        setCurrency(CurrencyCodeUtils.getCurrencyString(context, Integer.parseInt(redCLSConfirmationResponse.getCurrency())));
        setReference(redCLSTransactionData.getInvoice());
        // Commented for client future use
//        setTicketClosingMessage();

        if (redCLSTransactionData.isContactlessOperation())
        {
            displayCtlsLogo();
        }
    }

    public Document getDocument() {
        return document;
    }

    public String getHTML()
    {
        return document.html();
    }

    public Bitmap getBitmap() {
        return new Html2Bitmap.Builder().setContext(context)
                .setContent(WebViewContent.html(getHTML()))
                .build()
                .getBitmap();
    }

    private String getDate(String fullDate)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try
        {

            Date date = formatter.parse(fullDate);
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            return dateFormatter.format(date);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private String getTime(String fullDate)
    {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        try
        {
            Date date = formatter.parse(fullDate);
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return timeFormatter.format(date);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public Element getHtmlBody()
    {
        return htmlBody;
    }

    public void displayCtlsLogo()
    {
        Element ctlsLogoElement = htmlBody.getElementById(HtmlIDs.CTLS_LOGO.getId());
        ctlsLogoElement.removeAttr("hidden");
    }

    public void setTicketLogo(String srcName){
        //Set Merchant Number Value
        Element ticketLogoElement = htmlBody.getElementById(HtmlIDs.TICKET_LOGO.getId());
        Log.d(TAG, "setTicketLogo: logo path in STL :file:///" + srcName);
        ticketLogoElement.attr("src", "file:///" + srcName);
    }

    //region Setters
    public void setTicketVersion(String ticketVersion)
    {
        Element ticketVersionElement = htmlBody.getElementById(HtmlIDs.CUSTOMER_OR_MERCHANT_VERSION.getId());
        if (ticketVersion != null)
        {
            ticketVersionElement.text(ticketVersion);
        }
        else
        {
            ticketVersionElement.text("");
        }
    }

    public void setMerchantName(String merchantName)
    {
        Element merchantNameElement = htmlBody.getElementById(HtmlIDs.MERCHANT_NAME.getId());
        if (merchantName != null)
        {
            merchantNameElement.text(merchantName);
        }
        else
        {
            merchantNameElement.text("");
        }
    }

    public void setMerchantNumber(String merchantNumber)
    {
        //Set Merchant Number Label
        Element merchantNumberLabelElement = htmlBody.getElementById(HtmlIDs.MERCHANT_LABEL.getId());
        merchantNumberLabelElement.text(context.getString(R.string.merchant));

        //Set Merchant Number Value
        Element merchantNumberElement = htmlBody.getElementById(HtmlIDs.MERCHANT_NUMBER.getId());
        if (merchantNumber != null)
        {
            merchantNumberElement.text(merchantNumber);
        }
        else
        {
            merchantNumberElement.text("");
        }
    }

    public void setTPV(String tpv)
    {
        //Set TPV Label
        Element tpvLabelElement = htmlBody.getElementById(HtmlIDs.TPV_LABEL.getId());
        tpvLabelElement.text(context.getString(R.string.tpv));

        //Set TPV Value
        Element tpvElement = htmlBody.getElementById(HtmlIDs.TPV.getId());
        if (tpv != null)
        {
            tpvElement.text(tpv);
        }
        else
        {
            tpvElement.text("");
        }
    }

    public void setCardNumber(String cardNumber)
    {
        Element cardNumberElement = htmlBody.getElementById(HtmlIDs.CARD_NUMBER.getId());
        if (cardNumber != null)
        {
            cardNumberElement.text(cardNumber);
        }
        else
        {
            cardNumberElement.text("");
        }
    }

    public void setOperationType(String operationType)
    {
        Element operationTypeElement = htmlBody.getElementById(HtmlIDs.OPERATION_TYPE.getId());
        if (operationType != null)
        {
            operationTypeElement.text(operationType);
        }
        else
        {
            operationTypeElement.text("");
        }
    }

    public void setOriginalOrder(String originalOrder)
    {
        //Set Original Order Label
        Element originalOrderLabelElement = htmlBody.getElementById(HtmlIDs.ORIGINAL_ORDER_LABEL.getId());
        originalOrderLabelElement.text(context.getString(R.string.original_order));

        //Set Order Value
        Element originalOrderElement = htmlBody.getElementById(HtmlIDs.ORIGINAL_ORDER.getId());
        if (originalOrder != null)
        {
            originalOrderElement.text(originalOrder);
        }
        else
        {
            originalOrderElement.text("");
        }
    }

    public void setTransactionDate(String date)
    {
        //Set Date Label
        Element dateLabelElement = htmlBody.getElementById(HtmlIDs.DATE_LABEL.getId());
        dateLabelElement.text(context.getString(R.string.date));

        //Set Date Value
        Element dateElement = htmlBody.getElementById(HtmlIDs.TRANSACTION_DATE.getId());
        if (date != null)
        {
            dateElement.text(date);
        }
        else
        {
            dateElement.text("");
        }
    }

    public void setTransactionTime(String time)
    {
        //Set Time Label
        Element timeLabelElement = htmlBody.getElementById(HtmlIDs.TIME_LABEL.getId());
        timeLabelElement.text(context.getString(R.string.time));

        //Set Time Value
        Element timeElement = htmlBody.getElementById(HtmlIDs.TRANSACTION_TIME.getId());
        if (time != null)
        {
            timeElement.text(time);
        }
        else
        {
            timeElement.text("");
        }
    }

    public void setOrderNumber(String orderNumber)
    {
        //Set Order Label
        Element orderLabelElement = htmlBody.getElementById(HtmlIDs.ORDER_LABEL.getId());
        orderLabelElement.text(context.getString(R.string.op));

        //Set Order Number Value
        Element orderNumberElement = htmlBody.getElementById(HtmlIDs.ORDER_NUMBER.getId());
        if (orderNumber != null)
        {
            orderNumberElement.text(orderNumber);
        }
        else
        {
            orderNumberElement.text("");
        }
    }

    public void setAmount(String amount)
    {
        Element amountElement = htmlBody.getElementById(HtmlIDs.TRANSACTION_AMOUNT.getId());
        if (amount != null)
        {
            amountElement.text(amount);
        }
        else
        {
            amountElement.text("");
        }
    }

    public void setCurrency(String currency)
    {
        Element currencyElement = htmlBody.getElementById(HtmlIDs.CURRENCY.getId());
        if (currency != null)
        {
            currencyElement.text(currency);
        }
        else
        {
            currencyElement.text("");
        }
    }

    public void setReference(String reference)
    {
        if (reference == null)
        {
            return;
        }

        //Set Reference Label
        Element referenceLabelElement = htmlBody.getElementById(HtmlIDs.REFERENCE_LABEL.getId());
        referenceLabelElement.text(context.getString(R.string.reference_));

        //Set Reference Value
        Element referenceElement = htmlBody.getElementById(HtmlIDs.REFERENCE.getId());
        referenceElement.text(reference);
    }

    // Commented for client future use
//    private void setTicketClosingMessage()
//    {
//        Element ticketClosingMessageElement = htmlBody.getElementById(HtmlIDs.TICKET_CLOSING_MESSAGE.getId());
//        ticketClosingMessageElement.text(context.getString(R.string.ticket_closing_message));
//    }

    //endregion Setters

    //region Getters
    public String getTicketLogo()
    {
        return htmlBody.getElementById(HtmlIDs.TICKET_LOGO.getId()).attr("src");
    }

    public String getTicketVersion()
    {
        return htmlBody.getElementById(HtmlIDs.CUSTOMER_OR_MERCHANT_VERSION.getId()).text();
    }

    public String getMerchantName()
    {
        return htmlBody.getElementById(HtmlIDs.MERCHANT_NAME.getId()).text();
    }

    public String getMerchantNumber()
    {
        //Set Merchant Number Value
        return htmlBody.getElementById(HtmlIDs.MERCHANT_NUMBER.getId()).text();
    }

    public String getTPV()
    {
        return htmlBody.getElementById(HtmlIDs.TPV.getId()).text();
    }

    public String getCardNumber()
    {
        return htmlBody.getElementById(HtmlIDs.CARD_NUMBER.getId()).text();
    }

    public String getOperationType()
    {
        return htmlBody.getElementById(HtmlIDs.OPERATION_TYPE.getId()).text();
    }

    public String getOriginalOrder()
    {
        return htmlBody.getElementById(HtmlIDs.ORIGINAL_ORDER.getId()).text();
    }

    public String getTransactionDate()
    {
        return htmlBody.getElementById(HtmlIDs.TRANSACTION_DATE.getId()).text();
    }

    public String getTransactionTime()
    {
        return htmlBody.getElementById(HtmlIDs.TRANSACTION_TIME.getId()).text();
    }

    public String getOrderNumber()
    {
        return htmlBody.getElementById(HtmlIDs.ORDER_NUMBER.getId()).text();
    }

    public String getAmount()
    {
        return htmlBody.getElementById(HtmlIDs.TRANSACTION_AMOUNT.getId()).text();
    }

    public String getCurrency()
    {
        return htmlBody.getElementById(HtmlIDs.CURRENCY.getId()).text();
    }

    public String getReference()
    {
        return htmlBody.getElementById(HtmlIDs.REFERENCE.getId()).text();
    }

    // Commented for client future use
//    private String getTicketClosingMessage()
//    {
//        return htmlBody.getElementById(HtmlIDs.TICKET_CLOSING_MESSAGE.getId()).text();
//    }

    //endregion Setters
}