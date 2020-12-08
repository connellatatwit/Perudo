package zzzLetsGoPerooDOE;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class PerudoServer {
	
	
	private int numPlayers = 0;
	private ServerSocket ss;
	private ArrayList<Integer> pool = new ArrayList<Integer>();
	private ArrayList<Integer> p1Hand;
	private ArrayList<Integer> p2Hand;
	private int diePickedR;
	private int betAmount;
	private int callerB;
	private ServerSConnection player1;
	private ServerSConnection player2;
	private int player1PickedDie;
	private int player2PickedDie;
	private int player1PickedBet;
	private int player2PickedBet;
	private int betDieRem;
	private int betAmountRem;
	private boolean needRoll = false;
	private boolean goNext;
	private boolean p1IsDead = false;
	private boolean p2IsDead = false;

	
	private class ServerSConnection implements Runnable{
		private Socket socket;
		private DataInputStream dataIn;
		private DataOutputStream dataOut;
		private int playerID;
		
		public ServerSConnection(Socket s, int id) {
			playerID = id;
			socket = s;	
			try {
				dataIn = new DataInputStream(socket.getInputStream());
				dataOut = new DataOutputStream(socket.getOutputStream());
				
			} catch (IOException ex) {
				System.out.println("Whoops2");
			}
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true) {
				int check = 0;
				System.out.println(p1Hand + " : Player 1");
				System.out.println(p2Hand + " : Player 2");
				
				try {
					if(playerID ==1) {
						player1PickedDie = dataIn.readInt();
						//Call start TODO test this
						if(player1PickedDie == 7) {
							pool.addAll(p1Hand);
							pool.addAll(p2Hand);
							for(int i = 0; i < pool.size(); i++) {
								if(pool.get(i) == betDieRem || pool.get(i) == 1) {
									check++;
								}
							}
							if(check >= betAmountRem) {
								//p1 wrong
								p1Hand.remove(0);
								if(p1Hand.isEmpty()) {
									System.out.println("PLAYER 1 LOST");
									p1IsDead = true;
									player1.CheckWinner(p1IsDead);
									player2.CheckWinner(p2IsDead);
								}
								else {
									player2.sendDiePicked(player1PickedDie);
									goNext = true;
									player1.TellTurn(goNext);
									goNext = false;
									player2.TellTurn(goNext);
								}
							}
							else {
								p2Hand.remove(0);
								if(p2Hand.isEmpty()) {
									p2IsDead = true;
									System.out.println("PLAYER 2 LOST");
									player2.CheckWinner(p2IsDead);
									player1.CheckWinner(p1IsDead);
								}
								else {
									player2.sendDiePicked(player1PickedDie);
									goNext = false;
									player1.TellTurn(goNext);
									goNext = true;
									player2.TellTurn(goNext);
								}
							}
							pool.clear();
							Roll(p1Hand);
							Roll(p2Hand);
							//ShowHand(p1Hand);
							//ShowWinnerHand(p2Hand);
							//ShowHand(p2Hand);
							
						}
						else {
							//Call end
							//When call is not pressed
							player1PickedBet = dataIn.readInt();
							System.out.println("Player 1 picked die #" + player1PickedDie);
							System.out.println("and is saying there are " + player1PickedBet + " of them.");
							player2.sendDiePicked(player1PickedDie);
							player2.sendAmountPicked(player1PickedBet);
							betDieRem = player1PickedDie;
							betAmountRem = player1PickedBet;
						}
					}
					else {
						player2PickedDie = dataIn.readInt();
						//Call start
						if(player2PickedDie == 7) {
							pool.addAll(p1Hand);
							pool.addAll(p2Hand);
							
							for(int i = 0; i < pool.size(); i++) {
								if(pool.get(i) == betDieRem || pool.get(i) == 1) {
									check++;
								}
							}
							if(check >= betAmountRem) {
								//p2 wrong
								p2Hand.remove(0);
								if(p2Hand.isEmpty()) {
									System.out.println("PLAYER 2 LOST");
									p2IsDead = true;
									player2.CheckWinner(p2IsDead);
									player1.CheckWinner(p1IsDead);
								}
								else {
									player1.sendDiePicked(player2PickedDie);
									goNext = true;
									player2.TellTurn(goNext);
									goNext = false;
									player1.TellTurn(goNext);
								}
							}
							else {
								p1Hand.remove(0);
								if(p1Hand.isEmpty()) {
									System.out.println("PLAYER 1 LOST");
									//Sends 8 three times to p1
									p1IsDead = true;
									player1.CheckWinner(p1IsDead);
									player2.CheckWinner(p2IsDead);
								}
								else {
									player1.sendDiePicked(player2PickedDie);
									goNext = false;
									player2.TellTurn(goNext);
									goNext = true;
									player1.TellTurn(goNext);
								}
								
							}
							pool.clear();
							Roll(p1Hand);
							Roll(p2Hand);
							//ShowHand(p1Hand);
							//ShowHand(p2Hand);
							
						}
						
						else {
							//Call end 
							//When call is not pressed
							player2PickedBet = dataIn.readInt();
							System.out.println("Player 2 picked die #" + player2PickedDie);
							System.out.println("and is saying there are " + player2PickedBet + " of them.");
							player1.sendDiePicked(player2PickedDie);
							player1.sendAmountPicked(player2PickedBet);
							betDieRem = player2PickedDie;
							betAmountRem = player2PickedBet;
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("RunRUNRUN");
				}
			}
			
		}
		
		private void ShowWinnerHand(ArrayList<Integer> p2Hand) {
			// TODO Auto-generated method stub
			
		}

		private void CheckWinner(boolean isDead) {
			// TODO Auto-generated method stub
			try {
				 //to player 2
				if(isDead) {
					dataOut.writeInt(8);
				}
				else {
					dataOut.writeInt(10);
				}
			} catch(IOException ex) {
				System.out.println("CheckWinner Fail");
			}
		}

		private void TellTurn(boolean goNext) {
			// TODO Auto-generated method stub
			try {
				dataOut.writeInt(9);
				dataOut.writeBoolean(goNext);
				dataOut.flush();
			} catch (IOException ex){
				System.out.println("TellTurn messUp");
			}
			
		}

		public ArrayList<Integer> Roll(ArrayList<Integer> cup){
			int handSize = cup.size();
			cup.clear();
			for(int i = 0; i < handSize;i++) {
				cup.add(i, (((int) Math.ceil(Math.random() * 6))));	
			}
			
			return cup;
		}
		public ArrayList<Integer> InitialDeal(ArrayList<Integer> cup){
			for(int i = 0; i < 6; i++) {
				cup.add(((int) Math.ceil(Math.random() * 6)));
			}
			return cup;
		}

		private void sendDiePicked(int PickedDie) {
			// TODO Auto-generated method stub
			try {
				dataOut.writeInt(PickedDie);;
				dataOut.flush();
				
			} catch (IOException ex) {
				System.out.println("pickDIe");
			}
		}
		private void sendAmountPicked(int PickedAmount) {
			// TODO Auto-generated method stub
			try {
				dataOut.writeInt(PickedAmount);;
				dataOut.flush();
				
			} catch (IOException ex) {
				System.out.println("Pick Amount");
			}
		}

		public void ShowHand(ArrayList<Integer> hand) {
				// TODO Auto-generated method stub
			for(int i = 0; i < hand.size(); i++) {
				try {
					dataOut.writeInt(hand.get(i));
				} catch(IOException ex) {
						System.out.println("wShowHand Muess up");
				}
			}
		}
	}
	
	public PerudoServer() {
		try {
			ss = new ServerSocket(2334);
			
		}catch(IOException ex) {
			System.out.println("whoops0");
		}
	}
	public void aConnections() {
		try {
			System.out.println("----Waiting for players----");
			while(numPlayers < 2) {
				Socket s = ss.accept();
				numPlayers++;
				ServerSConnection ssc = new ServerSConnection(s, numPlayers);
				System.out.println("Player #" + numPlayers + " Connected");
				ssc.dataOut.writeInt(numPlayers);
				
				if(numPlayers == 1) {
					player1 = ssc;
					p1Hand = new ArrayList<Integer>();
					ssc.InitialDeal(p1Hand);
					//ssc.ShowHand(p1Hand);
				}
				else {
					player2 = ssc;
					p2Hand = new ArrayList<Integer>();
					ssc.InitialDeal(p2Hand);
					//ssc.ShowHand(p2Hand);
				}
				Thread t = new Thread(ssc);
				t.start();
			}
		} catch(IOException ex) {
			
		}
		
	}
	
	
	public static void main(String[] args) {
		PerudoServer ps = new PerudoServer();
		ps.aConnections();
	}

}
