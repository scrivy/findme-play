package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Locations;
import play.*;
import play.mvc.*;

import views.html.*;

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
}