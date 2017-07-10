import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

/**
 * @author Tyler Robbins
 * @version 1.0
 * @since 0.4
 */
public class ViewTicketGui extends JDialog {
    /**
     * Done only because eclipse wouldn't shut the fuck up otherwise.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The ticket being viewed.
     */
    private Ticket ticket;
    
    /**
     * The ticket before any modifications
     */
    private Ticket oldTicket;
    
    /**
     * The label for the ticket's title
     */
    private JLabel titleLabel;
    
    /**
     * The label for the ticket's ID
     */
    private JLabel idLabel;
    
    /**
     * The label for the ticket's author.
     */
    private JLabel authorLabel;
    
    /**
     * The label for the ticket's type
     */
    private JLabel typeLabel;
    
    /**
     * The label for the ticket's date.
     */
    private JLabel dateLabel;
    
    /**
     * The label for the ticket's status combo box.
     */
    private JLabel statusCBLabel;
    
    /**
     * The label for the ticket's priority combo box.
     */
    private JLabel priorityCBLabel;
    
    /**
     * The combo box for the ticket's status.
     */
    private JComboBox<Ticket.TicketStatus> statusComboBox;
    
    /**
     * The combo box for the ticket's priority.
     */
    private JComboBox<Ticket.TicketPriority> priorityComboBox;
    
    /**
     * The table of all comments for the ticket.
     */
    private JTable commentsTable;
    
    /**
     * The scroll pane for the comments table.
     */
    private JScrollPane commentsScrollPane;
    
    /**
     * The scroll pane for the new comment text area.
     */
    private JScrollPane newCommentScrollPane;
    
    /**
     * The scroll pane for the ticket's details text area.
     */
    private JScrollPane detailsScrollPane;
    
    /**
     * The label of the new comment text area.
     */
    private JLabel newCommentFieldLabel;
    
    /**
     * The text area for inputting a new comment.
     */
    private JTextArea newCommentField;
    
    /**
     * The text area where the details of the ticket are displayed.
     */
    private JTextArea detailsTextArea;
    
    /**
     * The button to add a comment to a ticket.
     */
    private JButton addNewCommentButton;
    
    /**
     * The button to update changes to a ticket.
     */
    private JButton updateTicketButton;
    
    /**
     * An array of all the columns in the comment table.
     */
    public final String[] columns = {
            "Author",
            "Date",
            "Comment"
    };
    
    /**
     * Constructs a new ViewTicketGui, initializes the UI, and adds all comments for the ticket to the comments table. 
     * @param newTicket The ticket this Gui is supposed to show.
     */
    public ViewTicketGui(Ticket newTicket) {
        ticket = new Ticket(newTicket);
        oldTicket = newTicket;
        
        initUI();
        
        setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
        
        updateCommentsTable();
    }
    
    /**
     * Warns that the window is about to closed with unsaved changes if unsaved changes exist. Will close the window.
     */
    private void windowCloseWarning() {
        // If the ticket has changed
        if(!ticket.equals(oldTicket)) {
            int result = JOptionPane.showConfirmDialog(null,
                                                       "You have unsaved changes to this ticket. " +
                                                         "Are you sure you want to exit?",
                                                       "Unsaved changes", JOptionPane.YES_NO_OPTION);
            if(result == JOptionPane.YES_OPTION) {
                dispose();
            }
        } else {
            dispose();
        }
    }
    
    /**
     * Shows a specific comment. Will open a new window.
     * @param commentIdx The index in the comment table of the comment to show.
     */
    public void showSpecificComment(int commentIdx) {
        TicketComment tc = ticket.getComments().get(commentIdx);
        
        EventQueue.invokeLater(() -> {
            ViewCommentGui vtg = new ViewCommentGui(tc);
            vtg.setVisible(true);
        });
    }
    
