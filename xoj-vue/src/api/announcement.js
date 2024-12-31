import request from '@/utils/request'

const front_api_name = ''
const admin_api_name = '/xoj/announcement'

// 通告相关api


export default {

    // 获取公告
    getAnnouncementList(limit, currentPage) {
        return request({
            url: `/api${admin_api_name}`,
            method: 'get',
            params: {
                limit: limit,
                currentPage: currentPage,
            }
        })
    },
    // 添加公告
    addAnnouncement(data) {
        return request({
            url: `/api${admin_api_name}`,
            method: 'post',
            data: data
        })
    },
    // 删除公告
    deleteAnnouncement(aid) {
        return request({
            url: `/api${admin_api_name}`,
            method: 'delete',
            params: {
                aid: aid
            }
        })
    },
    // 修改公告
    updateAnnouncement(data) {
        return request({
            url: `/api${admin_api_name}`,
            method: 'put',
            data: data
        })
    }

}