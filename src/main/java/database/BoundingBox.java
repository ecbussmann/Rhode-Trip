package database;

import attractions.Museum;
import attractions.Park;
import attractions.Restaurant;
import attractions.Shop;
import edu.brown.cs.student.termProject.AttractionNode;
import edu.brown.cs.student.termProject.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class with static functions to find all the attractions within a bounding box
 * around two sets of coordinates.
 */
public final class BoundingBox {

  private BoundingBox() {

  }

  /**
   * Creates a bounding box around the given starting and end positions,
   * and returns a list of the AttractionNodes from the yelp-database within
   * this box that are of the given categories.
   *
   * @param coords1       - [lat, lon] of the start position
   * @param coords2       - [lat, lon] of the end position
   * @param categories    - list of acceptable attraction categories
   * @param prefNumStops  - number of stops user prefers to make
   * @param costPref      - cost preference of user
   * @param preferredStop - list of doubles representing users preference for each stop type
   * @return list of attraction nodes of the given categories within a
   * box around the given positions
   */
  public static List<AttractionNode> findAttractionsBetween(
      double[] coords1, double[] coords2, List<String> categories,
      int prefNumStops, int costPref, double[] preferredStop) {
    double[] boundingBoxBounds = findBoundingBoxBounds(coords1, coords2);
    double[] expandedBoundingBoxBounds = expandBoundingBoxBounds(boundingBoxBounds, 2.0);
    try {
      return findAttractionsWithinBoundingBox(expandedBoundingBoxBounds, categories, prefNumStops,
          costPref, preferredStop);
    } catch (SQLException | IOException e) {
      throw new IllegalArgumentException("ERROR: Error while connecting to SQL database");
    }
  }

  /**
   * Finds the bounds for a bounding box to be created around the given coordinates.
   *
   * @param coords1 - [lat, lon] of the first position
   * @param coords2 - [lat, lon] of the second position
   * @return [start lat, end lat, start lon, end lon] of the bounding box
   */
  public static double[] findBoundingBoxBounds(double[] coords1, double[] coords2) {
    double[] bounds = new double[4];
    if (coords1[0] < coords2[0]) {
      bounds[0] = coords1[0];
      bounds[1] = coords2[0];
    } else {
      bounds[0] = coords2[0];
      bounds[1] = coords1[0];
    }
    if (coords1[1] < coords2[1]) {
      bounds[2] = coords1[1];
      bounds[3] = coords2[1];
    } else {
      bounds[2] = coords2[1];
      bounds[3] = coords1[1];
    }
    return bounds;
  }

  /**
   * Extends the given bounding box expansionFactor units in every direction.
   *
   * @param boundingBoxBounds - original bounding box to be expanded
   * @param expansionFactor   - number of units to expand the bounding box by in every direction
   * @return [start lat, end lat, start lon, end lon] of the expanded bounding box
   */
  public static double[] expandBoundingBoxBounds(double[] boundingBoxBounds,
                                                 double expansionFactor) {
    double[] expandedBoundingBoxBounds = new double[4];
    expandedBoundingBoxBounds[0] = boundingBoxBounds[0] - expansionFactor;
    expandedBoundingBoxBounds[1] = boundingBoxBounds[1] + expansionFactor;
    expandedBoundingBoxBounds[2] = boundingBoxBounds[2] - expansionFactor;
    expandedBoundingBoxBounds[3] = boundingBoxBounds[3] + expansionFactor;
    return expandedBoundingBoxBounds;
  }

