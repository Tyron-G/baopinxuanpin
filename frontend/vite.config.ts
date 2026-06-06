import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

const apiProxyTarget = process.env.VITE_API_PROXY_TARGET || 'http://127.0.0.1:8088'

export default defineConfig({
  plugins: [
    vue(),
    Components({
      resolvers: [ElementPlusResolver({ importStyle: 'css' })]
    })
  ],
  resolve: {
    alias: {
      '@': '/src'
    }
  },
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (!id.includes('node_modules')) return
          if (id.includes('zrender')) return 'zrender'
          if (id.includes('echarts')) return 'echarts'
          if (
            id.includes('@floating-ui') ||
            id.includes('@popperjs') ||
            id.includes('async-validator') ||
            id.includes('dayjs') ||
            id.includes('lodash-unified') ||
            id.includes('normalize-wheel-es') ||
            id.includes('@ctrl/tinycolor')
          ) {
            return 'element-plus-vendor'
          }
          if (id.includes('vue-router')) return 'vue-router'
          if (
            id.includes('/node_modules/vue/') ||
            id.includes('\\node_modules\\vue\\') ||
            id.includes('/node_modules/@vue/') ||
            id.includes('\\node_modules\\@vue\\')
          ) {
            return 'vue-vendor'
          }
        }
      }
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: apiProxyTarget,
        changeOrigin: true
      }
    }
  }
})
