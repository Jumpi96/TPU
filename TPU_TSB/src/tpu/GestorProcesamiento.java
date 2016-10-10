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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author juamp
 */
public class GestorProcesamiento {
    private HashMap <String,int[]>  hashCompleto;

    public GestorProcesamiento() {
        hashCompleto= leerBD();
    }
   
    
    public void procesar(String origen){
        HashMap <String,Integer> nuevoHash=contarPalabras(origen);
        grabarBD(origen,nuevoHash);
    }

    private void grabarBD(String origen,HashMap<String,Integer> hash){
        actualizarHash(hash);
        
        Set<String> s=hash.keySet();
        Iterator it = s.iterator();
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:D:\\Facultad\\TSB\\TPU\\Repositorio\\TPU\\TPU_TSB\\vocabulario");
            Statement st = conn.createStatement();
            String consulta;
            String palabra;
            consulta="INSERT INTO Palabras (contenido,archivo,repeticiones) VALUES \n";
            for (int i = 0; i < 499; i++) { // HACERLO CADA 500 crear otro LOOP
                palabra=(String)it.next();
                consulta+="('"+palabra+"',"+hash.get(palabra)+",'"+origen+"'),";
            }
            palabra=(String)it.next();
            consulta+="('"+palabra+"',"+hash.get(palabra)+",'"+origen+"');";
            st.execute(consulta);
            st.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(GestorProcesamiento.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void actualizarHash(HashMap <String,Integer> hash){ // Eficiencia de manejar dos loops a la vez????
        Set<String> s=hash.keySet();
        Iterator it = s.iterator();
        int[] temp;
        String actual;
        for (int i = 0; i < s.size(); i++) {
            actual=(String)it.next();
            if (!hashCompleto.containsKey(actual))
                hashCompleto.put(actual,new int[] {hash.get(actual),1});
            else{
                temp=hashCompleto.get(actual);
                hashCompleto.put(actual,new int[]{temp[0]+hash.get(actual),temp[1]+1});
            } 
        }   
    }
    private HashMap<String,int[]> leerBD(){
        // Completar
        return new HashMap();
    }
    
    private HashMap<String,Integer> contarPalabras(String origen){
        BufferedReader br;
        StringSimbolizador st;
        String linea,cont;
        HashMap <String,Integer> hash=new HashMap();
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
                            if(hash.containsKey(cont)) {
                                hash.put(cont,hash.get(cont)+1);
                            }
                            else
                                hash.put(cont,1);
                    }
                }   
                linea=br.readLine();
            }
        } catch (NullPointerException ex) {} catch (IOException ex) {
            Logger.getLogger(GestorProcesamiento.class.getName()).log(Level.SEVERE, null, ex);
        }
        return hash;
    }
}
