package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;
import play.mvc.WebSocket;

/**
 * Created by daniel on 9/16/14.
 */
public class Location {
    private final String uuid;
    private final WebSocket.Out<JsonNode> out;
    private double lat;
    private double lng;
    private int accuracy;

    Location(String uuid, WebSocket.Out<JsonNode> out) {
        this.uuid = uuid;
        this.out = out;
    }

    public void update(double lat, double lng, int accuracy) {
        this.lat = lat;
        this.lng = lng;
        this.accuracy = accuracy;
    }

    public ObjectNode getLatLng() {
        ObjectNode response = Json.newObject();
        response.put("id", uuid);
        ArrayNode latlng = response.putArray("latlng");
        latlng.add(this.lat);
        latlng.add(this.lng);
        response.put("accuracy", this.accuracy);

        return response;
    }
}
