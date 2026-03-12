package dev.lars.apimanager.apis.languageAPI;

import org.bukkit.OfflinePlayer;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

public interface ILanguageAPI {
    Instant getCreatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getCreatedAtAsync(OfflinePlayer player);

    Instant getUpdatedAt(OfflinePlayer player);

    CompletableFuture<Instant> getUpdatedAtAsync(OfflinePlayer player);

    void setLanguage(OfflinePlayer player, Language language);

    CompletableFuture<Void> setLanguageAsync(OfflinePlayer player, Language language);

    Language getLanguage(OfflinePlayer player);

    CompletableFuture<Language> getLanguageAsync(OfflinePlayer player);
}