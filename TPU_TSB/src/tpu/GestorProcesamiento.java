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
    private HashMap <String,int[]>  hash;

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
        int[] a;
        boolean primeraVezEnArchivo=true;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(origen), inputCharset));
            linea=br.readLine();
            while(true){
                if(linea.equals("")==false){
                    st=new StringSimbolizador(linea);
                    while(st.hasMoreTokens()){
                        cont=st.nextToken();
                        if(cont!=null)
                            if(hash.containsKey(cont)) {
                                a=hash.get(cont);
                                a[0]=a[0]+1;
                                if(primeraVezEnArchivo){
                                    a[1]=a[1]+1;
                                    primeraVezEnArchivo=false;
                                }
                                hash.put(cont,a);
                            }
                            else
                                hash.put(cont,new int[]{1,1});
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
