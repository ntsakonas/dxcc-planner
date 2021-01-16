# dxcc-planner
A command utility to assist ham radio operators in planning their antennas for maximum DXCC reachability (especially when on limited space)

This program can assist in planning your antennas for making the best usage of your (limited) space and still  
maximise your opportunities to work as many DXCC entities as possible.You can use it to calculate all the nearest  
DXCC entities (the easy ones) that are reachable with the minimal setup and can get you started.
It can  also help you out to find the optimal headings that will give you coverage of as many DXCC entities 
as possible given how many directional antennas you can put up.

NOTE: The program does not provide propagation predictions. It takes into consideration only geographical position of the DXCC entities in order to calculate their relative position to you location and reachability. 

## How to run it
Download the pre-build `jar` from the releases, make sure you have Java 1.11 or later installed and run it on a terminal like this:
```
java -jar dxcc-planner-2.0.jar -help
```
to get detailed help how to use it or try to run it some of the examples shown below.

NOTE: when specifying the center DXCC prefix use the basic prefix only. As an exception, ONLY For USA you have to use either "K-East" or "K-Mid" or "K-West" to specify the appropriate area.

## Evaluating your setup
For example, if you are restriced to just a fixed orientation dipole you can use the program to evaluate which DXCC entities are in the beaming of your antenna by providing the headings of the main lobes and optionally the beamwidth of the dipole (which by default is assumed to be 60 degrees).Here is how you could achieve this:

if you can put a dipole beaming to azimuths 110/290 and you live in England (DXCC prefix G) you can use this command to evaluate your opportunities with this setup:

```
$ java -jar dxcc-planner-2.0.jar -evaluate -center G -headings 290,110
```
you will see a long detailed printout which summarises as follows:

```
DXCC entities breakdown per heading
|---------|------------|---------|------|
| Heading | Total DXCC | Closest | Rare |
|---------|------------|---------|------|
|   290   |    044     |   010   | 013  |
|   110   |    089     |   064   | 019  |
|---------|------------|---------|------|

Summary:
 Total DXCC countries reachable   : 133
 Total DXCC in closest countries  : 074
 Total rare DXCC reachable        : 032
 Total continent(s) reachable     :   6 [EU, AS, NA, OC, AF, SA]
```

if not specified with the command option *-distance* the distance limit to assume a DXCC entity as 'near-by' is 6500 Km, a range that can be reached with 2 hops.Keep in mind that very close entities (eg <800 Km) may reside in your skip zone and may require an NVIS antenna.

Since the program does not take into accoutn the band/frequency you intend to use,you may need to run it with various parameters to see the effect.In the above example if the antenna was not a dipole, but a 44ft doublet , which is usable from 30m up to 10m band, the reachability would be different on each band because the beamwidth of the antenna decreases as the frequency increases.We can run multiple times by specifying the appropriate beamwidth.

for example, on 20m a 44ft doublet has a horizontal beamwidth of around 70 degrees which decreases to 51 degrees and 31 degress on 15m and 10m respectively.To see the differences on DXCC capabilities of this antenna we can run the following commands

for 20m where beamwidth is 70 degrees
```
$ java -jar dxcc-planner-2.0.jar -evaluate -center G -headings 290,110 -beamwidth 70
```

for 15m where beamwidth is 51 degrees
```
$ java -jar dxcc-planner-2.0.jar -evaluate -center G -headings 290,110 -beamwidth 51
```

for 10m where beamwidth is 31 degrees
```
$ java -jar dxcc-planner-2.0.jar -evaluate -center G -headings 290,110 -beamwidth 31
```

for 20m (beamwidth 70 degrees) the output  will be something like
```
DXCC entities breakdown per heading
|---------|------------|---------|------|
| Heading | Total DXCC | Closest | Rare |
|---------|------------|---------|------|
|   290   |    061     |   010   | 014  |
|   110   |    106     |   071   | 023  |
|---------|------------|---------|------|

Summary:
 Total DXCC countries reachable   : 167
 Total DXCC in closest countries  : 081
 Total rare DXCC reachable        : 037
 Total continent(s) reachable     :   6 [EU, AS, NA, OC, AF, SA]
```

for 15m (beamwidth 51 degrees) the output  will be something like
```
DXCC entities breakdown per heading
|---------|------------|---------|------|
| Heading | Total DXCC | Closest | Rare |
|---------|------------|---------|------|
|   290   |    039     |   010   | 011  |
|   110   |    076     |   057   | 014  |
|---------|------------|---------|------|

Summary:
 Total DXCC countries reachable   : 115
 Total DXCC in closest countries  : 067
 Total rare DXCC reachable        : 025
 Total continent(s) reachable     :   6 [EU, AS, NA, OC, AF, SA]
```

for 10m (beamwidth 31 degrees) the output  will be something like
```
DXCC entities breakdown per heading
|---------|------------|---------|------|
| Heading | Total DXCC | Closest | Rare |
|---------|------------|---------|------|
|   290   |    018     |   008   | 006  |
|   110   |    051     |   043   | 010  |
|---------|------------|---------|------|

Summary:
 Total DXCC countries reachable   : 069
 Total DXCC in closest countries  : 051
 Total rare DXCC reachable        : 016
 Total continent(s) reachable     :   5 [EU, AS, NA, OC, AF]
```

### Finding an optimal setup
If you do not know what would be a good antenna setup for your location you can specify a number of headings (assuming for example that you can put up directional antennas to fixed directions) and the programme will find the beamings where the most DXCC entities are located in order to get the most coverage.

for example
```
$ java -jar dxcc-planner-2.0.jar -optimal -center G -headings 4
```

the summary will be something like
```
DXCC entities breakdown per heading
|---------|------------|---------|------|
| Heading | Total DXCC | Closest | Rare |
|---------|------------|---------|------|
|   053   |    056     |   017   | 009  |
|   139   |    088     |   056   | 021  |
|   251   |    065     |   004   | 009  |
|   335   |    032     |   007   | 009  |
|---------|------------|---------|------|

Summary:
 Total DXCC countries reachable   : 241
 Total DXCC in closest countries  : 084
 Total rare DXCC reachable        : 048
 Total continent(s) reachable     :   6 [EU, AS, NA, OC, AF, SA]
```

indicating that the prominent headings are 335,251,053 and 139. Note that these headings may not be easily realisable for your available property.

### Nearest DXCC entities
if you want to find the nearest DXCC enities no matter their direction (maybe because you are thinking about instaling a vertical antenna) you can run the following command

```
$ java -jar dxcc-planner-2.0.jar -nearest -center G
```
which will print by default the 150 closest DXCC countries (you can override the list size by providing the *-limit* option)

