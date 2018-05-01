module.exports = {
    entry: './server/server.ts',
    output: {
        path: __dirname + '/dist',
        filename: 'server.js',
    },
    resolve: {
        // Add '.ts' and '.tsx' as a resolvable extension.
        extensions: ['.ts', '.tsx', '.js'],
    },
    module: {
        rules: [
            // All files with a '.ts' or '.tsx'
            // extension will be handled by 'ts-loader'
            {
                test: /\.tsx?$/,
                loader: 'ts-loader',
            },
        ],
    },
    target: 'node',
};