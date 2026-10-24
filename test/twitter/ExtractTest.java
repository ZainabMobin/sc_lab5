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
    private static final Tweet tweet3 = new Tweet(3, "alyssa", "empty mention is here! @", d1); //empty mention
    private static final Tweet tweet4 = new Tweet(4, "sassaki", "@Alyssa could you go over our schedule with @Yukio and @Rin? ", d2); //multiple different users mentioned
    private static final Tweet tweet5 = new Tweet(8, "yukio", "Pipe down @Rin", d1); //Single user mentioned
    private static final Tweet tweet6 = new Tweet(6, "yukio", "@alyssa@mit.com how's life in the big leagues? Does it live to your expectations?", d1); //wrong format for mention
    private static final Tweet tweet7 = new Tweet(7, "rin", "@yukio I'm using this network too. @YUKIO MY CAPS IS BROKEN. @YukIO!! ", d1); //rendundant mention of the same user
    private static final Tweet tweet8 = new Tweet(8, "kaneki", "An array of good and bad usernames like @Good_One55, @1337-mAn, and not so good ones too like $$himmy, SuperNova**99, ~OldName", d2);

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
	//  get the username from the list of users that come along with the tweets
	    
	//	  possible partitions:
	//  	edge cases: such as empty mention '@' only
	//	no mention, 1 mention, more than 1 mention of different users, more than 1 mention of the same user (redundant)
	    
	//  	no tweets provided at all (null obj) not catered since its not mentioned in the precondition, doing so would make it stronger and thereby weaken the specification. 
	//  	redundant cases: the username being case insensitive demands the function keeps the casing uniform
    
	//empty mention, floating '@' character
	@Test
	public void testGetMentionedUsersEmptyMention() {
	    
		Set<String> mentionedUsers = Extract.getMentionedUsers(Arrays.asList(tweet3));
	    
	    assertEquals("No username returned", Set.of(), mentionedUsers);
	}

	// 1 mention and casing
	@Test
	public void testGetMentionedUsersOneMentionAndCasing() {
		Set<String> result = Extract.getMentionedUsers(Arrays.asList(tweet5));
		assertEquals("Extract single username with case insensitivity lowercase", Set.of("rin"), result); //case insensitive, lowercase it
	}
	
	// redundant mentions of the same user and multiple different users  as well
	@Test
	public void testGetMentionedUsersMultipleAndRedundantUsersAndTrailingPuncutation() {
		List<Tweet> tweet_test = Arrays.asList(tweet4, tweet7);
		Set<String> result = Extract.getMentionedUsers(tweet_test);
		assertEquals("Extract multiple usernames for multiple mentions, one for redundant mentions and strips trailing punctuation marks", 
				Set.of("yukio", "rin", "alyssa"), result);
	}
	
	@Test
	public void testGetMentionedUsersWrongMentionFormat() {
		Set<String> result = Extract.getMentionedUsers(Arrays.asList(tweet6));
		assertEquals("Expect empty set for wrong mention format", Set.of(), result);
	}
	
	@Test
	public void testGetMentionedUsersNameLegality() {
		Set<String> result = Extract.getMentionedUsers(Arrays.asList(tweet8));
		assertEquals("Extract Legal names and remove illegal ones", Set.of("good_one55", "1337-man"), result);
	    
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
