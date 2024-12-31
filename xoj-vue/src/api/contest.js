import request from '@/utils/request'

const admin_contest_api_name = '/xoj/contest'
const admin_correction_api_name = '/xoj/contest-correction'
const front_contest_api_name = '/xoj/front-contest'

export default {

//========front=================
    // 获取考核列表分页数据
    getContestPage(limit, currentPage, keyword) {
        return request({
            url: `/api${front_contest_api_name}/get-contest-list`,
            method: 'get',
            params: {
                limit: limit,
                currentPage: currentPage,
                keyword: keyword
            }
        })
    },
    // 获得指定考核的详细信息
    getContestInfo(cid) {
        return request({
            url: `/api${front_contest_api_name}/get-contest-info`,
            method: 'get',
            params: {
                cid: cid
            }
        })
    },
    // 注册考核
    registerContest(data) {
        return request({
            url: `/api${front_contest_api_name}/register-contest`,
            method: 'post',
            data: data
        })
    },
    // 获得指定考核的题目列表
    getContestProblem(cid) {
        return request({
            url: `/api${front_contest_api_name}/get-contest-problem`,
            method: 'get',
            params: {
                cid: cid
            }
        })
    },
    // 获得指定考核的题目详情
    getContestProblemDetails(cid, cpid) {
        return request({
            url: `/api${front_contest_api_name}/get-contest-problem-details`,
            method: 'get',
            params: {
                cid: cid,
                cpid: cpid
            }
        })
    },
    // 获得指定考核的提交列表
    getContestSubmissionList(query) {
        return request({
            url: `/api${front_contest_api_name}/contest-submissions`,
            method: 'get',
            params: {
                limit: query.limit,
                currentPage: query.currentPage,
                onlyMine: query.onlyMine,
                contestID: query.contestID
            }
        })
    },
    // 获得指定考核的通知列表
    getContestAnnouncement(query) {
        return request({
            url: `/api${front_contest_api_name}/get-contest-announcement`,
            method: 'get',
            params: {
                limit: query.limit,
                currentPage: query.currentPage,
                cid: query.cid
            }
        })
    },

//========admin=================
    // 获取考核列表
    getContestList(limit, currentPage, keyword) {
        return request({
            url: `/api${admin_contest_api_name}/get-contest-list`,
            method: 'get',
            params: {
                limit: limit,
                currentPage: currentPage,
                keyword: keyword
            }
        })
    },
    // 获取考核详情
    getContest(cid) {
        return request({
            url: `/api${admin_contest_api_name}`,
            method: 'get',
            params: {
                cid: cid
            }
        })
    },
    // 添加考核
    addContest(data) {
        return request({
            url: `/api${admin_contest_api_name}`,
            method: 'post',
            data: data
        })
    },
    // 删除考核
    deleteContest(cid) {
        return request({
            url: `/api${admin_contest_api_name}`,
            method: 'delete',
            params: {
                cid: cid
            }
        })
    },
    // 修改考核
    updateContest(data) {
        return request({
            url: `/api${admin_contest_api_name}`,
            method: 'put',
            data: data
        })
    },

    // 获取考核题目列表
    getProblemList(query) {
        return request({
            url: `/api${admin_contest_api_name}/get-problem-list`,
            method: 'get',
            params: {
                limit: query.limit,
                currentPage: query.currentPage,
                keyword: query.keyword,
                cid: query.contestId
            }
        })
    },
    // 添加考核题目，只能从公共题库中选取
    addProblemFromPublic(data) {
        return request({
            url: `/api${admin_contest_api_name}/add-problem-from-public`,
            method: 'post',
            data: data
        })
    },
    // 修改考核题目
    updateProblem(data) {
        return request({
            url: `/api${admin_contest_api_name}/update-problem`,
            method: 'put',
            data: data
        })
    },
    // 移除考核题目
    removeProblem(cid, pid) {
        return request({
            url: `/api${admin_contest_api_name}/remove-problem`,
            method: 'delete',
            params: {
                cid: cid,
                pid: pid
            }
        })
    },


    // 获取公告
    getAnnouncementList(limit, currentPage, cid) {
        return request({
            url: `/api${admin_contest_api_name}/announcement`,
            method: 'get',
            params: {
                limit: limit,
                currentPage: currentPage,
                cid: cid
            }
        })
    },
    // 添加公告
    addAnnouncement(data) {
        return request({
            url: `/api${admin_contest_api_name}/announcement`,
            method: 'post',
            data: data
        })
    },
    // 删除公告
    deleteAnnouncement(aid) {
        return request({
            url: `/api${admin_contest_api_name}/announcement`,
            method: 'delete',
            params: {
                aid: aid
            }
        })
    },
    // 修改公告
    updateAnnouncement(data) {
        return request({
            url: `/api${admin_contest_api_name}/announcement`,
            method: 'put',
            data: data
        })
    },

    // 获取参加考核的新生列表
    getContestStudentInfo(query) {
        return request({
            url: `/api${admin_correction_api_name}/get-student-info`,
            method: 'get',
            params: {
                limit: query.limit,
                currentPage: query.currentPage,
                keyword: query.keyword,
                cid: query.contestId
            }
        })
    },
    // 添加批改信息
    addContestCorrection(data) {
        return request({
            url: `/api${admin_correction_api_name}`,
            method: 'post',
            data: data
        })
    },
    // 获取批改信息
    getContestCorrectionInfo(contestId, userId) {
        return request({
            url: `/api${admin_correction_api_name}`,
            method: 'get',
            params: {
                contestId: contestId,
                userId: userId
            }
        })
    },
    // 添加批改信息
    updateContestCorrection(data) {
        return request({
            url: `/api${admin_correction_api_name}`,
            method: 'put',
            data: data
        })
    },
    // 删除批改信息
    deleteContestCorrectionInfo(contestCorrectionId) {
        return request({
            url: `/api${admin_correction_api_name}`,
            method: 'delete',
            params: {
                contestCorrectionId: contestCorrectionId
            }
        })
    },
}