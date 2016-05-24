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
import java.util.Set;
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
        System.err.println((int) ' ');

    }

    StringBuilder readBuffer = new StringBuilder();
    StringBuilder writeBuffer = new StringBuilder();

    File readFile = new File("");

    public List<Short> compress(StringBuilder readBuffer) {
        // заполнение словаря
        Map<String, Short> dictionary = new HashMap<String, Short>();
        short code = 0;
        if (jCheckBox1.isSelected()) {
            System.err.println("Nf,k");
            File file = new File("../CodeDecode/bankcode.txt");
            StringBuilder strBuffer = new StringBuilder();
            if (file.exists() && file.length() != 0) {
                try {
                    //Объект для чтения файла в буфер
                    FileInputStream stream = new FileInputStream(file.getAbsoluteFile());
                    InputStreamReader reader = new InputStreamReader(stream, "Cp1251");
                    BufferedReader in = new BufferedReader(reader);
                    try {
                        //В цикле построчно считываем файл
                        String s;
                        while ((s = in.readLine()) != null) {
                            strBuffer.append(s);
                        }
                        String[] splitFile = strBuffer.toString().trim().split("(~\\|)");
                        
                        for (int i = 0; i < splitFile.length; i += 2) {
                            dictionary.put(splitFile[i], Short.parseShort(splitFile[i + 1]));
                        }
                    } finally {
                        in.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            for (int i = 32; i < 127; i++) {
                dictionary.put("" + (char) i, code++);
            }
            for (int i = 1040; i < 1104; i++) {
                dictionary.put("" + (char) i, code++);
            }
            //добавление символа №
            dictionary.put("" + (char) 8470, code++);
            //добавление символа ё
            dictionary.put("" + (char) 1105, code++);
            //добавление символа Ё
            dictionary.put("" + (char) 1025, code++);
            //символы открывающихся / закрывающихся ковычек
            dictionary.put("" + (char) 171, code++);
            dictionary.put("" + (char) 187, code++);
            //перенос
            dictionary.put("" + (char) 13, code++);
        }
        short max = 0;
        for (Map.Entry<String, Short> entry : dictionary.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
            }
        }
        code = max;
        String w = "";
        List<Short> result = new ArrayList<Short>();
        for (char c : readBuffer.toString().toCharArray()) {
            String wc = w + c;
            //System.out.println("wc " + wc);
            if (dictionary.containsKey(wc)) {
                w = wc;
            } else {
                result.add(dictionary.get(w));
                //System.out.println("result " + w);
                // Add wc to the dictionary.
                if (code != 4096) {
                    dictionary.put(wc, code++);
                    //System.out.println("dictionary " + wc);
                }
                w = "" + c;
            }
        }
        System.err.println(dictionary);
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
    public String decompress(List<Short> compressed) {
        // Build the dictionary.
        Short code = 0;
        Map<Short, String> dictionary = new HashMap<Short, String>();

        if (jCheckBox1.isSelected()) {
            
            File file = new File("../CodeDecode/bankcode.txt");
            StringBuilder strBuffer = new StringBuilder();
            if (file.exists() && file.length() != 0) {
                try {
                    //Объект для чтения файла в буфер
                    FileInputStream stream = new FileInputStream(file.getAbsoluteFile());
                    InputStreamReader reader = new InputStreamReader(stream, "Cp1251");
                    BufferedReader in = new BufferedReader(reader);
                    try {
                        //В цикле построчно считываем файл
                        String s;
                        while ((s = in.readLine()) != null) {
                            strBuffer.append(s);
                        }
                        String[] splitFile = strBuffer.toString().trim().split("(~\\|)");
                        
                        for (int i = 0; i < splitFile.length; i += 2) {
                            dictionary.put(Short.parseShort(splitFile[i + 1]), splitFile[i]);
                        }
                    } finally {
                        in.close();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {

            for (int i = 32; i < 127; i++) {
                dictionary.put(code, "" + (char) i);
                code++;
            }
            for (int i = 1040; i < 1104; i++) {
                dictionary.put(code, "" + (char) i);
                code++;
            }
            //добавление символа №
            dictionary.put(code, "" + (char) 8470);
            code++;
            //добавление символа ё
            dictionary.put(code, "" + (char) 1105);
            code++;
            //добавление символа Ё
            dictionary.put(code, "" + (char) 1025);
            code++;
            //добавление символа Ё
            dictionary.put(code, "" + (char) 171);
            code++;
            //добавление символа Ё
            dictionary.put(code, "" + (char) 187);
            code++;
            dictionary.put(code, "" + (char) 13);
            code++;
        }
        Short max = 0;
        for (Map.Entry<Short, String> entry : dictionary.entrySet()) {
            if (entry.getKey()> max) {
                max = entry.getKey();
            }
        }
        code = max;
        System.err.println(code);
        //извлечение первого символа
        int s = compressed.remove(0);
        String w = "";
        for (Map.Entry<Short, String> entry : dictionary.entrySet()) {
            if (entry.getKey() == s) {
                w = entry.getValue();
            }
        }

        StringBuffer result = new StringBuffer(w);
        for (int k : compressed) {
            String entry;
            if (dictionary.containsKey(k)) {
                entry = dictionary.get(k);
            } else if (k == code) {
                entry = w + w.charAt(0);
            } else {
                throw new IllegalArgumentException("Bad compressed k: " + k);
            }

            result.append(entry);
            // Add w+entry[0] to the dictionary.
            if (code != 4096) {
                dictionary.put(code++, w + entry.charAt(0));
            }
            w = entry;
        }

        return result.toString();
    }

    //считать изначальные данные
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

    //записать раскодированную последовательность
    public File writeFile(String fileName, String compressed) {
        File writeFile = new File(fileName);
        try {
            //проверяем, что если файл не существует то создаем его
            if (!writeFile.exists()) {
                writeFile.createNewFile();
            }

            //PrintWriter обеспечит возможности записи в файл
            PrintWriter out = new PrintWriter(writeFile.getAbsoluteFile(), "Cp1251");

            try {
                out.print(compressed);
                //Записываем текст у файл
                /*for (int i = 0; i < compressed.length(); i++) {
                 out.print(compressed[i]);
                 }*/
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

    //считать закодированную последовательность
    public List<Short> readCodeFile(String fileName) {
        List<Short> list = new ArrayList<Short>();
        try {
            // Wrap the FileInputStream with a DataInputStream
            FileInputStream file_input = new FileInputStream(fileName);
            DataInputStream data_in = new DataInputStream(file_input);

            while (true) {

                try {
                    list.add(data_in.readShort());
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

    //записать закодированную последовательость
    public DataOutputStream writeСodeFile(String fileName, List<Short> compressed) { //DataOutputStream
        DataOutputStream data_out = null;
        try {
            // Create an output stream to the file.
            FileOutputStream file_output = new FileOutputStream(fileName);
            // Wrap the FileOutputStream with a DataOutputStream
            data_out = new DataOutputStream(file_output);

            // Write the data to the file in an integer/double pair
            for (Short i : compressed) {
                data_out.writeShort(i);
            }
            // Close file when finished with it..
            file_output.close();
        } catch (IOException e) {
            System.out.println("IO exception = " + e);
        }
        return data_out;
        
        /*File writeFile = new File(fileName);
        try {
            //проверяем, что если файл не существует то создаем его
            if (!writeFile.exists()) {
                writeFile.createNewFile();
            }

            //PrintWriter обеспечит возможности записи в файл
            PrintWriter out = new PrintWriter(writeFile.getAbsoluteFile(), "Cp1251");

            try {
                out.print(compressed);
                //Записываем текст у файл
                for (int i : compressed) {
                out.print(compressed + " ");
            }
            } finally {
                //После чего мы должны закрыть файл
                //Иначе файл не запишется
                out.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return writeFile;*/
    }

    public String getFileName(String fullName) {
        char[] c = fullName.toCharArray();
        for (int i = 0; i < fullName.length(); i++) {
            if (c[i] == '.') {
                fullName = fullName.substring(0, i);
            }
        }
        return fullName;
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
        jCheckBox1 = new javax.swing.JCheckBox();
        jComboBox1 = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();

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

        jCheckBox1.setSelected(true);
        jCheckBox1.setText("Использовать таблицу кодов");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "банковская", "налоговая", "Item 3", "Item 4" }));

        jSeparator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton1))
                            .addComponent(jLabel3))
                        .addGap(0, 19, Short.MAX_VALUE))
                    .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton2)
                            .addComponent(jLabel2)
                            .addComponent(jButton2)
                            .addComponent(jRadioButton1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox1)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRadioButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jRadioButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addContainerGap(80, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    List<Short> compressed;

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
                System.out.println(compressed.size());
                String fileName = readFile.getParent() + "\\code_" + getFileName(readFile.getName()) + ".dat";
                DataOutputStream outputFile = writeСodeFile(fileName, compressed);

                jLabel3.setVisible(true);
                jLabel3.setText("Размер файла до комперессии " + readFile.length() + " байт. После компрессии " + outputFile.size() + " байт.");
                JOptionPane.showMessageDialog(jButton1, "Данные кодирования сохранены в файл code_" + getFileName(readFile.getName()) + ".dat");
            } else if (jRadioButton2.isSelected()) {
                List<Short> decodeSequence = readCodeFile(readFile.getAbsolutePath());
                String decompressed = decompress(decodeSequence);
                System.out.println(decompressed);
                
                String fileName = readFile.getParent() + "\\decode_" + getFileName(readFile.getName()) + ".txt";
                File outputFile = writeFile(fileName, decompressed);
                jLabel3.setVisible(true);
                jLabel3.setText("Размер файла до декомперессии " + readFile.length() + " байт. После декомпрессии " + outputFile.length() + " байт.");
                JOptionPane.showMessageDialog(jButton1, "Данные декодирования сохранены в файл decode_" + getFileName(readFile.getName()) + ".txt");
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
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    // End of variables declaration//GEN-END:variables
}