  /**
   * Returns a list of all the attractions in the yelp database within the given
   * bounding box that are of the given categories.
   *
   * @param boundingBoxBounds - [start lat, end lat, start lon, end lon] of the bounding box
   * @param categories        - list of acceptable attraction categories
   * @param prefNumStops      - number of stops preferred by user
   * @param costPref          - cost preference of user
   * @param preferredStop     - double representing user's value preference of each stop type
   * @return list of attraction nodes of the given categories within the given bounding box
   * @throws SQLException - if yelp database cannot be successfully queried
   * @throws IOException  - if issue with yelp API connection
   */
  public static List<AttractionNode> findAttractionsWithinBoundingBox(
      double[] boundingBoxBounds, List<String> categories, int prefNumStops, int costPref,
      double[] preferredStop)
      throws SQLException, IOException {
    ArrayList<String> nameList = new ArrayList<>();
    Connection conn = Database.getYelpDatabaseConnection();
    String query = "SELECT * FROM yelp_business "
        + "WHERE (latitude BETWEEN ? and ?) "
        + "AND (longitude BETWEEN ? AND ?)"
        + "AND (stars > 3.0)"
        + "AND (review_count > 10)";
    PreparedStatement prep = conn.prepareStatement(query);
    prep.setDouble(1, boundingBoxBounds[0]);
    prep.setDouble(2, boundingBoxBounds[1]);
    prep.setDouble(3, boundingBoxBounds[2]);
    prep.setDouble(4, boundingBoxBounds[3]);
    ResultSet rs = prep.executeQuery();
    List<AttractionNode> attractionsWithinBox = new ArrayList<>();
    while (rs.next()) {
      String id = rs.getString("business_id");
      String name = rs.getString("name");
      nameList.add(name);
      String address = rs.getString("address");
      String city = rs.getString("city");
      String state = rs.getString("state");
      String postalCode = rs.getString("postal_code");
      String[] location = new String[] {address, city, state, postalCode};
      double lat = rs.getDouble("latitude");
      double lon = rs.getDouble("longitude");
      double[] coords = new double[] {lat, lon};
      double price = 0.0;
      double rating = rs.getDouble("stars");
      double reviewCount = rs.getDouble("review_count");
      String categoriesList = rs.getString("categories");
      if ((categoriesList.contains("Restaurants")
          || categoriesList.contains("Food")
          || categoriesList.contains("Bars"))
          && categories.contains("Restaurant")) {
        Integer priceInt = findPriceField(rs.getString("attributes"));
        if (priceInt != null) {
          price = priceInt;
          AttractionNode nextAttraction = new Restaurant(
              id, name, location, coords, price, rating, reviewCount);
          attractionsWithinBox.add(nextAttraction);
        }
      } else if (categoriesList.contains("Museums") && categories.contains("Museum")) {
        AttractionNode nextAttraction = new Museum(
            id, name, location, coords, price, rating, reviewCount);
        attractionsWithinBox.add(nextAttraction);
      } else if (categoriesList.contains("Parks") && categories.contains("Park")) {
        AttractionNode nextAttraction = new Park(
            id, name, location, coords, price, rating, reviewCount);
        attractionsWithinBox.add(nextAttraction);
      }
    }
    prep.close();
    rs.close();
   // System.out.println("Length of attractions from database within bounding box is: "
    //    + attractionsWithinBox.size());
    for (int i = 0; i < (4 * prefNumStops + 4); i++) {
      double reqLat = 0;
      double reqLon = 0;
      if (boundingBoxBounds[0] > boundingBoxBounds[1]) { //start lat > end lat
        reqLat = ((boundingBoxBounds[0] - boundingBoxBounds[1]) / (prefNumStops + 2)) * i
            + boundingBoxBounds[1];
      } else {
        reqLat = ((boundingBoxBounds[1] - boundingBoxBounds[0]) / (prefNumStops + 2)) * i
            + boundingBoxBounds[0];
      }
      if (boundingBoxBounds[2] > boundingBoxBounds[3]) { //start lon > end lon
        reqLon = ((boundingBoxBounds[2] - boundingBoxBounds[3]) / (prefNumStops + 2)) * i
            + boundingBoxBounds[3];
      } else {
        reqLon = ((boundingBoxBounds[3] - boundingBoxBounds[2]) / (prefNumStops + 2)) * i
            + boundingBoxBounds[2];
      }
      List<AttractionNode> nodes = new ArrayList<>();
      for (int z = 0; z < 4; z++) {
        if (preferredStop[z] >= Constants.THIRTY_THRESHOLD && preferredStop[z]
            < Constants.FOURTY_THRESHOLD) {
          if (i % 3 == 0) {
            List<AttractionNode> req = createYelpRequest(z, reqLat, reqLon, costPref);
            nodes.addAll(req);
          }
        } else if (preferredStop[z] >= Constants.FOURTY_THRESHOLD && preferredStop[z]
            < Constants.HALF_THRESHOLD) {
          if (i % 3 == 0 || i % Constants.EIGHT == 0) {
            List<AttractionNode> req = createYelpRequest(z, reqLat, reqLon, costPref);
            nodes.addAll(req);
          }
        } else if (preferredStop[z] >= Constants.HALF_THRESHOLD && preferredStop[z]
            < Constants.SIXTY_THRESHOLD) {
          if (i % 2 == 0) {
            List<AttractionNode> req = createYelpRequest(z, reqLat, reqLon, costPref);
            nodes.addAll(req);
          }
        } else if (preferredStop[z] >= Constants.SIXTY_THRESHOLD && preferredStop[z]
            < Constants.THREE_FOURTHS_THRESHOLD) {
          if (i % 2 == 0 || i % 5 == 0) {
            List<AttractionNode> req = createYelpRequest(z, reqLat, reqLon, costPref);
            nodes.addAll(req);
          }
        } else if (preferredStop[z] >= Constants.THREE_FOURTHS_THRESHOLD && preferredStop[z]
            < Constants.HIGH_PREFERENCE) {
          if (i % 2 == 0 || i % 3 == 0) {
            List<AttractionNode> req = createYelpRequest(z, reqLat, reqLon, costPref);
            nodes.addAll(req);
          }
        } else if (preferredStop[z] >= Constants.HIGH_PREFERENCE) {
          List<AttractionNode> req = createYelpRequest(z, reqLat, reqLon, costPref);
          nodes.addAll(req);
        }
      }
      List<AttractionNode> removeRepeats = new ArrayList<>();
      for (AttractionNode n : nodes) {
        if (nameList.contains(n.getName())) {
          removeRepeats.add(n);
         // System.out.println("A repeat");
        } else {
          nameList.add(n.getName());
        }
      }
      nodes.removeAll(removeRepeats);
      attractionsWithinBox.addAll(nodes);
    }
    return attractionsWithinBox;
  }

