package edu.brown.cs.student.termProject;

import database.BoundingBox;

import database.Database;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests for the BoundingBox class.
 */
public class BoundingBoxTests {

  @Test
  public void testFindBoundingBoxBoundsSameStartEnd() {
    assertArrayEquals(BoundingBox.findBoundingBoxBounds(
            new double[]{0, 0}, new double[]{0, 0}),
            new double[]{0, 0, 0, 0}, 0);

    assertArrayEquals(BoundingBox.findBoundingBoxBounds(
            new double[]{10, 10}, new double[]{10, 10}),
            new double[]{10, 10, 10, 10}, 0);
  }

  @Test
  public void testFindBoundingBoxBoundsVerticalLine() {
    assertArrayEquals(BoundingBox.findBoundingBoxBounds(
            new double[]{-10, 5}, new double[]{10, 5}),
            new double[]{-10, 10, 5, 5}, 0);

    assertArrayEquals(BoundingBox.findBoundingBoxBounds(
            new double[]{34, 10}, new double[]{33, 10}),
            new double[]{33, 34, 10, 10}, 0);
  }

  @Test
  public void testFindBoundingBoxBoundsHorizontalLine() {
    assertArrayEquals(BoundingBox.findBoundingBoxBounds(
            new double[]{-3, 10}, new double[]{-3, 0}),
            new double[]{-3, -3, 0, 10}, 0);

    assertArrayEquals(BoundingBox.findBoundingBoxBounds(
            new double[]{0, 5}, new double[]{0, 5.01}),
            new double[]{0, 0, 5, 5.01}, 0);
  }

  @Test
  public void testFindBoundingBoxBoundsAllDifferentCoordinates() {
    assertArrayEquals(BoundingBox.findBoundingBoxBounds(
            new double[]{-1, 2}, new double[]{5, 4}),
            new double[]{-1, 5, 2, 4}, 0);

    assertArrayEquals(BoundingBox.findBoundingBoxBounds(
            new double[]{-1, 4}, new double[]{5, 2}),
            new double[]{-1, 5, 2, 4}, 0);

    assertArrayEquals(BoundingBox.findBoundingBoxBounds(
            new double[]{5, 2}, new double[]{-1, 4}),
            new double[]{-1, 5, 2, 4}, 0);

    assertArrayEquals(BoundingBox.findBoundingBoxBounds(
            new double[]{5, 4}, new double[]{-1, 2}),
            new double[]{-1, 5, 2, 4}, 0);
  }

  @Test
  public void testExpandBoundingBoxBounds() {
    assertArrayEquals(BoundingBox.expandBoundingBoxBounds(
            new double[]{-1, 5, 2, 4}, 0),
            new double[]{-1, 5, 2, 4}, 0);

    assertArrayEquals(BoundingBox.expandBoundingBoxBounds(
            new double[]{-1, 5, 2, 4}, 2),
            new double[]{-3, 7, 0, 6}, 0);

    assertArrayEquals(BoundingBox.expandBoundingBoxBounds(
            new double[]{-1, 5, 2, 4}, -3),
            new double[]{2, 2, 5, 1}, 0);
  }

  @Test
  public void testFindAttractionsBetweenSameStartEnd() {
    Database.setYelpDatabaseConnection();
    try {
      List<AttractionNode> attractions = BoundingBox.findAttractionsBetween(
              new double[]{40, -105}, new double[]{40, -105},
              Arrays.asList("Restaurant", "Museum", "Park", "Shop"),
      5, 5, new double[]{2, 2, 2, 2});
      assertTrue(containsAttractionWithID(attractions, "6iYb2HFDywm3zjuRg0shjw"));
      assertFalse(containsAttractionWithID(attractions, "D4JtQNTI4X3KcbzacDJsMw"));
    } catch (Exception e) {
      assertTrue(false);
    }
  }

