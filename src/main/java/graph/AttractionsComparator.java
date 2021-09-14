package graph;

public class AttractionsComparator {

  private double PreferredPrice;
  private double[] PreferredStop;
  private int PreferredNumberofStops; //currently do not have this in value heuristic

    /**
     * Class Constructor.
     */
    public AttractionsComparator() { }

    //@Override
    public int compare(AttractionNode o1, AttractionNode o2) {
      if ((o1.generateValue(PreferredPrice, PreferredStop[o1.getType()]) + o1.getDistance()) >
          (o2.generateValue(PreferredPrice, PreferredStop[o2.getType()]) + o2.getDistance())) {
        return 1;
      }
      if ((o2.getCost() + o2.getDistance()) > (o1.getCost() + o1.getDistance())) {
        return -1;
      }
      return 0;
    }




}
