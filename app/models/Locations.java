package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.F.*;
import play.libs.Json;
import play.mvc.WebSocket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Locations {
    private static final Map<String, Location> sockets = new ConcurrentHashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void addWebSocket(final String uuid, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {

        sockets.put(uuid, new Location(uuid, out));

        in.onMessage(new Callback<JsonNode>() {
            public void invoke(JsonNode event) {
                System.out.println(uuid + ": " + event);

                String action = event.get("action").toString();

                if (action.equals("\"updateLocation\"")) {
                    JsonNode data = event.get("data");
                    JsonNode latLng = data.get("latlng");

                    double lat = latLng.get(0).asDouble();
                    double lng = latLng.get(1).asDouble();
                    int accuracy = data.get("accuracy").asInt();

                    updateLocation(uuid, dataToJson(uuid, lat, lng, accuracy));
                    sockets.get(uuid).update(lat, lng, accuracy);
                }
            }
        });

        in.onClose(new Callback0() {
            public void invoke() {
                sockets.remove(uuid);

                System.out.println(uuid + ": Disconnected");
            }
        });

        sendAllLocations(uuid);

        System.out.println(uuid + ": Connected");
        System.out.println("Map size = " + sockets.size());
    }

    private static void sendAllLocations(String uuid) {
        ObjectNode locationsJson = mapper.createObjectNode();

        for (String key : sockets.keySet()) {
            if (!key.equals(uuid)) {
                ObjectNode locationJson = locationsJson.putObject(key);
                locationJson.putAll(sockets.get(key).getLatLng());
            }
        }

        ObjectNode response = mapper.createObjectNode();
        response.put("action", "allLocations");
        ObjectNode data = response.putObject("data");
        ObjectNode locations = data.putObject("locations");
        locations.putAll(locationsJson);

        sockets.get(uuid).write(response);
    }

    private static void updateLocation(String uuid, ObjectNode dataJson) {
        // build json to send
        ObjectNode json = mapper.createObjectNode();
        json.put("action", "updateLocation");
        ObjectNode data = json.putObject("data");
        data.putAll(dataJson);

        for (String key : sockets.keySet()) {
            if (!key.equals(uuid)) {
                sockets.get(key).write(json);
            }
        }
    }

    public static ObjectNode dataToJson(String uuid, double lat, double lng, int accuracy) {
        ObjectNode data = Json.newObject();
        data.put("id", uuid);
        ArrayNode latlng = data.putArray("latlng");
        latlng.add(lat);
        latlng.add(lng);
        data.put("accuracy", accuracy);

        return data;
    }
}