  @Test
  public void testFindAttractionsBetweenDifferentStartEnd() {
    Database.setYelpDatabaseConnection();
    try {
      List<AttractionNode> attractions = BoundingBox.findAttractionsBetween(
              new double[]{40, -105}, new double[]{39, -106},
              Arrays.asList("Restaurant", "Museum", "Park", "Shop"),
              5, 5, new double[]{2, 2, 2, 2});
      assertTrue(containsAttractionWithID(attractions, "6iYb2HFDywm3zjuRg0shjw"));
      assertFalse(containsAttractionWithID(attractions, "tCbdrRPZA0oiIYSmHG3J0w"));
    } catch (Exception e) {
      assertTrue(false);
    }
  }

  @Test
  public void testFindAttractionsBetweenEmptyCategoriesList() {
    Database.setYelpDatabaseConnection();
    try {
      List<AttractionNode> attractions = BoundingBox.findAttractionsBetween(
              new double[]{40, -105}, new double[]{39, -106},
              Arrays.asList(),
              5, 5, new double[]{2, 2, 2, 2});
      assertTrue(attractions.isEmpty());
    } catch (Exception e) {
      assertTrue(false);
    }
  }

  @Test
  public void testFindAttractionsBetweenInvalidCategoryNames() {
    Database.setYelpDatabaseConnection();
    try {
      List<AttractionNode> attractions = BoundingBox.findAttractionsBetween(
              new double[]{40, -105}, new double[]{39, -106},
              Arrays.asList("Parks", "Restaurants"),
              5, 5, new double[]{2, 2, 2, 2});
      assertTrue(attractions.isEmpty());
    } catch (Exception e) {
      assertTrue(false);
    }
  }

  @Test
  public void testFindAttractionsBetweenValidCategoryNoStops() {
    Database.setYelpDatabaseConnection();
    try {
      List<AttractionNode> attractions = BoundingBox.findAttractionsBetween(
              new double[]{20, -100}, new double[]{20, -100},
              Arrays.asList("Park"),
              5, 5, new double[]{2, 2, 2, 2});
      assertTrue(attractions.isEmpty());
    } catch (Exception e) {
      assertTrue(false);
    }
  }

  @Test
  public void testFindAttractionsBetweenOnlyCategoriesOfValidStops() {
    Database.setYelpDatabaseConnection();
    try {
      List<AttractionNode> attractions = BoundingBox.findAttractionsBetween(
              new double[]{40, -105}, new double[]{40, -105},
              Arrays.asList("Museum", "Park", "Shop"),
              5, 0, new double[]{2, 2, 2, 2});
      assertFalse(containsAttractionWithID(attractions, "6iYb2HFDywm3zjuRg0shjw"));
      assertTrue(containsAttractionWithID(attractions, "UYmatKG6_NMm0WOADzoDoQ"));
      assertTrue(containsAttractionWithID(attractions, "GlI_cFeXKtsI6qqUJobOpg"));
    } catch (Exception e) {
      assertTrue(false);
    }
  }

  @Test
  public void testFindAttractionsWithinBoundingBoxAllCategories() {
    Database.setYelpDatabaseConnection();
    try {
      List<AttractionNode> attractions = BoundingBox.findAttractionsWithinBoundingBox(
              new double[]{38, 42, -107, -105},
              Arrays.asList("Restaurant", "Museum", "Park", "Shop"),
              5, 5, new double[]{2, 2, 2, 2});
      assertTrue(containsAttractionWithID(attractions, "6iYb2HFDywm3zjuRg0shjw"));
      assertFalse(containsAttractionWithID(attractions, "D4JtQNTI4X3KcbzacDJsMw"));
    } catch (Exception e) {
      assertTrue(false);
    }
  }

  @Test
  public void testFindAttractionsWithinBoundingBoxEmptyCategoriesList() {
    Database.setYelpDatabaseConnection();
    try {
      List<AttractionNode> attractions = BoundingBox.findAttractionsWithinBoundingBox(
              new double[]{40, 42, -107, -105},
              Arrays.asList(),
              5, 5, new double[]{2, 2, 2, 2});
      assertTrue(attractions.isEmpty());
    } catch (Exception e) {
      assertTrue(false);
    }
  }

