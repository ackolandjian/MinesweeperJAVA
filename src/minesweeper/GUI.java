package minesweeper;

import java.awt.*; 
import java.awt.event.*;
import java.util.*;
import java.util.Map.Entry;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

/** 
 * La classe GUI qui est l'interface principale du jeu
 * @author Anna Christiane Kolandjian
 * @revision 0.0
 */

public class GUI extends JPanel implements ActionListener {
	/**
	 * Default serialVerisonUID for Serializable GUI
	 */
	private static final long serialVersionUID = 1L;
	private Demineur demin;
	private Case [][] tabCases;
	private int nbCaseRestantes = 0;
	private Compteur cpt = new Compteur();
	private boolean lostSolo = false;
	private boolean winSolo = false;
	private boolean first_time = true;
	private boolean state_connexion = false;
	
	private JTextField fillPort = new JTextField("3000");
	private JTextField fillName = new JTextField("Gui");
	private JTextField fillNameServ = new JTextField("localhost");
	private JButton butConnexion = new JButton("Connexion");
	private JScrollPane scrollbar;
	private JTextArea chatBox;
	private JButton btnSend = new JButton("Send");
	JTextField textMessage = new JTextField("");
	
	//Menu Item et Menu
	private JMenuItem mQuitter;
	private JMenuItem mReset;
	private JMenuItem mAbout;
    final JCheckBoxMenuItem beginner = new JCheckBoxMenuItem("Begineer");
    final JCheckBoxMenuItem intermediate = new JCheckBoxMenuItem("Intermediate");
    final JCheckBoxMenuItem expert = new JCheckBoxMenuItem("Expert");
    final JCheckBoxMenuItem custom = new JCheckBoxMenuItem("Custom");
    
	private JPanel panel_demin;
	private JPanel panelNorth;
	private JPanel panelSouth;
	private JPanel box_title;
	private JPanel panelEast;
	private JPanel subPanelEast;

	private JLabel title;
	Icon icon = new ImageIcon("img/happy.gif");
	private JButton emoji = new JButton(icon);
	private JTextArea[] scoreJoueur;


