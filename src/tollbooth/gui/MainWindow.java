package tollbooth.gui;

import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import javax.swing.JScrollPane;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JTextField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import tollbooth.plaza.TollPlaza;
import tollbooth.terminals.Terminal;
import tollbooth.vehicles.*;

public class MainWindow {
	static BorderMainThread bmt = null;
	JFrame frmTollPlaza;
	JTextField plateNumberField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		MainWindow window = new MainWindow();
		bmt = new BorderMainThread(window);
		bmt.start();
	}
	
	private void stopSimulation() {
		TollPlaza.stopSimulation();
		bmt.interrupt();
	}
	
	private void restartSimulation() {
		TollPlaza.stopSimulation();
		TollPlaza.isRunning = true;
		bmt.interrupt();
		bmt.run();
	}

	/**
	 * Create the application.
	 */
	public MainWindow() {
		initialize();
	}


	/**
	 * Initialise the contents of the frame.
	 */
	private void initialize() {
		frmTollPlaza = new JFrame();
		frmTollPlaza.setTitle("Toll Plaza");
		frmTollPlaza.setResizable(false);
		frmTollPlaza.setBounds(100, 100, 859, 590);
		frmTollPlaza.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmTollPlaza.getContentPane().setLayout(null);
		
		findVehicleBtn = new JButton("Find");
		findVehicleBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Object result = findMatchingPlates(plateNumberField.getText());
				
				if (result == null) {
					result = "Not found!";
				}
				else result = (Vehicle)result;
				foundTextArea.setText("VEHICLE | DRIVER | PASSENGERS | ADDITIONAL\n" + result.toString());
			}
		});
		
		findVehicleBtn.setFont(new Font("Fira Code", Font.PLAIN, 10));
		findVehicleBtn.setBounds(147, 409, 103, 19);
		frmTollPlaza.getContentPane().add(findVehicleBtn);
		
		plateNumberField = new JTextField();
		plateNumberField.setBounds(8, 410, 131, 17);
		frmTollPlaza.getContentPane().add(plateNumberField);
		plateNumberField.setColumns(10);
		
		remainingVehiclesPane = new JScrollPane();
		remainingVehiclesPane.setBounds(258, 8, 131, 420);
		frmTollPlaza.getContentPane().add(remainingVehiclesPane);
		
		remainingVehiclesList = new JList<String>();
		remainingVehiclesPane.setViewportView(remainingVehiclesList);
		remainingVehiclesList.setModel(TollPlaza.remainingVehModel);
		
		waitingLbl = new JLabel("In queue");
		waitingLbl.setHorizontalAlignment(SwingConstants.CENTER);
		remainingVehiclesPane.setColumnHeaderView(waitingLbl);
		waitingLbl.setFont(new Font("Fira Code", Font.ITALIC, 11));
		
		finedVehiclesPane = new JScrollPane();
		finedVehiclesPane.setBounds(393, 8, 193, 201);
		frmTollPlaza.getContentPane().add(finedVehiclesPane);
		
		 finedVehiclesList = new JList<String>();
		finedVehiclesPane.setViewportView(finedVehiclesList);
		finedVehiclesList.setModel(TollPlaza.criminalsModel);
		
		finedLbl = new JLabel("Civilians apprehended");
		finedVehiclesPane.setColumnHeaderView(finedLbl);
		finedLbl.setHorizontalAlignment(SwingConstants.CENTER);
		finedLbl.setFont(new Font("Fira Code", Font.ITALIC, 11));
		
		crossedVehiclesPane = new JScrollPane();
		crossedVehiclesPane.setBounds(393, 217, 193, 211);
		frmTollPlaza.getContentPane().add(crossedVehiclesPane);
		
		crossedVehiclesList = new JList<String>();
		crossedVehiclesPane.setViewportView(crossedVehiclesList);
		crossedVehiclesList.setModel(TollPlaza.crossedModel);
		
		crossedLbl = new JLabel("Vehicles crossed");
		crossedVehiclesPane.setColumnHeaderView(crossedLbl);
		crossedLbl.setFont(new Font("Fira Code", Font.ITALIC, 11));
		crossedLbl.setHorizontalAlignment(SwingConstants.CENTER);
		
		eventsScrollPane = new JScrollPane();
		eventsScrollPane.setBounds(594, 8, 243, 420);
		frmTollPlaza.getContentPane().add(eventsScrollPane);
		
		eventsList = new JList<String>();
		eventsScrollPane.setViewportView(eventsList);
		eventsList.setModel(TollPlaza.eventsModel);
		
		eventsLbl = new JLabel("Events");
		eventsLbl.setFont(new Font("Fira Code", Font.ITALIC, 11));
		eventsLbl.setHorizontalAlignment(SwingConstants.CENTER);
		eventsScrollPane.setColumnHeaderView(eventsLbl);
		
		stopSimBtn = new JButton("STOP");
		stopSimBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Simulation stopped
				stopSimulation();
			}
		});
		stopSimBtn.setBackground(new Color(255, 74, 74));
		stopSimBtn.setFont(new Font("Fira Code", Font.BOLD, 11));
		stopSimBtn.setBounds(8, 302, 88, 40);
		frmTollPlaza.getContentPane().add(stopSimBtn);
		
		restartSimBtn = new JButton("RESTART");
		restartSimBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Simulation restarted
				restartSimulation();
			}
		});
		restartSimBtn.setBackground(new Color(0, 128, 255));
		restartSimBtn.setFont(new Font("Fira Code", Font.BOLD, 11));
		restartSimBtn.setBounds(8, 350, 88, 40);
		frmTollPlaza.getContentPane().add(restartSimBtn);
		
		panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0), 2));
		panel.setBounds(8, 436, 829, 109);
		frmTollPlaza.getContentPane().add(panel);
		panel.setLayout(null);
		
		foundTextArea = new JTextArea();
		foundTextArea.setBackground(new Color(240, 240, 240));
		foundTextArea.setBounds(8, 8, 813, 93);
		panel.add(foundTextArea);
		foundTextArea.setWrapStyleWord(true);
		foundTextArea.setEditable(false);
		foundTextArea.setLineWrap(true);
		foundTextArea.setFont(new Font("Fira Code Medium", Font.PLAIN, 11));
		
		cst1Lbl = new JLabel("CST1");
		cst1Lbl.setHorizontalAlignment(SwingConstants.CENTER);
		cst1Lbl.setFont(new Font("Fira Code", Font.BOLD, 14));
		cst1Lbl.setBounds(8, 11, 60, 30);
		frmTollPlaza.getContentPane().add(cst1Lbl);
		
		cst2Lbl = new JLabel("CST2");
		cst2Lbl.setHorizontalAlignment(SwingConstants.CENTER);
		cst2Lbl.setFont(new Font("Fira Code", Font.BOLD, 14));
		cst2Lbl.setBounds(190, 11, 60, 30);
		frmTollPlaza.getContentPane().add(cst2Lbl);
		
		pol1Lbl = new JLabel("POL1");
		pol1Lbl.setForeground(Color.BLACK);
		pol1Lbl.setHorizontalAlignment(SwingConstants.CENTER);
		pol1Lbl.setFont(new Font("Fira Code", Font.BOLD, 14));
		pol1Lbl.setBounds(8, 49, 60, 30);
		frmTollPlaza.getContentPane().add(pol1Lbl);
		
		pol2Lbl = new JLabel("POL2");
		pol2Lbl.setHorizontalAlignment(SwingConstants.CENTER);
		pol2Lbl.setFont(new Font("Fira Code", Font.BOLD, 14));
		pol2Lbl.setBounds(97, 49, 60, 30);
		frmTollPlaza.getContentPane().add(pol2Lbl);
		
		pol3Lbl = new JLabel("POL3");
		pol3Lbl.setHorizontalAlignment(SwingConstants.CENTER);
		pol3Lbl.setFont(new Font("Fira Code", Font.BOLD, 14));
		pol3Lbl.setBounds(190, 49, 60, 30);
		frmTollPlaza.getContentPane().add(pol3Lbl);
		
		veh0lbl = new JLabel("[1]");
		veh0lbl.setHorizontalAlignment(SwingConstants.CENTER);
		veh0lbl.setFont(new Font("Fira Code", Font.BOLD, 14));
		veh0lbl.setBounds(97, 87, 60, 30);
		frmTollPlaza.getContentPane().add(veh0lbl);
		
		veh1lbl = new JLabel("[2]");
		veh1lbl.setHorizontalAlignment(SwingConstants.CENTER);
		veh1lbl.setFont(new Font("Fira Code", Font.BOLD, 14));
		veh1lbl.setBounds(107, 125, 60, 30);
		frmTollPlaza.getContentPane().add(veh1lbl);
		
		veh2lbl = new JLabel("[3]");
		veh2lbl.setHorizontalAlignment(SwingConstants.CENTER);
		veh2lbl.setFont(new Font("Fira Code", Font.BOLD, 14));
		veh2lbl.setBounds(117, 163, 60, 30);
		frmTollPlaza.getContentPane().add(veh2lbl);
		
		veh3lbl = new JLabel("[4]");
		veh3lbl.setHorizontalAlignment(SwingConstants.CENTER);
		veh3lbl.setFont(new Font("Fira Code", Font.BOLD, 14));
		veh3lbl.setBounds(127, 201, 60, 30);
		frmTollPlaza.getContentPane().add(veh3lbl);
		
		veh4lbl = new JLabel("[5]");
		veh4lbl.setHorizontalAlignment(SwingConstants.CENTER);
		veh4lbl.setFont(new Font("Fira Code", Font.BOLD, 14));
		veh4lbl.setBounds(137, 239, 60, 30);
		frmTollPlaza.getContentPane().add(veh4lbl);
		
		elapsedTextLbl = new JLabel("Elapsed:");
		elapsedTextLbl.setFont(new Font("Fira Code Light", Font.PLAIN, 12));
		elapsedTextLbl.setBounds(8, 275, 60, 19);
		frmTollPlaza.getContentPane().add(elapsedTextLbl);
		
		timeElapsedLbl = new JLabel("$$$");
		timeElapsedLbl.setHorizontalAlignment(SwingConstants.RIGHT);
		timeElapsedLbl.setFont(new Font("Fira Code Light", Font.PLAIN, 11));
		timeElapsedLbl.setBounds(65, 279, 29, 11);
		frmTollPlaza.getContentPane().add(timeElapsedLbl);
	}
	
	protected Vehicle findMatchingPlates(String plates) {
		for (Vehicle veh : TollPlaza.vehicles) {
			if (veh.registrationPlates.equals(plates))
				return veh;
		}
		
		return null;
	}
	
	private static void resetLabelUtil(JLabel lab) {
		lab.setText("---");
		lab.setForeground(Color.BLACK);
	}
	
	public static void resetVehicleLabels() {
		MainWindow.resetLabelUtil(veh0lbl);
		MainWindow.resetLabelUtil(veh1lbl);
		MainWindow.resetLabelUtil(veh2lbl);
		MainWindow.resetLabelUtil(veh3lbl);
		MainWindow.resetLabelUtil(veh4lbl);
	}
	


	
	// ---
	Color carColor = new Color(255, 128, 128);
	Color busColor = new Color(128, 255, 128);
	Color truckColor = new Color(128, 128, 255);
	
	JButton findVehicleBtn;
	JList<String> remainingVehiclesList;
	JScrollPane remainingVehiclesPane;
	JLabel waitingLbl;
	JList<String> finedVehiclesList;
	JLabel finedLbl;
	JScrollPane crossedVehiclesPane;
	JLabel eventsLbl;
	JButton restartSimBtn;
	JScrollPane finedVehiclesPane;
	JScrollPane eventsScrollPane;
	JList<String> crossedVehiclesList;
	JLabel crossedLbl;
	JList<String> eventsList;
	JButton stopSimBtn;
	JTextArea foundTextArea;
	JPanel panel;	
	JLabel cst1Lbl;
	JLabel cst2Lbl;
	JLabel pol1Lbl;
	JLabel pol3Lbl;
	JLabel pol2Lbl;
	static JLabel veh0lbl;
	static JLabel veh1lbl;
	static JLabel veh2lbl;
	static JLabel veh3lbl;
	static JLabel veh4lbl;
	JLabel elapsedTextLbl;
	JLabel timeElapsedLbl;
}
