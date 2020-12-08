package zzzLetsGoPerooDOE;

import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;


public class PlayerClient extends JFrame{
	private ClientConnect csc;
	
	private int width;
	private int height;
	private int handSize;
	private Container contentPane;
	private JTextArea message;
	private JTextArea message2;
	//Buttons
	private JButton b1;
	private JButton b2;
	private JButton b3;
	private JButton b4;
	private JButton b5;
	private JButton b6;
	private JButton up;
	private JButton down;
	private JButton callButton;
	private JButton doneButton;
	private boolean dieBEnabled;
	private boolean otherBEnabled;
	private int playerNumber =0;
	private int betDieNumber = 0;
	
	private class ClientConnect {
		private Socket socket;
		private DataInputStream dataIn;
		private DataOutputStream dataOut;
		
			public ClientConnect(){
			
				try {
					socket = new Socket("localhost", 2334);
					dataIn = new DataInputStream(socket.getInputStream());
					dataOut = new DataOutputStream(socket.getOutputStream());
					playerNumber = dataIn.readInt();
				} catch(IOException ex) {
					
				}
			}

			public void sendBet(int betDieNumber) {
				// TODO Auto-generated method stub
				try {
					dataOut.writeInt(betDieNumber);;
					dataOut.flush();
				}catch(IOException ex) {
					System.out.println("SendBet");
				}
			}

			public void sendBetDie(int die) {
				// TODO Auto-generated method stub
				try {
					dataOut.writeInt(die);;
					dataOut.flush();
				}catch(IOException ex) {
					System.out.println("SendDie");
				}
			}			
	}
	
