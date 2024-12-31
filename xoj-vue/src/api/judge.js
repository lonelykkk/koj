import request from '@/utils/request'

const api_name = '/xoj/front-judge'

export default {
    // 题目评测
    submitProblemJudge(data) {
        return request({
            url: `/api${api_name}/submit-problem-judge`,
            method: 'post',
            data: data
        })
    },
    // 题目测试评测
    submitProblemTestJudge(data) {
        return request({
            url: `/api${api_name}/submit-problem-test-judge`,
            method: 'post',
            data: data
        })
    },
    // 获取题目评测状态
    checkSubmissionStatus(submitId) {
        return request({
            url: `/api${api_name}/check-submission-status`,
            method: 'post',
            params: {
                submitId
            } 
        })
    },
    // 获取题目测试评测状态
    getTestJudgeResult(testJudgeKey) {
        return request({
            url: `/api${api_name}/get-test-judge-result`,
            method: 'post',
            params: {
                testJudgeKey
            }
        })
    },
    // 获取用户题目评测列表
    getSubmissionList(query) {
        return request({
            url: `/api${api_name}/get-submission-list`,
            method: 'get',
            params: {
                limit: query.limit,
                currentPage: query.currentPage,
                onlyMine: query.onlyMine,
                problemID: query.problemID
            }
        })
    },
    // 获取评测信息
    getSubmissionDetail(submitId) {
        return request({
            url: `/api${api_name}/get-submission-detail`,
            method: 'get',
            params: {
                submitId
            }
        })
    }
}