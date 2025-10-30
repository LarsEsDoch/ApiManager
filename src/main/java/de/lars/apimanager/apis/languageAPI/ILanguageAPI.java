package de.lars.apimanager.apis.languageAPI;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface ILanguageAPI {
    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

    void setLanguage(OfflinePlayer player, Integer id);

    CompletableFuture<Void> setLanguageAsync(OfflinePlayer player, Integer id);

    Integer getLanguage(OfflinePlayer player);

    CompletableFuture<Integer> getLanguageAsync(OfflinePlayer player);
}