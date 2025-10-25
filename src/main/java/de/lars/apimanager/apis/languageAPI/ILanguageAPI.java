package de.lars.apimanager.apis.languageAPI;

import org.bukkit.OfflinePlayer;

import java.sql.Timestamp;
import java.util.concurrent.CompletableFuture;

public interface ILanguageAPI {
    Timestamp getCreatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getCreatedAtAsync(OfflinePlayer player);

    Timestamp getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Timestamp> getUpdatedAtAsync(OfflinePlayer player);

    void setLanguage(OfflinePlayer player, Integer id);

    CompletableFuture<Void> setLanguageAsync(OfflinePlayer player, Integer id);

    Integer getLanguage(OfflinePlayer player);

    CompletableFuture<Integer> getLanguageAsync(OfflinePlayer player);
}