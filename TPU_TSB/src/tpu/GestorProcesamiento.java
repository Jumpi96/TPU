/*
 GestorProcesamiento.
  Clase encargada de procesar archivos nuevos, leer vocabulario existente,
  grabar en la base de datos y actualizar el vocabulario
  sin recurrir a ella.
 */
package tpu;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

    public HashMap<String, int[]> getHashCompleto() {
        return hashCompleto;
    }

    public GestorProcesamiento() {
        hashCompleto= leerBD();
    }
   
    
    public boolean procesar(String origen){
        if (!estaProcesado(origen)) {
            HashMap <String,Integer> nuevoHash=contarPalabras(origen);
            actualizarVocabulario(origen,nuevoHash);
            return true;
        }
        else
            return false;
    }

    /*
    Graba apariciones en el archivo procesado en la BD y actualiza el Heap
    que contiene el vocabulario en memoria.
    Hace ambas cosas en la misma iteración para evitar duplicar el tiempo de
    ejecución. Realiza INSERT múltiples a través del procesamiento de lotes.
    Utiliza PreparedStatements para mejorar funcionamiento y evitar errores.
    Trabaja con transacciones dado optimización de SQLite en esos casos.
    Trabaja con lotes de hasta 1000 palabras para evitar problemas de memoria.
    */
    private void actualizarVocabulario(String origen,HashMap<String,Integer> hash){        
        Set<String> s=hash.keySet();
        Iterator it = s.iterator();
        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:vocabulario");
            Statement trans=conn.createStatement();
            String consulta="INSERT INTO palabras (contenido,repeticiones,origen) VALUES (?,?,'"+origen+"')";
            PreparedStatement st=conn.prepareStatement(consulta);
            String palabra;
            int[] temp;
            int tope;
            trans.execute("BEGIN");
            for(int contador=0;contador*1000<s.size();contador++){
                
                if ((contador+1)*1000<s.size())
                    tope=1000;
                else
                    tope=s.size()-contador*1000;
                
                for (int i = 0; i < tope; i++) {
                    palabra=(String)it.next();
                    if (!hashCompleto.containsKey(palabra))
                        hashCompleto.put(palabra,new int[] {hash.get(palabra),1});
                    else{   
                        temp=hashCompleto.get(palabra);
                        hashCompleto.put(palabra,new int[]{temp[0]+hash.get(palabra),temp[1]+1});
                    }
                    st.setString(1, palabra);
                    st.setInt(2, hash.get(palabra));
                    st.addBatch();
                }
                st.executeBatch();
            }
            trans.execute("END");
            trans.close();
            st.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(GestorProcesamiento.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
    Lee el vocabulario existente, devuelve el HashMap completo con palabras,
    repeticiones y archivos distintos en los que apareció.
    */
    private HashMap<String,int[]> leerBD(){
        HashMap<String,int[]> hash = new HashMap();
        Connection conn;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:vocabulario");
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

    private boolean estaProcesado(String origen) {
        Connection conn;
        boolean procesado=true;
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:vocabulario");
            Statement st = conn.createStatement();
            String consulta="SELECT COUNT(*)";
            consulta+="FROM palabras WHERE origen LIKE '"+origen+"'";
            ResultSet rs=st.executeQuery(consulta);
            if (rs.getInt(1)!=0)
                procesado=true;
            else
                procesado=false;
            
            rs.close();
            st.close();
            conn.close();
        } catch (SQLException ex) {
            Logger.getLogger(GestorProcesamiento.class.getName()).log(Level.SEVERE, null, ex);
        }
        return procesado;
    }
}
