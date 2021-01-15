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


    public static void main(String[] args) {
        Optional<ProgramOptions> programOptions = ProgramOptionsProcessor.extractProgramOptions(args);
        programOptions.ifPresentOrElse(options -> new DXCCPlanner().runAnalysis(options), () -> ProgramOptionsProcessor.showUsage());
    }

    public void runAnalysis(ProgramOptions programOptions) {
        // execute the main  processing
        Map<String, DXCCEntity> dxccEntities = DXCCEntitiesReader.loadDXCCEntities(programOptions.getDxccCenter());
        DXCCEntity myDxccEntity = Optional.ofNullable(dxccEntities.get(programOptions.getDxccCenter()))
                .orElseThrow(() -> new IllegalArgumentException("Could not find your DXCC entity.make sure that " + programOptions.getDxccCenter() + " is correct"));
        List<DXCCEntity> dxccList = sortCountriesAroundMe(dxccEntities);

        //
        // modes
        // OPTIMAL_MODE  = find optimal setup given the maximum available headings I can have
        // EVALUATE_MODE = evaluate my setup , given my available headings
        // NEAREST_MODE  = print the closest DXCC entities to my location

        ResultPrinter.printCentralLocationInfo(programOptions, myDxccEntity);
        if (programOptions.getMode() == ProgramOptions.MODE.OPTIMAL)
            findMostActiveHeadings(programOptions, dxccList);
        else if (programOptions.getMode() == ProgramOptions.MODE.NEAREST)
            ResultPrinter.printClosestDXCCEntities(programOptions, dxccList, myDxccEntity);
        else if (programOptions.getMode() == ProgramOptions.MODE.EVALUATE)
            printDXCCEntitiesOnHeadings(programOptions,dxccList);

    }


    private void findMostActiveHeadings(ProgramOptions programOptions, List<DXCCEntity> dxccList) {
        //
        // cluster all headings to the DXCC entities up to the maximum number the user requested
        //
        List<Integer> listOfHeadings = dxccList
                .stream()
                .map(dxccEntity -> (int) dxccEntity.bearing).collect(Collectors.toList());
        List<Integer> headings = Clustering.findHeadingClusters(listOfHeadings, programOptions.getNumberOfBeamings());

        printHeadingsDetails(headings, programOptions, dxccList);
    }


    private void printDXCCEntitiesOnHeadings(ProgramOptions programOptions, List<DXCCEntity> dxccList) {
        printHeadingsDetails(programOptions.getAvailableBeamings(), programOptions, dxccList);
    }

    private void printHeadingsDetails(List<Integer> headings, ProgramOptions programOptions, List<DXCCEntity> dxccList) {

        // print an overview of the optimal headings discovered
        ResultPrinter.printOptimalHeadingsInfo(headings);

        // if the optimal headings are close to form dipoles, just print a hint for the user
        ResultPrinter.printHintsIdHeadingsFormDipoles(headings);
        System.out.println();

        ArrayList<AntennaBeamingStatistics> beamingStatistics = new ArrayList<AntennaBeamingStatistics>();
        ArrayList<String> continents = new ArrayList<String>();

        BeamingStatisticsCollector statisticsCollector = BeamingStatisticsCollector.getCollector();
        ResultPrinter.printDXCCDetailsForHeadings(headings, programOptions, dxccList,statisticsCollector);
        ResultPrinter.printHeadingStatistics(statisticsCollector);

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

}
