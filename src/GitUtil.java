import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

import javax.swing.JOptionPane;

/**
 * @author Tyler Robbins
 * @version 1.0
 * @since 0.8
 */
public class GitUtil {
    /**
     * Initializes the copied GIT repository for this project
     * <p>
     * Will create a new folder, with the project repository copied into it.
     *  This folder will be clean of all files except those in the _Tickets branch of the project.
     *  If no such branch exists, then it will be created as an orphan.
     */
    public static void init() {
        try {
            File projectDir = FileUtil.getProjectTicketDir();
            String gitDir = FileUtil.getGitDir();
        
            if(gitDir == null) {
                System.err.println("Cannot do anything in an invalid Git Directory. Quitting.");
                System.exit(1);
            }
            
            if(!isGitDir(projectDir)) {
                System.out.println(projectDir.toString() + " is not a valid git directory. Initializing");
                try {
                    // If the directory doesn't exist
                    Process p = Runtime.getRuntime().exec(new String[] { "git", "clone", "--no-checkout", gitDir, projectDir.getCanonicalPath() });
                    
                    // System.out.println("Executing git clone --no-checkout " + gitDir + " " + projectDir.getCanonicalPath());
                    
                    p.waitFor();
                    
                    p = Runtime.getRuntime().exec(new String[] { "git", "rev-parse", "--verify", "_Tickets"}, null, projectDir);
                    
                    // If the branch doesn't exist, then create it
                    if(p.waitFor() != 0) {
                        p = Runtime.getRuntime().exec(new String[] { "git", "checkout", "--orphan", "_Tickets"}, null, projectDir);
                        p.waitFor();
                        p = Runtime.getRuntime().exec(new String[] { "git", "rm", "--cached", "-r", projectDir.getCanonicalPath() }, null, projectDir);
                        p.waitFor();
                        
                        FileUtil.cleanUnecessaryFiles(FileUtil.getProjectTicketDir());
                    } else {
                        p = Runtime.getRuntime().exec(new String[] { "git", "checkout","_Tickets" }, null, projectDir);
                        p.waitFor();
                    }
                    
                    fixRemote();
                    
                    sync();
                } catch(IOException e) {
                    e.printStackTrace();
                    if(Configuration.isGuiEnabled()) {
                        JOptionPane.showMessageDialog(null, "Failed to initialize project dir. Quitting", "Fatal I/O error", JOptionPane.ERROR_MESSAGE);
                    } else {
                        System.err.println("Failed to initialize project dir.");
                    }
                    
                    System.exit(1);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                    if(Configuration.isGuiEnabled()) {
                        JOptionPane.showMessageDialog(null, "Failed to initialize project dir. Quitting", "Process interrupted.", JOptionPane.ERROR_MESSAGE);
                    } else {
                        System.err.println("Failed to initialize project dir.");
                    }
                    
                    System.exit(1);
                }
            }
        } catch(IOException e) {
            System.err.println("Failed to get git project dir. Quitting.");
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    /**
     * Grabs the remote url set for the copied repository
     * @return The remote url for this repository
     */
    public static String getRemote() {
        String remote = null;
        
        try {
            Process p = Runtime.getRuntime().exec(new String[] { "git", "remote", "get-url", "origin"},
                                                  null, FileUtil.getProjectTicketDir());
            BufferedReader stdInput= new BufferedReader(new InputStreamReader(p.getInputStream()));
        
            if(p.waitFor() == 0) {
                remote = "";
                for (String line = ""; line != null; line = stdInput.readLine()) {
                    remote += line;
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        
        return remote;
    }
    
    /**
     * Changes  the remote origin for the copied project to what it should be, as opposed to the
     *  local filepath used to clone it.
     * <p>
     * The remote url for the clone is set to the following format: https://USERNAME:PASSWORD@domain/project.git
     * <p>
     * Note: The username and password are stored in the remote url because of issues with how Git
     *  asks for the username and password when calling push and pull. Instead of reading from stdin,
     *  Git reads from /dev/tty, which is difficult to write to. Using Git over SSH would request the
     *  username and password over stdin, however not every remote host supports uploading public ssh
     *  keys, so it is not an option.
     */
    public static void fixRemote() {
        try {
            String username = null;
            char[] password = null;
            
            String encOrigin;
            
            if(Configuration.isGuiEnabled()) {
                UserPassGui upg = new UserPassGui();
                upg.setVisible(true);
                
                if(upg.getExitState() == UserPassGui.ExitState.OK) {
                    username = upg.getUsername();
                    password = upg.getPassword();
                } else {
                    System.err.println("Need password to properly set origin.");
                    return;
                }
            } else {
                System.out.print("Username: ");
                username = new Scanner(System.in).nextLine();
                
                if(username.length() > 0)
                    password = System.console().readPassword("Password: ");
            }
            
            Process p = Runtime.getRuntime().exec(new String[] { "git", "remote", "get-url", "origin"},
                                                  null, new File(FileUtil.getGitDir()));
            BufferedReader stdInput= new BufferedReader(new InputStreamReader(p.getInputStream()));
            String origin = "";
            
            int wf = p.waitFor();
            if(wf == 0 || wf == 128) { // TODO: REMOVE THIS 128 CHECK IT IS ONLY HERE FOR DEBUGGING
                for (String line = ""; line != null; line = stdInput.readLine()) {
                    origin += line;
                }
                
                // Yeah, it's plain text, but fucking hell I've just spent like an hour and a half
                //  trying to figure out how to fucking pass this piece of shit to git, only to find
                //  out that it doesn't even accept the password through stdIn
                // String newOrigin;
                /*
                if((username == null || username.isEmpty()) &&
                   (password == null || password.length == 0))
                    encOrigin = origin;
                    // newOrigin = origin;
                else
                    encOrigin = SecurityUtil.encrypt("https://" + username + ":" + new String(password) + "@" + origin); //origin.split("//")[1]);
                    // newOrigin = "https://" + username + ":" + new String(password) + "@" + origin.split("//")[1];
                */
                // System.out.println("HERE!");
                SecurityUtil.writeOrigin("https://" + username + ":" + new String(password) + "@" + origin);
                // System.out.println("DONE!");
                /*
                p = Runtime.getRuntime().exec(new String[] { "git", "remote", "set-url", "origin", newOrigin},
                                              null, FileUtil.getProjectTicketDir());
                p.waitFor();
                */
            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Commits a filename using Git.
     * @param fileName The filename/path to commit
     * @return true if the commit was successful, false otherwise.
     */
    public static boolean commit(String fileName) {
        try {
            return commit(new File(FileUtil.getProjectTicketDir(), fileName));
        } catch(IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Commits a File object using Git using the message "[TICKET] This commit message was auto-generated.".
     * @param file The File object to commit.
     * @return true if the commit was successful, false otherwise.
     */
    public static boolean commit(File file) {
        try {
            Process p = Runtime.getRuntime().exec(new String[] { "git", "add", file.getCanonicalPath()}, null, file.getParentFile());
            p.waitFor();
            
            p = Runtime.getRuntime().exec(new String[] { "git", "commit", file.getCanonicalPath(), "-m",
                                                         "[TICKET] This commit message was auto-generated."
                                                       }, null, file.getParentFile());
            
            return p.waitFor() == 0;
        } catch(IOException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Checks if a given directory is a valid Git directory.
     * @param dirName The directory to check.
     * @return true if the directory is a valid Git directory, false otherwise.
     */
    public static boolean isGitDir(String dirName) {
        return isGitDir(new File(dirName));
    }
    
    /**
     * Checks if a given File object points to a valid Git directory.
     * @param dir The File object to check.
     * @return true if the File object is a valid Git directory, false otherwise.
     */
    public static boolean isGitDir(File dir) {
        try {
            Process p = Runtime.getRuntime().exec(new String[] { "git", "rev-parse" }, null, dir);
            
            return p.waitFor() == 0;
        } catch(IOException e) {
            // e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Syncs the copied repository with remote.
     * <p>
     * Will call {@link #fixRemote()} if remote points to somewhere on the filesystem.
     * @return true if the sync was successful, false otherwise.
     */
    public static boolean sync() {
        // If remote points to somewhere on the system, then fix remote and have it point to the
        //  correct place
        if(new File(getRemote()).exists()) {
            fixRemote();
        }
        
        String origin = SecurityUtil.readOrigin();
        
        return pull(origin) && push(origin);
    }
    
    /**
     * Pushes the copied repository's _Tickets branch to the remote origin if the branch exists.
     * @return true if the push was successful, false otherwise.
     */
    public static boolean push(String origin) {
        try {
            // Process p = Runtime.getRuntime().exec(new String[] { "git", "push", "-u", "origin", "_Tickets" },
            Process p = Runtime.getRuntime().exec(new String[] { "git", "push", "--repo=\"" + origin + "\""},
                                                  null, FileUtil.getProjectTicketDir());
            
            if (p != null) {
                int out = p.waitFor();
            
                return out == 0;
            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Pulls the copied repository's _Tickets branch from the remote origin if the branch exists.
     * @return true if the pull was successful, false otherwise.
     */
    public static boolean pull(String origin) {
        try {
            // Process p = Runtime.getRuntime().exec(new String[] { "git", "pull", "origin", "_Tickets" },
            Process p = Runtime.getRuntime().exec(new String[] { "git", "pull", "\"" + origin + "\"", "_Tickets"},
                                                  null, FileUtil.getProjectTicketDir());
            
            if(p != null) {
                int out = p.waitFor();
            
                return out == 0;
            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Gets what is registered in the user's git config user.email variable.
     * @return The users email as defined in git config.
     */
    public static String getEmail() {
        try {
            Process p = Runtime.getRuntime().exec(new String[] { "git", "config", "--global", "user.email" },
                                                  null, null);
            
            BufferedReader stdInput= new BufferedReader(new InputStreamReader(p.getInputStream()));
            String email = "";
            
            if(p.waitFor() == 0) {
                for (String line = ""; line != null; line = stdInput.readLine()) {
                    email += line;
                }
                
                return email;
            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Gets what is registered in the user's git config user.name variable.
     * @return The users name as defined in git config.
     */
    public static String getAuthor() {
        try {
            Process p = Runtime.getRuntime().exec(new String[] { "git", "config", "--global", "user.name"},
                                                  null, null);
            
            BufferedReader stdInput= new BufferedReader(new InputStreamReader(p.getInputStream()));
            String author = "";
            
            if(p.waitFor() == 0) {
                for (String line = ""; line != null; line = stdInput.readLine()) {
                    author += line;
                }
                
                if(author.length() == 0) return getEmail();
                
                return author;
            }
        } catch(IOException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        
        return null;
    }
}
