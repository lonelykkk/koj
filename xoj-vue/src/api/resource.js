import request from '@/utils/request'

const admin_api_name = '/xoj/resource'
const front_api_name = '/xoj/front-resource'

export default {

    //=========front============
    getResources(keyword, resourceTagId) {
        return request({
            url: `/api${front_api_name}/get-resource-list`,
            method: 'get',
            params: {
                keyword: keyword,
                resourceTagId: resourceTagId
            }
        })
    },
    getTags() {
        return request({
            url: `/api${front_api_name}/get-resource-tag`,
            method: 'get'
        })
    },


    //=========admin============
    getResourceList(limit, currentPage, keyword) {
        return request({
            url: `/api${admin_api_name}/get-resource-list`,
            method: 'get',
            params: {
                keyword: keyword,
                currentPage: currentPage,
                limit: limit
            }
        })
    },
    addResource(data) {
        return request({
            url: `/api${admin_api_name}`,
            method: 'post',
            data: data
        })
    },
    updateResource(data) {
        return request({
            url: `/api${admin_api_name}`,
            method: 'put',
            data: data
        })
    },
    deleteResource(rid) {
        return request({
            url: `/api${admin_api_name}`,
            method: 'delete',
            params: {
                rid: rid
            }
        })
    },

    getTagList() {
        return request({
            url: `/api${admin_api_name}/get-tag-list`,
            method: 'get'
        })
    },
    addTag(data) {
        return request({
            url: `/api${admin_api_name}/add-tag`,
            method: 'post',
            data: data
        })
    },
    updateResourceTag(data) {
        return request({
            url: `/api${admin_api_name}/update-tag`,
            method: 'put',
            data: data
        })
    },
    deleteResourceTag(resourceTagId) {
        return request({
            url: `/api${admin_api_name}/delete-tag`,
            method: 'delete',
            params: {
                resourceTagId: resourceTagId
            }
        })
    },
}