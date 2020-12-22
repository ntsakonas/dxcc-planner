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

import ntsakonas.utils.DistanceCalculator;
import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;

import java.util.*;


public class DXCCPlanner {

    // the full dxcc list and most wanted dxcc entities are saved into these files
    private final static String COUNTRY_FILE = "countries.txt";
    private final static String MOST_WANTED_FILE = "mostwanted.txt";
    // during processing we take into account only the most rare countries
    private final static int MOST_WANTED_RANKING = 50;
    // this is the distance we assume that is easily reachable (well within 2-hops)
    private static final int DEFAULT_DIST_FOR_CLOSEST = 6500;
    // a limit for display (where applicable)
    private static final int DEFAULT_MAX_CLOSEST_TO_PRINT = 150;
    // default antenna to use is one dipole (2 headings, 60 degrees beamwidth)
    private static final int DEFAULT_DIPLE_BEAMWIDTH = 60;
    private static final int DEFAULT_NUMBER_OF_HEADINGS = 2;
    private static final int OPTIMAL_MODE = 1;
    private static final int NEAREST_MODE = 2;
    private static final int EVALUATE_MODE = 3;
    private String _dxccCenter;
    private int _numberOfBeamings;
    private int _maximumNumberOfCountriesToPrint;
    private int _maximumDistanceForClosest;
    private ArrayList<DXCCEntity> _dxccList;
    private DXCCEntity _myDxccEntity;
    private String _currentDirectory;
    private int _antennaBeamWidth;
    private int _mode;
    private ArrayList<Integer> _availableBeamings;

    public DXCCPlanner(String currentDirectory) {
        _currentDirectory = currentDirectory;
        _dxccList = new ArrayList<DXCCEntity>();
        _maximumDistanceForClosest = DEFAULT_DIST_FOR_CLOSEST;
        _maximumNumberOfCountriesToPrint = DEFAULT_MAX_CLOSEST_TO_PRINT;
        _antennaBeamWidth = DEFAULT_DIPLE_BEAMWIDTH;
        _numberOfBeamings = DEFAULT_NUMBER_OF_HEADINGS;
    }

    public static void main(String[] args) {

        DXCCPlanner planner = initialisePlanner(args);
        if (planner == null)
            return;

        // execute the main  processing
        planner.prepareDXCCEntities();

        planner.displayResults();
    }

    private void displayResults() {
        //
        // modes
        // OPTIMAL_MODE  = find optimal setup given the maximum available headings I can have
        // EVALUATE_MODE = evaluate my setup , given my available headings
        // NEAREST_MODE  = print the closest DXCC entities to my location

        if (_mode == OPTIMAL_MODE)
            findMostActiveHeadings();
        else if (_mode == NEAREST_MODE)
            printClosestDXCCEntities();
        else if (_mode == EVALUATE_MODE)
            printDXCCEntitiesOnHeadings();
    }

