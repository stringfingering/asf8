package hello;

//import com.sun.
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CtlBox extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	//Graphical components, in order of vertical display.
	InstPanel instPanel;
	AbcChord2 abcChord;
	Hello topo;
	Hello msg;
	EntryPanel3 entryPanel;
	GluePanel gluePanel;
	JButton showGlue;
	JLabel currentTextDisplay; // jb
	
	
	RangeImage rangeImage;
	Rate2 rate;
	JFrame sf;
	JPanel sP;
	JButton survey;
	Boolean tookSurvey;
	boolean surveyWinOpen;
	
	
	
	//FingerboardPanel fbp;
	//JMenuBar menuBar;
	//InstMenu instMenu;
	//JMenu aboutMenu;
	
	
	

	public CtlBox(Cfinger2 c) {
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		// GridLayout g = new GridLayout(0,1,0,30);

		//BoxLayout g = new BoxLayout(this, BoxLayout.Y_AXIS);
		//this.setLayout(g);
		setLayout(new GridBagLayout());
		GridBagConstraints c1 = new GridBagConstraints();
		GridBagConstraints c2 = new GridBagConstraints();
		GridBagConstraints c3 = new GridBagConstraints();
		GridBagConstraints c4 = new GridBagConstraints();
		GridBagConstraints c5 = new GridBagConstraints();
		GridBagConstraints c6 = new GridBagConstraints();
		GridBagConstraints c7 = new GridBagConstraints();
		GridBagConstraints c8 = new GridBagConstraints();
		
		
		//this.setM
		abcChord = new AbcChord2(c);
		entryPanel = new EntryPanel3();
		instPanel = new InstPanel();
		gluePanel = new GluePanel();
		showGlue = new JButton("Show glue");
		currentTextDisplay = new JLabel();//jb
		
		
		
		//currentTextDisplay.setText("Nothing to say.");
		
		rate = new Rate2(c);
		msg = new Hello("Enter up to 4 notes:"); // Hello();
		topo = new Hello("Topology:");
		//positions = new Hello("Positions");
		survey = new JButton("Very brief survey");
		// rangeImage = new RangeImage();
		
		
		//// add(rangeImage, g);
		//// add(Box.createVerticalGlue());
		
		/*add(Box.createRigidArea(new Dimension(0, 15)));
		
		add(instPanel, g);
		add(Box.createRigidArea(new Dimension(0, 5)));
		
		add(abcChord,g);
		
		add(Box.createRigidArea(new Dimension(0, 12)));
		add(topo, g);
		//add(positions,g);
		add(Box.createRigidArea(new Dimension(0, 5)));
		add(msg, g);
		add(Box.createRigidArea(new Dimension(0, 5)));
		add(entryPanel, g);
		
		
		add(Box.createRigidArea(new Dimension(0, 10)));
		add(gluePanel, g);
		add(Box.createRigidArea(new Dimension(0, 5)));
		add(showGlue, g);
		
		add(Box.createRigidArea(new Dimension(0, 8)));
		// TODO Put it a little more to the left
		add(currentTextDisplay, g);
		
		// add(next, g);
		
		//add(Box.createRigidArea(new Dimension(0, 50)));
		//add(rate, g);
		// add(Box.createVerticalGlue());
		
		
		add(Box.createRigidArea(new Dimension(0, 10)));*/
		
		/*
		tookSurvey = false;
		
		sP = new JPanel();
		//sP.add(survey);
		;
		add(sP, g);*/

		c1.gridx = 0;
		c1.gridy = 0;
		c1.fill = GridBagConstraints.BOTH;
		c1.weightx = 0;
		c1.weighty = 0.1;
		c1.anchor = GridBagConstraints.PAGE_END;
		add(instPanel, c1);
		
		c2.gridx = 0;
		c2.gridy = 1;
		c2.fill = GridBagConstraints.BOTH;
		c2.weightx = 0;
		c2.weighty = 0.3;
		c2.insets = new Insets(10, 0, 0, 0);
		add(abcChord, c2);
		
		c3.gridx = 0;
		c3.gridy = 2;
		c3.fill = GridBagConstraints.VERTICAL;
		c3.weightx = 0;
		c3.weighty = 0.3;
		add(topo, c3);
		
		c4.gridx = 0;
		c4.gridy = 3;
		c4.fill = GridBagConstraints.VERTICAL;
		c4.weightx = 0;
		c4.weighty = 0.3;
		add(msg, c4);
		
		c5.gridx = 0;
		c5.gridy = 4;
		c5.fill = GridBagConstraints.VERTICAL;
		c5.weightx = 0;
		c5.weighty = 0.25;
		add(entryPanel, c5);
		
		c6.gridx = 0;
		c6.gridy = 5;
		c6.fill = GridBagConstraints.VERTICAL;
		c6.weightx = 0;
		c6.weighty = 0.1;
		add(gluePanel, c6);
		
		c7.gridx = 0;
		c7.gridy = 6;
		c7.fill = GridBagConstraints.VERTICAL;
		c7.weightx = 0;
		c7.weighty = 0.1;
		add(showGlue, c7);
		
		c8.gridx = 0;
		c8.gridy = 7;
		c8.fill = GridBagConstraints.VERTICAL;
		c8.weightx = 0;
		c8.weighty = 0.1;
		add(currentTextDisplay, c8);
		
		
		sf = new JFrame("Survey says ...");
		JPanel ss = new surveyForm(sf, this);
		sf.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				sf.setVisible(false);
			}
		});
		
		sf.add(ss);
		sf.pack();
		survey.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// System.out.println("SURVEY SAYS:");

				sf.setVisible(true);

			}
		});

		////add(Box.createRigidArea(new Dimension(0, 300)));
		//add(Box.createVerticalGlue()); // TODO
		//// msg.say("Enter 2,3 or 4 notes:");
	
	}

	public void clear() {
		// next.disable();
	}

}