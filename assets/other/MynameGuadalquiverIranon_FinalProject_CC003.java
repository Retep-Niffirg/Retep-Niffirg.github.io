package myname.guadalquiveriranon_finalproject_cc003;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.filechooser.*;
import org.json.simple.*;
import org.json.simple.parser.*;
import java.util.regex.*;

public class MynameGuadalquiverIranon_FinalProject_CC003 {

    static JFrame f = new JFrame("Lost and Found Management System");
    
    static String Columns [] = {"Date Requested/Found", "Item Name", "Description", "Requester", "Last Known Location", "Found?", "Retrieved?"}; //column names for the table
    static Pattern JsonCheck = Pattern.compile(".json", Pattern.CASE_INSENSITIVE); //check for ".json" file extension
    
    
    static DefaultTableModel Table1 = new DefaultTableModel(null, Columns) {
        @Override
            public boolean isCellEditable(int row, int column) { //every column will be uneditable except for "found?" and "retrieved?"
                if(ci.getState()) {//if "ci" checkbox is checked
                    return true;
                } else {
                    return column >= 5;
                }
            }
        @Override
            public Class getColumnClass(int Index) { //"found?" and "retrieved?" will be boolean checkboxes
                Class c = String.class;
                if (Index >= 5) {
                    return Boolean.class;
                }
                return c;
            }
            public boolean setCellSelectionEnabled () { 
                return true;
            }
    };
    
    static JTable MainTable = new JTable(Table1); //apply tablemodel to MainTable
    static int SelectR; //tracks which row is selected on MainTable
    static JButton DELETEROW = new JButton("DELETE ROW"); //delete button
    static JCheckBoxMenuItem ci = new JCheckBoxMenuItem("Make Table Editable"); //checkbox to make table editable or not
    
    static JSONArray Date = new JSONArray(); //Date Requested/Found
    static JSONArray Item = new JSONArray(); //Item Name
    static JSONArray Desc = new JSONArray(); //Description
    static JSONArray Name = new JSONArray(); //Requester
    static JSONArray Loc = new JSONArray(); //Last Known Location
    static JSONArray Found = new JSONArray(); //Found?
    static JSONArray Ret = new JSONArray(); //Retrieved?
   
    static ListSelectionListener ls = new ListSelectionListener() {
        @Override
            public void valueChanged(ListSelectionEvent e) {
                //check for which row is selected
                SelectR = MainTable.getSelectedRow();
                DELETEROW.setEnabled(true);

                if (SelectR < 0) { //disables button when index is -1 (default when nothing is selected)
                    DELETEROW.setEnabled(false);
                    DELETEROW.setText("DELETE ROW");
                } else {
                    DELETEROW.setText("DELETE ROW: " + SelectR);
                }
            }
    };   
    
    public static class LineWrapCellRenderer  extends JTextArea implements TableCellRenderer { //lets text in cells wrap when hitting edge
        @Override
        public Component getTableCellRendererComponent (JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                this.setText((String)value);
                this.setWrapStyleWord(true);
                this.setLineWrap(true);
                return this;
        }
    }  
    
    public static void SaveWindow() { //window for saving
        JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new FileNameExtensionFilter(".json, *.JSON", "json")); //filechooser will only allow json files to appear
        