    private static DXCCPlanner initialisePlanner(String[] args) {

        // create planner with default parameters
        String currentDirectory = System.getProperty("user.dir");
        DXCCPlanner planner = new DXCCPlanner(currentDirectory);

        // add all supported options
        Options options = new Options();

        options.addOption("limit", true, "");
        options.addOption("distance", true, "");
        options.addOption("headings", true, "");
        options.addOption("beamwidth", true, "");
        options.addOption("center", true, "");
        options.addOption("nearest", false, "");
        options.addOption("optimal", false, "");
        options.addOption("evaluate", false, "");

        CommandLineParser parser = new DefaultParser();

        boolean shouldShowUsage = false;
        try {
            CommandLine cmd = parser.parse(options, args);


            if (!cmd.hasOption("nearest") && !cmd.hasOption("optimal") && !cmd.hasOption("evaluate")) {
                throw new ParseException("at least one of nearest/optimal/evaluate should be specified");
            }
            if (cmd.hasOption("nearest") && cmd.hasOption("optimal") && cmd.hasOption("evaluate")) {
                throw new ParseException("only one of nearest/optimal/evaluate should be specified");
            }
            if (cmd.hasOption("nearest")) {
                planner.setNearestMode();
            }
            if (cmd.hasOption("optimal")) {
                planner.setOptimalMode();
            }
            if (cmd.hasOption("evaluate")) {
                planner.setEvaluateMode();
            }

            if (cmd.hasOption("limit")) {
                planner.setMaximumNumberOfDXCCEntitiesToPrint(Integer.parseInt(cmd.getOptionValue("limit")));
            }
            if (cmd.hasOption("distance")) {
                planner.setClosestCountrieMaxDistance(Integer.parseInt(cmd.getOptionValue("distance")));
            }
            if (cmd.hasOption("headings")) {
                if (planner.isInEvaluateMode()) {
                    String[] headings = StringUtils.split(cmd.getOptionValue("headings"), ",");
                    ArrayList<Integer> headingsToEvaluate = new ArrayList<Integer>();

                    for (String h : headings)
                        headingsToEvaluate.add(Integer.parseInt(h));

                    planner.setBeamings(headingsToEvaluate);
                } else {
                    if (StringUtils.contains(cmd.getOptionValue("headings"), ',')) {
                        throw new IllegalArgumentException("a list of headings is supported only in evaluate mode (use -evaluate option)");
                    }

                    planner.setNumberOfBeamings(Integer.parseInt(cmd.getOptionValue("headings")));
                }
            }
            if (cmd.hasOption("beamwidth")) {
                planner.setAntennaBeamwidth(Integer.parseInt(cmd.getOptionValue("beamwidth")));
            }
            if (cmd.hasOption("center")) {
                planner.setDXCCCenter(cmd.getOptionValue("center"));
            }


        } catch (Throwable e) {

            System.out.println("ERROR: " + e.getMessage());
            System.out.println();

            shouldShowUsage = true;
        }


        if (shouldShowUsage) {
            showUsage();
            return null;
        }


        return planner;
    }

    private static void showUsage() {
        //System.out.println("=========================================================");
        System.out.println("DXCC planner v1.0 (by SV1DJG,Feb 2016)                   ");
        System.out.println("--------------------------------------");

        System.out.println("This program may assist in planning your antennas for making the best usage of your (limited) space and still  ");
        System.out.println("maximise your opportunities to work as many DXCC entities as possible.You can use it to calculate all the nearest  ");
        System.out.println("DXCC entities (the easy ones) that are reachable with the minimal setup and can get you started.");
        System.out.println("The program can also help you out to find the optimal headings that will give you coverage of as many DXCC entities ");
        System.out.println("as possible.");
        System.out.println();
        System.out.println("syntax:");
        System.out.println();
        System.out.println("       dxccplanner [-limit LIMIT] [-distance DISTANCE] [-headings HEADINGS] [-beamwidth BEAMWIDTH] -center CENTER -nearest | -optimal | -evaluate");
        System.out.println();
        System.out.println("options:");
        System.out.println();
        System.out.println("       -limit LIMIT          set upper limit for nearest countries list print (default is 150)");
        System.out.println("       -distance DISTANCE    set maximum distance considered as 'near-by' (default is 6500 Km)");
        System.out.println("       -headings HEADINGS]   set number of headings to use for optimal calculation (default is 2)");
        System.out.println("                             set a list of headings to use for evaluate calculation ");
        System.out.println("       -beamwidth BEAMWIDTH] set the antenna beamwidth to use for optimal calculation");
        System.out.println("       -center CENTER        set the DXCC entity to use as center (official prefix required, no defaults)");
        System.out.println("                             For USA you have to use either \"K-East\" or \"K-Mid\" or \"K-West\" to specify");
        System.out.println("                             the appropriate area.Do not use just K");
        System.out.println();
        System.out.println("commands:");
        System.out.println();
        System.out.println("       -nearest 		 display the list of nearest DXCC entities");
        System.out.println("       -optimal 	         calculate and display the optimal headings");
        System.out.println("       -evaluate 	         evaluate DXCC coverage for specific headings");
        System.out.println();
        System.out.println("examples:");
        System.out.println();
        System.out.println("dxccplanner -nearest -limit 150 -distance 5000 -center G");
        System.out.println("    displays a list of up to 150 nearest DXCC entities with");
        System.out.println("    within 5000 Km centered a England (prefix G).");
        System.out.println();
        System.out.println("dxccplanner -optimal -headings 4 -beamwidth 40 -center G");
        System.out.println("    displays the 4 optimal headings for an antenna with ");
        System.out.println("    beamwidth of 40 degrees centered at England (prefix G).");
        System.out.println();
        System.out.println("dxccplanner -evaluate -headings 55,165,220 -beamwidth 40 -center G");
        System.out.println("    displays the possible DXCC coverage using 3 headings at 44,165, and 220 degrees using an antenna with ");
        System.out.println("    beamwidth of 40 degrees centered at England (prefix G).");
    }

