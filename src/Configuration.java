/**
 * Configuration information storage
 * 
 * @author Tyler Robbins
 * @version 1.0
 * @since 0.8
 */
public final class Configuration {
    /**
     * Dummy constructor to remove the possibility of constructing this object
     */
    private Configuration() { }
    
    /**
     * Is the Gui enabled.
     */
    private static boolean guiEnabled = true;
    
    /**
     * The working directory (where the original project repository is).
     */
    private static String workingDir = null;
    
    /**
     * The working directory as a single name (no path seperators).
     */
    private static String singleDir = null;
    
    /**
     * The version of this program
     */
    private static final String VERSION = "1.0";
    
    /**
     * Gets if the Gui is enabled.
     * @return If the gui is enabled.
     */
    public static boolean isGuiEnabled() {
        return guiEnabled;
    }
    
    /**
     * Sets if the Gui is enabled.
     * @param guiEnabled The value to set {@code Configuration.guiEnabled} to.
     */
    public static void setIsGuiEnabled(boolean guiEnabled) {
        Configuration.guiEnabled = guiEnabled;
    }
    
    /**
     * Gets the working directory (where the original project repository is).
     * @return The working directory.
     */
    public static String getWorkingDir() {
        return workingDir;
    }
    
    /**
     * Sets the working directory (where the original project repository is).
     * @param workingDir The pathname to set the working directory to.
     */
    public static void setWorkingDir(String workingDir) {
        Configuration.workingDir = workingDir;
    }

    /**
     * Sets the working directory as a single name.
     * @param singleDir The name to set the single directory to.
     */
    public static void setWorkingDirAsSingle(String singleDir) {
        Configuration.singleDir = singleDir;
    }

    /**
     * Gets the working directory as a single name.
     * @return The single directory name.
     */
    public static String getWorkingDirAsSingle() {
        return singleDir;
    }

    /**
     * Gets the version of this program.
     * @return The version of this program.
     */
    public static String getVersion() {
        return VERSION;
    }
}
