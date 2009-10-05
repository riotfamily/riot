/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.common.util;

import java.util.Random;

import org.springframework.util.Assert;

/**
 * Class that generates random strings suitable for use as passwords.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class RandomStringGenerator {

	public enum Chars {
    	UPPER("ABCDEFGHJKLKMNPQRSTWXYZ", "IOUV"),
    	LOWER( "abcdefghijkmnpqrstwxyz", "louv"),
    	DIGITS("23456789", "01"),
    	SPECIALS("", "#+.,*-_@&%!?");
    	
	    private final String clear;
	    
	    private final String ambigious;
	    
	    Chars(String clear, String ambigious) {
	    	this.clear = clear;
	    	this.ambigious = ambigious;
	    }
    }
	
    private static RandomStringGenerator defaultInstance = 
    		new RandomStringGenerator(8, false, 
    				Chars.UPPER, 
    				Chars.LOWER, 
    				Chars.DIGITS);

    private Random random = new Random();

    private int length;
    
    private String chars; 
	
    public RandomStringGenerator() {
    }
    
    public RandomStringGenerator(int length, boolean inculdeAmbigious, 
    		Chars... chars) {
    	
    	this.length = length;
    	StringBuilder sb = new StringBuilder();
    	for (Chars c : chars) {
    		sb.append(c.clear);
    		if (inculdeAmbigious) {
    			sb.append(c.ambigious);
    		}
    	}
    	this.chars = sb.toString();
    	Assert.hasLength(this.chars, "No valid characters specified");
    }
    
    public RandomStringGenerator(int length, String chars) {
    	Assert.isTrue(length > 0, "Length must be greater than zero");
    	Assert.hasLength(chars, "No valid characters specified");
    	this.length = length;
    	this.chars = chars;
    }

    public static RandomStringGenerator getDefaultInstance() {
    	return defaultInstance;
    }
       
    public long getPossibleCombinations() {
    	return (long) Math.pow(chars.length(), length);
    }
    
    public int getChanceToGuess(long validPasswords) {
    	return (int) (getPossibleCombinations() / validPasswords);
    }
    
    public String generate() {
        StringBuffer buffer = new StringBuffer(length);

        // Loop through the password characters and assign each
        // a random character from the set of allowed characters
        for (int i = 0; i < length; i++) {
            int index = random.nextInt() % chars.length();
            index = Math.abs(index);
            char c = chars.charAt(index);
            buffer.append(c);
        }
        return buffer.toString();
    }

}
