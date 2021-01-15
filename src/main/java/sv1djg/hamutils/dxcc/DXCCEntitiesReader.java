////////////////////////////////////////////////////////////////////////
//
// This file is part of DXCCPlanner.
// 
// A utility to assist ham radio operators in planning their antennas
// especially when on limited space.
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

import com.ntsakonas.javalibs.modjava.types.tuple.Tuple2;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ntsakonas.utils.DistanceCalculator;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ntsakonas.javalibs.modjava.types.tuple.Tuple.tuple;

/**
 * Reads information from the countries file and extracts the DXCC entities' information.
 * <p>
 * The country file is downloaded from http://www.country-files.com/
 * The original file uses fixed column size and its format is described here: http://www.country-files.com/cty-dat-format/
 * Different logging programs use different variations and the easiest to use is the CSV version found here http://www.country-files.com/contest/aether/
 * <p>
 * The CSV version has the same filed order but it is not fixed width, just comma separated
 * The field order in the CSV file is as follows:
 * 1 	Primary DXCC Prefix
 * 2 	Country Name
 * 3    DXCC Entity number
 * 4    2-letter continent abbreviation
 * 5    CQ Zone
 * 6    ITU Zone
 * 7 	Latitude in degrees, + for North
 * 8    Longitude in degrees, + for West
 * 9 	Local time offset from GMT
 * 10   List of additional DXCC prefixes
 * <p>
 * NOTE:: the longitude is POSITIVE for WEST longitudes, which is the opposite of what is required for various calculations
 * so it is reverted when entities are created.
 */
public class DXCCEntitiesReader {

    private final static String COUNTRY_FILE = "countries.txt";
    private final static String MOST_WANTED_FILE = "mostwanted.txt";

    @AllArgsConstructor
    @ToString
    @EqualsAndHashCode
    private static class DXCCCountry {

        public final String prefix;
        public final String countryName;
        public final String continent;
        public final double latitude;
        public final double longitude;
    }


    public static Map<String, DXCCEntity> loadDXCCEntities(String myDXCCPrefix) {

        Map<String, DXCCCountry> dxccCountries = fileAsStream().andThen(loadDXCCCountries()).apply(COUNTRY_FILE);
        Map<String, Integer> mostWantedDxccCountries = fileAsStream().andThen(loadMostWantedEntities()).apply(MOST_WANTED_FILE);
        DXCCCountry myDXCCCountry = Optional.ofNullable(dxccCountries.get(myDXCCPrefix)).orElseThrow(() -> new IllegalArgumentException("Could not find your DXCC entity.make sure that " + myDXCCPrefix + " is correct"));

        // augment the countries with distance and bearing from 'myDXCCCountry' as well as with the most wanted rank
        return dxccCountries.entrySet().stream()
                .map(entry -> {
                    DXCCCountry dxccCountry = entry.getValue();
                    int dxccMostWantedRanking = mostWantedDxccCountries.getOrDefault(dxccCountry.prefix, 0).intValue();

                    return new DXCCEntity(dxccCountry.prefix, dxccCountry.countryName, dxccCountry.continent, dxccCountry.latitude, dxccCountry.longitude,
                            DistanceCalculator.distanceFrom(myDXCCCountry.latitude, myDXCCCountry.longitude, dxccCountry.latitude, dxccCountry.longitude),
                            DistanceCalculator.bearingTo(myDXCCCountry.latitude, myDXCCCountry.longitude, dxccCountry.latitude, dxccCountry.longitude), dxccMostWantedRanking
                    );
                }).collect(Collectors.toMap(dxccEntity -> dxccEntity.prefix, Function.identity(), (dxccEntity1, dxccEntity2) -> dxccEntity1));
    }

    private static Function<InputStream, Map<String, DXCCCountry>> loadDXCCCountries() {
        return inputStream -> {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            try (Stream<String> countryFile = br.lines()) {
                return countryFile
                        .filter(line -> !line.isEmpty())
                        .map(line -> line.replace(";", ""))
                        .map(line -> line.split(","))
                        .map(entityDetails ->
                                new DXCCCountry(entityDetails[0].toUpperCase(), entityDetails[1], entityDetails[3], Double.parseDouble(entityDetails[6]), Double.parseDouble(entityDetails[7]))
                        )
                        .collect(Collectors.toMap(entity -> entity.prefix, Function.identity()));//,(entity1, entity2) -> entity1));
            } catch (Throwable e) {
                e.printStackTrace();
                return Collections.EMPTY_MAP;
            }
        };
    }

    // the most wanted file used is taken http://www.clublog.org/mostwanted.php
    // and manually cleaned to remove entities' names
    //
    // example entries
    // 1,P5
    // 2,3Y/B
    // 3,VP8S
    //
    // field #1 -> ranking in most wanted
    // field #2 -> prefix
    //
    public static Function<InputStream, Map<String, Integer>> loadMostWantedEntities() {
        return inputStream -> {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            try (Stream<String> countryFile = br.lines()) {
                return countryFile
                        .filter(line -> !line.isEmpty())
                        .map(line -> line.split(","))
                        .map((Function<String[], Tuple2<String, Integer>>) entityDetails -> tuple(entityDetails[1].toUpperCase(), Integer.valueOf(entityDetails[0])))
                        .collect(Collectors.toMap(Tuple2::get_1, Tuple2::get_2, (entity1, entity2) -> entity1));
            } catch (Throwable e) {
                return Collections.EMPTY_MAP;
            }
        };
    }

    private static Function<String, InputStream> fileAsStream() {
        return filename -> (new DXCCEntitiesReader()).getClass().getClassLoader().getResourceAsStream(filename);
    }

}