### Example outputs
Nearest DXCC entities
```
$ ./dxccplanner.jar -nearest -center G 
Current settings:
-----------------
Central DXCC entity       : G (England)
Maximum distance to assume a DXCC entity as a 'close' one : 6500 Km
Maximum DXCC entities to print         : 150
Maximum rare DXCC entities to consider : 50


Displaying up to 150 closest DXCC entities (up to 6500 km)
|-----|---|------------------------------------------|--------|------|------------|---------|
|  #  | R |             DXCC Entity name             | Prefix | Cont |   Distance | Heading |
|-----|---|------------------------------------------|--------|------|------------|---------|
| 001 |   | England                                  | G      |  EU  |       0.00 |   000   |
| 002 |   | Wales                                    | GW     |  EU  |     162.30 |   251   |
| 003 |   | Isle of Man                              | GD     |  EU  |     257.40 |   309   |
| 004 |   | Guernsey                                 | GU     |  EU  |     377.20 |   192   |
| 005 |   | Jersey                                   | GJ     |  EU  |     397.85 |   187   |
| 006 |   | Northern Ireland                         | GI     |  EU  |     405.86 |   304   |
| 007 |   | Ireland                                  | EI     |  EU  |     440.49 |   277   |
| 008 |   | Netherlands                              | PA     |  EU  |     472.47 |   093   |
| 009 |   | Scotland                                 | GM     |  EU  |     482.59 |   340   |
| 010 |   | Belgium                                  | ON     |  EU  |     492.09 |   115   |
| 011 |   | Luxembourg                               | LX     |  EU  |     602.54 |   117   |
| 012 |   | France                                   | F      |  EU  |     793.35 |   160   |
| 013 |   | Fed. Rep. of Germany                     | DL     |  EU  |     810.49 |   099   |
| 014 |   | Denmark                                  | OZ     |  EU  |     823.65 |   059   |
| 015 | ! | Shetland Islands                         | GM/S   |  EU  |     859.54 |   359   |
| 016 | ! | ITU HQ                                   | 4U1I   |  EU  |     912.11 |   140   |
| 017 |   | Switzerland                              | HB     |  EU  |     949.17 |   129   |
| 018 |   | Liechtenstein                            | HB0    |  EU  |    1006.71 |   124   |
| 019 |   | Faroe Islands                            | OY     |  EU  |    1083.63 |   344   |
| 020 |   | Norway                                   | LA     |  EU  |    1111.58 |   030   |
| 021 | ! | Andorra                                  | C3     |  EU  |    1156.17 |   167   |
| 022 |   | Monaco                                   | 3A     |  EU  |    1198.75 |   143   |
| 023 |   | Austria                                  | OE     |  EU  |    1214.29 |   113   |
| 024 |   | Czech Republic                           | OK     |  EU  |    1247.41 |   097   |
| 025 |   | Sweden                                   | SM     |  EU  |    1343.47 |   039   |
| 026 |   | Slovenia                                 | S5     |  EU  |    1344.93 |   117   |
| 027 | ! | Vienna Intl Ctr                          | 4U1V   |  EU  |    1351.60 |   104   |
| 028 |   | Poland                                   | SP     |  EU  |    1359.16 |   084   |
| 029 |   | Spain                                    | EA     |  EU  |    1402.83 |   191   |
| 030 |   | San Marino                               | T7     |  EU  |    1416.56 |   128   |
| 031 |   | Corsica                                  | TK     |  EU  |    1430.47 |   142   |
| 032 |   | Kaliningrad                              | UA2    |  EU  |    1456.01 |   072   |
| 033 |   | Croatia                                  | 9A     |  EU  |    1481.04 |   118   |
| 034 |   | Market Reef                              | OJ0    |  EU  |    1485.29 |   049   |
| 035 |   | Balearic Islands                         | EA6    |  EU  |    1502.71 |   165   |
| 036 |   | Italy                                    | I      |  EU  |    1519.76 |   131   |
| 037 |   | Slovak Republic                          | OM     |  EU  |    1556.80 |   097   |
| 038 |   | Portugal                                 | CT     |  EU  |    1557.39 |   201   |
| 039 |   | Aland Islands                            | OH0    |  EU  |    1560.98 |   049   |
| 040 | ! | Sov Mil Order of Malta                   | 1A     |  EU  |    1593.83 |   133   |
| 041 |   | Vatican City                             | HV     |  EU  |    1595.77 |   133   |
| 042 |   | Hungary                                  | HA     |  EU  |    1604.93 |   104   |
| 043 | ! | Sardinia                                 | IS     |  EU  |    1622.67 |   145   |
| 044 |   | Lithuania                                | LY     |  EU  |    1653.61 |   069   |
| 045 |   | Iceland                                  | TF     |  EU  |    1655.26 |   330   |
| 046 |   | Bosnia-Herzegovina                       | E7     |  EU  |    1679.27 |   116   |
| 047 |   | Latvia                                   | YL     |  EU  |    1723.95 |   063   |
| 048 |   | Estonia                                  | ES     |  EU  |    1775.27 |   056   |
| 049 | ! | Gibraltar                                | ZB     |  EU  |    1873.09 |   190   |
| 050 |   | Ceuta & Melilla                          | EA9    |  AF  |    1899.37 |   190   |
| 051 | ! | Serbia                                   | YU     |  EU  |    1912.80 |   111   |
| 052 |   | Montenegro                               | 4O     |  EU  |    1917.58 |   118   |
| 053 |   | Belarus                                  | EU     |  EU  |    1945.06 |   074   |
| 054 | ! | Republic of Kosovo                       | Z6     |  EU  |    2018.61 |   114   |
| 055 |   | Romania                                  | YO     |  EU  |    2037.14 |   101   |
| 056 |   | Finland                                  | OH     |  EU  |    2041.68 |   042   |
| 057 |   | Jan Mayen                                | JX     |  EU  |    2060.61 |   353   |
| 058 | ! | Sicily                                   | IT9    |  EU  |    2077.45 |   138   |
| 059 |   | Albania                                  | ZA     |  EU  |    2077.77 |   120   |
| 060 |   | Tunisia                                  | 3V     |  AF  |    2109.59 |   152   |
| 061 |   | North Macedonia                          | Z3     |  EU  |    2128.28 |   116   |
| 062 |   | Ukraine                                  | UR     |  EU  |    2187.52 |   085   |
| 063 | ! | African Italy                            | IG9    |  AF  |    2201.28 |   144   |
| 064 |   | Bulgaria                                 | LZ     |  EU  |    2251.57 |   108   |
| 065 |   | Malta                                    | 9H     |  EU  |    2252.77 |   140   |
| 066 |   | Moldova                                  | ER     |  EU  |    2256.16 |   094   |
| 067 |   | Greece                                   | SV     |  EU  |    2279.89 |   119   |
| 068 |   | Morocco                                  | CN     |  AF  |    2326.95 |   188   |
| 069 | ! | Mount Athos                              | SV/A   |  EU  |    2394.11 |   116   |
| 070 |   | Azores                                   | CU     |  EU  |    2515.23 |   241   |
| 071 |   | Madeira Islands                          | CT3    |  AF  |    2547.53 |   215   |
| 072 | ! | Bear Island                              | JW/B   |  EU  |    2580.99 |   013   |
| 073 | ! | European Turkey                          | TA1    |  EU  |    2627.98 |   107   |
| 074 |   | Algeria                                  | 7X     |  AF  |    2769.15 |   172   |
| 075 |   | European Russia                          | UA     |  EU  |    2811.08 |   070   |
| 076 |   | Crete                                    | SV9    |  EU  |    2836.13 |   122   |
| 077 |   | Svalbard                                 | JW     |  EU  |    2890.95 |   008   |
| 078 |   | Dodecanese                               | SV5    |  EU  |    2939.70 |   117   |
| 079 |   | Canary Islands                           | EA8    |  AF  |    2965.15 |   209   |
| 080 |   | Greenland                                | OX     |  NA  |    3007.48 |   336   |
| 081 |   | Asiatic Turkey                           | TA     |  AS  |    3193.60 |   103   |
| 082 | ! | Libya                                    | 5A     |  AF  |    3212.83 |   145   |
| 083 |   | Western Sahara                           | S0     |  AF  |    3277.24 |   203   |
| 084 |   | Cyprus                                   | 5B     |  AS  |    3345.42 |   112   |
| 085 |   | UK Base Areas on Cyprus                  | ZC4    |  AS  |    3355.17 |   111   |
| 086 | ! | Franz Josef Land                         | R1FJ   |  EU  |    3572.68 |   013   |
| 087 |   | Lebanon                                  | OD     |  AS  |    3617.67 |   110   |
| 088 |   | Georgia                                  | 4L     |  AS  |    3625.15 |   090   |
| 089 | ! | Syria                                    | YK     |  AS  |    3645.56 |   106   |
| 090 |   | Mauritania                               | 5T     |  AF  |    3660.27 |   195   |
| 091 |   | Armenia                                  | EK     |  AS  |    3724.25 |   092   |
| 092 |   | Palestine                                | E4     |  AS  |    3734.04 |   115   |
| 093 |   | Israel                                   | 4X     |  AS  |    3764.11 |   114   |
| 094 |   | Egypt                                    | SU     |  AF  |    3860.06 |   127   |
| 095 |   | Mali                                     | TZ     |  AF  |    3867.45 |   181   |
| 096 |   | Jordan                                   | JY     |  AS  |    3874.35 |   113   |
| 097 |   | Azerbaijan                               | 4J     |  AS  |    3886.96 |   090   |
| 098 |   | St. Pierre & Miquelon                    | FP     |  NA  |    3888.93 |   282   |
| 099 |   | Niger                                    | 5U     |  AF  |    4021.10 |   162   |
| 100 |   | Iraq                                     | YI     |  AS  |    4057.76 |   103   |
| 101 |   | St. Paul Island                          | CY9    |  NA  |    4123.87 |   285   |
| 102 | ! | Sable Island                             | CY0    |  NA  |    4302.38 |   281   |
| 103 |   | Senegal                                  | 6W     |  AF  |    4335.29 |   200   |
| 104 |   | Chad                                     | TT     |  AF  |    4457.32 |   149   |
| 105 |   | Burkina Faso                             | XT     |  AF  |    4533.66 |   180   |
| 106 |   | Cape Verde                               | D4     |  AF  |    4540.04 |   214   |
| 107 |   | The Gambia                               | C5     |  AF  |    4573.15 |   202   |
| 108 |   | Kazakhstan                               | UN     |  AS  |    4573.58 |   068   |
| 109 |   | Guinea-Bissau                            | J5     |  AF  |    4684.67 |   199   |
| 110 | ! | Guinea                                   | 3X     |  AF  |    4717.37 |   193   |
| 111 |   | Kuwait                                   | 9K     |  AS  |    4723.33 |   103   |
| 112 | ! | Turkmenistan                             | EZ     |  AS  |    4771.73 |   085   |
| 113 |   | Benin                                    | TY     |  AF  |    4782.00 |   174   |
| 114 |   | Nigeria                                  | 5N     |  AF  |    4838.85 |   167   |
| 115 |   | Iran                                     | EP     |  AS  |    4885.64 |   095   |
| 116 | ! | Uzbekistan                               | UK     |  AS  |    4930.63 |   077   |
| 117 |   | Saudi Arabia                             | HZ     |  AS  |    4936.33 |   112   |
| 118 | ! | Togo                                     | 5V     |  AF  |    4940.00 |   176   |
| 119 |   | Ghana                                    | 9G     |  AF  |    5011.56 |   180   |
| 120 |   | Sudan                                    | ST     |  AF  |    5015.26 |   136   |
| 121 |   | Sierra Leone                             | 9L     |  AF  |    5036.58 |   196   |
| 122 |   | Cote d'Ivoire                            | TU     |  AF  |    5040.25 |   186   |
| 123 | ! | Asiatic Russia                           | UA9    |  AS  |    5195.69 |   050   |
| 124 |   | Liberia                                  | EL     |  AF  |    5196.75 |   190   |
| 125 |   | Bahrain                                  | A9     |  AS  |    5207.71 |   103   |
| 126 |   | Qatar                                    | A7     |  AS  |    5312.70 |   104   |
| 127 |   | Cameroon                                 | TJ     |  AF  |    5408.83 |   162   |
| 128 | ! | United Nations HQ                        | 4U1U   |  NA  |    5435.86 |   286   |
| 129 |   | Bermuda                                  | VP9    |  NA  |    5451.23 |   271   |
| 130 |   | Canada                                   | VE     |  NA  |    5484.04 |   293   |
| 131 |   | Central African Republic                 | TL     |  AF  |    5487.44 |   150   |
| 132 | ! | Eritrea                                  | E3     |  AF  |    5494.02 |   124   |
| 133 |   | Afghanistan                              | YA     |  AS  |    5537.47 |   083   |
| 134 |   | Kyrgyzstan                               | EX     |  AS  |    5566.25 |   070   |
| 135 |   | Tajikistan                               | EY     |  AS  |    5588.02 |   075   |
| 136 |   | United Arab Emirates                     | A6     |  AS  |    5606.52 |   102   |
| 137 | ! | United States (East Coast)               | K-EAST |  NA  |    5761.92 |   286   |
| 138 | ! | Equatorial Guinea                        | 3C     |  AF  |    5782.71 |   164   |
| 139 |   | Sao Tome & Principe                      | S9     |  AF  |    5890.88 |   169   |
| 140 |   | Oman                                     | A4     |  AS  |    5941.16 |   098   |
| 141 |   | Yemen                                    | 7O     |  AS  |    5967.71 |   114   |
| 142 |   | Djibouti                                 | J2     |  AF  |    5990.81 |   122   |
| 143 |   | Gabon                                    | TR     |  AF  |    6035.25 |   163   |
| 144 | ! | Annobon Island                           | 3C0    |  AF  |    6063.02 |   171   |
| 145 |   | Ethiopia                                 | ET     |  AF  |    6072.25 |   128   |
| 146 |   | Republic of South Sudan                  | Z8     |  AF  |    6124.02 |   138   |
| 147 |   | Republic of the Congo                    | TN     |  AF  |    6183.68 |   159   |
| 148 |   | Pakistan                                 | AP     |  AS  |    6185.11 |   084   |
| 149 | ! | St. Peter & St. Paul                     | PY0S   |  SA  |    6399.68 |   213   |
| 150 |   | Uganda                                   | 5X     |  AF  |    6467.00 |   138   |
|-----|---|------------------------------------------|--------|------|------------|---------|
NOTE: a '!' in the R column indicates a top-50 rare DXCC entity.


Summary:
 Total DXCC countries reachable   : 150
 Total DXCC in closest countries  : 150
 Total rare DXCC reachable        : 029
 Total continent(s) reachable     :   5 [EU, AS, NA, AF, SA]
```

