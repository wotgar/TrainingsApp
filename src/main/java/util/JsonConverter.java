package util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import model.Uebung;

import java.util.List;

public class JsonConverter {

    private final Gson gson;

    public JsonConverter() {

        gson = new GsonBuilder().disableHtmlEscaping().create();
    }

    public String convertUebungenToJson(List<Uebung> uebungen) {

        JsonArray jArray = gson.toJsonTree(uebungen).getAsJsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("uebungen", jArray);

        return jsonObject.toString();
    }
}
