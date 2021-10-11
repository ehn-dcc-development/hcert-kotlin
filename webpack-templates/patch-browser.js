// Workaround for https://youtrack.jetbrains.com/issue/KT-46082

const webpack = require('webpack')

// Make sure dependencies that explicitly require these (built-in) node.js libraries can find them
config.resolve.alias = {
    crypto: require.resolve("crypto-browserify"),
    buffer: require.resolve("buffer"),
    stream: require.resolve("stream-browserify"),
    util: require.resolve("util"),
    'node-inspect-extracted': require.resolve("node-inspect-extracted"),
    url: require.resolve("url"),
    assert: require.resolve("assert"),
    constants: require.resolve("constants-browserify")
}

// Add polyfills for implicitly required node.js built-ins
config.plugins.push(
    new webpack.ProvidePlugin({
        process: 'process/browser.js',
        Buffer: ['buffer', 'Buffer'],
    }))
config.performance = {
    maxEntrypointSize: 512000*5,
    maxAssetSize: 512000*5
}