package edu.brown.cs.student.termProject;

import attractions.Museum;
import attractions.Park;
import attractions.Shop;
import attractions.Restaurant;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class AttractionNodesTests {

  @Test
  public void testMuseumMethods(){
    Museum museum = new Museum("id", "name", new String[] {"location"},
        new double[] {0.12312, 120.12}, 4.0, 3.4, 13);
    assertEquals(museum.getId(), "id");
    assertEquals(museum.getName(), "name");
    assertEquals(String.valueOf(museum.getRating()), "3.4");
    assertEquals(String.valueOf(museum.getPrice()), "4.0");
    assertEquals(String.valueOf(museum.getNumReviews()), "13.0");
    assertEquals(String.valueOf(museum.getType()), "0");
  }

  @Test
  public void testParkMethods(){
    Park park = new Park("id", "name", new String[] {"location"},
        new double[] {0.12312, 120.12}, 4.0, 3.4, 13);
    assertEquals(park.getId(), "id");
    assertEquals(park.getName(), "name");
    assertEquals(String.valueOf(park.getRating()), "3.4");
    assertEquals(String.valueOf(park.getPrice()), "4.0");
    assertEquals(String.valueOf(park.getNumReviews()), "13.0");
    assertEquals(String.valueOf(park.getType()), "1");
  }

  @Test
  public void testShopMethods(){
    Shop shop = new Shop("id", "name", new String[] {"location"},
        new double[] {0.12312, 120.12}, 4.0, 3.4, 13);
    assertEquals(shop.getId(), "id");
    assertEquals(shop.getName(), "name");
    assertEquals(String.valueOf(shop.getRating()), "3.4");
    assertEquals(String.valueOf(shop.getPrice()), "4.0");
    assertEquals(String.valueOf(shop.getNumReviews()), "13.0");
    assertEquals(String.valueOf(shop.getType()), "3");
  }

  @Test
  public void testRestaurantMethods(){
    Restaurant restaurant = new Restaurant("id", "name", new String[] {"location"},
        new double[] {0.12312, 120.12}, 4.0, 3.4, 13);
    assertEquals(restaurant.getId(), "id");
    assertEquals(restaurant.getName(), "name");
    assertEquals(String.valueOf(restaurant.getRating()), "3.4");
    assertEquals(String.valueOf(restaurant.getPrice()), "4.0");
    assertEquals(String.valueOf(restaurant.getNumReviews()), "13.0");
    assertEquals(String.valueOf(restaurant.getType()), "2");
  }
}