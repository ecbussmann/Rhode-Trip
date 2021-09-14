package edu.brown.cs.student.termProject;

import graph.Dijkstra;
import org.junit.Test;
import database.BoundingBox;
import database.Database;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DijkstraTests {

  @Test
  public void testDijkstraExecuteUnpopularNode(){
    Database.setYelpDatabaseConnection();
    List<String> categories = new ArrayList<>();
    categories.add("Park");
    categories.add("Restaurant");
    List<AttractionNode> attractions = BoundingBox.findAttractionsBetween(
        new double[]{41.83108984050821,-71.40029245994668},
        new double[]{41.819930960017274, -69}, categories, 4, 3,
        new double[]{30,98,45,13});
    System.out.println(attractions.size());
    System.out.println("^size");
    System.out.println(attractions.get(0).getName());
    Dijkstra dijkstra = new Dijkstra(Main.APICONNECTION);
    double[] preferredStop = {10, 10, 80, 70};
    dijkstra.setPreferences(preferredStop, 2, attractions);
    List<AttractionNode> path = dijkstra.execute(new double[]{41.83108984050821,-71.40029245994668},
        new double[]{41.819930960017274, -69}, 7);
    System.out.println(path.size() + "SIZE");
    for (int i = 0; i < path.size(); i++){
      assertTrue(path.get(i).getType() != 0);
    }
  }

  @Test
  public void testDijkstraExecuteEmptyPath(){
    Database.setYelpDatabaseConnection();
    List<String> categories = new ArrayList<>();
    List<AttractionNode> attractions = BoundingBox.findAttractionsBetween(
            new double[]{41.83108984050821,-71.40029245994668},
            new double[]{41.819930960017274, -69}, categories, 4, 3,
            new double[]{30,98,45,13});
    Dijkstra dijkstra = new Dijkstra(Main.APICONNECTION);
    double[] preferredStop = {10, 10, 80, 70};
    dijkstra.setPreferences(preferredStop, 2, attractions);
    List<AttractionNode> path = dijkstra.execute(new double[]{41.83108984050821,-71.40029245994668},
            new double[]{41.819930960017274, -69}, 1);
    assertTrue(path.isEmpty());
  }

  @Test
  public void testDijkstraExecuteCheckIncludedStops(){
    Database.setYelpDatabaseConnection();
    List<String> categories = new ArrayList<>();
    categories.add("Park");
    List<AttractionNode> attractions = BoundingBox.findAttractionsBetween(
            new double[]{41.83108984050821,-71.40029245994668},
            new double[]{41.819930960017274, -69}, categories, 4, 3,
            new double[]{30,98,45,13});
    System.out.println(attractions.size());
    Dijkstra dijkstra = new Dijkstra(Main.APICONNECTION);
    double[] preferredStop = {10, 10, 80, 70};
    dijkstra.setPreferences(preferredStop, 2, attractions);
    List<AttractionNode> path = dijkstra.execute(new double[]{41.83108984050821,-71.40029245994668},
            new double[]{41.819930960017274, -69}, 3);
    assertEquals(path.get(0).getId(), "qDs_ftCChiMjmEJ9b4bi5Q");
  }

  @Test
  public void testDijkstraExecuteCheckPathCorrectLength(){
    Database.setYelpDatabaseConnection();
    List<String> categories = new ArrayList<>();
    categories.add("Park");
    categories.add("Museum");
    List<AttractionNode> attractions = BoundingBox.findAttractionsBetween(
            new double[]{41.83108984050821,-71.40029245994668},
            new double[]{41.819930960017274, -69}, categories, 4, 3,
            new double[]{30,98,45,13});
    System.out.println(attractions.size());
    Dijkstra dijkstra = new Dijkstra(Main.APICONNECTION);
    double[] preferredStop = {10, 10, 80, 70};
    dijkstra.setPreferences(preferredStop, 2, attractions);
    List<AttractionNode> path = dijkstra.execute(new double[]{41.83108984050821,-71.40029245994668},
            new double[]{41.819930960017274, -69}, 5);
    assertEquals(path.size(), 4);
  }

  @Test
  public void testDijkstraExecuteStartEndBeyondDB(){
    Database.setYelpDatabaseConnection();
    List<String> categories = new ArrayList<>();
    categories.add("Park");
    categories.add("Museum");
    List<AttractionNode> attractions = BoundingBox.findAttractionsBetween(
            new double[]{0,0},
            new double[]{0, 0}, categories, 4, 3,
            new double[]{0,0,0,0});
    System.out.println(attractions.size());
    Dijkstra dijkstra = new Dijkstra(Main.APICONNECTION);
    double[] preferredStop = {0, 0, 0, 0};
    dijkstra.setPreferences(preferredStop, 2, attractions);
    List<AttractionNode> path = dijkstra.execute(new double[]{0,0},
            new double[]{1, 1}, 1);
    assertTrue(path.isEmpty());
  }

}
