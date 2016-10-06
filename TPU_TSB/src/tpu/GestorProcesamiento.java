/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpu;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Set;
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
        hash= new HashMap(); // Estimando cantidad de palabras a ingresar
    }
    
    public int countLines() throws IOException { // Estiamndo....
        InputStream is = new BufferedInputStream(new FileInputStream(origen));
        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
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
