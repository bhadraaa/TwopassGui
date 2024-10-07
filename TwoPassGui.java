import java.io.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;

class TwoPassAssembler {
    private static JTextArea text;
    private static File inputFile = new File("input.txt");
    private static File optabFile = new File("optab.txt");
    private static File symtabFile = new File("symtab.txt");
    private static File intermediateFile = new File("intermediate.txt");
    private static File objcodw = new File("object_code.txt");
    private static String op="ADD-18\nAND-40\nCOMP-28\nDIV-24\nJ-3C\nJEQ-30\nJGT-34\nJLT-38\nJSUB-48\nLDA-00\nLDCH-50\nLDL-08\nLDX-0\nMUL-20\nOR-44\nRD-D8\nRSUB-4C\nSTA-0C\nSTCH-54\nSTL-14\nSTSW-E8\nSTX-10\nSUB-1C\nTD-E0\nTIX-2C\nWD-DC\nEND-*";
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TwoPassAssembler::new);
    }

    public TwoPassAssembler() {
        JFrame frame1 = new JFrame("Assembler");
        frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame1.setSize(1300, 750);
        frame1.setLayout(null);

        frame1.getContentPane().setBackground(new Color(20,20,34));

        JLabel title = new JLabel("<html><u>Two Pass Assembler</u></html>", SwingConstants.CENTER);
        title.setBounds(500, 1, 300, 100);
        title.setForeground(new Color(255, 255, 255));
        title.setFont(new Font("Lucida Console", Font.BOLD, 24));
        frame1.add(title);

        JLabel inputL = new JLabel("Input Source Code");
        inputL.setBounds(80, 75, 250, 50);
        inputL.setForeground(new Color(255, 255, 255));
        inputL.setFont(new Font("Lucida Console", Font.BOLD, 20));
        frame1.add(inputL );
        
        JTextArea input = new JTextArea();
        input.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        input.setBackground(new Color(26, 29, 35));
        input.setForeground(new Color(255, 255, 255));
        input.setFont(new Font("Lucida Console", Font.PLAIN, 16));
        input.setCaretColor(Color.WHITE); 
        JScrollPane inputS = new JScrollPane(input);
        inputS.setBounds(70, 120, 350, 450);
        input.setToolTipText("Provide the source code structured as label, opcode, and operand, with tab spaces between each element");
        frame1.add(inputS);

        JLabel outputL = new JLabel("Object Code");
        outputL.setBounds(700, 95, 250, 50);
        outputL.setForeground(new Color(255, 255, 255));
        outputL.setFont(new Font("Lucida Console", Font.BOLD, 20));
        frame1.add(outputL);
        JTextArea output = new JTextArea();
        output.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        output.setBounds(630, 140, 650, 350);
        output.setBackground(new Color(26,29,35));
        output.setForeground(new Color(255, 255, 255));
        output.setFont(new Font("Lucida Console", Font.PLAIN, 16));
        output.setEditable(false);
        frame1.add(output);

        JButton assemB = new JButton("Assemble");
        assemB.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        assemB.setBounds(140,600, 200, 40);
        assemB.setBackground(new Color(231, 76, 60));
        assemB.setForeground(new Color(0, 0, 0));
        assemB.setToolTipText("Click the button to assemble the code.");
        frame1.add(assemB);

        JButton interB = new JButton("Intermediate Text");
        interB.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
         interB.setBounds(450, 160, 150, 40);
         interB.setBackground(new Color(155, 89, 182));
         interB.setForeground(new Color(0, 0 ,0));
         interB.setToolTipText("Click To View Contents of intermediate File");
        frame1.add( interB);

        JButton symB = new JButton("Symtab");
        symB.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        symB.setBounds(450, 230, 150, 40);
        symB.setBackground(new Color(155, 89, 182));
        symB.setForeground(new Color(0, 0, 0));
        symB.setToolTipText("Click To View Contents of Symbol table");
        frame1.add(symB);

        JButton opB = new JButton("Optab");
        opB.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        opB.setBounds(450, 300, 150, 40);
        opB.setBackground(new Color(155, 89, 182));
        opB.setForeground(new Color(0, 0, 0));
        opB.setToolTipText("Click To View Opcode and Mneumonic");
        frame1.add(opB);

        JButton clrB = new JButton("Clear");
        clrB.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
         clrB.setBounds(800, 600, 200, 40);
        clrB.setBackground(new Color(231, 76, 60));
        clrB.setForeground(new Color(0,0, 0));
        clrB.setToolTipText("Click To clear all fields and Reassemble");
        frame1.add(clrB);

        effect1(assemB);
        effect1(clrB);
        effect1(opB);
        effect1(symB);
        effect1(interB);

        effect2(output);
        effect2(input);

        
       assemB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                

                try (BufferedWriter inputWriter = new BufferedWriter(new FileWriter(inputFile))) {
                    String inputCode = input.getText();
                    if(inputCode==null){
                    output.setText(" ");
                    }
                    inputWriter.write(inputCode);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try (BufferedWriter opcodeWriter = new BufferedWriter(new FileWriter(optabFile))) {
                     opcodeWriter.write(op+" ");
                 } catch (IOException ex) {
                    ex.printStackTrace();
                }
                try{
                String label, opcode, operand, code, mnemonic;
                label = opcode = operand = code = mnemonic = "";
                int programLength = passOne(label, opcode, operand, code, mnemonic);
                passTwo(label, opcode, operand, code, mnemonic, programLength);
                } catch (IOException d){
                    d.printStackTrace();
                }
                try (BufferedReader reader = new BufferedReader(new FileReader(objcodw))) {
                    StringBuilder fileContent = new StringBuilder();
                    String objcode;

                    while ((objcode = reader.readLine()) != null) {
                        fileContent.append(objcode).append(System.lineSeparator());
                    }

                   output.setText(fileContent.toString());
                } catch (IOException f) {
                    f.printStackTrace();
                }

            }
        });

        interB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFile(intermediateFile, "Intermediate Text", frame1);
            }
        });

        symB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showFile(symtabFile, "Symbol Table", frame1);
            }
        });

        opB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 Dialog opD = new JDialog(frame1, "Opcode Table", true);
                JTextArea opT = new JTextArea(10, 40);
                opT.setText(op+" ");
                JScrollPane scrollPane = new JScrollPane(opT);
                opT.setEditable(false);
                opT.setFont(new Font("Lucida Console", Font.PLAIN, 16));
                opD.add(scrollPane);
                opD.setSize(400, 300);
                opD.setLocationRelativeTo(frame1);
                opD.setVisible(true);
            }
        });

        clrB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    String inputCode ;
                    String inputCode1;
                    if((inputCode = input.getText())!=null||(inputCode1 =output.getText())!=null){
                        input.setText("");
                        output.setText("");
                    }
                }
                catch(NullPointerException g){
                    g.printStackTrace();
                }

            }
        });

        frame1.setVisible(true);
    }
    public static void effect1(JButton button) {
        Color originalColor = button.getBackground(); 

        // Add a mouse listener for hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color( 176,224, 230)); 
                button.setBorder(BorderFactory.createLineBorder(new Color(179, 177, 137)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor); 
                button.setBorder(BorderFactory.createLineBorder(new Color(0, 0, 0)));
            }
        });
    }
    public static void effect2(JTextArea area) {
        Color originalColor = area.getBackground(); 
        area.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                area.setBackground(new Color( 20,20, 54)); 
                area.setBorder(BorderFactory.createLineBorder(new Color(179, 177, 137)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                area.setBackground(originalColor); 
                area.setBorder(BorderFactory.createLineBorder(new Color(0,0,0)));
            }
        });
    }

    private void showFile(File file, String title, JFrame parent) {
        JDialog dialog = new JDialog(parent, title, true);
        text = new JTextArea(10, 40);
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            text.setText(content.toString());
        } catch (IOException ex) {
            text.setText("Error reading file: " + ex.getMessage());
        }
        JScrollPane scroll = new JScrollPane(text);
        dialog.add(scroll);
        dialog.setSize(400, 300);
        text.setFont(new Font("Lucida Console", Font.PLAIN, 16));
        text.setEditable(false);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

  public static int passOne(String label, String opcode, String operand, String code, String mnemonic) throws IOException {
    int locctr = 0, start = 0, length;
    boolean found;
    try{
        Scanner inputS = new Scanner(inputFile);
        BufferedWriter symW = new BufferedWriter(new FileWriter(symtabFile, false));
        BufferedWriter interW = new BufferedWriter(new FileWriter(intermediateFile, false));

        label = inputS.next();
        opcode = inputS.next();
        operand = inputS.next();

        if (opcode.equals("START")) {
            start = Integer.parseInt(operand, 16); 
            locctr = start;
            interW.write(label + "\t" + opcode + "\t" + operand + "\n");

            if (inputS.hasNext()) {
                label = inputS.next();
                opcode = inputS.next();
                operand = inputS.next();
            }
        }
        while (!opcode.equals("END")) {
            interW.write(Integer.toHexString(locctr).toUpperCase() + "\t" + label + "\t" + opcode + "\t" + operand + "\n");

            if (!label.equals("**")) {
                symW.write(label + "\t" + Integer.toHexString(locctr).toUpperCase() + "\n");
            }

            found = false;

            try (Scanner opS = new Scanner(optabFile)) {
                while (opS.hasNextLine()) {
                    String line = opS.nextLine();
                    String[] parts = line.split("-");
                    if (parts.length >= 2) {
                        code = parts[0];
                        mnemonic = parts[1];

                        if (opcode.equals(code)) {
                            locctr += 3;
                            found = true;
                            break;
                        }
                    }
                }
            }

            if (!found) {
                if (opcode.equals("WORD")) {
                    locctr += 3;
                } else if (opcode.equals("RESW")) {
                    locctr += 3 * Integer.parseInt(operand);
                } else if (opcode.equals("BYTE")) {
                    if (operand.charAt(0) == 'C') {
                        locctr += (operand.length() - 3); 
                    } else if (operand.charAt(0) == 'X') {
                        locctr += (operand.length() - 3) / 2;  
                    }
                } else if (opcode.equals("RESB")) {
                    locctr += Integer.parseInt(operand);
                }
            }

            if (inputS.hasNext()) {
                label = inputS.next();
                opcode = inputS.next();
                operand = inputS.next();
            }
        }

        interW.write(Integer.toHexString(locctr).toUpperCase() + "\t" + label + "\t" + opcode + "\t" + operand + "\n");
        inputS.close();
        symW.close();
        interW.close();
    }
    catch (NoSuchElementException ex) {
            JOptionPane.showMessageDialog(null, "Error processing input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
    catch (NullPointerException x) {
        x.printStackTrace();
    }

    length =  locctr - start;

    return length;
}



public static void passTwo(String label, String opcode, String operand, String code, String mnemonic, int programLength) throws IOException {
    String objcode = "", sym_addr;
    String start_addr = "", addr = " ";
    String name = "";
    String text_record = "";
    int text_len = 0, count = 0, tlength = 0;

    BufferedReader br1 = new BufferedReader(new FileReader(intermediateFile));
    BufferedReader br2 = new BufferedReader(new FileReader(optabFile));
    BufferedReader br3 = new BufferedReader(new FileReader(symtabFile));
    BufferedWriter objW = new BufferedWriter(new FileWriter("object_code.txt"));
    String[] parts;
    String line = br1.readLine();
    if (line != null && !line.trim().isEmpty()) {
        parts = line.split("\t");

        label = parts[0];
        opcode = parts[1];
        operand = parts[2];

        if (opcode.equals("START")) {
            name = label;
            start_addr = operand;
            String lengthInHex = String.format("%02X", programLength);
            objW.write("\n\nH^" + name + "^00" + start_addr + "^" + lengthInHex + "\n\n");
            line = br1.readLine();
            parts = line.split("\t");
            start_addr = parts[0];
            label = parts[1];
            opcode = parts[2];
            operand = parts[3];
        }

        text_record = "T^00" + start_addr;
        int len = 0;

        while (!opcode.equals("END")) {
            if (opcode.charAt(0) != '.') {
                br2 = new BufferedReader(new FileReader("optab.txt"));
                boolean found = false;

                while ((line = br2.readLine()) != null) {
                    parts = line.split("-");
                    if (parts.length >= 2) {
                        code = parts[0];
                        mnemonic = parts[1];
                        if (opcode.equals(code)) {
                            found = true;
                            break;
                        }
                    }
                }

                if (found) {
                    if (!operand.equals("-")) {
                        br3 = new BufferedReader(new FileReader("symtab.txt"));
                        while ((line = br3.readLine()) != null) {
                            parts = line.split("\t");
                            if (operand.equals(parts[0])) {
                                operand = parts[1];
                                break;
                            }
                        }
                    } else {
                        operand = "00";
                    }
                    objcode = mnemonic + operand;
                } else if (opcode.equals("BYTE")) {
                    if (operand.startsWith("C")) {
                        StringBuilder str = new StringBuilder();
                        for (int i = 2; i < operand.length() - 1; i++) {
                            str.append(String.format("%02X", (int) operand.charAt(i)));
                        }
                        objcode = str.toString(); 
                        len += operand.length() - 3;
                    } else if (operand.startsWith("X")) {
                        objcode = operand.substring(2, operand.length() - 1);
                        len += objcode.length() / 2;
                    }
                } else if (opcode.equals("WORD")) {
                    objcode = String.format("%06X", Integer.parseInt(operand)); 
                    len += 3;
                } else {
                    objcode = "";
                }

                if (len + objcode.length() / 2 > 36 ||count == 6) {
                    text_record += "^" + String.format("%02X", tlength / 2);
                    objW.write(formatTex(text_record));
                    text_record = "\nT^00" + addr;
                    len = 0;
                    count = 0;
                    tlength = 0;
                }

                text_record += "^" + objcode;
                tlength += objcode.length();
                count++;
            }
            line = br1.readLine();
            if (line != null) {
                parts = line.split("\t");
                addr = parts[0];
                label = parts[1];
                opcode = parts[2];
                operand = parts[3];
            }
        }

        if (count > 0) {
            text_record += "^" + String.format("%02X", tlength / 2);
        }
        objW.write(formatTex(text_record));
        objW.write("\n\n\nE^00" + start_addr + "\n");

        br1.close();
        br2.close();
        br3.close();
        objW.flush();
        objW.close();
    }
}

public static String formatTex(String input) {
    if (input.length() > 0) {
        String lastTwo = input.substring(input.length() - 2);
        String part0 = input.substring(0, 9);
        String part1 = input.substring(9);
        String part2 = part1.substring(0, part1.lastIndexOf('^'));
        return part0 + " ^ " + lastTwo + " ^ " + part2;
    }
    return "";
}

}
