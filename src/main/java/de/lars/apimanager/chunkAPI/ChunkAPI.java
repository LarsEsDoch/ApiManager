package de.lars.apiManager.chunkAPI;

public class ChunkAPI {

    private static IChunkAPI api;

    public static IChunkAPI getApi() {
        return api;
    }

    public static void setApi(IChunkAPI api) {
        ChunkAPI.api = api;
    }
}
