/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tpu;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author juamp
 */
public class TPU {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TPU.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        GestorProcesamiento g = new GestorProcesamiento("16082-8.txt");
        g.procesar();
    }
    
    private static void baseDatos() throws SQLException{
        Connection conn = DriverManager.getConnection("jdbc:sqlite:D:\\Facultad\\TSB\\TPU\\Repositorio\\TPU\\TPU_TSB\\vocabulario");
        //seguir
    }
}
