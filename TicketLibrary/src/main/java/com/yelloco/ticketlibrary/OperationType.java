package com.yelloco.ticketlibrary;

public enum OperationType
{
    SALE(R.string.sale),
    REFUND(R.string.refund),
    PRE_AUTH(R.string.pre_auth),
    CONFIRMATION(R.string.confirmation_string_in_stl);

    private int resId;

    OperationType(int resId)
    {
        this.resId = resId;
    }

    public int getResId()
    {
        return resId;
    }
}