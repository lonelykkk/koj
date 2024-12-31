import request from '@/utils/request'

const front_api_name = '/xoj/front-home'


export default {

    // 获取最近14天的比赛信息列表
    getRecentContest() {
        return request({
            url: `/api${front_api_name}/get-recent-contest`,
            method: 'get'
        })
    },
    // 获取主页公告列表
    getAnnouncementList(limit, currentPage) {
        return request({
            url: `/api${front_api_name}/get-announcement`,
            method: 'get',
            params: {
                limit: limit,
                currentPage: currentPage,
            }
        })
    },
    // 获取最近一周提交统计
    getRecentWeekSubmission() {
        return request({
            url: `/api${front_api_name}/get-recent-week-submission`,
            method: 'get'
        })
    }
}