package com.system_design_url.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.http.impl.Http2ServerResponse;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Route;
import io.vertx.ext.web.Router;


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
      log.info("Received long URL -> Returning short URL");
    });
    server.requestHandler(router).listen(8080);

  }
}
