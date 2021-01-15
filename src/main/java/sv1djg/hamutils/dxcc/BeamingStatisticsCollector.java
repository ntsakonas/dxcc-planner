package sv1djg.hamutils.dxcc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface BeamingStatisticsCollector {


    HeadingStatistics headingStats(int heading);

    void addContinent(String continent);

    List<HeadingStatistics> beamingStatistics();

    Set<String> continents();

    static BeamingStatisticsCollector getCollector() {

        List<HeadingStatistics> beamingStatistics = new ArrayList<>();
        Set<String> continentsCovered = new HashSet<>();

        return new BeamingStatisticsCollector() {

            @Override
            public HeadingStatistics headingStats(int heading) {
                HeadingStatistics headingStatistics = new HeadingStatistics(heading);
                beamingStatistics.add(headingStatistics);
                return headingStatistics;
            }

            @Override
            public void addContinent(String continent) {
                continentsCovered.add(continent);
            }

            @Override
            public List<HeadingStatistics> beamingStatistics() {
                return beamingStatistics;
            }

            @Override
            public Set<String> continents() {
                return continentsCovered;
            }
        };
    }


}


