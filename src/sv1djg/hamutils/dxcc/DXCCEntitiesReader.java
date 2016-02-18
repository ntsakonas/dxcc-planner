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

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

public class DXCCEntitiesReader
{

    ArrayList<DXCCEntity> _dxccList;
    private String _countriesFile;

    ArrayList<DXCCEntity> _mostWantedList;
    private String _mostWantedFile;

    public DXCCEntitiesReader(String countriesFile,String mostWantedFile)
    {

	_countriesFile = countriesFile;
	_dxccList = new ArrayList<DXCCEntity>();

	_mostWantedFile = mostWantedFile;
	_mostWantedList = new ArrayList<DXCCEntity>();

	readDXCCList();
    }

    private void readDXCCList()
    {
	readCountryFile();
	readMostWantedFile();
	updateDXCCEntitiesWithMostWanted();
	
	// only for debug reasons
	//sortCountriesOnRarity();
	//printList();
	
    }
    
    private void readCountryFile()
    {
	// the country file used is taken from http://www.country-files.com/cty/
	// the appropriate version is for Aether log
	//
	// example entries
	// SV,Greece,236,EU,20,28,39.78,-21.78,-2.0,J4 SV SW SX SY SZ =SV9DRU/1;
	// field #1 -> prefix
	// field #2 -> country name
	// field #3 -> entity code (not needed)
	// field #4 -> continent (not needed)
	// field #5 -> CQ Zone (not needed)
	// field #6 -> ITU Zone (not needed)
	// field #7 -> latitude (+ for north, - for south)
	// field #8 -> longitude(+ for west, - for east)
	// field #9 -> time diff (not needed)
	// field #10 -> additional prefixes for this country
	//
	// we need fields 1,2,4,7,8
	// field 8 normally should be negated

	try
	{
	    // Open the file (it is embedded in the jar)
	    InputStream fstream = getClass().getResourceAsStream(_countriesFile);

	    // Get the object of DataInputStream
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader countriesReader = new BufferedReader(new InputStreamReader(in), 65535);

	    NumberFormat format = NumberFormat.getInstance(Locale.US);

	    String countryEntry = countriesReader.readLine();

	    while (countryEntry != null)
	    {

		String[] countryDetails = StringUtils.split(countryEntry, ",");
		if (countryDetails != null && countryDetails.length >= 9)
		{

		    DXCCEntity dxccEntity = new DXCCEntity();

		    dxccEntity.prefix = countryDetails[0];
		    dxccEntity.countryName = countryDetails[1];
		    dxccEntity.continent = countryDetails[3];

		    dxccEntity.latitude = format.parse(countryDetails[6]).doubleValue();
		    dxccEntity.longitude = -format.parse(countryDetails[7]).doubleValue();

		    _dxccList.add(dxccEntity);

		}

		countryEntry = countriesReader.readLine();
	    }
	    
	    countriesReader.close();

	} catch (Throwable e)
	{
	   e.printStackTrace();
	}

    }
    
    
    private void readMostWantedFile()
    {
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
	
	// we read the file and create DXCC entities although not all fields are used
	
	try
	{
	    // Open the file (it is embedded in the jar)
	    InputStream fstream = getClass().getResourceAsStream(_mostWantedFile);
	  
	    // Get the object of DataInputStream
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader countriesReader = new BufferedReader(new InputStreamReader(in), 65535);

	    String countryEntry = countriesReader.readLine();

	    while (countryEntry != null)
	    {

		String[] countryDetails = StringUtils.split(countryEntry, ",");
		if (countryDetails != null && countryDetails.length == 2)
		{

		    DXCCEntity dxccEntity = new DXCCEntity();

		    dxccEntity.rankingInMostWanted = Integer.parseInt(countryDetails[0]);
		    dxccEntity.prefix = countryDetails[1];
		  
		    _mostWantedList.add(dxccEntity);

		}

		countryEntry = countriesReader.readLine();
	    }
	    
	    countriesReader.close();

	} catch (Throwable e)
	{
	   e.printStackTrace();
	}

    }
    
    private void updateDXCCEntitiesWithMostWanted()
    {

	int dxccListCount = _dxccList.size();
	int wantedDxccCount = _mostWantedList.size();
	int matchedWantedDxccCount = 0;
	
	//System.out.println("Matching DXCC list to most wanted...PASS 1");

	// iterate once and match the most common ones
	for (DXCCEntity dxccEntity: _dxccList)
	{
	    boolean foundMatch = false;
	    for (DXCCEntity wantedEntity: _mostWantedList)
	    {
	      if (wantedEntity.prefix.equalsIgnoreCase(dxccEntity.prefix))
	      {
		  dxccEntity.rankingInMostWanted = wantedEntity.rankingInMostWanted;
		  
		  matchedWantedDxccCount++;
		  foundMatch = true;
		  break;
	      }
	    }
	    
	  //  if (!foundMatch)
	  //	System.out.println("-- prefix "+dxccEntity.prefix+" not found in most wanted list");
	}
	
	// iterate a second time and resolve the very specific ones
	// usually we don't get a match on the first iteration because of small differences like
	// DXCC -> 5V , most wanted -> 5V7
	//
	// so in this iteration we check all the unresolved and check sub-prefixes
	
	//System.out.println("Matching DXCC list to most wanted...PASS 2");
	for (DXCCEntity dxccEntity: _dxccList)
	{
	    if (dxccEntity.rankingInMostWanted != 0)
		continue;
	    
	    boolean foundMatch = false;
	    for (DXCCEntity wantedEntity: _mostWantedList)
	    {
		if (StringUtils.startsWithIgnoreCase(StringUtils.remove(wantedEntity.prefix, '/')
							,StringUtils.remove(dxccEntity.prefix,'/')))
		{
		    dxccEntity.rankingInMostWanted = wantedEntity.rankingInMostWanted;

		    matchedWantedDxccCount++;
		    foundMatch = true;
		    break;
		}
	    }

	   // if (!foundMatch)
	   //	System.out.println("-- prefix "+dxccEntity.prefix+" not found in most wanted list");
	}
	
	//System.out.println("* Checked "+dxccListCount+" DXCC entities across "+wantedDxccCount+" most wanted and found "+matchedWantedDxccCount+" matches");
    }

    public ArrayList<DXCCEntity> getDXCCList()
    {
        return _dxccList;
    }
    
    //
    // for validation purposes only
    //
    private void sortCountriesOnRarity()
    {

	Comparator<DXCCEntity> dxccListSorter = new Comparator<DXCCEntity>()
		{

        	    @Override
        	    public int compare(DXCCEntity o1, DXCCEntity o2)
        	    {
        		if (o1.rankingInMostWanted < o2.rankingInMostWanted)
        		    return -1;
        		else if (o1.rankingInMostWanted > o2.rankingInMostWanted)
        		    return 1;
        		else
        		    return 0;
        	    }
		};

	Collections.sort(_dxccList, dxccListSorter);
    }

    private void printList()
    {
	System.out.println("DXCC list sorted on rarity");
	
	for (int i=0;i<_dxccList.size();i++)
	{
	    DXCCEntity entity = _dxccList.get(i);
	    System.out.println(String.format("%03d %-5.5s %-40.40s %-2.2s   %8.2f  %03d", entity.rankingInMostWanted,entity.prefix,entity.countryName,entity.continent,entity.distance,(int)entity.bearing));
	}
    }
}
