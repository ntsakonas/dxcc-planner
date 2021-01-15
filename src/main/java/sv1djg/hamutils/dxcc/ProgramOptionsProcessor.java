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

import org.apache.commons.cli.*;
import org.apache.commons.lang.StringUtils;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProgramOptionsProcessor {

    // during processing we take into account only the most rare countries
    private final static int MOST_WANTED_RANKING = 50;
    // this is the distance we assume that is easily reachable (well within 2-hops)
    private static final int DEFAULT_DIST_FOR_CLOSEST = 6500;
    // a limit for display (where applicable)
    private static final int DEFAULT_MAX_CLOSEST_TO_PRINT = 150;
    private static final int DEFAULT_NUMBER_OF_HEADINGS = 2;
    // default antenna to use is one dipole (2 headings, 60 degrees beamwidth)
    private static final int DEFAULT_DIPOLE_BEAMWIDTH = 60;

    public static Optional<ProgramOptions> extractProgramOptions(String[] arguments) {
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

        try {
            CommandLine cmd = parser.parse(options, arguments);
            ProgramOptions.ProgramOptionsBuilder builder = ProgramOptions.builder();
            // set defaults
            builder.maximumDistanceForClosest(DEFAULT_DIST_FOR_CLOSEST);
            builder.maximumNumberOfCountriesToPrint(DEFAULT_MAX_CLOSEST_TO_PRINT);
            builder.antennaBeamWidth(DEFAULT_DIPOLE_BEAMWIDTH);
            builder.numberOfBeamings(DEFAULT_NUMBER_OF_HEADINGS);
            builder.numberOfMostWanted(MOST_WANTED_RANKING);

            if (!cmd.hasOption("nearest") && !cmd.hasOption("optimal") && !cmd.hasOption("evaluate")) {
                throw new ParseException("at least one of nearest/optimal/evaluate should be specified");
            }
            if (cmd.hasOption("nearest") && cmd.hasOption("optimal") && cmd.hasOption("evaluate")) {
                throw new ParseException("only one of nearest/optimal/evaluate should be specified");
            }
            if (cmd.hasOption("nearest")) {
                builder.mode(ProgramOptions.MODE.NEAREST);
            }
            if (cmd.hasOption("optimal")) {
                builder.mode(ProgramOptions.MODE.OPTIMAL);
            }
            if (cmd.hasOption("evaluate")) {
                builder.mode(ProgramOptions.MODE.EVALUATE);
            }
            if (cmd.hasOption("limit")) {
                builder.maximumNumberOfCountriesToPrint(Integer.parseInt(cmd.getOptionValue("limit")));
            }
            if (cmd.hasOption("distance")) {
                builder.maximumDistanceForClosest(Integer.parseInt(cmd.getOptionValue("distance")));
            }
            if (cmd.hasOption("headings")) {
                if (cmd.hasOption("evaluate")) {
                    builder.availableBeamings(
                            Stream.of(StringUtils.split(cmd.getOptionValue("headings"), ","))
                                    .map(Integer::valueOf)
                                    .collect(Collectors.toList()));

                } else {
                    if (StringUtils.contains(cmd.getOptionValue("headings"), ',')) {
                        throw new IllegalArgumentException("a list of headings is supported only in evaluate mode (use -evaluate option)");
                    }

                    builder.numberOfBeamings(Integer.parseInt(cmd.getOptionValue("headings")));
                }
            }
            if (cmd.hasOption("beamwidth")) {
                builder.antennaBeamWidth(Integer.parseInt(cmd.getOptionValue("beamwidth")));
            }
            if (cmd.hasOption("center")) {
                builder.dxccCenter(cmd.getOptionValue("center").toUpperCase());
            }

            return validate(builder.build());

        } catch (Throwable e) {

            System.out.println("ERROR: " + e.getMessage());
            System.out.println();

            return Optional.empty();
        }
    }

    private static Optional<ProgramOptions> validate(ProgramOptions programOptions) {
        if (programOptions.getDxccCenter() == null || programOptions.getDxccCenter().isEmpty())
            throw new IllegalArgumentException("the prefix to use for central location cannot be empty or null");

        if (programOptions.getMode() != ProgramOptions.MODE.EVALUATE && (programOptions.getNumberOfBeamings() < 0 || programOptions.getNumberOfBeamings() > 36))
            throw new IllegalArgumentException("the number of beamings should be realistic (larger than 0 but not larger than 36)");

        if (programOptions.getMode() == ProgramOptions.MODE.EVALUATE && (programOptions.getAvailableBeamings() == null || programOptions.getAvailableBeamings().size() == 0 || programOptions.getAvailableBeamings().size() > 36))
            throw new IllegalArgumentException("the number of beamings should be realistic (larger than 0 but not larger than 36)");

        if (programOptions.getAntennaBeamWidth() < 5 || programOptions.getAntennaBeamWidth() > 180)
            throw new IllegalArgumentException("the beamwidth of the antenna should be realistic (larger than 5 degrees but not larger than 180)");

        if (programOptions.getMaximumNumberOfCountriesToPrint() < 1)
            throw new IllegalArgumentException("the max number of countries to print cannot be zero or negative");

        if (programOptions.getMaximumDistanceForClosest() < 1 || programOptions.getMaximumDistanceForClosest() > 15000)
            throw new IllegalArgumentException("the distance to assume nearby countries should be realistic (larger than 1 Km but not larger than 15000)");

        return Optional.of(programOptions);
    }


    public static void showUsage() {
        System.out.println("DXCC planner v2.0 (by SV1DJG, Copyright (C) Feb 2016-2021)");
        System.out.println("This program comes with ABSOLUTELY NO WARRANTY;");
        System.out.println("This is free software, and you are welcome to redistribute it under certain conditions;");
        System.out.println("----------------------------------------------------------");

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
        System.out.println("       -nearest 		     display the list of nearest DXCC entities");
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

}
