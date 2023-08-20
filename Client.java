import java.net.*;

import javax.swing.*;
import java.io.*;
import java.awt.*;
import java.awt.event.*;

public class Client extends JFrame {
	Socket socket;
	// variable of reading and writing
	BufferedReader br;
	PrintWriter out;
	// declaring the varaible for jframe
	private JLabel heading = new JLabel("Client Area:");
	private JTextArea messageArea = new JTextArea();
	private JTextField messageInput = new JTextField();
	private Font font = new Font("Roboto", Font.PLAIN, 20);

	public Client() {
		try {
			System.out.println("Client is sending request");
			// request of client that send to server of that ip
			socket = new Socket("127.0.0.1", 7778);
			System.out.println("Done..");
			// reading and writing
			// on server side br get in character with help of input
			// socket. helps to create a pile line to throw the input and output
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(socket.getOutputStream());
			createGUI();
			// to handle the events
			handleEvents();
			// calling the methods after completing the method of upper
			startReading();
			// startWriting();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void handleEvents() {
		// it is interface so overriding the methods
		messageInput.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {

			}

			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				// to get the code of enter
				// System.out.println("Key released" + e.getKeyCode());
				// it helps us to send the written msg to server
				if (e.getKeyCode() == 10) {
					String contentToSend = messageInput.getText();
					messageArea.append("Me :" + contentToSend + "\n");
					out.println(contentToSend);
					out.flush();
					messageInput.setText("");
					messageInput.requestFocus();
				}
			}
		});

	}

	private void createGUI() {
		// gui codes..
		this.setTitle("Client Messanger[END]");
		this.setSize(500, 700);
		// center the gui when we open it
		this.setLocationRelativeTo(null);
		// to close the gui when cross is clicked
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// setting the font
		heading.setFont(font);
		messageArea.setFont(font);
		messageInput.setFont(font);

		// icon
		heading.setIcon(new ImageIcon("icon.jpg"));
		heading.setHorizontalTextPosition(SwingConstants.CENTER);
		heading.setVerticalTextPosition(SwingConstants.BOTTOM);
		heading.setHorizontalAlignment(SwingConstants.CENTER);
		heading.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		// layout
		this.setLayout(new BorderLayout());
		// not have any editing in middle
		messageArea.setEditable(false);

		// adding the component
		this.add(heading, BorderLayout.NORTH);
		// to have scroll in the side
		JScrollPane jscrollpane = new JScrollPane(messageArea);
		this.add(jscrollpane, BorderLayout.CENTER);
		this.add(messageInput, BorderLayout.SOUTH);
		this.setVisible(true);
	}

	// read and write at same time so multi threading
	public void startReading() throws Exception {
		// thread read using lamda
		Runnable r1 = () -> {
			System.out.println("Client started sending...");
			try {
				while (true) {
					String msg = br.readLine();
					if (msg.equals("exit")) {
						// System.out.println("Client has terminated");
						JOptionPane.showMessageDialog(this, "Client has terminated");
						messageInput.setEnabled(false);

						socket.close();
						break;
					}
					// System.out.println("Client :" + msg);
					messageArea.append("Server :" + msg + "\n");
				}
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("Connection Closed..");

			}
		};
		// creating object of thread and start
		new Thread(r1).start();
	}

	public void startWriting() {
		// thread write from user using lamda
		Runnable r2 = () -> {
			try {
				while (true && !socket.isClosed()) {
					// this means providing the output from the server by typiing
					BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));
					String content = br1.readLine();
					// Class PrintWriter. Prints formatted representations of objects to a
					// text-output stream .
					out.println(content);
					// to send anyhow
					out.flush();
					if (content.equals("exit")) {
						socket.close();
						break;
					}
				}
				System.out.println("Connection Closed..");
			} catch (Exception e) {

				System.out.println("Connection Closed..");

			}
		};
		new Thread(r2).start();
	}

	public static void main(String[] args) {
		System.out.println("Client is ready");
		new Client();
	}
}
