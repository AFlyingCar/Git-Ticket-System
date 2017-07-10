import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * Document Filter to limit the number of characters allowed in a JField/JTextArea/etc.
 * 
 * @author Tyler Robbins
 * @version 1.0
 * @since 0.2
 */
public class DocumentSizeFilter extends DocumentFilter {
    /**
     * The maximum number of characters allowed in the document.
     */
    private int maximumChars;

    /**
     * Constructs a new DocumentSizeFilter
     * @param maxChars The maximum number of characters allowed in this document.
     */
    public DocumentSizeFilter(int maxChars) {
        maximumChars = maxChars;
    }

    /**
     * Gets the maximum number of characters allowed in this document.
     * @return The maximum number of characters allowed.
     */
    public int getMaxChars() {
        return maximumChars;
    }

    /**
     * Sets the maximum number of characters allowed in this document.
     * @param maxChars The maximum number of characters allowed in this document.
     */
    public void setMaxChars(int maxChars) {
        maximumChars = maxChars;
    }

    /**
     * {@inheritDoc}
     */
    public void insertString(FilterBypass fb, int offs, String str,
                             AttributeSet a) throws BadLocationException
    {
        if((fb.getDocument().getLength() + str.length()) <= maximumChars) {
            super.insertString(fb, offs, str, a);
        } else {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    /**
     * {@inheritDoc}
     */
    public void replace(FilterBypass fb, int offs, int length, String str,
                        AttributeSet a) throws BadLocationException
    {
        if ((fb.getDocument().getLength() + str.length() - length) <= maximumChars)
            super.replace(fb, offs, length, str, a);
        else
            Toolkit.getDefaultToolkit().beep();
    }
}
