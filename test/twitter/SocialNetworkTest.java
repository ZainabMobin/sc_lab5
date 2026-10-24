/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

public class SocialNetworkTest {
	
	@Test(expected=AssertionError.class)
	public void testAssertionsEnabled() {
		assert false; // make sure assertions are enabled with VM argument: -ea
	}

	private static final Instant d1 = Instant.parse("2016-02-17T10:00:00Z");
	private static final Instant d2 = Instant.parse("2016-02-17T11:00:00Z");
	
	private static final Tweet tweet1 = new Tweet(1, "alyssa", "is it reasonable to talk about rivest so much?", d1);
	private static final Tweet tweet2 = new Tweet(2, "bbitdiddle", "rivest talk in 30 minutes #hype", d2);
	private static final Tweet tweet3 = new Tweet(3, "alyssa", "empty mention is here! @@ @", d1); //empty mention
	private static final Tweet tweet4 = new Tweet(4, "sassaki", "@Alyssa could you go over our schedule with @Yukio and @Rin? ", d2); //multiple different users mentioned
	private static final Tweet tweet5 = new Tweet(8, "yukio", "Pipe down @Rin", d1); //Single user mentioned
	private static final Tweet tweet6 = new Tweet(12, "yukio", "@alyssa@mit.com how's life in the big leagues? Does it live to your expectations?", d1); //wrong format for mention
	private static final Tweet tweet7 = new Tweet(7, "rin", "@yukio I'm using this network too. @YUKIO MY CAPS IS BROKEN. @YukIO!! ", d1); //rendundant mention of the same user
	private static final Tweet tweet8 = new Tweet(8, "kaneki", "An array of good usernames like @kAnEkI equivalent to @Kaneki, and not so good ones like $$himmy, SuperNova**99, ~OldName", d2); //self mention
	private static final Tweet tweet9 = new Tweet(9, "bbitdiddle", "totally resonable, init @kaneki?", d2);
	private static final Tweet tweet10 = new Tweet(10, "rin", "Im mentioning multiple people like @sassaki @kaneki @yukio", d2);
	private static final Tweet tweet11 = new Tweet(11, "rin", "@kaneki again a mention in another tweet of mine @sassaki", d2);
    

	
    /*
     * Testing strategy for guessFoloowsGraph(tweets):
     *  
     *  number of tweets: 0, 1, >1
     *  mentions in one tweet: 0, 1, >1 (different, redundant mentions)
     *  tweets by the same author: 1, >1 (different, redundant mentions)
     *  case of mentioned username
     *  
     * guessFollowsGraph() is the client that calls getMentionedUsers(), it tests edge cases for usernames tested for getMentionedUsers() as well since it consumes the results
     *  Edge Cases:
     *  self-mention: 1, >1
     *  empty mention '@': 1, >1
     *  illegal mention: '@' in the middle
     *  illegal naming: anything except [a-zA-Z0-9_-]+
     *  
     *  
     *  
     * Testing strategy for influencers(followsGraph):
     * 
     * graph size: 0, 1, >1
     * follower number: 0, 1, >1
     * tied number of followers : author names sorted alphabetically (makes function deterministic)
     * Follower descending order: from famous to no-names
     * 
     * Edge Case: self mention, empty mention
     * 
     */
    
