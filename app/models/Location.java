package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.mvc.WebSocket;

import static models.Locations.dataToJson;

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
        return dataToJson(this.uuid, this.lat, this.lng, this.accuracy);
    }

    public void write(JsonNode json) {
        this.out.write(json);
    }
}
