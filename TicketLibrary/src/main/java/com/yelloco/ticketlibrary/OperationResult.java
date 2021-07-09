package com.yelloco.ticketlibrary;

public enum OperationResult {
    AUTH("Autorizacion", R.string.auth),
    PREAUTH("PreAutorizacion", R.string.pre_auth),
    REFUND("Devolucion", R.string.refund),
    DENIED("Denegadas", R.string.denied),
    AUTORIZADA("AUTORIZADA", R.string.authorized),
    DENEGADA("DENEGADA", R.string.declined),
    CONF("Confirmacion", R.string.confirmation_string_in_stl),
    CONFIRMATION("Confirmaciones", R.string.confirmation_string_in_stl);

    private int resultForTicket;
    private String response;

    OperationResult(String response, int resultForTicket)
    {
        this.response = response;
        this.resultForTicket = resultForTicket;
    }

    public int getResultForTicket()
    {
        return resultForTicket;
    }

    public String getResponse(){return response;}
}
