# dxcc-planner
A command utility to assist ham radio operators in planning their antennas for maximum DXCC reachability (especially when on limited space)

This program can assist in planning your antennas for making the best usage of your (limited) space and still  
maximise your opportunities to work as many DXCC entities as possible.You can use it to calculate all the nearest  
DXCC entities (the easy ones) that are reachable with the minimal setup and can get you started.
It can  also help you out to find the optimal headings that will give you coverage of as many DXCC entities 
as possible given how many directional antennas you can put up.

NOTE: The program does not provide propagation predictions. It takes into consideration only geographical position of the DXCC entities in order to calculate their relative position to you location and reachability. 

## Installation
Download the dxccplanner.jar and save it on your system.It is an executable jar requiring Java 1.7. 
on linux systems you will need to give this file executable permissions by executing
```
$ chmod +x dxccplanner.jar
```

Run it with the appropariate options as described below.

NOTE: when specifying the center DXCC prefix use the basic prefix only. As an exception, ONLY For USA you have to use either "K-East" or "K-Mid" or "K-West" to specify the appropriate area.

## Evaluating your setup
For example, if you are restriced to just a fixed orientation dipole you can use the program to evaluate which DXCC entities are in the beaming of your antenna by providing the headings of the main lobes and optionally the beamwidth of the dipole (which by default is assumed to be 60 degrees).Here is how you could achieve this:

if you can put a dipole beaming to azimuths 110/290 and you live in England (DXCC prefix G) you can use this command to evaluate your opportunities with this setup:

```
$ ./dcxxplanner.jar -evaluate -center G -headings 290,110
```
you will see a long detailed printout which summarises as follows:

```
DXCC entities breakdown per heading
|---------|------------|---------|------|
| Heading | Total DXCC | Closest | Rare |
|---------|------------|---------|------|
|   290   |    044     |   010   | 009  |
|   110   |    085     |   060   | 013  |
|---------|------------|---------|------|
Total DXCC countries reachable   : 129
Total DXCC in closest countries  : 070
Total rare DXCC reachable        : 022
Total continent(s) reachable     :   6 [EU, NA, SA, OC, AS, AF]
```

if not specified with the command option *-distance* the distance limit to assume a DXCC entity as 'near-by' is 6500 Km, a range that can be reached with 2 hops.Keep in mind that very close entities (eg <800 Km) may reside in your skip zone and may require an NVIS antenna.

Since the program does not take into accoutn the band/frequency you intend to use,you may need to run it with various parameters to see the effect.In the above example if the antenna was not a dipole, but a 44ft doublet , which is usable from 30m up to 10m band, the reachability would be different on each band because the beamwidth of the antenna decreases as the frequency increases.We can run multiple times by specifying the appropriate beamwidth.

for example, on 20m a 44ft doublet has a horizontal beamwidth of around 70 degrees which decreases to 51 degrees and 31 degress on 15m and 10m respectively.To see the differences on DXCC capabilities of this antenna we can run the following commands

for 20m where beamwidth is 70 degrees
```
$ ./dcxxplanner.jar -evaluate -center G -headings 290,110 -beamwidth 70
```

for 15m where beamwidth is 51 degrees
```
$ ./dcxxplanner.jar -evaluate -center G -headings 290,110 -beamwidth 51
```

for 10m where beamwidth is 31 degrees
```
$ ./dcxxplanner.jar -evaluate -center G -headings 290,110 -beamwidth 31
```

for 20m (beamwidth 70 degrees) the output  will be
```
|---------|------------|---------|------|
| Heading | Total DXCC | Closest | Rare |
|---------|------------|---------|------|
|   290   |    061     |   010   | 010  |
|   110   |    101     |   066   | 015  |
|---------|------------|---------|------|
Total DXCC countries reachable   : 162
Total DXCC in closest countries  : 076
Total rare DXCC reachable        : 025
Total continent(s) reachable     :   6 [EU, NA, SA, OC, AS, AF]
```

for 15m (beamwidth 51 degrees) the output  will be
```
|---------|------------|---------|------|
| Heading | Total DXCC | Closest | Rare |
|---------|------------|---------|------|
|   290   |    039     |   010   | 008  |
|   110   |    073     |   054   | 009  |
|---------|------------|---------|------|
Total DXCC countries reachable   : 112
Total DXCC in closest countries  : 064
Total rare DXCC reachable        : 017
Total continent(s) reachable     :   6 [EU, NA, SA, OC, AS, AF]
```

for 10m (beamwidth 31 degrees) the output  will be
```
|---------|------------|---------|------|
| Heading | Total DXCC | Closest | Rare |
|---------|------------|---------|------|
|   290   |    018     |   008   | 004  |
|   110   |    048     |   040   | 006  |
|---------|------------|---------|------|
Total DXCC countries reachable   : 066
Total DXCC in closest countries  : 048
Total rare DXCC reachable        : 010
Total continent(s) reachable     :   5 [EU, NA, OC, AS, AF]
```

### Finding an optimal setup
If you do not know what would be a good antenna setup for your location you can specify a number of headings (assuming for example that you can put up directional antennas to fixed directions) and the programme will find the beamings where the most DXCC entities are located in order to get the most coverage.

for example
```
$ ./dcxxplanner.jar -optimal -center G -headings 4
```

the summary will be
```
|---------|------------|---------|------|
| Heading | Total DXCC | Closest | Rare |
|---------|------------|---------|------|
|   333   |    033     |   007   | 007  |
|   251   |    065     |   004   | 008  |
|   056   |    062     |   022   | 011  |
|   142   |    084     |   052   | 013  |
|---------|------------|---------|------|
Total DXCC countries reachable   : 244
Total DXCC in closest countries  : 085
Total rare DXCC reachable        : 039
Total continent(s) reachable     :   6 [EU, NA, OC, SA, AS, AF]
```

indicating that the prominent headings are 333,251,056 and 142. Note that these headings may not be easily realisable for your available property.

