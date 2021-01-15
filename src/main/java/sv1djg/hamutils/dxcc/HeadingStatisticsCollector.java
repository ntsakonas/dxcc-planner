package sv1djg.hamutils.dxcc;

import java.util.*;
import java.util.stream.Collectors;

public class HeadingStatisticsCollector {

    private static class Statistics {

        int totalDxccEntitiesCovered;
        int totalClosestDxccEntitiesCovered;
        int totalRareDxccEntitiesCovered;

        void incTotalDXCCEntities() {
            totalDxccEntitiesCovered++;
        }

        void incClosestDXCCEntities() {
            totalClosestDxccEntitiesCovered++;
        }

        void incRareDXCCEntities() {
            totalRareDxccEntitiesCovered++;
        }
    }


    public HeadingStatisticsCollector(int maxDistance, int maxMostWantedRanking) {
        this.maxDistance = maxDistance;
        this.maxMostWantedRanking = maxMostWantedRanking;
    }

    private final Map<Integer, Statistics> beamingStatistics = new HashMap<>();
    private final Set<String> continentsCovered = new HashSet<>();
    private final int maxDistance;
    private final int maxMostWantedRanking;

    public void addEntity(int heading, DXCCEntity entity) {
        Statistics headingStatistics = beamingStatistics.computeIfAbsent(heading, integer -> new Statistics());
        headingStatistics.incTotalDXCCEntities();

        if (entity.distance <= maxDistance)
            headingStatistics.incClosestDXCCEntities();

        if (entity.rankingInMostWanted <= maxMostWantedRanking)
            headingStatistics.incRareDXCCEntities();

        continentsCovered.add(entity.continent);
    }

    public List<HeadingStatistics> beamingStatistics() {
        return beamingStatistics.entrySet().stream()
                .map(entry -> {
                    int heading = entry.getKey().intValue();
                    Statistics statistics = entry.getValue();
                    return new HeadingStatistics(heading, statistics.totalDxccEntitiesCovered, statistics.totalClosestDxccEntitiesCovered, statistics.totalRareDxccEntitiesCovered);
                }).collect(Collectors.toList());
    }

    public Set<String> continents() {
        return continentsCovered;
    }
}


