package sv1djg.hamutils.dxcc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ResultPrinter {

    // prints overall statistics for all headings (how many DXCC entities can be reached, how many of them are considered
    // nearby (easy) and how many rares and continents can be reached
    public static void printHeadingStatistics(BeamingStatisticsCollector statisticsCollector) {
        System.out.println(String.format("DXCC entities breakdown per heading"));

        System.out.println("|---------|------------|---------|------|");
        System.out.println("| Heading | Total DXCC | Closest | Rare |");
        System.out.println("|---------|------------|---------|------|");

        // sum up statistics as they are printed
        int totalCountriesCovered = 0;
        int totalClosestCountriesCovered = 0;
        int totalRareCountriesCovered = 0;

        for (AntennaBeamingStatistics headingStatistic : statisticsCollector.beamingStatistics()) {
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
        System.out.println(String.format("Total continent(s) reachable     : %3d %s", statisticsCollector.continents().size(), Arrays.toString(statisticsCollector.continents().toArray())));


    }

    // for each heading discovered, prints statistics (how many DXCC entities can be reached, how many of them are considered
    // nearby (easy) and how many rares and continents can be reached
    public static void printDXCCDetailsForHeadings(List<Integer> headings, ProgramOptions programOptions, Map<Integer, List<DXCCEntity>> dxccEntitiesPerHeading, BeamingStatisticsCollector statisticsCollector) {

        for (Map.Entry<Integer, List<DXCCEntity>> entry : dxccEntitiesPerHeading.entrySet()) {

            int heading = entry.getKey().intValue();
            AntennaBeamingStatistics headingStats = statisticsCollector.headingStats(heading);

            System.out.println(String.format("DXCC entities around heading of %03d degress (within +/- %d degress from the main heading)", heading, programOptions.getAntennaBeamWidth() / 2));

            System.out.println("|---|---|------------------------------------------|--------|------|------------|---------|");
            System.out.println("| R | C |             DXCC Entity name             | Prefix | Cont |   Distance | Heading |");
            System.out.println("|---|---|------------------------------------------|--------|------|------------|---------|");

            List<DXCCEntity> dxccList = entry.getValue();
            for (DXCCEntity entity : dxccList) {
                System.out.println(String.format("| %c | %c | %-40.40s | %-6.6s |  %-2.2s  |   %8.2f |   %03d   |",
                        (entity.rankingInMostWanted <= programOptions.getNumberOfMostWanted()) ? '!' : ' ',
                        (entity.distance <= programOptions.getMaximumDistanceForClosest()) ? '*' : ' ',
                        entity.countryName,
                        entity.prefix,
                        entity.continent,
                        entity.distance,
                        (int) entity.bearing));

                headingStats.incrTotalDXCCEntities();

                if (entity.distance <= programOptions.getMaximumDistanceForClosest())
                    headingStats.incrClosestDXCCEntities();

                if (entity.rankingInMostWanted <= programOptions.getNumberOfMostWanted())
                    headingStats.incrRareDXCCEntities();

                statisticsCollector.addContinent(entity.continent);
            }

            System.out.println("|---|---|------------------------------------------|--------|------|------------|---------|");
            System.out.println();

        }

        System.out.println("NOTE: a '*' in the C column indicates a DXCC entity among the " + programOptions.getMaximumNumberOfCountriesToPrint() + " closest ones (up to " + programOptions.getMaximumDistanceForClosest() + " km)");
        System.out.println("NOTE: a '!' in the R column indicates a top-" + programOptions.getNumberOfMostWanted() + " rare DXCC entity.");
        System.out.println();
    }

    // if two headings are close to form a dipole then hint the user about it.
    // the main lobes of a dipole are 180 degrees apart, so if we allow a margin of +/- 10 degrees
    // the we hint the user if the headings are 170-190 degrees apart
    public static void printHintsIfHeadingsFormDipoles(List<Integer> headings) {
        // just a small hint:if two lobes form almost a dipole print it out ;-)
        if (headings.size() > 1) {
            for (int i = 0; i < (headings.size() - 1); i++) {
                for (int j = i + 1; j < headings.size(); j++) {
                    Integer heading1 = headings.get(i);
                    Integer heading2 = headings.get(j);

                    // if the headings are 170-190 degrees apart, this could be handled with a dipole
                    // with some compromise
                    if (Math.abs(Math.abs(heading1 - heading2) - 180) < 10) {
                        System.out.println(String.format("HINT: beamings %03d and %03d almost form a dipole", heading1.intValue(), heading2.intValue()));
                    }
                }
            }
        }
        System.out.println();
    }

    //
    // print an overview of the optimal headings discovered
    //
    public static void printOptimalHeadingsInfo(List<Integer> headings) {
        System.out.println("Main " + headings.size() + " headings to use for optimal DXCC entities coverage");
        int headingsIndex = 0;
        for (Integer heading : headings) {
            headingsIndex++;
            System.out.println(String.format(" - Heading %03d at %03d degrees", headingsIndex, heading.intValue()));
        }
    }

    // print an overview of the current parameters used for this run
    public static void printCentralLocationInfo(ProgramOptions programOptions, DXCCEntity myDxccEntity) {
        System.out.println("Current settings:");
        System.out.println("-----------------");
        System.out.println(String.format("Central DXCC entity       : %s (%s)", myDxccEntity.prefix, myDxccEntity.countryName));

        if (programOptions.getMode() == ProgramOptions.MODE.EVALUATE)
            System.out.println(String.format("Beamings to evaluate      : %s", Arrays.toString(programOptions.getAvailableBeamings().toArray())));
        else if (programOptions.getMode() == ProgramOptions.MODE.OPTIMAL)
            System.out.println(String.format("Maximum beamings to use   : %d", programOptions.getNumberOfBeamings()));

        if (programOptions.getMode() != ProgramOptions.MODE.NEAREST)
            System.out.println(String.format("Antenna beamwidth to use  : %d", programOptions.getAntennaBeamWidth()));

        System.out.println(String.format("Maximum distance to assume a DXCC entity as a 'close' one : %d Km", programOptions.getMaximumDistanceForClosest()));

        if (programOptions.getMode() == ProgramOptions.MODE.NEAREST)
            System.out.println(String.format("Maximum DXCC entities to print         : %d", programOptions.getMaximumNumberOfCountriesToPrint()));
        System.out.println(String.format("Maximum rare DXCC entities to consider     : %d", programOptions.getNumberOfMostWanted()));

        System.out.println();

    }


    // prints up to a maximum number of DXCC entities in increasing distance from the central location
    public static void printClosestDXCCEntities(ProgramOptions programOptions, List<DXCCEntity> dxccList) {

        System.out.println();
        System.out.println(String.format("Displaying up to %d closest DXCC entities (up to %d km)", programOptions.getMaximumNumberOfCountriesToPrint(), programOptions.getMaximumDistanceForClosest()));

        System.out.println("|-----|---|------------------------------------------|--------|------|------------|---------|");
        System.out.println("|  #  | R |             DXCC Entity name             | Prefix | Cont |   Distance | Heading |");
        System.out.println("|-----|---|------------------------------------------|--------|------|------------|---------|");

        int totalRareCountriesCovered = 0;
        List<String> continents = new ArrayList<>();
        int i = 0;
        for (DXCCEntity entity : dxccList) {
            System.out.println(String.format("| %03d | %c | %-40.40s | %-6.6s |  %-2.2s  |   %8.2f |   %03d   |",
                    ++i,
                    entity.rankingInMostWanted <= programOptions.getNumberOfMostWanted() ? '!' : ' ',
                    entity.countryName,
                    entity.prefix,
                    entity.continent,
                    entity.distance,
                    (int) entity.bearing));

            if (entity.rankingInMostWanted <= programOptions.getNumberOfMostWanted())
                totalRareCountriesCovered++;

            if (!continents.contains(entity.continent))
                continents.add(entity.continent);
        }
        System.out.println("|-----|---|------------------------------------------|--------|------|------------|---------|");
        System.out.println("NOTE: a '!' in the R column indicates a top-" + programOptions.getNumberOfMostWanted() + " rare DXCC entity.");

        System.out.println();

        System.out.println(String.format("Total rare DXCC reachable        : %03d", totalRareCountriesCovered));
        System.out.println(String.format("Total continent(s) reachable     : %3d %s", continents.size(), Arrays.toString(continents.toArray())));


    }

}
