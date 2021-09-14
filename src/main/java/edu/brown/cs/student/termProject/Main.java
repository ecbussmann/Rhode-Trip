package edu.brown.cs.student.termProject;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import attractions.Park;
import com.google.maps.GeoApiContext;
import database.BoundingBox;
import database.Database;
import graph.Dijkstra;
import joptsimple.OptionParser;
import joptsimple.OptionSet;
import org.json.JSONException;

import spark.ExceptionHandler;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.Spark;
import spark.TemplateViewRoute;
import spark.template.freemarker.FreeMarkerEngine;

import com.google.common.collect.ImmutableMap;

import freemarker.template.Configuration;

import org.json.JSONObject;
import com.google.gson.Gson;


/**
 * The Main class of our project. This is where execution begins.
 */
public final class Main {

  private static final int DEFAULT_PORT = 4567;
  private static final int TIMER_DELAY = 2000;
  public static final GeoApiContext
      APICONNECTION =
      new GeoApiContext.Builder().apiKey("AIzaSyAbX-U5h4aaNk2TTyrhYfFBG5a1C3zGU-c").build();

  // map of latest checkins: maps user id to their latest checkins

  /**
   * The initial method called when execution begins.
   *
   * @param args An array of command line arguments
   */
  public static void main(String[] args) {
    new Main(args).run();
  }

  private final String[] args;

  /**
   * main constructor.
   *
   * @param args args.
   */
  private Main(String[] args) {
    this.args = args;
  }

  /**
   * Method to start the program.
   */
  private void run() {
    Database.setYelpDatabaseConnection();
    // Parse command line arguments
    OptionParser parser = new OptionParser();
    parser.accepts("gui");
    parser.accepts("port").withRequiredArg().ofType(Integer.class)
        .defaultsTo(DEFAULT_PORT);
    OptionSet options = parser.parse(args);

    if (options.has("gui")) {
      runSparkServer((int) options.valueOf("port"));
    }

  }

  /**
   * creates free marker engine.
   *
   * @return free marker engine
   */
  private static FreeMarkerEngine createEngine() {
    Configuration config = new Configuration();
    File templates = new File("src/main/resources/spark/template/freemarker");
    try {
      config.setDirectoryForTemplateLoading(templates);
    } catch (IOException ioe) {
      System.out.printf("ERROR: Unable use %s for template loading.%n",
          templates);
      System.exit(1);
    }
    return new FreeMarkerEngine(config);
  }

  /**
   * runs the spark server.
   *
   * @param port given number
   */
  private void runSparkServer(int port) {
    Spark.port(port);
    Spark.externalStaticFileLocation("src/main/resources/static");
    Spark.exception(Exception.class, new ExceptionPrinter());


    Spark.options("/*", (request, response) -> {
      String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
      if (accessControlRequestHeaders != null) {
        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
      }

      String accessControlRequestMethod = request.headers("Access-Control-Request-Method");

      if (accessControlRequestMethod != null) {
        response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
      }

      return "OK";
    });

    FreeMarkerEngine freeMarker = createEngine();

    // Setup Spark Routes
    Spark.before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
    Spark.exception(Exception.class, new ExceptionPrinter());
    Spark.post("/route", new RouteHandler());
  }

  /**
   * Handle requests to the front page of our Stars website.
   */
  private static class FrontHandler implements TemplateViewRoute {
    @Override
    public ModelAndView handle(Request req, Response res) {
      Map<String, Object> variables = ImmutableMap.of("title",
          "Stars: Query the database", "answer", " ");
      return new ModelAndView(variables, "query.ftl");
    }
  }

