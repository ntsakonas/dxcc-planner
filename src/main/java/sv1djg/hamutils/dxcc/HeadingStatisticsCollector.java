package sv1djg.hamutils.dxcc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface HeadingStatisticsCollector {


    HeadingStatistics headingStats(int heading);

    void addContinent(String continent);

    List<HeadingStatistics> beamingStatistics();

    Set<String> continents();

    static HeadingStatisticsCollector getCollector() {

        List<HeadingStatistics> beamingStatistics = new ArrayList<>();
        Set<String> continentsCovered = new HashSet<>();

        return new HeadingStatisticsCollector() {

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


