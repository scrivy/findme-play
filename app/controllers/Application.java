package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import models.Locations;
import play.*;
import play.mvc.*;

import views.html.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
        String tilePath = z + "/" + x + "/" + y;
        File tileFile = new File("public/tiles/" + tilePath);

        if (!tileFile.exists()) {
            new File("public/tiles/" + z + "/" + x).mkdirs();
            URL tile = new URL("http://78.47.233.251/outdoors/" + tilePath);
            URLConnection yc = tile.openConnection();
            InputStream fis = yc.getInputStream();
            FileOutputStream fos = new FileOutputStream("public/tiles/" + tilePath);
            int bytesRead = -1;
            byte[] buffer = new byte[4096];
            while ((bytesRead = fis.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            fos.close();
            fis.close();
            System.out.println("downloaded tile " + y);
        }

        InputStream is = new FileInputStream(tileFile);
        return ok(is);
    }
}