        int Confirm = fc.showSaveDialog(f);
        if (Confirm == JFileChooser.APPROVE_OPTION) {
            
            for (int j = 0; j < MainTable.getRowCount(); j++) { //add all current table values to the JSONarrays
                Date.add(MainTable.getValueAt(j, 0));
                Item.add(MainTable.getValueAt(j, 1));
                Desc.add(MainTable.getValueAt(j, 2));
                Name.add(MainTable.getValueAt(j, 3));
                Loc.add(MainTable.getValueAt(j, 4));
                Found.add(MainTable.getValueAt(j, 5));
                Ret.add(MainTable.getValueAt(j, 6));         
            } 

            JSONObject o = new JSONObject(); //put everything into a JSONObject to be written to a file
            o.put("Date Requested", Date);
            o.put("Item Name", Item);
            o.put("Item Description", Desc);
            o.put("Requester", Name);
            o.put("Last Known Location", Loc);
            o.put("Found", Found);
            o.put("Retrieved", Ret);
            
            Matcher Filename = JsonCheck.matcher(String.valueOf(fc.getSelectedFile())); //regex to check if the user manually inputted ".json" in filename
            boolean IsJson = Filename.find();
            
            if (IsJson) { //if it has ".json"
                try (FileWriter w = new FileWriter(String.valueOf(fc.getSelectedFile()))) {
                    w.write(o.toString());
                    w.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } 
            } else { //if not
                try (FileWriter w = new FileWriter(fc.getSelectedFile() + ".json")) { //add ".json" to the end
                    w.write(o.toString());
                    w.flush();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            
            //clear arrays for the next save
            Date = new JSONArray();
            Item = new JSONArray();
            Desc = new JSONArray();
            Name = new JSONArray();
            Loc = new JSONArray();
            Found = new JSONArray();
            Ret = new JSONArray();
        }
    }
    
    public static void LoadWindow() { //jframe for saving
        JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new FileNameExtensionFilter(".json, *.JSON", "json")); //same as SaveWindow()
            
        int Confirm = fc.showOpenDialog(f);
        if (Confirm == JFileChooser.APPROVE_OPTION) {
            int Confirm2 = JOptionPane.showConfirmDialog(f, "Are you sure? (Unsaved progess will be lost.)", "Lost and Found", JOptionPane.YES_NO_OPTION); //fully confirm choice first
                switch (Confirm2) { // 0 - yes, 1 - no
                    case 0:
                        JSONParser Jp =  new JSONParser();
                        Matcher Filename = JsonCheck.matcher(String.valueOf(fc.getSelectedFile()));
                        boolean IsJson = Filename.find();

                        if (IsJson) { //if it is a json file
                            try (FileReader r = new FileReader(fc.getSelectedFile())) {
                                JSONObject o = (JSONObject)Jp.parse(r);

                                Date = (JSONArray)o.get("Date Requested"); //get the table values from the selected json file
                                Item = (JSONArray)o.get("Item Name");
                                Desc = (JSONArray)o.get("Item Description"); 
                                Name = (JSONArray)o.get("Requester");
                                Loc = (JSONArray)o.get("Last Known Location"); 
                                Found = (JSONArray)o.get("Found");
                                Ret = (JSONArray)o.get("Retrieved");
                                Table1.setRowCount(0); //clear table first

                                for (int i = 0; i < Date.size(); i++) { //get all table values from json arrays and put them in the table
                                        Object NewRow[] = {Date.get(i), Item.get(i), Desc.get(i), Name.get(i), Loc.get(i), Found.get(i), Ret.get(i)};
                                        Table1.addRow(NewRow);
                                    }
                            } catch (FileNotFoundException ex) {
                                JOptionPane.showMessageDialog(f, "This file does not exist!", "Lost and Found", JOptionPane.ERROR_MESSAGE);
                            } catch (IOException ex) {
                                ex.printStackTrace();
                            } catch (ParseException ex) {
                                JOptionPane.showMessageDialog(f, "This file is invalid.", "Lost and Found", JOptionPane.ERROR_MESSAGE);
                            }
                        } else { //if not
                            JOptionPane.showMessageDialog(f, "This file does not exist!", "Lost and Found", JOptionPane.ERROR_MESSAGE); //"does not exist" since it would be impossible to select a non-json file
                        }

                        //clear arrays again for next time
                        Date = new JSONArray(); 
                        Item = new JSONArray(); 
                        Desc = new JSONArray(); 
                        Name = new JSONArray();
                        Loc = new JSONArray(); 
                        Found = new JSONArray(); 
                        Ret = new JSONArray();  
                        break;
                        
                    case 1:
                        JOptionPane.showMessageDialog(f, "File was not loaded.", "Lost and Found", JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                }
    }
    
    public static void AddWindow() { //window for detail prompt
        JFrame f3 = new JFrame("New Item");
        JPanel p = new JPanel();
        
        Toolkit k = Toolkit.getDefaultToolkit(); 
        Image Icon = k.createImage("l&f.png");
        f3.setIconImage(Icon);
        
        JLabel l = new JLabel("Name of Requester:"); //labels for textboxes
        JLabel l2 = new JLabel("Item Name:");
        JLabel l3 = new JLabel("Item Description:");
        JLabel l4 = new JLabel("Current Date:");
        JLabel l5 = new JLabel("Last known location:");
        
        JTextField NameT = new JTextField(); //textfields for details
        JTextField ItemT = new JTextField();
        JTextArea DescT = new JTextArea();
        DescT.setLineWrap(true); //description textarea will wrap text if it reaches the edge
        DescT.setWrapStyleWord(true); //wrap by word instead of by char
        JTextField DateT = new JTextField();
        JTextField LocT = new JTextField(); //"T" indicates it is from a textfield/area
        
        JButton SUBMIT = new JButton("SUBMIT"); //submit
        SUBMIT.addActionListener(e -> {
            Object NewRow[] = {(DateT.getText()), (ItemT.getText()), (DescT.getText()), (NameT.getText()), "", false, false};
            if (LocT.getText().isBlank()) { //in case Location is left blank, replace with "N/A"
                NewRow[4] = ("N/A");
            } else {
                NewRow[4] = LocT.getText();
            }
            f3.setVisible(false);
            
            Table1.addRow(NewRow);
        });
        SUBMIT.setEnabled(false);
        
        DocumentListener d = new DocumentListener(){ //check if textfields are empty or not
            @Override
            public void changedUpdate(DocumentEvent e) {
              Empty();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
              Empty();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
              Empty();
            }

            public void Empty() {
               if (DateT.getText().isBlank() || ItemT.getText().isBlank() || DescT.getText().isBlank() || NameT.getText().isBlank()) { //check if any field is blank (except for location)
                    SUBMIT.setEnabled(false); //disable submit button if blank
                } else {
                    SUBMIT.setEnabled(true); //enable if not
                }
            }
        };
        
        //adds the textfield empty check
        NameT.getDocument().addDocumentListener(d); 
        ItemT.getDocument().addDocumentListener(d);
        DescT.getDocument().addDocumentListener(d);
        DateT.getDocument().addDocumentListener(d);
        
        GroupLayout Group = new GroupLayout(p); //setting up the panel layout
            Group.setAutoCreateGaps(true);
            Group.setAutoCreateContainerGaps(true);
            p.setLayout(Group);
            Group.setHorizontalGroup(
                Group.createSequentialGroup()
                    .addGroup(Group.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(l)
                        .addComponent(l2)
                        .addComponent(l3)
                        .addComponent(l4)
                        .addComponent(l5))
                    .addGroup(Group.createParallelGroup(GroupLayout.Alignment.TRAILING)
                        .addComponent(NameT)   
                        .addComponent(ItemT)
                        .addComponent(DescT)
                        .addComponent(DateT)
                        .addComponent(LocT) 
                        .addComponent(SUBMIT))
            );
            Group.setVerticalGroup(
                Group.createSequentialGroup()
                    .addGroup(Group.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        .addComponent(l)
                        .addComponent(NameT))
                    .addGroup(Group.createParallelGroup(GroupLayout.Alignment.BASELINE)    
                        .addComponent(l2)
                        .addComponent(ItemT))
                    .addGroup(Group.createParallelGroup(GroupLayout.Alignment.BASELINE) 
                        .addComponent(l3)
                        .addComponent(DescT))
                        .addGap(10)
                    .addGroup(Group.createParallelGroup(GroupLayout.Alignment.BASELINE)    
                        .addComponent(l4)
                        .addComponent(DateT))
                    .addGroup(Group.createParallelGroup(GroupLayout.Alignment.BASELINE)    
                        .addComponent(l5)
                        .addComponent(LocT))
                    .addGroup(Group.createParallelGroup(GroupLayout.Alignment.TRAILING)   
                        .addComponent(SUBMIT))
                    
            );
        
        f3.add(p);
        f3.setSize(400, 300);
        f3.setLocationRelativeTo(f); //makes this frame appear in the center of the main one
        f3.setVisible(true);
    }
    
    public static void AboutWindow() {
        JOptionPane.showMessageDialog(f, "Created by:\n     ME\n     Johanna Ezra Iranon\n     Art Xenos D. Guadalquiver\n\nWith a lot of help from stackoverflow.com", "About Lost and Found", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void Frame() {
        f.setSize(1280, 720);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null); //jframe will appear in the center
        
        Toolkit k = Toolkit.getDefaultToolkit(); 
        Image Icon = k.createImage("l&f.png");
        f.setIconImage(Icon);
    }
    
    public static void Table() {
        MainTable.setRowHeight(100);
        MainTable.getSelectionModel().addListSelectionListener(ls);
        MainTable.setDefaultRenderer(String.class, new LineWrapCellRenderer()); //applies the line wrap onto the cells
        MainTable.setAutoCreateRowSorter(true);  //when the headers are clicked, the colums will be sorted alphabetically
        
        
        JScrollPane s = new JScrollPane(MainTable);
        f.add(s, BorderLayout.CENTER);
    }
    
    public static void MenuBar(){
        JMenuBar M = new JMenuBar();
        JMenu m = new JMenu("File"); // FILE MENU
        
        JMenuItem i = new JMenuItem("New Table");
        i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK)); //Ctrl + N
        i.addActionListener((ActionEvent e) -> {    
            int Confirm = JOptionPane.showConfirmDialog(f, "Are you sure? (Unsaved progress will be lost.)", "Lost and Found", JOptionPane.YES_NO_OPTION); 
            switch (Confirm) { // 0 - yes, 1 - no
                case 0: 
                   Table1.setRowCount(0);
            }
        });
        m.add(i);
        
        i = new JMenuItem("Save to JSON");
        i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK)); //Ctrl + S
        i.addActionListener((ActionEvent e) -> {    
            SaveWindow(); //refer to this method for an in-depth explanation
        });
        m.add(i);
        
