import Vue from 'vue'
import App from './App.vue'
import './registerServiceWorker'
import router from './router'
import store from './store'
import ElementUI from 'element-ui' // 引入element-ui
import 'element-ui/lib/theme-chalk/index.css' // 引入element-ui样式
import axios from 'axios' 
import Message from 'vue-m-message'
import 'vue-m-message/dist/index.css'
// import i18n from '@/i18n'

import 'font-awesome/css/font-awesome.min.css' // 引入Font Awesome 字体图标

import VueCropper from 'vue-cropper'
Vue.use(VueCropper)

// import VueKinesis from "vue-kinesis"
import SlideVerify from 'vue-monoplasty-slide-verify'

import Md_Katex from '@iktakahiro/markdown-it-katex'
import Katex from '@/common/katex'

import highlight from '@/common/highlight'

//  markdown编辑器
import mavonEditor from 'mavon-editor' 
import 'mavon-editor/dist/css/index.css'
Vue.use(mavonEditor)

import 'xe-utils'
import VXETable from 'vxe-table'
import 'vxe-table/lib/style.css'

import ECharts from 'vue-echarts/components/ECharts.vue'
import 'echarts/lib/chart/bar'
import 'echarts/lib/chart/line'
import 'echarts/lib/chart/pie'
import 'echarts/lib/component/title'
import 'echarts/lib/component/grid'
import 'echarts/lib/component/dataZoom'
import 'echarts/lib/component/legend'
import 'echarts/lib/component/tooltip'
import 'echarts/lib/component/toolbox'
import 'echarts/lib/component/markPoint'
Vue.component('ECharts', ECharts)

// 使用CDN的方式如下引入echarts
import VueECharts from 'vue-echarts';
Vue.component('ECharts', VueECharts)

import VueDOMPurifyHTML from 'vue-dompurify-html'
Vue.use(VueDOMPurifyHTML)

import filters from '@/common/filters.js'
Object.keys(filters).forEach(key => {   // 注册全局过滤器
  Vue.filter(key, filters[key])
})

import VueClipBoard from 'vue-clipboard2'
Vue.use(VueClipBoard)

// Vue.use(VueKinesis)
Vue.use(SlideVerify) // 滑动验证码组件
Vue.use(Katex)  // 数学公式渲染
Vue.use(highlight) // 代码高亮
Vue.use(ElementUI)
Vue.use(Message, { name: 'msg' }) // `Vue.prototype.$msg` 全局消息提示
// VXETable.setup({
//   // 对组件内置的提示语进行国际化翻译
//   i18n: (key, value) => i18n.t(key, value)
// })
Vue.use(VXETable) // 表格组件
Vue.prototype.$axios = axios


Vue.prototype.$markDown = mavonEditor.mavonEditor.getMarkdownIt().use(Md_Katex)  // 挂载到vue

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
