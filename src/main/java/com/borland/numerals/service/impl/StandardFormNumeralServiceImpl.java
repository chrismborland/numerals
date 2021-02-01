package com.borland.numerals.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.borland.numerals.App;
import com.borland.numerals.service.NumeralPair;
import com.borland.numerals.service.NumeralService;
import com.codahale.metrics.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service is used to convert numbers to the Roman numeral equivalent using the
 * standard form.
 * 
 * @author chrismborland
 */
public class StandardFormNumeralServiceImpl implements NumeralService {

    public static final Logger LOG = LoggerFactory.getLogger(StandardFormNumeralServiceImpl.class);

    // metrics
    private final Timer singleResponses = App.metrics.timer(App.METRIX_PREFIX + ".singleConversions");
    private final Timer multipleResponses = App.metrics.timer(App.METRIX_PREFIX + ".rangeConversions");

    // thread pool size for handling range requests.
    private static int THREAD_POOL_SIZE = 10;

    // standard form numeral service boundaries.
    public static final int MIN_NUMERAL = 1;
    public static final int MAX_NUMERAL = 3999;

    @Override
    public NumeralPair convertToNumeral(final int number) {
        // start timer.
        final Timer.Context timer = singleResponses.time();
        NumeralPair pair = null;
        ExecutorService executorService = null;
        try {
            executorService = Executors.newSingleThreadExecutor();
            Future<NumeralPair> future = executorService.submit(new StandardFormNumeralCallable(number));
            pair = future.get();
        } catch (InterruptedException | ExecutionException e) {
            LOG.warn("Failed to get execution result.", e);
        } finally {
            if (executorService != null) {
                executorService.shutdown();
            }
        }
        // stop timer.
        timer.stop();
        return pair;
    }

    @Override
    public Set<NumeralPair> convertToNumeral(final int lowNumber, final int highNumber) {
        // start timer.
        final Timer.Context timer = multipleResponses.time();
        Set<NumeralPair> pairs = new TreeSet<NumeralPair>();
        ExecutorService executorService = null;
        final List<Callable<NumeralPair>> callables = new ArrayList<Callable<NumeralPair>>();
        try {
            // get executor service with configured thread pool size.
            executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
            // add conversion tasks for all numbers in our range. 
            for (int i = lowNumber; i <= highNumber; i++) {
                callables.add(new StandardFormNumeralCallable(i));
            }
            // invoke conversion tasks.  gaurantees all tasks have executed.
            for (Future<NumeralPair> future : executorService.invokeAll(callables)) {
                // add numeral pair to collection.
                pairs.add(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            LOG.warn("Failed to get execution result.", e);
            // clear out the pairs, so we know a problem occurred.
            pairs = null;
        } finally {
            if (executorService != null) {
                executorService.shutdown();
            }
        }
        // stop timer.
        timer.stop();
        return pairs;
    }

    @Override
    public boolean isEligible(final int number) {
        return number >= MIN_NUMERAL && number <= MAX_NUMERAL;
    }

    @Override
    public boolean isRangeEligible(final int lowNumber, final int highNumber) {
        return isEligible(lowNumber) && isEligible(highNumber) && lowNumber < highNumber;
    }

}