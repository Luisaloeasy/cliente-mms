package com.megasoft.merchant.mms.comunicacion;

import java.util.ArrayList;
import com.google.gson.*;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Clase encargada de recibir un TokenDatos y transformarlo a un JSON
 * @author Luis Alejandro Aloisi Millan
 *
 */
public class Traductor {

	private static Logger logger = Logger.getLogger(Traductor.class.getName());

	public static ArrayList<JSONObject> tokenDatosMQToJsonArray(TokenDatosMQ lenguaje) {
		try {
			Gson gson = new Gson();
			String jsonString = gson.toJson(lenguaje);
			JSONArray jsonArray = new JSONArray("[" + jsonString + "]");
			ArrayList<JSONObject> jsonList = new ArrayList<>();
			for (int i = 0; i < jsonArray.length(); i++) {
				jsonList.add(jsonArray.getJSONObject(i));
			}
			return jsonList;
		} catch (JsonIOException | JsonSyntaxException | JSONException e) {
			logger.error("MMS v2 -- Error al crear JSON tokenDatosMQToJsonArray", e);
			return new ArrayList<>();
		}
	}
}