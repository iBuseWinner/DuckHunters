package ru.fennec.free.duckhunters.handlers.database.data.mappers;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;
import ru.fennec.free.duckhunters.common.interfaces.IGamePlayer;
import ru.fennec.free.duckhunters.handlers.players.GamePlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GamePlayerMapper implements RowMapper<IGamePlayer> {
    @Override
    public IGamePlayer map(ResultSet rs, StatementContext ctx) throws SQLException {
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("wins", rs.getLong("wins"));
        statistics.put("loses", rs.getLong("loses"));
        statistics.put("kills", rs.getLong("kills"));
        statistics.put("deaths", rs.getLong("deaths"));
        return new GamePlayer(
                rs.getLong("id"),
                UUID.fromString(rs.getString("uuid")),
                statistics);
    }
}
