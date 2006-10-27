/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.util;

import java.util.Random;

public class PasswordGenerator {

    private static final String UPPER_CHARS = "ABCDEFGHJKLKMNPQRSTWXYZ";
    private static final String AMBIGUOUS_UPPER_CHARS = "IOUV";
    
    private static final String LOWER_CHARS = "abcdefghijkmnpqrstwxyz";
    private static final String AMBIGUOUS_LOWER_CHARS = "louv";
    
    private static final String DIGITS = "23456789";
    private static final String AMBIGUOUS_DIGITS = "01";

    private static PasswordGenerator defaultInstance = new PasswordGenerator();

    private Random random = new Random();

    private boolean useUpperChars;
    private boolean useLowerChars;
    private boolean useDigits;
    
    private boolean includeAmbiguous;

    private int passwordLength;

    private StringBuffer chars; 
	
    public PasswordGenerator() {
        this(8, true, true, true);
    }

    public PasswordGenerator(
        int passwordLength,
        boolean useUpperChars,
        boolean useLowerChars,
        boolean useDigits) {

        this.passwordLength = passwordLength;
        this.useUpperChars = useUpperChars;
        this.useLowerChars = useLowerChars;
        this.useDigits = useDigits;
    }

    public static PasswordGenerator getDefaultInstance() {
    	return defaultInstance;
    }
    
    public boolean getUseUpperChars() {
        return useUpperChars;
    }

    public boolean getUseLowerChars() {
        return useLowerChars;
    }

    public boolean getUseDigits() {
        return useDigits;
    }

    public boolean isIncludeAmbiguous() {
		return includeAmbiguous;
	}
    
    public void setUseUpperChars(boolean v) {
    	check();
        useUpperChars = v;
    }

    public void setUseLowerChars(boolean v) {
    	check();
        useLowerChars = v;
    }

    public void setUseDigits(boolean v) {
    	check();
        useDigits = v;
    }

	public void setIncludeAmbiguous(boolean includeAmbiguous) {
		check();
		this.includeAmbiguous = includeAmbiguous;
	}
	
	public void setPasswordLength(int i) {
        passwordLength = i;
    }
	
    public int getPasswordLength() {
        return passwordLength;
    }

    protected void check() {
    	if (chars != null) {
    		throw new IllegalStateException(
    				"PasswordGenerator is already configured");
    	}
    }
    
    protected void prepare() {
    	if (chars == null) {
	    	chars = new StringBuffer();
	        if (getUseLowerChars()) {
	            chars.append(LOWER_CHARS);
	            if (includeAmbiguous) {
	            	chars.append(AMBIGUOUS_LOWER_CHARS);
	            }
	        }
	        if (getUseUpperChars()) {
	            chars.append(UPPER_CHARS);
	            if (includeAmbiguous) {
	            	chars.append(AMBIGUOUS_UPPER_CHARS);
	            }
	        }
	        if (getUseDigits()) {
	            chars.append(DIGITS);
	            if (includeAmbiguous) {
	            	chars.append(AMBIGUOUS_DIGITS);
	            }
	        }
	        if (chars.length() == 0) {
	        	throw new IllegalStateException(
	            		"At least one type of characters must be specified");
	        }
    	}
    }
    
    public long getPossibleCombinations() {
    	return (long) Math.pow(chars.length(),passwordLength);
    }
    
    public int getChanceToGuess(long validPasswords) {
    	//return (int) (100 / (validPasswords / getPossibleCombinations()));
    	return (int)(getPossibleCombinations() / validPasswords);
    }
    
    public String generate() {
   		prepare();
        StringBuffer buffer = new StringBuffer(passwordLength);

        // Loop through the password characters and assign each
        // a random character from the set of allowed characters

        for (int i = 0; i < passwordLength; i++) {
            int index = random.nextInt() % chars.length();
            index = Math.abs(index);
            char c = chars.charAt(index);
            buffer.append(c);
        }
        return buffer.toString();
    }

}
