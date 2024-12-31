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
        // target: 'http://114.55.134.193:7777',       //   要发向的后台服务器地址  如果后台服务跑在后台开发人员的机器上，就写成 `http://ip:port` 如 `http:192.168.12.213:8081`   ip为后台服务器的ip
        // target: 'http://106.53.179.195:9001',
        target: 'http://127.0.0.1:9001',
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
