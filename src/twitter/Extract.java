/* Copyright (c) 2007-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package twitter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.Instant;

/**
 * Extract consists of methods that extract information from a list of tweets.
 * 
 * DO NOT change the method signatures and specifications of these methods, but
 * you should implement their method bodies, and you may add new public or
 * private methods or classes if you like.
 */
public class Extract {

    /**
     * Get the time period spanned by tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return a minimum-length time interval that contains the timestamp of
     *         every tweet in the list.
     */
    public static Timespan getTimespan(List<Tweet> tweets) {
    	Instant start = Instant.MAX;
    	Instant end = Instant.MIN;
    	    	
    	for (Tweet t: tweets) {
    		Instant timestamp = t.getTimestamp();
    		
    		//by default caters to the case where only 1 tweet is passed
    		if (start.isAfter(timestamp))
    			start = timestamp;

    		if (end.isBefore(timestamp)) 
    			end = timestamp;
    	}
    	
    	return new Timespan(start, end);
    }

    /**
     * Get usernames mentioned in a list of tweets.
     * 
     * @param tweets
     *            list of tweets with distinct ids, not modified by this method.
     * @return the set of usernames who are mentioned in the text of the tweets.
     *         A username-mention is "@" followed by a Twitter username (as
     *         defined by Tweet.getAuthor()'s spec).
     *         The username-mention cannot be immediately preceded or followed by any
     *         character valid in a Twitter username.
     *         For this reason, an email address like bitdiddle@mit.edu does NOT 
     *         contain a mention of the username mit.
     *         Twitter usernames are case-insensitive, and the returned set may
     *         include a username at most once.
     */
    public static Set<String> getMentionedUsers(List<Tweet> tweets){
    	Set<String> usernames = new HashSet<>();
    	
    	for (Tweet t : tweets) {
    		String text = t.getText();
    		if (text.isBlank() || text.isEmpty()) { //empty tweet text
    			continue;
    		}
    		
    		String[] text_words = text.split(" ");//split words by empty space
    		for (String word: text_words) {
    			if (word.charAt(0) != '@') continue; //not a name

    			String name = word.substring(1); //extract username itself and lowercase it
    			//check '@' in between name i.e., wrong format
    			if (name.isEmpty() || (name.indexOf('@')!=-1)) 
    				continue; 
    			
    			//remove trailing punctuation marks
    			while(! name.isEmpty()) {
    				char c = name.charAt(name.length()-1);
    				if (Character.isLetterOrDigit(c) || c == '_' || c == '-') break; //valid trail reached
    				
    				//invalid trail, remove it
    				name = name.substring(0, name.length()-1);
    			}
    			
    			if (name.isEmpty()) continue; //punctuation trails ate away the word entirely
    			
//    			check if any illegal symbols in the name persist ie * & % $ etc
    			if ( ! name.matches("[a-zA-Z0-9_-]+")) continue;

    			// finally add it in the list
    			usernames.add(name.toLowerCase());
    		}
    		
    	}
    	return usernames;
    }

}
