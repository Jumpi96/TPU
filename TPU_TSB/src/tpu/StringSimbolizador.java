/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpu;

import java.util.NoSuchElementException;

/**
 *
 * @author juamp
 */
public class StringSimbolizador {
    private int currentPosition;
    private int newPosition;
    private int maxPosition;
    private String str;
    private final String delimiters=" /,.;?¿¡!\"'*(){}_[]";
    
    private final String noIntermedios="0123456789"; //IMPLEMENTAR
    private int[] noIntermediosCodePoints; // IMPLEMENTAR
    private int guionCodePoint;
    private int contadorGuiones;
    private boolean noEsPalabra=false; //IMPLEMENTAR
    
    private boolean delimsChanged;
    private int maxDelimCodePoint;
    private boolean hasSurrogates = false;
    private int[] delimiterCodePoints;

    public StringSimbolizador(String str) {
        currentPosition = 0;
        newPosition = -1;
        delimsChanged = false;
        this.str = str;        
        maxPosition = str.length();
        setMaxDelimCodePoint();
        contadorGuiones=0;
    }
    
    private void setMaxDelimCodePoint(){
        if (delimiters == null) {
            maxDelimCodePoint = 0;
            return;
        }

        int m = 0;
        int c;
        int count = 0;
        for (int i = 0; i < delimiters.length(); i += Character.charCount(c)) {
            c = delimiters.charAt(i);
            if (c >= Character.MIN_HIGH_SURROGATE && c <= Character.MAX_LOW_SURROGATE) {
                c = delimiters.codePointAt(i);
                hasSurrogates = true;
            }
            if (m < c)
                m = c;
            count++;
        }
        maxDelimCodePoint = m;

        if (hasSurrogates) {
            delimiterCodePoints = new int[count];
            for (int i = 0, j = 0; i < count; i++, j += Character.charCount(c)) {
                c = delimiters.codePointAt(j);
                delimiterCodePoints[i] = c;
            }
            //Codigo agregado
            noIntermediosCodePoints = new int[noIntermedios.length()];
            for (int i = 0, j = 0; i < noIntermedios.length(); i++, j += Character.charCount(c)) {
                c = noIntermedios.codePointAt(j);
                noIntermediosCodePoints[i] = c;
            }
            String ch="-";
            guionCodePoint=ch.codePointAt(0);
        }
    }
    
    public boolean hasMoreTokens() {
        newPosition = skipDelimiters(currentPosition);
        return (newPosition < maxPosition);
    }

    private int skipDelimiters(int startPos) {
        if (delimiters == null)
            throw new NullPointerException();

        int position = startPos;
        while (position < maxPosition) {
            if (!hasSurrogates) {
                char c = str.charAt(position);
                if ((c > maxDelimCodePoint) || (delimiters.indexOf(c) < 0))
                    break;
                position++;
            } else {
                int c = str.codePointAt(position);
                if ((c > maxDelimCodePoint) || !isDelimiter(c)) {
                    break;
                }
                position += Character.charCount(c);
            }
        }
        return position;
    }
    
    private boolean isDelimiter(int codePoint) {
        for (int i = 0; i < delimiterCodePoints.length; i++) {
            if (delimiterCodePoints[i] == codePoint) {
                return true;
            }
        }
        //codigoAgregado ESTO NO FUNCIONA ASI; CAMBIAR STRATEGY
        for (int i = 0; i < noIntermediosCodePoints.length; i++) {
            if (noIntermediosCodePoints[i] == codePoint) {
                noEsPalabra=true;
                break;
            }
        }
        if(codePoint==guionCodePoint){
            contadorGuiones++;
        } 
        return false;
    }
    
    public String nextToken() {
        currentPosition = (newPosition >= 0 && !delimsChanged) ?
            newPosition : skipDelimiters(currentPosition);

        contadorGuiones=0;
        noEsPalabra=false;
        delimsChanged = false;
        newPosition = -1;

        if (currentPosition >= maxPosition)
            throw new NoSuchElementException();
        int start = currentPosition;
        currentPosition = scanToken(currentPosition);
        // Codigo agregado
        if (!noEsPalabra && contadorGuiones<1)
            return str.substring(start, currentPosition);
        else
            return nextToken();
    }
    
    private int scanToken(int startPos) {
        int position = startPos;
        while (position < maxPosition) {
            if (!hasSurrogates) {
                char c = str.charAt(position);
                if ((c <= maxDelimCodePoint) && (delimiters.indexOf(c) >= 0))
                    break;
                position++;
            } else {
                int c = str.codePointAt(position);
                if ((c <= maxDelimCodePoint) && isDelimiter(c))
                    break;
                position += Character.charCount(c);
            }
        }
        return position;
    }
}
