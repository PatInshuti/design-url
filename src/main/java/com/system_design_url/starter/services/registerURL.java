package com.system_design_url.starter.services;
import com.system_design_url.starter.middlewares.DatabaseUtil;
import io.rebloom.client.Client;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.apache.commons.lang.RandomStringUtils;
import com.system_design_url.starter.utils.Utils;
import java.sql.*;


public class registerURL {

  public static Client bloomFilter = new Client(Utils.localHost, 6379);

  private static final Logger log = LoggerFactory.getLogger(registerURL.class);

  public static String register(String longUrl) throws SQLException {
    Connection conn = DatabaseUtil.getConnection();

    // check if long url exist in DB before starting encoding
    ResultSet result = checkUrlExistsInDb("long_url", longUrl, conn);
    if(result.next()) return result.getString("short_url");

    // encode the long_url & save to DB
    String shortUrl = encode(longUrl);
    int insertedRows = saveEncodedUrlInDb(longUrl, shortUrl, conn);
    if(insertedRows < 1) throw new SQLException("Unable to Insert a new Record \n");

    bloomFilter.add(Utils.bloomFilter.SHORT_URL.name(), shortUrl);
    return shortUrl;
  }

  public static String encode(String url){
    // Generate id
    long ID = Long.parseLong(RandomStringUtils.randomNumeric(Utils.length));

    // convert to base 62
    StringBuilder sb = new StringBuilder();
    while(ID > 0){
      long rem = ID%62;
      ID = ID/62;
      sb.append(Utils.base62Chars.charAt((int)rem));
    }
    return sb.toString();
  }

  public static ResultSet checkUrlExistsInDb(String urlType, String url, Connection conn) throws SQLException {
    PreparedStatement pst = conn.prepareStatement("SELECT long_url, short_url FROM Urls WHERE "+urlType+"=?");
    pst.setString(1, url);
    return pst.executeQuery();
  }

  public static int saveEncodedUrlInDb(String longUrl, String shortUrl, Connection conn) throws SQLException{
    log.info("Saving URL into DB");
    PreparedStatement insrtpst = conn.prepareStatement("INSERT INTO Urls(long_url,short_url) VALUES(?,?)");
    insrtpst.setString(1, longUrl);;
    insrtpst.setString(2, shortUrl);
    return insrtpst.executeUpdate();
  }

}
