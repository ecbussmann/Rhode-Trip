package attractions;

import edu.brown.cs.student.termProject.AttractionNode;
import edu.brown.cs.student.termProject.Constants;

/**
 * The Shop class stores information of Shops and implements the attraction node interface
 * for Dijkstra's.
 */
public class Shop implements AttractionNode {
  private String id;
  private String name;
  private String[] location;
  private double[] coordinates;
  private double price;
  private double rating;
  private double cost;
  private double distance = 0;
  private boolean visit = false;
  private int numPrev = 0;
  private double value = 0;
  private String nodeType = "shop";
  private double numReviews;


  /**
   * The constructor sets the fields.
   * @param shopId the id
   * @param shopName the name of the stop
   * @param loc the address of the Museum in an array
   * @param coords the latitude/longitude coordinates
   * @param p the price associated with the stop
   * @param rate the five star rating
   * @param reviewCount number of reviews left at this establishment
   */
  public Shop(String shopId, String shopName, String[] loc, double[] coords, Double p,
                Double rate,  double reviewCount) {
    id = shopId;
    name = shopName;
    location = loc;
    coordinates = coords;
    price = p;
    rating = rate;
    cost = Double.POSITIVE_INFINITY;
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
    double shopValue = preferredStop;
    value = (1 - shopValue / Constants.VALUE_BOUND) * dist * Constants.PREFERENCE_VALUE_SCALE;
    value = value + (Constants.AVERAGE_REVIEWS_SHOPS / numReviews) * dist
      * Constants.REVIEW_SCALE;
    value = value + (1 - rating / Constants.MAX_RATING) * dist;
    value = value + (Math.abs(price - preferredPrice)) * dist * Constants.PRICE_SCALE;
    value = value * Constants.VALUE_SCALE;
    value = value * Constants.VALUE_SCALE_SHOPS;
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
    return 3;
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

