import java.util.Date;
import java.util.List;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

/**
 * @author Tyler Robbins
 * @version 1.0
 * @since 0.4
 */
public class Ticket {
    /**
     * @author Tyler Robbins
     * @version 1.0
     * @since 0.4
     */
    public enum TicketStatus {
        /**
         * If the Ticket is Open
         */
        OPEN,

        /**
         * If the Ticket is Closed
         */
        CLOSED,
        
        /**
         * If the Ticket is Invalid
         */
        INVALID,
        
        /**
         * If the Ticket is In Progress
         */
        IN_PROGRESS;
        
        @Override
        public String toString() {
            switch(ordinal()) {
                case 0:
                    return "Open";
                case 1:
                    return "Closed";
                case 2:
                    return "Invalid";
                case 3:
                    return "In Progress";
                default:
                    return "";
            }
        }
    }
    
    /**
     * @author Tyler Robbins
     * @version 1.0
     * @since 0.4
     */
    public enum TicketPriority {
        /**
         * If the ticket has High priority
         */
        HIGH,
        
        /**
         * If the ticket has Normal priority.
         */
        NORMAL,
        
        /**
         * If the ticket has Low priority.
         */
        LOW;
        
        @Override
        public String toString() {
            switch(ordinal()) {
                case 0:
                    return "High";
                case 1:
                    return "Normal";
                case 2:
                    return "Low";
                default:
                    return "";
            }
        }
    }
    
    /**
     * @author Tyler Robbins
     * @version 1.0
     * @since 0.4
     */
    public enum TicketType {
        /**
         * If the ticket is a Feature request/about a Feature
         */
        FEATURE,
        
        /**
         * If the ticket is a Hotfix request/about a Hotfix
         */
        HOTFIX,
        
        /**
         * If the ticket is about a Release
         */
        RELEASE,
        
        /**
         * If the ticket is a Support request.
         */
        SUPPORT;
        
        @Override
        public String toString() {
            switch(ordinal()) {
                case 0:
                    return "Feature";
                case 1:
                    return "Hotfix";
                case 2:
                    return "Release";
                case 3:
                    return "Support";
                default:
                    return "";
            }
        }
    }
    
    /**
     * The title of this ticket.
     */
	private String title;
	
	/**
	 * The details of this ticket.
	 */
	private String details;
	
	/**
	 * The author of this ticket.
	 */
	private String author;
	
	/**
	 * The date this ticket was authored.
	 */
	private Date date;
	
	/**
	 * The status of this ticket.
	 */
	private TicketStatus status;
	
	/**
	 * The priority of this ticket.
	 */
	private TicketPriority priority;
	
	/**
	 * The type of this ticket.
	 */
	private TicketType type;
	
	/**
	 * The ID of this ticket
	 * @deprecated Use the MD5 Hash ID instead.
	 */
	@Deprecated
	private long id;
	
	/**
	 * A List of all comments for this ticket.
	 */
	private List<TicketComment> comments;
	
	/**
	 * The next ID of this ticket
	 * @deprecated Use the MD5 Hash ID instead.
	 */
	@Deprecated
	private static int maxID;
	
	/**
	 * Clones a ticket.
	 * @param t The ticket to make a clone of
	 */
	public Ticket(Ticket t) {
	    init(t.getTitle(), t.getDetails(), t.getAuthor(), t.getDate(), t.getType(),
	         t.getPriority(), t.getStatus(), 0);
	    comments = new ArrayList<TicketComment>(t.getComments());
	}
	
	/**
	 * Creates a new Ticket.
	 */
	public Ticket() {
	    init("", "", "", new Date(), TicketType.FEATURE, TicketPriority.HIGH, TicketStatus.INVALID, maxID++);
	}
	
	/**
	 * Creates a new Ticket.
	 * @param newTitle The title of the ticket
	 * @param newDetails The details of the ticket
	 * @param newAuthor The author of the ticket.
	 * @param newDate The date of the ticket.
	 * @param newType The type of the ticket.
	 * @param newPriority The priority of the ticket.
	 * @param newStatus The status of the ticket.
	 */
	public Ticket(String newTitle, String newDetails, String newAuthor, Date newDate,
                  TicketType newType, TicketPriority newPriority, TicketStatus newStatus)
	{
	    init(newTitle, newDetails, newAuthor, newDate, newType, newPriority, newStatus, maxID++);
	}
	
	/**
	 * Initializes a ticket.
	 * @param newTitle The title of the ticket
     * @param newDetails The details of the ticket
     * @param newAuthor The author of the ticket.
     * @param newDate The date of the ticket.
     * @param newType The type of the ticket.
     * @param newPriority The priority of the ticket.
     * @param newStatus The status of the ticket.
	 * @param newID The ID of the ticket.
	 */
	private void init(String newTitle, String newDetails, String newAuthor, Date newDate, TicketType newType,
	                  TicketPriority newPriority, TicketStatus newStatus, long newID)
	{
	    comments = new ArrayList<TicketComment>();
	    
	    title = newTitle;
	    details = newDetails;
	    author = newAuthor;
	    date = newDate;
	    type = newType;
	    priority = newPriority;
	    status = newStatus;
	    // id = newID;
	    
        // md5ID = generateMD5ID(this);
	}
	
	/**
	 * Adds a comment to this Ticket.
	 * @param comment The comment to add.
	 */
	public void addComment(TicketComment comment) {
	    comments.add(comment);
	}
	
