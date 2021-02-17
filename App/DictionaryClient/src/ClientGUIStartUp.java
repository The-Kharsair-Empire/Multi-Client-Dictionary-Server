
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.UnknownHostException;

public class ClientGUIStartUp extends JFrame {

    private JPanel contentPane;
    private JTextField Entry_serverAddress;
    private JLabel Label_PortNumber;
    private JTextField Entry_PortNumber;
    private static ClientGUIStartUp frame;
    private JTextArea msgDisplay;
    private ClientBackendThread clientBackendThread;



    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    frame = new ClientGUIStartUp();
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
    public ClientGUIStartUp() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);

        JLabel Label_ServerAddress = new JLabel("Server Address： ");

        Entry_serverAddress = new JTextField();
        Entry_serverAddress.setColumns(10);

        Label_PortNumber = new JLabel("Port Number:");

        Entry_PortNumber = new JTextField();
        Entry_PortNumber.setText("");
        Entry_PortNumber.setColumns(10);

        JButton btnCreateClient = new JButton("Create Client");
        JButton btnCloseClient = new JButton("Shut down Client");

        btnCreateClient.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                msgDisplay.setText("Waiting for server to accept your connection, due to limited worker size\n");
                try {
                    String portNumberString = Entry_PortNumber.getText();
                    String serverAddressString = Entry_serverAddress.getText();

                    if (!portNumberString.matches("-?(0|[1-9]\\d*)")) {
                        msgDisplay.setText("Port number contain invalid character or white space!!");
                        Entry_PortNumber.setText("");
                    }

                    else {
//                        msgDisplay.setText("");
                        clientBackendThread = new ClientBackendThread(serverAddressString, Integer.parseInt(portNumberString));
                        btnCreateClient.setVisible(false);
                        btnCloseClient.setVisible(true);

                        frame.setVisible(false);
                        clientBackendThread.start();


                    }


                } catch (NumberFormatException nfe) {//handling all errors
                    msgDisplay.setText("not numeric");
                    Entry_PortNumber.setText("");
                } catch (UnknownHostException unhe) {
                    msgDisplay.setText("unknown host, server address is incorrect: " + unhe.getMessage());
                    Entry_serverAddress.setText("");
                } catch (IllegalArgumentException ilgalAe) {
                    msgDisplay.setText("Invalid IP or port number: " + ilgalAe.getMessage());
                    Entry_PortNumber.setText("");
                } catch (ConnectException ce) {
                    msgDisplay.setText("problem to connect, check your connection detail\nor check if the server is on\nproblem: " + ce.getMessage());
                    Entry_PortNumber.setText("");
                    Entry_serverAddress.setText("");
                }  catch (UnsupportedEncodingException usee) {
                    msgDisplay.setText("catching error in input/output stream encoding: " + usee.getMessage());
                } catch (IOException ioe) {
                    msgDisplay.setText("An error happen while trying to establish connection to server\n\n");

                    msgDisplay.append("maybe the server hasn't started\n");
                    msgDisplay.append("or check if you enter valid server address or port number\n");
                }




            }
        });
        
        msgDisplay = new JTextArea();
        msgDisplay.setColumns(10);
        

        btnCloseClient.addActionListener(new ActionListener() {
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
        btnCloseClient.setVisible(false);

        GroupLayout gl_contentPane = new GroupLayout(contentPane);
        gl_contentPane.setHorizontalGroup(
        	gl_contentPane.createParallelGroup(Alignment.TRAILING)
        		.addGroup(gl_contentPane.createSequentialGroup()
        			.addComponent(btnCreateClient)
        			.addContainerGap(315, Short.MAX_VALUE))
        		.addGroup(gl_contentPane.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
        				.addComponent(msgDisplay, Alignment.LEADING, GroupLayout.DEFAULT_SIZE, 420, Short.MAX_VALUE)
        				.addGroup(gl_contentPane.createSequentialGroup()
        					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
        						.addComponent(Label_ServerAddress)
        						.addComponent(Label_PortNumber))
        					.addGap(54)
        					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
        						.addComponent(Entry_PortNumber, GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
        						.addComponent(Entry_serverAddress, GroupLayout.DEFAULT_SIZE, 191, Short.MAX_VALUE)
        						.addComponent(btnCloseClient))))
        			.addGap(14))
        );
        gl_contentPane.setVerticalGroup(
        	gl_contentPane.createParallelGroup(Alignment.LEADING)
        		.addGroup(gl_contentPane.createSequentialGroup()
        			.addContainerGap()
        			.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
        				.addComponent(Label_ServerAddress, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE)
        				.addComponent(Entry_serverAddress, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addPreferredGap(ComponentPlacement.RELATED)
        			.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
        				.addComponent(Label_PortNumber)
        				.addComponent(Entry_PortNumber, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        			.addGap(18)
        			.addGroup(gl_contentPane.createParallelGroup(Alignment.BASELINE)
        				.addComponent(btnCreateClient)
        				.addComponent(btnCloseClient))
        			.addPreferredGap(ComponentPlacement.UNRELATED)
        			.addComponent(msgDisplay, GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
        			.addContainerGap())
        );
        contentPane.setLayout(gl_contentPane);
    }
}
