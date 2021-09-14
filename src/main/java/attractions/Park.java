package attractions;

import graph.AttractionNode;

/**
 * The park class stores information of parks and implements the attraction node interface for
 * Dijkstra's
 */
public class Park implements AttractionNode {
  private String id;
  private String name;
  private String[] location;
  private double[] coordinates;
  private double price;
  private double rating;
  private double cost;
  private boolean visit = false;
  private double distance = 0;
  private int numPrev = 0;

  /**
   * The constructor sets the fields
   * @param parkId the id
   * @param parkName the name of the stop
   * @param loc the address of the park in an array
   * @param coords the latitude/longitude coordinates
   * @param p the price associated with the stop
   * @param rate the five star rating
   */
  public Park(String parkId, String parkName, String[] loc, double[] coords, Double p,
              Double rate){
    id = parkId;
    name = parkName;
    location = loc;
    coordinates = coords;
    price = p;
    rating = rate;
    cost = Double.POSITIVE_INFINITY;

  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String[] getLocation() {
    return location;
  }

  @Override
  public double[] getCoordinates() {
    return coordinates;
  }

  @Override
  public double getPrice() {
    return price;
  }

  @Override
  public double getRating() {
    return rating;
  }

  @Override
  public double generateValue(double PreferredPrice, double PreferredStop) {
    return 0;
  }

  @Override
  public void setCost(double c) {
    cost = c;
  }

  @Override
  public double getCost() {
    return cost;
  }

  @Override
  public void setDistance(double c) {distance = c;}

  @Override
  public double getDistance() { return distance; }

  @Override
  public void setVisited(boolean c) { visit = c; }

  @Override
  public boolean getVisited() { return visit; }

  @Override
  public void setNumPrev(int c) {numPrev = c;}

  @Override
  public int getNumPrev() {return numPrev;}

  @Override
  public void reset() {
    distance = 0;
    visit = false;
    numPrev = 0;
  }

  @Override
  public int getType() {return 1;}

}
