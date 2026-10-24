/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * SocialNetwork provides methods that operate on a social network.
 * 
 * A social network is represented by a Map<String, Set<String>> where map[A] is
 * the set of people that person A follows on Twitter, and all people are
 * represented by their Twitter usernames. Users can't follow themselves. If A
 * doesn't follow anybody, then map[A] may be the empty set, or A may not even exist
 * as a key in the map; this is true even if A is followed by other people in the network.
 * Twitter usernames are not case sensitive, so "ernie" is the same as "ERNie".
 * A username should appear at most once as a key in the map or in any given
 * map[A] set.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class SocialNetwork {

    /**
     * Guess who might follow whom, from evidence found in tweets.
     * 
     * @param tweets
     *            a list of tweets providing the evidence, not modified by this
     *            method.
     * @return a social network (as defined above) in which Ernie follows Bert
     *         if and only if there is evidence for it in the given list of
     *         tweets.
     *         One kind of evidence that Ernie follows Bert is if Ernie
     *         @-mentions Bert in a tweet. This must be implemented. Other kinds
     *         of evidence may be used at the implementor's discretion.
     *         All the Twitter usernames in the returned social network must be
     *         either authors or @-mentions in the list of tweets.
     */
    public static Map<String, Set<String>> guessFollowsGraph(List<Tweet> tweets) {
    	Map<String, Set<String>> graph = new HashMap<>();
    	
    	if (tweets.isEmpty()) return graph; //return empty graph
    	
    	for (Tweet t : tweets) {
    		String author = t.getAuthor();
    		Set<String> mentions = Extract.getMentionedUsers(Arrays.asList(t));
    		
    		//remove self-mention
    		if(mentions.contains(author))
    			mentions.remove(author);
    		
    		// add author with empty list FIRST (In previous implementation it was added 
    		// later after disqualifying author for no mentions)
    		if( ! graph.containsKey(author)) // if not already, create Hashset (mutable) with author's name
    			graph.put(author, new HashSet<>());
    		
    		//key-Empty list for this tweet 't'
    		if(mentions.isEmpty())	continue;
    		
    		graph.get(author).addAll(mentions);
    		
    	}
    	return graph;
    }

    /**
     * Find the people in a social network who have the greatest influence, in
     * the sense that they have the most followers.
     * 
     * @param followsGraph
     *            a social network (as defined above)
     * @return a list of all distinct Twitter usernames in followsGraph, in
     *         descending order of follower count.
     */
    public static List<String> influencers(Map<String, Set<String>> followsGraph) {
    	
    	List<String> influencers = new ArrayList<>();
    	HashMap<String, Integer> influencer_map = new HashMap<>();
    	
    	if (followsGraph.isEmpty()) return influencers;

    	for (Map.Entry<String, Set<String>> unit : followsGraph.entrySet()) {
    		String follower_key = unit.getKey();
    		if (! influencer_map.containsKey(follower_key))
    			influencer_map.put(follower_key, 0); //add follower in hashmap if not already
    		
    		for (String followed: followsGraph.get(follower_key)) {
    			if (! influencer_map.containsKey(followed)) {
    				influencer_map.put(followed, 0); // add followed if not already
    			}	
    			influencer_map.put(followed, influencer_map.get(followed) + 1); // increment followed for current follower_key 
    		}
    	}
    	
    	// convert to arraylist since hashmap is unordered
    	List<Map.Entry<String, Integer>> follower_list = new ArrayList<>(influencer_map.entrySet());
    	
    	follower_list.sort((e1, e2) -> { //sort influencers by numbers, then alphabetically to keep output deterministic
    		int count_comparator = e2.getValue().compareTo(e1.getValue()); 
    		//unequal values, descending order sort
    		if (count_comparator != 0) return count_comparator;
    		//people with tied numerical vaLues comparator is 0
    		return e1.getKey().compareTo(e2.getKey());
    	});
    	
    	for (Map.Entry<String, Integer> entry: follower_list) {
    		influencers.add(entry.getKey());// extract only string keys
    	}
    	return influencers;
    }

}
