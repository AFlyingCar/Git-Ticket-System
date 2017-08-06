import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class StartGui extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private JButton newTicketButton;
	private JButton listTicketsButton;
	private JButton syncButton;
	private JButton changeUserButton;
	
	private static StartGui instance;
	
	private StartGui() {
		initUI();
	}
	
	public static StartGui getInstance() {
		if(instance == null) {
			instance = new StartGui();
		}
		
		return instance;
	}
	
	public void buildComponents() {
		buildButtons();
	}
	
	public void buildButtons() {
		newTicketButton = new JButton("New");
		newTicketButton.addActionListener((ActionEvent event) -> {
			EventQueue.invokeLater(() -> {
				NewTicketGui ntg = new NewTicketGui();
				ntg.setVisible(true);
			});
		});
		
		listTicketsButton = new JButton("List");
		listTicketsButton.addActionListener((ActionEvent event) -> {
            EventQueue.invokeLater(() -> {
                ListTicketsGui ltg = ListTicketsGui.getInstance();
                
                // Update before opening the gui
                ltg.updateTicketList();
                ltg.updateTicketTable();
                
                ltg.setVisible(true);
            });
        });
		
		syncButton = new JButton("Sync");
		syncButton.addActionListener((ActionEvent event) -> {
		    if(!GitUtil.sync()) {
		        JOptionPane.showMessageDialog(null, "Failed to sync.", "Sync error.", JOptionPane.ERROR_MESSAGE);
		    } else {
		        JOptionPane.showMessageDialog(null, "Sync Successful.");
		    }
		});
		
		changeUserButton = new JButton("Change User");
		changeUserButton.addActionListener((ActionEvent event) -> {
		    GitUtil.fixRemote();
		});
	}
	
	public void initUI() {
		buildComponents();
		
		setSize(300, 200);
		
		getContentPane().setLayout(null);
		
		addComponent(newTicketButton, 50, 25);
		addComponent(listTicketsButton, 190, 25);
		addComponent(syncButton, 50, 100);
		addComponent(changeUserButton, 190 - (listTicketsButton.getPreferredSize().width / 2), 100);
		
		// createLayout(newTicketButton, listTicketsButton, syncButton);
		
		setTitle("Ticket System");
		
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
	}
	
	public void addComponent(JComponent component, int xPos, int yPos) {
		getContentPane().add(component);
		
		Insets insets = getContentPane().getInsets();
		Dimension size = component.getPreferredSize();
		
		component.setBounds(xPos + insets.left, yPos + insets.top, size.width, size.height);
	}
}
