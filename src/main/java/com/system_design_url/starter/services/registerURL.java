package com.system_design_url.starter.services;
import com.system_design_url.starter.db.DatabaseUtil;
import org.apache.commons.lang.RandomStringUtils;

import java.sql.Connection;


public class registerURL {
  private static final int length = 15;
  private static final String base62Chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  private static Connection conn = DatabaseUtil.getConnection();

  public static String register(String longUrl){
    String shortUrl = encode(longUrl);

    // Save shortUrl, longUrl in DB

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

}
