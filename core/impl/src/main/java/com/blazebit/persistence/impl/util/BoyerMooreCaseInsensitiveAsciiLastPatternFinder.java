/*
 * Copyright 2014 - 2022 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blazebit.persistence.impl.util;

/**
 * @author Christian Beikov
 * @since 1.2.0
 */
public class BoyerMooreCaseInsensitiveAsciiLastPatternFinder extends AbstractPatternFinder {

    // Only support ASCII
    private static final int RADIX = 256;
    private final int[] left;
    private final char[] pattern;

    public BoyerMooreCaseInsensitiveAsciiLastPatternFinder(String pattern) {
        final int length = pattern.length();
        this.pattern = new char[length];

        this.left = new int[RADIX];
        for (int i = 0; i < RADIX; i++) {
            this.left[i] = length - 1;
        }
        for (int i = length - 1; i >= 0; i--) {
            final char c = Character.toLowerCase(pattern.charAt(i));
            this.pattern[i] = c;
            this.left[c] = i;
        }
    }

    public int indexIn(char[] text, int start, int end) {
        int m = pattern.length;
        int skip;
        for (int i = end - m; i >= start; i -= skip) {
            skip = 0;
            for (int j = 0; j < m; j++) {
                final char c = Character.toLowerCase(text[i + j]);
                if (pattern[j] != c) {
                    skip = Math.max(1, left[c] - j);
                    break;
                }
            }
            if (skip == 0) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int indexIn(CharSequence text, int start, int end) {
        int m = pattern.length;
        int skip;
        for (int i = end - m; i >= start; i -= skip) {
            skip = 0;
            for (int j = 0; j < m; j++) {
                final char c = Character.toLowerCase(text.charAt(i + j));
                if (pattern[j] != c) {
                    skip = Math.max(1, left[c] - j);
                    break;
                }
            }
            if (skip == 0) {
                return i;
            }
        }
        return -1;
    }

}
