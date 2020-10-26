package minesweeper;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

/** 
 * L'interface du Gui
 * @author Anna-Christiane Kolandjian
 * @revision 0.0
 */

public class GUIServ extends JPanel implements ActionListener {
	
	/**
	 * This serialVersionUID is for the Serializable GUIServ
	 */
	private static final long serialVersionUID = 1L;
	private JButton butStart = new JButton("Start");
	private JTextArea msgArea = new JTextArea(5,30);
	private JScrollPane scrollbar;
	private JTextField fillDimensionX;
	private JTextField fillDimensionY;
	private final JComboBox<String[]> fillLevel = new JComboBox(new String[] {"Easy","Medium", "Hard"});
	private Serveur serv;
	private boolean status_butStart = true;
	private JPanel panelNorth;
	private JLabel title;
	private JPanel box_title;
	
	/**
	 * constructeur du guiServeur
	 * @param serveur
	 * @param DimX de la textbox
	 * @param DimY de la textbox
	 */
	GUIServ(Serveur serveur, int DimX, int DimY){
		serv = serveur;
		fillDimensionX = new JTextField(String.valueOf(DimX));
		fillDimensionY = new JTextField(String.valueOf(DimX));
		msgArea.setEditable(false);
		DefaultCaret caret = (DefaultCaret)msgArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		scrollbar = new JScrollPane(msgArea);
		scrollbar.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		setLayout(new BorderLayout());
		
		panelNorth = new JPanel();
		panelNorth.setLayout(new BorderLayout());
		box_title = new JPanel();
		title = new JLabel("Welcome to the Server");
		title.setForeground(Color.white);
		box_title.setBackground(Color.GRAY);
		box_title.add(title);
		panelNorth.add(box_title);
		
		add(panelNorth, BorderLayout.NORTH);
		add(scrollbar, BorderLayout.CENTER);
		add(butStart, BorderLayout.SOUTH);
		butStart.addActionListener(this);
		//ajout des paramètres panelWest
		JPanel panelWest = new JPanel();
		panelWest.setLayout(new GridLayout(5,2));
		panelWest.add(new JLabel("Dimension x: "));
		panelWest.add(fillDimensionX);
		panelWest.add(new JLabel("Dimension y: "));
		panelWest.add(fillDimensionY);
		panelWest.add(new JLabel("Difficulté "));
		panelWest.add(fillLevel);
		add(panelWest, BorderLayout.WEST);
	}
	
	/**
	 * send start message
	 */
	public void changeStart() {
		status_butStart = true;
		butStart.setText("Start");
	}
	
	/**
	 * add a message on Gui Server
	 * @param args
	 */
	public void addMessage(String args) { 
		msgArea.append(args + '\n');
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==butStart && status_butStart) {
			if(serv.getNbJoueurConnect() == 0)
				addMessage("aucun joueur connecté, partie non démarré");
			else if(serv.getNbJoueurConnect() == 1)
				addMessage("Un seul joueur connecté, partie non démarré");
			else {
				if(Integer.parseInt(fillDimensionX.getText()) < 50 && Integer.parseInt(fillDimensionX.getText()) > 0 && Integer.parseInt(fillDimensionY.getText()) < 50 && Integer.parseInt(fillDimensionY.getText()) > 0) {
					if(fillLevel.getSelectedIndex() == 0)
						serv.beginGame(Integer.parseInt(fillDimensionX.getText()), Integer.parseInt(fillDimensionY.getText()), Level.EASY);
					else if(fillLevel.getSelectedIndex() == 1)
						serv.beginGame(Integer.parseInt(fillDimensionX.getText()), Integer.parseInt(fillDimensionY.getText()), Level.MEDIUM);
					else if(fillLevel.getSelectedIndex() == 2)
						serv.beginGame(Integer.parseInt(fillDimensionX.getText()), Integer.parseInt(fillDimensionY.getText()), Level.HARD);
					addMessage("start serveur");
					butStart.setText("partie en cours");
					status_butStart = false;
				}
				else {
					addMessage("Dimension trop grand. Choisissez entre 0 et 50)");
				}
			}
			
		}
		
	}
}