	/**
	 * Gets all comments for this ticket.
	 * @return A List of all comments for this ticket.
	 */
	public List<TicketComment> getComments() {
	    return comments;
	}
	
	/**
	 * Sets the title of this Ticket and regenerates the MD5 hash.
	 * @param newTitle What to set this Ticket's title to.
	 */
	public void setTitle(String newTitle) {
		title = newTitle;
	}
	
	/**
	 * Sets the details of this ticket and regenerates the MD5 hash.
	 * @param newDetails What to set this Ticket's details to.
	 */
	public void setDetails(String newDetails) {
		details = newDetails;
	}
	
	/**
	 * Sets the type of this ticket.
	 * @param newType What to set this ticket's type to.
	 */
	public void setType(TicketType newType) {
	    type = newType;
	}
	
	/**
	 * Sets the priority of this Ticket.
	 * @param newPriority What to set this ticket's priority to.
	 */
	public void setPriority(TicketPriority newPriority) {
	    priority = newPriority;
	}
	
	/**
	 * Sets the status of this Ticket.
	 * @param newStatus What to set this ticket's status to.
	 */
	public void setStatus(TicketStatus newStatus) {
	    status = newStatus;
	}
	
	/**
	 * Sets the date this ticket was authored and regenerates the MD5 hash.
	 * @param newDate What to set this ticket's date to.
	 */
	public void setDate(Date newDate) {
	    date = newDate;
	}
	
	/**
	 * Sets the author of this ticket and regenerates the MD5 hash.
	 * @param newAuthor What to set this ticket's author to.
	 */
	public void setAuthor(String newAuthor) {
	    author = newAuthor;
	}
	
	/**
	 * Sets the MD5 Hash ID of this ticket.
	 * @deprecated Let the Ticket generate it based on the details, author, and date.
	 * @param newMD5ID What to set this ticket's MD5 hash to.
	 */
	@Deprecated
	public void setMD5ID(String newMD5ID) {
	    // md5ID = newMD5ID;
	}
	
	/**
	 * Gets the author of this ticket.
	 * @return The author of this ticket.
	 */
	public String getAuthor() {
	    return author;
	}
	
	/**
	 * Gets the title of this ticket.
	 * @return The title of this ticket.
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Gets the details of this ticket.
	 * @return The details of this ticket.
	 */
	public String getDetails() {
		return details;
	}
	
	/**
	 * Gets the date of this ticket.
	 * @return The date of this ticket.
	 */
	public Date getDate() {
	    return date;
	}
	
	/**
	 * Gets the type of this ticket.
	 * @return The type of this ticket.
	 */
	public TicketType getType() {
	    return type;
	}
	
	/**
	 * Gets the priority of this ticket.
	 * @return The priority of this ticket.
	 */
	public TicketPriority getPriority() {
	    return priority;
	}
	
	/**
	 * Gets the status of this ticket.
	 * @return The status of this ticket.
	 */
	public TicketStatus getStatus() {
	    return status;
	}
	
	/**
	 * Gets the ID of this ticket
	 * @deprecated Use {@link #getMD5ID()} instead.
	 * @return The ID of this ticket.
	 */
	@Deprecated
	public long getID() {
	    return id;
	}
	
	/**
	 * Gets the MD5 Hash ID of this ticket.
	 * @return The MD5 Hash ID of this ticket.
	 */
	public String getMD5ID() {
	    return generateMD5ID(this);
	}
	
	/**
	 * Gets the short version of the MD5 Hash ID of this ticket (The first half of it)
	 * @return The short version of the MD5 Hash ID of this ticket.
	 */
	public String getShortMD5ID() {
	    String md5ID = getMD5ID();
	    return md5ID.substring(0, md5ID.length() / 2);
	}
	
	public boolean equals(Ticket other) {
	    return other.getMD5ID().equals(getMD5ID()) && other.getComments().size() == comments.size() &&
	           other.getPriority() == priority && other.getStatus() == status && other.getType() == type;
	}
	
	/**
	 * Generates the MD5 Hash ID for a ticket.
	 * <p>
	 * Generated with the following string: "${TITLE}${DETAILS}${AUTHOR}${DATE}"
	 * @param ticket The ticket to generate a MD5 Hash ID for.
	 * @return The MD5 Hash ID for a ticket if no errors occurred, null otherwise.
	 */
	public static String generateMD5ID(Ticket ticket) {
	    try {
	        byte[] bytes = (ticket.getTitle() + ticket.getDetails() + ticket.getAuthor() + ticket.getDate().toString()).getBytes();
	    
	        MessageDigest md = MessageDigest.getInstance("MD5");
	        byte[] digested = md.digest(bytes);
	        
	        return toHexString(digested);
	    } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
	}
	
	/**
	 * Converts an array of bytes to a hex string.
	 * <p>
	 * Grabbed from StackOverflow, but unfortunately, I don't remember where it was :(
	 * @param bytes The array of bytes to convert.
	 * @return A hex string from the array of bytes.
	 */
    private static String toHexString(byte[] bytes) {
        // TODO: Move this to a helper class
        if (bytes == null) {
            throw new IllegalArgumentException("Byte array must not be null.");
        }
        StringBuffer hex = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            hex.append(Character.forDigit((bytes[i] & 0XF0) >> 4, 16));
            hex.append(Character.forDigit((bytes[i] & 0X0F), 16));
        }
        return hex.toString();
    }
}
