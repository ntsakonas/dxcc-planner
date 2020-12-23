////////////////////////////////////////////////////////////////////////
//
// A utility to assist ham radio operators in planning their antennas
// especially when on limited space.
//
// The utility process the current DXCC entities list and a fairly recent 
// Most-wanted DXCC list to present useful perspectives on the reachability
// of all the DXCC entities.
//
// Please see the usage and examples on how to use it
//
// Copyright 2016, Nick Tsakonas, SV1DJG
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
/////////////////////////////////////////////////////////////////////////


package sv1djg.hamutils.dxcc;

import java.util.*;
import java.util.stream.Collectors;


public class DXCCPlanner {

    private List<DXCCEntity> _dxccList;
    private DXCCEntity _myDxccEntity;

    private final ProgramOptions programOptions;

    public DXCCPlanner(ProgramOptions programOptions) {
        this.programOptions = programOptions;
        _dxccList = new ArrayList<>();
    }

    public static void main(String[] args) {
        Optional<ProgramOptions> programOptions = ProgramOptionsProcessor.extractProgramOptions(args);
        programOptions.ifPresentOrElse(options -> new DXCCPlanner(options).runAnalysis(), () -> ProgramOptionsProcessor.showUsage());
    }

    public void runAnalysis() {
        // execute the main  processing
        prepareDXCCEntities(programOptions);
        displayResults();
    }

    private void prepareDXCCEntities(ProgramOptions programOptions) {
        Map<String, DXCCEntity> dxccEntities = DXCCEntitiesReader.loadDXCCEntities(programOptions.getDxccCenter());
        _myDxccEntity = Optional.ofNullable(dxccEntities.get(programOptions.getDxccCenter())).orElseThrow(() -> new IllegalArgumentException("Could not find your DXCC entity.make sure that " + _myDxccEntity + " is correct"));
        _dxccList = sortCountriesAroundMe(dxccEntities);
    }


    private void displayResults() {
        //
        // modes
        // OPTIMAL_MODE  = find optimal setup given the maximum available headings I can have
        // EVALUATE_MODE = evaluate my setup , given my available headings
        // NEAREST_MODE  = print the closest DXCC entities to my location

        if (programOptions.getMode() == ProgramOptions.MODE.OPTIMAL)
            findMostActiveHeadings();
        else if (programOptions.getMode() == ProgramOptions.MODE.NEAREST)
            printClosestDXCCEntities();
        else if (programOptions.getMode() == ProgramOptions.MODE.EVALUATE)
            printDXCCEntitiesOnHeadings();
    }


    // used to keep statistics per heading (when the major headings have been found)
    private static class AntennaBeamingStatistics {

        public int heading;
        public int totalDxccEntitiesCovered;
        public int totalClosestDxccEntitiesCovered;
        public int totalRareDxccEntitiesCovered;
    }


    private void findMostActiveHeadings() {
        //
        // cluster all headings up to the maximum number the user requested
        //
        ArrayList<Integer> headings = findHeadingClusters();

        printHeadingsDetails(headings);
    }


    private void printDXCCEntitiesOnHeadings() {
        printHeadingsDetails(programOptions.getAvailableBeamings());
    }

    private void printHeadingsDetails(List<Integer> headings) {
        printCentralLocationInfo();

        // print an overview of the optimal headings discovered
        printOptimalHeadingsInfo(headings);

        // if the optimal headings are close to form dipoles, just print a hint for the user
        printHintsIdHeadingsFormDipoles(headings);

        System.out.println();


        ArrayList<AntennaBeamingStatistics> beamingStatistics = new ArrayList<DXCCPlanner.AntennaBeamingStatistics>();
        ArrayList<String> continents = new ArrayList<String>();


        printDXCCDetailsForHeadings(headings, beamingStatistics, continents);

        printHeadingStatistics(beamingStatistics, continents);

        System.out.println();

    }


