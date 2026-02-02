package com.megasoft.merchant.mms.persistence.response;

import org.json.JSONObject;

import java.util.ArrayList;

public class QueryMMSResult {
    private ArrayList<JSONObject> resultadoFinal;
    private final double queryExecutionTime;
    private final double jsonExecutionTime;

    public QueryMMSResult(ArrayList<JSONObject> resultadoFinal, double queryExecutionTime, double jsonExecutionTime) {
        this.resultadoFinal = resultadoFinal;
        this.queryExecutionTime = queryExecutionTime;
        this.jsonExecutionTime =jsonExecutionTime;
    }

    public ArrayList<JSONObject> getResultadoFinal() {
        return resultadoFinal;
    }

    public double getQueryExecutionTime() {
        return queryExecutionTime;
    }

    public double getJsonExecutionTime(){
        return jsonExecutionTime;
    }

    public int getSize() {
        return resultadoFinal.size();
    }
}