Optimal setup

```
$ java -jar dxcc-planner-2.0.jar -optimal -center G -headings 4
Current settings:
-----------------
Central DXCC entity       : G (England)
Maximum beamings to use   : 4
Antenna beamwidth to use  : 60
Maximum distance to assume a DXCC entity as a 'close' one : 6500 Km
Maximum rare DXCC entities to consider : 50

Main 4 headings to use for optimal DXCC entities coverage
 - Heading 001 at 045 degrees
 - Heading 002 at 188 degrees
 - Heading 003 at 118 degrees
 - Heading 004 at 287 degrees

DXCC entities around heading of 118 degress (within +/- 30 degress from the main heading)
|---|---|------------------------------------------|--------|------|------------|---------|
| R | C |             DXCC Entity name             | Prefix | Cont |   Distance | Heading |
|---|---|------------------------------------------|--------|------|------------|---------|
|   | * | Netherlands                              | PA     |  EU  |     472.47 |   093   |
|   | * | Belgium                                  | ON     |  EU  |     492.09 |   115   |
|   | * | Luxembourg                               | LX     |  EU  |     602.54 |   117   |
|   | * | Fed. Rep. of Germany                     | DL     |  EU  |     810.49 |   099   |
| ! | * | ITU HQ                                   | 4U1I   |  EU  |     912.11 |   140   |
|   | * | Switzerland                              | HB     |  EU  |     949.17 |   129   |
|   | * | Liechtenstein                            | HB0    |  EU  |    1006.71 |   124   |
|   | * | Monaco                                   | 3A     |  EU  |    1198.75 |   143   |
|   | * | Austria                                  | OE     |  EU  |    1214.29 |   113   |
|   | * | Czech Republic                           | OK     |  EU  |    1247.41 |   097   |
|   | * | Slovenia                                 | S5     |  EU  |    1344.93 |   117   |
| ! | * | Vienna Intl Ctr                          | 4U1V   |  EU  |    1351.60 |   104   |
|   | * | San Marino                               | T7     |  EU  |    1416.56 |   128   |
|   | * | Corsica                                  | TK     |  EU  |    1430.47 |   142   |
|   | * | Croatia                                  | 9A     |  EU  |    1481.04 |   118   |
|   | * | Italy                                    | I      |  EU  |    1519.76 |   131   |
|   | * | Slovak Republic                          | OM     |  EU  |    1556.80 |   097   |
| ! | * | Sov Mil Order of Malta                   | 1A     |  EU  |    1593.83 |   133   |
|   | * | Vatican City                             | HV     |  EU  |    1595.77 |   133   |
|   | * | Hungary                                  | HA     |  EU  |    1604.93 |   104   |
| ! | * | Sardinia                                 | IS     |  EU  |    1622.67 |   145   |
|   | * | Bosnia-Herzegovina                       | E7     |  EU  |    1679.27 |   116   |
| ! | * | Serbia                                   | YU     |  EU  |    1912.80 |   111   |
|   | * | Montenegro                               | 4O     |  EU  |    1917.58 |   118   |
| ! | * | Republic of Kosovo                       | Z6     |  EU  |    2018.61 |   114   |
|   | * | Romania                                  | YO     |  EU  |    2037.14 |   101   |
| ! | * | Sicily                                   | IT9    |  EU  |    2077.45 |   138   |
|   | * | Albania                                  | ZA     |  EU  |    2077.77 |   120   |
|   | * | North Macedonia                          | Z3     |  EU  |    2128.28 |   116   |
| ! | * | African Italy                            | IG9    |  AF  |    2201.28 |   144   |
|   | * | Bulgaria                                 | LZ     |  EU  |    2251.57 |   108   |
|   | * | Malta                                    | 9H     |  EU  |    2252.77 |   140   |
|   | * | Moldova                                  | ER     |  EU  |    2256.16 |   094   |
|   | * | Greece                                   | SV     |  EU  |    2279.89 |   119   |
| ! | * | Mount Athos                              | SV/A   |  EU  |    2394.11 |   116   |
| ! | * | European Turkey                          | TA1    |  EU  |    2627.98 |   107   |
|   | * | Crete                                    | SV9    |  EU  |    2836.13 |   122   |
|   | * | Dodecanese                               | SV5    |  EU  |    2939.70 |   117   |
|   | * | Asiatic Turkey                           | TA     |  AS  |    3193.60 |   103   |
| ! | * | Libya                                    | 5A     |  AF  |    3212.83 |   145   |
|   | * | Cyprus                                   | 5B     |  AS  |    3345.42 |   112   |
|   | * | UK Base Areas on Cyprus                  | ZC4    |  AS  |    3355.17 |   111   |
|   | * | Lebanon                                  | OD     |  AS  |    3617.67 |   110   |
|   | * | Georgia                                  | 4L     |  AS  |    3625.15 |   090   |
| ! | * | Syria                                    | YK     |  AS  |    3645.56 |   106   |
|   | * | Armenia                                  | EK     |  AS  |    3724.25 |   092   |
|   | * | Palestine                                | E4     |  AS  |    3734.04 |   115   |
|   | * | Israel                                   | 4X     |  AS  |    3764.11 |   114   |
|   | * | Egypt                                    | SU     |  AF  |    3860.06 |   127   |
|   | * | Jordan                                   | JY     |  AS  |    3874.35 |   113   |
|   | * | Azerbaijan                               | 4J     |  AS  |    3886.96 |   090   |
|   | * | Iraq                                     | YI     |  AS  |    4057.76 |   103   |
|   | * | Kuwait                                   | 9K     |  AS  |    4723.33 |   103   |
|   | * | Iran                                     | EP     |  AS  |    4885.64 |   095   |
|   | * | Saudi Arabia                             | HZ     |  AS  |    4936.33 |   112   |
|   | * | Sudan                                    | ST     |  AF  |    5015.26 |   136   |
|   | * | Bahrain                                  | A9     |  AS  |    5207.71 |   103   |
|   | * | Qatar                                    | A7     |  AS  |    5312.70 |   104   |
| ! | * | Eritrea                                  | E3     |  AF  |    5494.02 |   124   |
|   | * | United Arab Emirates                     | A6     |  AS  |    5606.52 |   102   |
|   | * | Oman                                     | A4     |  AS  |    5941.16 |   098   |
|   | * | Yemen                                    | 7O     |  AS  |    5967.71 |   114   |
|   | * | Djibouti                                 | J2     |  AF  |    5990.81 |   122   |
|   | * | Ethiopia                                 | ET     |  AF  |    6072.25 |   128   |
|   | * | Republic of South Sudan                  | Z8     |  AF  |    6124.02 |   138   |
|   | * | Uganda                                   | 5X     |  AF  |    6467.00 |   138   |
|   |   | Rwanda                                   | 9X     |  AF  |    6727.03 |   143   |
|   |   | Burundi                                  | 9U     |  AF  |    6872.83 |   144   |
|   |   | Kenya                                    | 5Z     |  AF  |    6886.82 |   133   |
|   |   | Somalia                                  | T5     |  AF  |    7091.07 |   125   |
|   |   | Tanzania                                 | 5H     |  AF  |    7309.28 |   140   |
|   |   | Lakshadweep Islands                      | VU7    |  AS  |    7958.24 |   095   |
|   |   | Malawi                                   | 7Q     |  AF  |    8163.10 |   144   |
|   |   | Seychelles                               | S7     |  AF  |    8304.59 |   119   |
|   |   | Comoros                                  | D6     |  AF  |    8330.53 |   134   |
| ! |   | Glorioso Islands                         | FT/G   |  AF  |    8519.70 |   130   |
|   |   | Mayotte                                  | FH     |  AF  |    8544.64 |   133   |
|   |   | Maldives                                 | 8Q     |  AS  |    8629.52 |   099   |
|   |   | Mozambique                               | C9     |  AF  |    8641.89 |   144   |
|   |   | Sri Lanka                                | 4S     |  AS  |    8809.08 |   091   |
| ! |   | Juan de Nova & Europa                    | FT/J   |  AF  |    8846.09 |   137   |
| ! |   | Agalega & St. Brandon                    | 3B6    |  AF  |    8921.50 |   122   |
|   |   | Madagascar                               | 5R     |  AF  |    9220.73 |   134   |
| ! |   | Tromelin Island                          | FT/T   |  AF  |    9319.42 |   126   |
|   |   | Chagos Islands                           | VQ9    |  AF  |    9592.71 |   107   |
|   |   | Reunion Island                           | FR     |  AF  |    9874.38 |   128   |
|   |   | Mauritius                                | 3B8    |  AF  |    9908.62 |   126   |
|   |   | Rodriguez Island                         | 3B9    |  AF  |   10177.57 |   121   |
|   |   | Cocos (Keeling) Islands                  | VK9C   |  OC  |   11636.19 |   090   |
| ! |   | Crozet Island                            | FT/W   |  AF  |   12130.33 |   144   |
| ! |   | Amsterdam & St. Paul Is.                 | FT/Z   |  AF  |   12611.24 |   122   |
| ! |   | Kerguelen Islands                        | FT/X   |  AF  |   13124.68 |   135   |
| ! |   | Heard Island                             | VK0H   |  AF  |   13659.27 |   136   |
| ! |   | Macquarie Island                         | VK0M   |  OC  |   18709.62 |   106   |
|---|---|------------------------------------------|--------|------|------------|---------|

DXCC entities around heading of 188 degress (within +/- 30 degress from the main heading)
|---|---|------------------------------------------|--------|------|------------|---------|
| R | C |             DXCC Entity name             | Prefix | Cont |   Distance | Heading |
|---|---|------------------------------------------|--------|------|------------|---------|
|   | * | Guernsey                                 | GU     |  EU  |     377.20 |   192   |
|   | * | Jersey                                   | GJ     |  EU  |     397.85 |   187   |
|   | * | France                                   | F      |  EU  |     793.35 |   160   |
| ! | * | Andorra                                  | C3     |  EU  |    1156.17 |   167   |
|   | * | Spain                                    | EA     |  EU  |    1402.83 |   191   |
|   | * | Balearic Islands                         | EA6    |  EU  |    1502.71 |   165   |
|   | * | Portugal                                 | CT     |  EU  |    1557.39 |   201   |
| ! | * | Gibraltar                                | ZB     |  EU  |    1873.09 |   190   |
|   | * | Ceuta & Melilla                          | EA9    |  AF  |    1899.37 |   190   |
|   | * | Morocco                                  | CN     |  AF  |    2326.95 |   188   |
|   | * | Madeira Islands                          | CT3    |  AF  |    2547.53 |   215   |
|   | * | Algeria                                  | 7X     |  AF  |    2769.15 |   172   |
|   | * | Canary Islands                           | EA8    |  AF  |    2965.15 |   209   |
|   | * | Western Sahara                           | S0     |  AF  |    3277.24 |   203   |
|   | * | Mauritania                               | 5T     |  AF  |    3660.27 |   195   |
|   | * | Mali                                     | TZ     |  AF  |    3867.45 |   181   |
|   | * | Niger                                    | 5U     |  AF  |    4021.10 |   162   |
|   | * | Senegal                                  | 6W     |  AF  |    4335.29 |   200   |
|   | * | Burkina Faso                             | XT     |  AF  |    4533.66 |   180   |
|   | * | Cape Verde                               | D4     |  AF  |    4540.04 |   214   |
|   | * | The Gambia                               | C5     |  AF  |    4573.15 |   202   |
|   | * | Guinea-Bissau                            | J5     |  AF  |    4684.67 |   199   |
| ! | * | Guinea                                   | 3X     |  AF  |    4717.37 |   193   |
|   | * | Benin                                    | TY     |  AF  |    4782.00 |   174   |
|   | * | Nigeria                                  | 5N     |  AF  |    4838.85 |   167   |
| ! | * | Togo                                     | 5V     |  AF  |    4940.00 |   176   |
|   | * | Ghana                                    | 9G     |  AF  |    5011.56 |   180   |
|   | * | Sierra Leone                             | 9L     |  AF  |    5036.58 |   196   |
|   | * | Cote d'Ivoire                            | TU     |  AF  |    5040.25 |   186   |
|   | * | Liberia                                  | EL     |  AF  |    5196.75 |   190   |
|   | * | Cameroon                                 | TJ     |  AF  |    5408.83 |   162   |
| ! | * | Equatorial Guinea                        | 3C     |  AF  |    5782.71 |   164   |
|   | * | Sao Tome & Principe                      | S9     |  AF  |    5890.88 |   169   |
|   | * | Gabon                                    | TR     |  AF  |    6035.25 |   163   |
| ! | * | Annobon Island                           | 3C0    |  AF  |    6063.02 |   171   |
|   | * | Republic of the Congo                    | TN     |  AF  |    6183.68 |   159   |
| ! | * | St. Peter & St. Paul                     | PY0S   |  SA  |    6399.68 |   213   |
|   |   | Ascension Island                         | ZD8    |  AF  |    6859.49 |   194   |
|   |   | Fernando de Noronha                      | PY0F   |  SA  |    6932.17 |   215   |
|   |   | Angola                                   | D2     |  AF  |    7504.67 |   158   |
|   |   | St. Helena                               | ZD7    |  AF  |    7654.47 |   184   |
|   |   | Namibia                                  | V5     |  AF  |    8504.09 |   162   |
| ! |   | Trindade & Martim Vaz                    | PY0T   |  SA  |    8579.84 |   206   |
|   |   | South Africa                             | ZS     |  AF  |    9395.98 |   158   |
|   |   | Tristan da Cunha & Gough Islands         | ZD9    |  AF  |   10051.16 |   188   |
| ! |   | Bouvet                                   | 3Y/B   |  AF  |   11927.39 |   177   |
| ! |   | South Georgia Island                     | VP8/G  |  SA  |   12369.31 |   201   |
| ! |   | South Sandwich Islands                   | VP8/S  |  SA  |   12566.72 |   193   |
|   |   | Falkland Islands                         | VP8    |  SA  |   12776.61 |   215   |
| ! |   | South Orkney Islands                     | VP8/O  |  SA  |   13199.49 |   202   |
| ! |   | South Shetland Islands                   | VP8/H  |  SA  |   13718.35 |   208   |
| ! |   | Peter 1 Island                           | 3Y/P   |  SA  |   15303.44 |   212   |
|   |   | Antarctica                               | CE9    |  SA  |   15875.30 |   180   |
|---|---|------------------------------------------|--------|------|------------|---------|

DXCC entities around heading of 045 degress (within +/- 30 degress from the main heading)
|---|---|------------------------------------------|--------|------|------------|---------|
| R | C |             DXCC Entity name             | Prefix | Cont |   Distance | Heading |
|---|---|------------------------------------------|--------|------|------------|---------|
|   | * | Denmark                                  | OZ     |  EU  |     823.65 |   059   |
|   | * | Norway                                   | LA     |  EU  |    1111.58 |   030   |
|   | * | Sweden                                   | SM     |  EU  |    1343.47 |   039   |
|   | * | Kaliningrad                              | UA2    |  EU  |    1456.01 |   072   |
|   | * | Market Reef                              | OJ0    |  EU  |    1485.29 |   049   |
|   | * | Aland Islands                            | OH0    |  EU  |    1560.98 |   049   |
|   | * | Lithuania                                | LY     |  EU  |    1653.61 |   069   |
|   | * | Latvia                                   | YL     |  EU  |    1723.95 |   063   |
|   | * | Estonia                                  | ES     |  EU  |    1775.27 |   056   |
|   | * | Belarus                                  | EU     |  EU  |    1945.06 |   074   |
|   | * | Finland                                  | OH     |  EU  |    2041.68 |   042   |
|   | * | European Russia                          | UA     |  EU  |    2811.08 |   070   |
|   | * | Kazakhstan                               | UN     |  AS  |    4573.58 |   068   |
| ! | * | Asiatic Russia                           | UA9    |  AS  |    5195.69 |   050   |
|   | * | Kyrgyzstan                               | EX     |  AS  |    5566.25 |   070   |
|   |   | Mongolia                                 | JT     |  AS  |    6800.38 |   049   |
|   |   | Nepal                                    | 9N     |  AS  |    7385.66 |   074   |
|   |   | China                                    | BY     |  AS  |    7702.32 |   057   |
|   |   | Bhutan                                   | A5     |  AS  |    7722.99 |   071   |
|   |   | Bangladesh                               | S2     |  AS  |    7968.74 |   074   |
| ! |   | DPR of Korea                             | P5     |  AS  |    8563.87 |   038   |
|   |   | Myanmar                                  | XZ     |  AS  |    8758.68 |   071   |
|   |   | Republic of Korea                        | HL     |  AS  |    8977.26 |   039   |
|   |   | Japan                                    | JA     |  AS  |    9367.68 |   031   |
|   |   | Laos                                     | XW     |  AS  |    9432.93 |   066   |
|   |   | Macao                                    | XX9    |  AS  |    9606.44 |   057   |
|   |   | Hong Kong                                | VR     |  AS  |    9628.06 |   056   |
|   |   | Thailand                                 | HS     |  AS  |    9629.48 |   073   |
| ! |   | Taiwan                                   | BV     |  AS  |    9855.25 |   050   |
|   |   | Vietnam                                  | 3W     |  AS  |    9856.47 |   065   |
| ! |   | Pratas Island                            | BV9P   |  AS  |    9916.70 |   055   |
|   |   | Cambodia                                 | XU     |  AS  |    9945.76 |   069   |
|   |   | Ogasawara                                | JD/O   |  AS  |   10466.66 |   031   |
| ! |   | Scarborough Reef                         | BS7    |  AS  |   10503.46 |   057   |
|   |   | Spratly Islands                          | 1S     |  AS  |   10785.86 |   063   |
|   |   | Philippines                              | DU     |  OC  |   10941.08 |   055   |
| ! |   | Minami Torishima                         | JD/M   |  OC  |   11123.09 |   022   |
|   |   | Brunei Darussalam                        | V8     |  OC  |   11307.28 |   066   |
|   |   | East Malaysia                            | 9M6    |  OC  |   11395.74 |   068   |
|   |   | Mariana Islands                          | KH0    |  OC  |   11830.64 |   033   |
|   |   | Guam                                     | KH2    |  OC  |   11981.13 |   034   |
|   |   | Palau                                    | T8     |  OC  |   12138.67 |   046   |
|   |   | Micronesia                               | V6     |  OC  |   13109.41 |   022   |
|   |   | Timor - Leste                            | 4W     |  OC  |   13240.50 |   063   |
|   |   | Papua New Guinea                         | P2     |  OC  |   14437.90 |   042   |
|   |   | Solomon Islands                          | H4     |  OC  |   14869.06 |   025   |
|   |   | Australia                                | VK     |  OC  |   14978.69 |   068   |
| ! |   | Temotu Province                          | H40    |  OC  |   15201.98 |   018   |
| ! |   | Willis Island                            | VK9W   |  OC  |   15248.07 |   042   |
| ! |   | Mellish Reef                             | VK9M   |  OC  |   15614.51 |   035   |
| ! |   | Chesterfield Islands                     | FK/C   |  OC  |   15964.31 |   033   |
|   |   | Vanuatu                                  | YJ     |  OC  |   16013.27 |   016   |
|   |   | New Caledonia                            | FK     |  OC  |   16364.02 |   022   |
|   |   | Lord Howe Island                         | VK9L   |  OC  |   17182.42 |   041   |
|   |   | Norfolk Island                           | VK9N   |  OC  |   17235.92 |   022   |
|   |   | New Zealand                              | ZL     |  OC  |   18736.45 |   020   |
|---|---|------------------------------------------|--------|------|------------|---------|

DXCC entities around heading of 287 degress (within +/- 30 degress from the main heading)
|---|---|------------------------------------------|--------|------|------------|---------|
| R | C |             DXCC Entity name             | Prefix | Cont |   Distance | Heading |
|---|---|------------------------------------------|--------|------|------------|---------|
|   | * | Isle of Man                              | GD     |  EU  |     257.40 |   309   |
|   | * | Northern Ireland                         | GI     |  EU  |     405.86 |   304   |
|   | * | Ireland                                  | EI     |  EU  |     440.49 |   277   |
|   | * | St. Pierre & Miquelon                    | FP     |  NA  |    3888.93 |   282   |
|   | * | St. Paul Island                          | CY9    |  NA  |    4123.87 |   285   |
| ! | * | Sable Island                             | CY0    |  NA  |    4302.38 |   281   |
| ! | * | United Nations HQ                        | 4U1U   |  NA  |    5435.86 |   286   |
|   | * | Bermuda                                  | VP9    |  NA  |    5451.23 |   271   |
|   | * | Canada                                   | VE     |  NA  |    5484.04 |   293   |
| ! | * | United States (East Coast)               | K-EAST |  NA  |    5761.92 |   286   |
|   |   | Anguilla                                 | VP2E   |  NA  |    6501.04 |   258   |
|   |   | St. Martin                               | FS     |  NA  |    6516.05 |   258   |
|   |   | St. Barthelemy                           | FJ     |  NA  |    6518.55 |   258   |
|   |   | Sint Maarten                             | PJ7    |  NA  |    6519.56 |   258   |
|   |   | St. Kitts & Nevis                        | V4     |  NA  |    6561.47 |   257   |
|   |   | Saba & St. Eustatius                     | PJ5    |  NA  |    6565.07 |   258   |
|   |   | British Virgin Islands                   | VP2V   |  NA  |    6608.01 |   259   |
|   |   | US Virgin Islands                        | KP2    |  NA  |    6663.34 |   259   |
|   |   | Puerto Rico                              | KP4    |  NA  |    6740.48 |   261   |
| ! |   | Aves Island                              | YV0    |  NA  |    6764.24 |   257   |
|   |   | Turks & Caicos Islands                   | VP5    |  NA  |    6782.32 |   268   |
| ! |   | Desecheo Island                          | KP5    |  NA  |    6837.71 |   262   |
| ! |   | United States (Mid-USA)                  | K-MID  |  NA  |    6841.75 |   298   |
| ! |   | Bahamas                                  | C6     |  NA  |    6860.22 |   273   |
|   |   | Dominican Republic                       | HI     |  NA  |    6934.82 |   265   |
|   |   | Haiti                                    | HH     |  NA  |    7044.91 |   266   |
|   |   | Guantanamo Bay                           | KG4    |  NA  |    7150.91 |   269   |
| ! |   | Navassa Island                           | KP1    |  NA  |    7287.53 |   268   |
| ! |   | Cuba                                     | CM     |  NA  |    7359.66 |   274   |
|   |   | Bonaire                                  | PJ4    |  SA  |    7375.98 |   258   |
|   |   | Curacao                                  | PJ2    |  SA  |    7428.16 |   259   |
|   |   | Aruba                                    | P4     |  SA  |    7461.34 |   260   |
|   |   | Jamaica                                  | 6Y     |  NA  |    7470.82 |   270   |
|   |   | Cayman Islands                           | ZF     |  NA  |    7627.00 |   274   |
| ! |   | San Andres & Providencia                 | HK0/A  |  NA  |    8245.76 |   269   |
|   |   | Belize                                   | V3     |  NA  |    8327.48 |   278   |
|   |   | Honduras                                 | HR     |  NA  |    8387.04 |   275   |
|   |   | Colombia                                 | HK     |  SA  |    8395.53 |   258   |
|   |   | Nicaragua                                | YN     |  NA  |    8440.90 |   272   |
|   |   | Panama                                   | HP     |  NA  |    8441.21 |   266   |
| ! |   | United States (West Coast)               | K-WEST |  NA  |    8450.76 |   315   |
|   |   | Guatemala                                | TG     |  NA  |    8563.76 |   278   |
|   |   | El Salvador                              | YS     |  NA  |    8607.94 |   276   |
|   |   | Costa Rica                               | TI     |  NA  |    8622.29 |   270   |
|   |   | Mexico                                   | XE     |  NA  |    8700.96 |   289   |
|   |   | Malpelo Island                           | HK0/M  |  SA  |    8990.69 |   264   |
| ! |   | Cocos Island                             | TI9    |  NA  |    9221.92 |   269   |
|   |   | Ecuador                                  | HC     |  SA  |    9258.34 |   258   |
| ! |   | Revillagigedo                            | XF4    |  NA  |    9593.27 |   296   |
|   |   | Galapagos Islands                        | HC8    |  SA  |   10047.00 |   269   |
|   |   | Clipperton Island                        | FO/C   |  NA  |   10258.60 |   290   |
|   |   | Easter Island                            | CE0Y   |  SA  |   13553.26 |   266   |
|   |   | Marquesas Islands                        | FO/M   |  OC  |   13885.54 |   307   |
| ! |   | Ducie Island                             | VP6/D  |  OC  |   14388.61 |   280   |
|   |   | Pitcairn Island                          | VP6    |  OC  |   14767.17 |   285   |
|   |   | French Polynesia                         | FO     |  OC  |   15220.79 |   312   |
|   |   | Austral Islands                          | FO/A   |  OC  |   15777.93 |   307   |
|---|---|------------------------------------------|--------|------|------------|---------|

NOTE: a '*' in the C column indicates a DXCC entity among the 150 closest ones (up to 6500 km)
NOTE: a '!' in the R column indicates a top-50 rare DXCC entity.

DXCC entities breakdown per heading
|---------|------------|---------|------|
| Heading | Total DXCC | Closest | Rare |
|---------|------------|---------|------|
|   118   |    094     |   066   | 022  |
|   188   |    053     |   037   | 014  |
|   045   |    056     |   015   | 010  |
|   287   |    057     |   010   | 014  |
|---------|------------|---------|------|

Summary:
 Total DXCC countries reachable   : 260
 Total DXCC in closest countries  : 128
 Total rare DXCC reachable        : 060
 Total continent(s) reachable     :   6 [EU, AS, NA, OC, AF, SA]
```

