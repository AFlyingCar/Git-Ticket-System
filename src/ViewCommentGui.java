import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author Tyler Robbins
 * @version 1.0
 * @since 0.9
 */
public class ViewCommentGui extends JDialog {
    /**
     * Done only because eclipse wouldn't shut the fuck up otherwise.
     */
    private static final long serialVersionUID = 1L;

    /**
     * The TicketComment to be shown by this Gui
     */
    private TicketComment comment;
    
    /**
     * A scroll pane for this comment's contents.
     */
    private JScrollPane commentScrollPane;
    
    /**
     * A label for the date this comment was authored.
     */
    private JLabel dateLabel;
    
    /**
     * A label for this comment's author.
     */
    private JLabel authorLabel;
    
    /**
     * A text area for the contents of this comment.
     */
    private JTextArea commentTextArea;
    
    /**
     * Constructs a new ViewCommentGui and initializes the UI.
     * @param tc The TicketComment to display
     */
    public ViewCommentGui(TicketComment tc) {
        comment = tc;
        
        initUI();
        
        setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
    }
    
    /**
     * Builds the labels for this UI.
     */
    public void buildLabels() {
        dateLabel = new JLabel("Date: " + new SimpleDateFormat("yyyy/MM/dd").format(comment.getDate()));
        dateLabel.setFont(new Font("Courier", Font.PLAIN, 15));
        // dateLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        dateLabel.setToolTipText(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(comment.getDate()));
        
        authorLabel = new JLabel("Author: " + comment.getAuthor());
        authorLabel.setFont(new Font("Courier", Font.PLAIN, 15));
    }
    
    /**
     * Builds the text areas for this UI.
     */
    public void buildTextAreas() {
        commentTextArea = new JTextArea(comment.getComment(), 5, 33);
        commentTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        commentTextArea.setEditable(false);
        commentTextArea.setLineWrap(true);
        commentTextArea.setWrapStyleWord(true);
        commentTextArea.setFont(new Font("Courier", Font.PLAIN, 15));
    }
    
    /**
     * Builds the scroll panes for this UI.
     */
    public void buildScrollPanes() {
        commentScrollPane = new JScrollPane(commentTextArea,
                                            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        commentScrollPane.setPreferredSize(new Dimension(300, 200));
    }
    
    /**
     * Builds the components for this UI.
     */
    public void buildComponents() {
        buildLabels();
        buildTextAreas();
        buildScrollPanes();
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
        
        setSize(320, 300);
        
        addComponent(dateLabel, 5, 5);
        addComponent(authorLabel, 5, dateLabel.getPreferredSize().height + 10);
        addComponent(commentScrollPane, 5, authorLabel.getPreferredSize().height +
                                           dateLabel.getPreferredSize().height + 15);
        
        setTitle("View Comment by " + comment.getAuthor());
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
