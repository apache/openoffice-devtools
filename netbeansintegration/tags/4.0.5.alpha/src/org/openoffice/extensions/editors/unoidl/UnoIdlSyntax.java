/**************************************************************
 * 
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * 
 *************************************************************/

package org.openoffice.extensions.editors.unoidl;

import org.netbeans.editor.Syntax;
import org.netbeans.editor.TokenID;
import org.openoffice.extensions.util.LogWriter;

/**
 *
 */
public class UnoIdlSyntax extends Syntax {
//    private static final ErrorManager LOGGER = ErrorManager.getDefault().getInstance("org.openoffice.unoidleditor.IdlSyntax"); // NOI18N
//    private static final boolean LOG = LOGGER.isLoggable(ErrorManager.INFORMATIONAL);

    private static final int ISI_BLOCK_COMMENT = 1;
    private static final int ISI_COMMENT_START = 2;
    private static final int ISI_STAR_IN_BLOCK_COMMENT = 3;
    private static final int ISI_LINE_COMMENT = 4;
    private static final int ISI_TEXT = 5;
    private static final int ISI_WHITESPACE = 6;
    private static final int ISI_KEYWORD = 7;
    private static final int ISI_PRECOMPILER = 8;
    private static final int ISI_OPENING_BRACKET = 9;
    private static final int ISI_TEXT_IN_BRACKETS = 10;
    private static final int ISI_DOUBLE_QUOTE = 11;
    private static final int ISI_SINGLE_QUOTE = 12;
    private static final int ISI_BACKSLASH_IN_DOUBLE_QUOTE = 13;
    private static final int ISI_BACKSLASH_IN_SINGLE_QUOTE = 14;
    private static final int ISI_NUMBER = 15;
    private static final int ISI_FUNCTION = 16;

    /** all recognized keywords **/
    private static final String[] KEYWORDS = {  
        // idl file types
        "constants", "enum", "exception", "interface", "service", "struct", 
        "singleton",
        // data types
        "any", "boolean", "byte", "char", "double", "float", "hyper", "long", 
        "sequence", "short", "string", "type", "void",
        // modifiers
        "const", "published", "unsigned", 
        // namespace
        "module", 
        // exceptions
        "raises",  
    }; // NOI18N

    /**
     * Creates a new instance of UnoIdlSyntax
     */
    public UnoIdlSyntax() {
        tokenContextPath = UnoIdlTokenContext.contextPath;
    }
    
    public UnoIdlSyntax(char[] theBuffer) {
        this.buffer = theBuffer;
        // initialize local variables with meaningful values
        this.state = INIT;
        this.offset = 0;
        this.stopOffset = theBuffer.length - 1;
    }
    
    public TokenID parseNextToken() {
        return doParseToken();
    }
    
    protected TokenID parseToken() {
        TokenID result = doParseToken();
        LogWriter.getLogWriter().log(LogWriter.LEVEL_INFO, "parseToken: " + result); // NOI18N
        return result;
    }
    
