import request from '@/utils/request'

const front_api_name = '/xoj/front-about'
const admin_api_name = '/xoj/studio-info'

export default {
    //===========front===========
    getMember() {
        return request({
            url: `/api${front_api_name}/get-member`,
            method: 'get'
        })
    },
    getAwards(limit, currentPage) {
        return request({
            url: `/api${front_api_name}/get-awards`,
            method: 'get',
            params: {
                limit: limit,
                currentPage: currentPage
            }
        })
    },

    //===========admin===========
    getMemberList(limit, currentPage, keyword) {
        return request({
            url: `/api${admin_api_name}/get-member-list`,
            method: 'get',
            params: {
                limit: limit,
                currentPage: currentPage,
                keyword: keyword
            }
        })
    },
    addStudioInfo(data) {
        return request({
            url: `/api${admin_api_name}`,
            method: 'post',
            data: data
        })
    },
    updateStudioInfo(data) {
        return request({
            url: `/api${admin_api_name}`,
            method: 'put',
            data: data
        })
    },
    deleteStudioInfo(sid) {
        return request({
            url: `/api${admin_api_name}`,
            method: 'delete',
            params: {
                sid: sid
            }
        })
    },

    getAwardsList(limit, currentPage, keyword) {
        return request({
            url: `/api${admin_api_name}/get-awards-list`,
            method: 'get',
            params: {
                limit: limit,
                currentPage: currentPage,
                keyword: keyword
            }
        })
    },
    addAwards(data) {
        return request({
            url: `/api${admin_api_name}/add-awards`,
            method: 'post',
            data: data
        })
    },
    updateAwards(data) {
        return request({
            url: `/api${admin_api_name}/update-awards`,
            method: 'put',
            data: data
        })
    },
    deleteAwards(aid) {
        return request({
            url: `/api${admin_api_name}/delete-awards`,
            method: 'delete',
            params: {
                aid: aid
            }
        })
    }
}