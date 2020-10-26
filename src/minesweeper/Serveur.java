package minesweeper;

import java.awt.*; 
//import java.awt.event.*;
import java.io.*;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.Map.Entry;
import javax.swing.*;

/** 
 * Classe Serveur
 * @author Anna Christiane Kolandjian
 */

public class Serveur extends JFrame implements Runnable{

	/**
	 * Default serialVerisonUID for Serializable Serveur
	 */
	private static final long serialVersionUID = 1L;
	GUIServ gui;
	Champ champ_mine;
	int nbMines;
	int DimX = 10;
	int DimY = 10;
	int nbCaseRestante;
	int nbJoueurMax = 5;
	int nbJoueurConnect = 0;
	int nbJoueurElimine = 0;
	String messageIn;
	String messageIn2;
	String messageOut;
	ServerSocket gestSock;
	Thread connect_thread;
	int connect_thread_i = 0;
	String[][] score;
	boolean startPartie = false; //si la partie a commencé
	//création des collections
	HashSet<DataInputStream>dataIn = new HashSet<DataInputStream>();
	HashSet<DataOutputStream>dataOut = new HashSet<DataOutputStream>();
	HashMap<String, Color> listColor = new HashMap<String, Color>();
	Map<String, Integer> listJoueur = new HashMap<String, Integer>();
	Color[] color = {Color.red, Color.blue, Color.green, Color.orange, Color.pink, Color.cyan};
	private Iterator<DataInputStream> itIn = dataIn.iterator();
	private Iterator<DataOutputStream> itOut = dataOut.iterator();
	