    private TokenID doParseToken() {
        char actChar;

        // get the state with a lookahead function
        int[] offsetAndState = getNextState(buffer, offset, stopOffset, state);
        // add the count of chars used for determining the lookahead 
        // to the offset
        offset += offsetAndState[0];
        // set the state
        state = offsetAndState[1];
        // go to the end of the state and return a token.
        while (offset < stopOffset) {
            actChar = buffer[offset];
            switch(state) {
                case ISI_LINE_COMMENT:
                    switch (actChar) {
                        case '\n': // NOI18N
                            offset++;
                            return UnoIdlTokenContext.COMMENT;
                    }
                break;    
                case ISI_BLOCK_COMMENT:
                    switch (actChar) {
                        case '*': // NOI18N
                            state = ISI_STAR_IN_BLOCK_COMMENT;
                            break;
                    }
                break;
                case ISI_STAR_IN_BLOCK_COMMENT:
                    switch (actChar) {
                        case '/': // NOI18N
                            offset++;
                            return UnoIdlTokenContext.COMMENT;
                        case '*': // NOI18N
                            // state stays the same; just here to show it
                        break;
                        default:
                            state = ISI_BLOCK_COMMENT;
                    }
                break;
                case ISI_PRECOMPILER: //have read this already
                    offset++;
                    return UnoIdlTokenContext.PRECOMPILE;
                case ISI_KEYWORD:
                    // offset++ is not necessary; we're one further because the length
                    // of the keyword is added to offset.
                    return UnoIdlTokenContext.KEYWORD;
                case ISI_TEXT:
                    int[] nextStuff = getNextState(buffer, offset, stopOffset, state);
                    int newState = nextStuff[1];  // not really interested in offset
                    // switch between text and whitespace: only in whitespace mode
                    // are numbers allowed; this way they always start with a whitespace
                    if (newState == ISI_WHITESPACE) {
                        state = ISI_WHITESPACE;
                    }
                    else if (newState != ISI_TEXT) { 
                        return UnoIdlTokenContext.TEXT;
                    }
                break;
                case ISI_WHITESPACE:
                    nextStuff = getNextState(buffer, offset, stopOffset, state);
                    newState = nextStuff[1];  // not really interested in offset
                    if (newState == ISI_TEXT) {
                        state = ISI_TEXT;
                    }
                    else if (newState != ISI_WHITESPACE) { 
                        return UnoIdlTokenContext.TEXT;
                    }
                break;
                case ISI_OPENING_BRACKET:
                    offset++;
                    state = ISI_TEXT_IN_BRACKETS;
                    return UnoIdlTokenContext.TEXT;
                case ISI_TEXT_IN_BRACKETS:
                    switch (actChar) {
                        case ']': // NOI18N // closing bracket found
                            state = ISI_TEXT;
                            // no offset++ because ']' is not part of the token, it's only
                            // the text inside
                            return UnoIdlTokenContext.BRACKET_TEXT;
                        // default: state stays the same...
                    }
                break;
                case ISI_DOUBLE_QUOTE:
                    switch (actChar) {
                        case '\"': // NOI18N // closing double quote found.
                            offset++;
                            return UnoIdlTokenContext.STRING;
                        case '\\': // NOI18N
                            state = ISI_BACKSLASH_IN_DOUBLE_QUOTE;
                            break;
                        // default: state stays the same...
                    }
                break;
                case ISI_BACKSLASH_IN_DOUBLE_QUOTE: // do not react on any char
                    state = ISI_DOUBLE_QUOTE;
                break;
                case ISI_SINGLE_QUOTE:
                    switch (actChar) {
                        case '\'': // NOI18N // closing single quote found.
                            offset++;
                            return UnoIdlTokenContext.CHAR;
                        case '\\': // NOI18N
                            state = ISI_BACKSLASH_IN_SINGLE_QUOTE;
                            break;
                        // default: state stays the same...
                    }
                break;
                case ISI_BACKSLASH_IN_SINGLE_QUOTE: // do not react on any char
                    state = ISI_SINGLE_QUOTE;
                break;
                case ISI_NUMBER:
                    if ((actChar > '9' || actChar < '0') && actChar != '.') {
                        // offset++ is not needed: now we're after the number
                        return UnoIdlTokenContext.NUMBER;
                    }
                    // do nothing in every other case
                break;
            }
            offset++;
        }
        switch(state) {
            case ISI_BLOCK_COMMENT:
            case ISI_LINE_COMMENT:
                return UnoIdlTokenContext.COMMENT;
            case ISI_WHITESPACE:
            case ISI_TEXT:
            case ISI_FUNCTION:
                return UnoIdlTokenContext.TEXT;
            case ISI_KEYWORD:
                return UnoIdlTokenContext.KEYWORD;
            case ISI_PRECOMPILER:
                return UnoIdlTokenContext.PRECOMPILE;
            case ISI_TEXT_IN_BRACKETS:
                return UnoIdlTokenContext.BRACKET_TEXT;
            case ISI_DOUBLE_QUOTE:
                return UnoIdlTokenContext.STRING;
            case ISI_SINGLE_QUOTE:
                return UnoIdlTokenContext.CHAR;
            case ISI_NUMBER:
                return UnoIdlTokenContext.NUMBER;
        }
        return null;
    }

