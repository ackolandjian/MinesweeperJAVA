package minesweeper;

import java.awt.Color; 
import java.io.*;
import java.net.Socket;
import java.util.HashMap;

/** 
 * Classe Client pour un joueur
 * @author Anna Christiane Kolandjian
 * @revision 0.0
 */

public class Client implements Runnable{
	
	Demineur demin;
	int DimX;
	int DimY;
	int x_case;
	int y_case;
	int nbMines;
	String namePlayer;
	String player;
	boolean statePartie = false;
	boolean loose = false;
	boolean joueurConnected = false;
	private int nbJoueurConnect;
	private Thread ecoute = new Thread(this);
	HashMap<String, Color> listColor = new HashMap<String, Color>();
	HashMap<String, Integer> listJoueur = new HashMap<String, Integer>();
	Color[] color = {Color.red, Color.blue, Color.green, Color.orange, Color.pink, Color.DARK_GRAY, Color.cyan, Color.black};
	DataInputStream in;
	DataOutputStream out;
	String dataIn;
	private int indiceData = 0; //indice utilis� pour diff�rencier entre les donn� re�u
	Socket client;
	
	/** 
	 * Constructeur Client avec un param�tre de type Demineur
	 * @param Demineur 
	 */
	Client(Demineur demineur){
			demin = demineur;
	}
	
	/** 
	 * Fonction connexionServeur appel� par le GUI pour le transfert de donn�es quand le bouton
	 * connexion est click�
	 * @param nom_serveur qui est le nom du serveur utilis�
	 * @param nom du joueur
	 * @param port utilis�
	 */
	public void connexionServeur(String nom_serveur, String nom, int port) {
		try {
			client = new Socket(nom_serveur, port);
			out =new DataOutputStream(client.getOutputStream());
			in = new DataInputStream(client.getInputStream());
			if (nom.length() > 0) // envoi du nom
				out.writeUTF(nom);
			 else
			 out.writeUTF("Gros Bill");
			namePlayer = nom;
			dataIn = in.readUTF();
			if(dataIn.compareTo("connected") == 0) {
				joueurConnected = true;
				demin.getGui().add_message("Connexion au serveur r�ussi");
				demin.getGui().change_state_connexion(true);
				int color1 = in.readInt();
				demin.getGui().changePanelNorth(color[color1]);
				ecoute.start();
			}
			else if(dataIn.compareTo("start") == 0) {
				demin.getGui().add_message("Connexion �chou�, partie d�j� en cours");
			}
			else if(dataIn.compareTo("full") == 0) {
				demin.getGui().add_message("Connexion �chou�, serveur plein");
			}
			else if(dataIn.compareTo("name") == 0) {
				demin.getGui().add_message("Connexion �chou�, nom d�j� utilis�");
			}
			
		}
		catch (IOException e) {
			e.printStackTrace();
			demin.getGui().add_message(e.getMessage());
		}
	}

	/** 
	 * retourne si un joueur est connect� ou pas
	 */
	public boolean isJoueurConnected() {
		return joueurConnected;
	}
	
	/** 
	 * retourne le nombre de joueur(s) connect�(s)
	 */
	public int getNbJoueurConnect() {
		return nbJoueurConnect;
	}
	
	/** 
	 * set le nombre de joueur, pour le mettre � jour
	 */
	public void setJoueurConnected(boolean t) {
		joueurConnected = t;
	}
	
	/** 
	 * retourne la liste de couleurs
	 */
	public HashMap<String, Color> getListColor(){
		return(listColor);
	}
	
	/** 
	 * retourne loose si il est lost ou pas
	 */
	public boolean getLoose() {
		return(loose);
	}
	
	/** 
	 * Envoie du data au serveur � travers la classe Case, lorsqu'une case est revealed
	 */
	public void sendData(String data) {
		try {
			out.writeUTF(data);
		}
		catch (IOException e) {
			e.printStackTrace();
			demin.getGui().add_message(e.getMessage());
		}
	}
	
	/** 
	 * retourne la liste de couleurs
	 */
	public Color[] getColor() {
		return(color);
	}
	
