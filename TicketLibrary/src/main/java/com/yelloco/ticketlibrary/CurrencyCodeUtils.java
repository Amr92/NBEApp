package com.yelloco.ticketlibrary;

import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

public class CurrencyCodeUtils
{
    public static String getCurrencyString(Context context, int currencyCode)
    {
        try {
            InputStream is = context.getAssets().open("iso4217.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            Element element = doc.getDocumentElement();
            element.normalize();

            NodeList nList = doc.getElementsByTagName("CcyNtry");

            for (int i = 0; i < nList.getLength(); i++)
            {
                Node node = nList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    Element element2 = (Element) node;

                    if ( Integer.parseInt(getValue("CcyNbr", element2)) == currencyCode)
                    {
                       return getValue("Ccy", element2);
                    }
                }
            }

            System.err.println("CurrencyCodeUtils: This currency code [" + currencyCode + "] Not Found in iso4217.xml file");
            return null;
        } catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private static String getValue(String tag, Element element)
    {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = nodeList.item(0);
        return node.getNodeValue();
    }
}