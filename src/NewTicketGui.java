import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultStyledDocument;

/**
 * @author Tyler Robbins
 * @version 1.0
 * @since 0.1
 */
public class NewTicketGui extends JDialog {
    /**
     * Done only because eclipse wouldn't shut the fuck up otherwise.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * The maximum amount of characters allowed in a ticket title.
     */
    public static final int TITLE_TEXT_AREA_MAX_CHARS = 55;
    
    /**
     * The text area where the ticket's title is inputted.
     */
    private JTextArea titleTextArea;
    
    /**
     * The text area where the ticket's details are inputted.
     */
	private JTextArea detailsTextArea;
	
	/**
	 * The scrollpane for the details input text area.
	 */
	private JScrollPane detailsTextAreaScrollPane;
	
	/**
	 * The button to submit a ticket.
	 */
	private JButton submitButton;
	
	/**
	 * The button to cancel a ticket.
	 */
	private JButton cancelButton;
	
	/**
	 * The combo box of different ticket types.
	 */
	private JComboBox<Ticket.TicketType> typeComboBox;
	
	/**
	 * The combo box of different ticket priorities.
	 */
	private JComboBox<Ticket.TicketPriority> priorityComboBox;
	
	/**
	 * The label for the title's text area.
	 */
	private JLabel titleTextAreaLabel;
	
	/**
	 * The label for the details' text area.
	 */
	private JLabel detailsTextAreaLabel;
	
	/**
	 * The label for the type's combo box.
	 */
	private JLabel typeComboBoxLabel;
	
	/**
	 * The label for the priority's combo box.
	 */
	private JLabel priorityComboBoxLabel;
	
	/**
	 * The newly created ticket.
	 */
	private Ticket ticket;
	
	/**
	 * Has the ticket been submitted yet
	 */
	private boolean submitted = false;
	
	/**
	 * An array of all the options that can be in the type combo box
	 */
	public final Ticket.TicketType[] typeOptions = {
        Ticket.TicketType.FEATURE,
        Ticket.TicketType.HOTFIX,
        Ticket.TicketType.RELEASE,
        Ticket.TicketType.SUPPORT
	};
	
	/**
	 * An array of all the options that can be in the priority combo box.
	 */
	public final Ticket.TicketPriority[] priorityOptions = {
	        Ticket.TicketPriority.LOW,
	        Ticket.TicketPriority.NORMAL,
	        Ticket.TicketPriority.HIGH
	};
	
	/**
	 * Constructs a NewTicketGui. Also initializes the UI elements.
	 */
	public NewTicketGui() {
		initUI();
		setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
	}
	
	/**
	 * Creates a new Ticket based on the contents of the fields.
	 * @return The ticket that was just created.
	 */
	public Ticket buildNewTicket() {
	    return new Ticket(titleTextArea.getText(),
                          detailsTextArea.getText(),
                          GitUtil.getAuthor(),
                          new Date(),
                          (Ticket.TicketType)typeComboBox.getSelectedItem(),
                          (Ticket.TicketPriority)priorityComboBox.getSelectedItem(),
                          Ticket.TicketStatus.OPEN);
	}
	
	/**
	 * Constructs and submits a new ticket.
	 * <p>
	 * Will write a ticket to the filename MD5Hash.ticket, and commit it. Will show an error message
     *  dialog box on error.
	 * @return 0 if the submission was successful, 1 otherwise.
	 */
	public int submitTicket() {
	    if(titleTextArea.getText().length() == 0 || detailsTextArea.getText().length() == 0) {
	        JOptionPane.showMessageDialog(null, "Ticket must have both a title and details.",
	                                      "Incomplete ticket", JOptionPane.ERROR_MESSAGE);
	        return 1;
	    }
	    
	    ticket = buildNewTicket();
	    
	    try {
	        FileUtil.writeTicket(ticket);
	        GitUtil.commit(ticket.getMD5ID() + ".ticket");
	        
	        submitted = true;
	    } catch(IOException e) {
	        System.err.println(e);
	        
	        JOptionPane.showMessageDialog(null, "Failed to write ticket to disk.", "I/O Error",
	                                      JOptionPane.ERROR_MESSAGE);
	        return 1;
	    }
	    
	    return 0;
	}
	