	/** 
	 * Responsable au disconnect d'un joueur si il disconnecte
	 */
	public void disconnect() {
		try {
			out.writeUTF("disconnect");
			out.writeUTF("disconnect");
			joueurConnected=false;
			ecoute.stop();
			client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** 
	 * fonction pour envoyer un message
	 * N'est pas impl�ment� totalement
	 */
	public void sendMessage() {
		try {
			out.writeUTF("sendMessage");
			out.writeUTF(demin.getGui().getTextMessage());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * retourne statePartie qui d�finie si la partie est en cours ou pas
	 */
	public boolean getStatePartie() {
		return(statePartie);
	}
	
	/** 
	 * surcharge de la fonction run du thread ecoute
	 */
	public void run() {
		while(ecoute != null) {
			try {
				dataIn = in.readUTF();
				System.out.println(dataIn);
				indiceData ++;
				System.out.println(indiceData);
				if(indiceData == 1) {	//La premi�re data est la dimension x de la partie
					DimX = Integer.parseInt(dataIn);
				}
				else if(indiceData == 2) {	//La deuxi�me data est la dimension y de la partie
					DimY = Integer.parseInt(dataIn);
					demin.getChamp().newGame(DimX, DimY, 0);	
					demin.getGui().newPartie(DimX,DimY);
					demin.getGui().add_message("ALLEZZZZZZZ GOOO !!!");
					statePartie = true;
				}
				else if(indiceData == 3) {	//La troisi�me data est la qtt� de joueurs connect�s
					nbJoueurConnect = Integer.parseInt(dataIn);
					//la premiere fois on initialise tout
					listColor = new HashMap<String, Color>();
					listJoueur = new HashMap<String, Integer>();
					for (int i = 0; i < nbJoueurConnect; i++) {
						String playerConnect = in.readUTF();
						Color colorPlayer = new Color(in.readInt());
						listColor.put(playerConnect, colorPlayer);
						listJoueur.put(playerConnect, 0);
					}
					demin.getGui().createScore(listColor, listJoueur, nbJoueurConnect);
				}

				//while playing
				else if((indiceData-4)%4 == 0) { //le nom du joueur se r�p�te
					player = dataIn;
					int score = listJoueur.get(player);
					score++; //qqn a click� evidemment, donc incr�menter son score
					listJoueur.put(player, score); //mettre � jour le score
					demin.getGui().updateScore(listJoueur, nbJoueurConnect);
				}
				else if((indiceData-4)%4 == 1) { //l'index X de la case click�
					x_case = Integer.parseInt(dataIn);
				}
				else if((indiceData-4)%4 == 2) { //l'index Y de la case click�
					y_case = Integer.parseInt(dataIn);
				}
				else if((indiceData-4)%4 == 3) { //le message click�
					//une mine est click�
					if(dataIn.equals("Mine")) {
						if(namePlayer.equals(player)) {
							demin.getGui().add_message("Vous �tes tomb� sur une mine vous avez perdu !");
							loose = true;
						}
						else {
							demin.getGui().add_message("le joueur : " + player + " est tomb� sur une mine en " + x_case + ";" + y_case );
						}
						demin.getGui().getTabCase(x_case, y_case).reveal_case(-1, player);
					}
					//qqn a gagn�
					else if(dataIn.equals("Win")) {
						demin.getGui().getTabCase(x_case, y_case).reveal_case(-2, player);
						String score = listJoueur.toString();
						demin.getGui().Win(score);
						indiceData = 0;
						statePartie = false;
						loose = false;
						
					}
					//tout le monde a perdu
					else if(dataIn.equals("loose")) {
						demin.getGui().loose_all();
						indiceData = 0;
						loose = false;
					}
					//if dind't lose and didn't win, reveal the clicked case for all
					else {
						int nbMineAround = Integer.parseInt(dataIn);
						demin.getGui().getTabCase(x_case, y_case).reveal_case(nbMineAround, player);
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
				demin.getGui().add_message(e.getMessage());
			}
		}
	}
}

