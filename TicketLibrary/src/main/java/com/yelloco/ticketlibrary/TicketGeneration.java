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

import es.redsys.paysys.Operative.Managers.RedCLSTransactionData;

public class TicketGeneration
{
    private Context context;
    private Document document;
    private Element htmlBody;

    public TicketGeneration(Context context) throws IOException
    {
        this.context = context;
        InputStream inputStream = context.getAssets().open("chip_online_ticket_merchant_copy.html");
        int size = inputStream.available();
        byte[] buffer = new byte[size];
        inputStream.read(buffer);
        String str = new String(buffer);
        document = Jsoup.parse(str, "UTF-8");
        htmlBody = document.body();
    }

    public void parse(OperationType operationType, TicketCopy ticketCopy, String merchantName, String merchantNumber, String terminalId, RedCLSTransactionData redCLSTransactionData)
    {
        setTicketVersion(context.getString(ticketCopy.getResId()));
        setMerchantName(merchantName);
        setMerchantNumber(merchantNumber);
        setTerminalId(terminalId);
        setCardNumber(redCLSTransactionData.getCardClient());
        setCardHolderName(redCLSTransactionData.getCardHolder());
        setCardExpirationDate(redCLSTransactionData.getExpiration());
        setOperationType(context.getString(operationType.getResId()));
        setAuthorizationCode(redCLSTransactionData.getAutorizationNumber());
        setCreditOrDebitLabel(redCLSTransactionData.getRateApplied());
        setOrderNumber(redCLSTransactionData.getOrder());
        setTransactionDate(getDate(redCLSTransactionData.getOperationDate()));
        setTransactionTime(getTime(redCLSTransactionData.getOperationDate()));
        setAmount(redCLSTransactionData.getAmount());
        setCurrency(CurrencyCodeUtils.getCurrencyString(context, redCLSTransactionData.getCurrency()));
        // Commented for client future use
//        setTicketClosingMessage();

        if ((redCLSTransactionData.isEmvOperation() == null) && (!redCLSTransactionData.isContactlessOperation()))
        {
            //Magnetic_Swipe_Operation
        }
        else if ((redCLSTransactionData.isEmvOperation() == null) && (redCLSTransactionData.isContactlessOperation()))
        {
            //CTLS_Magnetic_Swipe_Operation
            displayCtlsLogo();
        }
        else if (redCLSTransactionData.isEmvOperation())
        {
            // EMV_Operation
            Element emvSectionElement = htmlBody.getElementById(HtmlIDs.EMV_SECTION.getId());
            emvSectionElement.removeAttr("hidden");
            setTicketLabel(redCLSTransactionData.getLabelApp());
            setApplicationId(redCLSTransactionData.getIdApp());
            setNumOfTransaction(redCLSTransactionData.getContTrans());
            setAuthorizationResponseCode(redCLSTransactionData.getArc());
            setTerminalVerificationResult(redCLSTransactionData.getResVerification());

            if (redCLSTransactionData.isContactlessOperation())
            {
                displayCtlsLogo();
            }
        }

        if (redCLSTransactionData.isDccTransaction())
        {
            Element transactionDccSectionElement = htmlBody.getElementById(HtmlIDs.DCC_SECTION_PART_ONE.getId());
            transactionDccSectionElement.removeAttr("hidden");
            Element dccSectionElement = htmlBody.getElementById(HtmlIDs.DCC_SECTION_PART_TWO.getId());
            dccSectionElement.removeAttr("hidden");
            setLiteralLinLitTrans(redCLSTransactionData.getDccSelectionData().getLiteralLinLitTrans());
            setLiteralLinLitDivisa(redCLSTransactionData.getDccSelectionData().getCurrencyChangeSymbol());
            setCurrencyChangeAmount(redCLSTransactionData.getDccSelectionData().getCurrencyChangeAmount());
            setLiteralLinMarca(redCLSTransactionData.getDccSelectionData().getLiteralLinMarca());
            setLinMarkUp(redCLSTransactionData.getDccSelectionData().getLiteralLinMarkUp());
            setLinLitComision(redCLSTransactionData.getDccSelectionData().getLiteralLinLitComision());
            setLiteralLinLitEntidad(redCLSTransactionData.getDccSelectionData().getLiteralLinLitEntidad());
            setLiteralLinCambio(redCLSTransactionData.getDccSelectionData().getLiteralLinCambio());
            setRestLiterals(redCLSTransactionData.getRestLiterals().get(0));
            setLinLitInf(redCLSTransactionData.getDccSelectionData().getLiteralLinLitInfCambio());
        }

        if (ticketCopy == TicketCopy.MERCHANT_COPY)
        {
            Element authenticationSectionElement = htmlBody.getElementById(HtmlIDs.AUTHENTICATION_SECTION.getId());
            authenticationSectionElement.removeAttr("hidden");

            if (redCLSTransactionData.isPinAuthenticated())
            {
                setPinAuthenticatedLiteral(redCLSTransactionData.getPinAuthenticatedLiteral());
            }
            else
            {
                displaySignatureSection();
            }
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

    public void setTicketLogo(String srcName){
        //Set Merchant Number Value
        Element ticketLogoElement = htmlBody.getElementById(HtmlIDs.TICKET_LOGO.getId());
        Log.d("TicketGeneration", "setTicketLogo: logo path in STL :file:///" + srcName);
        ticketLogoElement.attr("src", "file:///" + srcName);
    }

    public void displayCtlsLogo()
    {
        Element ctlsLogoElement = htmlBody.getElementById(HtmlIDs.CTLS_LOGO.getId());
        ctlsLogoElement.removeAttr("hidden");
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

    public void setTerminalId(String terminalId)
    {
        //Set Terminal Label
        Element terminalLabelElement = htmlBody.getElementById(HtmlIDs.TERMINAL_LABEL.getId());
        terminalLabelElement.text(context.getString(R.string.terminal));

        //Set Terminal ID Value
        Element terminalIdElement = htmlBody.getElementById(HtmlIDs.TERMINAL_ID.getId());
        if (terminalId != null)
        {
            terminalIdElement.text(terminalId);
        }
        else
        {
            terminalIdElement.text("");
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

    public void setCardHolderName(String cardHolderName)
    {
        Element cardHolderNameElement = htmlBody.getElementById(HtmlIDs.CARD_HOLDER_NAME.getId());
        if (cardHolderName != null)
        {
            cardHolderNameElement.text(cardHolderName);
        }
        else
        {
            cardHolderNameElement.text("");
        }
    }

    public void setCardExpirationDate(String cardExpirationDate)
    {
        Element cardExpirationDateElement = htmlBody.getElementById(HtmlIDs.CARD_EXPIRATION_DATE.getId());
        if (cardExpirationDate != null)
        {
            cardExpirationDateElement.text(cardExpirationDate);
        }
        else
        {
            cardExpirationDateElement.text("");
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

    public void setAuthorizationCode(String authorizationCode)
    {
        Element authorizationCodeElement = htmlBody.getElementById(HtmlIDs.AUTHORIZATION_CODE.getId());
        if (authorizationCode != null)
        {
            authorizationCodeElement.text(authorizationCode);
        }
        else
        {
            authorizationCodeElement.text("");
        }
    }

    public void setCreditOrDebitLabel(String creditOrDebitLabel)
    {
        Element creditOrDebitLabelElement = htmlBody.getElementById(HtmlIDs.CREDIT_DEBIT_LABEL.getId());
        if (creditOrDebitLabel != null)
        {
            creditOrDebitLabelElement.text(creditOrDebitLabel);
        }
        else
        {
            creditOrDebitLabelElement.text("");
        }
    }

    public void setOrderNumber(String orderNumber)
    {
        //Set Order Label
        Element orderLabelElement = htmlBody.getElementById(HtmlIDs.ORDER_LABEL.getId());
        orderLabelElement.text(context.getString(R.string.order));

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

    public void setTicketLabel(String ticketLabel)
    {
        Element ticketLabelElement = getHtmlBody().getElementById(HtmlIDs.TICKET_LABEL.getId());
        if (ticketLabel != null)
        {
            ticketLabelElement.text(ticketLabel);
        }
        else
        {
            ticketLabelElement.text("");
        }
    }

    public void setApplicationId(String applicationId)
    {
        //Set Application Label
        Element timeLabelElement = htmlBody.getElementById(HtmlIDs.APPLICATION_LABEL.getId());
        timeLabelElement.text(context.getString(R.string.application_label));

        Element applicationIdElement = getHtmlBody().getElementById(HtmlIDs.APPLICATION_ID.getId());
        if (applicationId != null)
        {
            applicationIdElement.text(applicationId);
        }
        else
        {
            applicationIdElement.text("");
        }
    }

    public void setNumOfTransaction(String numOfTransaction)
    {
        Element numOfTransactionElement = getHtmlBody().getElementById(HtmlIDs.NUMBER_OF_TRANSACTION.getId());
        if (numOfTransaction != null)
        {
            numOfTransactionElement.text(numOfTransaction);
        }
        else
        {
            numOfTransactionElement.text("");
        }
    }

    public void setAuthorizationResponseCode(String authorizationResponseCode)
    {
        Element authorizationResponseCodeElement = getHtmlBody().getElementById(HtmlIDs.AUTHORIZATION_RESPONSE_CODE.getId());
        if (authorizationResponseCode != null)
        {
            authorizationResponseCodeElement.text(authorizationResponseCode);
        }
        else
        {
            authorizationResponseCodeElement.text("");
        }
    }

    public void setTerminalVerificationResult(String terminalVerificationResult)
    {
        Element terminalVerificationResultElement = getHtmlBody().getElementById(HtmlIDs.TERMINAL_VERIFICATION_RESULT.getId());
        if (terminalVerificationResult != null)
        {
            terminalVerificationResultElement.text(terminalVerificationResult);
        }
        else
        {
            terminalVerificationResultElement.text("");
        }
    }

    public void setLiteralLinLitTrans(String literalLinLitTrans)
    {
        Element literalLinLitTransElement = getHtmlBody().getElementById(HtmlIDs.LITERALLINLITTRANS.getId());
        if (literalLinLitTrans != null)
        {
            literalLinLitTransElement.text(literalLinLitTrans);
        }
        else
        {
            literalLinLitTransElement.text("");
        }
    }

    public void setLiteralLinLitDivisa(String literalLinLitDivisa)
    {
        Element literalLinLitDivisaElement = getHtmlBody().getElementById(HtmlIDs.LITERALLINLITDIVISA.getId());
        if (literalLinLitDivisa != null)
        {
            literalLinLitDivisaElement.text(literalLinLitDivisa);
        }
        else
        {
            literalLinLitDivisaElement.text("");
        }
    }

    public void setCurrencyChangeAmount(double currencyChangeAmount)
    {
        Element currencyChangeAmountElement = getHtmlBody().getElementById(HtmlIDs.CURRENCY_CHANGE_AMOUNT.getId());
        currencyChangeAmountElement.text(String.valueOf(currencyChangeAmount));
    }

    public void setLiteralLinMarca(String literalLinMarca)
    {
        Element literalLinMarcaElement = getHtmlBody().getElementById(HtmlIDs.LITERALLINMARCA.getId());
        if (literalLinMarca != null)
        {
            literalLinMarcaElement.text(literalLinMarca);
        }
        else
        {
            literalLinMarcaElement.text("");
        }
    }

    public void setLinMarkUp(String linMarkUp)
    {
        Element linMarkUpElement = getHtmlBody().getElementById(HtmlIDs.LINMARKUP.getId());
        if (linMarkUp != null)
        {
            linMarkUpElement.text(linMarkUp);
        }
        else
        {
            linMarkUpElement.text("");
        }
    }

    public void setLinLitComision(String linLitComision)
    {
        Element linLitComisionElement = getHtmlBody().getElementById(HtmlIDs.LINLITCOMISION.getId());
        if (linLitComision != null)
        {
            linLitComisionElement.text(linLitComision);
        }
        else
        {
            linLitComisionElement.text("");
        }
    }

    public void setLiteralLinLitEntidad(String literalLinLitEntidad)
    {
        Element literalLinLitEntidadElement = getHtmlBody().getElementById(HtmlIDs.LITERALLINLITENTIDAD.getId());
        if (literalLinLitEntidad != null)
        {
            literalLinLitEntidadElement.text(literalLinLitEntidad);
        }
        else
        {
            literalLinLitEntidadElement.text("");
        }
    }

    public void setLiteralLinCambio(String literalLinCambio)
    {
        Element literalLinCambioElement = getHtmlBody().getElementById(HtmlIDs.LITERALLINCAMBIO.getId());
        if (literalLinCambio != null)
        {
            literalLinCambioElement.text(literalLinCambio);
        }
        else
        {
            literalLinCambioElement.text("");
        }
    }

    public void setRestLiterals(String restLiterals)
    {
        Element restLiteralsElement = getHtmlBody().getElementById(HtmlIDs.RESTLITERALS.getId());
        if (restLiterals != null)
        {
            restLiteralsElement.text(restLiterals);
        }
        else
        {
            restLiteralsElement.text("");
        }
    }

    public void setLinLitInf(String linLitInf)
    {
        Element linLitInfElement = getHtmlBody().getElementById(HtmlIDs.LINLITINF.getId());
        if (linLitInf != null)
        {
            linLitInfElement.text(linLitInf);
        }
        else
        {
            linLitInfElement.text("");
        }
    }

    private void setPinAuthenticatedLiteral(String pinAuthenticatedLiteral)
    {
        Element pinAuthenticationElement = htmlBody.getElementById(HtmlIDs.PIN_AUTHENTICATION.getId());
        pinAuthenticationElement.removeAttr("hidden");
        if (pinAuthenticatedLiteral != null)
        {
            pinAuthenticationElement.text(pinAuthenticatedLiteral);
        }
        else
        {
            pinAuthenticationElement.text("");
        }
    }

    // Commented for client future use
//    private void setTicketClosingMessage()
//    {
//        Element ticketClosingMessageElement = htmlBody.getElementById(HtmlIDs.TICKET_CLOSING_MESSAGE.getId());
//        ticketClosingMessageElement.text(context.getString(R.string.ticket_closing_message));
//    }
    //endregion Setters

    private void displaySignatureSection()
    {
        Element signatureAuthenticationElement = htmlBody.getElementById(HtmlIDs.SIGNATURE_AUTHENTICATION.getId());
        signatureAuthenticationElement.removeAttr("hidden");

        //Set Signature Label
        Element signatureLabelElement = htmlBody.getElementById(HtmlIDs.SIGNATURE_LABEL.getId());
        signatureLabelElement.text(context.getString(R.string.customer_signature));
    }

    public boolean isCtlsLogoDisplayed()
    {
        Element ctlsLogoElement = htmlBody.getElementById(HtmlIDs.CTLS_LOGO.getId());
        return !ctlsLogoElement.hasAttr("hidden");
    }

    public boolean isEMVSectionDisplayed()
    {
        Element emvSectionElement = htmlBody.getElementById(HtmlIDs.EMV_SECTION.getId());
        return !emvSectionElement.hasAttr("hidden");
    }

    public boolean isDCCSectionDisplayed()
    {
        Element dccSectionElement = htmlBody.getElementById(HtmlIDs.DCC_SECTION_PART_ONE.getId());
        return !dccSectionElement.hasAttr("hidden");
    }

    public boolean isAuthenticationSectionDisplayed()
    {
        Element authenticationSectionElement = htmlBody.getElementById(HtmlIDs.AUTHENTICATION_SECTION.getId());
        return !authenticationSectionElement.hasAttr("hidden");
    }

    public boolean isPinAuthenticatedLiteralDisplayed()
    {
        Element ctlsLogoElement = htmlBody.getElementById(HtmlIDs.PIN_AUTHENTICATION.getId());
        return !ctlsLogoElement.hasAttr("hidden");
    }

    public boolean isSignatureBoxDisplayed()
    {
        Element ctlsLogoElement = htmlBody.getElementById(HtmlIDs.SIGNATURE_AUTHENTICATION.getId());
        return !ctlsLogoElement.hasAttr("hidden");
    }

    private void prepareTicket(){
        document.select("div.redsysLogoNormal img").attr("hidden", "hidden");
        Element signatureAuthenticationElement = htmlBody.getElementById("redsysLogoBlack");
        signatureAuthenticationElement.removeAttr("hidden");
    }

    @NonNull
    @Override
    public String toString()
    {
        return getHTML();
    }

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
        return htmlBody.getElementById(HtmlIDs.MERCHANT_NUMBER.getId()).text();
    }

    public String getTerminalId()
    {
        return htmlBody.getElementById(HtmlIDs.TERMINAL_ID.getId()).text();
    }

    public String getCardNumber()
    {
        return htmlBody.getElementById(HtmlIDs.CARD_NUMBER.getId()).text();
    }

    public String getCardHolderName()
    {
        return htmlBody.getElementById(HtmlIDs.CARD_HOLDER_NAME.getId()).text();
    }

    public String getCardExpirationDate()
    {
        return htmlBody.getElementById(HtmlIDs.CARD_EXPIRATION_DATE.getId()).text();
    }

    public String getOperationType()
    {
        return htmlBody.getElementById(HtmlIDs.OPERATION_TYPE.getId()).text();
    }

    public String getAuthorizationCode()
    {
        return htmlBody.getElementById(HtmlIDs.AUTHORIZATION_CODE.getId()).text();
    }

    public String getCreditOrDebitLabel()
    {
        return htmlBody.getElementById(HtmlIDs.CREDIT_DEBIT_LABEL.getId()).text();
    }

    public String getOrderNumber()
    {
        return htmlBody.getElementById(HtmlIDs.ORDER_NUMBER.getId()).text();
    }

    public String getTransactionDate()
    {
        return htmlBody.getElementById(HtmlIDs.TRANSACTION_DATE.getId()).text();
    }

    public String getTransactionTime()
    {
        return htmlBody.getElementById(HtmlIDs.TRANSACTION_TIME.getId()).text();
    }

    public String getAmount()
    {
        return htmlBody.getElementById(HtmlIDs.TRANSACTION_AMOUNT.getId()).text();
    }

    public String getCurrency()
    {
        return htmlBody.getElementById(HtmlIDs.CURRENCY.getId()).text();
    }

    public String getTicketLabel()
    {
        return getHtmlBody().getElementById(HtmlIDs.TICKET_LABEL.getId()).text();
    }

    public String getApplicationId()
    {
        return getHtmlBody().getElementById(HtmlIDs.APPLICATION_ID.getId()).text();
    }

    public String getNumOfTransaction()
    {
        return getHtmlBody().getElementById(HtmlIDs.NUMBER_OF_TRANSACTION.getId()).text();
    }

    public String getAuthorizationResponseCode()
    {
        return getHtmlBody().getElementById(HtmlIDs.AUTHORIZATION_RESPONSE_CODE.getId()).text();
    }

    public String getTerminalVerificationResult()
    {
        return getHtmlBody().getElementById(HtmlIDs.TERMINAL_VERIFICATION_RESULT.getId()).text();
    }

    public String getLiteralLinLitTrans()
    {
        return getHtmlBody().getElementById(HtmlIDs.LITERALLINLITTRANS.getId()).text();
    }

    public String getCurrencyChangeSymbol()
    {
        return getHtmlBody().getElementById(HtmlIDs.LITERALLINLITDIVISA.getId()).text();
    }

    public String getCurrencyChangeAmount()
    {
        return getHtmlBody().getElementById(HtmlIDs.CURRENCY_CHANGE_AMOUNT.getId()).text();
    }

    public String getLiteralLinMarca()
    {
        return getHtmlBody().getElementById(HtmlIDs.LITERALLINMARCA.getId()).text();
    }

    public String getLiteralLinMarkUp()
    {
        return getHtmlBody().getElementById(HtmlIDs.LINMARKUP.getId()).text();
    }

    public String getLiteralLinLitComision()
    {
        return getHtmlBody().getElementById(HtmlIDs.LINLITCOMISION.getId()).text();
    }

    public String getLiteralLinLitEntidad()
    {
        return getHtmlBody().getElementById(HtmlIDs.LITERALLINLITENTIDAD.getId()).text();
    }

    public String getLiteralLinCambio()
    {
        return getHtmlBody().getElementById(HtmlIDs.LITERALLINCAMBIO.getId()).text();
    }

    public String getRestLiterals()
    {
        return getHtmlBody().getElementById(HtmlIDs.RESTLITERALS.getId()).text();
    }

    public String getLiteralLinLitInfCambio()
    {
        return getHtmlBody().getElementById(HtmlIDs.LINLITINF.getId()).text();
    }

    // Commented for client future use
//    private String getTicketClosingMessage()
//    {
//        return htmlBody.getElementById(HtmlIDs.TICKET_CLOSING_MESSAGE.getId()).text();
//    }
    //endregion Getters
}
