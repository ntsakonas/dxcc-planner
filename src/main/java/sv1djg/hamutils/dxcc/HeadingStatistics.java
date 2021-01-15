package sv1djg.hamutils.dxcc;

import lombok.AllArgsConstructor;
import lombok.Value;

// used to keep statistics per heading
@AllArgsConstructor
@Value
public class HeadingStatistics {

    public final int heading;
    public final int totalDxccEntitiesCovered;
    public final int totalClosestDxccEntitiesCovered;
    public final int totalRareDxccEntitiesCovered;

}
