package com.tw.spark;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.File;

public class AirportsByLatitudeProblem {
//    private static Logger logger = Logger.getLogger(AirportsByLatitudeProblem.class);

    public static void main(String[] args) throws Exception {

        /* Create a Spark program to read the airport data from in/airports.text,  find all the airports whose latitude are bigger than 40.
           Then output the airport's name and the airport's latitude to out/airports_by_latitude.text.

           Each row of the input file contains the following columns:
           Airport ID, Name of airport, Main city served by airport, Country where airport is located, IATA/FAA code,
           ICAO Code, Latitude, Longitude, Altitude, Timezone, DST, Timezone in Olson format

           Sample output:
           "St Anthony", 51.391944
           "Tofino", 49.082222
           ...
         */

        SparkConf conf = new SparkConf().setAppName("airportByLatitudeProblem").setMaster("local[3]");
        JavaSparkContext sc = new JavaSparkContext(conf);
        JavaRDD<String> lines = sc.textFile(args[0]);
        JavaRDD<String> filteredLines = lines.filter(line -> {
            String sixthElement = line.split(",")[6];
//            logger.debug("The sixth element is "+ sixthElement);
            return Float.parseFloat(sixthElement) > 40;
        });
        JavaRDD<String> outputLines = filteredLines.map(line -> {
            String[] lineSplits = line.split(",");
            return String.join(",",lineSplits[1], lineSplits[6]);
        });

//        FileUtils.deleteQuietly(new File("out/airports_by_latitude.text"));
        outputLines.saveAsTextFile(args[1]);
    }
}
