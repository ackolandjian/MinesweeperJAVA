package minesweeper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.JPanel;

/** 
 * Compteur de la partie
 * @author Anna Christiane Kolandjian
 * @revision 0.0
 */

public class Compteur extends JPanel implements Runnable {
 /**
	 * Default serialVerisonUID for Serializable Compteur
	 */
	private static final long serialVersionUID = 1L;
	private int compteurText;
	private int widthCompteur = 60;
	private int heightCompteur = 40;
	private Thread process_cpt;
	
	
    /***
     * Constructeur du Compteur sans paramètres
     */
	public Compteur() {
		setPreferredSize(new Dimension(widthCompteur+2,heightCompteur+2));
		process_cpt = new Thread(this);
	}
	
    /***
     * Surcharge de la fonction public void run() 
     */
	@Override
	public void run() {
		while (process_cpt != null) {
			try { 
				Thread.sleep(1000);
				compteurText ++;
				repaint();
			}
			catch(InterruptedException e){
				e.printStackTrace();
			}
		}
	}
	
    /***
     * @return le compteur actuel de type String
     */
	public int getCompteur_txt() {
		return compteurText;
	}
	 
    /***
     * Démarrage du compteur
     */
	public void start_cpt() {
		compteurText = 0;
		process_cpt = new Thread(this);
		process_cpt.start();
	}
	
    /***
     * Fin du compteur
     */
	public void stop_cpt() {
		process_cpt = null;
	}
	
    /***
     * Redémarrage du compteur
     */
	public void reset_cpt() {
		compteurText = 0;
	}
	
    /***
     * Surcharge de paintComponent
     * @Param Graphics gc
     */
	public void paintComponent (Graphics gc) {
		int pos_digit;
		String sec;
		super.paintComponent(gc);
		gc.setColor(Color.BLACK);
		gc.fillRect(0, 0, widthCompteur, heightCompteur);
		gc.setColor(new Color(180, 180, 180));
		gc.drawRect(0, 0, widthCompteur, heightCompteur);
		gc.setFont (new Font("DS-Digital", 1, 24));
		gc.setColor(Color.white);

		if(compteurText < 10) {
			pos_digit = (getWidth()/2)-20;
			sec = "00" + String.valueOf(compteurText);
		}
		else if (compteurText < 100) {
			pos_digit = (getWidth()/2)-20;
			sec = "0" + String.valueOf(compteurText);
		}
		else {
			pos_digit = (getWidth()/2)-20;
			sec = "999";
		}
		gc.drawString(sec, pos_digit, (getHeight()/2)+7);
	}
}
