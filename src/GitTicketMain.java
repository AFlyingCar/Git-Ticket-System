import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Main class of the program.
 * <p>
 * Supports both a gui interface, and a command line interface.
 * <p>
 * <code>GitTicket $[OPTIONS] $[COMMAND] $COMMAND_ARGS...</code>
 * <p>
 * Command-line options:
 * <p><code> --no-gui</code>
 * <p><code> --gitDir=${GITDIRECTORY}</code>
 * 
 * <p>Commands:
 * <p><code>  new ${TITLE} ${DETAILS} ${TYPE} ${PRIORITY}</code>
 * <p>    - Creates a new ticket with the corresponding title, details, type, and priority
 * <p><code>  list</code>
 * <p>    - Lists all tickets for this project
 * <p><code>  show ${MD5ID} [comment]</code>
 * <p>    - Shows either the contents of a ticket, or the comments for a ticket.
 * <p><code>  edit ${MD5ID} [priority=${PRIORITY}] [status=${STATUS}] [type=${TYPE}]</code>
 * <p>    - Edits a specified ticket.
 * <p><code>  comment ${MD5ID} $COMMENT CONTENTS...</code>
 * <p>    - Creates a comment on a ticket
 * <p><code>  userpass</code>
 * <p>    - Prompts the user for a username and password again
 * <p><code>  sync</code>
 * <p>    - Syncs all local tickets with all tickets stored remotely.
 * <p><code>  help $[COMMAND]</code>
 * <p>    - Prints a help message
 * 
 * @author Tyler Robbins
 * @version 1.0
 * @since 0.1
 */
public class GitTicketMain {
    /**
     * @author StrataIT
     * @version 1.0
     * @since 0.9
     */
    private enum CommandType {
        NONE,
        NEW,
        LIST,
        SHOW,
        EDIT,
        SYNC,
        USERPASS,
        COMMENT,
        HELP;
        
        /** A cached list of all names in this enum. */
        private static List<String> names = null;
        
        /**
         * Gets a list of all names in this enum.
         * <p>
         * Will generate the list once, then cache the results and return that.
         * @return A list of all names in this enum.
         */
        public static List<String> names() {
            if(names == null) {
                CommandType[] values = values();
                names = new ArrayList<String>(values.length);
                for(int i = 0; i < values.length; i++) {
                    names.add(values[i].toString().toUpperCase());
                }
            }
            
            return names;
        }
    };
    
    /**
     * Prints a help message based on command line arguments.
     * @param args The list of arguments for this command.
     */
    private static void help(List<String> args) {
        if(args.size() == 0) {
            help();
            return;
        }
        
        if(!CommandType.names().contains(args.get(0).toUpperCase())) {
            System.err.println("Unknown command " + args.get(0));
            help();
            System.exit(1);
        }
        
        help(CommandType.valueOf(args.get(0).toUpperCase()));
    }
    
    /**
     * Prints a help message for a specific command.
     * <p>
     * Will print the generic message if CommandType.NONE is given.
     * @param command The specific command to print for.
     */
    private static void help(CommandType command) {
        switch(command) {
            case HELP:
                System.out.println("help $[COMMAND]");
                System.out.println("\tPrints out the generic help message, or the help message for a specific command.");
                break;
            case NONE:
                help();
                break;
            case USERPASS:
                System.out.println("userpass");
                System.out.println("\tAllows changing of the username and password used to connect to the remote server.");
                break;
            case NEW:
                System.out.println("new ${TITLE} ${DETAILS} ${TYPE} ${PRIORITY}");
                System.out.println("\tCreates a new ticket.");
                break;
            case LIST:
                System.out.println("list");
                System.out.println("\tLists all tickets for this project.");
                break;
            case SHOW:
                System.out.println("show ${MD5ID} [comment]");
                System.out.println("\tShows either the contents of a ticket, or the comments for a ticket.");
                break;
            case EDIT:
                System.out.println("edit ${MD5ID} [priority=${PRIORITY}] [status=${STATUS}] [type=${TYPE}]");
                System.out.println("\tEdits a specified ticket.");
                break;
            case COMMENT:
                System.out.println("comment ${MD5ID} $COMMENT CONTENTS...");
                System.out.println("\tCreates a comment on a ticket");
                break;
            case SYNC:
                System.out.println("sync");
                System.out.println("Syncs all local tickets with all tickets stored remotely.");
                break;
        }
    }
    
