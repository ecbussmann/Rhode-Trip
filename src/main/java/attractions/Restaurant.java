package attractions;

import edu.brown.cs.student.termProject.AttractionNode;
import edu.brown.cs.student.termProject.Constants;

/**
 * The Restaurant class stores information of Restaurants and implements the attraction node
 * interface for Dijkstra's.
 */
public class Restaurant implements AttractionNode {
  private String id;
  private String name;
  private String[] location;
  private double[] coordinates;
  private double price;
  private double rating;
  private double value;
  private double cost;
  private boolean visit = false;
  private double distance = 0;
  private int numPrev = 0;
  private String nodeType = "restaurant";
  private double numReviews;

  /**
   * The constructor sets the fields.
   * @param restaurantId the id
   * @param restaurantName the name of the stop
   * @param loc the address of the Restaurant in an array
   * @param coords the latitude/longitude coordinates
   * @param p the price associated with the stop
   * @param rate the five star rating
   * @param reviewCount number of reviews left at this establishment
   */
  public Restaurant(String restaurantId, String restaurantName, String[] loc, double[] coords,
                   Double p, Double rate,  double reviewCount) {
    id = restaurantId;
    name = restaurantName;
    location = loc;
    coordinates = coords;
    price = p;
    rating = rate;
    cost = Double.POSITIVE_INFINITY;
    value = 0;
    numReviews = reviewCount;

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
  public double generateValue(double preferredPrice, double preferredStop, double dist) {
    double restaurantValue = preferredStop;
    value = (1 - restaurantValue / Constants.VALUE_BOUND)
        * dist * Constants.PREFERENCE_VALUE_SCALE;
    value = value + (Constants.AVERAGE_REVIEWS_RESTAURANTS / numReviews)
        * dist * Constants.REVIEW_SCALE;
    value = value + (1 - rating / Constants.MAX_RATING) * dist;
    value = value + (Math.abs(price - preferredPrice)) * dist * Constants.PRICE_SCALE;
    value = value * Constants.VALUE_SCALE;
    value = value * Constants.VALUE_SCALE_RESTAURANTS;
    return value;
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
  public void setDistance(double c) {
    distance = c;
  }

  @Override
  public double getDistance() {
    return distance;
  }

  @Override
  public void setVisited(boolean c) {
    visit = c;
  }

  @Override
  public boolean getVisited() {
    return visit;
  }

  @Override
  public void setNumPrev(int c) {
    numPrev = c;
  }

  @Override
  public int getNumPrev() {
    return numPrev;
  }

  @Override
  public int getType() {
    return 2;
  }

  @Override
  public double getValue() {
    return value;
  }

  @Override
  public double getNumReviews() {
    return numReviews;
  }
}
