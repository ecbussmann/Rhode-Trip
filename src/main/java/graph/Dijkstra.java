package graph;


import attractions.Park;

import java.util.*;

/**
 * Th Dijkstra class performs Dijkstra's algorithm on a list of attraction nodes
 */
public class Dijkstra {
  private List<AttractionNode> nodes;
  private HashMap<AttractionNode, Boolean> visited; //keep track of visited nodes
  private HashMap<AttractionNode, Double> distances; //keep track of distances between node and end
  private HashMap<AttractionNode,AttractionNode> previous; //keep track of previous pointers

  public Dijkstra(List<AttractionNode> n){
    nodes = n;
    visited = new HashMap<>();
    distances = new HashMap<>();
    previous = new HashMap<>();
    for (AttractionNode node: nodes){
      visited.put(node, false);
      distances.put(node, distanceFormula(0.0,0.0,0.0,0.0));
      node.setCost(node.generateValue() + distances.get(node)); //set the cost of the node based off
      // of
      // its
      // calculated
      // value and its distance to the target
    }
  }
  public List<AttractionNode> execute(double[] starting, double[] ending){
    for (AttractionNode node: nodes){
      distances.put(node, distanceFormula(0.0,0.0,0.0,0.0));
      node.setCost(node.generateValue() + distances.get(node)); //set the cost of the node based
      // on it's calculated value and its distance to the target
    }
    List <AttractionNode> shortestPath = new ArrayList<>();
    double[] target = ending; //get the target
    if (starting == ending) { //stop if the start and end node are the same
      return null;
    }
    PriorityQueue<AttractionNode> pq = new PriorityQueue(new Comparator<AttractionNode>() {
      public int compare(AttractionNode o1, AttractionNode o2) {
        //The comparator implements the A* extension as it considers both path cost and
        // distance to target for priority
        if ((o1.getCost() + distances.get(o1)) > (o2.getCost() + distances.get(o2))) {
          return 1;
        }
        if ((o2.getCost() + distances.get(o2)) > (o1.getCost() + distances.get(o1))) {
          return -1;
        }
        return 0;
      }
    });
    //Make a dumby attraction node to represent start and end? may be a better way to handle this
    AttractionNode start = new Park("0", "", new String[0], starting,0.0,0.0 );
    AttractionNode end = new Park("0", "", new String[0], target,0.0,0.0 );
    distances.replace(end, 0.0); //setting the distance of end node to 0
    start.setCost(0.0);
    pq.add(start);
    while (!(pq.isEmpty()) && !(visited.get(end))) {
      AttractionNode current = pq.poll();
      visited.replace(current,true); //mark the popped node as visited
        for (AttractionNode node: nodes) {
          //if the current nodes cost plus the distance between the current and the node you are
          // examining is less than the node you are examining's cost
          double edgeWeight = distanceFormula(current.getCoordinates()[0],
            current.getCoordinates()[1], node.getCoordinates()[0], node.getCoordinates()[1]);
            if ((current.getCost() + edgeWeight) <= node.getCost()) {
              node.setCost(current.getCost() + edgeWeight + node.generateValue());
              previous.put(node, current);
              pq.add(node);
            }
          }
        }
    return shortestPath;
  }

  /**
   * The distance formula will use the google maps API to compute the distance between a node and
   * the path
   */
  private Double distanceFormula(double la1,double lo1, double la2, double lo2){
    return 0.0;

  }
}
