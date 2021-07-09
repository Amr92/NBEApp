package com.yelloco.ticketlibrary;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

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

import es.redsys.paysys.Operative.Managers.RedCLSTotalsQueryResponse;

import static es.redsys.paysys.Utils.RedCLSCifradoUtil.TAG;

public class TotalsTicketGenaration {
    private final String TAG = TicketGeneration.class.getSimpleName();
    private Context context;
    private Document document;
    private Element htmlBody;

    public TotalsTicketGenaration(Context context) throws IOException
    {
        this.context = context;
        InputStream inputStream = context.getAssets().open("totals_query_structure.html");
        int size = inputStream.available();
        byte[] buffer = new byte[size];
        inputStream.read(buffer);
        String str = new String(buffer);
        document = Jsoup.parse(str, "UTF-8");
        htmlBody = document.body();
    }

    public void parse(String merchantName, String merchantNumber, String tpv, String requestDate, RedCLSTotalsQueryResponse redCLSTotalsQueryResponse)
    {
        setMerchantName(merchantName);
        setMerchantNumber(merchantNumber);
        setTPVNumber(tpv);
        setTransactionDate(getDate(requestDate));
        setTransactionTime(getTime(requestDate));
        setTicketLabel();
        setAuthorizationAmount(String.valueOf(redCLSTotalsQueryResponse.getAuthorizations().getAmountAuthorizations()));
        setAuthorizationNumOfOperations(String.valueOf(redCLSTotalsQueryResponse.getAuthorizations().getAuthorizedTransactions()));
        setPreAuthorizationAmount(String.valueOf(redCLSTotalsQueryResponse.getPreauthorizations().getAmountAuthorizations()));
        setPreAuthorizationNumOfOperations(String.valueOf(redCLSTotalsQueryResponse.getPreauthorizations().getAuthorizedTransactions()));
        setRefundsAmount(String.valueOf(redCLSTotalsQueryResponse.getRefunds().getAmountAuthorizations()));
        setRefundsNumOfOperations(String.valueOf(redCLSTotalsQueryResponse.getRefunds().getAuthorizedTransactions()));
        setDeniedAmount("-");
        setDeniedNumOfOperations(String.valueOf(redCLSTotalsQueryResponse.getAuthorizations().getRefusalsTransactions()));
        setConfirmationsAmount(String.valueOf(redCLSTotalsQueryResponse.getConfirmations().getAmountAuthorizations()));
        setConfirmationsNumOfOperations(String.valueOf(redCLSTotalsQueryResponse.getConfirmations().getAuthorizedTransactions()));
        setCurrencyLabel(CurrencyCodeUtils.getCurrencyString(context, Integer.parseInt(redCLSTotalsQueryResponse.getCurrency())));
        setTotalsAmount(calcTotalsAmount(redCLSTotalsQueryResponse));
        setTotalsNumOfOperations(calcTotalsNumOfOperation(redCLSTotalsQueryResponse));
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

    public void setMerchantNumber(String merchantNumber)
    {
        //Set Merchant Number Label
        Element merchantLabelNumberElement = htmlBody.getElementById(HtmlIDs.MERCHANT_NUMBER_LABEL.getId());
        merchantLabelNumberElement.text(context.getString(R.string.merchant));

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

    private void setTicketLabel() {
        //Set Operation type
        Element totalsQueryElement = htmlBody.getElementById(HtmlIDs.TICKET_LABEL.getId());
        totalsQueryElement.text(context.getString(R.string.totals_query));
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

    public void setENTNumber(String entNumber)
    {
        //Set ENT Label
        Element entLabelElement = htmlBody.getElementById(HtmlIDs.ENT_LABEL.getId());
        entLabelElement.text(context.getString(R.string.ent));

        //Set ENT Value
        Element entNumberElement = htmlBody.getElementById(HtmlIDs.ENT_NUMBER.getId());
        if (entNumber != null)
        {
            entNumberElement.text(entNumber);
        }
        else
        {
            entNumberElement.text("");
        }
    }

    public void setAuthorizationAmount(String authorizationAmount)
    {
        Element authLabelElement = htmlBody.getElementById(HtmlIDs.AUTH_LABEL.getId());
        authLabelElement.text(context.getString(R.string.auth));

        //Set Authorization Amount Value
        Element authorizationAmountElement = htmlBody.getElementById(HtmlIDs.AUTHORIZATIONS_AMOUNT.getId());
        if (authorizationAmount != null)
        {
            authorizationAmountElement.text(authorizationAmount);
        }
        else
        {
            authorizationAmountElement.text("");
        }
    }

    public void setAuthorizationNumOfOperations(String authorizationNumOfOperations)
    {

        //Set Authorization Amount Value
        Element authorizationNumOfOperationsElement = htmlBody.getElementById(HtmlIDs.AUTHORIZATIONS_NUM_OF_OPERATIONS.getId());
        if (authorizationNumOfOperations != null)
        {
            authorizationNumOfOperationsElement.text(authorizationNumOfOperations);
        }
        else
        {
            authorizationNumOfOperationsElement.text("");
        }
    }

    public void setPreAuthorizationAmount(String preAuthorizationAmount)
    {
        Element preauthLabelElement = htmlBody.getElementById(HtmlIDs.PREAUTH_LABEL.getId());
        preauthLabelElement.text(context.getString(R.string.pre_auth_label));

        //Set PreAuthorization Amount Value
        Element preAuthorizationAmountElement = htmlBody.getElementById(HtmlIDs.PRE_AUTHORIZATIONS_AMOUNT.getId());
        if (preAuthorizationAmount != null)
        {
            preAuthorizationAmountElement.text(preAuthorizationAmount);
        }
        else
        {
            preAuthorizationAmountElement.text("");
        }
    }
    public void setPreAuthorizationNumOfOperations(String preAuthorizationNumOfOperations)
    {
        //Set PreAuthorization Amount Value
        Element preAuthorizationNumOfOperationsElement = htmlBody.getElementById(HtmlIDs.PRE_AUTHORIZATIONS_NUM_OF_OPERATIONS.getId());
        if (preAuthorizationNumOfOperations != null)
        {
            preAuthorizationNumOfOperationsElement.text(preAuthorizationNumOfOperations);
        }
        else
        {
            preAuthorizationNumOfOperationsElement.text("");
        }
    }

    public void setRefundsAmount(String refundsAmount)
    {

        Element refundLabelElement = htmlBody.getElementById(HtmlIDs.REFUND_LABEL.getId());
        refundLabelElement.text(context.getString(R.string.refund_label));

        //Set Refunds Amount Value
        Element refundsAmountElement = htmlBody.getElementById(HtmlIDs.REFUNDS_AMOUNT.getId());
        if (refundsAmount != null)
        {
            refundsAmountElement.text(refundsAmount);
        }
        else
        {
            refundsAmountElement.text("");
        }
    }
    public void setRefundsNumOfOperations(String refundsNumOfOperations)
    {
        //Set Refunds Amount Value
        Element refundsNumOfOperationsElement = htmlBody.getElementById(HtmlIDs.REFUNDS_NUM_OF_OPERATIONS.getId());
        if (refundsNumOfOperations != null)
        {
            refundsNumOfOperationsElement.text(refundsNumOfOperations);
        }
        else
        {
            refundsNumOfOperationsElement.text("");
        }
    }

    public void setConfirmationsAmount(String confirmationsAmount)
    {
        Element confirmationLabelElement = htmlBody.getElementById(HtmlIDs.CONFIRMATIONS_LABEL.getId());
        confirmationLabelElement.text(context.getString(R.string.confirmations_label));


        //Set Confirmations Amount Value
        Element confirmationsAmountElement = htmlBody.getElementById(HtmlIDs.CONFIRMATIONS_AMOUNT.getId());
        if (confirmationsAmount != null)
        {
            confirmationsAmountElement.text(confirmationsAmount);
        }
        else
        {
            confirmationsAmountElement.text("");
        }
    }
    public void setConfirmationsNumOfOperations(String confirmationsNumOfOperations)
    {
        //Set Confirmations Amount Value
        Element confirmationsNumOfOperationsElement = htmlBody.getElementById(HtmlIDs.CONFIRMATIONS_NUM_OF_OPERATIONS.getId());
        if (confirmationsNumOfOperations != null)
        {
            confirmationsNumOfOperationsElement.text(confirmationsNumOfOperations);
        }
        else
        {
            confirmationsNumOfOperationsElement.text("");
        }
    }

    public void setDeniedAmount(String deniedAmount)
    {
        Element deniedLabelElement = htmlBody.getElementById(HtmlIDs.DENIED_LABEL.getId());
        deniedLabelElement.text(context.getString(R.string.denied_label));


        //Set Confirmations Amount Value
        Element deniedAmountElement = htmlBody.getElementById(HtmlIDs.DENIED_AMOUNT.getId());
        if (deniedAmount != null)
        {
            deniedAmountElement.text(deniedAmount);
        }
        else
        {
            deniedAmountElement.text("");
        }
    }
    public void setDeniedNumOfOperations(String deniedNumOfOperations)
    {
        //Set Confirmations Amount Value
        Element deniedNumOfOperationsElement = htmlBody.getElementById(HtmlIDs.DENIED_NUM_OF_OPERATIONS.getId());
        if (deniedNumOfOperations != null)
        {
            deniedNumOfOperationsElement.text(deniedNumOfOperations);
        }
        else
        {
            deniedNumOfOperationsElement.text("");
        }
    }

    public String calcTotalsAmount(RedCLSTotalsQueryResponse redCLSTotalsQueryResponse){
        double totalAmount;
        totalAmount = redCLSTotalsQueryResponse.getAuthorizations().getAmountAuthorizations() +
                redCLSTotalsQueryResponse.getPreauthorizations().getAmountAuthorizations() +
                redCLSTotalsQueryResponse.getRefunds().getAmountAuthorizations() +
                redCLSTotalsQueryResponse.getConfirmations().getAmountAuthorizations();
        return String.valueOf(totalAmount);
    }

    public void setCurrencyLabel(String currencyLabel){
        //Set currency Label
        Element currencyLabelElement = htmlBody.getElementById(HtmlIDs.CURRENCY_LABEL_OPERATIONS.getId());
        currencyLabelElement.text(currencyLabel);
    }

    public void setTotalsAmount(String totalsAmount)
    {
        //Set Confirmations Amount Value
        Element totalsAmountElement = htmlBody.getElementById(HtmlIDs.TOTALS_AMOUNT.getId());
        if (totalsAmount != null)
        {
            totalsAmountElement.text(totalsAmount);
        }
        else
        {
            totalsAmountElement.text("");
        }
    }

    public String calcTotalsNumOfOperation(RedCLSTotalsQueryResponse redCLSTotalsQueryResponse){
        int totalNumOfOperations;
        totalNumOfOperations = redCLSTotalsQueryResponse.getAuthorizations().getAuthorizedTransactions() +
                redCLSTotalsQueryResponse.getPreauthorizations().getAuthorizedTransactions() +
                redCLSTotalsQueryResponse.getAuthorizations().getRefusalsTransactions() +
                redCLSTotalsQueryResponse.getRefunds().getAuthorizedTransactions() +
                redCLSTotalsQueryResponse.getConfirmations().getAuthorizedTransactions();
        return String.valueOf(totalNumOfOperations);
    }

    public void setTotalsNumOfOperations(String totalsNumOfOperations)
    {
        //Set Confirmations Amount Value
        Element totalsNumOfOperationsElement = htmlBody.getElementById(HtmlIDs.TOTALS_NUM_OF_OPERATIONS.getId());
        if (totalsNumOfOperations != null)
        {
            totalsNumOfOperationsElement.text(totalsNumOfOperations);
        }
        else
        {
            totalsNumOfOperationsElement.text("");
        }
    }

    // Commented for client future use
//    private void setTicketClosingMessage()
//    {
//        Element ticketClosingMessageElement = htmlBody.getElementById(HtmlIDs.TICKET_CLOSING_MESSAGE.getId());
//        ticketClosingMessageElement.text(context.getString(R.string.ticket_closing_message));
//    }

    public String getTicketLogo()
    {
        return htmlBody.getElementById(HtmlIDs.TICKET_LOGO.getId()).attr("src");
    }

    public String getMerchantName(){
        return htmlBody.getElementById(HtmlIDs.MERCHANT_NAME.getId()).text();
    }

    public String getMerchantNumber(){
        return htmlBody.getElementById(HtmlIDs.MERCHANT_NUMBER.getId()).text();
    }
    public String getTPVNumber(){
        return htmlBody.getElementById(HtmlIDs.TPV_NUMBER.getId()).text();
    }
    public String getENTNumber(){
        return htmlBody.getElementById(HtmlIDs.ENT_NUMBER.getId()).text();
    }
    public String getTicketLabel(){
        return htmlBody.getElementById(HtmlIDs.TICKET_LABEL.getId()).text();
    }
    public String getTransactionDate(){
        return htmlBody.getElementById(HtmlIDs.TRANSACTION_DATE.getId()).text();
    }
    public String getTransactionTime(){
        return htmlBody.getElementById(HtmlIDs.TRANSACTION_TIME.getId()).text();
    }
    public String getAuthAmount(){
        return htmlBody.getElementById(HtmlIDs.AUTHORIZATIONS_AMOUNT.getId()).text();
    }
    public String getAuthNumOfOp(){
        return htmlBody.getElementById(HtmlIDs.AUTHORIZATIONS_NUM_OF_OPERATIONS.getId()).text();
    }
    public String getPreAuthorizationAmount(){
        return htmlBody.getElementById(HtmlIDs.PRE_AUTHORIZATIONS_AMOUNT.getId()).text();
    }
    public String getPreAuthorizationNumOfOperations(){
        return htmlBody.getElementById(HtmlIDs.PRE_AUTHORIZATIONS_NUM_OF_OPERATIONS.getId()).text();
    }
    public String getRefundsAmount(){
        return htmlBody.getElementById(HtmlIDs.REFUNDS_AMOUNT.getId()).text();
    }
    public String getRefundsNumOfOperations(){
        return htmlBody.getElementById(HtmlIDs.REFUNDS_NUM_OF_OPERATIONS.getId()).text();
    }
    public String getConfirmationsAmount(){
        return htmlBody.getElementById(HtmlIDs.CONFIRMATIONS_AMOUNT.getId()).text();
    }
    public String getConfirmationsNumOfOperations(){
        return htmlBody.getElementById(HtmlIDs.CONFIRMATIONS_NUM_OF_OPERATIONS.getId()).text();
    }
    public String getTotalAmount(){
        return htmlBody.getElementById(HtmlIDs.TOTALS_AMOUNT.getId()).text();
    }
    public String getTotalNumOfOperations(){
        return htmlBody.getElementById(HtmlIDs.TOTALS_NUM_OF_OPERATIONS.getId()).text();
    }
    public String getCurrency(){
        return htmlBody.getElementById(HtmlIDs.CURRENCY_LABEL.getId()).text();
    }

    // Commented for client future use
//    private String getTicketClosingMessage()
//    {
//        return htmlBody.getElementById(HtmlIDs.TICKET_CLOSING_MESSAGE.getId()).text();
//    }

    @NonNull
    @Override
    public String toString()
    {
        return getHTML();
    }

}
