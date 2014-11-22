

import java.awt.Point;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Observable;

public class GameClient extends Observable implements Runnable {

	public static GameClient thisInstance;

	public static GameClient getInstance() {
		return thisInstance == null ? thisInstance = new GameClient()
				: thisInstance;
	}

	public static String SERVER = "localhost";
	public static int PORT = 9000;

	float me;
	float other;
	Point ball;

	int myPoints, otherPoints;
	int myColor, otherColor;
	boolean myReady, otherReady;

	public static final int IDLE = 0, CONNECTING = 1, SELECTION = 2, READY = 3,
			GAME = 4, CONNECTED = 5, ERROR = 6;
	int state;
	DatagramSocket clientSock;
	private int myID;
	private Thread clientThread;

	public void runClient() {
		clientThread = new Thread(this);
		clientThread.start();
	}

	private GameClient() {
		me = 0;
		other = 0;
		ball = new Point();
		myPoints = otherPoints = 0;
		myColor = otherColor = 1;
		myID = 0;
		state = IDLE;
	}

	public void setState(int state) {
		this.state = state;
		setChanged();
		notifyObservers(state);
	}

	public void run() {
		try {
			connect();
			startup();
			choosing();
			while (true) {
				sendMyLoc();
				recieveGameData();
				System.out.println("Me: " + me);
				System.out.println("Other: " + other);
				System.out.println("Ball: " + ball);
				Thread.sleep(50);

			}
		} catch (Exception e) {
			e.printStackTrace();
			setState(ERROR);
		}
	}

	public void connect() throws Exception {
		setState(CONNECTING);
		System.out.println("Connecting To Server...");
		clientSock = new DatagramSocket();
		clientSock.connect(InetAddress.getByName(SERVER), PORT);
	}

	/**
	 * Disconnect From Server. Send Disconnect Message To Server
	 * @throws Exception
	 */
	public void disconnect() {
		clientThread.interrupt();
		try {
			send("EXTREME_PONG,"+myID+",DISCONNECT");
		} catch (Exception e) {}
		clientSock.disconnect();
		setState(IDLE);
	}

	/**
	 * Say Hello To Server, Receive A Player ID (1 or 2), And Wait For Other Player To Be Ready
	 * @throws Exception
	 */
	public void startup() throws Exception {
		System.out.println("Sending Player HELO...");
		send("EXTREME_PONG");
		System.out.println("Receiving Response...");
		String rcv = receive();
		System.out.println("Received Response...");
		String[] rs = rcv.split(",");
		if (rs[0].contains("EXTREME")) {
			myID = Integer.parseInt(rs[1]);
			System.out.println("My ID: " + myID);
			setState(CONNECTED);
		}
		rcv = receive();
		rs = rcv.split(",");
		
		setState(SELECTION);
		System.out.println("State Switched To Choosing Color");
	}

	private void choosing() throws Exception {
		while (true) {
			System.out.println("Sending My Color: "+myColor+", Ready = "+myReady);
			send(myID + "," + myColor + "," + (myReady ? 1 : 0));
			String[] rcv = receive().split(",");
			if(rcv[0].equals("EXTREME")){
				otherColor = Integer.parseInt(rcv[1]);
				otherReady = Integer.parseInt(rcv[2]) == 1;
				System.out.println("Received Enemy Color: "+otherColor+", Ready = "+otherReady);
			}
			if (myReady && otherReady)
				break;
			Thread.sleep(100);
		}
		setState(READY);

	}

	public void choose(int colorID, boolean selected) {
		this.myColor = colorID;
		this.myReady = selected;
	}

	private void recieveGameData() throws Exception {
		// Receive Other's Location
		String other = receive();
		this.other = Float.parseFloat(other);

		// Receive Ball Location
		String[] ball = receive().split(",");
		this.ball.setLocation(Double.parseDouble(ball[0]),
				Double.parseDouble(ball[1]));

		// Receive Points
		String[] points = receive().split(",");
		this.myPoints = Integer.parseInt(points[0]);
		this.otherPoints = Integer.parseInt(points[1]);

	}

	private void sendMyLoc() throws Exception {
		send("EXTREME_PONG," + myID + "," + me);
	}

	public Point getBallLoc() {
		return ball;
	}

	public void setMyLocation(float y) {
		try {
			clientThread.wait();
			me = y;
			clientThread.notify();
		} catch (InterruptedException e) {
		}
	}

	public float getEnemyLocation() {
		return other;
	}

	public void send(String data) throws Exception {
		DatagramPacket snd = new DatagramPacket(new byte[1024], 1024,
				InetAddress.getByName(SERVER), PORT);
		snd.setData(data.getBytes());
		clientSock.send(snd);
	}

	public String receive() throws Exception {
		DatagramPacket rcv = new DatagramPacket(new byte[1024], 1024);
		clientSock.receive(rcv);
		return new String(rcv.getData()).trim();
	}

	public int getMyID() {
		return myID;
	}

	public int getState() {
		return state;
	}

	public int getEnemyColor() {
		return otherColor;
	}
	
	public void setMyColor(int myColor){
		this.myColor=myColor;
	}

	public void setColorReady() {
		this.myReady=true;
	}

}
