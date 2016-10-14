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
import java.sql.ResultSet;
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
        actualizarVocabulario(origen,nuevoHash);
    }

    /*
    Graba apariciones en el archivo procesado en la BD y actualiza el Heap
    que contiene el vocabulario en memoria.
    Hace ambas cosas en la misma iteración para evitar duplicar el tiempo de
    ejecución. Realiza INSERT múltiples de hasta 500 filas por límite de SQLite.
    */
    private void actualizarVocabulario(String origen,HashMap<String,Integer> hash){
        //actualizarHash(hash);
        
        Set<String> s=hash.keySet();
        Iterator it = s.iterator();
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:D:\\Facultad\\TSB\\TPU\\Repositorio\\TPU\\TPU_TSB\\vocabulario");
            Statement st=conn.createStatement();
            String consulta;
            String palabra;
            int[] temp;
            int contador=0;
            int tope;
            while(contador*500<s.size()){ 
                consulta="INSERT INTO Palabras (contenido,repeticiones,origen) VALUES \n   ";
                if ((contador+1)*500<s.size())
                    tope=500;
                else
                    tope=s.size()-contador*500;
                
                for (int i = 0; i < tope-1; i++) {
                    palabra=(String)it.next();
                    if (!hashCompleto.containsKey(palabra))
                        hashCompleto.put(palabra,new int[] {hash.get(palabra),1});
                    else{
                        temp=hashCompleto.get(palabra);
                        hashCompleto.put(palabra,new int[]{temp[0]+hash.get(palabra),temp[1]+1});
                    }
                    consulta+="('"+palabra+"',"+hash.get(palabra)+",'"+origen+"'),";
                }
                palabra=(String)it.next();
                consulta+="('"+palabra+"',"+hash.get(palabra)+",'"+origen+"');";
                st.execute(consulta);
                contador++;
            }
            st.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(GestorProcesamiento.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private HashMap<String,int[]> leerBD(){
        HashMap<String,int[]> hash = new HashMap();
        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:D:\\Facultad\\TSB\\TPU\\Repositorio\\TPU\\TPU_TSB\\vocabulario");
            Statement st = conn.createStatement();
            String consulta="SELECT contenido,SUM(repeticiones),COUNT(origen)";
            consulta+="FROM palabras GROUP BY contenido";
            ResultSet rs=st.executeQuery(consulta);
            
            while (rs.next()) {
                hash.put(rs.getString(1), new int[]{rs.getInt(2),rs.getInt(3)});
            }
            rs.close();
            st.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(GestorProcesamiento.class.getName()).log(Level.SEVERE, null, ex);
        }
        
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
