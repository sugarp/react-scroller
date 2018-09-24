//config.mode = "production";
//config.devtool = false;
//
//const UglifyJsPlugin = require("uglifyjs-webpack-plugin");
//const OptimizeCSSAssetsPlugin = require("optimize-css-assets-webpack-plugin");
//
//if(!config.optimization) {
//    config.optimization = {}
//}
//
//config.optimization.minimize = true;
//config.optimization.minimizer = [
//    new UglifyJsPlugin({
//        cache: true,
//        parallel: true,
//        uglifyOptions: {
//            mangle: true,
//            output: {
//                comments: false
//            }
//        },
//        sourceMap: false,
//        exclude: [/\.min\.js$/gi]
//    }),
//    new OptimizeCSSAssetsPlugin({}),
//]