    /**
     * take the next n chars to determine the next state for reading the buffer.
     * start with char at offset and take no more than stopOffset-offset chars from 
     * buffer.
     * @return array of length 2; index 0 is the count which is added to offset;
     * index 1 is the state we're now in.
     */
    private int[] getNextState(
                    char[] buffer, int localOffset, int stopOffset, int oldState) {
        if (oldState == ISI_BLOCK_COMMENT) {
            return new int[]{0, oldState};
        }
        if (oldState == ISI_TEXT_IN_BRACKETS) {
            return new int[]{0, oldState};
        }
        int newState = INIT;
        int offsetAdd = 0;
        char nextChar = buffer[localOffset];
        if (nextChar == '/' && ++localOffset < stopOffset) { // one more char for a comment
            nextChar = buffer[localOffset];
            switch(nextChar) {
                case '*':
                    newState = ISI_BLOCK_COMMENT;
                    offsetAdd = 2;
                    break;
                case '/':
                    newState = ISI_LINE_COMMENT;
                    offsetAdd = 2;
                    break;
                default: 
                    newState = ISI_TEXT;
                    offsetAdd = 1;
            }
        }
        else if (nextChar == '#') { // precompiler statement
            while(!Character.isWhitespace(nextChar) && localOffset + ++offsetAdd < stopOffset) {
                nextChar = buffer[localOffset + offsetAdd];
            }
            newState = ISI_PRECOMPILER;
        }
        else if (nextChar == '[') { // opening bracket
            newState = ISI_OPENING_BRACKET;
        }
        else if (nextChar == '\\') { 
            newState = ISI_TEXT; // if this is the beginning, it's surely text
            offsetAdd++; // jump over the next char
        }
        else if (nextChar == '\"') { // NOI18N // opening bracket
            // strange stuff may happen if a file ever starts with a quote, but that's a syntax error anyway...'
            newState = ISI_DOUBLE_QUOTE;
            offsetAdd++;
        }
        else if (nextChar == '\'') { // opening bracket
            newState = ISI_SINGLE_QUOTE;
            offsetAdd++;
        }
        else if (nextChar <= '9' && nextChar >= '0') { // numeric!
            if (oldState == ISI_WHITESPACE) {
                newState = ISI_NUMBER;
            }
            else {
                newState = ISI_TEXT;
            }
        }
        else if (nextChar == '.') { // lookahead for next char to make sure it's a number
            if (localOffset + ++offsetAdd < stopOffset) {
                nextChar = buffer[localOffset + offsetAdd];
                if (nextChar <= '9' && nextChar >= '0') {
                    newState = ISI_NUMBER;
                    offsetAdd++;
                }
                else {
                    newState = ISI_TEXT;
                }
            }
        }
        else {
            if (oldState != ISI_TEXT) {
                String keyword = getKeyword(buffer, localOffset, stopOffset);
                if (keyword == null) {
                    if (Character.isWhitespace(nextChar)) {
                        newState = ISI_WHITESPACE;
                    }
                    else {
                        newState = ISI_TEXT;
                    }
                }
                else {
                    newState = ISI_KEYWORD;
                    this.tokenLength = keyword.length();
                    offsetAdd = tokenLength;
                }
            }
            else {
                if (Character.isWhitespace(nextChar)) {
                    newState = ISI_WHITESPACE;
                }
                else {
                    newState = ISI_TEXT;
                }
            }
        }
        return new int[] {offsetAdd, newState};
    }
    
    /**
     * returns the keyword if it was found in the buffer
     */
    private static String getKeyword(char[]buffer, int offset, int stopOffset) {
        for (int i=0; i<KEYWORDS.length; i++) {
            String keyword = KEYWORDS[i];
            if (keyword.length() + offset < stopOffset) {
                String match = String.copyValueOf(
                        buffer, offset, keyword.length());
                // check if after the keyword's a whitespace!
                if (match.equals(keyword)) {
                    if (buffer.length > offset + keyword.length()) {
                        if (Character.isWhitespace(buffer[offset + keyword.length()]))
                            return keyword;
                    }
                }
            }
        }
        return null;
    }
}


