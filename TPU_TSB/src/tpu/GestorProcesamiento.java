/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpu;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Set;
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
        hash= new HashMap();
    }
   
    
    public void procesar(){
        contarPalabras();
        //cargarBD();
    }
    
    private void contarPalabras(){
        BufferedReader br;
        StringSimbolizador st;
        String linea,cont;
        Charset inputCharset = Charset.forName("ISO-8859-1");

        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(origen), inputCharset));
            linea=br.readLine();
            while(true){
                if(linea.equals("")==false){
                    st=new StringSimbolizador(linea);
                    while(st.hasMoreTokens()){
                        cont=st.nextToken();
                        if(cont!=null)
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
        Set set = hash.keySet();
        System.out.println(set.size());
    }
}
