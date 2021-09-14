package edu.brown.cs.student.termProject;

import graph.AttractionNode;
import org.junit.Test;
import database.BoundingBox;
import database.Database;

import java.util.ArrayList;
import java.util.List;

public class DijkstraTests {

  @Test
  public void testThis(){
    Database.setYelpDatabaseConnection();
    List<String> categories = new ArrayList<>();
    categories.add("Park");
    categories.add("Restaurant");
    List<AttractionNode> attractions = BoundingBox.findAttractionsBetween(
        new double[]{41.83108984050821,-71.40029245994668},
        new double[]{41.819930960017274, -71.41042819577497}, categories);
    System.out.println(attractions.size());
    System.out.println("^size");
    System.out.println(attractions.get(0).getName());
  }

}
