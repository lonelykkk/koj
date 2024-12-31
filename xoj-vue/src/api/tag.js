import request from '@/utils/request'

// 题目标签

const api_name = '/xoj/tag'

export default {
    getAllTag() {
        return request({
            url: `/api${api_name}/all`,
            method: 'get'
        })
    },
    updateTag(tag) {
        return request({
            url: `/api${api_name}`,
            method: 'put',
            data: tag
        })
    },
    addTag(tag) {
        return request({
            url: `/api${api_name}`,
            method: 'post',
            data: tag
        })
    },
    deleteTagById(tid) {
        return request({
            url: `/api${api_name}`,
            method: 'delete', 
            params: {
                tid
            }
        })
    }

}