### Nearest DXCC entities
if you want to find the nearest DXCC enities no matter their direction (maybe because you are thinking about instaling a vertical antenna) you can run the following command

```
$ ./dcxxplanner.jar -nearest -center G
```
which will print by default the 150 closest DXCC countries (you can override the list size by providing the *-limit* option)

### example outputs
Nearest DXCC entities
```
$ ./dcxxplanner.jar -nearest -center G 
Current settings:
-----------------
Central DXCC entity       : G (England)
Maximum distance to assume 'close DXCC': 6500 Km
Maximum DXCC entities to print         : 150
Maximum rare DXCC entities to use      : 50


Displaying up to 150 closest DXCC entities (up to 6500 km)
|-----|---|------------------------------------------|--------|------|------------|---------|
|  #  | R |             DXCC Entity name             | Prefix | Cont |   Distance | Heading |
|-----|---|------------------------------------------|--------|------|------------|---------|
| 001 |   | Wales                                    | GW     |  EU  |     162.30 |   251   |
| 002 |   | Isle of Man                              | GD     |  EU  |     257.40 |   309   |
| 003 |   | Guernsey                                 | GU     |  EU  |     377.20 |   192   |
| 004 |   | Jersey                                   | GJ     |  EU  |     397.85 |   187   |
| 005 |   | Northern Ireland                         | GI     |  EU  |     405.86 |   304   |
| 006 |   | Ireland                                  | EI     |  EU  |     440.49 |   277   |
| 007 |   | Netherlands                              | PA     |  EU  |     472.47 |   093   |
| 008 |   | Scotland                                 | GM     |  EU  |     482.59 |   340   |
| 009 |   | Belgium                                  | ON     |  EU  |     492.09 |   115   |
| 010 |   | Luxembourg                               | LX     |  EU  |     602.54 |   117   |
| 011 |   | France                                   | F      |  EU  |     793.35 |   160   |
| 012 |   | Fed. Rep. of Germany                     | DL     |  EU  |     810.49 |   099   |
| 013 |   | Denmark                                  | OZ     |  EU  |     823.65 |   059   |
| 014 |   | ITU HQ                                   | 4U1I   |  EU  |     912.11 |   140   |
| 015 |   | Switzerland                              | HB     |  EU  |     949.17 |   129   |
| 016 |   | Liechtenstein                            | HB0    |  EU  |    1006.71 |   124   |
| 017 |   | Faroe Islands                            | OY     |  EU  |    1083.63 |   344   |
| 018 |   | Norway                                   | LA     |  EU  |    1111.58 |   030   |
| 019 |   | Andorra                                  | C3     |  EU  |    1156.17 |   167   |
| 020 |   | Monaco                                   | 3A     |  EU  |    1198.75 |   143   |
| 021 |   | Austria                                  | OE     |  EU  |    1214.29 |   113   |
| 022 |   | Czech Republic                           | OK     |  EU  |    1247.41 |   097   |
| 023 |   | Sweden                                   | SM     |  EU  |    1343.47 |   039   |
| 024 |   | Slovenia                                 | S5     |  EU  |    1344.93 |   117   |
| 025 |   | Poland                                   | SP     |  EU  |    1359.16 |   084   |
| 026 |   | Spain                                    | EA     |  EU  |    1402.83 |   191   |
| 027 |   | San Marino                               | T7     |  EU  |    1416.56 |   128   |
| 028 |   | Corsica                                  | TK     |  EU  |    1430.47 |   142   |
| 029 |   | Kaliningrad                              | UA2    |  EU  |    1456.01 |   072   |
| 030 |   | Croatia                                  | 9A     |  EU  |    1481.04 |   118   |
| 031 |   | Market Reef                              | OJ0    |  EU  |    1485.29 |   049   |
| 032 |   | Balearic Islands                         | EA6    |  EU  |    1502.71 |   165   |
| 033 |   | Italy                                    | I      |  EU  |    1519.76 |   131   |
| 034 |   | Slovak Republic                          | OM     |  EU  |    1556.80 |   097   |
| 035 |   | Portugal                                 | CT     |  EU  |    1557.39 |   201   |
| 036 |   | Aland Islands                            | OH0    |  EU  |    1560.98 |   049   |
| 037 |   | Sov Mil Order of Malta                   | 1A     |  EU  |    1593.83 |   133   |
| 038 |   | Vatican City                             | HV     |  EU  |    1595.77 |   133   |
| 039 |   | Hungary                                  | HA     |  EU  |    1604.93 |   104   |
| 040 |   | Sardinia                                 | IS     |  EU  |    1622.67 |   145   |
| 041 |   | Lithuania                                | LY     |  EU  |    1653.61 |   069   |
| 042 |   | Iceland                                  | TF     |  EU  |    1655.26 |   330   |
| 043 |   | Bosnia-Herzegovina                       | E7     |  EU  |    1679.27 |   116   |
| 044 |   | Latvia                                   | YL     |  EU  |    1723.95 |   063   |
| 045 |   | Estonia                                  | ES     |  EU  |    1775.27 |   056   |
| 046 |   | Gibraltar                                | ZB     |  EU  |    1873.09 |   190   |
| 047 |   | Ceuta & Melilla                          | EA9    |  AF  |    1899.37 |   190   |
| 048 | ! | Serbia                                   | YU     |  EU  |    1912.80 |   111   |
| 049 |   | Montenegro                               | 4O     |  EU  |    1917.58 |   118   |
| 050 |   | Belarus                                  | EU     |  EU  |    1945.06 |   074   |
| 051 |   | Romania                                  | YO     |  EU  |    2037.14 |   101   |
| 052 |   | Finland                                  | OH     |  EU  |    2041.68 |   042   |
| 053 |   | Jan Mayen                                | JX     |  EU  |    2060.61 |   353   |
| 054 |   | Albania                                  | ZA     |  EU  |    2077.77 |   120   |
| 055 |   | Tunisia                                  | 3V     |  AF  |    2109.59 |   152   |
| 056 |   | Macedonia                                | Z3     |  EU  |    2128.28 |   116   |
| 057 |   | Ukraine                                  | UR     |  EU  |    2187.52 |   085   |
| 058 |   | Bulgaria                                 | LZ     |  EU  |    2251.57 |   108   |
| 059 |   | Malta                                    | 9H     |  EU  |    2252.77 |   140   |
| 060 |   | Moldova                                  | ER     |  EU  |    2256.16 |   094   |
| 061 |   | Greece                                   | SV     |  EU  |    2279.89 |   119   |
| 062 |   | Morocco                                  | CN     |  AF  |    2326.95 |   188   |
| 063 | ! | Mount Athos                              | SV/a   |  EU  |    2394.11 |   116   |
| 064 |   | Azores                                   | CU     |  EU  |    2515.23 |   241   |
| 065 |   | Madeira Islands                          | CT3    |  AF  |    2547.53 |   215   |
| 066 |   | Algeria                                  | 7X     |  AF  |    2769.15 |   172   |
| 067 |   | European Russia                          | UA     |  EU  |    2811.08 |   070   |
| 068 |   | Crete                                    | SV9    |  EU  |    2836.13 |   122   |
| 069 |   | Svalbard                                 | JW     |  EU  |    2890.95 |   008   |
| 070 |   | Dodecanese                               | SV5    |  EU  |    2939.70 |   117   |
| 071 |   | Canary Islands                           | EA8    |  AF  |    2973.73 |   208   |
| 072 |   | Greenland                                | OX     |  NA  |    3007.48 |   336   |
| 073 |   | Asiatic Turkey                           | TA     |  AS  |    3193.60 |   103   |
| 074 | ! | Libya                                    | 5A     |  AF  |    3212.83 |   145   |
| 075 |   | Western Sahara                           | S0     |  AF  |    3277.24 |   203   |
| 076 |   | Cyprus                                   | 5B     |  AS  |    3345.42 |   112   |
| 077 |   | UK Base Areas on Cyprus                  | ZC4    |  AS  |    3355.17 |   111   |
| 078 | ! | Franz Josef Land                         | R1FJ   |  EU  |    3572.68 |   013   |
| 079 |   | Lebanon                                  | OD     |  AS  |    3617.67 |   110   |
| 080 |   | Georgia                                  | 4L     |  AS  |    3625.15 |   090   |
| 081 | ! | Syria                                    | YK     |  AS  |    3645.56 |   106   |
| 082 |   | Mauritania                               | 5T     |  AF  |    3660.27 |   195   |
| 083 |   | Armenia                                  | EK     |  AS  |    3724.25 |   092   |
| 084 |   | Palestine                                | E4     |  AS  |    3734.04 |   115   |
| 085 |   | Israel                                   | 4X     |  AS  |    3764.11 |   114   |
| 086 |   | Egypt                                    | SU     |  AF  |    3860.06 |   127   |
| 087 |   | Mali                                     | TZ     |  AF  |    3867.45 |   181   |
| 088 |   | Jordan                                   | JY     |  AS  |    3874.35 |   113   |
| 089 |   | Azerbaijan                               | 4J     |  AS  |    3886.96 |   090   |
| 090 |   | St. Pierre & Miquelon                    | FP     |  NA  |    3888.93 |   282   |
| 091 |   | Niger                                    | 5U     |  AF  |    4021.10 |   162   |
| 092 |   | Iraq                                     | YI     |  AS  |    4057.76 |   103   |
| 093 |   | St. Paul Island                          | CY9    |  NA  |    4123.87 |   285   |
| 094 | ! | Sable Island                             | CY0    |  NA  |    4302.38 |   281   |
| 095 |   | Senegal                                  | 6W     |  AF  |    4335.29 |   200   |
| 096 |   | Chad                                     | TT     |  AF  |    4457.32 |   149   |
| 097 |   | Burkina Faso                             | XT     |  AF  |    4533.66 |   180   |
| 098 |   | Cape Verde                               | D4     |  AF  |    4540.04 |   214   |
| 099 |   | The Gambia                               | C5     |  AF  |    4573.15 |   202   |
| 100 |   | Kazakhstan                               | UN     |  AS  |    4573.58 |   068   |
| 101 |   | Guinea-Bissau                            | J5     |  AF  |    4684.67 |   199   |
| 102 |   | Guinea                                   | 3X     |  AF  |    4717.37 |   193   |
| 103 |   | Kuwait                                   | 9K     |  AS  |    4723.33 |   103   |
| 104 | ! | Turkmenistan                             | EZ     |  AS  |    4771.73 |   085   |
| 105 |   | Benin                                    | TY     |  AF  |    4782.00 |   174   |
| 106 |   | Nigeria                                  | 5N     |  AF  |    4838.85 |   167   |
| 107 |   | Iran                                     | EP     |  AS  |    4885.64 |   095   |
| 108 | ! | Uzbekistan                               | UK     |  AS  |    4930.63 |   077   |
| 109 |   | Saudi Arabia                             | HZ     |  AS  |    4936.33 |   112   |
| 110 |   | Togo                                     | 5V     |  AF  |    4940.00 |   176   |
| 111 |   | Ghana                                    | 9G     |  AF  |    5011.56 |   180   |
| 112 |   | Sudan                                    | ST     |  AF  |    5015.26 |   136   |
| 113 |   | Sierra Leone                             | 9L     |  AF  |    5036.58 |   196   |
| 114 |   | Cote d'Ivoire                            | TU     |  AF  |    5040.25 |   186   |
| 115 | ! | Asiatic Russia                           | UA9    |  AS  |    5195.69 |   050   |
| 116 |   | Liberia                                  | EL     |  AF  |    5196.75 |   190   |
| 117 |   | Bahrain                                  | A9     |  AS  |    5207.71 |   103   |
| 118 |   | Qatar                                    | A7     |  AS  |    5312.70 |   104   |
| 119 |   | Cameroon                                 | TJ     |  AF  |    5408.83 |   162   |
| 120 | ! | United Nations HQ                        | 4U1U   |  NA  |    5435.86 |   286   |
| 121 |   | Bermuda                                  | VP9    |  NA  |    5451.23 |   271   |
| 122 |   | Canada                                   | VE     |  NA  |    5484.04 |   293   |
| 123 |   | Central African Republic                 | TL     |  AF  |    5487.44 |   150   |
| 124 | ! | Eritrea                                  | E3     |  AF  |    5494.02 |   124   |
| 125 |   | Afghanistan                              | YA     |  AS  |    5537.47 |   083   |
| 126 |   | Kyrgyzstan                               | EX     |  AS  |    5566.25 |   070   |
| 127 |   | Tajikistan                               | EY     |  AS  |    5588.02 |   075   |
| 128 |   | United Arab Emirates                     | A6     |  AS  |    5606.52 |   102   |
| 129 |   | United States (East Coast)               | K      |  NA  |    5761.92 |   286   |
| 130 | ! | Equatorial Guinea                        | 3C     |  AF  |    5782.71 |   164   |
| 131 |   | Sao Tome & Principe                      | S9     |  AF  |    5890.88 |   169   |
| 132 |   | Oman                                     | A4     |  AS  |    5941.16 |   098   |
| 133 |   | Yemen                                    | 7O     |  AS  |    5967.71 |   114   |
| 134 |   | Djibouti                                 | J2     |  AF  |    5990.81 |   122   |
| 135 |   | Gabon                                    | TR     |  AF  |    6035.25 |   163   |
| 136 | ! | Annobon Island                           | 3C0    |  AF  |    6063.02 |   171   |
| 137 |   | Ethiopia                                 | ET     |  AF  |    6072.25 |   128   |
| 138 |   | Republic of South Sudan                  | Z8     |  AF  |    6124.02 |   138   |
| 139 |   | Republic of the Congo                    | TN     |  AF  |    6183.68 |   159   |
| 140 |   | Pakistan                                 | AP     |  AS  |    6185.11 |   084   |
| 141 | ! | St. Peter & St. Paul                     | PY0S   |  SA  |    6399.68 |   213   |
| 142 |   | Uganda                                   | 5X     |  AF  |    6467.00 |   138   |
|-----|---|------------------------------------------|--------|------|------------|---------|
NOTE: a '!' in the R column indicates a top-50 rare DXCC entity.

Total rare DXCC reachable        : 014
Total continent(s) reachable     :   5 [EU, AF, NA, AS, SA]
```

