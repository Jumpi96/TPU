/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
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
    private HashMap <String,Integer>  hash;

    public GestorProcesamiento(String origen) {
        this.origen = origen;
        hash= new HashMap(); // ¿Como estimamos largo del hash para evitar exceso de memoria?
    }
    
    public void procesar(){
        contarPalabras();
        //cargarBD();
    }
    
    private void contarPalabras(){
        BufferedReader br;
        String delimitadores = " /,.;?¿¡!\"'";
        StringTokenizer st;
        String linea,cont;

        
        try {
            br= new BufferedReader(new FileReader(origen));
            linea = br.readLine();
            while(true){
                if(linea.equals("")==false){
                    st=new StringTokenizer(linea,delimitadores);
                    while(st.hasMoreTokens()){
                        cont=st.nextToken();
                        if(hash.containsKey(cont)) 
                            hash.put(cont,hash.get(cont)+1);
                        else
                            hash.put(cont,1);
                    }
                }   
                linea=br.readLine();
            }
        } catch (NullPointerException ex) {} catch (IOException ex) {
            Logger.getLogger(GestorProcesamiento.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Working");
        hash.keySet();
    }
}