	/**
	 * Builds all the components of the UI.
	 */
	public void buildComponents() {
		buildButtons();
		buildTextAreas();
		buildLabels();
		buildScrollPanes();
		buildComboBoxes();
		buildWindowListeners();
	}
	
	/**
	 * Builds all of the labels for this UI.
	 */
	public void buildLabels() {
		titleTextAreaLabel = new JLabel("Title (" + TITLE_TEXT_AREA_MAX_CHARS + "): ");
		titleTextAreaLabel.setFont(new Font("Courier", Font.PLAIN, 15));
		
		detailsTextAreaLabel = new JLabel("Details: ");
		detailsTextAreaLabel.setFont(new Font("Courier", Font.PLAIN, 15));
		
		typeComboBoxLabel = new JLabel("Type: ");
		typeComboBoxLabel.setFont(new Font("Courier", Font.PLAIN, 15));
		
		priorityComboBoxLabel = new JLabel("Priority: ");
		priorityComboBoxLabel.setFont(new Font("Courier", Font.PLAIN, 15));
	}
	
	/**
	 * Builds all the text areas for this UI.
	 */
	public void buildTextAreas() {
		titleTextArea = new JTextArea("", 1, TITLE_TEXT_AREA_MAX_CHARS);
		
        DefaultStyledDocument tta_doc = new DefaultStyledDocument();
        tta_doc.setDocumentFilter(new DocumentSizeFilter(titleTextArea.getColumns()));
		
		titleTextArea.setEditable(true);
		titleTextArea.setDocument(tta_doc);
		titleTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		titleTextArea.setFont(new Font("Courier", Font.PLAIN, 15));
		titleTextArea.getDocument().addDocumentListener(new DocumentListener() {
		    public void insertUpdate(DocumentEvent event) {
		        titleTextAreaLabel.setText("Title (" +
		                                   (TITLE_TEXT_AREA_MAX_CHARS -
	                                        titleTextArea.getText().length()) +
		                                   "): ");
		    }
		    
		    public void removeUpdate(DocumentEvent event) {
                titleTextAreaLabel.setText("Title (" +
                                           (TITLE_TEXT_AREA_MAX_CHARS -
                                            titleTextArea.getText().length()) +
                                           "): ");
		    }
		    
		    public void changedUpdate(DocumentEvent event) {
	              titleTextAreaLabel.setText("Title (" +
	                                         (TITLE_TEXT_AREA_MAX_CHARS -
	                                          titleTextArea.getText().length()) +
	                                         "): ");
	              
		    }
		});
		
		detailsTextArea = new JTextArea("", 10, 55);
		detailsTextArea.setEditable(true);
		detailsTextArea.setLineWrap(true);
		detailsTextArea.setFont(new Font("Courier", Font.PLAIN, 15));
	}
	
