package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.F.*;
import play.libs.Json;
import play.mvc.WebSocket;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Locations {
    private static final Map<String, Location> sockets = new HashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void addWebSocket(final String uuid, WebSocket.In<JsonNode> in, WebSocket.Out<JsonNode> out) {

        synchronized (Locations.class) {
            sockets.put(uuid, new Location(uuid, out));
        }

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

                    Location location;
                    synchronized (Locations.class) {
                        location = sockets.get(uuid);
                    }
                    location.update(lat, lng, accuracy);

                    sendAllLocations(uuid);
                }
            }
        });

        in.onClose(new Callback0() {
            public void invoke() {
                System.out.println(uuid + ": Disconnected");
            }
        });

        System.out.println(uuid + ": Connected");

        ObjectNode hello = Json.newObject();
        hello.put("action", "hello");
        out.write(hello);
    }

    private static void sendAllLocations(String uuid) {
        Set<String> keys;
        synchronized (Locations.class) {
            keys = sockets.keySet();
        }

        ObjectNode locationsJson = mapper.createObjectNode();

        for (String key : keys) {
            if (!key.equals(uuid)) {
                ObjectNode locationJson = locationsJson.putObject(key);

                Location location;
                synchronized (Locations.class) {
                    location = sockets.get(key);
                }
                locationJson.putAll(location.getLatLng());
            }
        }

        System.out.println(locationsJson);

    }
}


