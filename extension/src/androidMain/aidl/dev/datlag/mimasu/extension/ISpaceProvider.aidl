package dev.datlag.mimasu.extension;

import dev.datlag.mimasu.extension.space.SpaceCallback;

interface ISpaceProvider {
    const int VERSION = 1;

    void requestSpace(in SpaceCallback callback);
    void clearCache(in SpaceCallback callback);
    void clearStorage(in SpaceCallback callback);
}