  @Test
  public void testFindAttractionsWithinBoundingBoxInvalidCategoryNames() {
    Database.setYelpDatabaseConnection();
    try {
      List<AttractionNode> attractions = BoundingBox.findAttractionsWithinBoundingBox(
              new double[]{40, 42, -107, -105},
              Arrays.asList("Parks", "Restaurants"),
              5, 5, new double[]{2, 2, 2, 2});
      assertTrue(attractions.isEmpty());
    } catch (Exception e) {
      assertTrue(false);
    }
  }

  @Test
  public void testFindAttractionsWithinBoundingBoxValidCategoryNoStops() {
    Database.setYelpDatabaseConnection();
    try {
      List<AttractionNode> attractions = BoundingBox.findAttractionsWithinBoundingBox(
              new double[]{18, 20, -98, -100},
              Arrays.asList("Park"),
              5, 5, new double[]{2, 2, 2, 2});
      assertTrue(attractions.isEmpty());
    } catch (Exception e) {
      assertTrue(false);
    }
  }

  @Test
  public void testFindAttractionsWithinBoundingBoxOnlyCategoriesOfValidStops() {
    Database.setYelpDatabaseConnection();
    try {
      List<AttractionNode> attractions = BoundingBox.findAttractionsWithinBoundingBox(
              new double[]{38, 42, -107, -103},
              Arrays.asList("Museum", "Park", "Shop"),
              5, 0, new double[]{2, 2, 2, 2});
      assertFalse(containsAttractionWithID(attractions, "6iYb2HFDywm3zjuRg0shjw"));
      assertTrue(containsAttractionWithID(attractions, "UYmatKG6_NMm0WOADzoDoQ"));
      assertTrue(containsAttractionWithID(attractions, "GlI_cFeXKtsI6qqUJobOpg"));
    } catch (Exception e) {
      assertTrue(false);
    }
  }

  @Test
  public void testFindPriceFieldNoPrice() {
    assertEquals(BoundingBox.findPriceField(""), null);
    assertEquals(BoundingBox.findPriceField("{u'BusinessAcceptsCreditCards': u'True'}"),
            null);
  }

  @Test
  public void testFindPriceFieldInvalidPricePosition() {
    assertEquals(BoundingBox.findPriceField(
            "{u'BusinessAcceptsCreditCards': u'True', u'RestaurantsPriceRange2': u2', " +
                    "u'BikeParking': u'True', u'RestaurantsDelivery': u'True'}"), null);
    assertEquals(BoundingBox.findPriceField(
            "{u'BusinessAcceptsCreditCards': u'True', u'RestaurantsPriceRange2': u' 2', " +
                    "u'BikeParking': u'True', u'RestaurantsDelivery': u'True'}"), null);
  }

  @Test
  public void testFindPriceFieldInvalidPriceValue() {
    assertEquals(BoundingBox.findPriceField(
            "{u'BusinessAcceptsCreditCards': u'True', u'RestaurantsPriceRange2': u'-2', " +
                    "u'BikeParking': u'True', u'RestaurantsDelivery': u'True'}"), null);
  }

  @Test
  public void testFindPriceFieldValidPrice() {
    Integer price = 2;
    assertEquals(BoundingBox.findPriceField(
            "{u'BusinessAcceptsCreditCards': u'True', u'RestaurantsPriceRange2': u'2', " +
                    "u'BikeParking': u'True', u'RestaurantsDelivery': u'True'}"), price);
  }

  @Test
  public void testFindPriceFieldExtractsOnlyInteger() {
    Integer price = 2;
    assertEquals(BoundingBox.findPriceField(
            "{u'BusinessAcceptsCreditCards': u'True', u'RestaurantsPriceRange2': u'2.3', " +
                    "u'BikeParking': u'True', u'RestaurantsDelivery': u'True'}"), price);
  }


  private static boolean containsAttractionWithID(List<AttractionNode> attractions, String Id) {
    for (AttractionNode attraction: attractions) {
      if (attraction.getId().equals(Id)) {
        return true;
      }
    }
    return false;
  }

}