	GUI(Demineur demin) {
		this.demin = demin;
		setLayout(new BorderLayout());
		//création de la barre
		JMenuBar menuBar = new JMenuBar();

		//menu partie
		JMenu menuPartie = new JMenu("Partie");
		menuBar.add(menuPartie);
		menuBar.add(Box.createGlue());
		JMenu menuHelp = new JMenu("Help");
		menuBar.add(menuHelp);
		mReset = new JMenuItem("Reset", KeyEvent.VK_R);
		mReset.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.ALT_MASK));
		mQuitter = new JMenuItem("Quitter", KeyEvent.VK_E);
		mQuitter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
		mQuitter.setToolTipText("The End");
		mAbout = new JMenuItem("A propos de");
        ButtonGroup status = new ButtonGroup();
        status.add(beginner);
        status.add(intermediate);
        status.add(expert);
        status.add(custom);
		menuPartie.add(mReset);
		menuPartie.addSeparator();
		menuPartie.add(beginner);
		menuPartie.add(intermediate);
		menuPartie.add(expert);
		menuPartie.add(custom);
		menuPartie.addSeparator();
		menuPartie.add(mQuitter);
		menuHelp.add(mAbout);
		//afficher la barre
		demin.setJMenuBar(menuBar);
		
		//ajout des panels south
		panelSouth = new JPanel();
		chatBox = new JTextArea(5,5);
		chatBox.setEditable(false);
		DefaultCaret caret = (DefaultCaret)chatBox.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollbar = new JScrollPane(chatBox);
		scrollbar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		panelSouth.setLayout(new BorderLayout());
		JPanel subPanelSouth = new JPanel(); //for server, name, port and connection button
		JPanel subPanelSouth2 = new JPanel(); // for textbox and send button
		subPanelSouth.setLayout(new GridLayout());
		subPanelSouth2.setLayout(new GridLayout());
		subPanelSouth2.add(textMessage);
		subPanelSouth2.add(btnSend);
		butConnexion.setForeground(Color.red);
		butConnexion.setContentAreaFilled(false);
		subPanelSouth.add(new JLabel("nom serveur: "));
		subPanelSouth.add(fillNameServ);
		subPanelSouth.add(new JLabel("nom: "));
		subPanelSouth.add(fillName);
		subPanelSouth.add(new JLabel("Port: "));
		subPanelSouth.add(fillPort);
		subPanelSouth.add(butConnexion, BorderLayout.SOUTH);
		panelSouth.add(subPanelSouth, BorderLayout.SOUTH);
		panelSouth.add(subPanelSouth2, BorderLayout.CENTER);
		panelSouth.add(scrollbar, BorderLayout.NORTH);
		add(panelSouth, BorderLayout.SOUTH);
		
		//ajout du champ avec les cases
		panel_demin = new JPanel();
		panel_demin.setBackground(new Color(238,238,238));
		placeCases();
		add(panel_demin, BorderLayout.CENTER);

		//ajout du compteur et du titre
		panelNorth = new JPanel();
		panelNorth.setLayout(new BorderLayout());
		box_title = new JPanel();
		title = new JLabel("Welcome to Minesweeper 2020");
		title.setForeground(Color.white);
		box_title.setBackground(Color.GRAY);
		box_title.add(title);
		emoji.setPreferredSize(new Dimension(50,30));
        emoji.setBackground(Color.GRAY);
        emoji.setBorder(BorderFactory.createLoweredBevelBorder());
		box_title.add(emoji);
		panelNorth.add(box_title, BorderLayout.CENTER);
		panelNorth.add(cpt, BorderLayout.EAST);
		add(panelNorth, BorderLayout.NORTH);
		
		mReset.addActionListener(this);
		beginner.addActionListener(this);
		intermediate.addActionListener(this);
		expert.addActionListener(this);
		mQuitter.addActionListener(this);
		btnSend.addActionListener(this);
		butConnexion.addActionListener(this);
	}
	
	/**
	 * changer l'icon du bouton du nord
	 * @param icon
	 */
	public void setICON(Icon icon) {
		emoji.setIcon(icon);
		emoji.setPreferredSize(new Dimension(50,30));
	}
	
	/**
	 * créer le scoreboard
	 * @param listColor
	 * @param listJoueur
	 * @param nbJoueurConnect
	 */
	public void createScore(HashMap<String, Color> listColor,HashMap<String, Integer> listJoueur, int nbJoueurConnect) {
		panelEast = new JPanel();
		panelEast.setLayout(new BorderLayout());
		subPanelEast = new JPanel();
		JLabel scores = new JLabel("High Scores");
       // subPanelEast.setBackground(Color.WHITE);
       // subPanelEast.setForeground(Color.WHITE);
		subPanelEast.setLayout(new GridLayout(5,2)); // 5 max clients
		panelEast.setPreferredSize(new Dimension(100,100));
		subPanelEast.add(scores);
		panelEast.add(subPanelEast, BorderLayout.NORTH);
		add(panelEast, BorderLayout.EAST);
		panelEast.setLayout(new GridLayout(nbJoueurConnect, 1));
		int increment = 0;
		scoreJoueur = new JTextArea[nbJoueurConnect];
		for(Entry<String, Color> entry : listColor.entrySet()) {
			Color valeur = entry.getValue(); // couleur
		    String cle = entry.getKey(); // id
			JPanel sousPanel = new JPanel();
			sousPanel.setLayout(new GridLayout(1,2));
		    //JLabel nomJoueur = new JLabel(cle); // id du joueur et de la couleur
			//nomJoueur.setForeground(valeur); //couleur
			JTextArea nomJoueur = new JTextArea(cle);
			nomJoueur.setForeground(valeur);
			sousPanel.add(nomJoueur);
			scoreJoueur[increment] = new JTextArea();
			scoreJoueur[increment].setEditable(false);
			scoreJoueur[increment].setText(String.valueOf((listJoueur.get(cle))));
			scoreJoueur[increment].setForeground(valeur); //couleur
			//JLabel f = new JLabel();
		//	f.setText(nomJoueur.getText() + scoreJoueur[increment].getText());
			sousPanel.add(scoreJoueur[increment]);
			//sousPanel.add(f);
			subPanelEast.add(sousPanel);
			increment++;
			}
	}
	
	/**
	 * mettre à jour le scoreboard
	 * @param listJoueur
	 * @param nbJoueurConnect
	 */
	public void updateScore(HashMap<String, Integer> listJoueur, int nbJoueurConnect) {
		int increment2 = 0;
		int keyMaxValeur = 0;
		for(Entry<String, Integer> entry : listJoueur.entrySet()) {
			Integer valeur = entry.getValue(); //score
			scoreJoueur[increment2].setText(String.valueOf(valeur));
			scoreJoueur[increment2].setFont(new Font("Minecraft", 0, 12));
			//Si le score est le même, donc ce n'est pas le bon joueur qui a clické sur une case
			if (entry.getValue().compareTo(Integer.parseInt(scoreJoueur[keyMaxValeur].getText())) > 0) {
				keyMaxValeur = increment2;
				break;
			}
			increment2++;
		}
		scoreJoueur[keyMaxValeur].setFont(new Font("Minecraft", Font.BOLD, 12));
	}
	
	/**
	 * changer la couleur du panel nord selon la couleur du client
	 * @param color
	 */
	public void changePanelNorth(Color color) {
		box_title.setBackground(color);
		title.setText("voici votre couleur !");
	}
	
	/**
	 * Ajouter un message sur le chatbox des clients
	 * @param message
	 */
	public void add_message(String message) {
		chatBox.append(message + '\n');
	}
	
	/**
	 * changer le status du bouton connexion
	 * @param state
	 */
	public void change_state_connexion(boolean state) {
		if(state) {
			butConnexion.setText("disconnect");
			state_connexion = true;
		}
		else {
			butConnexion.setText("connect");
			state_connexion = false;
		}
	}
	
	/**
	 * si un joueur a perdu
	 */
	public void loose() {
		cpt.stop_cpt();
		JOptionPane.showMessageDialog(null, "Perdu !", "Perdu", JOptionPane.OK_OPTION);
		lostSolo = true;
		if(!demin.getCli().isJoueurConnected())
			revealAllMines();
	}
	
	
	/**
	 * @return si le joueur en solo mode a perdu
	 */
	public boolean isLostSolo() {
		return lostSolo;
	}
	
	/**
	 * @return si le joueur en solo mode a gangné
	 */
	public boolean isWinSolo() {
		return winSolo;
	}
	
	/**
	 * si tout le monde perd
	 */
	public void loose_all() {
		cpt.stop_cpt();
		JOptionPane.showMessageDialog(null, "Tout le monde à perdu !", "Perdu", JOptionPane.OK_OPTION);
		lostSolo = true;
	}

	/**
	 * @param i indice ligne de la case
	 * @param j indice colonne de la case
	 * @return la case du tableau des cases
	 */
	public Case getTabCase(int i, int j) {
		return(tabCases[i][j]);
	}
	
	/**
	 * surcharge des events
	 */
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==mQuitter) {
			if (state_connexion == true)
				JOptionPane.showMessageDialog(null, "Déconnéctez-vous avant !");
			else System.exit(0);
		}
		else if (e.getSource()==butConnexion) {

			if(state_connexion == false) {
				demin.getCli().setJoueurConnected(true) ;
				String nomServ = fillNameServ.getText();
				String nom = fillName.getText();
				int port = Integer.parseInt(fillPort.getText());
				demin.getCli().connexionServeur(nomServ, nom, port);
			}
			else {
				demin.getCli().disconnect();
				change_state_connexion(false);
			}

		}
		
		if(!state_connexion ) {
			if(e.getSource()==mReset) {
				demin.getChamp().placeMines();
				demin.getChamp().affText();
				cpt.stop_cpt();
				newPartie();
			}
			else if (e.getSource() == beginner) {
				demin.getChamp().newGame(Level.EASY);
				newPartie(Level.EASY);
			}
			else if (e.getSource() == intermediate) {
				demin.getChamp().newGame(Level.MEDIUM);
				newPartie(Level.MEDIUM);
			}
			else if (e.getSource() == expert) {
				demin.getChamp().newGame(Level.HARD);
				newPartie(Level.HARD);
			}
		}
		else if(state_connexion &&(e.getSource() == beginner||e.getSource()==mReset||e.getSource() == intermediate||e.getSource() == expert)) { 
				JOptionPane.showMessageDialog(null, "Déconnéctez-vous avant !");
		}
		
		if (state_connexion && e.getSource()==btnSend) {
			//JOptionPane.showMessageDialog(null, "I did not implement it yet!");
			//demin.getCli().sendMessage();
			//String msg = textMessage.getText();
		}
	}
	
	/**
	 * fonction non utilisé
	 * @return le message écrit dans le text box message
	 */
	public String getTextMessage() {
		return textMessage.getText();
	}

	/**
	 * nouvelle partie
	 */
	private void newPartie() {
		for (int i=0; i < demin.getChamp().getDimX();i++) {
			for (int j=0; j < demin.getChamp().getDimY();j++) {
				tabCases[i][j].newPartie();
			}
		}
		first_time = true;
		lostSolo = false;
		winSolo = false;
	} 
	
	/**
	 * win mode connecté avec un paramètre score
	 * @param score le score du joueur quand il a gagné
	 */
	public void Win(String score) {
		cpt.stop_cpt();
		
		for (int i=0; i < demin.getChamp().getDimX();i++) {
			for (int j=0; j < demin.getChamp().getDimY();j++) {
				if(!tabCases[i][j].isRevealed())
				tabCases[i][j].reveal_case();
			}
		}
		JOptionPane.showMessageDialog(null, "La partie est terminée !\n score : " + score, "gagné !", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * reveal all mines when lost, when you have other cases covered
	 */
	public void revealAllMines() {
		for (int i=0; i < demin.getChamp().getDimX();i++) {
			for (int j=0; j < demin.getChamp().getDimY();j++) {
				if(demin.getChamp().isMin(i, j)) {
					tabCases[i][j].setCaseValue(-1);
					tabCases[i][j].reveal_solo();
				}		
			}
		}
	}

	/**
	 * win solo mode, sans paramètres
	 */
	public void winSolo() {
		int score = cpt.getCompteur_txt();
		cpt.stop_cpt();
		JOptionPane.showMessageDialog(null, "La partie est terminée !\n score : " + score , "gagné !", JOptionPane.INFORMATION_MESSAGE);
		winSolo = true;
		//revealAllMines();
	}
	
	/**
	 * @return le nb de cases restantes
	 */
	public int getNbCaseRestantes() {
		return nbCaseRestantes;
	}
	
	/**
	 * mettre à jour le nb de cases restantes, qui sont couvertes
	 */
	public void decreaseNbCaseRestantes() {
		nbCaseRestantes--;
	}
	
	/**
	 * initialiser les nbCasesRestantes, appelé au début d'une partie
	 */
	public void setNbCaseRestantes() {
		nbCaseRestantes = demin.getChamp().getDimX()*demin.getChamp().getDimY();
	}
	
	/**
	 * mettre une nouvelle partie et initialiser les variables avec un paramètre level
	 * @param level
	 */
	public void newPartie(Level level) {
		panel_demin.removeAll();
		demin.getChamp().placeMines();
		placeCases();
		demin.pack();
		first_time = true;
		lostSolo = false;
		winSolo = false;
	}
	
	/**
	 * mettre une nouvelle partie et initialiser les variables 
	 * avec les dimensions comme paramètres
	 * @param DimX 
	 * @param DimY
	 */
	public void newPartie(int DimX, int DimY) {
		panel_demin.removeAll();
		placeCases();
		demin.pack();
		first_time = true;
		lostSolo = false;
		winSolo = false;
	}

	/**
	 * Placer les cases sur le champ
	 */
	private void placeCases() {
		panel_demin.setLayout(new GridLayout(demin.getChamp().getDimX(),demin.getChamp().getDimY()));

		tabCases = new Case[demin.getChamp().getDimX()][demin.getChamp().getDimY()];
		for (int i=0; i < demin.getChamp().getDimX();i++) {
			for (int j=0; j < demin.getChamp().getDimY();j++) {
				tabCases[i][j] = new Case(i, j, demin);
				panel_demin.add(tabCases[i][j]);
			}
		}
	}
	
/*public void shareMessage(Map<String, Integer> listJoueur, String nomJoueur) {
	if(listJoueur.containsKey(nomJoueur)) {
		demin.getGui().add_message("message sent from "+ nomJoueur );
	}
	else {
		demin.getGui().add_message(nomJoueur + ": sent a message." );
	}
}*/
	/**
	 * @return cpt le compteur
	 */
	public Compteur getCompteur() {
		return(cpt);
	}
	
	/**
	 * @return si first time
	 */
	public boolean getFirst_time() {
		return(first_time);
	}
	
	/**
	 * 
	 * @param setter pour initialiser le first_time
	 */
	public void setFirst_time(boolean setter) {
		first_time = setter;
	}
}
