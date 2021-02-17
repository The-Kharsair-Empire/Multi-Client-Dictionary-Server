
import org.json.simple.parser.ParseException;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;

public class ServerGUIStartUp extends JFrame {

    private JPanel contentPane;
    private JTextField entry_PortNumber;
    private JTextField entry_DictionaryFile;
    private JTextField entry_WorkerPoolSize;
    private JTextField TextDisplay;
    private JButton btnStopServer;
    private JButton btnStartServer;
    private WorkerPoolManager workerPoolManager;


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    ServerGUIStartUp frame = new ServerGUIStartUp();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public ServerGUIStartUp() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JLabel lblPortNumber = new JLabel("Port Number:");

        entry_PortNumber = new JTextField();
        entry_PortNumber.setColumns(10);

        JLabel Label_DictionaryFile = new JLabel("Dictionary File Name:");

        entry_DictionaryFile = new JTextField();
        entry_DictionaryFile.setColumns(10);

        JLabel Label_WorkerPoolSize = new JLabel("Worker Pool Size:");

        entry_WorkerPoolSize = new JTextField();
        entry_WorkerPoolSize.setColumns(10);

        TextDisplay = new JTextField();
        TextDisplay.setColumns(10);

        btnStartServer = new JButton("Start Server");
        btnStartServer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println(entry_DictionaryFile.getText()); //if -else check whether these input are valid, these checks doesn't need to test the error and can be don't on client side
                    String portNumberString = entry_PortNumber.getText();
                    String WorkerPoolSizeString = entry_WorkerPoolSize.getText();
                    if ((! portNumberString.matches("-?(0|[1-9]\\d*)") ) ||  portNumberString.length() > 5) {
                        TextDisplay.setText("enter valid number port number !!");
                        entry_PortNumber.setText("");
                    } else if (Integer.parseInt(portNumberString) > 65535 || Integer.parseInt(portNumberString) <= 0) {
                        TextDisplay.setText("Port number out of range !!");
                        entry_PortNumber.setText("");
                    }
                    else if ( ! WorkerPoolSizeString.matches("-?(0|[1-9]\\d*)")){
                        TextDisplay.setText("enter number for worker size!!");
                        entry_WorkerPoolSize.setText("");
                    } else if (Integer.parseInt(WorkerPoolSizeString) > 20){
                        TextDisplay.setText("Worker size too large, max 20!!");
                        entry_WorkerPoolSize.setText("");
                    } else if (Integer.parseInt(WorkerPoolSizeString) < 0) {
                        TextDisplay.setText("Negative worker size???!! you sure?");
                        entry_WorkerPoolSize.setText("");
                    } else if (Integer.parseInt(WorkerPoolSizeString) == 0) {
                        TextDisplay.setText("you will need some worker");
                        entry_WorkerPoolSize.setText("");
                    }
                    else {
                        workerPoolManager = new WorkerPoolManager(Integer.parseInt(portNumberString), entry_DictionaryFile.getText(), Integer.parseInt(WorkerPoolSizeString));
                        workerPoolManager.start();

                        btnStartServer.setVisible(false);
//                        Label_DictionaryFile.setVisible(false);
//                        Label_WorkerPoolSize.setVisible(false);
//                        lblPortNumber.setVisible(false);
                        entry_DictionaryFile.setEditable(false);
                        entry_PortNumber.setEditable(false);
                        entry_WorkerPoolSize.setEditable(false);
                        TextDisplay.setText("Server started!");
                    }
                } catch (ClassCastException cce) {
                    TextDisplay.setText("Incorrect json format, correct{content:[{word: w, meaning: [m1, m2 ..]}, ..]}");

                    entry_DictionaryFile.setText("");
                } catch (FileNotFoundException foe) { //these error handling are needed to passed to socket to see whether they are the correct input
                    TextDisplay.setText("File not found, enter the correct json file");
                    entry_DictionaryFile.setText("");
                } catch (ParseException pe) {
                    TextDisplay.setText("File is not json!!");
                } catch (BindException be) {
                    System.out.println("Port number already in use");
                }catch (IOException ioe) {
                    TextDisplay.setText("FIle not open, error happen when opening the file");
                } catch (NumberFormatException nfe) {
                    TextDisplay.setText("not numeric");
                } catch (IllegalArgumentException iae) {
                    TextDisplay.setText("problem with your port number: " + iae.getMessage());
                }
                btnStopServer.setVisible(true);
            }
        });



        btnStopServer = new JButton("Terminate");
        btnStopServer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    workerPoolManager.killAllandSaveFile();
                    System.exit(0);
                } catch (IOException ioe){
                    System.out.println("An IO exception occur, file possibly not saved: " + ioe.getMessage());
                    System.exit(0);
                } catch (NullPointerException npe) {
                    System.out.println("there hasn't been any thread created in the pool");
                    System.exit(0);
                }

            }
        });
        btnStopServer.setVisible(false);
        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(lblPortNumber)
                                                        .addComponent(Label_DictionaryFile)
                                                        .addComponent(Label_WorkerPoolSize))
                                                .addGap(47)
                                                .addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
                                                        .addComponent(entry_DictionaryFile, GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                                                        .addComponent(entry_PortNumber, GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                                                        .addComponent(entry_WorkerPoolSize, GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)))
                                        .addGroup(gl_contentPane.createSequentialGroup()
                                                .addComponent(btnStartServer)
                                                .addPreferredGap(ComponentPlacement.RELATED, 202, Short.MAX_VALUE)
                                                .addComponent(btnStopServer))
                                        .addComponent(TextDisplay, GroupLayout.DEFAULT_SIZE, 434, Short.MAX_VALUE))
                                .addContainerGap())
        );
        gl_contentPane.setVerticalGroup(
                gl_contentPane.createParallelGroup(Alignment.LEADING)
                        .addGroup(gl_contentPane.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(lblPortNumber)
                                        .addComponent(entry_PortNumber, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(ComponentPlacement.UNRELATED)
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(Label_DictionaryFile)
                                        .addComponent(entry_DictionaryFile, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18)
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(Label_WorkerPoolSize)
                                        .addComponent(entry_WorkerPoolSize, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                                .addGap(18)
                                .addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
                                        .addComponent(btnStartServer)
                                        .addComponent(btnStopServer))
                                .addPreferredGap(ComponentPlacement.RELATED)
                                .addComponent(TextDisplay, GroupLayout.DEFAULT_SIZE, 95, Short.MAX_VALUE)
                                .addContainerGap())
        );
        contentPane.setLayout(gl_contentPane);
    }
}
