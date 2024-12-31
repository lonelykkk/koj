import request from '@/utils/request'

// 用户相关api

const front_api_name = '/xoj/front-user'
const admin_api_name = '/xoj/user-info'


export default {
    
// ========admin========    
    getUserList(currentPage, limit, keyword, onlyAdmin ) {
        return request({
            url: `/api${admin_api_name}/get-user-list`,
            method: 'get',
            params: {
                currentPage: currentPage,
                limit: limit,
                keyword: keyword,
                onlyAdmin: onlyAdmin
            }
        })
    },
    editUser(data) {
        return request({
            url: `/api${admin_api_name}/edit-user`,
            method: 'put',
            data: data
        })
    },
    deleteUsers(ids) {
        return request({
            url: `/api${admin_api_name}/delete-user`,
            method: 'delete',
            data: ids
        })
    },

// ========front========    
    getUserInfo() {
        return request({
            url: `/api${front_api_name}/get-user-info`,
            method: 'get'
        })
    },
    changeUserInfo(data){
        return request({
            url: `/api${front_api_name}/change-userInfo`,
            method: 'put',
            data: data
        })
    },
    getContestList(limit, currentPage) {
        return request({
            url: `/api${front_api_name}/get-contest-list`,
            method: 'get',
            params: {
                currentPage: currentPage,
                limit: limit
            }
        })
    }

}