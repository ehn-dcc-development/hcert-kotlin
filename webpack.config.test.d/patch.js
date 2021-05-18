// Workaround for https://youtrack.jetbrains.com/issue/KT-46082

const webpack = require('webpack')
const path = require('path')

// Make sure dependencies that explicitly require these (built-in) node.js libraries can find them
config.resolve.alias = {
    crypto: require.resolve("crypto-browserify"),
    buffer: require.resolve("buffer"),
    stream: require.resolve("stream-browserify"),
    util: require.resolve("util"),
    'node-inspect-extracted': require.resolve("node-inspect-extracted"),
    url: require.resolve("url"),
}

config.resolve.modules.push(
    path.resolve("src/commonTest/resources")
)


// Add polyfills for implicitly required node.js built-ins
config.plugins.push(
    new webpack.ProvidePlugin({
        process: 'process/browser.js',
        Buffer: ['buffer', 'Buffer'],
    }))