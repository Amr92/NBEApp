package com.yelloco.ticketlibrary;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import com.izettle.html2bitmap.Html2Bitmap;
import com.izettle.html2bitmap.content.WebViewContent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.redsys.paysys.Operative.Managers.RedCLSTransactionData;

import static es.redsys.paysys.Utils.RedCLSCifradoUtil.TAG;

public class OperationListTicketGeneration {
    
    private Context context;
    private Document document;
    private Element htmlBody;
    private Element singleOperationHtmlBody;
    private List<OperationListValues> operationListValues;
    private int numOfOperations = 10;

    public OperationListTicketGeneration(Context context) throws IOException {
        this.context = context;
        String singleOperationHtml = readHtmlFromAssets("single_operation_structure.html");
        String fullHtml = readHtmlFromAssets("operation_list_structure.html");

        Document singleOperationDocument = Jsoup.parse(singleOperationHtml, "UTF-8");
        singleOperationHtmlBody = singleOperationDocument.body();

        document = Jsoup.parse(fullHtml, "UTF-8");
        htmlBody = document.body();
    }

    private String readHtmlFromAssets(String fileName) throws IOException{
        InputStream inputStream = context.getAssets().open(fileName);
        int size = inputStream.available();
        byte[] buffer = new byte[size];
        int bufferSize = inputStream.read(buffer);
        return new String(buffer);
    }

    public void parse(String merchantName, String merchantNumber, String tpv, String requestDate, String fromDate, String toDate,
                      String currentPage, String totalPages, List<RedCLSTransactionData> redCLSTransactionData) {
        setMerchantName(merchantName);
        setMerchantNumber(merchantNumber);
        setTPVNumber(tpv);
        setTransactionDate(getDate(requestDate));
        setTransactionTime(getTime(requestDate));
        setTicketLabel();
        setFromDate(getDate(fromDate));
        setToDate(getDate(toDate));
        if (redCLSTransactionData.size() < numOfOperations)
            numOfOperations = redCLSTransactionData.size();

        operationListValues = new ArrayList<>();
        for (int i = 0; i < numOfOperations; i++) {
            OperationListValues operationValue = new OperationListValues();
            operationValue.setOperationDate(getDate(redCLSTransactionData.get(i).getOperationDate()));
            operationValue.setOperationTime(getTime(redCLSTransactionData.get(i).getOperationDate()));
            operationValue.setOrderId(redCLSTransactionData.get(i).getOrder());
            operationValue.setOperationType(redCLSTransactionData.get(i).getType());
            operationValue.setOperationResult(redCLSTransactionData.get(i).getResult());
            operationValue.setAmount(redCLSTransactionData.get(i).getAmount());
            operationValue.setCurrency(CurrencyCodeUtils.getCurrencyString(context, redCLSTransactionData.get(i).getCurrency()));
            operationValue.setCardNumber(redCLSTransactionData.get(i).getCardClient());
            operationValue.setAuthNumber(redCLSTransactionData.get(i).getAutorizationNumber());
            operationValue.setReference(redCLSTransactionData.get(i).getReferenciaOperacion());

            addOperationToList(operationValue);
        }
        setCurrentPage(currentPage);
        setTotalPages(totalPages);
        // Commented for client future use
//        setTicketClosingMessage();
    }

    public Document getDocument() {
        return document;
    }

    public String getHTML() {
        return document.html();
    }

    public Bitmap getBitmap() {
        return new Html2Bitmap.Builder().setContext(context)
                .setContent(WebViewContent.html(getHTML()))
                .build()
                .getBitmap();
    }

