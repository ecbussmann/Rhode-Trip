package graph;

import attractions.Park;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;
import edu.brown.cs.student.termProject.AttractionNode;
import edu.brown.cs.student.termProject.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;


/**
 * Th Dijkstra class performs Dijkstra's algorithm on a list of attraction nodes.
 */
public class Dijkstra {
  private List<AttractionNode> nodes;
  private HashMap<AttractionNode, Boolean> visited; //keep track of visited nodes
  private HashMap<AttractionNode, Double> distances; //keep track of distances between node and end
  private HashMap<AttractionNode, AttractionNode> previous; //keep track of previous pointers
  private double[] preferredStop;
  private int costPreference;
  private GeoApiContext connection;

  /**
   * Class Constructor.
   * @param apiConnection to yelp API
   */
  public Dijkstra(GeoApiContext apiConnection) {
    connection = apiConnection;
    nodes = new ArrayList<>();
    visited = new HashMap<>();
    distances = new HashMap<>();
    previous = new HashMap<>();
  }

  /**
   * Method that executes Djikstras.
   * In this case an altered Djikstras, that returns most valued route, not quickest.
   * @param starting starting locations: lat, lon.
   * @param ending ending locations: lat, long.
   * @param numStops preferred number of stops.
   * @return lost of nodes in route.
   */
  public List<AttractionNode> execute(double[] starting, double[] ending, int numStops) {
    double pathDistance = distanceFormula(starting[0], starting[1], ending[0], ending[1]);
    visited = new HashMap<>();
    distances = new HashMap<>();
    previous = new HashMap<>();
    //Comparator considers value generated as well as a cost by distance in determining how to
    // rank the stops in the priority queue
    PriorityQueue<AttractionNode> pq = new PriorityQueue(new Comparator<AttractionNode>() {
      public int compare(AttractionNode o1, AttractionNode o2) {
        if ((o1.getCost() + o1.generateValue(costPreference,
            preferredStop[o1.getType()], pathDistance))
            > (o2.getCost()) + o2.generateValue(costPreference,
              preferredStop[o2.getType()], pathDistance)) {
          return 1;
        }
        if ((o2.getCost() + o2.generateValue(costPreference,
            preferredStop[o2.getType()], pathDistance))
            > (o1.getCost() + o1.generateValue(costPreference,
              preferredStop[o1.getType()], pathDistance))) {
          return -1;
        }
        return 0;
      }
    });
    List<AttractionNode> shortestPath = new ArrayList<>();
    double[] target = ending; //get the target
    if (starting == ending) { //stop if the start and end node are the same
      return null;
    }
    //Make a dumby attraction node to represent start and end
    AttractionNode start = new Park("0", "starting Node", new String[0], starting,
        0.0, 0.0, 0.0);
    nodes.add(start);
    AttractionNode end = new Park("0", "ending Node", new String[0], target,
        0.0, 0.0, 0.0);
    nodes.add(end);
    distances.replace(end, 0.0); //setting the distance of end node to 0
    pq.add(start);
    for (AttractionNode node: nodes) {
      visited.put(node, false);
      previous.put(node, null);
      node.setCost(Double.POSITIVE_INFINITY);
    }
    start.setCost(0.0);
    while (!(pq.isEmpty()) && !(visited.get(end))) {
      AttractionNode current = pq.poll();
      visited.replace(current, true); //mark the popped node as visited
      List<AttractionNode> connectedNodes = getConnectedNodes(current, end, pathDistance, numStops);
      for (AttractionNode node: connectedNodes) {
        //if the current nodes cost plus the distance between the current and the node you are
        // examining is less than the node you are examining's cost
        double edgeWeight = distanceFormula(current.getCoordinates()[0],
            current.getCoordinates()[1], node.getCoordinates()[0], node.getCoordinates()[1]);
        if ((current.getCost() + edgeWeight) < node.getCost()) {
          node.setCost(current.getCost() + edgeWeight); //reset the cost
          previous.replace(node, current); //reset the previous pointer
          pq.add(node);
        }
      }
    }
    AttractionNode curr = end;
    if (previous.get(curr) == start) {
    //  System.out.println("shortest path is direct one :(");
    }
    while (previous.get(curr) != start) { //compile the "best" path
      shortestPath.add(previous.get(curr));
      curr = previous.get(curr);
    }
    Collections.reverse(shortestPath); //want path to be in order from start to end
    return shortestPath;
  }

