package com.system_design_url.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import com.system_design_url.starter.services.registerURL;

import java.sql.SQLException;

public class MainVerticle extends AbstractVerticle {

  Logger log = LoggerFactory.getLogger(MainVerticle.class);

  /*
  * Post Request with long URL --> encode URL -> Write to DB -> Return short URL
  * Get Short URL -> Cache -> Check DB -> Return Error / Redirect
  * */


  @Override
  public void start() {

    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);

    router.route(HttpMethod.GET, "/url").handler(ctx->{
      log.info("Received Short URL -> Returning long url");
    });

    router.route(HttpMethod.POST, "/url").handler(ctx ->{

      // Register URL with long URL
      Future<Buffer> body = ctx.request().body();

      body.onComplete(requestBody->{
        JsonObject req = requestBody.result().toJsonObject();
        String longUrl = req.getString("longUrl");
        try {
          ctx.response().putHeader("Content-Type","plain/text").end(registerURL.register(longUrl));
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }
      });

    });
    server.requestHandler(router).listen(8080);

  }
}
