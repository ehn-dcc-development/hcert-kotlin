// Workaround for https://youtrack.jetbrains.com/issue/KT-46082

const webpack = require('webpack')

config.resolve.alias = {
    crypto: require.resolve("crypto-browserify"),
    buffer: require.resolve("buffer"),
    stream: require.resolve("stream-browserify"),
    util: require.resolve("util")
}

config.plugins.push(
    new webpack.ProvidePlugin({
        process: 'process/browser.js',
        Buffer: ['buffer', 'Buffer'],
    }))