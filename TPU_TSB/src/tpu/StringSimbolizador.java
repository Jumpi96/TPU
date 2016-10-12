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
    private final String delimiters=" /,.;?¿¡!\"*(){}_[]«:»";
    private final String noIntermedios="0123456789"; 
    private int contadorGuiones;
    private boolean noEsPalabra;
    private int maxDelimCodePoint;

    public StringSimbolizador(String str) {
        currentPosition = 0;
        newPosition = -1;
        this.str = str;        
        maxPosition = str.length();
        setMaxDelimCodePoint();
        contadorGuiones=0;
        noEsPalabra=false;
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
            if (m < c)
                m = c;
            count++;
        }
        maxDelimCodePoint = m;
        
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
            char c = str.charAt(position);
               
            if ((c > maxDelimCodePoint) || (delimiters.indexOf(c) < 0))
                break;
            noEsPalabra=false;    
            position++;
        }
        return position;
    }
    
    
    public String nextToken() {
        currentPosition = (newPosition >= 0) ?
            newPosition : skipDelimiters(currentPosition);

        contadorGuiones=0;
        newPosition = -1;

        if (currentPosition >= maxPosition)
            throw new NoSuchElementException();
        int start = currentPosition;
        currentPosition = scanToken(currentPosition);
        // Codigo agregado
        if (!noEsPalabra && contadorGuiones<=1)
            return str.substring(start, currentPosition);
        else
            if(hasMoreTokens())
                return nextToken();
            else
                return null;
    }
    
    private int scanToken(int startPos) {
        int position = startPos;
        while (position < maxPosition) {
            char c = str.charAt(position);
            if((noIntermedios.indexOf(c)>=0)){
                noEsPalabra=true;
            }
            
            if(c=='-')
                contadorGuiones++;
             
            if ((c <= maxDelimCodePoint) && (delimiters.indexOf(c) >= 0))
                break;
            position++;
        }
        return position;
    }
}
