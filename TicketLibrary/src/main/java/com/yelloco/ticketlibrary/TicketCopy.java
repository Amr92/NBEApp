package com.yelloco.ticketlibrary;

public enum TicketCopy
{
    CUSTOMER_COPY(R.string.customer_copy),
    MERCHANT_COPY(R.string.merchant_copy);

    private int resId;

    TicketCopy(int resId)
    {
        this.resId = resId;
    }

    public int getResId()
    {
        return resId;
    }
}