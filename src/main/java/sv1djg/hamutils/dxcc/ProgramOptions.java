package sv1djg.hamutils.dxcc;

import lombok.Builder;
import lombok.Value;

import java.util.List;


@Value
@Builder
public class ProgramOptions {

    private final MODE mode;
    private final int maximumNumberOfCountriesToPrint;
    private final int numberOfBeamings;
    private final int maximumDistanceForClosest;
    private final int antennaBeamWidth;
    private final String dxccCenter;
    private final List<Integer> availableBeamings;
    private final int numberOfMostWanted;

    public enum MODE {
        OPTIMAL, NEAREST, EVALUATE
    }

}