        i = new JMenuItem("Load from JSON");
        i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK)); //Ctrl + O
        i.addActionListener((ActionEvent e) -> {
            LoadWindow(); //refer to this method for an in-depth explanation
            });
        m.add(i); 
        m.addSeparator();
        
        i = new JMenuItem("Exit");
        i.addActionListener((ActionEvent e) -> {
            int Confirm = JOptionPane.showConfirmDialog(f, "Are you sure? (Unsaved progress will be lost.)", "Lost and Found", JOptionPane.YES_NO_OPTION); 
            switch (Confirm) { // 0 - yes, 1 - no
                case 0: 
                   System.exit(0);
            }
        });         
        m.add(i); 
        
        M.add(m);
        
        m = new JMenu("Edit"); //EDIT MENU
        
        i = new JMenuItem("Add New Row");
        i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK + ActionEvent.SHIFT_MASK)); //Ctrl + Shift + A
        i.addActionListener(e -> {
            AddWindow(); //refer to this method for an in-depth explanation
        });
        m.add(i);
        
        i = new JMenuItem("Delete Selected Row");
        i.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0)); //Del
        i.addActionListener(e -> { //this is here just to add a hotkey for deleting rows
            DELETEROW.doClick();
        });        
        m.add(i);
        
        m.addSeparator();
        
        ci.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.SHIFT_MASK)); //Shift + E
        m.add(ci); //checkbox is added here
        
        M.add(m);
        
        m = new JMenu("About");
        i = new JMenuItem("About Lost and Found Management System");
        i.addActionListener((ActionEvent e) -> {
            AboutWindow(); //refer to this method for an in-depth explanation
        });  
        m.add(i);
        
        M.add(m);
        f.setJMenuBar(M);
    }
    
    public static void ToolBar() {
        JToolBar t = new JToolBar();
        t.setFloatable(false);

        JLabel l = new JLabel ("Search Current Table: ");
        t.add(l);
        t.addSeparator();
        
        JTextField tf = new JTextField();
        DocumentListener d = new DocumentListener(){//checks when the textfields are updated
            @Override
            public void changedUpdate(DocumentEvent e) {
                Search();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                Search();
            }
            @Override
            public void insertUpdate(DocumentEvent e) {
                Search();
            }

            public void Search() {
                TableRowSorter s = new TableRowSorter(Table1);
                String search = ((tf.getText()));
                s.setRowFilter((RowFilter.regexFilter("(?i)" + search))); //"(?i)" makes the regex filter case-insensitive
                MainTable.setRowSorter(s);
            }
        };
        tf.getDocument().addDocumentListener(d);
        t.add(tf);
        t.addSeparator();
        t.addSeparator();
        
        DELETEROW.addActionListener((ActionEvent e) -> {
            Table1.removeRow(MainTable.convertRowIndexToModel(SelectR)); //deletes a single selected row
        });       
        DELETEROW.setEnabled(false);
        t.add(DELETEROW); 
        
        f.add(t, BorderLayout.PAGE_END);
    };
    

    
    public static void main(String[] args) {
        Frame();
        Table();
        MenuBar();
        ToolBar();

        f.setVisible(true);   
    }
    
}
