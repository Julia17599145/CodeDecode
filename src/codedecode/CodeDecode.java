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
    }

    //StringBuilder readBuffer = new StringBuilder();
    //StringBuilder writeBuffer = new StringBuilder();
    File readFile = new File("");

    public String[] readTable() {
        String[] splitFile = {};
        File file = new File("../CodeDecode/bankcode.txt");
        StringBuilder strBuffer = new StringBuilder();
        if (file.exists() && file.length() != 0) {
            try {
                FileInputStream stream = new FileInputStream(file.getAbsoluteFile());
                InputStreamReader reader = new InputStreamReader(stream, "Cp1251");
                BufferedReader in = new BufferedReader(reader);
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        strBuffer.append(line);
                    }
                    splitFile = strBuffer.toString().trim().split("(~\\|)");
                } finally {
                    in.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return splitFile;
    }

    public Map<String, Short> startCodeTable() {
        Map<String, Short> dictionary = new HashMap<String, Short>();
        short code = 0;
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
        dictionary.put("" + (char) 182, code++);
        return dictionary;
    }

    public List<Short> compress(StringBuilder readBuffer) {
        // заполнение словаря
        Map<String, Short> dictionary = new HashMap<String, Short>();
        short code = 0;
        if (jCheckBox1.isSelected()) {
            String[] splitFile = readTable();
            if (splitFile.length == 0) {
                dictionary = startCodeTable();
                JOptionPane.showMessageDialog(jButton1, "Файл не найден! Кодирование будет выполнено стандартной таблицей");
            } else {
                for (int i = 0; i < splitFile.length; i += 2) {
                    dictionary.put(splitFile[i], Short.parseShort(splitFile[i + 1]));
                }
            }

        } else {
            dictionary = startCodeTable();
        }
        short max = 0;
        for (Map.Entry<String, Short> entry : dictionary.entrySet()) {
            if (entry.getValue() > max) {
                max = entry.getValue();
            }
        }
        code = max;
        String previousLine = "";
        //String currentLine = "";
        List<Short> result = new ArrayList<Short>();
        if (jCheckBox3.isSelected()) {
            char[] sequence = readBuffer.toString().toCharArray();
            for (int i = 0; i < sequence.length; i++) {
                String currentLine = previousLine + sequence[i];
                if (dictionary.containsKey(currentLine)) {
                    previousLine = currentLine;
                } else {
                    int count = 30;
                    int j = 0;
                    String match = previousLine;
                    int numberMatch = 0;
                    while (count > 0) {
                        if (i + j < sequence.length - 1) {
                            currentLine = previousLine + sequence[i + j];
                            if (dictionary.containsKey(currentLine)) {
                                previousLine = currentLine;
                                match = previousLine;
                                j++;
                                numberMatch = j;
                            } else {
                                previousLine = currentLine;
                                j++;
                                count--;
                            }
                        } else {
                            break;
                        }
                    }
                    result.add(dictionary.get(match));
                    if (code != 4096) {
                        String newStr = match + sequence[i + numberMatch];
                        dictionary.put(newStr, code++);
                        
                    }
                    i += numberMatch;
                    previousLine = "" + sequence[i];
                }
            }
            if (!previousLine.equals("")) {
                result.add(dictionary.get(previousLine));
            }
        } else {
            for (char simbol : readBuffer.toString().toCharArray()) {
                String currentLine = previousLine + simbol;
                if (dictionary.containsKey(currentLine)) {
                    previousLine = currentLine;
                } else {
                    result.add(dictionary.get(previousLine));
                    // Add wc to the dictionary.
                    System.out.println("here");
                    if (code != 4096) {
                        dictionary.put(currentLine, code++);
                        System.out.println(code);
                    }
                    previousLine = "" + simbol;
                }
            }
            if (!previousLine.equals("")) {
                result.add(dictionary.get(previousLine));
            }
        }
        return result;
    }

    public Map<Short, String> startDecodeTable() {
        Short code = 0;
        Map<Short, String> dictionary = new HashMap<Short, String>();
        for (int i = 32; i < 127; i++) {
            dictionary.put(code++, "" + (char) i);
        }
        for (int i = 1040; i < 1104; i++) {
            dictionary.put(code++, "" + (char) i);
        }
        //добавление символа №
        dictionary.put(code++, "" + (char) 8470);
        //добавление символа ё
        dictionary.put(code++, "" + (char) 1105);
        //добавление символа Ё
        dictionary.put(code++, "" + (char) 1025);
        //добавление символа Ё
        dictionary.put(code++, "" + (char) 171);
        //добавление символа Ё
        dictionary.put(code++, "" + (char) 187);
        //перенос
        dictionary.put(code++, "" + (char) 182);
        return dictionary;
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
            String[] splitFile = readTable();

            for (int i = 0; i < splitFile.length; i += 2) {
                dictionary.put(Short.parseShort(splitFile[i + 1]), splitFile[i]);
            }
        } else {
            dictionary = startDecodeTable();
        }
        Short max = 0;
        for (Map.Entry<Short, String> entry : dictionary.entrySet()) {
            if (entry.getKey() > max) {
                max = entry.getKey();
            }
        }
        code = max;
        //извлечение первого символа
        int s = compressed.remove(0);
        String previousLine = "";
        for (Map.Entry<Short, String> entry : dictionary.entrySet()) {
            if (entry.getKey() == s) {
                previousLine = entry.getValue();
            }
        }

        StringBuffer result = new StringBuffer(previousLine);
        for (Short k : compressed) {
            String entry;
            if (dictionary.containsKey(k)) {
                entry = dictionary.get(k);
            } else if (k == code) {
                entry = previousLine + previousLine.charAt(0);
            } else {
                throw new IllegalArgumentException("Bad compressed k: " + k);
            }

            result.append(entry);
            if (code != 4096) {
                dictionary.put(code++, previousLine + entry.charAt(0));
            }
            previousLine = entry;
        }

        return result.toString();
    }

    //считать данные для кодирования
    public StringBuilder readFile(String fileName) {
        StringBuilder readBuffer = new StringBuilder();
        try {
            FileInputStream stream = new FileInputStream(fileName);
            InputStreamReader reader = new InputStreamReader(stream, "Cp1251");
            BufferedReader in = new BufferedReader(reader);
            try {
                String line;
                while ((line = in.readLine()) != null) {
                    readBuffer.append(line);
                }
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return readBuffer;
    }

    //записать раскодированную последовательность
    public File writeFile(String fileName, String compressed) {
        File writeFile = new File(fileName);
        try {
            if (!writeFile.exists()) {
                writeFile.createNewFile();
            }
            PrintWriter out = new PrintWriter(writeFile.getAbsoluteFile(), "Cp1251");
            try {
                out.print(compressed);
            } finally {
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
            FileInputStream file_input = new FileInputStream(fileName);
            DataInputStream data_in = new DataInputStream(file_input);
            while (true) {
                try {
                    list.add(data_in.readShort());
                } catch (EOFException eof) {
                    break;
                };
            }
            data_in.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    //записать закодированную последовательость
    public DataOutputStream writeСodeFile(String fileName, List<Short> compressed) {
        DataOutputStream data_out = null;
        try {
            FileOutputStream file_output = new FileOutputStream(fileName);
            data_out = new DataOutputStream(file_output);

            for (int i = 0; i < compressed.size(); i++) {
                //System.out.println(i + " " + compressed.get(i));
                data_out.writeShort(compressed.get(i));
            }
            file_output.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return data_out;
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
        jCheckBox3 = new javax.swing.JCheckBox();

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

        jCheckBox3.setSelected(true);
        jCheckBox3.setText("Использовать опережающий просмотр");

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
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jSeparator3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jSeparator2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jRadioButton2)
                            .addComponent(jLabel2)
                            .addComponent(jButton2)
                            .addComponent(jRadioButton1))
                        .addGap(39, 39, 39)
                        .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox1)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jCheckBox3))
                        .addGap(0, 0, 0)))
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
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(14, 14, 14)
                        .addComponent(jCheckBox3))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addContainerGap(80, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        //readFile();
        int returnVal = jFileChooser1.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            readFile = jFileChooser1.getSelectedFile();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        if (!readFile.exists()) {
            JOptionPane.showMessageDialog(jButton1, "Вы не выбрали файл!");
            return;
        } else {
            if (jRadioButton1.isSelected()) {
                StringBuilder readBuffer = readFile(readFile.getAbsolutePath());
                List<Short> compressed = compress(readBuffer);
                String fileName = readFile.getParent() + "\\code_" + getFileName(readFile.getName()) + ".dat";
                DataOutputStream outputFile = writeСodeFile(fileName, compressed);

                jLabel3.setVisible(true);
                jLabel3.setText("Размер файла до комперессии " + readFile.length() + " байт. После компрессии " + outputFile.size() + " байт.");
                JOptionPane.showMessageDialog(jButton1, "Данные кодирования сохранены в файл code_" + getFileName(readFile.getName()) + ".dat");
            } else if (jRadioButton2.isSelected()) {
                List<Short> decodeSequence = readCodeFile(readFile.getAbsolutePath());
                String decompressed = decompress(decodeSequence);
                String fileName = readFile.getParent() + "\\decode_" + getFileName(readFile.getName()) + ".txt";
                File outputFile = writeFile(fileName, decompressed);

                jLabel3.setVisible(true);
                jLabel3.setText("Размер файла до декомперессии " + readFile.length() + " байт. После декомпрессии " + outputFile.length() + " байт.");
                JOptionPane.showMessageDialog(jButton1, "Данные декодирования сохранены в файл decode_" + getFileName(readFile.getName()) + ".txt");
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
    private javax.swing.JCheckBox jCheckBox3;
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