    // covers: 0 tweets
    @Test
    public void testGuessFollowsGraphEmpty() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(new ArrayList<>());    
        assertTrue("Expected empty graph for no tweets", followsGraph.isEmpty());
    }
    // covers: >1 tweets, 0 mention, different authors
    @Test
    public void testGuessFollowsGraphNoMention() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1, tweet2));
        // Check key follows no-one, regardless of whether key itelf exists
        assertTrue("Expected empty Followed list for no mentions", 
        		followsGraph.values().stream().allMatch(Set::isEmpty));
    }
    // covers: 1 tweet, >1 self-mention, case different, illegal names
    @Test
    public void testGuessFollowGraphsSelfMentionIllegalNames() {
    	Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet8));
    	assertTrue("Expected empty Followed list for self mention ", 
    			followsGraph.values().stream().allMatch(Set::isEmpty));
    }
    // covers: 1 tweet, 1 mention, different case
    @Test
    public void testGuessFollowsGraphSingleMention() {
    	Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet5));
    	
    	assertEquals("Expected graph with single entry", Map.of("yukio", Set.of("rin")), followsGraph);
    	assertTrue("Expected graph with size 1 for single tweet and single mention", followsGraph.size()==1);
    }
    // covers: >1 tweets, >1 mention, different authors, different case
    @Test
    public void testGuessFollowsGraphSingleMentionsManyTweets() {
    	Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet4, tweet9, tweet10));
    	
    	assertEquals("Expected graph for multiple mentions",
    			Map.of(
	    			"bbitdiddle", Set.of("kaneki"), 
	    			"sassaki", Set.of("alyssa", "yukio", "rin"),
	    			"rin", Set.of("yukio", "sassaki", "kaneki")
    			), followsGraph);
    	assertTrue("Expected graph with size 3 for three different author's tweets", followsGraph.size()==3);
    }
    // covers: >1 tweets, >1 redundant mention, same authors, different case, trailing punctuation
    @Test
    public void testGuessFollowsGraphRedundantMentionsManyTweetsTrailingPunctuation() {
    	Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet7, tweet10, tweet11));
    	
    	assertEquals("Expected graph for multiple & redundant mentions in many tweets with stray punctuation", 
    			Map.of(
					"rin", Set.of("sassaki", "kaneki", "yukio")
				), followsGraph);
    	assertTrue("Expected graph with size 1 for 3 tweets from the same person", followsGraph.size()==1);
    }
    // covers: >1 tweets, no mention, different authors, empty mention, illegal mention
    @Test
    public void testGuessFollowsGraphEmptyMentionIllegalMention() {
    	Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet3, tweet6));
    	assertTrue("Expected empty followed list for Empty and Illegal Mentions", 
    			followsGraph.values().stream().allMatch(Set::isEmpty));
    }
    
    // cover graph size 0
    @Test
    public void testInfluencersEmpty() {
        Map<String, Set<String>> followsGraph = new HashMap<>();
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("Expected empty list", influencers.isEmpty());
    }
    
    // cover 0 followers, graph size 1, single author, no mention
    @Test
    public void testInfluencersNoFollower() {
    	Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet1));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("Expected one person with zero followers for no mention", List.of("alyssa"), influencers);
    }
    // cover 0 followers, self mention, graph size 1, case insensitive
    @Test
    public void testInfluencersSelfMention() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet8));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("Expected one person with zero followers for self-mention", List.of("kaneki"), influencers);
    }
    // cover 0 followers, empty mention, graph size 1
    @Test
    public void testInfluencersEmptyMention() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet3));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertEquals("Expected one person with zero followers for empty mention", List.of("alyssa"), influencers);
    }
    // cover 1 follower, graph size 1, descending order wrt followers
    @Test
    public void testInfluencersOneFollowerOrder() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet7));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
       
        assertEquals("Expected list of one person with 1 follower and one person with 0 followers", 
        		List.of("yukio", "rin"), influencers);
        assertTrue("Expected list of size 2", influencers.size()==2);
        assertEquals("Expected first person with 1 follower at start of list", "yukio", influencers.get(0));
        assertEquals("Expected second person with 0 followers at end of list", "rin", influencers.get(1));  
    }
    // cover >1 follower, graph size >1, follower order, alphabetical order for tied with 0 followers, 
    @Test
    public void testInfluencersMoreFollowersAlphabeticalOrder() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet9, tweet11));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("Expected list with size 4", influencers.size() == 4);
        assertEquals("Expected follower order", Arrays.asList("kaneki", "sassaki", "bbitdiddle", "rin"), influencers);
        assertEquals("Expected alphabetical ordering for tied 0 follower: B first", "bbitdiddle", influencers.get(2));
        assertEquals("Expected alphabetical ordering for tied 0 follower: R next", "rin", influencers.get(3));
    }
	 // cover >1 follower, tied follower list alphabetical ordering, graph size >1
    @Test
    public void testInfluencersTiedFollowers() {
        Map<String, Set<String>> followsGraph = SocialNetwork.guessFollowsGraph(Arrays.asList(tweet4, tweet9, tweet10, tweet11));
        List<String> influencers = SocialNetwork.influencers(followsGraph);
        
        assertTrue("Expected list with size 6", influencers.size() == 6);
        assertEquals("Expected follower order", Arrays.asList("kaneki", "yukio", "alyssa", "rin", "sassaki", "bbitdiddle"), influencers);
        assertEquals("Expected alphabetical ordering for tied 2 followers: K first", "kaneki", influencers.get(0));
        assertEquals("Expected alphabetical ordering for tied 2 followers: Y next", "yukio", influencers.get(1));
        assertEquals("Expected alphabetical ordering for tied 1 follower: A first", "alyssa", influencers.get(2));
        assertEquals("Expected alphabetical ordering for tied 1 follower: R next", "rin", influencers.get(3));
        assertEquals("Expected alphabetical ordering for tied 1 follower: S last", "sassaki", influencers.get(4));
    }

    /*
     * Warning: all the tests you write here must be runnable against any
     * SocialNetwork class that follows the spec. It will be run against several
     * staff implementations of SocialNetwork, which will be done by overwriting
     * (temporarily) your version of SocialNetwork with the staff's version.
     * DO NOT strengthen the spec of SocialNetwork or its methods.
     * 
     * In particular, your test cases must not call helper methods of your own
     * that you have put in SocialNetwork, because that means you're testing a
     * stronger spec than SocialNetwork says. If you need such helper methods,
     * define them in a different class. If you only need them in this test
     * class, then keep them in this test class.
     */

}
