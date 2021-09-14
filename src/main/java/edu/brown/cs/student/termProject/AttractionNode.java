package edu.brown.cs.student.termProject;

/**
 * The attraction node interface is implemented by each attraction and is used for Dijkstra's.
 */
public interface AttractionNode {

  /**
   * Gets the unique Id of the node.
   * @return Id of the node
   */
  String getId();

  /**
   * Gets the non-unique name of the node.
   * @return name of the node
   */
  String getName();

  /**
   * Gets the location of the node.
   * @return [address, city, state, postal_code]
   */
  String[] getLocation();

  /**
   * Gets the latitude/longitude location of the node.
   * @return [lat,long]
   */
  double[] getCoordinates();

  /**
   * Gets price associated with a node.
   * @return price where 1 = $, 2 = $$, 3 = $$$
   */
  double getPrice();

  /**
   * Gets rating of a node.
   * @return rating between 0 and 5
   */
  double getRating();

  /**
   * Node's value heuristic.
   * @param preferredPrice value indicating user's preferred price point
   * @param preferredStop value indicating how much user prefers that type of stop
   * @param dist from current node to prev
   * @return value heuristic used in Dijkstra's PQ
   */
  double generateValue(double preferredPrice, double preferredStop, double dist);

  /**
   * Sets the "cost" associated with the node to be used in Dijkstra's.
   * @param c the cost
   */
  void setCost(double c);

  /**
   * Retrieves the "cost" associated with the node to be used in Dijkstra's.
   * @return the cost
   */
  double getCost();

  /**
   * Set's distance.
   * @param c double of distance to be set.
   */
  void setDistance(double c);

  /**
   * Returns distance using node's distance calculation.
   * @return double representing distance
   */
  double getDistance();

  /**
   * sets boolean representing if node has been visited.
   * @param c boolean
   */
  void setVisited(boolean c);

  /**
   * Returned boolean of visited.
   * @return true if visited, false otherwise.
   */
  boolean getVisited();

  /**
   * Sets the number of previous nodes in route.
   * @param c int of num previous nodes
   */
  void setNumPrev(int c);

  /**
   * Get's number of previous stops.
   * will be used in value heuristic
   * @return integer of number of prev
   */
  int getNumPrev();

  /**
   * integer associated with Attraction type.
   * 0 = Museum
   * 1 = Park
   * 2 = Restaurant
   * 3 = Shop
   * @return integer
   */
  int getType();
  /**
   * returns the node's "value".
   * @return the value
   */
  double getValue();

  /**
   * returns the node's number of reviews.
   * @return the number of revies
   */
  double getNumReviews();

}
