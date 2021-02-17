

import org.json.simple.JSONObject;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;


public class ClientGUIFunctionalities extends JFrame {

    private JPanel contentPane;
    private JTextField Entry_word;
    private JTextField Entry_meaning;
    private JTextArea MsgBox;
    private ClientBackendThread clientBackendThread;

    /**
     * Create the frame.
     */
    public ClientGUIFunctionalities(ClientBackendThread communicator) {
        this.clientBackendThread = communicator;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JRadioButton rdbtnAdd = new JRadioButton("Add");

        JRadioButton rdbtnQuery = new JRadioButton("Query");

        JRadioButton rdbtnRemove = new JRadioButton("Remove");

        JRadioButton rdbtnAdditionalMeanings = new JRadioButton("Add additional meanings");

        ButtonGroup G = new ButtonGroup();

        G.add(rdbtnAdd);
        G.add(rdbtnQuery);
        G.add(rdbtnRemove);
        G.add(rdbtnAdditionalMeanings);


        Entry_word = new JTextField();
        Entry_word.setColumns(10);

        Entry_meaning = new JTextField();
        Entry_meaning.setColumns(10);

        JLabel lblWord = new JLabel("Word:");

        JLabel Label_meaning = new JLabel("Meaning:");


        JButton btnSubmit = new JButton("Submit");
        rdbtnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                Entry_word.setEnabled(true);
                Entry_meaning.setEnabled(true);
            }
        });
        rdbtnQuery.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                Entry_word.setEnabled(true);
                Entry_meaning.setEnabled(false);
            }
        });
        rdbtnRemove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                Entry_word.setEnabled(true);
                Entry_meaning.setEnabled(false);
            }
        });
        rdbtnAdditionalMeanings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e){
                Entry_word.setEnabled(true);
                Entry_meaning.setEnabled(true);
            }
        });
        btnSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.out.println("connected to server: " + communicator.connected);
                if (clientBackendThread.connected) { //some input errors will not needed to be sent to server to see whether it is a error, so just handle then in the client side

                    String word = Entry_word.getText();
                    boolean clearToSend;

                    if (word.trim().isEmpty()) {
                        MsgBox.setText("you cannot enter empty word!!!\n");
                        Entry_word.setText("");
                    }
                    else if (word.trim().matches("^[!@#$%^&*()\\[\\]_\\-+{}|><~?]+$")) {
                        MsgBox.setText("your word is Non-sense!!!\n");
                        Entry_word.setText("");
                    }

                    else {
                        clearToSend = false;

                        String meaning = Entry_meaning.getText();
                        String op = "";

                        if (rdbtnAdd.isSelected()) {

                            if (meaning.trim().isEmpty() ) {
                                MsgBox.setText("you cannot enter empty meaning!\n");
                                Entry_meaning.setText("");
                            } else if (meaning.trim().matches("^[!@#$%^&*()_+{}|\\[\\]\\-:;',.><~?]+$")) {
                                MsgBox.setText("your meaning is Non-sense!!\n");
                                Entry_meaning.setText("");
                            } else {
                                op = "ADD";
                                clearToSend = true;
                            }

                        }
                        else if (rdbtnQuery.isSelected()) {op = "QUERY"; clearToSend = true;}
                        else if (rdbtnRemove.isSelected()) {op = "REMOVE"; clearToSend = true;}
                        else if (rdbtnAdditionalMeanings.isSelected()) {

                            if (meaning.trim().isEmpty()) {
                                MsgBox.setText("you are not adding any meaning!\n");
                                Entry_meaning.setText("");
                            }
                            else if (meaning.trim().matches("^[!@#$%^&*()\\[\\]\\-_+{}|><~?]+$")) {
                                MsgBox.setText("your meaning added is Non-sense!!\n");
                                Entry_meaning.setText("");
                            }
                            else {
                                op = "ADDADDITIONAL";
                                clearToSend = true;
                            }
                        }
                        else {
                            MsgBox.setText("invalid option");
                        }

                        if (clearToSend) {

                            JSONObject sendObject = new JSONObject();
                            sendObject.put("op", op);
                            sendObject.put("word", word);
                            sendObject.put("meaning", meaning);
                            try {
                                clientBackendThread.send(sendObject.toString());
                                System.out.println("request sent");
                            } catch (Exception ex) {
                                clientBackendThread.connected = false;
                                MsgBox.setText("An error happen when requesting data from the server: " + ex.getMessage() + "\n");
                                MsgBox.append("Server not responding, please exit");

                            }
                        }

                    }

                } else {
                    MsgBox.setText("you are not connected to server, try again\n");
                }

            }
        });
        
        JScrollPane scrollPane = new JScrollPane();
        
        JButton btnTerminate = new JButton("Terminate");
        btnTerminate.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
                try {
                    clientBackendThread.termiate();
                    System.exit(0);
                } catch (IOException ioe){
                    System.out.println("Socket didn't close properly");
                    System.exit(0);
                }
        	}
        });




        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
        	gl_contentPane.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_contentPane.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
        				.addComponent(scrollPane, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 429, GroupLayout.PREFERRED_SIZE)
        				.addGroup(gl_contentPane.createSequentialGroup()
        					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
        						.addComponent(rdbtnQuery)
        						.addGroup(gl_contentPane.createSequentialGroup()
        							.addGap(21)
        							.addComponent(lblWord))
        						.addComponent(Label_meaning))
        					.addPreferredGap(ComponentPlacement.RELATED)
        					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
        						.addGroup(gl_contentPane.createSequentialGroup()
        							.addComponent(rdbtnAdd)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addComponent(rdbtnRemove, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
        							.addPreferredGap(ComponentPlacement.RELATED)
        							.addComponent(rdbtnAdditionalMeanings))
        						.addComponent(Entry_word, GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
        						.addComponent(Entry_meaning, GroupLayout.DEFAULT_SIZE, 353, Short.MAX_VALUE)))
        				.addGroup(gl_contentPane.createSequentialGroup()
        					.addGap(341)
        					.addComponent(btnSubmit))
        				.addComponent(btnTerminate, Alignment.TRAILING))
        			.addContainerGap())
        );
        gl_contentPane.setVerticalGroup(
        	gl_contentPane.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_contentPane.createSequentialGroup()
        			.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
        				.addComponent(rdbtnQuery)
        				.addComponent(rdbtnAdd, GroupLayout.PREFERRED_SIZE, 17, GroupLayout.PREFERRED_SIZE)
        				.addComponent(rdbtnRemove, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
        				.addComponent(rdbtnAdditionalMeanings))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
        				.addComponent(Entry_word, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
        				.addComponent(lblWord))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
        				.addComponent(Label_meaning)
        				.addComponent(Entry_meaning, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addPreferredGap(ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
        			.addComponent(btnTerminate)
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 105, GroupLayout.PREFERRED_SIZE)
        			.addGap(2)
        			.addComponent(btnSubmit))
        );
        
                MsgBox = new JTextArea();
                scrollPane.setViewportView(MsgBox);
                MsgBox.setColumns(10);
        contentPane.setLayout(gl_contentPane);
    }



    public void showText(String text){
        MsgBox.append(text);
    }
}
