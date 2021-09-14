package graph;

/**
 * The attraction node interface is implemented by each attraction and is used for Dijkstra's
 */
public interface AttractionNode {

  /**
   * Gets the unique Id of the node
   *
   * @return Id of the node
   */
  String getId();

  /**
   * Gets the non-unique name of the node
   *
   * @return name of the node
   */
  String getName();

  /**
   * Gets the location of the node
   *
   * @returns [address, city, state, postal_code]
   */
  String[] getLocation();

  /**
   * Gets the latitude/longitude location of the node
   *
   * @returns [lat,long]
   */
  double[] getCoordinates();

  /**
   * Gets price associated with a node
   *
   * @returns price where 1 = $, 2 = $$, 3 = $$$
   */
  double getPrice();

  /**
   * Gets rating of a node
   *
   * @returns rating between 0 and 5
   */
  double getRating();

  /**
   * Gets the value of the node computed with our value algorithm
   *
   * @returns numerical "value"
   */
  double generateValue(double PreferredPrice, double PreferredStop);

  /**
   * Sets the "cost" associated with the node to be used in Dijkstra's
   *
   * @param c the cost
   */
  void setCost(double c);

  /**
   * Retrieves the "cost" associated with the node to be used in Dijkstra's
   *
   * @returns the cost
   */
  double getCost();

  void setDistance(double c);

  double getDistance();

  void setVisited(boolean c);

  boolean getVisited();

  void setNumPrev(int c);

  int getNumPrev();

  void reset();

  int getType();


//  /**
//   * check if the node has been visited
//   *
//   * @returns boolean to represent if it has been visited
//   */
//  boolean visited();

}
