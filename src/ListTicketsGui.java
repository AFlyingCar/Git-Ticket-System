import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

/**
 * @author Tyler Robbins
 * @version 1.0
 * @since 0.3
 */
public class ListTicketsGui extends JDialog {
    /**
     * Done only because eclipse wouldn't shut the fuck up otherwise.
     */
    private static final long serialVersionUID = 1L;

    /**
     * A scroll pane of all tickets listed here.
     */
    private JScrollPane ticketsListScrollPane;
    
    /**
     * A table of all tickets listed here.
     */
    private JTable ticketsTable;
    
    /**
     * A checkbox of if tickets with an open status should be displayed.
     */
    private JCheckBox showOpenCheckBox;
    /**
     * A checkbox of if tickets with a closed status should be displayed.
     */
    private JCheckBox showClosedCheckBox;
    
    /**
     * A checkbox of if tickets with an invalid status should be displayed.
     */
    private JCheckBox showInvalidCheckBox;
    
    /**
     * A checkbox of if tickets with an in progress status should be displayed.
     */
    private JCheckBox showInProgressCheckBox;
    
    /**
     * A label for the filter options.
     */
    private JLabel showOptionsLabel;
    
    /**
     * A list of all the Ticket objects to show
     */
    private List<Ticket> tickets;
    
    /**
     * A list of all tickets that are being viewed.
     */
    private List<Ticket> openTickets;
    
    /**
     * Should open tickets be shown
     */
    boolean showOpen;
    
    /**
     * Should closed tickets be shown
     */
    boolean showClosed;
    
    /**
     * Should invalid tickets be shown
     */
    boolean showInvalid;
    
    /**
     * Should in progress tickets be shown
     */
    boolean showInProgress;
    
    /**
     * The instance of this Gui
     */
    private static ListTicketsGui instance = null;
    
    /**
     * An array of all the columns in the ticket table.
     */
    public final String[] columns = {
            "ID",
            "Status",
            "Priority",
            "Title"
    };
    
    /**
     * Constructs a new ListTicketsGui by calling {@link GitUtil.init()}, initializing the UI, and
     *  adding all tickets to the ticket table.
     */
    private ListTicketsGui() {
        GitUtil.init();
        
        openTickets = new ArrayList<Ticket>();
        
        updateTicketList();
        
        initUI();
        
        setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
        
        updateTicketTable();
    }
    
    /**
     * Gets the instance for this singleton.
     * @return The instance for this singleton.
     */
    public static ListTicketsGui getInstance() {
        if(instance == null) {
            instance = new ListTicketsGui();
        }
        
        return instance;
    }

    /**
     * Converts a Ticket to a row that can be used in the ticket table.
     * @param ticket The ticket to convert.
     * @return A String[] of all columns in the row to represent a single ticket.
     */
    public String[] ticketToRow(Ticket ticket) {
        String[] row = new String[4];
        
        row[0] = ticket.getShortMD5ID();
        row[1] = ticket.getStatus().toString();
        row[2] = ticket.getPriority().toString();
        row[3] = ticket.getTitle();
        
        return row;
    }
    
