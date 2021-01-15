package sv1djg.hamutils.dxcc;

// used to keep statistics per heading (when the major headings have been found)

public class HeadingStatistics {

    public int heading;
    public int totalDxccEntitiesCovered;
    public int totalClosestDxccEntitiesCovered;
    public int totalRareDxccEntitiesCovered;

    public HeadingStatistics(int heading) {
        this.heading = heading;
    }

    public void incrTotalDXCCEntities() {
        totalDxccEntitiesCovered++;
    }

    public void incrClosestDXCCEntities() {
        totalClosestDxccEntitiesCovered++;
    }

    public void incrRareDXCCEntities() {
        totalRareDxccEntitiesCovered++;
    }

}
