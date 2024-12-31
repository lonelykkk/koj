import request from '@/utils/request'

const category_api_name = '/xoj/category'
const discussion_api_name = '/xoj/front-discussion'
const comment_api_name = '/xoj/front-comment'
const amdin_discussion_api = '/xoj/discussion'

export default {
    // 获取贴子评论列表
    getCommentList(query) {
        return request({
            url: `/api${comment_api_name}/get-comment-list`,
            method: 'get',
            params: {
                limit: query.limit,
                currentPage: query.currentPage,
                did: query.did
            }
        })
    },
    // 发布评论
    addComment(data) {
        return request({
            url: `/api${comment_api_name}`,
            method: 'post',
            data: data
        })
    },
    // 删除评论
    deleteComment(cid) {
        return request({
            url: `/api${comment_api_name}`,
            method: 'delete',
            params: {
                cid: cid
            }
        })
    },
    // 点赞评论
    addCommentLike(cid, toLike) {
        return request({
            url: `/api${comment_api_name}/comment-like`,
            method: 'post',
            params: {
                cid: cid,
                toLike: toLike
            }
        })
    },
    // 获取评论回复列表
    getReplyList(cid) {
        return request({
            url: `/api${comment_api_name}/get-reply-list`,
            method: 'get',
            params: {
                cid: cid
            }
        })
    },
    // 添加回复评论
    addReply(data) {
        return request({
            url: `/api${comment_api_name}/reply`,
            method: 'post',
            data: data
        })
    },
    // 删除回复评论
    deleteReply(rid) {
        return request({
            url: `/api${comment_api_name}/reply`,
            method: 'delete',
            params: {
                rid: rid
            }
        })
    },
    // 获取贴子列表
    getDiscussionList(query) {
        return request({
            url: `/api${discussion_api_name}/get-discussion-list`,
            method: 'get',
            params: {
                limit: query.limit,
                currentPage: query.currentPage,
                keyword: query.keyword,
                cid: query.cid,
                pid: query.pid,
                onlyMine: query.onlyMine,
                isAdmin: query.isAdmin
            }
        })
    },
    // 获取贴子详情
    getDiscussionDetail(did) {
        return request({
            url: `/api${discussion_api_name}/get-discussion-detail`,
            method: 'get',
            params: {
                did: did
            }
        })
    },
    // 发布贴子
    addDiscussion(data) {
        return request({
            url: `/api${discussion_api_name}`,
            method: 'post',
            data: data
        })
    },
    // 删除贴子
    deleteDiscussion(did) {
        return request({
            url: `/api${discussion_api_name}`,
            method: 'delete',
            params: {
                did: did
            }
        })
    },
    // 修改贴子
    updateDiscussion(data) {
        return request({
            url: `/api${discussion_api_name}`,
            method: 'put',
            data: data
        })
    },
    // 点赞贴子
    addDiscussionLike(did, toLike) {
        return request({
            url: `/api${discussion_api_name}/discussion-like`,
            method: 'post',
            params: {
                did,
                toLike
            }
        })
    },
    // 举报贴子
    addDiscussionReport(data) {
        return request({
            url: `/api${discussion_api_name}/discussion-report`,
            method: 'post',
            data: data
        })
    },
    // 获取贴子分类
    getCategoryList() {
        return request({
            url: `/api${category_api_name}/get-category-list`,
            method: 'get'
        })
    },
    // 添加贴子分类
    addCategory(data) {
        return request({
            url: `/api${category_api_name}`,
            method: 'post',
            data: data
        })
    },
    // 修改贴子分类
    updateCategory(data) {
        return request({
            url: `/api${category_api_name}`,
            method: 'post',
            data: data
        })
    },
    // 删除贴子分类
    deleteCategory(cid) {
        return request({
            url: `/api${category_api_name}`,
            method: 'delete',
            params: {
                cid: cid
            }
        })
    },
    // 删除贴子
    adminDeleteDiscussion(data) {
        return request({
            url: `/api${amdin_discussion_api}`,
            method: 'delete',
            data: data
        })
    },
    // 修改贴子
    adminUpdateDiscussion(data) {
        return request({
            url: `/api${amdin_discussion_api}`,
            method: 'put',
            data: data
        })
    },
    // 获取举报贴子信息
    adminGetReportList(currentPage, limit) {
        return request({
            url: `/api${amdin_discussion_api}/get-report-list`,
            method: 'get',
            params: {
                limit: limit,
                currentPage: currentPage
            }
        })
    },
    // 修改贴子举报
    adminupdateDiscussionReport(data) {
        return request({
            url: `/api${amdin_discussion_api}/discussion-report`,
            method: 'put',
            data: data
        })
    }
}