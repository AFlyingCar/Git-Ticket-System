import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import javax.swing.JOptionPane;

/**
 * Utility functions for dealing with the File system
 * 
 * @author Tyler Robbins
 * @version 1.0
 * @since 0.5
 */
public class FileUtil {
    /**
     * Dummy constructor to prevent outside construction.
     */
    private FileUtil() { }
    
    /**
     * Gets the Git directory which this is a project for.
     * <p>
     * If Git directory is not set yet, then it will attempt to set it to the current working directory.
     * If that is not a valid directory either, then it will prompt the user for a username and password
     *  continuously until either a valid Git directory is recieved, or the user cancels the dialogue.
     * If a valid Git directory has been found, then the working dir is set to that directory.
     * 
     * Supports the nogui option
     * @return the git directory if one has been set, null otherwise.
     */
    // Suprress warnings about the Scanner below, because fucking Scanner closes System.in when you close it, which is fucking stupid and causes a really bizarre exception.
    @SuppressWarnings("resource")
    public static String getGitDir() {
        String gitDir = Configuration.getWorkingDir();
        
        if(gitDir != null && GitUtil.isGitDir(gitDir)) return gitDir;
        
        try {
            gitDir = Paths.get("").toFile().getCanonicalPath();
        } catch(IOException e) {
            e.printStackTrace();
            gitDir = null;
        }

        if(!GitUtil.isGitDir(gitDir)) {
            if(Configuration.isGuiEnabled()) {
                try {
                    gitDir = Paths.get("").toFile().getCanonicalPath();
                } catch(IOException e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "I/O Error",
                                                  "Failed to get canonical path for '.'",
                                                  JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                
                do {
                    gitDir = (String)JOptionPane.showInputDialog(null, "The directory " + gitDir +
                                                                        " is not a valid GIT directory. Please provide one.",
                                                                        "Invalid Git Directory", JOptionPane.PLAIN_MESSAGE);
                    if(gitDir == null) {
                        JOptionPane.showMessageDialog(null, "Fatal Error",
                                "Cannot do anything in an invalid Git Directory. Quitting.",
                                JOptionPane.ERROR_MESSAGE);
                        return null;
                    }
                } while(!GitUtil.isGitDir(gitDir));
            } else {
                try {
                    gitDir = Paths.get("").toFile().getCanonicalPath();
                } catch(IOException e) {
                    System.err.println("Failed to get canonical path for '.'");
                    e.printStackTrace();
                    return null;
                }
                
                do {
                    System.out.println("The directory " + gitDir + " is not a valid GIT directory. Please provide one.");
                    Scanner scanner = new Scanner(System.in);
                    gitDir = scanner.nextLine();
                
                    if(gitDir == null || gitDir.length() == 0) {
                        return null;
                    }
                
                    if(GitUtil.isGitDir(gitDir)) {
                        break;
                    }
                } while(true);
            }
        }
        
        Configuration.setWorkingDir(gitDir);
        
        return gitDir;
    }
    
    /**
     * Gets the directory where the application is running from.
     * @return the directory where the application is running from.
     */
    public static File getCurrentRunningDir() {
        return Paths.get("").toFile();
    }
    
    /**
     * Creates the directory where this application will write all project repository copies to. 
     * @return The directory that was created.
     * @throws IOException from Files.createDirectory(Path)
     */
    public static Path createAppDir() throws IOException {
        Path path = null;
        
        try {
                path = getAppDir();
                Files.createDirectory(path);
        } catch(FileAlreadyExistsException e) {
            // Ignore this one
        }
        
        return path;
    }
    
    /**
     * Gets the directory where this application will write all project repository copies to.
     * <p>
     * On Windows, the directory created will be %LOCALAPPDATA%/Tickets
     * On *nix, the directory will be ~/.Tickets
     * @return The directory.
     */
    public static Path getAppDir() {
        if(System.getProperty("os.name").startsWith("Windows")) {
            return Paths.get(System.getenv("LOCALAPPDATA"), "Tickets");
        } else {
            return Paths.get(System.getProperty("user.home"), ".Tickets");
        }
    }
    
    /**
     * Gets the project directory path as a single name with no directory seperators.
     * <p>
     * Converts all '/'s and '\' to '_', and removes all colons.
     * <p> 
     * Example: C:\Users\User\Desktop\project\ will become C_Users_User_Desktop_project
     * @return The project directory path as a single name.
     */
    public static String getProjectDirAsSingleDir() {
        String singleDir = Configuration.getWorkingDirAsSingle();
        
        if(singleDir == null) {
            String workingDir = getGitDir();
            if(workingDir == null) {
                System.exit(1);
            }
            
            singleDir = workingDir.replaceAll("(\\\\|/)", "_").replaceAll(":", "");
            
            Configuration.setWorkingDirAsSingle(singleDir);
        }
        
        return singleDir;
    }
    
    /**
     * Gets the copied project repository directory where tickets will be written and read from.
     * @return The copied project repository directory where tickets will be written and read from.
     * @throws IOException From createAppDir() and Files.createDirectory
     */
    public static File getProjectTicketDir() throws IOException {
        File f = new File(createAppDir().toFile(), getProjectDirAsSingleDir());
        
        try {
            if(!f.exists()) Files.createDirectory(Paths.get(f.toString()));
        } catch(FileAlreadyExistsException e) {
            // Ignore this one
        }
        
        return f;
    }
    
    /**
     * Cleans up unnecessary files in the given directory
     * <p>
     * In the given directory, all files except the .git/ folder and all .ticket files are removed.
     * 
     * @param directory the directory to clean uneccessary files from. 
     */
    public static void cleanUnecessaryFiles(File directory) {
        File[] fileList = directory.listFiles();
        
        for(File f : fileList) {
            if(f.getName().equals(".git") || f.getName().endsWith(".ticket")) continue;
            
            if(f.isDirectory())
                cleanUnecessaryFiles(f);
            
            f.delete();
        }
    }
    
    /**
     * Cleans up unnecessary files in the copied project repository directory
     * <p>
     * In the copied project repository directory, all files except the .git/ folder and all .ticket
     *  files are removed. 
     * @deprecated
     */
    @Deprecated
    public static void cleanUnecessaryFiles() {
        try {
            File[] fileList = getProjectTicketDir().listFiles();
            
            for(File f : fileList) {
                if(f.getName().equals(".git") || f.getName().endsWith(".ticket")) continue;
                f.delete();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Writes a Ticket object to a file.
     * @param ticket The ticket to write.
     * @return A File object representing the file the ticket was written to if the ticket was written
     *          to successfully, null otherwise.
     * @throws IOException From File.createNewFile()
     */
    public static File writeTicket(Ticket ticket) throws IOException {
        File file = new File(getProjectTicketDir(), ticket.getMD5ID() + ".ticket");
        
        if(!file.exists()) file.createNewFile();
        
        /*
         * [TITLE]
         * DetailsStart
         * [DETAILS]
         * ...
         * DetailsEnd
         * [AUTHOR]
         * [SHORT MD5]
         * [STATUS]
         * [PRIORITY]
         * [TYPE]
         * CommentsStart
         * CommentStart
         * [AUTHOR]
         * [DATE]
         * [COMMENT]
         * ...
         * CommentEnd
         * ...
         * CommentsEnd
         */
        if(file.exists()) {
            PrintStream ps = new PrintStream(file);
            
            ps.println(ticket.getShortMD5ID());
            
            ps.println(ticket.getTitle());
            
            ps.println("DetailsStart");
            ps.println(ticket.getDetails());
            ps.println("DetailsEnd");
            
            ps.println(ticket.getAuthor());
            ps.println(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(ticket.getDate()));
            
            ps.println(ticket.getStatus().ordinal());
            ps.println(ticket.getPriority().ordinal());
            ps.println(ticket.getType().ordinal());
            
            ps.println("CommentsStart");
            for(TicketComment tc : ticket.getComments()) {
                ps.println("CommentStart");
                ps.println(tc.getAuthor());
                ps.println(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(tc.getDate()));
                
                ps.println(tc.getComment());
                ps.println("CommentEnd");
            }
            ps.println("CommentsEnd");
            
            ps.close();
            return file;
        }
        
        return null;
    }
    
    /**
     * Gets a list of all ticket filenames that exist in the copied project repository directory.
     * @return A List of all ticket filenames.
     */
    public static List<String> getAllTicketFilenames() {
        List<String> filenames = new ArrayList<String>();
        File dir;
        
        try {
            dir = getProjectTicketDir();
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
        
        File[] fList = dir.listFiles();
        
        for(File f : fList) {
            if(f.getName().endsWith(".ticket")) {
                try {
                    filenames.add(f.getCanonicalPath());
                } catch(IOException e) {
                    System.err.println("Failed to get canonical path of ticket " + f.getName().split(".")[0] + ". Skipping.");
                    e.printStackTrace();
                }
            }
        }
        
        return filenames;
    }
    
    /**
     * Gets a List of Ticket objects representing each ticket filename from {@link #getAllTicketFilenames()}
     * @return A List of all Ticket objects representing each ticket filename in the copied project
     *          repository directory.  
     */
    public static List<Ticket> getAllTickets() {
        List<String> filenames = getAllTicketFilenames();
        List<Ticket> tickets = new ArrayList<Ticket>();
        
        for(String filename : filenames) {
            Ticket t = readTicketFilename(filename);
            
            if(t != null) tickets.add(t);
        }
        
        return tickets;
    }
    
    /**
     * Reads a Ticket from a given filename.
     * @param filename The filename to read the ticket from.
     * @return A Ticket object if the filename could be successfully read, null otherwise.
     */
    public static Ticket readTicketFilename(String filename) {
        File ticketFile = new File(filename);
        return readTicketFile(ticketFile);
    }
    
    /**
     * Reads a Ticket from a given File object.
     * @param ticketFile The File object to read the ticket from.
     * @return A Ticket object if the file could be successfully read, null otherwise.
     */
    public static Ticket readTicketFile(File ticketFile) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(ticketFile));
            String line;
            Ticket ticket = new Ticket();
            
            br.readLine(); // Skip over short MD5 ID
            
            if((line = br.readLine()) == null) {
                System.err.println("Malformed Ticket Title!");
                br.close();
                return null;
            }
            ticket.setTitle(line.trim());
            
            if((line = br.readLine()) == null || !line.trim().equals("DetailsStart")) {
                System.err.println("Malformed Ticket Details!");
                br.close();
                return null;
            }
            
            while((line = br.readLine()) != null) {
                if(line.equals("DetailsEnd")) break;
                
                ticket.setDetails(ticket.getDetails() + line.trim() + "\n");
            }
            
            ticket.setDetails(ticket.getDetails().trim());
            
            if((line = br.readLine()) == null) {
                System.err.println("Malformed Ticket Author!");
                br.close();
                return null;
            }
            
            ticket.setAuthor(line.trim());
            
            if((line = br.readLine()) == null) {
                System.err.println("Malformed Ticket Date!");
                br.close();
                return null;
            }
            try {
                ticket.setDate(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(line.trim()));
            } catch(ParseException e) {
                System.err.println("Failed to parse date. Probably malformed!");
                e.printStackTrace();
                br.close();
                return null;
            }
            
            if((line = br.readLine()) == null) {
                System.err.println("Malformed Ticket Status!");
                br.close();
                return null;
            }
            ticket.setStatus(Ticket.TicketStatus.values()[Integer.valueOf(line.trim())]);
            
            if((line = br.readLine()) == null) {
                System.err.println("Malformed Ticket Priority!");
                br.close();
                return null;
            }
            ticket.setPriority(Ticket.TicketPriority.values()[Integer.valueOf(line.trim())]);
            
            if((line = br.readLine()) == null) {
                System.err.println("Malformed Ticket Type!");
                br.close();
                return null;
            }
            ticket.setType(Ticket.TicketType.values()[Integer.valueOf(line.trim())]);
            
            if(!br.readLine().equals("CommentsStart")) {
                System.err.println("Malformed Ticket Comments Section!");
                br.close();
                return null;
            }
            
            while((line = br.readLine()) != null) {
                if(line.equals("CommentsEnd")) break;
                
                if(line.equals("CommentStart")) {
                    String author = br.readLine();
                    String rawDate = br.readLine();
                    String comment = "";
                    
                    while((line = br.readLine()) != null) {
                        if(line.equals("CommentEnd")) break;
                        
                        comment += line;
                    }
                    
                    if(line == null) {
                        System.err.println("Malformed ticket Comments Section!");
                        br.close();
                        return null;
                    }
                    
                    Date date = null;
                    try {
                        DateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        date = formatter.parse(rawDate);
                    } catch(ParseException e) {
                        System.err.println("Failed to parse date. Probably malformed!");
                        e.printStackTrace();
                        br.close();
                        return null;
                    }
                    
                    ticket.addComment(new TicketComment(author, date, comment));
                }
            }
            
            br.close();
            
            // Sanity check
            if(!ticket.getMD5ID().equals(ticketFile.getName().split("\\.")[0])) {
                System.err.println("Ticket MD5 ID does not match MD5 ID for contents.");
                System.err.println("Ticket was either corrupted or incorrectly modified.");
                return null;
            }
            
            return ticket;
        } catch(IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Reads a specific ticket based on the beginning of its MD5 ID
     * @param startMD5ID the first N characters of a ticket's MD5 ID
     * @return A Ticket object of the ticket specified. If one does not exist with an ID starting with {@code startMD5ID}, then null is returned.
     */
    public static Ticket readTicket(String startMD5ID) {
        File dir;
        try{
            dir = getProjectTicketDir();
        } catch(IOException e) {
            e.printStackTrace();
            return null;
        }
        
        File[] filesList = dir.listFiles();
        File ticketFile = null;
        int numFiles = 0;
        
        for(File f : filesList) {
            if(f.getName().startsWith(startMD5ID)) {
                ticketFile = f;
                numFiles++;
            }
        }
        
        if(numFiles == 0) {
            System.err.println("No such ticket exists starting with " + startMD5ID);
            return null;
        } else if(numFiles > 1) {
            System.err.println("Ticket MD5ID is abiguous. " + numFiles + " found starting with " + startMD5ID + ".");
            return null;
        } else {
            return readTicketFile(new File(dir.getAbsolutePath(), ticketFile.getName()));
        }
    }
}