    /**
     * Shows a specific ticket. Will open a new window.
     * @param ticketIndex The index in the ticket table of the ticket to show.
     */
    public void showSpecificTicket(int ticketIndex) {
        // Only show the ticket if it isn't already open
        if(!openTickets.contains(tickets.get(ticketIndex))) {
            openTickets.add(tickets.get(ticketIndex));
            EventQueue.invokeLater(() -> {
                ViewTicketGui vtg = new ViewTicketGui(tickets.get(ticketIndex));
                vtg.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent event) {
                        openTickets.remove(tickets.get(ticketIndex));
                    }
                });
                vtg.setVisible(true);
            });
        }
    }
    
    /**
     * Updates the list of tickets based on the tickets in the copied repository directory.
     */
    public void updateTicketList() {
        tickets = FileUtil.getAllTickets();
    }
    
    /**
     * Updates the displayed tickets based on the tickets list and the currently set filter settings.
     */
    public void updateTicketTable() {
        ((DefaultTableModel) ticketsTable.getModel()).setRowCount(0);
        
        showOpen = showOpenCheckBox.isSelected();
        showClosed = showClosedCheckBox.isSelected();
        showInvalid = showInvalidCheckBox.isSelected();
        showInProgress = showInProgressCheckBox.isSelected();
        
        for(Ticket t : tickets) {
            switch(t.getStatus()) {
                case OPEN:
                    if(showOpen) addRow(t);
                    break;
                case CLOSED:
                    if(showClosed) addRow(t);
                    break;
                case INVALID:
                    if(showInvalid) addRow(t);
                    break;
                case IN_PROGRESS:
                    if(showInProgress) addRow(t);
                    break;
                default:
                    System.err.println("Uknown ticket status: " + t.getStatus());
            }
        }
    }
    
    /**
     * Adds a row to the tickets table.
     * @param t The ticket to add.
     */
    public void addRow(Ticket t) {
        ((DefaultTableModel)ticketsTable.getModel()).addRow(ticketToRow(t));
    }
    
    /**
     * Builds all of the components for this UI.
     */
    public void buildComponents() {
        buildLabels();
        buildCheckBoxes();
        buildTables();
        buildScrollPanes();
    }
    
    /**
     * Builds all of the tables for this UI.
     */
    protected void buildTables() {
        ticketsTable = new JTable() {
            private static final long serialVersionUID = 1L;
            
            public boolean isCellEditable(int r, int c) {
                return false;
            }

            public String getToolTipText(MouseEvent event) {
                int row = rowAtPoint(event.getPoint());
                int col = columnAtPoint(event.getPoint());

                try {
                    //comment row, exclude heading
                    if(row >=  0){
                      return getValueAt(row, col).toString();
                    }
                } catch (NullPointerException e1) {
                    //catch null pointer exception if mouse is over an empty line
                }

                return null;
            }
        };
        
        ticketsTable.setModel(new DefaultTableModel(columns, 0));
        ticketsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ticketsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                JTable table = (JTable)event.getSource();
                if(event.getClickCount() >= 2) {
                    Rectangle rect = table.getBounds();
                    
                    if(rect != null && rect.contains(event.getPoint())) {
                        int row = table.rowAtPoint(event.getPoint());
                        showSpecificTicket(row);
                    }
                }
            }
        });
    }
    
    /**
     * Builds all of the labels for this UI.
     */
    public void buildLabels() {
        showOptionsLabel = new JLabel("Show: ");
        showOptionsLabel.setFont(new Font("Courier", Font.PLAIN, 15));
    }
    
    /**
     * Builds all of the check boxes for this UI.
     */
    public void buildCheckBoxes() {
        ActionListener cbListener = new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                updateTicketTable();
            }
        };
        
        showOpenCheckBox = new JCheckBox("Open", true);
        showOpenCheckBox.addActionListener(cbListener);
        showOpenCheckBox.setFont(new Font("Courier", Font.PLAIN, 15));
        
        showClosedCheckBox = new JCheckBox("Closed", true);
        showClosedCheckBox.addActionListener(cbListener);
        showClosedCheckBox.setFont(new Font("Courier", Font.PLAIN, 15));
        
        showInvalidCheckBox = new JCheckBox("Invalid", true);
        showInvalidCheckBox.addActionListener(cbListener);
        showInvalidCheckBox.setFont(new Font("Courier", Font.PLAIN, 15));
        
        showInProgressCheckBox = new JCheckBox("In Progress", true);
        showInProgressCheckBox.addActionListener(cbListener);
        showInProgressCheckBox.setFont(new Font("Courier", Font.PLAIN, 15));
    }
    
    /**
     * Builds all of the scroll panes for this UI.
     */
    public void buildScrollPanes() {
        ticketsListScrollPane = new JScrollPane(ticketsTable);
        ticketsListScrollPane.setPreferredSize(new Dimension(400, 300));
    }
    
    /**
     * Initializes the UI.
     * <p>
     * Builds the components, sets the window dimensions, places all components, then finishes
     *  setting the window properties.
     */
    public void initUI() {
        buildComponents();
        
        getContentPane().setLayout(null);
        
        addComponent(ticketsListScrollPane, 10, 10);
        addComponent(showOptionsLabel, 30 + ticketsListScrollPane.getPreferredSize().width, 10);
        addComponent(showOpenCheckBox, 40 + ticketsListScrollPane.getPreferredSize().width,
                     20 + showOptionsLabel.getPreferredSize().height);
        addComponent(showClosedCheckBox, 40 + ticketsListScrollPane.getPreferredSize().width,
                     25 + showOptionsLabel.getPreferredSize().height +
                     showOpenCheckBox.getPreferredSize().height);
        addComponent(showInvalidCheckBox, 40 + ticketsListScrollPane.getPreferredSize().width,
                     30 + showOptionsLabel.getPreferredSize().height +
                     showOpenCheckBox.getPreferredSize().height + 
                     showClosedCheckBox.getPreferredSize().height);
        addComponent(showInProgressCheckBox, 40 + ticketsListScrollPane.getPreferredSize().width,
                     35 + showOptionsLabel.getPreferredSize().height +
                     showOpenCheckBox.getPreferredSize().height + 
                     showClosedCheckBox.getPreferredSize().height + 
                     showInvalidCheckBox.getPreferredSize().height);
        
        setTitle("List Tickets");
        setSize(600, 345);
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