  /**
   * Helper method that creates yelp api request.
   * @param i int of attraction type
   * @param reqLat request latitude
   * @param reqLon request longitude
   * @param costPref user's cost preference
   * @return nodes gotten in request
   * @throws IOException if request fails
   */
  public static List<AttractionNode> createYelpRequest(int i, double reqLat,
                                                       double reqLon, int costPref)
      throws IOException {
    List<AttractionNode> nodes = new ArrayList<>();
    if (i == 2) {
      List<String> param = new ArrayList<>();
      param.add("restaurant");
      param.add("food");
      param.add("bars");
      URL url = new URL(
          "https://api.yelp.com/v3/businesses/search?latitude=" + reqLat + "&longitude="
              + reqLon + "&categories="
              + "\"restaurant\",\"food\",\"bars\"" + "&price=" + costPref);
      List<AttractionNode> restaurants = yelpUrlToAttractions(url, "Restaurant");
      nodes.addAll(restaurants);
    }
    if (i == 1) {
      URL url = new URL(
          "https://api.yelp.com/v3/businesses/search?latitude=" + reqLat + "&longitude="
              + reqLon + "&categories=parks");
      List<AttractionNode> parks = yelpUrlToAttractions(url, "Park");
      nodes.addAll(parks);
    }
    if (i == 0) {
      URL url = new URL(
          "https://api.yelp.com/v3/businesses/search?latitude=" + reqLat + "&longitude="
              + reqLon + "&categories=museums");
      List<AttractionNode> museums = yelpUrlToAttractions(url, "Museum");
      nodes.addAll(museums);
      double reqLat2 = reqLat + Constants.LATOFFSET;
      double reqLon2 = reqLon + Constants.LATOFFSET;
      URL url2 = new URL(
          "https://api.yelp.com/v3/businesses/search?latitude=" + reqLat2 + "&longitude="
              + reqLon2 + "&categories=museums");
      List<AttractionNode> museums2 = yelpUrlToAttractions(url2, "Museum");
      nodes.addAll(museums2);
    }
    if (i == 3) {
      URL url = new URL(
          "https://api.yelp.com/v3/businesses/search?latitude=" + reqLat + "&longitude="
              + reqLon + "&categories=shoppingcenters");
      List<AttractionNode> shops = yelpUrlToAttractions(url, "Shop");
      nodes.addAll(shops);
      System.out.println(shops.size() + " many shops");
      double reqLat2 = reqLat + Constants.LATOFFSET;
      double reqLon2 = reqLon + Constants.LATOFFSET;
      URL url2 = new URL(
          "https://api.yelp.com/v3/businesses/search?latitude=" + reqLat2 + "&longitude="
              + reqLon2 + "&categories=shoppingcenters");
      List<AttractionNode> shops2 = yelpUrlToAttractions(url2, "Shop");
      nodes.addAll(shops2);
    }
    return nodes;
  }

