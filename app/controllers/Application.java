package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Locations;
import play.*;
import play.mvc.*;

import views.html.*;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;

public class Application extends Controller {

    public static Result index() {
        return ok(map.render());
    }

    public static WebSocket<JsonNode> ws() {
        return new WebSocket<JsonNode>() {
            public void onReady(In<JsonNode> in, Out<JsonNode> out) {
                Locations.addWebSocket(UUID.randomUUID().toString(), in, out);
            }
        };
    }

    public static Result getTile(String z, String x, String y) throws Exception {
        URL tile = new URL("http://78.47.233.251/outdoors/" + z + "/" + x + "/" + y);
        URLConnection yc = tile.openConnection();
        InputStream is = yc.getInputStream();
        return ok(is);
    }
}