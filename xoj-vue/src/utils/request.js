import axios from 'axios'
import Vue from 'vue'
import router from '@/router'
import store from "@/store"
import mMessage from '@/utils/message'
import utils from '@/utils/utils'

// 检测用户的设备是否为移动设备
const isMobile = /ipad|iphone|midp|rv:1.2.3.4|ucweb|android|windows ce|windows mobile/.test(navigator.userAgent.toLowerCase());


// 创建axios实例
const service = axios.create({
    timeout: 90000 // 请求超时时间
})

// 请求拦截器
service.interceptors.request.use(
    config => {
        // 每次发送请求之前判断vuex中是否存在token
        const token = localStorage.getItem('token')
        if (config.url != '/api/xoj/user-info/login' && config.url != '/api/xoj/front-user/login') {
            token && (config.headers.Authorization = token)
        }
        let type = config.url.split("/")[2]
        // 携带请求区别是否为admin
        if (type === 'user-info') {
            config.headers['Url-Type'] = 'admin'
        } else {
            config.headers['Url-Type'] = 'general'
        }

        return config
    },
    error => {
        mMessage.error(error.response.data.message)
        if (!isMobile) {
            Vue.prototype.$notify.error({
                title: '错误',
                message: error.response.data.message,
                duration: 5000,
                offset: 50
            });
        }
        return Promise.error(error);
    }
)

// 响应拦截器
service.interceptors.response.use(
    response => {
        if (response.headers['refresh-token']) { // token续约！
            store.commit('changeUserToken', response.headers['authorization'])
        }
        if (response.data.code === 20000 || response.data.code == undefined) {
            return Promise.resolve(response);
        } else {
            if (!isMobile) {
                Vue.prototype.$notify.error({
                    title: '错误',
                    message: response.data.message,
                    duration: 5000,
                    offset: 50
                });
            } else {
                mMessage.error(response.data.message);
            }
            return Promise.reject(response);
        }

    },
    // 服务器状态码不是200的情况
    error => {
        if (error.response) {
            if (error.response.headers['refresh-token']) { // token续约！！
                store.commit('changeUserToken', error.response.headers['authorization'])
            }
            if (error.response.data instanceof Blob) { // 如果是文件操作的返回，由后续进行处理
                return Promise.resolve(error.response);
            }
            console.log(error.response);
            switch (error.response.status) {
                // 401: 未登录 token过期
                // 未登录则跳转登录页面，并携带当前页面的路径
                // 在登录成功后返回当前页面，这一步需要在登录页操作。
                case 401:
                    if (error.response.data.message) {
                        if (!isMobile) {
                            Vue.prototype.$notify.error({
                                title: '错误',
                                message: error.response.data.message,
                                duration: 5000,
                                offset: 50
                            });
                        } else {
                            mMessage.error(error.response.data.message);
                        }
                    }
                    if (error.response.config.headers['Url-Type'] === 'admin') {
                        router.push("/admin/login")
                    } else {
                        store.commit('changeModalStatus', { mode: 'Login', visible: true });
                    }
                    store.commit('clearUserInfoAndToken');
                    break;
                // 403
                // 无权限访问或操作的请求
                case 403:
                    if (error.response.data.message) {
                        if (!isMobile) {
                            Vue.prototype.$notify.error({
                                title: '错误',
                                message: error.response.data.message,
                                duration: 5000,
                                offset: 50
                            });
                        } else {
                            mMessage.error(error.response.data.message);
                        }
                    }
                    let isAdminApi = error.response.config.url.startsWith('/api/admin')
                    if (isAdminApi) {
                        router.push("/admin")
                    }
                    break;
                // 404请求不存在
                case 40004:
                    mMessage.error('查询错误，找不到要请求的资源！');
                    break;
                // 其他错误，直接抛出错误提示
                default:
                    if (error.response.data) {
                        if (error.response.data.message) {
                            if (!isMobile) {
                                Vue.prototype.$notify.error({
                                    title: '错误',
                                    message: error.response.data.message,
                                    duration: 5000,
                                    offset: 50
                                });
                            } else {
                                mMessage.error(error.response.data.message);
                            }
                        } else {
                            mMessage.error('服务器错误，请重新刷新！');
                        }
                    }
                    break;
            }
            console.log(error.response.status)
            return Promise.reject(error)
        } else { //处理断网或请求超时，请求没响应
            if (error.code == 'ECONNABORTED' || error.message.includes('timeout')) {
                mMessage.error('请求超时，请稍后再尝试！')
            } else {
                mMessage.error('网络错误，与服务器链接出现异常，请稍后再尝试！')
            }
            return Promise.reject(error)
        }
    }
)

export default service