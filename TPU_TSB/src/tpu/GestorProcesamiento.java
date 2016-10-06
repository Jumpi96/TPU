/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpu;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author juamp
 */
public class GestorProcesamiento {
    private String origen;
    private HashTable <Palabra> hash;

    public GestorProcesamiento(String origen) {
        this.origen = origen;
        hash= new HashTable(); // ¿Como estimamos largo del hash para evitar exceso de memoria?
    }
    
    public void procesar(){
        contarPalabras();
        //cargarBD();
    }
    
    private void contarPalabras(){
        File f = new File(origen);
        Scanner s;
        try {
            s = new Scanner(f);
            String delimitadores = " ,.;?¿¡!\"'";
            StringTokenizer st;

            while(s.hasNextLine()){
                st=new StringTokenizer(s.nextLine(),delimitadores);
                while(st.hasMoreTokens()){
                    String cont=st.nextToken();
                    if(hash.containsContenido(cont)) 
                        ((Palabra)hash.getContenido(cont)).contar();
                    else
                        hash.put(new Palabra(cont));
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GestorProcesamiento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
}