    /**
     * Prints a generic help message.
     */
    private static void help() {
        System.out.println("Git Ticket System v" + Configuration.getVersion());
        System.out.println("");
        System.out.println("GitTicket $[OPTIONS] $[COMMAND] $COMMAND_ARGS...");
        System.out.println("Command-line options:");
        System.out.println("\t--no-gui");
        System.out.println("\t--gitDir=${GITDIRECTORY}");
        System.out.println("");
        System.out.println("Commands:");
        System.out.println(" - new ${TITLE} ${DETAILS} ${TYPE} ${PRIORITY}");
        System.out.println(" - list");
        System.out.println(" - show ${MD5ID} [comment]");
        System.out.println(" - edit ${MD5ID} $[priority=${PRIORITY}] $[status=${STATUS}] $[type=${TYPE}]");
        System.out.println(" - comment ${MD5ID} $COMMENT CONTENTS...");
        System.out.println(" - userpass");
        System.out.println(" - sync");
        System.out.println(" - help $[COMMAND]");
    }
    
    /**
     * Creates and commits a new ticket.
     * @param args The list of arguments for this command.
     */
    private static void newTicket(List<String> args) {
        if(args.size() < 4) {
            System.err.println("Invalid number of arguments.");
            help(CommandType.NEW);
            System.exit(1);
        }
        
        String title = args.get(0);
        String details = args.get(1);
        
        String value = "";
        Ticket.TicketType type;
        Ticket.TicketPriority priority;
        try {
            value = args.get(2);
            type = Ticket.TicketType.valueOf(value.toUpperCase());
            value = args.get(3);
            priority = Ticket.TicketPriority.valueOf(value.toUpperCase());
        } catch(IllegalArgumentException e) {
            System.err.println("Invalid value " + value);
            System.exit(1);
            return; // This will never be hit, but it is here to make the compiler happy.
        }
        
        Ticket t = new Ticket(title, details, GitUtil.getAuthor(),
                              new Date(), type, priority, Ticket.TicketStatus.OPEN);
        
        try {
            File file = FileUtil.writeTicket(t);
            if(file != null) {
                GitUtil.commit(file);
            } else {
                System.err.print("Failed to write ticket to disk.");
                System.exit(1);
            }
            
            System.out.println("New ticket created with ID " + t.getShortMD5ID());
        } catch(IOException e) {
            System.err.print("Failed to write ticket to disk.");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Lists all tickets.
     * @param args The list of arguments for this command.
     */
    private static void listTickets(List<String> args) {
        List<String> names = FileUtil.getAllTicketFilenames();
        
        boolean showShort = false;
        if(args.contains("--short")) showShort = true;
        
        for(String name : names) {
            String id = name.split("\\.")[0];
            id = id.split("\\\\")[id.split("\\\\").length - 1];
            
            if(showShort)
                System.out.println(id.substring(0, id.length() / 2));
            else
                System.out.println(id);
        }
    }
    
    /**
     * Edits a specific ticket.
     * @param args The list of arguments for this command.
     */
    private static void editTicket(List<String> args) {
        if(args.size() < 1) {
            System.err.println("Invalid number of arguments. Must specify start of ticket ID.");
            help(CommandType.EDIT);
            System.exit(1);
        }
        
        Ticket t = FileUtil.readTicket(args.get(0));
        
        if(t != null) {
            boolean hasChanged = false;
            
            for(int i = 1; i < args.size(); i++) {
                String arg = args.get(i);
                String value = "";
                
                try {
                    if(arg.startsWith("priority=")) {
                        value = arg.split("=")[1];
                        Ticket.TicketPriority priority = Ticket.TicketPriority.valueOf(value.toUpperCase());
                        t.setPriority(priority);
                        hasChanged = true;
                    } else if(arg.startsWith("status=")) {
                        value = arg.split("=")[1];
                        Ticket.TicketStatus status = Ticket.TicketStatus.valueOf(value.toUpperCase());
                        t.setStatus(status);
                        hasChanged = true;
                    } else if(arg.startsWith("type=")) {
                        value = arg.split("=")[1];
                        Ticket.TicketType type = Ticket.TicketType.valueOf(value.toUpperCase());
                        t.setType(type);
                        hasChanged = true;
                    } else {
                        System.err.println("Unknown requested change " + arg);
                        help(CommandType.EDIT);
                        System.exit(1);
                    }
                } catch(IllegalArgumentException e) {
                    System.err.println("Invalid value: " + value);
                    System.exit(1);
                }
            }
            
            // Don't bother writing to disk if the ticket hasn't changed
            if(hasChanged) {
                try {
                    FileUtil.writeTicket(t);
                } catch(IOException e) {
                    System.err.println("Failed to save changes to disk.");
                    e.printStackTrace();
                    System.exit(1);
                }
            }
        } else {
            System.exit(1);
        }
    }
    
    /**
     * Shows information about a specific ticket.
     * @param args The list of arguments for this command.
     */
    private static void showTicket(List<String> args) {
        if(args.size() < 1) {
            System.err.println("Invalid number of arguments. Must specify start of ticket ID.");
            help(CommandType.SHOW);
            System.exit(1);
        }
        
        Ticket t = FileUtil.readTicket(args.get(0));
        
        if(t == null) System.exit(1);
        
        if(args.size() > 1 && args.get(1).equals("comment")) {
            List<TicketComment> comments = t.getComments();
            
            System.out.println("Comments:");
            for(TicketComment comment : comments) {
                System.out.println("=========");
                System.out.println("Date: " + comment.getDate());
                System.out.println("Author: " + comment.getAuthor());
                String[] contents = comment.getComment().split("\n");
                for(String line : contents) {
                    System.out.println("\t" + line);
                }
            }
        } else {
            String header = "Ticket " + t.getShortMD5ID() + " by " + t.getAuthor() + " on " + t.getDate();
            System.out.println(header);
            System.out.println(new String(new char[header.length()]).replace('\0', '='));
            
            System.out.println(t.getTitle());
            
            System.out.println("Details:");
            String[] details = t.getDetails().split("\n");
            for(String line : details) {
                System.out.println("\t" + line);
            }
            
            System.out.println("Status: " + t.getStatus().toString());
            System.out.println("Priority: " + t.getPriority().toString());
            System.out.println("Type: " + t.getType().toString());
        }
    }
    
    /**
     * Syncs remote and local tickets.
     * @param args The list of arguments for this command.
     */
    private static void syncTickets(List<String> args) {
        if(!GitUtil.sync())
            System.err.println("Failed to sync.");
    }
    
    /**
     * Adds a comment to a specific ticket.
     * @param args The list of arguments for this command.
     */
    private static void commentTicket(List<String> args) {
        if(args.size() < 1) {
            System.err.println("Invalid number of arguments. Must specify start of ticket ID.");
            help(CommandType.SHOW);
            System.exit(1);
        }
        
        Ticket t = FileUtil.readTicket(args.get(0));
        
        if(t == null) System.exit(1);
        
        String newContents = "";
        for(int i = 1; i < args.size(); i++) {
            newContents += args.get(i) + " ";
        }
        
        t.addComment(new TicketComment(GitUtil.getAuthor(), new Date(), newContents.trim()));
        
        try {
            FileUtil.writeTicket(t);
        } catch(IOException e) {
            System.err.println("Failed to update ticket.");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Changes the username and password used for this repository's ticket origin thing.
     * @param args The list of arguments for this command.
     */
    private static void userpass(List<String> args) {
        GitUtil.fixRemote();
    }
    
    /**
     * The starting point of project
     * @param args The arguments passed to this program on the command line.
     */
	public static void main(String[] args) {
	    CommandType command = CommandType.NONE;
	    
	    int i = 0;
	    
	    for(; i < args.length; i++) {
	        String arg = args[i];
	        // Stop searching once we've found an argument that is not a -option or a --option
	        if(CommandType.names().contains(arg.toUpperCase())) {
	            command = CommandType.valueOf(arg.toUpperCase());
	            break;
	        }
	        
	        if(arg.equals("--no-gui")) Configuration.setIsGuiEnabled(false);
	        else if(arg.startsWith("--gitDir=")) Configuration.setWorkingDir(arg.split("=")[1]);
	    }
	    
	    GitUtil.init();
	    
	    if(Configuration.isGuiEnabled()) {
	        EventQueue.invokeLater(() -> {
			    StartGui main = StartGui.getInstance();
			    main.setVisible(true);
		    });
	    } else {
	        List<String> argList = new ArrayList<String>();
	        for(++i; i < args.length; i++) argList.add(args[i]);
	        
	        switch(command) {
	            case NEW:
	                newTicket(argList);
	                break;
	            case LIST:
	                listTickets(argList);
	                break;
	            case SHOW:
	                showTicket(argList);
	                break;
	            case EDIT:
	                editTicket(argList);
	                break;
	            case SYNC:
	                syncTickets(argList);
	                break;
	            case USERPASS:
	                userpass(argList);
	                break;
	            case COMMENT:
	                commentTicket(argList);
	                break;
	            case HELP:
	                help(argList);
	                break;
                default:
                    System.err.println("Invalid command.");
                    System.exit(1);
	        }
	    }
	}
}
