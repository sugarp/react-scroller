/*
*   This section allows requiring *.scss from *.js files
*/

var resources = "../resources/main/";
//var resources = "../../resources/main/";

config.entry = {
    main: "./scrollable-demo",
    styles: resources + "sass/main.scss"
}


var MiniCssExtractPlugin = MiniCssExtractPlugin || require("mini-css-extract-plugin");

config.plugins.push(
    new MiniCssExtractPlugin({
        // Options similar to the same options in webpackOptions.output
        // both options are optional
        filename: "[name].css",
        chunkFilename: "[id].css"
    })
)

if(!config.optimization) {
    config.optimization = {}
}

config.optimization.splitChunks = {
    cacheGroups: {
        styles: {
            name: 'styles.bundle',
            test: /\.(sa|sc|c)ss$/,
            chunks: 'all',
            enforce: true
        }
    }
}

/*
-------RULES-------
*/

config.module.rules.push({
     test: /\.(sa|sc|c)ss$/,
     use: [
        MiniCssExtractPlugin.loader,
        'css-loader',
        'sass-loader'
     ],
 })

 config.module.rules.push( {
    test: /\.css$/,
    use: [ 'style-loader', 'css-loader' ]
})



config.module.rules.push({
    test: /\.svg/,
    use: {
        loader: 'svg-url-loader',
        options: {}
    }
})
