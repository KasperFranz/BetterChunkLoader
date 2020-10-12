package guru.franz.mc.bcl.datastore.database;

import guru.franz.mc.bcl.exception.UserNotFound;
import net.kaikk.mc.bcl.CChunkLoader;
import net.kaikk.mc.bcl.datastore.PlayerData;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface DatabaseInterface {


    void setupTable() throws SQLException;

    List<CChunkLoader> getChunkloadersByServerAndWorld(String serverName, String world) throws SQLException;

    Map<UUID, PlayerData> getPlayers() throws SQLException;

    void insertOrUpdatePlayerData(UUID playerId, int worldLoaders, int personalLoaders) throws SQLException;

    PlayerData getPlayerDataByUUID(UUID uuid) throws SQLException, UserNotFound;

    void insertOrUpdateChunkLoader(CChunkLoader chunkLoader) throws SQLException;

    void deleteChunkLoader(CChunkLoader chunkLoader) throws SQLException;

    int deleteChunkLoadersByOwner(UUID ownerId) throws SQLException;
}
