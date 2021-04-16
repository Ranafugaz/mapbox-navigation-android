package com.mapbox.navigation.core.navigator

import com.mapbox.common.TilesetDescriptor
import com.mapbox.navigation.navigator.internal.MapboxNativeNavigator
import com.mapbox.navigator.CacheHandle

private typealias NativeTilesetDescriptorFactory = com.mapbox.navigator.TilesetDescriptorFactory

/**
 * A factory to build navigation [TilesetDescriptor]
 */
class TilesetDescriptorFactory internal constructor(
    navigator: MapboxNativeNavigator
) {
    private val cache: CacheHandle = navigator.cache!!

    /**
     * Creates TilesetDescriptor using the specified dataset and version.
     *
     * @param tilesDataset string built out of `<account>[.<graph>]` variables.
     * Account can be `mapbox` for default datasets or your username for other.
     * Graph can be left blank if you don't target any custom datasets.
     * @param tilesProfile profile of the dataset.
     * @param version tiles version
     */
    fun build(tilesDataset: String, tilesProfile: String, version: String): TilesetDescriptor =
        NativeTilesetDescriptorFactory.build(
            combineDatasetWithProfile(tilesDataset, tilesProfile),
            version
        )

    /**
     * Creates TilesetDescriptor using the specified dataset and latest locally available version.
     *
     * @param tilesDataset string built out of `<account>[.<graph>]` variables.
     * Account can be `mapbox` for default datasets or your username for other.
     * Graph can be left blank if you don't target any custom datasets.
     * @param tilesProfile profile of the dataset.
     */
    fun buildLatestLocal(tilesDataset: String, tilesProfile: String): TilesetDescriptor =
        NativeTilesetDescriptorFactory.buildLatestLocal(
            cache,
            combineDatasetWithProfile(tilesDataset, tilesProfile)
        )

    /**
     * Creates TilesetDescriptor using the specified dataset and latest version retrieved from
     * the server.
     *
     * @param tilesDataset string built out of `<account>[.<graph>]` variables.
     * Account can be `mapbox` for default datasets or your username for other.
     * Graph can be left blank if you don't target any custom datasets.
     * @param tilesProfile profile of the dataset.
     */
    fun buildLatestServer(tilesDataset: String, tilesProfile: String): TilesetDescriptor =
        NativeTilesetDescriptorFactory.buildLatestServer(
            cache,
            combineDatasetWithProfile(tilesDataset, tilesProfile)
        )

    private fun combineDatasetWithProfile(tilesDataset: String, tilesProfile: String): String {
        return StringBuilder().apply {
            append(tilesDataset)
            append("/")
            append(tilesProfile)
        }.toString()
    }
}
