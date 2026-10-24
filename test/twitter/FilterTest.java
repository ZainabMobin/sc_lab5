/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class FilterTest {

    /*
     * TODO: your testing strategies for these methods should go here.
     * See the ic03-testing exercise for examples of what a testing strategy comment looks like.
     * Make sure you have partitions.
     */
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2018-02-17T11:00:00Z");

    private static final Instant d3 = Instant.parse("2019-05-29T10:00:00Z");
    private static final Instant d4 = Instant.parse("2025-01-13T11:00:00Z");
    private static final Instant d5 = Instant.parse("2026-06-19T11:00:00Z");
    private static final Instant d6 = Instant.parse("2013-02-17T09:00:00Z");    
	
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    private static final Tweet tweet3 = new Tweet(1, "alyssa", "Going to a hiking trip this Saturday!", d3);
    private static final Tweet tweet4 = new Tweet(2, "bbitdiddle", "Can't wait for the next SEECS event!", d4);
    private static final Tweet tweet5 = new Tweet(3, "Courage", "The things I do for love", d2);
    private static final Tweet tweet6 = new Tweet(45, "ancient", "I am an ancient tweet", d6);
    
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testWrittenByMultipleTweetsSingleResult() {
        List<Tweet> writtenBy = Filter.writtenBy(Arrays.asList(tweet1, tweet2), "alyssa");
        
        assertEquals("expected singleton list", 1, writtenBy.size());
        assertTrue("expected list to contain tweet", writtenBy.contains(tweet1));
    }
    
    @Test
    public void testInTimespanMultipleTweetsMultipleResults() {
        Instant testStart = Instant.parse("2016-02-17T09:00:00Z");
        Instant testEnd = Instant.parse("2016-02-17T12:00:00Z");
        
        List<Tweet> inTimespan = Filter.inTimespan(Arrays.asList(tweet1, tweet2), new Timespan(testStart, testEnd));
        
        assertFalse("expected non-empty list", inTimespan.isEmpty());
        assertTrue("expected list to contain tweets", inTimespan.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, inTimespan.indexOf(tweet1));
    }
    
//    partitions:
//    case insensitive: words matching the description must be checked regardless of case
//    order of tweets is retained
//    No matches: expect empty list
//    1 word matches
//	  More than 1 word matches
//	  1 tweet matched
//	  More than 1 tweet matches
//    Passing empty word list
//    passing empty tweet list 
//    both word and tweet list is empty

    @Test
    public void testContaining() {
        List<Tweet> containing = Filter.containing(Arrays.asList(tweet1, tweet2), Arrays.asList("talk"));
        
        assertFalse("expected non-empty list", containing.isEmpty());
        assertTrue("expected list to contain tweets", containing.containsAll(Arrays.asList(tweet1, tweet2)));
        assertEquals("expected same order", 0, containing.indexOf(tweet1));
        
        // 1 word, More than 1 word matches, 1 tweet, More than 1 tweet matches, 
		// case sensitivity and order retention are also checked
        List<Tweet> tweets1 = Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5, tweet6);
        List<String> strings1 = Arrays.asList("LOVE", "riVeSt", "seeCs", "hIKiNG");
        assertEquals("Case insensitive, word matching and order retention expected", 
        		Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5), 
        		Filter.containing(tweets1, strings1));
        
        // no matches found
        List<String> strings2 = Arrays.asList("Courage", "roads", "suite", "auditory");
        assertEquals("Expected empty list with both empty input parameters", Arrays.asList(), 
        		Filter.containing(tweets1, strings2));
      
//        Passing empty word list, empty tweet list and both
        assertEquals("Expected empty list with both empty input parameters", Arrays.asList(), Arrays.asList());
    }

    
    // partition:
//    more than 1 match, also checks for order retention
//    	1 match: one tweet lies within the timespan
//    not matched: timespan before/after the list of tweets
//    timespan start, end dates lies exactly on tweet instant
//    timespan is a point fixed date instead of a range
    @Test
    public void testInTimespan() {

    	// matches are greater than 1 for order retention
    	// also tweet1 is equal to start point
        Timespan timespan1 = new Timespan(d1, d5);
        List<Tweet> tweets1 = Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5, tweet6);
    	assertEquals("more than 1 match", Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5), Filter.inTimespan(tweets1, timespan1));
    	
    	// no tweets matched, return empty
    	Timespan timespan2 = new Timespan(d1, d3);
    	List<Tweet> tweets2 = Arrays.asList(tweet4, tweet6);
    	assertEquals("No match", Arrays.asList(), Filter.inTimespan(tweets2, timespan2));
        
    	// tweet time matches with exactly with start time, end time and both times (singular point timespan)
    	Timespan timespan3 = new Timespan(d2, d2);
    	List<Tweet> tweets3 = Arrays.asList(tweet3, tweet5, tweet1, tweet2, tweet4, tweet6);
    	assertEquals("Exact match on start, end, both", Arrays.asList(tweet5, tweet2), Filter.inTimespan(tweets3, timespan3));
    }
    
//    precondition states username is required to be legal as defined by specs, 
//    we do not divulge in checking the legality of the names then
//    also not concerned about null values passed as parameters since already defined in precondition
//    partition strat:
//    edge case: one tweet, absent username
//    retain order: many tweets, present username for at least 2 of them to check 
//    		order is preserved (covers 1 and >1 tweets with username)
    @Test
    public void testWrittenBy() {
    	
        List<Tweet> tweets1 = Arrays.asList(tweet1, tweet2, tweet3, tweet4, tweet5);
        assertEquals("at least 2 target tweets for order retention", 
        		Arrays.asList(tweet1, tweet3), Filter.writtenBy(tweets1, "alyssa"));
    	
        //empty list for no user found
        List<Tweet> tweets2 = Arrays.asList(tweet1, tweet3, tweet5);
        assertEquals("Tweet list without target user", 
        		Arrays.asList(), Filter.writtenBy(tweets2, "bbitdiddle"));
    }
    /*
     * Warning: all the tests you write here must be runnable against any Filter
     * class that follows the spec. It will be run against several staff
     * implementations of Filter, which will be done by overwriting
     * (temporarily) your version of Filter with the staff's version.
     * DO NOT strengthen the spec of Filter or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Filter, because that means you're testing a stronger
     * spec than Filter says. If you need such helper methods, define them in a
     * different class. If you only need them in this test class, then keep them
     * in this test class.
     */

}