	/**
	 * Builds all the scroll panes for this UI.
	 */
	public void buildScrollPanes() {
		detailsTextAreaScrollPane = new JScrollPane(detailsTextArea,
													JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
													JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}
	
	/**
	 * Builds all the combo boxes for this UI.
	 */
	public void buildComboBoxes() {
		typeComboBox = new JComboBox<Ticket.TicketType>(typeOptions);
		typeComboBox.setFont(new Font("Courier", Font.PLAIN, 15));
		
		priorityComboBox = new JComboBox<Ticket.TicketPriority>(priorityOptions);
		priorityComboBox.setFont(new Font("Courier", Font.PLAIN, 15));
	}
	
	private void buildWindowListeners() {
	    addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent event) {
	            if(!submitted) {
	                int result = JOptionPane.showConfirmDialog(null,
                                                               "Are you sure you want to cancel " +
                                                                   "this ticket?.",
                                                               "Unsubmitted Ticket",
                                                               JOptionPane.YES_NO_OPTION);
                    if(result == JOptionPane.YES_OPTION) {
                        dispose();
                    }
	            } else {
	                dispose();
	            }
	        }
	    });
	}
	
	/**
	 * Builds all the buttons for this UI.
	 */
	public void buildButtons() {
	    submitButton = new JButton("Submit");
	    submitButton.addActionListener((ActionEvent ae) -> {
	        Object[] options = { "OK", "Cancel" };
	        int result = JOptionPane.showOptionDialog(null, "Submit the ticket?", "Confirmation",
	                                                  JOptionPane.DEFAULT_OPTION,
	                                                  JOptionPane.QUESTION_MESSAGE,
                                                      null, options, options[0]);
	        if(result == JOptionPane.YES_OPTION) {
	            // Don't submit the ticket if things fail
	            if(submitTicket() != 0) {
	                return;
	            }
	            
	            JOptionPane.showMessageDialog(null, "Ticket Submitted");
	            
	            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	        }
        });
	    submitButton.setFont(new Font("Courier", Font.PLAIN, 15));
	    
	    cancelButton = new JButton("Cancel");
	    cancelButton.addActionListener((ActionEvent ae) -> {
            Object[] options = { "OK", "Cancel" };
            int result = JOptionPane.showOptionDialog(null, "Are you sure you want to cancel?",
                                                      "Confirmation", JOptionPane.DEFAULT_OPTION,
                                                      JOptionPane.QUESTION_MESSAGE,
                                                      null, options, options[0]);
            if(result == JOptionPane.YES_OPTION) {
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            }
	    });
	    cancelButton.setFont(new Font("Courier", Font.PLAIN, 15));
	}
	
	/**
	 * Initializes the UI.
	 * <p>
	 * Builds the components, sets the window dimensions, places all components, then finishes
     *  setting the window properties.
	 */
	public void initUI() {
		buildComponents();
		
		setSize(800, 400);
		
		getContentPane().setLayout(null);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		addComponent(titleTextAreaLabel, 0, 0);
		addComponent(titleTextArea, titleTextAreaLabel.getPreferredSize().width, 0);
		addComponent(detailsTextAreaLabel, 0, 50);
		addComponent(detailsTextAreaScrollPane,
					 detailsTextAreaLabel.getPreferredSize().width,
					 detailsTextAreaLabel.getPreferredSize().height + 50);
		
		addComponent(typeComboBox, getSize().width - typeComboBox.getPreferredSize().width - 10, 0);
		addComponent(typeComboBoxLabel,
	                 getSize().width - typeComboBoxLabel.getPreferredSize().width -
	                  typeComboBox.getPreferredSize().width - 10,
	                 0);
		
        addComponent(priorityComboBox,
                     getSize().width - priorityComboBox.getPreferredSize().width - 10,
                     typeComboBox.getPreferredSize().height + 10);
        addComponent(priorityComboBoxLabel,
                     getSize().width - priorityComboBox.getPreferredSize().width -
                      priorityComboBoxLabel.getPreferredSize().width - 10,
                     typeComboBox.getPreferredSize().height + 10);
		
		addComponent(submitButton, detailsTextAreaLabel.getPreferredSize().width, 300);
		addComponent(cancelButton,
		             (detailsTextArea.getPreferredSize().width -
                      cancelButton.getPreferredSize().width) +
		              detailsTextAreaLabel.getPreferredSize().width,
		             300);
		
		setTitle("New Ticket");
		setLocationRelativeTo(null);
		setResizable(false);
	}
	
	/**
	 * Adds a component to the JFrame this gui extends from.
	 * @param component The component to add.
	 * @param xPos The x position to add the component to.
	 * @param yPos The y position to add the component to.
	 */
	public void addComponent(JComponent component, int xPos, int yPos) {
		getContentPane().add(component);
		
		Insets insets = getContentPane().getInsets();
		Dimension size = component.getPreferredSize();
		
		component.setBounds(xPos + insets.left, yPos + insets.top, size.width, size.height);
	}
}
