package com.system_design_url.starter.services;
import com.system_design_url.starter.db.DatabaseUtil;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import org.apache.commons.lang.RandomStringUtils;
import java.sql.*;


public class registerURL {
  private static final Logger log = LoggerFactory.getLogger(registerURL.class);
  private static final int length = 15;
  private static final String base62Chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  public static String register(String longUrl) throws SQLException {
    Connection conn = DatabaseUtil.getConnection();

    // check if long url exist in DB before starting encoding
    ResultSet result = checkLongUrlExistsInDb(longUrl,conn);
    if(result.next()) return result.getString("short_url");

    // encode the long_url & save to DB
    String shortUrl = encode(longUrl);
    int insertedRows = saveEncodedUrlInDb(longUrl, shortUrl, conn);
    if(insertedRows < 1) throw new SQLException("Unable to Insert a new Record \n");

    return shortUrl;
  }

  public static String encode(String url){
    // Generate id
    long ID = Long.parseLong(RandomStringUtils.randomNumeric(length));

    // convert to base 62
    StringBuilder sb = new StringBuilder();
    while(ID > 0){
      long rem = ID%62;
      ID = ID/62;
      sb.append(base62Chars.charAt((int)rem));
    }
    return sb.toString();
  }

  public static ResultSet checkLongUrlExistsInDb(String longUrl, Connection conn) throws SQLException {
    PreparedStatement pst = conn.prepareStatement("SELECT long_url, short_url FROM Urls WHERE long_url=?");
    pst.setString(1,longUrl);
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
