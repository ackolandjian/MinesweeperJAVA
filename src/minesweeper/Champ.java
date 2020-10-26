package minesweeper;
import java.util.*;

/** 
 * Champ Java Objet, champ de mines
 * @author Anna Christiane Kolandjian
 * @revision 0.0
 */

public class Champ{
	
    /**
     * Initilisation du tableau
     */
	private int NBMINES;
	public boolean[][] tabMine;

	Random alea = new Random();

    /**
     * Constructeur sans paramètres
     */
	public Champ() { 
		this(Level.EASY);
	}
    /**
     * Constructeur avec paramètre de type Level
     */
	public Champ(Level level) {
		newGame(level);
	}
	
    /**
     * Constructeur custom
     * @param x custom nb lignes
     * @param y custom nb colonnes
     * @param nbMines custom nb total de mines
     */
	public Champ(int DimX, int DimY, int nbMines) {
		newGame(DimX, DimY, nbMines);
	}
	
    /**
     * On initilise le tableau des mines
     * @param x nb de lines
     * @param y nb de colonnes
     * @param mines nb total de mines
     */
	private void initChamp(int x, int y, int mines) {
		tabMine = new boolean [x][y];
		NBMINES = mines;
	}

    /**
     * On place les mines aléatoirement
     */
	public void placeMines() {
		//remise à zeros
		for(int x=0; x<tabMine.length; x++) {
			for(int y=0; y<tabMine[0].length; y++) {
				tabMine[x][y] = false;
			}
		}
		for(int i=0;i<NBMINES;) {
			int x = alea.nextInt(tabMine.length); //tirage au sort nb appartenant à [0,DIM-1]
			int y = alea.nextInt(tabMine[0].length); //tirage au sort nb appartenant à [0,DIM-1]
			//si n'est pas une mine, mettre une mine
			if(!tabMine[x][y]) {
				i++;
				tabMine[x][y] = true;
			}
		}
	}
	
    /***
     * Fonction d'affichage du champ qu'on a, dans le terminal
     * @return return le champ sous type de String
     */
	public String affText() {
		String printChamp = "";
		for(int x=0; x<tabMine.length; x++) {
			for(int y=0; y<tabMine[0].length; y++) {
				if(!tabMine[x][y]) {
					printChamp += String.valueOf((calculNbMine(x,y)));
					System.out.print(calculNbMine(x,y));
				}
				else {
					printChamp += "X";
					System.out.print("X");
				}
				System.out.print("  ");
			}
			System.out.print("\n");
		}
		return(printChamp);
	}
	
	/***
    *
    * @return le nombre de mines
    */
	public int getTotalNbMines() {
		return NBMINES;
	}
	
    /**
     * Calcul du nombre de voisins pour un endroit donné
     * @param x ligne de la case à considérer
     * @param y colonne de la case à considérer
     * @return return les bombes voisins
     */
	public int calculNbMine(int x, int y) {
		
		int nbMines = 0;
		int borneMinX, borneMinY, borneMaxX, borneMaxY;
		
		borneMinX = x==0 ? 0 : x-1;
		borneMinY = y==0 ? 0 : y-1;
		borneMaxX = x==tabMine.length-1 ? tabMine.length : x+2;
		borneMaxY = y==tabMine[0].length-1 ? tabMine[0].length : y+2;
		
		for (int i = borneMinX; i < borneMaxX; i++) {
			for(int j = borneMinY; j < borneMaxY; j++) {
				if(tabMine[i][j] && !(i==x && j==y)) {
					nbMines++;
				}
			}
		}
		return (nbMines);
	}
	
    /***
     * Fonction appelée par le constructeur de champ pour construire un nouveau champ 
     * et aussi pour faire une nouvelle partie en mode solo
     * @param level Level de la partie qu'on veut effectuer
     */
	public void newGame(Level level) {
		if(level == Level.EASY) {
			initChamp(10,10,10);
		}
		else if(level == Level.MEDIUM) {
			initChamp (20,20,40);
		}
		else if(level == Level.HARD) {
			initChamp (30,30,60);
		}
		else {
			initChamp (10,10,10);
		}
	}
	
    /***
     * Fonction appelée par le constructeur de champ pour construire un nouveau champ 
     * et aussi pour faire une nouvelle partie en mode solo
     * @param Dimension X custom nb de lignes
     * @param Dimension Y custom nb de colonnes
     * @param nbMines custom nb total de mines 
     */
	public void newGame(int DimX, int DimY, int nbMines) {
		initChamp(DimX,DimY,nbMines);
	}
	
	/***
	 * Dimension X du tableau de mines
	 * @return Dimension X du tableau de mines
	 */
	public int getDimX() {return tabMine.length;}
	
    /***
     * Dimension Y du tableau de mines
     * @return Dimension Y du tableau de mines
     */
	public int getDimY() {return tabMine[0].length;}
	  
    /***
     * Renvoie si il y a une mine à la position x,y
     * @param x ligne de la position à vérifier
     * @param y colonne de la position à vérifier
     * @return si il y a une bombe ou pas
     */
	public boolean isMin(int x, int y) {return tabMine[x][y];}

}
