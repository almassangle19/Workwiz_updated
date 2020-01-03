package com.example.workwiz.util;

import android.content.Context;

import com.example.workwiz.Job;
import com.example.workwiz.R;

import java.util.Arrays;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Utilities for Jobs.
 */
public class JobUtil {

    private static final String TAG = "JobUtil";

    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 4, 60,
            TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());

    private static final int MAX_IMAGE_NUM = 22;

    private static final String[] NAME_FIRST_WORDS = {
            "Alpha",
            "Beta",
            "Gamma",
            "Zeta",
            "Peta",
            "Pico",
    };

    private static final String[] NAME_SECOND_WORDS = {
            "One",
            "Nine",
            "Three",
            "Four",
            "Five",
            "Eight",
            "Ten",
    };


    /**
     * Create a random Job POJO.
     */
    public static Job getRandom(Context context) {
        Job job = new Job();
        Random random = new Random();

        // Cities (first element is 'Any')
        String[] cities = context.getResources().getStringArray(R.array.cities);
        cities = Arrays.copyOfRange(cities, 1, cities.length);

        // Categories (first element is 'Any')
        String[] categories = context.getResources().getStringArray(R.array.categories);
        categories = Arrays.copyOfRange(categories, 1, categories.length);

        int[] prices = new int[]{1, 2, 3};

        job.setName(getRandomName(random));
        job.setCity(getRandomString(cities, random));
        job.setCategory(getRandomString(categories, random));
        job.setPrice(getRandomInt(prices, random));
        job.setAvgRating(getRandomRating(random));
        job.setNumRatings(random.nextInt(20));

        return job;
    }



    /**
     * Get price represented as dollar signs.
     */
    public static String getPriceString(Job job) {
        return getPriceString(job.getPrice());
    }

    /**
     * Get price represented as dollar signs.
     */
    public static String getPriceString(int priceInt) {
        switch (priceInt) {
            case 1:
                return "$";
            case 2:
                return "$$";
            case 3:
            default:
                return "$$$";
        }
    }

    private static double getRandomRating(Random random) {
        double min = 1.0;
        return min + (random.nextDouble() * 4.0);
    }

    private static String getRandomName(Random random) {
        return getRandomString(NAME_FIRST_WORDS, random) + " "
                + getRandomString(NAME_SECOND_WORDS, random);
    }

    private static String getRandomString(String[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

    private static int getRandomInt(int[] array, Random random) {
        int ind = random.nextInt(array.length);
        return array[ind];
    }

}