  /**
   * Helper class that takes in yelp url,
   * makes an API request,
   * and creates an attraction node accordingly.
   *
   * @param yelpUrl    yelp url to make API request from
   * @param attraction type of attraction we are making request for
   * @return list of attraction nodes, created from data from API call
   * @throws IOException occurs when failure from API call.
   */
  public static List<AttractionNode> yelpUrlToAttractions(URL yelpUrl, String attraction)
      throws IOException {
    HttpURLConnection con = (HttpURLConnection) yelpUrl.openConnection();
    con.setRequestMethod("GET");
    String yelpKeyAbby =
        "RLI6ay67RiMOfxTbWDWV9iPiC4WyVydxY2ePdMV8SoTi74Ddx6Ez7fkBa8cY2LoI6_yqJaVTGGqsiUT_GeGh4"
            + "RO0gOyclHeDDaoD43pkWDQmjSHrM_DyifOLaGF7YHYx";
    String yelpKey =
        "PlFxZHha-zmiQvrMQUb12P0uD9s_GZRJzzGZqVSJFKgR4iDXj1aBw"
            + "OBuMc2DBFYkZODPw2V5PaJBMapJ5WA9PA3Lx2cGXq9FkzzT45m9t9I9gsXDdGCwQnuYu6J3YHYx";
    con.setRequestProperty("Authorization", "Bearer " + yelpKeyAbby);
    BufferedReader in = null;
    List<AttractionNode> attractions = new ArrayList<>();
    try {
      in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = in.readLine()) != null) {
        sb.append(line);
      }
      JSONObject json = new JSONObject(sb.toString());
      if (json.getInt("total") != 0) {
        for (int j = 0; j < json.getJSONArray("businesses").length(); j++) {
          try {
            //for each business create a json object to parse
            JSONObject businessJson = json.getJSONArray("businesses").getJSONObject(j);
            String name = businessJson.get("name").toString();
            String id = businessJson.get("id").toString();
            JSONObject locationJson = businessJson.getJSONObject("location");
            //location field is its own json object
            String[] loc = new String[] {locationJson.get("address1").toString(), locationJson.get(
                "city").toString(), locationJson.get("state").toString(),
                locationJson.get("zip_code").toString()};
            double[] coords = new double[] {businessJson.getJSONObject("coordinates").getDouble(
                "latitude"), businessJson.getJSONObject("coordinates").getDouble(
                "longitude")};
            double price;
            try { //try catch for price because sometimes it isnt a field
              price = businessJson.get("price").toString().toCharArray().length;
            } catch (JSONException e) {
              price = 0.0;
            }
            double rating = businessJson.getDouble("rating");
            double reviewCount = businessJson.getDouble("review_count");
            AttractionNode node;
            switch (attraction) {
              case "Park":
                node = new Park(id, name, loc, coords, price, rating, reviewCount);
                break;
              case "Restaurant":
                node = new Restaurant(id, name, loc, coords, price, rating, reviewCount);
                break;
              case "Museum":
                node = new Museum(id, name, loc, coords, price, rating, reviewCount);
                break;
              case "Shop":
                node = new Shop(id, name, loc, coords, price, rating, reviewCount);
                break;
              default:
                node = null;
                break;
            }
            if (node != null) {
              attractions.add(node);
            }
          } catch (JSONException e) {
            //System.out.println(e + "exception");
          }
        }
        return attractions;
      }

    } catch (IOException | JSONException e) {
      //System.out.println("ERROR: failed to open input stream");
      e.printStackTrace();
    }
    return attractions;
  }

  /**
   * Helper method to extract price field from attributes string.
   * @param attributes - single attribute entry from yelp db
   * @return price value is attribute string has price, else null
   */
  public static Integer findPriceField(String attributes) {
    Integer price = null;
    if (attributes.contains("RestaurantsPriceRange2")) {
      int startIndex = attributes.indexOf("RestaurantsPriceRange2");
      int priceIndex = startIndex + Constants.PRICE_INDEX;
      String priceString = attributes.charAt(priceIndex) + "";
      try {
        price = Integer.parseInt(priceString);
      } catch (Exception e) {

      }
    }
    return price;
  }
}
