package guru.franz.mc.bcl.utils;

public final class Messages {
    public static final String CREATE_CHUNKLOADER_LOG = "%s created a %s at %s with radius %s";
    public static final String CREATE_CHUNKLOADER_USER = "Chunk loader created to cover %s chunks.";
    public static final String EDIT_CHUNKLOADER_LOG_OTHER = "%s edited %s's chunk loader at %s's radius from %s to %s,";
    public static final String EDIT_CHUNKLOADER_LOG_SELF = "%s edited their chunk loader at %s's radius from %s to %s.";
    public static final String EDIT_CHUNKLOADER_USER = "The chunk loader is now updated to cover %s chunks.";
    public static final String DELETE_CHUNKLOADER_LOG_OTHER = "%s deleted %s's chunk loader at %s.";
    public static final String DELETE_CHUNKLOADER_LOG_SELF = "%s deleted their chunk loader at %s.";
    public static final String DELETE_CHUNKLOADER_USER_OTHER = "You just removed %s's chunk loader at %s.";
    public static final String DELETE_CHUNKLOADER_USER_INFORM = "%s just removed your chunk loader at %s.";
    public static final String DELETE_CHUNKLOADER_USER_SELF = "You just removed your chunk loader at %s.";
    public static final String LIST_CHUNKLOADERS_TITLE = "%s chunk loaders";
    public static final String LIST_NO_CHUNKLOADERS = "%s has no chunk loaders";
    public static final String LIST_PERMISSION_ERROR = "You do not have permission to delete this chunk loader";
    public static final String LIST_ACTION_DELETE = "[DEL]";
    public static final String LIST_ACTION_DELETE_HOVER = "Click to delete the chunk loader";
    public static final String LIST_ACTION_TELEPORT = "[TP]";
    public static final String LIST_ACTION_TELEPORT_HOVER = "Click to teleport on top of the chunk loader";
    public static final String CMD_DELETE_OTHER_NO_CHUNKLOADERS = "%s does not have any chunk loaders.";
    public static final String CMD_DELETE_OTHER_SUCCESS = "All %d chunk loaders placed by %s have been removed!";
    public static final String CMD_DELETE_OTHER_SUCCESS_LOG = "%s deleted all %s's chunk loaders.";
    public static final String CMD_DELETE_OTHER_PERMISSION = "You do not have permission to delete others chunk loaders.";
    public static final String CMD_DELETE_OWN_NO_CHUNKLOADERS = "You do not have any chunk loaders to delete.";
    public static final String CMD_DELETE_OWN_SUCCESS = "All %d of your chunk loaders have been removed!";
    public static final String CMD_DELETE_OWN_SUCCESS_LOG = "%s deleted all their chunk loaders.";
    public static final String CMD_DELETE_OWN_PERMISSION = "You do not have permission to delete your own chunk loaders.";
    public static final String CMD_DELETE_OWN_CONFIRM = "Are you sure you want to delete all of your chunk loaders";
    public static final String PLUGIN_DISABLED_DATASTORE = "The plugin is disabled as we could not connect to the datastore - please verify the password and check database information. once correct you can run /bcl reload.";
    public static final String ARGUMENT_INVALID = "%s is not a valid argument, valid arguments: %s";
}