	/**
	 * *Constructeur du Serveur avec 4 paramètres
	 * @param x la dimension X du champ
	 * @param y la dimension Y du champ
	 * @param JMax le nombre max de joueurs
	 * @param port le numéro du port
	 */
	Serveur(int x, int y, int JMax, int port){
		super("Serveur");
		DimX = x;
		DimY = y;
		gui = new GUIServ(this, DimX, DimY);
		if (JMax <= 5) {
			nbJoueurMax = JMax;
		}
		else {
			nbJoueurMax = 5;
			gui.addMessage("Nb de joueur réajusté automatiquement à 5");
		}
		setContentPane(gui) ;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true) ;
		startServer(port);
	}

	/**
	 * startServer pour la gestion des sockets et appeler la fonction connexionClient 
	 * @param port le numéro du port
	 */
	public void startServer(int port) {
		gui.addMessage("démarrage du serveur");
		//ouverture du serveur de sockets
		try {
			gestSock=new ServerSocket(port);
			connexionClient();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Commencez le thread de connexion de clients qui attends des clients pour se connecter
	 */
	public void connexionClient() {
		connect_thread = new Thread(this);
		connect_thread.setName(connect_thread_i + "_connect");
		connect_thread.start();
		connect_thread_i++;
	}

	/**
	 * surcharger la fonction run, appelée par le thread connect_thread
	 */
	public void run() {
		while(connect_thread != null) {
			try {
				int numThread = connect_thread_i;
				Socket socket=gestSock.accept();
				DataInputStream entree = new DataInputStream(socket.getInputStream());
				DataOutputStream sortie = new DataOutputStream(socket.getOutputStream());
				String nomJoueur = entree.readUTF();
				if(startPartie) {
					sortie.writeUTF("start");
				}
				else if(nbJoueurConnect >= nbJoueurMax) {
					sortie.writeUTF("full");
				}
				else if(listJoueur.containsKey(nomJoueur)) {
					sortie.writeUTF("name");
				}
				else {
					dataIn.add(entree);
					dataOut.add(sortie);
					listColor.put(nomJoueur, color[numThread -1]);
					listJoueur.put(nomJoueur, 0);
					gui.addMessage("Joueur " + nbJoueurConnect + " : " +nomJoueur+" connected");
					sortie.writeUTF("connected");
					sortie.writeInt(numThread-1);
					nbJoueurConnect++;
					connexionClient();
					boolean disconnect = false;
					while(!disconnect) {
						messageIn = entree.readUTF();
						messageIn2 = entree.readUTF();
						System.out.println(messageIn2);
						if(messageIn.equals("disconnect")) {
							disconnect = true;
							gui.addMessage(nomJoueur + " déconnecté");
							nbJoueurConnect--;
							listJoueur.remove(nomJoueur);
							listColor.remove(nomJoueur);
							dataIn.remove(entree);
							dataOut.remove(sortie);
							isSomeoneConnected(); 
							connect_thread.stop();
						}
						if(startPartie && messageIn.equals("sendMessage")== true) {
							//System.out.println(entree.readUTF());
							//gui.addMessage(nomJoueur + " : " + messageIn2);
							//System.out.println(entree.readUTF());
						}
						if(startPartie && !messageIn.equals("disconnect") && !messageIn.equals("sendMessage")) {
							execute_command(nomJoueur);
						}
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
				gui.addMessage(e.getMessage());
			}
		}
	}
	
	/**
	 * verifie si il reste 0 joueur connecté, après un disconnect d'un joueur
	 */
	public void isSomeoneConnected() {
		if(nbJoueurConnect == 0) {
			endGame();
		}
	}
	
	/**
	 * terminer la partie
	 */
	public void endGame() {
		gui.changeStart();
		nbJoueurElimine = 0;
		startPartie = false;
		gui.addMessage("Fin de partie");
		for(Entry<String, Integer> entry : listJoueur.entrySet()) {
			String cle = entry.getKey();
			listJoueur.put(cle, 0);
		}	
	}
	
	/**
	 * executer une commande, qui est cliquer sur une case par un joueur
	 * @param joueur le nom du joueur qui a cliqué
	 */
	public synchronized void execute_command(String joueur) {
		int score = listJoueur.get(joueur); //retourner le score du joueur
		//si la case revealed est une mine
		if(champ_mine.isMin(Integer.parseInt(messageIn), Integer.parseInt(messageIn2))) {
			messageOut = "Mine";
			listJoueur.put(joueur, -1);
			nbJoueurElimine++;
			//si tout le monde a perdu
			if(nbJoueurElimine == nbJoueurConnect) {
				messageOut = "loose";
				endGame();
			}
		}
		else {
			messageOut = String.valueOf(champ_mine.calculNbMine(Integer.parseInt(messageIn), Integer.parseInt(messageIn2)));
			score++;
			nbCaseRestante--;
		}
		listJoueur.put(joueur, score);
		itOut = dataOut.iterator();
		while(itOut.hasNext()) {
			try {
				DataOutputStream send = itOut.next();
				send.writeUTF(joueur); // 1er data nom du joueur
				send.writeUTF(messageIn); // le deuxième data est la dimension x
				send.writeUTF(messageIn2); // le troisième data est la dimension y
				//if lost
				if(nbCaseRestante - nbMines != 0) {
					send.writeUTF(messageOut);//"Mine" ou "loose" 4ème data
				}
				//if won
				else {
					send.writeUTF("Win"); //"Win" 4ème data
					endGame();
				}
			}
			catch (IOException e) {
				e.printStackTrace();
				gui.addMessage(e.getMessage());
			}
		}	
	}
	
	/**
	 * @return nbJoueurConnect le nb de joueurs connectés
	 */
	public int getNbJoueurConnect() {
		return(nbJoueurConnect);
	}
	
	/**
	 * le nombre de mines calculé selon la difficulté
	 * @param level
	 * @return
	 */
	public float Difficulté(Level level) {
		if(level == Level.EASY) {
			return(0.1f);
		}
		else if(level == Level.MEDIUM) {
			return(0.2f);
		}
		else if(level == Level.HARD) {
			return(0.3f);
		}
		else {
			return(0.1f);
		}
	}
	
	/**
	 * fonction appelée par le GUIServ au début de la partie pour commencer
	 * @param dimX la dimension X du champ
	 * @param dimY la dimension Y du champ
	 * @param level le level de type Level
	 */
	public void beginGame(int dimX,int dimY,Level level) {
		DimX = dimX; //selon le textBox de dimX 
		DimY = dimY; //selon le textBox de dimY
		nbCaseRestante = DimX*DimY;
		nbMines =(int) (DimX*DimY*Difficulté(level));
		String envoie_dim_x = String.valueOf(DimX);
		String envoie_dim_y = String.valueOf(DimY);
		itOut = dataOut.iterator();
		while(itOut.hasNext()) {
			try {
				DataOutputStream com = itOut.next();
				com.writeUTF(envoie_dim_x); 
				com.writeUTF(envoie_dim_y);	
				com.writeUTF(Integer.toString(nbJoueurConnect));
				for(Entry<String, Color> entry : listColor.entrySet()) {
				    String cle = entry.getKey();
				    Color valeur = entry.getValue();
				    com.writeUTF(cle);
				    com.writeInt(valeur.getRGB());
				}
			}
			catch (IOException e) {
				e.printStackTrace();
				gui.addMessage(e.getMessage());
			}
		}
		
		champ_mine = new Champ(DimX, DimY, nbMines);
		champ_mine.placeMines();
		champ_mine.affText();
		startPartie = true;
		//ecoute_client();
	}

	public static void main(String[] args) {
		Serveur serv =  new Serveur(10,10,8, 3000);
	}
}
