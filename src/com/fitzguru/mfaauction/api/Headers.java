package com.fitzguru.mfaauction.api;

import org.apache.http.Header;
import org.apache.http.HeaderElement;

/**
 * Created by jtsuji on 11/18/14.
 */
public class Headers {
  public static Header APP_NAME = new Header() {
    @Override
    public String getName() {
      return "X-Parse-Application-Id";
    }

    @Override
    public String getValue() {
      return "NSTu2o0vGr9UJ0JYM5iPXSYGoDoQQ3ulrERXUEG0";
    }

    @Override
    public HeaderElement[] getElements() throws org.apache.http.ParseException {
      return new HeaderElement[0];
    }
  };

  public static Header APP_KEY = new Header() {
    @Override
    public String getName() {
      return "X-Parse-REST-API-Key";
    }

    @Override
    public String getValue() {
      return "D3H1F21LuG2lOzf8xf9jRmlOE8aPjrA7pJXffx0L";
    }

    @Override
    public HeaderElement[] getElements() throws org.apache.http.ParseException {
      return new HeaderElement[0];
    }
  };
}
