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

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class DXCCEntity {

    public final String prefix;
    public final String countryName;
    public final String continent;
    public final double latitude;
    public final double longitude;
    public final double distance;
    public final double bearing;
    public final int rankingInMostWanted;
}