    // used to keep statistics per heading (when the major headings have been found)
    private static class AntennaBeamingStatistics {

        public int heading;
        public int totalDxccEntitiesCovered;
        public int totalClosestDxccEntitiesCovered;
        public int totalRareDxccEntitiesCovered;
    }

    // used to set the prefix of the DXCC entity to use as center
    // for all calculations
    public void setDXCCCenter(String myDXCCCountry) {
        if (myDXCCCountry == null || myDXCCCountry.isEmpty())
            throw new IllegalArgumentException("the prefix to use for central location cannot be empty or null");

        _dxccCenter = myDXCCCountry;
    }

    // used to set how many different and independent beamings the user is able to setup
    //     
    public void setNumberOfBeamings(int availableBeamings) {
        if (availableBeamings < 0 || availableBeamings > 36)
            throw new IllegalArgumentException("the number of beamings should be realistic (larger than 0 but not larger than 36)");

        _numberOfBeamings = availableBeamings;
    }

    // used to set the beamings to use for evaluate mode
    //     
    public void setBeamings(ArrayList<Integer> availableBeamings) {
        if (availableBeamings == null || availableBeamings.size() == 0 || availableBeamings.size() > 36)
            throw new IllegalArgumentException("the number of beamings should be realistic (larger than 0 but not larger than 36)");

        _availableBeamings = availableBeamings;
    }

    // setting the beamwidth of the antenna to be used (this is applied to ALL headings)
    public void setAntennaBeamwidth(int beamWidth) {
        if (beamWidth < 5 || beamWidth > 180)
            throw new IllegalArgumentException("the beamwidth of the antenna should be realistic (larger than 5 degrees but not larger than 180)");

        _antennaBeamWidth = beamWidth;
    }

    // used to limit the number of entities when printing
    public void setMaximumNumberOfDXCCEntitiesToPrint(int numberOfCountries) {
        if (numberOfCountries < 1)
            throw new IllegalArgumentException("the max number of countries to print cannot be zero or negative");

        _maximumNumberOfCountriesToPrint = numberOfCountries;

    }

    // used to set the distance (in km) that is assumed 'nearby countries'
    public void setClosestCountrieMaxDistance(int distance) {
        if (distance < 1 || distance > 15000)
            throw new IllegalArgumentException("the distance to assume nearby countries should be realistic (larger than 1 Km but not larger than 15000)");

        _maximumDistanceForClosest = distance;

    }

    public void setOptimalMode() {
        _mode = OPTIMAL_MODE;
    }

    public void setNearestMode() {
        _mode = NEAREST_MODE;
    }

    public void setEvaluateMode() {
        _mode = EVALUATE_MODE;
    }

    public boolean isInEvaluateMode() {
        return _mode == EVALUATE_MODE;
    }

