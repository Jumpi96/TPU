/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpu;

/**
 *
 * @author juamp
 */
public class Palabra implements Comparable {
    private String contenido;
    private int contador;

    public Palabra(String contenido) {
        this.contenido = contenido;
        contador=1;
    }

    public String getContenido() {
        return contenido;
    }
    
    public void contar(){
        contador++;
    }

    @Override
    public int compareTo(Object o) {
        return contenido.compareTo(((Palabra)o).getContenido());
    }
    
    
}
