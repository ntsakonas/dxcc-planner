/*
    DXCC Planner - A utility to assist ham radio operators for optimal antenna setup for best DXCC reachability
    Copyright (C) 2016-2021, Nick Tsakonas (SV1DJG)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

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
        return Collections.unmodifiableSet(continentsCovered);
    }
}