Optimal setup

```
$ ./dcxxplanner.jar -optimal -center G -headings 4
Current settings:
-----------------
Central DXCC entity       : G (England)
Maximum beamings to use   : 4
Antenna beamwidth to use  : 60
Maximum distance to assume 'close DXCC': 6500 Km
Maximum rare DXCC entities to use      : 50

Main 4 headings to use for optimal DXCC entities coverage
Heading 001 at 195 degrees
Heading 002 at 290 degrees
Heading 003 at 049 degrees
Heading 004 at 122 degrees

DXCC entities around heading of 195 degress (within +/- 30 degress from the main heading)
|---|---|------------------------------------------|--------|------|------------|---------|
| R | C |             DXCC Entity name             | Prefix | Cont |   Distance | Heading |
|---|---|------------------------------------------|--------|------|------------|---------|
|   | * | Guernsey                                 | GU     |  EU  |     377.20 |   192   |
|   | * | Jersey                                   | GJ     |  EU  |     397.85 |   187   |
|   | * | Andorra                                  | C3     |  EU  |    1156.17 |   167   |
|   | * | Spain                                    | EA     |  EU  |    1402.83 |   191   |
|   | * | Balearic Islands                         | EA6    |  EU  |    1502.71 |   165   |
|   | * | Portugal                                 | CT     |  EU  |    1557.39 |   201   |
|   | * | Gibraltar                                | ZB     |  EU  |    1873.09 |   190   |
|   | * | Ceuta & Melilla                          | EA9    |  AF  |    1899.37 |   190   |
|   | * | Morocco                                  | CN     |  AF  |    2326.95 |   188   |
|   | * | Madeira Islands                          | CT3    |  AF  |    2547.53 |   215   |
|   | * | Algeria                                  | 7X     |  AF  |    2769.15 |   172   |
|   | * | Canary Islands                           | EA8    |  AF  |    2973.73 |   208   |
|   | * | Western Sahara                           | S0     |  AF  |    3277.24 |   203   |
|   | * | Mauritania                               | 5T     |  AF  |    3660.27 |   195   |
|   | * | Mali                                     | TZ     |  AF  |    3867.45 |   181   |
|   | * | Senegal                                  | 6W     |  AF  |    4335.29 |   200   |
|   | * | Burkina Faso                             | XT     |  AF  |    4533.66 |   180   |
|   | * | Cape Verde                               | D4     |  AF  |    4540.04 |   214   |
|   | * | The Gambia                               | C5     |  AF  |    4573.15 |   202   |
|   | * | Guinea-Bissau                            | J5     |  AF  |    4684.67 |   199   |
|   | * | Guinea                                   | 3X     |  AF  |    4717.37 |   193   |
|   | * | Benin                                    | TY     |  AF  |    4782.00 |   174   |
|   | * | Nigeria                                  | 5N     |  AF  |    4838.85 |   167   |
|   | * | Togo                                     | 5V     |  AF  |    4940.00 |   176   |
|   | * | Ghana                                    | 9G     |  AF  |    5011.56 |   180   |
|   | * | Sierra Leone                             | 9L     |  AF  |    5036.58 |   196   |
|   | * | Cote d'Ivoire                            | TU     |  AF  |    5040.25 |   186   |
|   | * | Liberia                                  | EL     |  AF  |    5196.75 |   190   |
|   | * | Sao Tome & Principe                      | S9     |  AF  |    5890.88 |   169   |
| ! | * | Annobon Island                           | 3C0    |  AF  |    6063.02 |   171   |
| ! | * | St. Peter & St. Paul                     | PY0S   |  SA  |    6399.68 |   213   |
|   |   | Ascension Island                         | ZD8    |  AF  |    6859.49 |   194   |
|   |   | Fernando de Noronha                      | PY0F   |  SA  |    6932.17 |   215   |
|   |   | St. Helena                               | ZD7    |  AF  |    7654.47 |   184   |
| ! |   | Trindade & Martim Vaz                    | PY0T   |  SA  |    8579.84 |   206   |
|   |   | Tristan da Cunha & Gough Islands         | ZD9    |  AF  |   10051.16 |   188   |
|   |   | Uruguay                                  | CX     |  SA  |   10897.35 |   223   |
| ! |   | Bouvet                                   | 3Y/b   |  AF  |   11927.39 |   177   |
| ! |   | South Georgia Island                     | VP8/g  |  SA  |   12369.31 |   201   |
| ! |   | South Sandwich Islands                   | VP8/s  |  SA  |   12566.72 |   193   |
|   |   | Falkland Islands                         | VP8    |  SA  |   12776.61 |   215   |
| ! |   | South Orkney Islands                     | VP8/o  |  SA  |   13199.49 |   202   |
|   |   | South Shetland Islands                   | VP8/h  |  SA  |   13718.35 |   208   |
| ! |   | Peter 1 Island                           | 3Y/p   |  SA  |   15303.44 |   212   |
|   |   | Antarctica                               | CE9    |  SA  |   15875.30 |   180   |
|---|---|------------------------------------------|--------|------|------------|---------|

DXCC entities around heading of 290 degress (within +/- 30 degress from the main heading)
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
|   | * | United States (East Coast)               | K      |  NA  |    5761.92 |   286   |
|   |   | Puerto Rico                              | KP4    |  NA  |    6740.48 |   261   |
|   |   | Turks & Caicos Islands                   | VP5    |  NA  |    6782.32 |   268   |
| ! |   | Desecheo Island                          | KP5    |  NA  |    6837.71 |   262   |
|   |   | United States (Mid-USA)                  | K      |  NA  |    6841.75 |   298   |
|   |   | Bahamas                                  | C6     |  NA  |    6860.22 |   273   |
|   |   | Dominican Republic                       | HI     |  NA  |    6934.82 |   265   |
|   |   | Haiti                                    | HH     |  NA  |    7044.91 |   266   |
|   |   | Guantanamo Bay                           | KG4    |  NA  |    7150.91 |   269   |
| ! |   | Navassa Island                           | KP1    |  NA  |    7287.53 |   268   |
| ! |   | Cuba                                     | CM     |  NA  |    7359.66 |   274   |
|   |   | Aruba                                    | P4     |  SA  |    7461.34 |   260   |
|   |   | Jamaica                                  | 6Y     |  NA  |    7470.82 |   270   |
|   |   | Cayman Islands                           | ZF     |  NA  |    7627.00 |   274   |
| ! |   | San Andres & Providencia                 | HK0/a  |  NA  |    8245.76 |   269   |
|   |   | Belize                                   | V3     |  NA  |    8327.48 |   278   |
|   |   | Honduras                                 | HR     |  NA  |    8387.04 |   275   |
|   |   | Nicaragua                                | YN     |  NA  |    8440.90 |   272   |
|   |   | Panama                                   | HP     |  NA  |    8441.21 |   266   |
|   |   | United States (West Coast)               | K      |  NA  |    8450.76 |   315   |
|   |   | Guatemala                                | TG     |  NA  |    8563.76 |   278   |
|   |   | El Salvador                              | YS     |  NA  |    8607.94 |   276   |
|   |   | Costa Rica                               | TI     |  NA  |    8622.29 |   270   |
|   |   | Mexico                                   | XE     |  NA  |    8700.96 |   289   |
|   |   | Malpelo Island                           | HK0/m  |  SA  |    8990.69 |   264   |
| ! |   | Cocos Island                             | TI9    |  NA  |    9221.92 |   269   |
| ! |   | Revillagigedo                            | XF4    |  NA  |    9593.27 |   296   |
|   |   | Galapagos Islands                        | HC8    |  SA  |   10047.00 |   269   |
|   |   | Clipperton Island                        | FO/c   |  NA  |   10258.60 |   290   |
|   |   | Easter Island                            | CE0Y   |  SA  |   13553.26 |   266   |
|   |   | Marquesas Islands                        | FO/m   |  OC  |   13885.54 |   307   |
| ! |   | Ducie Island                             | VP6/d  |  OC  |   14388.61 |   280   |
|   |   | Pitcairn Island                          | VP6    |  OC  |   14590.14 |   284   |
|   |   | French Polynesia                         | FO     |  OC  |   15220.79 |   312   |
|   |   | Austral Islands                          | FO/a   |  OC  |   15777.93 |   307   |
|---|---|------------------------------------------|--------|------|------------|---------|

DXCC entities around heading of 049 degress (within +/- 30 degress from the main heading)
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
| ! | * | Uzbekistan                               | UK     |  AS  |    4930.63 |   077   |
| ! | * | Asiatic Russia                           | UA9    |  AS  |    5195.69 |   050   |
|   | * | Kyrgyzstan                               | EX     |  AS  |    5566.25 |   070   |
|   | * | Tajikistan                               | EY     |  AS  |    5588.02 |   075   |
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
|   |   | Ogasawara                                | JD/o   |  AS  |   10466.66 |   031   |
| ! |   | Scarborough Reef                         | BS7    |  AS  |   10503.46 |   057   |
|   |   | West Malaysia                            | 9M2    |  AS  |   10569.57 |   076   |
|   |   | Spratly Islands                          | 1S     |  AS  |   10785.86 |   063   |
|   |   | Singapore                                | 9V     |  AS  |   10902.78 |   076   |
|   |   | Philippines                              | DU     |  OC  |   10941.08 |   055   |
| ! |   | Minami Torishima                         | JD/m   |  OC  |   11123.09 |   022   |
|   |   | Brunei Darussalam                        | V8     |  OC  |   11307.28 |   066   |
|   |   | East Malaysia                            | 9M6    |  OC  |   11395.74 |   068   |
|   |   | Mariana Islands                          | KH0    |  OC  |   11830.64 |   033   |
|   |   | Guam                                     | KH2    |  OC  |   11981.13 |   034   |
|   |   | Indonesia                                | YB     |  OC  |   12080.41 |   077   |
|   |   | Palau                                    | T8     |  OC  |   12138.67 |   046   |
|   |   | Micronesia                               | V6     |  OC  |   13109.41 |   022   |
|   |   | Timor - Leste                            | 4W     |  OC  |   13240.50 |   063   |
|   |   | Papua New Guinea                         | P2     |  OC  |   14437.90 |   042   |
|   |   | Solomon Islands                          | H4     |  OC  |   14869.06 |   025   |
|   |   | Australia                                | VK     |  OC  |   14978.69 |   068   |
| ! |   | Willis Island                            | VK9W   |  OC  |   15248.07 |   042   |
| ! |   | Mellish Reef                             | VK9M   |  OC  |   15614.51 |   035   |
| ! |   | Chesterfield Islands                     | FK/c   |  OC  |   15964.31 |   033   |
|   |   | New Caledonia                            | FK     |  OC  |   16364.02 |   022   |
|   |   | Lord Howe Island                         | VK9L   |  OC  |   17182.42 |   041   |
|   |   | Norfolk Island                           | VK9N   |  OC  |   17235.92 |   022   |
|   |   | New Zealand                              | ZL     |  OC  |   18736.45 |   020   |
|---|---|------------------------------------------|--------|------|------------|---------|

DXCC entities around heading of 122 degress (within +/- 30 degress from the main heading)
|---|---|------------------------------------------|--------|------|------------|---------|
| R | C |             DXCC Entity name             | Prefix | Cont |   Distance | Heading |
|---|---|------------------------------------------|--------|------|------------|---------|
|   | * | Netherlands                              | PA     |  EU  |     472.47 |   093   |
|   | * | Belgium                                  | ON     |  EU  |     492.09 |   115   |
|   | * | Luxembourg                               | LX     |  EU  |     602.54 |   117   |
|   | * | Fed. Rep. of Germany                     | DL     |  EU  |     810.49 |   099   |
|   | * | ITU HQ                                   | 4U1I   |  EU  |     912.11 |   140   |
|   | * | Switzerland                              | HB     |  EU  |     949.17 |   129   |
|   | * | Liechtenstein                            | HB0    |  EU  |    1006.71 |   124   |
|   | * | Monaco                                   | 3A     |  EU  |    1198.75 |   143   |
|   | * | Austria                                  | OE     |  EU  |    1214.29 |   113   |
|   | * | Czech Republic                           | OK     |  EU  |    1247.41 |   097   |
|   | * | Slovenia                                 | S5     |  EU  |    1344.93 |   117   |
|   | * | San Marino                               | T7     |  EU  |    1416.56 |   128   |
|   | * | Corsica                                  | TK     |  EU  |    1430.47 |   142   |
|   | * | Croatia                                  | 9A     |  EU  |    1481.04 |   118   |
|   | * | Italy                                    | I      |  EU  |    1519.76 |   131   |
|   | * | Slovak Republic                          | OM     |  EU  |    1556.80 |   097   |
|   | * | Sov Mil Order of Malta                   | 1A     |  EU  |    1593.83 |   133   |
|   | * | Vatican City                             | HV     |  EU  |    1595.77 |   133   |
|   | * | Hungary                                  | HA     |  EU  |    1604.93 |   104   |
|   | * | Sardinia                                 | IS     |  EU  |    1622.67 |   145   |
|   | * | Bosnia-Herzegovina                       | E7     |  EU  |    1679.27 |   116   |
| ! | * | Serbia                                   | YU     |  EU  |    1912.80 |   111   |
|   | * | Montenegro                               | 4O     |  EU  |    1917.58 |   118   |
|   | * | Romania                                  | YO     |  EU  |    2037.14 |   101   |
|   | * | Albania                                  | ZA     |  EU  |    2077.77 |   120   |
|   | * | Macedonia                                | Z3     |  EU  |    2128.28 |   116   |
|   | * | Bulgaria                                 | LZ     |  EU  |    2251.57 |   108   |
|   | * | Malta                                    | 9H     |  EU  |    2252.77 |   140   |
|   | * | Moldova                                  | ER     |  EU  |    2256.16 |   094   |
|   | * | Greece                                   | SV     |  EU  |    2279.89 |   119   |
| ! | * | Mount Athos                              | SV/a   |  EU  |    2394.11 |   116   |
|   | * | Crete                                    | SV9    |  EU  |    2836.13 |   122   |
|   | * | Dodecanese                               | SV5    |  EU  |    2939.70 |   117   |
|   | * | Asiatic Turkey                           | TA     |  AS  |    3193.60 |   103   |
| ! | * | Libya                                    | 5A     |  AF  |    3212.83 |   145   |
|   | * | Cyprus                                   | 5B     |  AS  |    3345.42 |   112   |
|   | * | UK Base Areas on Cyprus                  | ZC4    |  AS  |    3355.17 |   111   |
|   | * | Lebanon                                  | OD     |  AS  |    3617.67 |   110   |
| ! | * | Syria                                    | YK     |  AS  |    3645.56 |   106   |
|   | * | Armenia                                  | EK     |  AS  |    3724.25 |   092   |
|   | * | Palestine                                | E4     |  AS  |    3734.04 |   115   |
|   | * | Israel                                   | 4X     |  AS  |    3764.11 |   114   |
|   | * | Egypt                                    | SU     |  AF  |    3860.06 |   127   |
|   | * | Jordan                                   | JY     |  AS  |    3874.35 |   113   |
|   | * | Iraq                                     | YI     |  AS  |    4057.76 |   103   |
|   | * | Chad                                     | TT     |  AF  |    4457.32 |   149   |
|   | * | Kuwait                                   | 9K     |  AS  |    4723.33 |   103   |
|   | * | Iran                                     | EP     |  AS  |    4885.64 |   095   |
|   | * | Saudi Arabia                             | HZ     |  AS  |    4936.33 |   112   |
|   | * | Sudan                                    | ST     |  AF  |    5015.26 |   136   |
|   | * | Bahrain                                  | A9     |  AS  |    5207.71 |   103   |
|   | * | Qatar                                    | A7     |  AS  |    5312.70 |   104   |
|   | * | Central African Republic                 | TL     |  AF  |    5487.44 |   150   |
| ! | * | Eritrea                                  | E3     |  AF  |    5494.02 |   124   |
|   | * | United Arab Emirates                     | A6     |  AS  |    5606.52 |   102   |
|   | * | Oman                                     | A4     |  AS  |    5941.16 |   098   |
|   | * | Yemen                                    | 7O     |  AS  |    5967.71 |   114   |
|   | * | Djibouti                                 | J2     |  AF  |    5990.81 |   122   |
|   | * | Ethiopia                                 | ET     |  AF  |    6072.25 |   128   |
|   | * | Republic of South Sudan                  | Z8     |  AF  |    6124.02 |   138   |
|   | * | Uganda                                   | 5X     |  AF  |    6467.00 |   138   |
|   |   | Dem. Rep. of the Congo                   | 9Q     |  AF  |    6624.59 |   151   |
|   |   | Rwanda                                   | 9X     |  AF  |    6727.03 |   143   |
|   |   | Burundi                                  | 9U     |  AF  |    6872.83 |   144   |
|   |   | Kenya                                    | 5Z     |  AF  |    6886.82 |   133   |
|   |   | Somalia                                  | T5     |  AF  |    7091.07 |   125   |
|   |   | Tanzania                                 | 5H     |  AF  |    7309.28 |   140   |
|   |   | Zambia                                   | 9J     |  AF  |    7923.72 |   151   |
|   |   | Lakshadweep Islands                      | VU7    |  AS  |    7958.24 |   095   |
|   |   | Malawi                                   | 7Q     |  AF  |    8163.10 |   144   |
|   |   | Seychelles                               | S7     |  AF  |    8304.59 |   119   |
|   |   | Comoros                                  | D6     |  AF  |    8330.53 |   134   |
|   |   | Zimbabwe                                 | Z2     |  AF  |    8467.30 |   148   |
| ! |   | Glorioso Islands                         | FR/g   |  AF  |    8519.70 |   130   |
|   |   | Mayotte                                  | FH     |  AF  |    8544.64 |   133   |
|   |   | Maldives                                 | 8Q     |  AS  |    8629.52 |   099   |
|   |   | Mozambique                               | C9     |  AF  |    8641.89 |   144   |
| ! |   | Juan de Nova Europa                      | FR/j   |  AF  |    8846.09 |   137   |
| ! |   | Agalega & St. Brandon                    | 3B6    |  AF  |    8921.50 |   122   |
|   |   | Madagascar                               | 5R     |  AF  |    9220.73 |   134   |
| ! |   | Tromelin Island                          | FR/t   |  AF  |    9319.42 |   126   |
|   |   | Swaziland                                | 3DA    |  AF  |    9390.97 |   150   |
|   |   | Chagos Islands                           | VQ9    |  AF  |    9592.71 |   107   |
|   |   | Reunion Island                           | FR     |  AF  |    9874.38 |   128   |
|   |   | Mauritius                                | 3B8    |  AF  |    9908.62 |   126   |
|   |   | Rodriguez Island                         | 3B9    |  AF  |   10177.57 |   121   |
| ! |   | Crozet Island                            | FT5W   |  AF  |   12130.33 |   144   |
|   |   | Amsterdam & St. Paul Is.                 | FT5Z   |  AF  |   12611.24 |   122   |
| ! |   | Kerguelen Islands                        | FT5X   |  AF  |   13124.68 |   135   |
| ! |   | Heard Island                             | VK0H   |  AF  |   13659.27 |   136   |
| ! |   | Macquarie Island                         | VK0M   |  OC  |   18709.62 |   106   |
|---|---|------------------------------------------|--------|------|------------|---------|

NOTE: a '*' in the C column indicates a DXCC entity among the 150 closest ones (up to 6500 km)
NOTE: a '!' in the R column indicates a top-50 rare DXCC entity.
DXCC entities breakdown per heading
|---------|------------|---------|------|
| Heading | Total DXCC | Closest | Rare |
|---------|------------|---------|------|
|   195   |    045     |   031   | 008  |
|   290   |    044     |   010   | 009  |
|   049   |    059     |   017   | 010  |
|   122   |    091     |   061   | 013  |
|---------|------------|---------|------|
Total DXCC countries reachable   : 239
Total DXCC in closest countries  : 119
Total rare DXCC reachable        : 040
Total continent(s) reachable     :   6 [EU, AF, SA, NA, OC, AS]

```
Setup evaluation

