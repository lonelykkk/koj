import request from '@/utils/request'

// 登录和注册相关api

const front_api_name = '/xoj/front-user'
const admin_api_name = '/xoj/user-info'

export default {
    login(data) {
        return request({
            url: `/api${front_api_name}/login`,
            method: 'post',
            data: data
        })
    },
    logout() {
        return request({
            url: `/api${front_api_name}/logout`,
            method: 'get'
        })
    },

    checkUsernameOrEmail(data) {
        return request({
            url: `/api${front_api_name}/check-username-or-email`,
            method: 'post',
            data: data
        })
    },

    getRegisterCode(email) {
        return request({
            url: `/api${front_api_name}/get-register-code`,
            method: 'get',
            params: {
                email: email
            }
        })
    },

    register(data) {
        return request({
            url: `/api${front_api_name}/register`,
            method: 'post',
            data: data
        })
    },

    changeUserInfo(data) {
        return request({
            url: `/api${front_api_name}/change-userInfo`,
            method: 'put',
            data: data
        })
    },

    changePassword(data) {
        return request({
            url: `/api${front_api_name}/change-password`,
            method: 'post',
            data: data
        })
    },

    getChangeCode(email) {
        return request({
            url: `/api${front_api_name}/get-change-code`,
            method: 'get',
            params: {
                email: email
            }
        })
    },

    changeEmail(data) {
        return request({
            url: `/api${front_api_name}/change-email`,
            method: 'post',
            data: data
        })
    },

    adminLogin(data) {
        return request({
            url: `/api${admin_api_name}/login`,
            method: 'post',
            data: data
        })
    },
    adminLogout() {
        return request({
            url: `/api${admin_api_name}/logout`,
            method: 'get'
        })
    }

}