  /**
   * The distance formula will use the google maps API to compute the distance between a node and
   * the path.
   *
   * @param lat1 latitude of point 1
   * @param long1 longitude of point 1
   * @param lat2 latitude of point 2
   * @param long2 longitude of point 2
   * @return driving distance in Meters
   */
  public double distanceFormulaAPI(double lat1, double long1, double lat2, double long2) {
    try {
      LatLng start = new LatLng(lat1, long1);
      LatLng end = new LatLng(lat2, long2);
      DirectionsApiRequest req = DirectionsApi.newRequest(connection)
          .origin(start).destination(end).language("en");
      DirectionsResult response = req.await();
      if (response.routes.length > 0) {
        double dist = 0;
        for (int i = 0; i < response.routes[0].legs.length; i++) {
          dist += response.routes[0].legs[i].distance.inMeters;
        }
        return dist;
      }
    } catch (ApiException | InterruptedException | IOException e) {
      e.printStackTrace();
    }
    return 0.0;
  }

  /**
   * Haversine distance formula.
   * @param lat1 latitude of point 1
   * @param long1 longitude of point 1
   * @param lat2 latitude of point 2
   * @param long2 longitude of point 2
   * @return driving distance in Meters
   */
  private double distanceFormula(double lat1, double long1, double lat2, double long2) {
    double latDist = lat2 - lat1;
    double longDist = long2 - long1;
    latDist = Math.toRadians(latDist);
    longDist = Math.toRadians(longDist);
    double la1 = Math.toRadians(lat1);
    double la2 = Math.toRadians(lat2);
    double a = Math.pow(Math.sin(latDist / 2), 2) + Math.cos(la1) * Math.cos(la2)
        * Math.pow(Math.sin((longDist / 2)), 2);
    return (2.0 * Constants.EARTH_RADIUS * Math.asin(Math.sqrt(a)));
  }

  /**
   * Gets a list of nodes "connected" to the current node as specified by the "ideal spacing"
   * between nodes. Works to evenly space out stops on the roadtrip
   * @param node the node to find connections from
   * @param end the end node
   * @param distance the distance between the start and end nodes
   * @param numStops the number of stops ideally between start and end node
   * @return list of connected nodes
   */
  private List<AttractionNode> getConnectedNodes(AttractionNode node, AttractionNode end,
                                                 double distance, int numStops) {
    double spacing = distance / numStops;
    List<AttractionNode> connects = nodes;
    List<AttractionNode> toRemove = new ArrayList<>(); //To avoid concurrent modification error
    for (AttractionNode n: connects) {
      if (distanceFormula(n.getCoordinates()[0],
          n.getCoordinates()[1], end.getCoordinates()[0], end.getCoordinates()[1])
          >= distanceFormula(node.getCoordinates()[0], node.getCoordinates()[1],
              end.getCoordinates()[0], end.getCoordinates()[1])
      ) {
        toRemove.add(n);
        //remove the node from the list of possible connecting nodes if it is
        // farther from the target than the current node
      }
    }
    for (AttractionNode r: toRemove) {
      connects.remove(r);
    }
    Collections.sort(connects, new Comparator<AttractionNode>() {
      @Override
      //this comparator will sort the nodes based on HOW CLOSE THEY are to the target spacing,
      // i.e. nodes that are spaced closest to the "spacing" metric will be considered connected
      public int compare(AttractionNode o1, AttractionNode o2) {
        double distance1 = distanceFormula(o1.getCoordinates()[0], o1.getCoordinates()[1],
            node.getCoordinates()[0], node.getCoordinates()[1]);
        double distance2 = distanceFormula(o2.getCoordinates()[0], o2.getCoordinates()[1],
            node.getCoordinates()[0], node.getCoordinates()[1]);
        if (Math.abs(distance1 - spacing) > Math.abs(distance2 - spacing)) {
          return 1;
        }
        if (Math.abs(distance2 - spacing) > Math.abs(distance1 - spacing)) {
          return -1;
        } else {
          return 0;
        }
      }
    });

    //return a list of the number of connections specified in the constants class. Pick the n
    // closest elements to the "ideal distance"
    if (connects.size() >= Constants.NUM_CONNECTIONS) {
      return connects.subList(0, Constants.NUM_CONNECTIONS);
    } else if (connects.size() >= 1) {
      //if the list of potential connections is less than the specified number of connections for
      // each node just return the whole list
      return connects;
    } else {
      return new ArrayList<>();
    }
  }

  /**
   * Public method to set the user's preferences of stop types, as indicated in the front end.
   * Called in Main.java from the routeHandler.
   * @param prefStop list of how much each type of stop is preferred
   * @param costPref user's preference between $, $$, $$$
   * @param nodeList the list of attraction nodes
   */
  public void setPreferences(double[] prefStop, int costPref, List<AttractionNode> nodeList) {
    preferredStop = prefStop;
    costPreference = costPref;
    nodes = nodeList;
  }
}