    private String getDate(String fullDate) {
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

    private String getTime(String fullDate) {

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

    public Element getHtmlBody() {
        return htmlBody;
    }

    public void setMerchantName(String merchantName) {
        Element merchantNameElement = htmlBody.getElementById(HtmlIDs.MERCHANT_NAME.getId());
        if (merchantName != null) {
            merchantNameElement.text(merchantName);
        } else {
            merchantNameElement.text("");
        }
    }

    public void setTicketLabel() {
        String ticketLabel = context.getString(R.string.operation_list);
        Element ticketLabelElement = getHtmlBody().getElementById(HtmlIDs.TICKET_LABEL.getId());
        ticketLabelElement.text(ticketLabel);
    }

    public void setMerchantNumber(String merchantNumber) {
        //Set Merchant Number Label
        Element merchantNumberLabelElement = htmlBody.getElementById(HtmlIDs.MERCHANT_LABEL.getId());
        merchantNumberLabelElement.text(context.getString(R.string.merchant));

        //Set Merchant Number Value
        Element merchantNumberElement = htmlBody.getElementById(HtmlIDs.MERCHANT_NUMBER.getId());
        if (merchantNumber != null) {
            merchantNumberElement.text(merchantNumber);
        } else {
            merchantNumberElement.text("");
        }
    }

    public void setTicketLogo(String srcName){
        //Set Merchant Number Value
        Element ticketLogoElement = htmlBody.getElementById(HtmlIDs.TICKET_LOGO.getId());
        Log.d(TAG, "setTicketLogo: logo path in STL :file:///" + srcName);
        ticketLogoElement.attr("src", "file:///" + srcName);
    }

    public void setTransactionDate(String date) {
        //Set Date Label
        Element dateLabelElement = htmlBody.getElementById(HtmlIDs.DATE_LABEL.getId());
        dateLabelElement.text(context.getString(R.string.date));

        //Set Date Value
        Element dateElement = htmlBody.getElementById(HtmlIDs.TRANSACTION_DATE.getId());
        if (date != null) {
            dateElement.text(date);
        } else {
            dateElement.text("");
        }
    }

    public void setFromDate(String fromDate) {
        //Set Date Label
        Element fromDateLabelElement = htmlBody.getElementById(HtmlIDs.FROM_DATE_LABEL.getId());
        fromDateLabelElement.text(context.getString(R.string.from_date));

        Element fromDateElement = htmlBody.getElementById(HtmlIDs.FROM_DATE.getId());
        if (fromDate != null) {
            fromDateElement.text(fromDate);
        } else {
            fromDateElement.text("");
        }
    }

    public void setToDate(String toDate) {
        //Set Date Label
        Element toDateLabelElement = htmlBody.getElementById(HtmlIDs.TO_DATE_LABEL.getId());
        toDateLabelElement.text(context.getString(R.string.to_date_label));

        Element toDateElement = htmlBody.getElementById(HtmlIDs.TO_DATE.getId());
        if (toDate != null) {
            toDateElement.text(toDate);
        } else {
            toDateElement.text("");
        }
    }

    public void setCurrentPage(String currentPage) {
        Element currentPageElement = htmlBody.getElementById(HtmlIDs.CURRENT_PAGE.getId());
        if (currentPage != null) {
            currentPageElement.text(currentPage);
        } else {
            currentPageElement.text("");
        }
    }

    public void setTotalPages(String totalPages) {
        Element totalPagesElement = htmlBody.getElementById(HtmlIDs.TOTAL_PAGES.getId());
        if (totalPages != null) {
            totalPagesElement.text(totalPages);
        } else {
            totalPagesElement.text("");
        }
    }

    public void setTransactionTime(String time) {
        //Set Time Label
        Element timeLabelElement = htmlBody.getElementById(HtmlIDs.TIME_LABEL.getId());
        timeLabelElement.text(context.getString(R.string.time));

        //Set Time Value
        Element timeElement = htmlBody.getElementById(HtmlIDs.TRANSACTION_TIME.getId());
        if (time != null) {
            timeElement.text(time);
        } else {
            timeElement.text("");
        }
    }

    public void setTPVNumber(String tpvNumber) {
        //Set TPV Label
        Element tpvLabelElement = htmlBody.getElementById(HtmlIDs.TPV_LABEL.getId());
        tpvLabelElement.text(context.getString(R.string.tpv));

        //Set TPV Value
        Element tpvNumberElement = htmlBody.getElementById(HtmlIDs.TPV_NUMBER.getId());
        if (tpvNumber != null) {
            tpvNumberElement.text(tpvNumber);
        } else {
            tpvNumberElement.text("");
        }
    }

    public void setOperationDate(String operationDate) {
        //Set operation date Value
        Element operationDateElement = singleOperationHtmlBody.getElementById(HtmlIDs.OPERATION_DATE.getId());
        if (operationDate != null) {
            operationDateElement.text(operationDate);
        } else {
            operationDateElement.text("");
        }
    }

    public void setOperationTime(String operationTime) {
        Element operationTimeElement = singleOperationHtmlBody.getElementById(HtmlIDs.OPERATION_TIME.getId());
        if (operationTime != null) {
            operationTimeElement.text(operationTime);
        } else {
            operationTimeElement.text("");
        }
    }

    public void setOrderId(String orderId) {
        Element orderIdElement = singleOperationHtmlBody.getElementById(HtmlIDs.ORDER_ID.getId());
        if (orderId != null) {
            orderIdElement.text(orderId);
        } else {
            orderIdElement.text("");
        }
    }

    public void setOperationType(String operationType) {
        Element operationTypeElement = singleOperationHtmlBody.getElementById(HtmlIDs.SINGLE_OPERATION_TYPE.getId());
        if (operationType != null) {
            if (operationType.equals(OperationResult.AUTH.getResponse()))
                operationTypeElement.text(context.getString(OperationResult.AUTH.getResultForTicket()));
            if (operationType.equals(OperationResult.PREAUTH.getResponse()))
                operationTypeElement.text(context.getString(OperationResult.PREAUTH.getResultForTicket()));
            if (operationType.equals(OperationResult.REFUND.getResponse()))
                operationTypeElement.text(context.getString(OperationResult.REFUND.getResultForTicket()));
            if (operationType.equals(OperationResult.CONF.getResponse()))
                operationTypeElement.text(context.getString(OperationResult.CONFIRMATION.getResultForTicket()));

        } else {
            operationTypeElement.text("");
        }
    }

    public void setOperationResult(String operationResult) {
        Element operationResultElement = singleOperationHtmlBody.getElementById(HtmlIDs.OPERATION_RESULT.getId());
        if (operationResult != null) {
            operationResultElement.text(operationResult);
            if (operationResult.equals(OperationResult.AUTORIZADA.getResponse()))
                operationResultElement.text(context.getString(R.string.authorized));
            else if (operationResult.equals(OperationResult.DENEGADA.getResponse()))
                operationResultElement.text(context.getString(R.string.declined));
        } else {
            operationResultElement.text("");
        }
    }

    public void setAmount(String amount) {
        Element amountElement = singleOperationHtmlBody.getElementById(HtmlIDs.AMOUNT.getId());
        if (amount != null) {
            amountElement.text(amount);
        } else {
            amountElement.text("");
        }
    }

    public void setCurrency(String currency) {
        Element currencyElement = singleOperationHtmlBody.getElementById(HtmlIDs.CURRENCY_FOR_SINGLE_OPERATION.getId());
        if (currency != null) {
            currencyElement.text(currency);
        } else {
            currencyElement.text("");
        }
    }

    public void setCardNumber(String cardNumber) {
        Element cardNumberElement = singleOperationHtmlBody.getElementById(HtmlIDs.CARD_NUMBER_FOR_SINGLE_OPERATION.getId());
        if (cardNumber != null) {
            cardNumberElement.text(cardNumber);
        } else {
            cardNumberElement.text("");
        }
    }

    public void setAuthNumber(String authNumber) {
        Element authNumberElement = singleOperationHtmlBody.getElementById(HtmlIDs.AUTH_NUMBER_FOR_SINGLE_OPERATION.getId());
        if (authNumber != null) {
            authNumberElement.text(authNumber);
        } else {
            authNumberElement.text("");
        }
    }

    public void setReference(String reference) {
        Element referenceElement = singleOperationHtmlBody.getElementById(HtmlIDs.REFERENCE.getId());
        if (reference != null) {
            referenceElement.text(reference);
        } else {
            referenceElement.text("");
        }
    }

    // Commented for client future use
//    private void setTicketClosingMessage()
//    {
//        Element ticketClosingMessageElement = htmlBody.getElementById(HtmlIDs.TICKET_CLOSING_MESSAGE.getId());
//        ticketClosingMessageElement.text(context.getString(R.string.ticket_closing_message));
//    }

    public void addOperationToList(OperationListValues singleOperation) {
        operationListValues.add(singleOperation);

        setOperationDate(singleOperation.getOperationDate());
        setOperationTime(singleOperation.getOperationTime());
        setOrderId(singleOperation.getOrderId());
        setOperationType(singleOperation.getOperationType());
        setOperationResult(singleOperation.getOperationResult());
        setAmount(singleOperation.getAmount());
        setCurrency(singleOperation.getCurrency());
        setCardNumber(singleOperation.getCardNumber());
        setAuthNumber(singleOperation.getAuthNumber());
        setReference(singleOperation.getReference());

        Element singleOperationContent = singleOperationHtmlBody.getElementById(HtmlIDs.SINGLE_OPERATION.getId());
        document.select("div.operation_list ul").append("<li>\n" + singleOperationContent + "\n</li><br>");
    }

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

    public String getTransactionDate(){
        return htmlBody.getElementById(HtmlIDs.TRANSACTION_DATE.getId()).text();
    }

    public String getTransactionTime(){
        return htmlBody.getElementById(HtmlIDs.TRANSACTION_TIME.getId()).text();
    }

    public String getTicketLabel(){
        return htmlBody.getElementById(HtmlIDs.TICKET_LABEL.getId()).text();
    }

    public String getFromDate(){
        return htmlBody.getElementById(HtmlIDs.FROM_DATE.getId()).text();
    }

    public String getToDate(){
        return htmlBody.getElementById(HtmlIDs.TO_DATE.getId()).text();
    }

    public String getCurrentPage(){
        return htmlBody.getElementById(HtmlIDs.CURRENT_PAGE.getId()).text();
    }

    public String getTotalPages(){
        return htmlBody.getElementById(HtmlIDs.TOTAL_PAGES.getId()).text();
    }

    // Commented for client future use
//    private String getTicketClosingMessage()
//    {
//        return htmlBody.getElementById(HtmlIDs.TICKET_CLOSING_MESSAGE.getId()).text();
//    }

    public List<OperationListValues> getAllOperationList(){
        return operationListValues;
    }

}
