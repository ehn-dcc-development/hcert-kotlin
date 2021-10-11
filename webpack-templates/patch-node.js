// Workaround for https://youtrack.jetbrains.com/issue/KT-46082

const webpack = require('webpack')

config.performance = {
    maxEntrypointSize: 512000 * 5,
    maxAssetSize: 512000 * 5
}
config.resolve.fallback = {"util": false, "buffer": false, "stream": false, "crypto": false, "constants": false, "assert":false, "node-inspect-extracted":false}