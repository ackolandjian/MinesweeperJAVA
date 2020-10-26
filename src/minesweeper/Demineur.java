package minesweeper;

import javax.swing.*;

/** 
 * Demineur qui crée des clients
 * @author Anna Christiane Kolandjian
 * @revision 0.0
 */

public class Demineur extends JFrame {
	
	/**
	 * Default serialVerisonUID for Serializable Demineur
	 */
	private static final long serialVersionUID = 1L;
	Champ champ_mine = new Champ(Level.EASY);
	GUI gui = new GUI(this);
	Client cli = new Client(this);
	
	/**
	 * Constructeur du demineur sans paramètres
	 */
	public Demineur() {
		super("Demineur");
		champ_mine = new Champ(Level.EASY);
		champ_mine.placeMines();
		gui = new GUI(this);
		setContentPane(gui) ;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack() ;
		setVisible(true) ;
	}
	
	/**
	 * Constructeur du demineur avec paramètres 
	 * @param DimX la dimension X du champ
	 * @param DimY la dimension Y du champ
	 * @param nbMines le nb total de mines
	 */
	public Demineur(int DimX, int DimY, int nbMines) {
		super("Demineur");
		champ_mine = new Champ(Level.EASY);
		champ_mine.placeMines();
		gui = new GUI(this);
		setContentPane(gui) ;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack() ;
		setVisible(true) ;
	}
	
	/**
	 * @return le champ de mines
	 */
	public Champ getChamp(){
		return champ_mine;
	}
	
	/**
	 * @return le gui
	 */
	public GUI getGui() {
		return gui;
	}
	
	/**
	 * @return le client
	 */
	public Client getCli() {
		return(cli);
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new Demineur();
	}

}
