package sv1djg.hamutils.dxcc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface BeamingStatisticsCollector {


    AntennaBeamingStatistics headingStats(int heading);

    void addContinent(String continent);

    List<AntennaBeamingStatistics> beamingStatistics();

    Set<String> continents();

    static BeamingStatisticsCollector getCollector() {

        List<AntennaBeamingStatistics> beamingStatistics = new ArrayList<>();
        Set<String> continentsCovered = new HashSet<>();

        return new BeamingStatisticsCollector() {
            @Override
            public AntennaBeamingStatistics headingStats(int heading) {
                AntennaBeamingStatistics antennaBeamingStatistics = new AntennaBeamingStatistics(heading);
                beamingStatistics.add(antennaBeamingStatistics);
                return antennaBeamingStatistics;
            }

            @Override
            public void addContinent(String continent) {
                continentsCovered.add(continent);
            }

            @Override
            public List<AntennaBeamingStatistics> beamingStatistics() {
                return beamingStatistics;
            }

            @Override
            public Set<String> continents() {
                return continentsCovered;
            }
        };
    }


}


