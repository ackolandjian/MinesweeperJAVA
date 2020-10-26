package minesweeper;
import java.awt.Color; 
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/** 
 * Classe de Case qui correspond à une seule case du champ
 * @author Anna Christiane Kolandjian
 * @revision 0.0
 */

public class Case extends JPanel implements MouseListener{
	/**
	 * Default serialVerisonUID for Serializable Case
	 */
	private static final long serialVersionUID = 1L;
	public static final Color VERY_LIGHT_GRAY = new Color(238,238,238);
	private Demineur demin;
	private String txt = ""; //pour le compteur 
	private int x;
	private int y;
	private int caseValue;
	private String revealer;
	public int nbCaseRestantes; // qui ne sont pas revealed, mais couvertes
	private final static int DIM = 50; //dimension de la case
	private int mousePressed = 0;
	private boolean toggle = false;
	private boolean revealed;
	private boolean win;
	
	/**
	 * Constructeur de Case avec paramètres
	 * @param x custom la ligne de la case
	 * @param y custom la colonne de la case
	 */
	public Case(int x, int y, Demineur demin) {
		setPreferredSize(new Dimension(DIM-30,DIM));
		this.x = x;
		this.y = y;
		this.demin = demin;
		revealed = false;
		win = false;
		addMouseListener(this);
	}
	
	/**
	 * reveal une case 
	 * @param reveal -1,-2 ou la valeur de nbMinesAround
	 * @param player le joueur qui a clické
	 * 
	 */
	public void reveal_case( int reveal , String player) {
		mousePressed = 1;
		caseValue = reveal;
		revealer = player;
		repaint();
	}
	
	public void setCaseValue(int set){
		caseValue = set;
	}
	
	/**
	 * reveal une case si won
	 * 
	 */
	public void reveal_case() {
		mousePressed = 1;
		caseValue = -1;
		win = true;
		repaint();
	}
	
	/**
	 * reveal une case si en mode solo
	 * 
	 */
	public void reveal_solo() {
		mousePressed = 1;
		repaint();
	}
	
