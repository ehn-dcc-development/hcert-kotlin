config.target="node"
config.output.filename="hcert-node.js"
config.performance = {
    maxEntrypointSize: 512000 * 5,
    maxAssetSize: 512000 * 5
}
config.resolve.fallback = {"node-inspect-extracted":false}