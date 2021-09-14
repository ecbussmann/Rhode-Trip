package edu.brown.cs.student.termProject;

import database.BoundingBox;

import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

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

}