```
$ ./dcxxplanner.jar -evaluate -center G -headings 280,110 -beamwidth 51
Current settings:
-----------------
Central DXCC entity       : G (England)
Beamings to evaluate      : [280, 110]
Antenna beamwidth to use  : 51
Maximum distance to assume 'close DXCC': 6500 Km
Maximum rare DXCC entities to use      : 50

Main 2 headings to use for optimal DXCC entities coverage
Heading 001 at 280 degrees
Heading 002 at 110 degrees

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
|   | * | United States (East Coast)               | K      |  NA  |    5761.92 |   286   |
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
|   |   | United States (Mid-USA)                  | K      |  NA  |    6841.75 |   298   |
|   |   | Bahamas                                  | C6     |  NA  |    6860.22 |   273   |
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
| ! |   | San Andres & Providencia                 | HK0/a  |  NA  |    8245.76 |   269   |
|   |   | Belize                                   | V3     |  NA  |    8327.48 |   278   |
|   |   | Honduras                                 | HR     |  NA  |    8387.04 |   275   |
|   |   | Colombia                                 | HK     |  SA  |    8395.53 |   258   |
|   |   | Nicaragua                                | YN     |  NA  |    8440.90 |   272   |
|   |   | Panama                                   | HP     |  NA  |    8441.21 |   266   |
|   |   | Guatemala                                | TG     |  NA  |    8563.76 |   278   |
|   |   | El Salvador                              | YS     |  NA  |    8607.94 |   276   |
|   |   | Costa Rica                               | TI     |  NA  |    8622.29 |   270   |
|   |   | Mexico                                   | XE     |  NA  |    8700.96 |   289   |
|   |   | Malpelo Island                           | HK0/m  |  SA  |    8990.69 |   264   |
| ! |   | Cocos Island                             | TI9    |  NA  |    9221.92 |   269   |
|   |   | Ecuador                                  | HC     |  SA  |    9258.34 |   258   |
| ! |   | Revillagigedo                            | XF4    |  NA  |    9593.27 |   296   |
|   |   | Galapagos Islands                        | HC8    |  SA  |   10047.00 |   269   |
|   |   | Clipperton Island                        | FO/c   |  NA  |   10258.60 |   290   |
|   |   | Easter Island                            | CE0Y   |  SA  |   13553.26 |   266   |
| ! |   | Ducie Island                             | VP6/d  |  OC  |   14388.61 |   280   |
|   |   | Pitcairn Island                          | VP6    |  OC  |   14590.14 |   284   |
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
|   | * | San Marino                               | T7     |  EU  |    1416.56 |   128   |
|   | * | Croatia                                  | 9A     |  EU  |    1481.04 |   118   |
|   | * | Italy                                    | I      |  EU  |    1519.76 |   131   |
|   | * | Slovak Republic                          | OM     |  EU  |    1556.80 |   097   |
|   | * | Sov Mil Order of Malta                   | 1A     |  EU  |    1593.83 |   133   |
|   | * | Vatican City                             | HV     |  EU  |    1595.77 |   133   |
|   | * | Hungary                                  | HA     |  EU  |    1604.93 |   104   |
|   | * | Bosnia-Herzegovina                       | E7     |  EU  |    1679.27 |   116   |
| ! | * | Serbia                                   | YU     |  EU  |    1912.80 |   111   |
|   | * | Montenegro                               | 4O     |  EU  |    1917.58 |   118   |
|   | * | Romania                                  | YO     |  EU  |    2037.14 |   101   |
|   | * | Albania                                  | ZA     |  EU  |    2077.77 |   120   |
|   | * | Macedonia                                | Z3     |  EU  |    2128.28 |   116   |
|   | * | Ukraine                                  | UR     |  EU  |    2187.52 |   085   |
|   | * | Bulgaria                                 | LZ     |  EU  |    2251.57 |   108   |
|   | * | Moldova                                  | ER     |  EU  |    2256.16 |   094   |
|   | * | Greece                                   | SV     |  EU  |    2279.89 |   119   |
| ! | * | Mount Athos                              | SV/a   |  EU  |    2394.11 |   116   |
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
| ! |   | Glorioso Islands                         | FR/g   |  AF  |    8519.70 |   130   |
|   |   | Mayotte                                  | FH     |  AF  |    8544.64 |   133   |
|   |   | Maldives                                 | 8Q     |  AS  |    8629.52 |   099   |
|   |   | Sri Lanka                                | 4S     |  AS  |    8809.08 |   091   |
| ! |   | Agalega & St. Brandon                    | 3B6    |  AF  |    8921.50 |   122   |
|   |   | Madagascar                               | 5R     |  AF  |    9220.73 |   134   |
| ! |   | Tromelin Island                          | FR/t   |  AF  |    9319.42 |   126   |
|   |   | Chagos Islands                           | VQ9    |  AF  |    9592.71 |   107   |
|   |   | Reunion Island                           | FR     |  AF  |    9874.38 |   128   |
|   |   | Mauritius                                | 3B8    |  AF  |    9908.62 |   126   |
|   |   | Rodriguez Island                         | 3B9    |  AF  |   10177.57 |   121   |
|   |   | Cocos (Keeling) Islands                  | VK9C   |  OC  |   11636.19 |   090   |
|   |   | Amsterdam & St. Paul Is.                 | FT5Z   |  AF  |   12611.24 |   122   |
| ! |   | Macquarie Island                         | VK0M   |  OC  |   18709.62 |   106   |
|---|---|------------------------------------------|--------|------|------------|---------|

NOTE: a '*' in the C column indicates a DXCC entity among the 150 closest ones (up to 6500 km)
NOTE: a '!' in the R column indicates a top-50 rare DXCC entity.
DXCC entities breakdown per heading
|---------|------------|---------|------|
| Heading | Total DXCC | Closest | Rare |
|---------|------------|---------|------|
|   280   |    055     |   009   | 010  |
|   110   |    073     |   054   | 009  |
|---------|------------|---------|------|
Total DXCC countries reachable   : 128
Total DXCC in closest countries  : 063
Total rare DXCC reachable        : 019
Total continent(s) reachable     :   6 [EU, NA, SA, OC, AS, AF]

```




