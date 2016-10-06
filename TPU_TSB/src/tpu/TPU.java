/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpu;

import java.util.StringTokenizer;

/**
 *
 * @author juamp
 */
public class TPU {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        //Estimando...
        GestorProcesamiento g = new GestorProcesamiento("16082-8.txt");
        g.procesar();
        /*g=new GestorProcesamiento("22975-8.txt");
        g.procesar();
        g=new GestorProcesamiento("41575-8.txt");
        g.procesar();
        g=new GestorProcesamiento("18166-8.txt");
        g.procesar();*/
    }
    
}
