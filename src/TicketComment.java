import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Tyler Robbins
 * @version 1.0
 * @since 0.7
 */
public class TicketComment {
    /**
     * The author of this comment. 
     */
    private String author;
    
    /**
     * The date this ticket was authored.
     */
    private Date date;
    
    /**
     * The comment's contents
     */
    private String comment;
    
    /**
     * Constructs a new TicketComment.
     * @param newAuthor The author of this comment.
     * @param newDate The date this comment was authored.
     * @param newComment This comment's contents.
     */
    public TicketComment(String newAuthor, Date newDate, String newComment) {
        author = newAuthor;
        date = newDate;
        comment = newComment;
    }
    
    /**
     * Constructs an empty TicketComment
     */
    public TicketComment() {
        author = "";
        date = null;
        comment = "";
    }
    
    /**
     * Gets this comment's author.
     * @return This comment's author.
     */
    public String getAuthor() {
        return author;
    }
    
    /**
     * Gets this comment's creation date.
     * @return This comment's creation date.
     */
    public Date getDate() {
        return date;
    }
    
    /**
     * Gets this comment's contents.
     * @return This comment's contents.
     */
    public String getComment() {
        return comment;
    }
    
    /**
     * Converts this comment to a row that can be used in a table in {@link ViewTicketGui}.
     * @return This comment represented as a 3 element String array.
     */
    public String[] toRow() {
        return new String[] {
                author,
                new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date),
                comment.split("\n")[0]
        };
    }
}
