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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Clustering {

    // helper methods for finding clusters based on the beaming of each DXCC (relative to our central location)
    public static List<Integer> findHeadingClusters(List<Integer> headings, int desiredClusters) {
        //
        // intialise centroids
        //
        // create randomly _numberOfBeamings cluster as a start
        Random r = new Random();

        List<Integer> initialCentroids = new ArrayList<Integer>(desiredClusters);

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

    private static int[] findClosestCentroids(List<Integer> centroids, List<Integer> headings) {
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


    private static List<Integer> computeCentroids(int[] clusterIndexes, int desiredClusters, List<Integer> headings) {
        List<Integer> updatedCentroids = new ArrayList<Integer>(desiredClusters);

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