    private void prepareDXCCEntities() {
        if (_dxccCenter == null || _dxccCenter.isEmpty())
            throw new IllegalArgumentException("You must set your DXCC country before creating the DXCC List");

        if (_numberOfBeamings <= 0)
            throw new IllegalArgumentException("You must set a number of headings before creating the DXCC List");


        Map<String, DXCCEntity> dxccEntities = DXCCEntitiesReader.loadDXCCEntities(_dxccCenter);

        System.out.println(dxccEntities.get("3Y/B"));
        System.out.println(dxccEntities.get("FT5/W"));

        copyDXCCList(dxccEntities);

        _myDxccEntity = getMyDXCCEntity();

        if (_myDxccEntity == null)
            throw new IllegalArgumentException("Could not find your DXCC entity.make sure that \"" + _dxccCenter + "\" is correct");

        sortCountriesAroundMe();
    }

    private void findMostActiveHeadings() {
        //
        // cluster all headings up to the maximum number the user requested
        //
        ArrayList<Integer> headings = findHeadingClusters();

        printHeadingsDetails(headings);
    }


    private void printDXCCEntitiesOnHeadings() {
        printHeadingsDetails(_availableBeamings);
    }

    private void printHeadingsDetails(ArrayList<Integer> headings) {
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



    // THE NEW METHOD - which will be removed a sit is pointless
    private void copyDXCCList(Map<String, DXCCEntity> dxccEntities) {
        for (Map.Entry<String, DXCCEntity> entity : dxccEntities.entrySet())
            _dxccList.add(entity.getValue());
    }


    private void sortCountriesAroundMe() {
        // sort the DXCC list based on distance from the central location (my location)
        // sort ascending
        Comparator<DXCCEntity> dxccListSorter = new Comparator<DXCCEntity>() {

            @Override
            public int compare(DXCCEntity o1, DXCCEntity o2) {
                if (o1.distance < o2.distance)
                    return -1;
                else if (o1.distance > o2.distance)
                    return 1;
                else
                    return 0;
            }
        };

        Collections.sort(_dxccList, dxccListSorter);
    }


    // finds the details of the central location
    private DXCCEntity getMyDXCCEntity() {
        String usaArea = null;
        boolean centerIsUSA = (_dxccCenter.equalsIgnoreCase("K-Mid") || _dxccCenter.equalsIgnoreCase("K-East") || _dxccCenter.equalsIgnoreCase("K-West"));
        if (centerIsUSA) {
            usaArea = StringUtils.split(_dxccCenter, '-')[1];
        }

        for (DXCCEntity entity : _dxccList) {
            if (centerIsUSA) {
                if (entity.prefix.equalsIgnoreCase("K") && StringUtils.containsIgnoreCase(entity.countryName, usaArea))
                    return entity;

            } else {
                if (entity.prefix.equalsIgnoreCase(_dxccCenter))
                    return entity;

            }
        }

        return null;
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
    private void printDXCCDetailsForHeadings(ArrayList<Integer> initialCentroids, ArrayList<AntennaBeamingStatistics> beamingStatistics, ArrayList<String> continents) {

        for (Integer heading : initialCentroids) {

            AntennaBeamingStatistics headingStats = new AntennaBeamingStatistics();
            headingStats.heading = heading.intValue();

            System.out.println(String.format("DXCC entities around heading of %03d degress (within +/- %d degress from the main heading)", heading.intValue(), _antennaBeamWidth / 2));

            System.out.println("|---|---|------------------------------------------|--------|------|------------|---------|");
            System.out.println("| R | C |             DXCC Entity name             | Prefix | Cont |   Distance | Heading |");
            System.out.println("|---|---|------------------------------------------|--------|------|------------|---------|");


            int countryListSize = _dxccList.size();
            for (int countryIndex = 0; countryIndex < countryListSize; countryIndex++) {
                DXCCEntity entity = _dxccList.get(countryIndex);
                if (Math.abs(heading - entity.bearing) <= _antennaBeamWidth / 2) {
                    System.out.println(String.format("| %c | %c | %-40.40s | %-6.6s |  %-2.2s  |   %8.2f |   %03d   |",
                            (entity.rankingInMostWanted <= MOST_WANTED_RANKING) ? '!' : ' ',
                            (entity.distance <= _maximumDistanceForClosest) ? '*' : ' ',
                            entity.countryName,
                            entity.prefix,
                            entity.continent,
                            entity.distance,
                            (int) entity.bearing));

                    headingStats.totalDxccEntitiesCovered++;

                    if (entity.distance <= _maximumDistanceForClosest)
                        headingStats.totalClosestDxccEntitiesCovered++;

                    if (entity.rankingInMostWanted <= MOST_WANTED_RANKING)
                        headingStats.totalRareDxccEntitiesCovered++;

                    if (!continents.contains(entity.continent))
                        continents.add(entity.continent);

                }
            }
            beamingStatistics.add(headingStats);
            System.out.println("|---|---|------------------------------------------|--------|------|------------|---------|");
            System.out.println();

        }

        System.out.println("NOTE: a '*' in the C column indicates a DXCC entity among the " + _maximumNumberOfCountriesToPrint + " closest ones (up to " + _maximumDistanceForClosest + " km)");
        System.out.println("NOTE: a '!' in the R column indicates a top-" + MOST_WANTED_RANKING + " rare DXCC entity.");


    }

    // if two headings are close to form a dipole it hints the user, if one antenna can cover both
    // the margin allowed is +/- 10 degress, if the headings are 170-190 degress apart
    private void printHintsIdHeadingsFormDipoles(ArrayList<Integer> initialCentroids) {
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
    private void printOptimalHeadingsInfo(ArrayList<Integer> initialCentroids) {
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

        if (_mode == EVALUATE_MODE)
            System.out.println(String.format("Beamings to evaluate      : %s", Arrays.toString(_availableBeamings.toArray())));
        else if (_mode == OPTIMAL_MODE)
            System.out.println(String.format("Maximum beamings to use   : %d", _numberOfBeamings));

        if (_mode != NEAREST_MODE)
            System.out.println(String.format("Antenna beamwidth to use  : %d", _antennaBeamWidth));

        System.out.println(String.format("Maximum distance to assume 'close DXCC': %d Km", _maximumDistanceForClosest));

        if (_mode == NEAREST_MODE)
            System.out.println(String.format("Maximum DXCC entities to print         : %d", _maximumNumberOfCountriesToPrint));
        System.out.println(String.format("Maximum rare DXCC entities to use      : %d", MOST_WANTED_RANKING));

        System.out.println();

    }


    // prints up to a maximum number of DXCC entities in increasing distance from the central location
    private void printClosestDXCCEntities() {

        printCentralLocationInfo();

        System.out.println();
        System.out.println(String.format("Displaying up to %d closest DXCC entities (up to %d km)", _maximumNumberOfCountriesToPrint, _maximumDistanceForClosest));

        System.out.println("|-----|---|------------------------------------------|--------|------|------------|---------|");
        System.out.println("|  #  | R |             DXCC Entity name             | Prefix | Cont |   Distance | Heading |");
        System.out.println("|-----|---|------------------------------------------|--------|------|------------|---------|");

        int totalRareCountriesCovered = 0;
        ArrayList<String> continents = new ArrayList<String>();

        for (int i = 1; i <= _maximumNumberOfCountriesToPrint; i++) {
            DXCCEntity entity = _dxccList.get(i);
            if (entity.distance <= _maximumDistanceForClosest) {
                System.out.println(String.format("| %03d | %c | %-40.40s | %-6.6s |  %-2.2s  |   %8.2f |   %03d   |", i, (entity.rankingInMostWanted <= MOST_WANTED_RANKING) ? '!' : ' ', entity.countryName, entity.prefix, entity.continent, entity.distance, (int) entity.bearing));

                if (entity.rankingInMostWanted <= MOST_WANTED_RANKING)
                    totalRareCountriesCovered++;

                if (!continents.contains(entity.continent))
                    continents.add(entity.continent);

            }
        }
        System.out.println("|-----|---|------------------------------------------|--------|------|------------|---------|");
        System.out.println("NOTE: a '!' in the R column indicates a top-" + MOST_WANTED_RANKING + " rare DXCC entity.");

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

        int desiredClusters = _numberOfBeamings;
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