Setup evaluation

```
$ java -jar dxcc-planner-2.0.jar -evaluate -center G -headings 280,110 -beamwidth 51
Current settings:
-----------------
Central DXCC entity       : G (England)
Beamings to evaluate      : [280, 110]
Antenna beamwidth to use  : 51
Maximum distance to assume a DXCC entity as a 'close' one : 6500 Km
Maximum rare DXCC entities to consider : 50

Main 2 headings to use for optimal DXCC entities coverage
 - Heading 001 at 280 degrees
 - Heading 002 at 110 degrees

DXCC entities around heading of 280 degress (within +/- 25 degress from the main heading)
|---|---|------------------------------------------|--------|------|------------|---------|
| R | C |             DXCC Entity name             | Prefix | Cont |   Distance | Heading |
|---|---|------------------------------------------|--------|------|------------|---------|
|   | * | Northern Ireland                         | GI     |  EU  |     405.86 |   304   |
|   | * | Ireland                                  | EI     |  EU  |     440.49 |   277   |
|   | * | St. Pierre & Miquelon                    | FP     |  NA  |    3888.93 |   282   |
|   | * | St. Paul Island                          | CY9    |  NA  |    4123.87 |   285   |
| ! | * | Sable Island                             | CY0    |  NA  |    4302.38 |   281   |
| ! | * | United Nations HQ                        | 4U1U   |  NA  |    5435.86 |   286   |
|   | * | Bermuda                                  | VP9    |  NA  |    5451.23 |   271   |
|   | * | Canada                                   | VE     |  NA  |    5484.04 |   293   |
| ! | * | United States (East Coast)               | K-EAST |  NA  |    5761.92 |   286   |
|   |   | Anguilla                                 | VP2E   |  NA  |    6501.04 |   258   |
|   |   | St. Martin                               | FS     |  NA  |    6516.05 |   258   |
|   |   | St. Barthelemy                           | FJ     |  NA  |    6518.55 |   258   |
|   |   | Sint Maarten                             | PJ7    |  NA  |    6519.56 |   258   |
|   |   | Antigua & Barbuda                        | V2     |  NA  |    6523.46 |   256   |
|   |   | St. Kitts & Nevis                        | V4     |  NA  |    6561.47 |   257   |
|   |   | Saba & St. Eustatius                     | PJ5    |  NA  |    6565.07 |   258   |
|   |   | Montserrat                               | VP2M   |  NA  |    6576.39 |   256   |
|   |   | Guadeloupe                               | FG     |  NA  |    6597.56 |   255   |
|   |   | British Virgin Islands                   | VP2V   |  NA  |    6608.01 |   259   |
|   |   | US Virgin Islands                        | KP2    |  NA  |    6663.34 |   259   |
|   |   | Puerto Rico                              | KP4    |  NA  |    6740.48 |   261   |
| ! |   | Aves Island                              | YV0    |  NA  |    6764.24 |   257   |
|   |   | Turks & Caicos Islands                   | VP5    |  NA  |    6782.32 |   268   |
| ! |   | Desecheo Island                          | KP5    |  NA  |    6837.71 |   262   |
| ! |   | United States (Mid-USA)                  | K-MID  |  NA  |    6841.75 |   298   |
| ! |   | Bahamas                                  | C6     |  NA  |    6860.22 |   273   |
|   |   | Dominican Republic                       | HI     |  NA  |    6934.82 |   265   |
|   |   | Haiti                                    | HH     |  NA  |    7044.91 |   266   |
|   |   | Guantanamo Bay                           | KG4    |  NA  |    7150.91 |   269   |
| ! |   | Navassa Island                           | KP1    |  NA  |    7287.53 |   268   |
| ! |   | Cuba                                     | CM     |  NA  |    7359.66 |   274   |
|   |   | Bonaire                                  | PJ4    |  SA  |    7375.98 |   258   |
|   |   | Curacao                                  | PJ2    |  SA  |    7428.16 |   259   |
|   |   | Aruba                                    | P4     |  SA  |    7461.34 |   260   |
|   |   | Jamaica                                  | 6Y     |  NA  |    7470.82 |   270   |
|   |   | Cayman Islands                           | ZF     |  NA  |    7627.00 |   274   |
| ! |   | San Andres & Providencia                 | HK0/A  |  NA  |    8245.76 |   269   |
|   |   | Belize                                   | V3     |  NA  |    8327.48 |   278   |
|   |   | Honduras                                 | HR     |  NA  |    8387.04 |   275   |
|   |   | Colombia                                 | HK     |  SA  |    8395.53 |   258   |
|   |   | Nicaragua                                | YN     |  NA  |    8440.90 |   272   |
|   |   | Panama                                   | HP     |  NA  |    8441.21 |   266   |
|   |   | Guatemala                                | TG     |  NA  |    8563.76 |   278   |
|   |   | El Salvador                              | YS     |  NA  |    8607.94 |   276   |
|   |   | Costa Rica                               | TI     |  NA  |    8622.29 |   270   |
|   |   | Mexico                                   | XE     |  NA  |    8700.96 |   289   |
|   |   | Malpelo Island                           | HK0/M  |  SA  |    8990.69 |   264   |
| ! |   | Cocos Island                             | TI9    |  NA  |    9221.92 |   269   |
|   |   | Ecuador                                  | HC     |  SA  |    9258.34 |   258   |
| ! |   | Revillagigedo                            | XF4    |  NA  |    9593.27 |   296   |
|   |   | Galapagos Islands                        | HC8    |  SA  |   10047.00 |   269   |
|   |   | Clipperton Island                        | FO/C   |  NA  |   10258.60 |   290   |
|   |   | Easter Island                            | CE0Y   |  SA  |   13553.26 |   266   |
| ! |   | Ducie Island                             | VP6/D  |  OC  |   14388.61 |   280   |
|   |   | Pitcairn Island                          | VP6    |  OC  |   14767.17 |   285   |
|---|---|------------------------------------------|--------|------|------------|---------|

DXCC entities around heading of 110 degress (within +/- 25 degress from the main heading)
|---|---|------------------------------------------|--------|------|------------|---------|
| R | C |             DXCC Entity name             | Prefix | Cont |   Distance | Heading |
|---|---|------------------------------------------|--------|------|------------|---------|
|   | * | Netherlands                              | PA     |  EU  |     472.47 |   093   |
|   | * | Belgium                                  | ON     |  EU  |     492.09 |   115   |
|   | * | Luxembourg                               | LX     |  EU  |     602.54 |   117   |
|   | * | Fed. Rep. of Germany                     | DL     |  EU  |     810.49 |   099   |
|   | * | Switzerland                              | HB     |  EU  |     949.17 |   129   |
|   | * | Liechtenstein                            | HB0    |  EU  |    1006.71 |   124   |
|   | * | Austria                                  | OE     |  EU  |    1214.29 |   113   |
|   | * | Czech Republic                           | OK     |  EU  |    1247.41 |   097   |
|   | * | Slovenia                                 | S5     |  EU  |    1344.93 |   117   |
| ! | * | Vienna Intl Ctr                          | 4U1V   |  EU  |    1351.60 |   104   |
|   | * | San Marino                               | T7     |  EU  |    1416.56 |   128   |
|   | * | Croatia                                  | 9A     |  EU  |    1481.04 |   118   |
|   | * | Italy                                    | I      |  EU  |    1519.76 |   131   |
|   | * | Slovak Republic                          | OM     |  EU  |    1556.80 |   097   |
| ! | * | Sov Mil Order of Malta                   | 1A     |  EU  |    1593.83 |   133   |
|   | * | Vatican City                             | HV     |  EU  |    1595.77 |   133   |
|   | * | Hungary                                  | HA     |  EU  |    1604.93 |   104   |
|   | * | Bosnia-Herzegovina                       | E7     |  EU  |    1679.27 |   116   |
| ! | * | Serbia                                   | YU     |  EU  |    1912.80 |   111   |
|   | * | Montenegro                               | 4O     |  EU  |    1917.58 |   118   |
| ! | * | Republic of Kosovo                       | Z6     |  EU  |    2018.61 |   114   |
|   | * | Romania                                  | YO     |  EU  |    2037.14 |   101   |
|   | * | Albania                                  | ZA     |  EU  |    2077.77 |   120   |
|   | * | North Macedonia                          | Z3     |  EU  |    2128.28 |   116   |
|   | * | Ukraine                                  | UR     |  EU  |    2187.52 |   085   |
|   | * | Bulgaria                                 | LZ     |  EU  |    2251.57 |   108   |
|   | * | Moldova                                  | ER     |  EU  |    2256.16 |   094   |
|   | * | Greece                                   | SV     |  EU  |    2279.89 |   119   |
| ! | * | Mount Athos                              | SV/A   |  EU  |    2394.11 |   116   |
| ! | * | European Turkey                          | TA1    |  EU  |    2627.98 |   107   |
|   | * | Crete                                    | SV9    |  EU  |    2836.13 |   122   |
|   | * | Dodecanese                               | SV5    |  EU  |    2939.70 |   117   |
|   | * | Asiatic Turkey                           | TA     |  AS  |    3193.60 |   103   |
|   | * | Cyprus                                   | 5B     |  AS  |    3345.42 |   112   |
|   | * | UK Base Areas on Cyprus                  | ZC4    |  AS  |    3355.17 |   111   |
|   | * | Lebanon                                  | OD     |  AS  |    3617.67 |   110   |
|   | * | Georgia                                  | 4L     |  AS  |    3625.15 |   090   |
| ! | * | Syria                                    | YK     |  AS  |    3645.56 |   106   |
|   | * | Armenia                                  | EK     |  AS  |    3724.25 |   092   |
|   | * | Palestine                                | E4     |  AS  |    3734.04 |   115   |
|   | * | Israel                                   | 4X     |  AS  |    3764.11 |   114   |
|   | * | Egypt                                    | SU     |  AF  |    3860.06 |   127   |
|   | * | Jordan                                   | JY     |  AS  |    3874.35 |   113   |
|   | * | Azerbaijan                               | 4J     |  AS  |    3886.96 |   090   |
|   | * | Iraq                                     | YI     |  AS  |    4057.76 |   103   |
|   | * | Kuwait                                   | 9K     |  AS  |    4723.33 |   103   |
| ! | * | Turkmenistan                             | EZ     |  AS  |    4771.73 |   085   |
|   | * | Iran                                     | EP     |  AS  |    4885.64 |   095   |
|   | * | Saudi Arabia                             | HZ     |  AS  |    4936.33 |   112   |
|   | * | Bahrain                                  | A9     |  AS  |    5207.71 |   103   |
|   | * | Qatar                                    | A7     |  AS  |    5312.70 |   104   |
| ! | * | Eritrea                                  | E3     |  AF  |    5494.02 |   124   |
|   | * | United Arab Emirates                     | A6     |  AS  |    5606.52 |   102   |
|   | * | Oman                                     | A4     |  AS  |    5941.16 |   098   |
|   | * | Yemen                                    | 7O     |  AS  |    5967.71 |   114   |
|   | * | Djibouti                                 | J2     |  AF  |    5990.81 |   122   |
|   | * | Ethiopia                                 | ET     |  AF  |    6072.25 |   128   |
|   |   | Kenya                                    | 5Z     |  AF  |    6886.82 |   133   |
|   |   | Somalia                                  | T5     |  AF  |    7091.07 |   125   |
|   |   | Lakshadweep Islands                      | VU7    |  AS  |    7958.24 |   095   |
|   |   | Seychelles                               | S7     |  AF  |    8304.59 |   119   |
|   |   | Comoros                                  | D6     |  AF  |    8330.53 |   134   |
| ! |   | Glorioso Islands                         | FT/G   |  AF  |    8519.70 |   130   |
|   |   | Mayotte                                  | FH     |  AF  |    8544.64 |   133   |
|   |   | Maldives                                 | 8Q     |  AS  |    8629.52 |   099   |
|   |   | Sri Lanka                                | 4S     |  AS  |    8809.08 |   091   |
| ! |   | Agalega & St. Brandon                    | 3B6    |  AF  |    8921.50 |   122   |
|   |   | Madagascar                               | 5R     |  AF  |    9220.73 |   134   |
| ! |   | Tromelin Island                          | FT/T   |  AF  |    9319.42 |   126   |
|   |   | Chagos Islands                           | VQ9    |  AF  |    9592.71 |   107   |
|   |   | Reunion Island                           | FR     |  AF  |    9874.38 |   128   |
|   |   | Mauritius                                | 3B8    |  AF  |    9908.62 |   126   |
|   |   | Rodriguez Island                         | 3B9    |  AF  |   10177.57 |   121   |
|   |   | Cocos (Keeling) Islands                  | VK9C   |  OC  |   11636.19 |   090   |
| ! |   | Amsterdam & St. Paul Is.                 | FT/Z   |  AF  |   12611.24 |   122   |
| ! |   | Macquarie Island                         | VK0M   |  OC  |   18709.62 |   106   |
|---|---|------------------------------------------|--------|------|------------|---------|

NOTE: a '*' in the C column indicates a DXCC entity among the 150 closest ones (up to 6500 km)
NOTE: a '!' in the R column indicates a top-50 rare DXCC entity.

DXCC entities breakdown per heading
|---------|------------|---------|------|
| Heading | Total DXCC | Closest | Rare |
|---------|------------|---------|------|
|   280   |    055     |   009   | 013  |
|   110   |    076     |   057   | 014  |
|---------|------------|---------|------|

Summary:
 Total DXCC countries reachable   : 131
 Total DXCC in closest countries  : 066
 Total rare DXCC reachable        : 027
 Total continent(s) reachable     :   6 [EU, AS, NA, OC, AF, SA]
```




