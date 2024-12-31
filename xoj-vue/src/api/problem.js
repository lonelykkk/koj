import request from '@/utils/request'

const api_name = '/xoj/problem'
const front_api_name = '/xoj/front-problem'

export default {

//===========front============
    getProblemList(query) {
        return request({
            url: `/api${front_api_name}/get-problem-list`,
            method: 'get',
            params: {
                limit: query.limit,
                currentPage: query.currentPage,
                keyword: query.keyword,
                tagId: query.tagId,
                difficulty: query.difficulty,
                type: query.type
            }
        })
    },
    getProblemDetail(problemID) {
        return request({
            url: `/api${front_api_name}/get-problem-detail`,
            method: 'get',
            params: {
                pid: problemID
            }
        })
    },

//===========admin============
    getAllProblem(query) {
        return request({
            url: `/api${api_name}/getAll`,
            method: 'get',
            params: {
                limit: query.limit,
                currentPage: query.currentPage,
                keyword: query.keyword,
                auth: query.auth,
                contestId: query.contestId
            }
        })
    },
    updateProblem(data) {
        return request({
            url: `/api${api_name}`,
            method: 'put',
            data: data
        })
    },
    addProblem(data) {
        return request({
            url: `/api${api_name}`,
            method: 'post',
            data: data
        })
    },
    getProblem(pid) {
        return request({
            url: `/api${api_name}`,
            method: 'get',
            params: {
                pid
            }
        })
    },
    deleteProblem(pid) {
        return request({
            url: `/api${api_name}`,
            method: 'delete',
            params: {
                pid
            }
        })
    }
}
