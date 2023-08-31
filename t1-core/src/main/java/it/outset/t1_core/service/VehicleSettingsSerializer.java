package it.outset.t1_core.service;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import it.outset.t1_core.models.CloudSettings;

public class VehicleSettingsSerializer
        implements JsonDeserializer<CloudSettings> {

    @Override
    public CloudSettings deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jo = json.getAsJsonObject();

        CloudSettings obj = new CloudSettings();

        return obj;
    }
}
