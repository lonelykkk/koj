const { defineConfig } = require('@vue/cli-service')
module.exports = defineConfig({
  publicPath: '/',
  transpileDependencies: true,
  productionSourceMap: false,
  assetsDir: "assets",
  devServer: {
    host: '0.0.0.0',
    port: 6688,
    proxy: {
      '/api': {                                //   以'/api'开头的请求会被代理进行转发
        target: 'http://localhost:9001',
        changeOrigin: true,
        pathRewrite: { // 地址重写
          '^/api': '/',
        }
      }
    },
  },
  // 图标设置
  pwa: {
    iconPaths: {
      favicon32: 'favicon.ico',
      favicon16: 'favicon.ico',
      appleTouchIcon: 'favicon.ico',
      maskIcon: 'favicon.ico',
      msTileImage: 'favicon.ico'
    }
  }
})
