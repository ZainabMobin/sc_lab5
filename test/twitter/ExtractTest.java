/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Test;
public class ExtractTest {
    
    private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
    private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
    
    private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
    private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
    
    @Test(expected=AssertionError.class)
    public void testAssertionsEnabled() {
        assert false; // make sure assertions are enabled with VM argument: -ea
    }
    
    @Test
    public void testGetTimespanTwoTweets() {
        Timespan timespan = Extract.getTimespan(Arrays.asList(tweet1, tweet2));
        
        assertEquals("expected start", d1, timespan.getStart());
        assertEquals("expected end", d2, timespan.getEnd());
    }
    
    @Test
    public void testGetMentionedUsersNoMention() {
        Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet1));
        
        assertTrue("expected empty set", mentionedUsers.isEmpty());
    }
    
//    get the timestamp from tweet
//    check all tweets which has the oldest adn the latest one, 
// check for random edge cases, stuff that kills time like improper formatting
// sting of connected tweets implied by the timespan of the random tweets is here.
//
    
//    possible partitions
//    just one tweet in which its not a time span, rather a localized point with start = end
// 
    @Test
    public void testGetTimespan() {
    	
//    	single timestamp, tweet1 uses d1
    	List<Tweet> tweets1 = Arrays.asList(tweet1);
    	assertEquals("one tweet", new Timespan(d1, d1) , Extract.getTimespan(tweets1));

//    	array of tweets (happy path)
    	Instant d4 = Instant.parse("1992-11-18T02:00:00Z");
      	Instant d5 = Instant.parse("2065-02-08T10:00:00Z");
      	Tweet tweet4 = new Tweet(23, "oldiegoldie", "This is a historic tweet", d4);
      	Tweet tweet5 = new Tweet(768, "zoomer", "[Unintelligible slang from the future]", d5);
    	List<Tweet> tweets3 = Arrays.asList(tweet1, tweet2, tweet4, tweet5);
    	assertEquals("one tweet", new Timespan(d4, d5) , Extract.getTimespan(tweets3));
    }

//    get the username from the list of users that come along with the tweets
   
//	  possible partitions:
//    	edge cases: such as author being empty strings, 
//    	no tweets provided at all (null obj) not catered since its not mentioned in the precondition, 
//    doing so would make it stronger and thereby weaken the specification. 
//    	redundant cases: the username being case insensitive demands the function keeps the casing uniform
    @Test
    public void testGetMentionedUsers() {
//    	add tweets with redundant auhtors
    	Tweet tweet3 = new Tweet(43, "alysSA", "some random things", d1);
    	List<Tweet> tweet_test1 = Arrays.asList(tweet1, tweet2, tweet3);
    	assertEquals("case insensitivity", Set.of("alyssa", "bbitdiddle"), Extract.getMentionedUsers(tweet_test1));  	
    	
//    	add tweet with empty username
    	Tweet tweet4 = new Tweet(234, "", "nothing to see here", d2);
    	List<Tweet> tweet_test2 = Arrays.asList(tweet1, tweet2, tweet4);
    	assertEquals("Empty names", Set.of("alyssa", "bbitdiddle"), Extract.getMentionedUsers(tweet_test2));

//    	tweet with username having @ in between
    	Tweet tweet5 = new Tweet(234, "kan@ki", "is someting going on?", d1);
    	List<Tweet> tweet_test3 = Arrays.asList(tweet1, tweet2, tweet5);
    	assertEquals("@ mention ignored", Set.of("alyssa", "bbitdiddle"), Extract.getMentionedUsers(tweet_test3));    	    	
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * Extract class that follows the spec. It will be run against several staff
     * implementations of Extract, which will be done by overwriting
     * (temporarily) your version of Extract with the staff's version.
     * DO NOT strengthen the spec of Extract or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in Extract, because that means you're testing a
     * stronger spec than Extract says. If you need such helper methods, define
     * them in a different class. If you only need them in this test class, then
     * keep them in this test class.
     */

}
