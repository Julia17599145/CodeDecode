/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package codedecode;

import static codedecode.CodeDecode2.compress;
import static codedecode.CodeDecode2.decompress;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Юлия
 */
public class CodeDecode extends javax.swing.JFrame {

    /**
     * Creates new form CodeDocode
     */
    public CodeDecode() {
        initComponents();
        jLabel3.setVisible(false);
    }

    StringBuilder readBuffer = new StringBuilder();
    StringBuilder writeBuffer = new StringBuilder();
    
    File readFile = new File("");
    //File writeFile = new File("");

    public static List<Integer> compress(StringBuilder readBuffer) {
        // Build the dictionary.
        int dictSize = 256;
        Map<String, Integer> dictionary = new HashMap<String, Integer>();
        for (int i = 0; i < 256; i++) {
            dictionary.put("" + (char) i, i);
           // System.out.println((char) i);
        }

        String w = "";
        List<Integer> result = new ArrayList<Integer>();
        for (char c : readBuffer.toString().toCharArray()) {
            String wc = w + c;
            if (dictionary.containsKey(wc)) {
                w = wc;
            } else {
                result.add(dictionary.get(w));
                // Add wc to the dictionary.
                dictionary.put(wc, dictSize++);
                w = "" + c;
            }
        }

        // Output the code for w.
        if (!w.equals("")) {
            result.add(dictionary.get(w));
        }
        return result;
    }

    /**
     * Decompress a list of output ks to a string.
     *
     * @param compressed
     */
    public static String decompress(List<Integer> compressed) {
        // Build the dictionary.
        int dictSize = 256;

        Map<Integer, String> dictionary = new HashMap<Integer, String>();
        for (int i = 0; i < 256; i++) {
            dictionary.put(i, "" + (char) i);  
        }

        String w = "" + (char)(int) compressed.remove(0);
        StringBuffer result = new StringBuffer(w);
        for (int k : compressed) {
            String entry;
            if (dictionary.containsKey(k)) {
                entry = dictionary.get(k);
            } else if (k == dictSize) {
                entry = w + w.charAt(0);
            } else {
                throw new IllegalArgumentException("Bad compressed k: " + k);
            }

            result.append(entry);

            // Add w+entry[0] to the dictionary.
            dictionary.put(dictSize++, w + entry.charAt(0));

            w = entry;
        }
        return result.toString();
    }

    public StringBuilder readFile() {
        int returnVal = jFileChooser1.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            readFile = jFileChooser1.getSelectedFile();
            readBuffer = new StringBuilder();
            try {
                //Объект для чтения файла в буфер
                FileInputStream stream = new FileInputStream(readFile.getAbsoluteFile());
                InputStreamReader reader = new InputStreamReader(stream, "Cp1251");
                BufferedReader in = new BufferedReader(reader);
                try {
                    //В цикле построчно считываем файл
                    String s;
                    while ((s = in.readLine()) != null) {
                        readBuffer.append(s);
                        //sb.append("\n");
                    }
                } finally {
                    //Также не забываем закрыть файл
                    in.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return readBuffer;
    }

    public File writeFile(String fileName, List<Integer> compressed) {
        File writeFile = new File(fileName);
        try {
            //проверяем, что если файл не существует то создаем его
            if (!writeFile.exists()) {
                writeFile.createNewFile();
            }

            //PrintWriter обеспечит возможности записи в файл
            PrintWriter out = new PrintWriter(writeFile.getAbsoluteFile());

            try {
                //Записываем текст у файл
                for (int i : compressed) {
                    out.print(i);
                    out.print(" ");
                }
            } finally {
                //После чего мы должны закрыть файл
                //Иначе файл не запишется
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return writeFile;
    }

    public List<Integer> readCodeFile(String fileName) {
        List<Integer> list = new ArrayList<Integer>();
        try {
            // Wrap the FileInputStream with a DataInputStream
            FileInputStream file_input = new FileInputStream(fileName);
            DataInputStream data_in = new DataInputStream(file_input);

            while (true) {
                
                try {
                    list.add(data_in.readInt());
                } catch (EOFException eof) {
                    System.out.println("End of File");
                    break;
                }
                // Print out the integer, double data pairs.
                //System.out.println(list);
            }
            data_in.close();
        } catch (IOException e) {
            System.out.println("IO Exception =: " + e);
        }
        return list;
    }

    public DataOutputStream writeDecodeFile(String fileName, List<Integer> compressed) {
       DataOutputStream data_out = null;
        try {
            // Create an output stream to the file.
            FileOutputStream file_output = new FileOutputStream(fileName);
            // Wrap the FileOutputStream with a DataOutputStream
            data_out = new DataOutputStream(file_output);
            
            // Write the data to the file in an integer/double pair
            for (int i : compressed) {
                data_out.writeInt(i);
            }
            // Close file when finished with it..
            file_output.close();
        } catch (IOException e) {
            System.out.println("IO exception = " + e);
        }
        return data_out;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jFileChooser1 = new javax.swing.JFileChooser();
        buttonGroup1 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Открыть файл для компрессии / декомпрессии");

        jButton1.setText("Открыть файл");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Выберите действие:");

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("Компрессия данных");

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setText("Декомпрессия данных");

        jButton2.setText("Выполнить");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel3.setText("jLabel3");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jRadioButton2)
                    .addComponent(jRadioButton1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addComponent(jLabel2)
                    .addComponent(jButton2)
                    .addComponent(jLabel3))
                .addContainerGap(87, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addContainerGap(85, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    List<Integer> compressed;

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        readFile();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        if (!readFile.exists()) {
            JOptionPane.showMessageDialog(jButton1, "Вы не выбрали файл!");
            return;
        } else {
            if (jRadioButton1.isSelected()) {
                compressed = compress(readBuffer);
                System.out.println(compressed);
                String fileName = readFile.getParent() + "\\code_" + readFile.getName()+ ".dat";
                DataOutputStream outputFile = writeDecodeFile(fileName, compressed);
                jLabel3.setVisible(true);
                jLabel3.setText("Размер файла до комперессии " + readFile.length() + " байт. После компрессии " + outputFile.size()+ " байт.");

            } else if (jRadioButton2.isSelected()) {
                List<Integer> decodeSequence = readCodeFile(readFile.getAbsolutePath());
                //char[] decodeSequence = readBuffer.toString().toCharArray();
                //ArrayList<Integer>decodeList = new ArrayList(Arrays.asList(decodeSequence));
                String decompressed = decompress(decodeSequence);
                System.out.println(decompressed);

                //System.out.println(list);

            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;

                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(CodeDecode.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CodeDecode.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CodeDecode.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CodeDecode.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CodeDecode().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    // End of variables declaration//GEN-END:variables
}