  /**
   * Class that handles getting nearest traversable node for maps3 frontend.
   */
  private static class RouteHandler implements Route {
    @Override
    public Object handle(Request request, Response response) throws Exception {
      String error = "";
      List<AttractionNode> route = new ArrayList<>();
      try {
        JSONObject data = new JSONObject(request.body());
        //preferences which will be used for value heuristic
        int costPreference = data.getInt("costPref");
        double restaurantValue = data.getDouble("restValue");
        double museumValue = data.getDouble("musValue");
        double parkValue = data.getDouble("parkValue");
        double shopValue = data.getDouble("shopValue");
        double[] preferredStop = {museumValue, parkValue, restaurantValue, shopValue};
        double originLat = data.getDouble("originLat");
        double originLon = data.getDouble("originLon");
        double destLat = data.getDouble("destLat");
        double destLon = data.getDouble("destLon");
        double maxDist = 0.0;
        try {
          maxDist = data.getDouble("maxDist");
        } catch (JSONException e) {
          error = "Enter a number for the Maximum Distance field, without any punctuation";
          return new Gson().toJson(ImmutableMap.of("route", route, "error_message", error));
        }
        double middleLat = 0.0;
        double middleLong = 0.0;
        try {
          middleLat = data.getDouble("middleLat");
          middleLong = data.getDouble("middleLon");
        } catch (JSONException e) {
          System.out.println("Problem getting middle lat: " + e);
        }
        System.out.println("Middle Lat " + middleLat + " and middle long " + middleLong);
        int numStops = 0;
        try {
          numStops = data.getInt("stopPref");
        } catch (Exception e) {
          error = "Number of stops input is in the wrong format. Must be an integer greater than"
              + " 0";
          return new Gson().toJson(ImmutableMap.of("route", route, "error_message", error));
        }
        List<String> categories = new ArrayList<>();
        if (restaurantValue >= Constants.THRESHOLD_FOR_MIN_PREFERENCE) {
          categories.add("Restaurant");
        }
        if (parkValue >= Constants.THRESHOLD_FOR_MIN_PREFERENCE) {
          categories.add("Park");
        }
        if (museumValue >= Constants.THRESHOLD_FOR_MIN_PREFERENCE) {
          categories.add("Museum");
        }
        if (shopValue >= Constants.THRESHOLD_FOR_MIN_PREFERENCE) {
          categories.add("Shop");
        }
        Dijkstra dijkstra = new Dijkstra(APICONNECTION);
        if (numStops > 0) {
          double miles = dijkstra.distanceFormulaAPI(originLat,
              originLon, destLat, destLon) / Constants.THOUSAND / Constants.MILES_TO_KILOMETERS;
          System.out.println("Google Maps API: " + miles);
          //check to make sure the inputted max distance is not greater than the minimum distance
          if ((maxDist * Constants.MILES_TO_KILOMETERS) < (dijkstra.distanceFormulaAPI(originLat,
              originLon, destLat, destLon) / Constants.THOUSAND)) {
            error = "It is impossible to complete the trip in " + maxDist + " miles";
            System.out.println(error);
            return new Gson().toJson(ImmutableMap.of("route", route, "error_message", error));
          }
          //if the user has to make an intermediate step
          if (middleLat != 0.0 && middleLong != 0.0) {
            System.out.println("Intermediate Stop!");
            double proportionBeforeStop = dijkstra.distanceFormulaAPI(originLat, originLon,
                middleLat,
                middleLong) / (dijkstra.distanceFormulaAPI(originLat, originLon, middleLat,
                middleLong) + dijkstra.distanceFormulaAPI(middleLat, middleLong, destLat, destLon));
            double firstHalf = proportionBeforeStop * numStops;
            int numStopsFirstHalf = (int) Math.floor(firstHalf);
            int numStopsSecondHalf = numStops - numStopsFirstHalf;
            System.out.println("First half is : " + numStopsFirstHalf + " and second half is "
                + numStopsSecondHalf);
            dijkstra
                .setPreferences(preferredStop, costPreference, BoundingBox.findAttractionsBetween(
                    new double[] {originLat, originLon},
                    new double[] {middleLat, middleLong}, categories, numStopsFirstHalf,
                    costPreference, preferredStop));
            List<AttractionNode> route1 = dijkstra.execute(new double[] {originLat, originLon},
                new double[] {middleLat, middleLong}, numStopsFirstHalf);
            //set new preferences
            dijkstra
                .setPreferences(preferredStop, costPreference, BoundingBox.findAttractionsBetween(
                    new double[] {middleLat, middleLong},
                    new double[] {destLat, destLon}, categories, numStopsSecondHalf,
                    costPreference, preferredStop));
            List<AttractionNode> route2 = dijkstra.execute(new double[] {middleLat, middleLong},
                new double[] {destLat, destLon}, numStopsSecondHalf);

            route.addAll(route1);
            //dumby "node" to represent the user inputted intermediate stop
            AttractionNode med = new Park("0", "Intermediate Stop", new String[] {""},
                new double[] {middleLat, middleLong}, 0.0, 0.0, 0.0);
            med.setCost(0.0);
            route.add(med);
            route.addAll(route2);
            System.out.println("The length of your first part is " + route1.size() + " and your "
                + "second part is " + route2.size());

          } else {
            List<AttractionNode> attractions = BoundingBox.findAttractionsBetween(
                new double[] {originLat, originLon}, new double[] {destLat, destLon},
                categories, numStops, costPreference, preferredStop);
            System.out.println("number of nodes: " + attractions.size());
            dijkstra.setPreferences(preferredStop, costPreference, attractions);
            route = dijkstra.execute(new double[] {originLat, originLon},
                new double[] {destLat, destLon}, numStops);
          }
        } else if (numStops == 0) {
          error = "This route has no stops";
          return new Gson().toJson(ImmutableMap.of("route", route, "error_message", error));
        } else if (numStops < 0) {
          error = "The number of stops cannot be negative";
          return new Gson().toJson(ImmutableMap.of("route", route, "error_message", error));
        }
        double routeLength = 0;
        for (int i = 1; i < route.size(); i++) {
          routeLength += dijkstra.distanceFormulaAPI(route.get(i - 1).getCoordinates()[0],
              route.get(i - 1).getCoordinates()[1], route.get(i).getCoordinates()[0],
              route.get(i).getCoordinates()[1]);
        }
        routeLength += dijkstra.distanceFormulaAPI(route.get(0).getCoordinates()[0],
            route.get(0).getCoordinates()[1], originLat, originLon);
        routeLength += dijkstra.distanceFormulaAPI(route.get(route.size() - 1).getCoordinates()[0],
            route.get(route.size() - 1).getCoordinates()[1], destLat, destLon);
        routeLength = (routeLength / Constants.THOUSAND) / Constants.MILES_TO_KILOMETERS;
        System.out.println("Dijkstra's Route is this length " + route.size() + " and this many "
            + "miles: " + routeLength);
        if (routeLength > maxDist) {
          double diff = Math.ceil(routeLength - maxDist);
          error = "Unfortunately, we cannot plan you a roadtrip under " + maxDist + " miles. You"
              + " would need to increase your distance constraint by " + diff;
          System.out.println(error);
          route = new ArrayList<>();
        }
        for (AttractionNode r : route) {
          System.out.println(r.getName() + " "
              + " and a cost of " + r.getCost() + " and a value of " + r.getValue());
        }
      } catch (Exception e) {
        System.out.println("problem with dijkstras");
        e.printStackTrace();
      }
      Map<String, Object> variables = ImmutableMap.of("route", route, "error_message", error);
      return new Gson().toJson(variables);
    }
  }

  /**
   * Display an error page when an exception occurs in the server.
   */
  private static class ExceptionPrinter implements ExceptionHandler {
    @Override
    public void handle(Exception e, Request req, Response res) {
      res.status(500);
      StringWriter stacktrace = new StringWriter();
      try (PrintWriter pw = new PrintWriter(stacktrace)) {
        pw.println("<pre>");
        e.printStackTrace(pw);
        pw.println("</pre>");
      }
      res.body(stacktrace.toString());
    }
  }


}
