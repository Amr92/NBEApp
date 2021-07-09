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

import es.redsys.paysys.Operative.Managers.RedCLSRefundResponse;
import es.redsys.paysys.Operative.Managers.RedCLSTransactionData;

import static es.redsys.paysys.Utils.RedCLSCifradoUtil.TAG;

public class RefundTicketGeneration {
    private final String TAG = TicketGeneration.class.getSimpleName();
    private Context context;
    private Document document;
    private Element htmlBody;

    public RefundTicketGeneration(Context context) throws IOException
    {
        this.context = context;
        InputStream inputStream = context.getAssets().open("refund_structure.html");
        int size = inputStream.available();
        byte[] buffer = new byte[size];
        inputStream.read(buffer);
        String str = new String(buffer);
        document = Jsoup.parse(str, "UTF-8");
        htmlBody = document.body();
    }

    public void parse(String merchantName, String merchantNumber, String tpv, RedCLSTransactionData redCLSTransactionData, RedCLSRefundResponse redCLSRefundResponse) {

        setMerchantName(merchantName);
        setMerchantNumber(merchantNumber);
        setTPVNumber(tpv);
        setTransactionDate(getDate(redCLSRefundResponse.getOperationDate()));
        setTransactionTime(getTime(redCLSRefundResponse.getOperationDate()));
        setTicketLabel();
        setCardNumber(redCLSTransactionData.getCardClient());
        setAuthorizationCode(redCLSTransactionData.getAutorizationNumber());
        setOperation(redCLSRefundResponse.getOrder());
        setAmount(redCLSRefundResponse.getAmount());
        setCurrencyLabel(CurrencyCodeUtils.getCurrencyString(context, Integer.parseInt(redCLSRefundResponse.getCurrency())));

        // Commented for client future use
//        setTicketClosingMessage();
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
            assert date != null;
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
            assert date != null;
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

    public void setTicketLogo(String srcName){
        //Set Merchant Number Value
        Element ticketLogoElement = htmlBody.getElementById(HtmlIDs.TICKET_LOGO.getId());
        Log.d(TAG, "setTicketLogo: logo path in STL :file:///" + srcName);
        ticketLogoElement.attr("src", "file:///" + srcName);
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
    public void setTicketLabel()
    {
        String ticketLabel = context.getString(R.string.refund);
        Element ticketLabelElement = getHtmlBody().getElementById(HtmlIDs.TICKET_LABEL.getId());
        ticketLabelElement.text(ticketLabel);
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

    public void setTPVNumber(String tpvNumber)
    {
        //Set TPV Label
        Element tpvLabelElement = htmlBody.getElementById(HtmlIDs.TPV_LABEL.getId());
        tpvLabelElement.text(context.getString(R.string.tpv));

        //Set TPV Value
        Element tpvNumberElement = htmlBody.getElementById(HtmlIDs.TPV_NUMBER.getId());
        if (tpvNumber != null)
        {
            tpvNumberElement.text(tpvNumber);
        }
        else
        {
            tpvNumberElement.text("");
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
    public void setAuthorizationCode(String authorizationCode)
    {
        Element authCodeLabelElement = htmlBody.getElementById(HtmlIDs.AUTH_LABEL.getId());
        authCodeLabelElement.text(context.getString(R.string.auth_label));

        Element authorizationCodeElement = htmlBody.getElementById(HtmlIDs.AUTH_NUMBER.getId());
        if (authorizationCode != null)
        {
            authorizationCodeElement.text(authorizationCode);
        }
        else
        {
            authorizationCodeElement.text("");
        }
    }

    public void setAmount(String amount)
    {

        Element amountElement = htmlBody.getElementById(HtmlIDs.CURRENCY_VALUE.getId());
        if (amount != null)
        {
            amountElement.text(amount);
        }
        else
        {
            amountElement.text("");
        }
    }

    public void setOperation(String operation){
        Element operationElement = htmlBody.getElementById(HtmlIDs.OPERATION.getId());
        if (operation != null)
        {
            operationElement.text(operation);
        }
        else
        {
            operationElement.text("");
        }
    }

    public void setCurrencyLabel(String currencyLabel)
    {

        Element currencyElement = htmlBody.getElementById(HtmlIDs.CURRENCY_LABEL.getId());
        if (currencyLabel != null)
        {
            currencyElement.text(currencyLabel);
        }
        else
        {
            currencyElement.text("");
        }
    }

    public String getTicketLogo()
    {
        return htmlBody.getElementById(HtmlIDs.TICKET_LOGO.getId()).attr("src");
    }

    // Commented for client future use
//    private void setTicketClosingMessage()
//    {
//        Element ticketClosingMessageElement = htmlBody.getElementById(HtmlIDs.TICKET_CLOSING_MESSAGE.getId());
//        ticketClosingMessageElement.text(context.getString(R.string.ticket_closing_message));
//    }

    public String getCurrencyLabel(){
        return htmlBody.getElementById(HtmlIDs.CURRENCY_LABEL.getId()).text();
    }

    public String getOperation(){
        return htmlBody.getElementById(HtmlIDs.OPERATION.getId()).text();
    }

    public String getAmount(){
        return htmlBody.getElementById(HtmlIDs.CURRENCY_VALUE.getId()).text();
    }
    public String getAuthorizationCode(){
        return htmlBody.getElementById(HtmlIDs.AUTH_NUMBER.getId()).text();
    }
    public String getCardNumber(){
        return htmlBody.getElementById(HtmlIDs.CARD_NUMBER.getId()).text();
    }
    public String getTPVNumber(){
        return htmlBody.getElementById(HtmlIDs.TPV_NUMBER.getId()).text();
    }
    public String getTransactionTime(){
        return htmlBody.getElementById(HtmlIDs.TRANSACTION_TIME.getId()).text();
    }
    public String getTransactionDate(){
        return htmlBody.getElementById(HtmlIDs.TRANSACTION_DATE.getId()).text();
    }
    public String getMerchantNumber(){
        return htmlBody.getElementById(HtmlIDs.MERCHANT_NUMBER.getId()).text();
    }
    public String getTicketLabel(){
        return htmlBody.getElementById(HtmlIDs.TICKET_LABEL.getId()).text();
    }
    public String getMerchantName(){
        return htmlBody.getElementById(HtmlIDs.MERCHANT_NAME.getId()).text();
    }

    // Commented for client future use
//    private String getTicketClosingMessage()
//    {
//        return htmlBody.getElementById(HtmlIDs.TICKET_CLOSING_MESSAGE.getId()).text();
//    }
}
