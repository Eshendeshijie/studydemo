/*
 * Copyright (C) 2013 The Android Open Source Project
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

package com.example.util;

import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.util.SparseArray;

public final class TypefaceUtils {
    private TypefaceUtils() {
        // This utility class is not publicly instantiable.
    }

    // This sparse array caches key label text height in pixel indexed by key label text size.
    private static final SparseArray<Float> sTextHeightCache = CollectionUtils.newSparseArray();
    // This sparse array caches symbol position indexed by key label text size.
    private static final SparseArray<Integer> sFullSymbolCache = CollectionUtils.newSparseArray();
    public static final int SYMBOL_LEFT = 1;
    public static final int SYMBOL_MIDDLE = 0;
    public static final int SYMBOL_RIGHT = -1;
    // Working variable for the following method.
    private static final Rect sTextHeightBounds = new Rect();

    public static float getCharHeight(final char[] referenceChar, final Paint paint) {
        final int key = getCharGeometryCacheKey(referenceChar[0], paint);
        synchronized (sTextHeightCache) {
            final Float cachedValue = sTextHeightCache.get(key);
            if (cachedValue != null) {
                return cachedValue;
            }
            FontMetricsInt mFmi = paint.getFontMetricsInt();
        	int temp = 0 - mFmi.bottom - mFmi.top;
            //paint.getTextBounds(referenceChar, 0, 1, sTextHeightBounds);
            final float height = temp; //sTextHeightBounds.height();
            sTextHeightCache.put(key, height);
            return height;
        }
    }
    
    // This sparse array caches key label text width in pixel indexed by key label text size.
    private static final SparseArray<Float> sTextWidthCache = CollectionUtils.newSparseArray();
    // Working variable for the following method.
    private static final Rect sTextWidthBounds = new Rect();

    public static float getCharWidth(final char[] referenceChar, final Paint paint) {
        final int key = getCharGeometryCacheKey(referenceChar[0], paint);
        synchronized (sTextWidthCache) {
            final Float cachedValue = sTextWidthCache.get(key);
            if (cachedValue != null) {
                return cachedValue;
            }

            paint.getTextBounds(referenceChar, 0, 1, sTextWidthBounds);
            final float width = sTextWidthBounds.width();
            sTextWidthCache.put(key, width);
            return width;
        }
    }

    public static float getStringWidth(final String string, final Paint paint) {
        paint.getTextBounds(string, 0, string.length(), sTextWidthBounds);
        return sTextWidthBounds.width();
    }

    private static int getCharGeometryCacheKey(final char referenceChar, final Paint paint) {
        final int labelSize = (int)paint.getTextSize();
        final Typeface face = paint.getTypeface();
        final int codePointOffset = referenceChar << 15;
        if (face == Typeface.DEFAULT) {
            return codePointOffset + labelSize;
        } else if (face == Typeface.DEFAULT_BOLD) {
            return codePointOffset + labelSize + 0x1000;
        } else if (face == Typeface.MONOSPACE) {
            return codePointOffset + labelSize + 0x2000;
        } else {
            return codePointOffset + labelSize;
        }
    }

    public static float getLabelWidth(final String label, final Paint paint) {
        final Rect textBounds = new Rect();
        paint.getTextBounds(label, 0, label.length(), textBounds);
        return textBounds.width();
    }
    
    public static float getLabelHeight(final String label, final Paint paint) {
        final Rect textBounds = new Rect();
        paint.getTextBounds(label, 0, label.length(), textBounds);
        return textBounds.height();
    }
    
    public static int getSymbolPosition(final String symbol, final Paint paint) {
		if (symbol == null || symbol.isEmpty()) {
			return SYMBOL_MIDDLE;
		}
    	final int key = getCharGeometryCacheKey(symbol.charAt(0), paint);
        synchronized (sFullSymbolCache) {
            final Integer cachedValue = sFullSymbolCache.get(key);
            if(cachedValue != null) {
            	return cachedValue;
            }
            if(isHalfSymbol(symbol) || symbol.length() == 2) {
            	// half symbol must be middle symbol
                sFullSymbolCache.put(key, SYMBOL_MIDDLE);
                return SYMBOL_MIDDLE;
            }
            final Rect textBounds = new Rect();
            paint.getTextBounds(symbol, 0, symbol.length(), textBounds);
            float textWidth = paint.measureText(symbol);
            float textWidthOfBlankSpace = paint.measureText(" ");
            int leftSpace = textBounds.left;
            int rightSpace = (int) (textWidth - textBounds.right);
            int position = 0;
            if(textWidthOfBlankSpace != 0) {
            	position = (int) ((rightSpace - leftSpace) / textWidthOfBlankSpace);
            }
            sFullSymbolCache.put(key, position);
            return position;
            /*if(textBounds.right < (float)textWidth * 3 / 5 && textBounds.left <= (float)textWidth * 1 / 4) {
            	// left symbol
            	sFullSymbolCache.put(key, SYMBOL_LEFT);
            	return SYMBOL_LEFT;
            } else if(textBounds.left >= (float)textWidth * 2 / 5 && textBounds.right > (float)textWidth * 3 / 4) {
            	// right symbol
            	sFullSymbolCache.put(key, SYMBOL_RIGHT);
            	return SYMBOL_RIGHT;
            }
            // middle symbol
            sFullSymbolCache.put(key, SYMBOL_MIDDLE);
            return SYMBOL_MIDDLE;*/
        }
    }
    
    public static boolean isHalfSymbol(String symbol) {
        if(symbol != null) {
        	if(symbol.length() == symbol.getBytes().length) {
        		return true;
        	}
        }
        return false;
    }
    
    public static void clearAllCaches() {
    	sFullSymbolCache.clear();
    	sTextHeightCache.clear();
    	sTextWidthCache.clear();
    }
}