    private List<DXCCEntity> sortCountriesAroundMe(Map<String, DXCCEntity> dxccEntities) {
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

    // prints overall statistics for all headings (how many DXCC entities can be reached, how many of them are considered
    // nearby (easy) and how many rares and continents can be reached
    private void printHeadingStatistics(ArrayList<AntennaBeamingStatistics> beamingStatistics, ArrayList<String> continents) {
        System.out.println(String.format("DXCC entities breakdown per heading"));

        System.out.println("|---------|------------|---------|------|");
        System.out.println("| Heading | Total DXCC | Closest | Rare |");
        System.out.println("|---------|------------|---------|------|");

        // sum up statistics as they are printed
        int totalCountriesCovered = 0;
        int totalClosestCountriesCovered = 0;
        int totalRareCountriesCovered = 0;

        for (AntennaBeamingStatistics headingStatistic : beamingStatistics) {
            System.out.println(String.format("|   %03d   |    %03d     |   %03d   | %03d  |", headingStatistic.heading,
                    headingStatistic.totalDxccEntitiesCovered,
                    headingStatistic.totalClosestDxccEntitiesCovered,
                    headingStatistic.totalRareDxccEntitiesCovered));

            totalCountriesCovered += headingStatistic.totalDxccEntitiesCovered;
            totalClosestCountriesCovered += headingStatistic.totalClosestDxccEntitiesCovered;
            totalRareCountriesCovered += headingStatistic.totalRareDxccEntitiesCovered;

        }

        System.out.println("|---------|------------|---------|------|");

        System.out.println(String.format("Total DXCC countries reachable   : %03d", totalCountriesCovered));
        System.out.println(String.format("Total DXCC in closest countries  : %03d", totalClosestCountriesCovered));
        System.out.println(String.format("Total rare DXCC reachable        : %03d", totalRareCountriesCovered));
        System.out.println(String.format("Total continent(s) reachable     : %3d %s", continents.size(), Arrays.toString(continents.toArray())));


    }

    // for each heading discovered, prints statistics (how many DXCC entities can be reached, how many of them are considered
    // nearby (easy) and how many rares and continents can be reached
    private void printDXCCDetailsForHeadings(List<Integer> initialCentroids, ArrayList<AntennaBeamingStatistics> beamingStatistics, ArrayList<String> continents) {

        for (Integer heading : initialCentroids) {

            AntennaBeamingStatistics headingStats = new AntennaBeamingStatistics();
            headingStats.heading = heading.intValue();

            System.out.println(String.format("DXCC entities around heading of %03d degress (within +/- %d degress from the main heading)", heading.intValue(), programOptions.getAntennaBeamWidth()  / 2));

            System.out.println("|---|---|------------------------------------------|--------|------|------------|---------|");
            System.out.println("| R | C |             DXCC Entity name             | Prefix | Cont |   Distance | Heading |");
            System.out.println("|---|---|------------------------------------------|--------|------|------------|---------|");


            int countryListSize = _dxccList.size();
            for (int countryIndex = 0; countryIndex < countryListSize; countryIndex++) {
                DXCCEntity entity = _dxccList.get(countryIndex);
                if (Math.abs(heading - entity.bearing) <= programOptions.getAntennaBeamWidth() / 2) {
                    System.out.println(String.format("| %c | %c | %-40.40s | %-6.6s |  %-2.2s  |   %8.2f |   %03d   |",
                            (entity.rankingInMostWanted <= programOptions.getNumberOfMostWanted()) ? '!' : ' ',
                            (entity.distance <= programOptions.getMaximumDistanceForClosest()) ? '*' : ' ',
                            entity.countryName,
                            entity.prefix,
                            entity.continent,
                            entity.distance,
                            (int) entity.bearing));

                    headingStats.totalDxccEntitiesCovered++;

                    if (entity.distance <= programOptions.getMaximumDistanceForClosest())
                        headingStats.totalClosestDxccEntitiesCovered++;

                    if (entity.rankingInMostWanted <= programOptions.getNumberOfMostWanted())
                        headingStats.totalRareDxccEntitiesCovered++;

                    if (!continents.contains(entity.continent))
                        continents.add(entity.continent);

                }
            }
            beamingStatistics.add(headingStats);
            System.out.println("|---|---|------------------------------------------|--------|------|------------|---------|");
            System.out.println();

        }

        System.out.println("NOTE: a '*' in the C column indicates a DXCC entity among the " + programOptions.getMaximumNumberOfCountriesToPrint() + " closest ones (up to " + programOptions.getMaximumDistanceForClosest() + " km)");
        System.out.println("NOTE: a '!' in the R column indicates a top-" + programOptions.getNumberOfMostWanted() + " rare DXCC entity.");


    }

    // if two headings are close to form a dipole it hints the user, if one antenna can cover both
    // the margin allowed is +/- 10 degress, if the headings are 170-190 degress apart
    private void printHintsIdHeadingsFormDipoles(List<Integer> initialCentroids) {
        // just a small hint:if two lobes form almost a dipole print it out ;-)
        if (initialCentroids.size() > 1) {
            for (int i = 0; i < (initialCentroids.size() - 1); i++) {
                for (int j = i + 1; j < initialCentroids.size(); j++) {
                    Integer heading1 = initialCentroids.get(i);
                    Integer heading2 = initialCentroids.get(j);

                    // if the headings are 170-190 degress apart, this could be handled with a dipole
                    // with some compromise
                    if (Math.abs(Math.abs(heading1 - heading2) - 180) < 10) {
                        System.out.println(String.format("HINT: beamings %03d and %03d almost form a dipole", heading1.intValue(), heading2.intValue()));
                    }
                }
            }
        }
    }

    //
    // print an overview of the optimal headings discovered
    //
    private void printOptimalHeadingsInfo(List<Integer> initialCentroids) {
        System.out.println("Main " + initialCentroids.size() + " headings to use for optimal DXCC entities coverage");
        int headingsIndex = 0;
        for (Integer heading : initialCentroids) {
            headingsIndex++;
            System.out.println(String.format("Heading %03d at %03d degrees", headingsIndex, heading.intValue()));

        }
    }

    // print an overview of the current parameters used for this run
    private void printCentralLocationInfo() {
        System.out.println("Current settings:");
        System.out.println("-----------------");
        System.out.println(String.format("Central DXCC entity       : %s (%s)", _myDxccEntity.prefix, _myDxccEntity.countryName));

        if (programOptions.getMode() == ProgramOptions.MODE.EVALUATE)
            System.out.println(String.format("Beamings to evaluate      : %s", Arrays.toString(programOptions.getAvailableBeamings().toArray())));
        else if (programOptions.getMode() == ProgramOptions.MODE.OPTIMAL)
            System.out.println(String.format("Maximum beamings to use   : %d", programOptions.getNumberOfBeamings()));

        if (programOptions.getMode() != ProgramOptions.MODE.NEAREST)
            System.out.println(String.format("Antenna beamwidth to use  : %d", programOptions.getAntennaBeamWidth()));

        System.out.println(String.format("Maximum distance to assume 'close DXCC': %d Km", programOptions.getMaximumDistanceForClosest()));

        if (programOptions.getMode() == ProgramOptions.MODE.NEAREST)
            System.out.println(String.format("Maximum DXCC entities to print         : %d", programOptions.getMaximumNumberOfCountriesToPrint()));
        System.out.println(String.format("Maximum rare DXCC entities to use      : %d", programOptions.getNumberOfMostWanted()));

        System.out.println();

    }


    // prints up to a maximum number of DXCC entities in increasing distance from the central location
    private void printClosestDXCCEntities() {

        printCentralLocationInfo();

        System.out.println();
        System.out.println(String.format("Displaying up to %d closest DXCC entities (up to %d km)", programOptions.getMaximumNumberOfCountriesToPrint(), programOptions.getMaximumDistanceForClosest()));

        System.out.println("|-----|---|------------------------------------------|--------|------|------------|---------|");
        System.out.println("|  #  | R |             DXCC Entity name             | Prefix | Cont |   Distance | Heading |");
        System.out.println("|-----|---|------------------------------------------|--------|------|------------|---------|");

        int totalRareCountriesCovered = 0;
        ArrayList<String> continents = new ArrayList<String>();

        for (int i = 1; i <= programOptions.getMaximumNumberOfCountriesToPrint(); i++) {
            DXCCEntity entity = _dxccList.get(i);
            if (entity.distance <= programOptions.getMaximumDistanceForClosest()) {
                System.out.println(String.format("| %03d | %c | %-40.40s | %-6.6s |  %-2.2s  |   %8.2f |   %03d   |", i, (entity.rankingInMostWanted <= programOptions.getNumberOfMostWanted()) ? '!' : ' ', entity.countryName, entity.prefix, entity.continent, entity.distance, (int) entity.bearing));

                if (entity.rankingInMostWanted <= programOptions.getNumberOfMostWanted())
                    totalRareCountriesCovered++;

                if (!continents.contains(entity.continent))
                    continents.add(entity.continent);

            }
        }
        System.out.println("|-----|---|------------------------------------------|--------|------|------------|---------|");
        System.out.println("NOTE: a '!' in the R column indicates a top-" + programOptions.getNumberOfMostWanted() + " rare DXCC entity.");

        System.out.println();

        System.out.println(String.format("Total rare DXCC reachable        : %03d", totalRareCountriesCovered));
        System.out.println(String.format("Total continent(s) reachable     : %3d %s", continents.size(), Arrays.toString(continents.toArray())));


    }


    // helper methods for finding clusters based on the beaming of each DXCC (relative to our central location)
    private ArrayList<Integer> findHeadingClusters() {
        ArrayList<Integer> headings = new ArrayList<Integer>();

        // put all the headings on a list
        for (DXCCEntity entity : _dxccList) {
            Integer heading = (int) entity.bearing;
            headings.add(heading);
        }

        //
        // intialise centroids
        //
        // create randomly _numberOfBeamings cluster as a start
        Random r = new Random();

        int desiredClusters = programOptions.getNumberOfBeamings();
        ArrayList<Integer> initialCentroids = new ArrayList<Integer>(desiredClusters);

        for (int i = 1; i <= desiredClusters; i++) {
            int sampleIndex = r.nextInt(headings.size());
            initialCentroids.add(headings.get(sampleIndex));
        }

        int MAX_ITERATIONS = 200;
        for (int iteration = 0; iteration < MAX_ITERATIONS; iteration++) {
            int[] clusterIndexes = findClosestCentroids(initialCentroids, headings);
            initialCentroids = computeCentroids(clusterIndexes, initialCentroids.size(), headings);

        }

        return initialCentroids;
    }

    private int[] findClosestCentroids(ArrayList<Integer> centroids, ArrayList<Integer> headings) {
        int numberOfClusters = centroids.size();
        int[] clusterIndexes = new int[headings.size()];

        for (int pointIndex = 0; pointIndex < headings.size(); pointIndex++) {
            Integer heading = headings.get(pointIndex);

            double[] minimumDistances = new double[numberOfClusters];
            for (int i = 0; i < numberOfClusters; i++) {
                double distance = Math.abs(heading - centroids.get(i));

                minimumDistances[i] = distance;
            }

            // find the index of the minimum distance
            double minDistance = minimumDistances[0];
            int clusterIndex = 0;

            for (int i = 0; i < numberOfClusters; i++) {
                if (minimumDistances[i] < minDistance) {
                    minDistance = minimumDistances[i];
                    clusterIndex = i;
                }
            }

            clusterIndexes[pointIndex] = clusterIndex;
        }

        return clusterIndexes;
    }


    private ArrayList<Integer> computeCentroids(int[] clusterIndexes, int desiredClusters, ArrayList<Integer> headings) {
        ArrayList<Integer> updatedCentroids = new ArrayList<Integer>(desiredClusters);

        for (int clusterNumber = 0; clusterNumber < desiredClusters; clusterNumber++) {
            int samplesOnThisCentroid = 0;
            double sum = 0.0;

            for (int i = 0; i < clusterIndexes.length; i++) {
                int clusterIndex = clusterIndexes[i];
                if (clusterIndex == clusterNumber) {
                    samplesOnThisCentroid++;

                    Integer point = headings.get(i);
                    sum += point.intValue();
                }
            }

            double newCentroidSum = (1.0 / (double) samplesOnThisCentroid) * sum;

            Integer newCentroid = (int) newCentroidSum;

            updatedCentroids.add(newCentroid);
        }

        return updatedCentroids;
    }


}
