import Vue from 'vue'
import VueRouter from 'vue-router'
import { sync } from 'vuex-router-sync'
import adminRoutes from '@/router/adminRoutes'
import frontRoutes from '@/router/frontRoutes'
import myMessage from '@/utils/message'
import store from '@/store'

Vue.use(VueRouter)

//获取原型对象上的push函数
const originalPush = VueRouter.prototype.push
//修改原型对象中的push方法
VueRouter.prototype.push = function push(location) {
  return originalPush.call(this, location).catch(err => err)
}

let routes = new Set([...frontRoutes, ...adminRoutes])
const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

// 路由判断登录 根据路由配置文件的参数(全局身份验证token)
router.beforeEach((to, from, next) => {
  if (to.matched.some(record => record.meta.requireAuth)) { // 判断该路由是否需要登录权限
    const token = localStorage.getItem('token') || ''
    const isSuperAdmin = store.getters.isSuperAdmin
    const isAmdin = store.getters.isAdminRole
    if (token) { // 判断当前的token是否存在 ； 登录存入的token

      if (to.matched.some(record => record.meta.requireSuperAdmin)) { // 判断是否需要超级管理权限

        if (isSuperAdmin) { // 拥有权限就进入
          next()
        } else { // 没有超级管理员权限 全部返回登录页，并且清除缓存
          if (to.path.split('/')[1] === 'admin') { //管理端
            next({
              path: '/admin/login'
            })
            myMessage.error('请重新使用管理员账号登录！')
          } else { // 用户端
            next({
              path: '/home'
            })
            store.commit('changeModalStatus', { mode: 'Login', visible: true })
            myMessage.error('请先登录!')
            store.commit("clearUserInfoAndToken")
          }
        }
      } else if (to.matched.some(record => record.meta.requireAdmin)) { //判断是否需要管理员权限
        if (isAmdin) {
          next()
        } else { // 没有管理员权限 全部返回登录页，并且清除缓存
          if (to.path.split('/')[1] === 'admin') { // 管理端
            next({
              path: '/admin/login'
            })
            myMessage.error('请重新使用管理员账号登录！')
          } else {
            next({
              path: '/home'
            })
            store.commit('changeModalStatus', { mode: 'Login', visible: true })
            myMessage.error('请先登录!')
            store.commit("clearUserInfoAndToken")
          }
        }
      } else {
        next()
      }

    } else { // 如果没有token

      if (to.path.split('/')[1] === 'admin') {
        next({
          path: '/admin/login'  // 管理端无token认证返回登录页
        })
      } else {
        next({
          path: '/home'  // 无token认证的一致返回到主页
        })
        store.commit('changeModalStatus', { mode: 'Login', visible: true })
      }
      store.commit("clearUserInfoAndToken");
      myMessage.error('请先登录!')
    }
  } else { // 不需要登录认证的页面
    next()
  }

})

router.afterEach((to, from, next) => {

})

sync(store, router)

export default router
