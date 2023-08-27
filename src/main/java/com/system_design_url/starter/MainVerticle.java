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

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
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
    JedisPool jedisPool = new JedisPool("127.0.0.1",6379);

    router.route(HttpMethod.GET, "/:shortUrl").handler(ctx->{
      String shortUrl = ctx.request().getParam("shortUrl");

      // check if mapping from shortUrl to longUrl is in cache
      try( Jedis jedis = jedisPool.getResource() ){
        if(jedis.exists(shortUrl)) {
          ctx.redirect(jedis.get(shortUrl), (res) -> { log.info("Found URL in cache \n"); } );
          return;
        }
      }

      try {
        ResultSet result = registerURL.checkUrlExistsInDb("short_url", shortUrl, conn);
        if(result.next()){
          String longUrl = result.getString("long_url");

          // add long url to cache
          try( Jedis jedis = jedisPool.getResource() ) {
            jedis.configSet("maxmemory","100mb");
            jedis.configSet ("maxmemory-policy","allkeys-lfu");
            jedis.set(shortUrl, longUrl);
          }

          ctx.redirect(longUrl, (res)-> log.info("Successful redirect \n"));
          return;
        }

        ctx.response().putHeader("Content-Type","text/plain").end("URL not found \n");
      }
      catch (SQLException e) { throw new RuntimeException(e); }

    });

    router.route(HttpMethod.POST, "/").handler(ctx ->{

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
