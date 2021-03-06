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

import com.ntsakonas.javalibs.modjava.types.tuple.Tuple2;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.ntsakonas.javalibs.modjava.types.tuple.Tuple.tuple;


public class DXCCPlanner {


    public static void main(String[] args) {
        Optional<ProgramOptions> programOptions = ProgramOptionsProcessor.extractProgramOptions(args);
        programOptions.ifPresent(options -> {
            ProgramInfo.printProgramDetails();
            new DXCCPlanner().runAnalysis(options);
        });
    }

    public void runAnalysis(ProgramOptions programOptions) {

        Map<String, DXCCEntity> dxccEntities = DXCCEntitiesReader.loadDXCCEntities(programOptions.getDxccCenter());
        DXCCEntity myDxccEntity = Optional.ofNullable(dxccEntities.get(programOptions.getDxccCenter()))
                .orElseThrow(() -> new IllegalArgumentException("Could not find your DXCC entity.make sure that " + programOptions.getDxccCenter() + " is correct"));
        List<DXCCEntity> dxccList = sortCountriesAroundMeByDistance(dxccEntities);

        // modes
        // OPTIMAL_MODE  = find optimal setup given the maximum available headings I can have
        // EVALUATE_MODE = evaluate my setup , given my available headings
        // NEAREST_MODE  = print the closest DXCC entities to my location

        ResultPrinter.printRunSettings(programOptions, myDxccEntity);
        if (programOptions.getMode() == ProgramOptions.MODE.OPTIMAL) {
            List<Integer> headings = findMostActiveHeadings(dxccList, programOptions.getNumberOfBeamings());
            printDXCCEntitiesOnHeadings(programOptions, dxccList, headings);
        } else if (programOptions.getMode() == ProgramOptions.MODE.EVALUATE) {
            List<Integer> headings = programOptions.getAvailableBeamings();
            printDXCCEntitiesOnHeadings(programOptions, dxccList, headings);
        } else if (programOptions.getMode() == ProgramOptions.MODE.NEAREST) {
            List<DXCCEntity> entities = findClosestDXCCEntities(dxccList, programOptions.getMaximumNumberOfCountriesToPrint(), programOptions.getMaximumDistanceForClosest());
            printClosestDXCCEntities(programOptions, entities);
        }

    }

    // cluster the DXCC entities according to their proximity to the maximum number of headings
    private List<Integer> findMostActiveHeadings(List<DXCCEntity> dxccList, int numberOfHeadings) {
        List<Integer> listOfHeadings = dxccList.stream()
                .map(dxccEntity -> (int) dxccEntity.bearing)
                .collect(Collectors.toList());
        return Clustering.findHeadingClusters(listOfHeadings, numberOfHeadings);
    }

    // get up to N entities that lie up to M km from my own location
    // the list of entities is sorted by distance ascending (from my own location)
    private List<DXCCEntity> findClosestDXCCEntities(List<DXCCEntity> dxccList, int maxNumberEntities, int maximumDistance) {
        return dxccList.stream()
                .filter(dxccEntity -> dxccEntity.distance < maximumDistance)
                .limit(maxNumberEntities)
                .collect(Collectors.toList());
    }

    // find all the entities that lie on a heading (or within the specified beamwidth (+/- half of the antennaBeamWidth))
    private Map<Integer, List<DXCCEntity>> findDXCCEntitiesOnHeadings(List<DXCCEntity> dxccList, List<Integer> headings, int antennaBeamWidth) {
        final int halfBeamwidth = antennaBeamWidth / 2;
        return headings.stream()
                .flatMap(heading -> dxccList.stream()
                        .filter(dxccEntity -> Math.abs(heading - dxccEntity.bearing) <= halfBeamwidth)
                        .map((Function<DXCCEntity, Tuple2<Integer, DXCCEntity>>) dxccEntity -> tuple(heading, dxccEntity))
                )
                .collect(Collectors.groupingBy(Tuple2::get_1, Collectors.mapping(Tuple2::get_2, Collectors.toList())));
    }

    private void printDXCCEntitiesOnHeadings(ProgramOptions programOptions, List<DXCCEntity> dxccList, List<Integer> headings) {
        Map<Integer, List<DXCCEntity>> dxccEntitiesOnHeadings = findDXCCEntitiesOnHeadings(dxccList, headings, programOptions.getAntennaBeamWidth());
        printHeadingsDetails(headings, programOptions, dxccEntitiesOnHeadings);
    }

    private void printHeadingsDetails(List<Integer> headings, ProgramOptions programOptions, Map<Integer, List<DXCCEntity>> dxccEntities) {
        // print an overview of the optimal headings discovered
        ResultPrinter.printOptimalHeadingsInfo(headings);

        // if the optimal headings are close to form dipoles, just print a hint for the user
        ResultPrinter.printHintsIfHeadingsFormDipoles(headings);

        HeadingStatisticsCollector statisticsCollector = new HeadingStatisticsCollector(programOptions.getMaximumDistanceForClosest(), programOptions.getNumberOfMostWanted());
        ResultPrinter.printDXCCDetailsForHeadings(dxccEntities,
                programOptions.getNumberOfMostWanted(),
                programOptions.getMaximumDistanceForClosest(),
                programOptions.getAntennaBeamWidth() / 2,
                programOptions.getMaximumNumberOfCountriesToPrint(),
                statisticsCollector);
        ResultPrinter.printHeadingStatistics(statisticsCollector);

        System.out.println();
    }

    private void printClosestDXCCEntities(ProgramOptions programOptions, List<DXCCEntity> entities) {
        HeadingStatisticsCollector statisticsCollector = new HeadingStatisticsCollector(programOptions.getMaximumDistanceForClosest(), programOptions.getNumberOfMostWanted());

        ResultPrinter.printClosestDXCCEntities(entities,
                programOptions.getNumberOfMostWanted(),
                programOptions.getMaximumDistanceForClosest(),
                programOptions.getMaximumNumberOfCountriesToPrint(),
                statisticsCollector);

        ResultPrinter.printStatisticsSummary(statisticsCollector);
        System.out.println();
    }

    private List<DXCCEntity> sortCountriesAroundMeByDistance(Map<String, DXCCEntity> dxccEntities) {
        Comparator<DXCCEntity> byDxccDistanceAscending = (o1, o2) -> {
            if (o1.distance < o2.distance)
                return -1;
            else if (o1.distance > o2.distance)
                return 1;
            else
                return 0;
        };
        return dxccEntities.values().stream().sorted(byDxccDistanceAscending).collect(Collectors.toList());
    }

}
