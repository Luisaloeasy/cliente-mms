package com.megasoft.merchant.mms.persistence.response;

import org.json.JSONObject;

import java.util.ArrayList;

public class QueryConfigResult {
    private ArrayList<JSONObject> resultadoFinal;
    private double queryExecutionTime;
    private double jsonExecutionTime;


    public QueryConfigResult() {
    }


    public QueryConfigResult(ArrayList<JSONObject> resultadoFinal, double queryExecutionTime, double jsonExecutionTime) {
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

    public void clear() {
        resultadoFinal.clear();
    }
}
