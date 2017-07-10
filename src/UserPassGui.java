import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * @author Tyler Robbins
 * @version 1.0
 * @since 0.9
 */
public class UserPassGui extends JDialog {
    /**
     * Done only because eclipse wouldn't shut the fuck up otherwise.
     */
    private static final long serialVersionUID = 1L;

    /**
     * @author Tyler Robbins
     * @version 1.0
     * @since 0.9
     */
    public enum ExitState {
        /**
         * If the OK button was pressed
         */
        OK,
        /**
         * If the CANCEL button was pressed.
         */
        CANCEL
    }

    /**
     * The username that was inputted.
     */
    private String username;
    
    /**
     * The password that was inputted.
     */
    private char[] password;
    
    /**
     * The OK Button.
     */
    private JButton okButton;
    
    /**
     * The Cancel Button
     */
    private JButton cancelButton;
    
    /**
     * The label for the username field
     */
    private JLabel userLabel;
    
    /**
     * The label for the password field
     */
    private JLabel passLabel;
    
    /**
     * The text field for inputting the username
     */
    private JTextField usernameField;
    
    /**
     * The password field for inputting the password.
     */
    private JPasswordField passwordField;
    
    /**
     * The exit state of this gui (which button was pressed).
     */
    private ExitState exitState;
    
    /**
     * Constructs a new UserPassGui, initializes the UI, and sets the Modality type to the default.
     * <p>
     * exitState defaults to OK.
     */
    public UserPassGui() {
        exitState = ExitState.OK;
        initUI();
        
        setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
    }
    
    /**
     * Builds all buttons for this UI.
     */
    private void buildButtons() {
        okButton = new JButton("OK");
        okButton.addActionListener((ActionEvent ae) -> {
            username = usernameField.getText();
            password = passwordField.getPassword();
            exitState = ExitState.OK;
            
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
        
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((ActionEvent ae) -> {
            exitState = ExitState.CANCEL;
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        });
    }
    
    /**
     * Builds all fields for this UI.
     */
    private void buildFields() {
        passwordField = new JPasswordField(25);
        
        usernameField = new JTextField("", 25);
        usernameField.setEditable(true);
        usernameField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    }
    
    /**
     * Builds all labels for this UI.
     */
    public void buildLabels() {
        userLabel = new JLabel("Username: ");
        userLabel.setFont(new Font("Courier", Font.PLAIN, 15));
        
        passLabel = new JLabel("Password: ");
        passLabel.setFont(new Font("Courier", Font.PLAIN, 15));
    }
    
    /**
     * Builds all components for this UI.
     */
    private void buildComponents() {
        buildLabels();
        buildButtons();
        buildFields();
    }
    
    /**
     * Initializes the UI.
     * <p>
     * Builds the components, sets the window dimensions, places all components, then finishes
     *  setting the window properties.
     */
    private void initUI() {
        buildComponents();
        
        setSize(400, 130);
        
        getContentPane().setLayout(null);

        int nextYPos = 5;
        
        addComponent(userLabel, 5, 5);
        addComponent(usernameField, userLabel.getPreferredSize().width + 5, 5);
        
        nextYPos += userLabel.getPreferredSize().height + 5;
        addComponent(passLabel, 5, nextYPos);
        addComponent(passwordField, userLabel.getPreferredSize().width + 5, nextYPos);
        
        int buttonOffset = 120;
        nextYPos += passLabel.getPreferredSize().height + 10;
        addComponent(okButton, buttonOffset, nextYPos);
        addComponent(cancelButton, getSize().width - cancelButton.getPreferredSize().width - buttonOffset, nextYPos);
        
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
    
    /**
     * Gets the ExitState of this Gui.
     * @return This Gui's exit state.
     */
    public ExitState getExitState() {
        return exitState;
    }
    
    /**
     * Gets the username that was inputted.
     * @return The username that was inputted.
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Gets the password that was inputted.
     * @return The password that was inputted.
     */
    public char[] getPassword() {
        return password;
    }
}