    /**
     * Builds all of the window listeners for this UI.
     */
    public void buildWindowListeners() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                windowCloseWarning();
            }
        });
    }
    
    /**
     * Builds all of the labels for this UI.
     */
    public void buildLabels() {
        titleLabel = new JLabel(ticket.getTitle());
        titleLabel.setPreferredSize(new Dimension(300, titleLabel.getPreferredSize().height + 2));
        titleLabel.setFont(new Font("Courier", Font.PLAIN, 15));
        titleLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        titleLabel.setToolTipText(ticket.getTitle());
        
        idLabel = new JLabel("ID: " + ticket.getShortMD5ID());
        idLabel.setFont(new Font("Courier", Font.PLAIN, 15));
        
        typeLabel = new JLabel(ticket.getType().toString());
        typeLabel.setFont(new Font("Courier", Font.PLAIN, 15));
        
        authorLabel = new JLabel(ticket.getAuthor());
        authorLabel.setFont(new Font("Courier", Font.PLAIN, 15));
        
        dateLabel = new JLabel(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(ticket.getDate()));
        dateLabel.setFont(new Font("Courier", Font.PLAIN, 15));
        
        statusCBLabel = new JLabel("Status:");
        statusCBLabel.setFont(new Font("Courier", Font.PLAIN, 15));
        
        priorityCBLabel = new JLabel("Priority:");
        priorityCBLabel.setFont(new Font("Courier", Font.PLAIN, 15));
        
        newCommentFieldLabel = new JLabel("New comment");
        newCommentFieldLabel.setFont(new Font("Courier", Font.PLAIN, 15));
    }
    
    /**
     * Builds all of the combo boxes for this UI.
     */
    public void buildComboBoxes() {
        statusComboBox = new JComboBox<Ticket.TicketStatus>(Ticket.TicketStatus.values());
        statusComboBox.setSelectedItem(ticket.getStatus());
        statusComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                ticket.setStatus((Ticket.TicketStatus)statusComboBox.getSelectedItem());
            }
        });
        statusComboBox.setFont(new Font("Courier", Font.PLAIN, 15));
        
        priorityComboBox = new JComboBox<Ticket.TicketPriority>(Ticket.TicketPriority.values());
        priorityComboBox.setSelectedItem(ticket.getPriority());
        priorityComboBox.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent event) {
                ticket.setPriority((Ticket.TicketPriority)priorityComboBox.getSelectedItem());
            }
        });
        priorityComboBox.setFont(new Font("Courier", Font.PLAIN, 15));
    }
    
    /**
     * Builds all of the scroll panes for this UI.
     */
    public void buildScrollPanes() {
        commentsScrollPane = new JScrollPane(commentsTable);
        commentsScrollPane.setPreferredSize(new Dimension(495, 200));
        
        newCommentScrollPane = new JScrollPane(newCommentField,
                                               JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                               JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        
        detailsScrollPane = new JScrollPane(detailsTextArea);
    }
    
    /**
     * Builds all of the text areas for this UI.
     */
    public void buildTextAreas() {
        newCommentField = new JTextArea("", 3, 47);
        newCommentField.setEditable(true);
        newCommentField.setLineWrap(true);
        newCommentField.setFont(new Font("Courier", Font.PLAIN, 15));
        newCommentField.setWrapStyleWord(true);
        
        detailsTextArea = new JTextArea(ticket.getDetails(), 6, 33);
        detailsTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        detailsTextArea.setEditable(false);
        detailsTextArea.setLineWrap(true);
        detailsTextArea.setFont(new Font("Courier", Font.PLAIN, 15));
        detailsTextArea.setWrapStyleWord(true);
    }
    
    /**
     * Builds all of the buttons for this UI.
     */
    public void buildButtons() {
        addNewCommentButton = new JButton("Add");
        addNewCommentButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if(newCommentField.getText().trim().length() > 0) {
                    Object[] options = { "OK", "Cancel" };
                    int result = JOptionPane.showOptionDialog(null, "Add the comment?", "Confirmation",
                                                              JOptionPane.DEFAULT_OPTION,
                                                              JOptionPane.QUESTION_MESSAGE,
                                                              null, options, options[0]);
                    if(result == JOptionPane.YES_OPTION) {
                        TicketComment tc = createNewComment(newCommentField.getText());
                        ticket.addComment(tc);
                        newCommentField.setText("");
                        
                        updateCommentsTable();
                    }
                }
            }
        });
        addNewCommentButton.setFont(new Font("Courier", Font.PLAIN, 15));
        
        updateTicketButton = new JButton("Update");
        updateTicketButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                Object[] options = { "OK", "Cancel" };
                int result = JOptionPane.showOptionDialog(null, "Update the ticket?", "Confirmation",
                                                          JOptionPane.DEFAULT_OPTION,
                                                          JOptionPane.QUESTION_MESSAGE,
                                                          null, options, options[0]);
                if(result == JOptionPane.YES_OPTION) {
                    try {
                        FileUtil.writeTicket(ticket); // Rewrite the ticket to disk.
                        GitUtil.commit(ticket.getMD5ID() + ".ticket");
                        
                        oldTicket = ticket;
                        
                        ListTicketsGui.getInstance().updateTicketList();
                        ListTicketsGui.getInstance().updateTicketTable();
                        
                        oldTicket = ticket;
                    } catch(IOException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null,
                                                      "An error occurred when writing the ticket to disk.",
                                                      "I/O error.", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        updateTicketButton.setFont(new Font("Courier", Font.PLAIN, 15));
    }
    
    /**
     * Creates a new TicketComment.
     * @param commentContents The contents of the comment's message
     * @return a new TicketComment object.
     */
    public TicketComment createNewComment(String commentContents) {
        return new TicketComment(GitUtil.getAuthor(), new Date(), commentContents);
    }
    
    /**
     * Updates the table of comments for this ticket.
     */
    public void updateCommentsTable() {
        ((DefaultTableModel) commentsTable.getModel()).setRowCount(0);
        
        for(TicketComment tc : ticket.getComments())
            ((DefaultTableModel) commentsTable.getModel()).addRow(tc.toRow());
    }
    
    /**
     * Builds all of the tables for this UI.
     */
    public void buildTables() {
        commentsTable = new JTable() {
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
        commentsTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                JTable table = (JTable)event.getSource();
                if(event.getClickCount() >= 2) {
                    Rectangle rect = table.getBounds();
                    
                    if(rect != null && rect.contains(event.getPoint())) {
                        int row = table.rowAtPoint(event.getPoint());
                        showSpecificComment(row);
                    }
                }
            }
        });
        
        commentsTable.setModel(new DefaultTableModel(columns, 0));
        commentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        commentsTable.getColumn(columns[1]).setMaxWidth(120);
    }
    
    /**
     * Builds all of the components for this UI.
     */
    public void buildComponents() {
        buildLabels();
        buildComboBoxes();
        buildTextAreas();
        buildButtons();
        buildTables();
        buildScrollPanes();
        buildWindowListeners();
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
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        
        setSize(520, 550);
       
        int nextYPos = 5;  // Right
        int nextYPos2 = 5; // Left
        
        addComponent(titleLabel, 5, nextYPos2);
        
        nextYPos2 += titleLabel.getPreferredSize().height + 5;
        addComponent(detailsScrollPane, 5, nextYPos2);
       
        addComponent(idLabel, getSize().width - idLabel.getPreferredSize().width - 10, nextYPos);
        
        nextYPos += idLabel.getPreferredSize().height + 5;
        addComponent(authorLabel, getSize().width - authorLabel.getPreferredSize().width - 10, nextYPos);
        
        nextYPos += authorLabel.getPreferredSize().height + 5;
        addComponent(typeLabel, getSize().width - typeLabel.getPreferredSize().width - 10,
                     nextYPos);
        
        nextYPos += typeLabel.getPreferredSize().height + 5;
        addComponent(dateLabel, getSize().width - dateLabel.getPreferredSize().width - 10,
                     nextYPos);
        
        nextYPos += dateLabel.getPreferredSize().height + 5;
        addComponent(statusCBLabel,
                     getSize().width - statusCBLabel.getPreferredSize().width - statusComboBox.getPreferredSize().width - 15,
                     nextYPos);
        addComponent(statusComboBox, getSize().width - statusComboBox.getPreferredSize().width - 10,
                     nextYPos);
        
        nextYPos += statusComboBox.getPreferredSize().height + 5;
        addComponent(priorityCBLabel,
                     getSize().width - priorityCBLabel.getPreferredSize().width -
                      priorityComboBox.getPreferredSize().width - 15,
                     nextYPos);
        addComponent(priorityComboBox,
                     getSize().width - priorityComboBox.getPreferredSize().width - 10,
                     nextYPos);
        
        nextYPos += priorityComboBox.getPreferredSize().height + 5;
        addComponent(commentsScrollPane, 10, nextYPos);
        
        nextYPos += commentsScrollPane.getPreferredSize().height + 5;
        addComponent(newCommentFieldLabel, 12, nextYPos);
        nextYPos += newCommentFieldLabel.getPreferredSize().height + 5;
        addComponent(newCommentScrollPane, 12, nextYPos);
        
        addComponent(addNewCommentButton, 18 + newCommentScrollPane.getPreferredSize().width,
                     nextYPos);
        
        nextYPos += newCommentScrollPane.getPreferredSize().height + 10;
        addComponent(updateTicketButton, getSize().width / 2 - updateTicketButton.getPreferredSize().width / 2,
                     nextYPos);
        
        setTitle("View Ticket");
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