	/**
	 * Surcharge de la fonction paintComponent 
	 * @param Graphics gc
	 */
	public void paintComponent (Graphics gc) {
		super.paintComponent(gc);
		gc.setColor(VERY_LIGHT_GRAY);
		gc.fillRect(0, 0, this.getWidth(), this.getHeight());
		
		if(mousePressed == 1) {
			if(!win && demin.getCli().getNbJoueurConnect() >1) {
				gc.setColor(demin.getCli().getListColor().get(revealer));
				gc.fillRect(8, 8, this.getWidth()-12, this.getHeight()-12);
			}
			else if (!win && demin.getCli().isJoueurConnected() == false) {
				gc.setColor(Color.LIGHT_GRAY);
				gc.fillRect(6, 6, this.getWidth(), this.getWidth());
			}
			if(caseValue == -1) {
				BufferedImage image;			
				try {
				image = ImageIO.read(new File("img/bombe.png"));
				gc.drawImage(image, 8, 8, getWidth() - 10, getHeight() - 10, this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else if(caseValue == -2) {
				BufferedImage image;			
				try {
				image = ImageIO.read(new File("img/star.png"));
				gc.drawImage(image, 8, 8, getWidth() - 10, getHeight() - 10, this);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {	
				txt = String.valueOf(caseValue);
				gc.setColor(Color.white);
				//change colors if solo mode
				if(demin.getCli().getNbJoueurConnect()<=0) {
					if(demin.getChamp().calculNbMine(x,y)==1) {gc.setColor(Color.BLUE);}
					else if(demin.getChamp().calculNbMine(x,y) == 2) {gc.setColor(Color.RED);}
					else if(demin.getChamp().calculNbMine(x,y) == 3) {gc.setColor(new Color(40, 180, 99));}
					else if(demin.getChamp().calculNbMine(x,y) == 4) {gc.setColor(new Color(155, 89, 182));}
					else if(demin.getChamp().calculNbMine(x,y) == 5) {gc.setColor(new Color(240, 128, 128));}
					else if(demin.getChamp().calculNbMine(x,y) == 6) {gc.setColor(new Color(241, 196, 15));}
				}
				gc.setFont(new Font ("Minecraft", 1, 24));
				gc.drawString(txt, (this.getWidth()/2)-1, (this.getHeight()/2)+10);
				if(win == false) {
					revealed = true;
				}

			} 
		}
		else if (mousePressed == 2){
			BufferedImage image;
			try {
			image = ImageIO.read(new File("img/flag.png"));
			gc.drawImage(image, 8, 8, getWidth() - 10, getHeight() - 10, this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		else {
			gc.setColor(Color.GRAY);
			//gc.fillRect(7, 7, this.getWidth()-12, this.getHeight()-12);
			gc.fillRect(6, 6, this.getWidth(), this.getWidth());
				/*BufferedImage image;
				try {
				image = ImageIO.read(new File("img/case.png"));
				gc.drawImage(image, 8, 8, getWidth() - 10, getHeight() - 10, this);
				} catch (IOException e) {
					e.printStackTrace();
				}*/
		}
	}
	
	/**
	 * Fonction qui permet de savoir si une case est revealed ou pas
	 * @return boolean revealed
	 */
	public boolean isRevealed() {
		return(revealed);
	}

	/**
	 * Pour faire une nouvelle partie, réinitialiser les variables et rendre les cases
	 * covered again, cette fonction est utilisée par GUI
	 * 
	 */
	public void newPartie() {
		mousePressed = 0;
		toggle = false;
		repaint();
		revealed = false;
	}
	
	/**
	 * Les events de MouseListener
	 * @param MouseEvent e 
	 */
	public void mousePressed (MouseEvent e) {
		System.out.println();
		//first time process, either connected or disconnected
		if(demin.getGui().getFirst_time() == true && (demin.getCli().getStatePartie()||demin.getCli().getNbJoueurConnect()<=1||demin.getCli().isJoueurConnected()==false)) {
			demin.getGui().getCompteur().start_cpt();
			demin.getGui().setFirst_time(false);
			//si connecté(e) seul(e) ou pas connecté(e)
			if(demin.getCli().getNbJoueurConnect()<=1){
				demin.getGui().setICON(new ImageIcon("img/happy.gif"));
				demin.getGui().setNbCaseRestantes();
				demin.getChamp().placeMines();
				demin.getChamp().affText();
				if (demin.getCli().isJoueurConnected()==false) demin.getGui().add_message("Solo mode!");
			}
		}
		//while connecting/disconnecting
		if(demin.getCli().getNbJoueurConnect()<=1){
			if (demin.getCli().isJoueurConnected()==true){
				demin.getGui().add_message("Attendez un autre joueur !");
				demin.getGui().getCompteur().reset_cpt();
				demin.getGui().getCompteur().stop_cpt();
			}
		}
		
		if(e.getButton() == MouseEvent.BUTTON1) {
			//A player connected with 1 or more players
			if (demin.getCli().getNbJoueurConnect()>1 && demin.getCli().isJoueurConnected()==true) {
				if(!toggle && !demin.getCli().getLoose()  && !revealed && demin.getCli().getStatePartie()) {
					demin.getCli().sendData(String.valueOf(x));
					demin.getCli().sendData(String.valueOf(y));
					mousePressed = 1;
				};
			}
			//single mode ou connecté seul(e)
			if (demin.getCli().getNbJoueurConnect()<=1 && !demin.getCli().isJoueurConnected() && !revealed && !demin.getGui().isLostSolo() && !demin.getGui().isWinSolo()) {
				//if lost
				if(demin.getChamp().isMin(x,y)) {
					demin.getGui().setICON(new ImageIcon("img/crape.gif"));
					caseValue = -1;}
				//if did not loose
				else {
					demin.getGui().decreaseNbCaseRestantes();
					//if won
					if((demin.getGui().getNbCaseRestantes() - demin.getChamp().getTotalNbMines()) == 0 ) {
						demin.getGui().winSolo();
						caseValue = -2;
					}
					//if not mine or did not win
					else caseValue = demin.getChamp().calculNbMine(x,y);
				}
				reveal_solo();
			}
		}
		else if (e.getButton() == MouseEvent.BUTTON3 && revealed == false) {
			if(!toggle) {
			mousePressed = 2;
			}
			else {
				mousePressed = 3;
			}
			toggle = !toggle;
		}
		repaint(); //on appelle paint component
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		if(mousePressed == 1) {
			if(caseValue == -1) {
				demin.getGui().loose();
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}

