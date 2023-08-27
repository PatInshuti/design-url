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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.system_design_url.starter.services.registerURL;
import com.system_design_url.starter.db.DatabaseUtil;



public class MainVerticle extends AbstractVerticle {

  Logger log = LoggerFactory.getLogger(MainVerticle.class);

  /*
  * Post Request with long URL --> encode URL -> Write to DB -> Return short URL
  * Get Short URL -> Cache -> Check DB -> Return Error / Redirect
  * */


  @Override
  public void start() throws Exception {

    HttpServer server = vertx.createHttpServer();
    Router router = Router.router(vertx);
    Connection conn = DatabaseUtil.getConnection();

    router.route(HttpMethod.GET, "/url/:shortUrl").handler(ctx->{
      String shortUrl = ctx.request().getParam("shortUrl");

      try {
        ResultSet result = registerURL.checkUrlExistsInDb("short_url", shortUrl, conn);
        if(result.next()){
          String longUrl = result.getString("long_url");
          ctx.redirect(longUrl, (res)-> log.info("Successful redirect \n"));
        }

        ctx.response().putHeader("Content-Type","text/plain").end("URL not found \n");
      }
      catch (SQLException e) { throw new RuntimeException(e); }

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