	public void SetUpDieButtons() {
		ActionListener al = new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JButton b = (JButton) ae.getSource();
				String bWord = b.getText();
				int die;
				
				
				if(bWord == "Call!") {
					die = 7;
					csc.sendBetDie(die);
					otherBEnabled = false;
					dieBEnabled = false;
					toggleOtherButtons();
					toggleDiceButtons();
					UpdateRound();
				}
				else {
					die = Integer.parseInt(bWord);
					csc.sendBetDie(die);
					otherBEnabled = true;
					dieBEnabled = false;
					toggleDiceButtons();
					toggleOtherButtons();
				}
			}
		};
		b1.addActionListener(al);
		b2.addActionListener(al);
		b3.addActionListener(al);
		b4.addActionListener(al);
		b5.addActionListener(al);
		b6.addActionListener(al);
		callButton.addActionListener(al);
	}
	
	public void SetUpOtherButtons() {
		 ActionListener aL = new ActionListener() {
			 public void actionPerformed(ActionEvent ae) {
				 JButton b = (JButton) ae.getSource();
				 String bWord = b.getText();
				 
				 if(bWord == "Lock in") {
					 otherBEnabled = false;
					 csc.sendBet(betDieNumber);
					 betDieNumber = 0;
					 toggleOtherButtons();
					 Thread t = new Thread(new Runnable() {
							public void run() {
								UpdateTurn();
							}
						});
						t.start();
				 }
				 else if(bWord == "+1") {
					 betDieNumber++;
					 message2.setText("Your bet is now " + betDieNumber);
				 }
				 else if(bWord == "-1"){
					 betDieNumber--;
					 message2.setText("Your bet is now " + betDieNumber);
				 }
			 }
		 };
		 up.addActionListener(aL);
		 down.addActionListener(aL);
		 doneButton.addActionListener(aL);
	}
	
	private void toggleDiceButtons(){
		b1.setEnabled(dieBEnabled);
		b2.setEnabled(dieBEnabled);
		b3.setEnabled(dieBEnabled);
		b4.setEnabled(dieBEnabled);
		b5.setEnabled(dieBEnabled);
		b6.setEnabled(dieBEnabled);
		callButton.setEnabled(dieBEnabled);
	}
	public void toggleOtherButtons() {
		up.setEnabled(otherBEnabled);
		down.setEnabled(otherBEnabled);
		doneButton.setEnabled(otherBEnabled);
	}
	
	public void UpdateTurn() {
		try {
			int betDieP = csc.dataIn.readInt();
			if(betDieP == 7) {
				System.out.println("Your opponent Called!");
				UpdateRound();
			}
			else if(betDieP == 10) {
				endGame(true);
			}
			else{
				//System.out.println("GOT TO HERE");
				
				int betAmountP = csc.dataIn.readInt();
			
				System.out.println("Opponents bet: " + betAmountP + " d" + betDieP + "'s");
			
				dieBEnabled = true;
				otherBEnabled = false;
				toggleDiceButtons();
				toggleOtherButtons();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void endGame(boolean youDead) {
		// TODO Auto-generated method stub
		if(youDead) {
			System.out.println("You Win!");
		}
		else {
			System.out.println("You Lose!");
		}
	}

	public void UpdateRound() {
		try {
			
			int checkOver = csc.dataIn.readInt(); //Call presser is here
			if(checkOver == 8) {
				endGame(false);
			}
			else if(checkOver == 10) {
				endGame(true);
			}
			else {
				boolean goNext = csc.dataIn.readBoolean();
				if(goNext) {
					System.out.println("You lost a dice because you were wrong!");
					handSize--;
					System.out.println("Hand Size: " + handSize);
					//GetHand();
					dieBEnabled = true;
					otherBEnabled = false;
					toggleOtherButtons();
					toggleDiceButtons();
				}
				else {
					System.out.println("Your opponent lost a dice because they were wrong!");
					System.out.println("Hand Size: " + handSize);
					dieBEnabled = false;
					otherBEnabled = false;
					toggleOtherButtons();
					toggleDiceButtons();
					Thread t = new Thread(new Runnable() {
						public void run() {
							UpdateTurn();
						}
					});
					t.start();
				}
			}
		} catch(IOException ex) {
			System.out.println("Update Round");
		}
	}
	
	public void ConnectServer() {
		csc = new ClientConnect();
	}
	
	private PlayerClient(int w, int h) {
		width = w;
		height = h;
		b1 = new JButton("1");
		b2 = new JButton("2");
		b3 = new JButton("3");
		b4 = new JButton("4");
		b5 = new JButton("5");
		b6 = new JButton("6");
		up = new JButton("+1");
		down = new JButton("-1");
		callButton = new JButton("Call!");
		doneButton = new JButton("Lock in");
		contentPane = this.getContentPane();
		message = new JTextArea();
		message2 = new JTextArea();
		handSize = 6;
	}
	private void setUpGUI() {
		this.setSize(width, height);
		this.setTitle("Player "  + playerNumber);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		contentPane.setLayout(new GridLayout(1, 5));
		contentPane.add(message);
		contentPane.add(message2);
		message.setText("Player UNO");
		message.setWrapStyleWord(true);
		message.setLineWrap(true);
		message.setEditable(false);
		message.setText("Your bet: ");
		message2.setWrapStyleWord(true);
		message2.setLineWrap(true);
		message2.setEditable(false);
		contentPane.add(b1);
		contentPane.add(b2);
		contentPane.add(b3);
		contentPane.add(b4);
		contentPane.add(b5);
		contentPane.add(b6);
		contentPane.add(up);
		contentPane.add(down);
		contentPane.add(callButton);
		contentPane.add(doneButton);
		
		
		System.out.println("You are player: " + playerNumber);
		
		if(playerNumber == 1) {
			message.setText("Player 1: You go first.");
			//GetHand();
			System.out.println();
			dieBEnabled = true;
			otherBEnabled = false;
			toggleOtherButtons();
		} else {
			message.setText("Player 2: You go second, please wait...");
			//GetHand();
			System.out.println();
			dieBEnabled = false;
			otherBEnabled = false;
			toggleOtherButtons();
			toggleDiceButtons();
			Thread t = new Thread(new Runnable() {
				public void run() {
					UpdateTurn();
				}
			});
			t.start();
		}
		
		this.setVisible(true);
	}
		
	
	private void GetHand() {
		// TODO Auto-generated method stub
		int show = -1;
		try {
			for(int i = 0; i < handSize; i++) {
				show = csc.dataIn.readInt();
				System.out.print(show + " ");
			}
		} catch(IOException ex) {
			System.out.println("GetHAnd MESDSED up");
		}
	}

	public static void main(String[] args) {
		PlayerClient p = new PlayerClient(850, 200);
		p.ConnectServer();
		p.setUpGUI();
		
		p.SetUpDieButtons();
		
		p.SetUpOtherButtons();
		
	}
	

}
