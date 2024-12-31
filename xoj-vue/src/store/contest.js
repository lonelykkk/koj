import moment from 'moment'
import api from '@/api/contest'
import { CONTEST_STATUS, CONTEST_TYPE } from '@/common/constants'
import time from '@/common/time'

const state = {
    now: moment(),
    intoAccess: false, // 比赛进入权限
    submitAccess: false, // 保护比赛的提交权限
    contest: {
        auth: CONTEST_TYPE.PUBLIC
    },
    isRegistered: false, // 是否报名考核
    contestProblems: [],
    itemVisible: {
        table: true,
        chart: true,
    },
}

const getters = {
    contestStatus: (state, getters) => {
        return state.contest.status;
    },
    contestRuleType: (state, getters) => {
        return state.contest.type;
    },
    isRegistered: () => {
        return state.isRegistered
    },
    isContestAdmin: (state, getters, _, rootGetters) => {
        return rootGetters.isAuthenticated &&
            (state.contest.author === rootGetters.userInfo.username || rootGetters.isSuperAdmin)
    },
    canSubmit: (state, getters) => {
        return state.intoAccess || state.submitAccess || state.contest.auth === CONTEST_TYPE.PUBLIC || getters.isContestAdmin
    },
    contestMenuDisabled: (state, getters) => {
        // 比赛创建者或者超级管理员可以直接查看
        if (getters.isContestAdmin) return false
        // 未开始不可查看
        if (getters.contestStatus === CONTEST_STATUS.SCHEDULED) return true
        if (state.contest.auth === CONTEST_TYPE.PRIVATE) {
            // 私有赛需要通过验证密码方可查看比赛
            return !state.intoAccess
        }
        // 用户未报名不可以查看
        return !state.isRegistered

    },
    problemSubmitDisabled: (state, getters, _, rootGetters) => {
        // 比赛结束不可交题
        if (getters.contestStatus === CONTEST_STATUS.ENDED) {
            return true
            // 比赛未开始不可交题，除非是比赛管理者
        } else if (getters.contestStatus === CONTEST_STATUS.SCHEDULED) {
            return !getters.isContestAdmin
        }
        // 未登录不可交题
        return !rootGetters.isAuthenticated
    },
    // 是否需要显示密码验证框
    passwordFormVisible: (state, getters) => {
        // 如果是公开赛，保护赛，或已注册过，管理员都不用再显示
        return state.contest.auth !== CONTEST_TYPE.PUBLIC && state.contest.auth !== CONTEST_TYPE.PROTECTED && !state.intoAccess && !getters.isContestAdmin
    },
    contestStartTime: (state) => {
        return moment(state.contest.startTime)
    },
    contestEndTime: (state) => {
        return moment(state.contest.endTime)
    },
    // 比赛计时文本显示
    countdown: (state, getters) => {
        // 还未开始的显示
        if (getters.contestStatus === CONTEST_STATUS.SCHEDULED) {

            let durationMs = getters.contestStartTime.diff(state.now, 'seconds')

            let duration = moment.duration(durationMs, 'seconds')

            // time is too long
            if (duration.weeks() > 0) {
                return duration.humanize() + '后开始'
            }

            if (duration.asSeconds() <= 0) {
                state.contest.status = CONTEST_STATUS.RUNNING
            }

            let texts = time.secondFormat(durationMs)

            return '开始剩余时间：' + texts
            // 比赛进行中的显示
        } else if (getters.contestStatus === CONTEST_STATUS.RUNNING) {
            // 倒计时文本显示
            if (getters.contestEndTime.diff(state.now, 'seconds') > 0) {
                let texts = time.secondFormat(getters.contestEndTime.diff(state.now, 'seconds'))
                return '剩余时间：' + texts
            } else {
                state.contest.status = CONTEST_STATUS.ENDED
                return "00:00:00"
            }

        } else {
            return 'Ended'
        }
    },
    // 比赛开始到现在经过的秒数
    BeginToNowDuration: (state, getters) => {
        return moment.duration(state.now.diff(getters.contestStartTime, 'seconds'), 'seconds').asSeconds()
    },

    // 比赛进度条显示
    progressValue: (state, getters) => {
        // 还未开始的显示
        if (getters.contestStatus === CONTEST_STATUS.SCHEDULED) {
            return 0;
            // 比赛进行中的显示
        } else if (getters.contestStatus === CONTEST_STATUS.RUNNING) {
            // 获取比赛开始到现在经过的秒数
            let duration = getters.BeginToNowDuration
            // 消耗时间除以整体时间
            return (duration / state.contest.duration) * 100
        } else {
            return 100;
        }
    },
}

const mutations = {
    changeContest(state, payload) {
        state.contest = payload.contest
        state.isRegistered = payload.registered
    },
    changeContestProblems(state, payload) {
        state.contestProblems = payload.contestProblems
    },
    changeContestItemVisible(state, payload) {
        state.itemVisible = { ...state.itemVisible, ...payload }
    },
    contestRegister(state, payload) {
        state.isRegistered = payload.registered
    },
    contestIntoAccess(state, payload) {
        state.intoAccess = payload.intoAccess
    },
    contestSubmitAccess(state, payload) {
        state.submitAccess = payload.submitAccess
    },
    clearContest(state) {
        state.contest = {}
        state.contestProblems = []
        state.itemVisible = {
            table: true,
            chart: true,
            realName: false
        }
    },
    now(state, payload) {
        state.now = payload.now
    },
    nowAdd1s(state) {
        state.now = moment(state.now.add(1, 's'))
    },
}

const actions = {
    getContest({ commit, rootState, dispatch }) {
        return new Promise((resolve, reject) => {
            api.getContestInfo(rootState.route.params.contestID).then((res) => {
                resolve(res)
                let contest = res.data.data.contest
                let registered = res.data.data.registered
                commit('changeContest', { contest: contest, registered: registered })
                commit('now', { now: moment(contest.now) })
                // if (contest.auth == CONTEST_TYPE.PRIVATE) {
                //     dispatch('getContestAccess', { auth: CONTEST_TYPE.PRIVATE })
                // } else if (contest.auth == CONTEST_TYPE.PROTECTED) {
                //     dispatch('getContestAccess', { auth: CONTEST_TYPE.PROTECTED })
                // }
            }, err => {
                reject(err)
            })
        })
    },

    getContestProblems({ commit, rootState }) {
        return new Promise((resolve, reject) => {
            api.getContestProblem(rootState.route.params.contestID).then(res => {
                resolve(res)
                commit('changeContestProblems', { contestProblems: res.data.data })
            }, (err) => {
                commit('changeContestProblems', { contestProblems: [] })
                reject(err)
            })
        })
    },
    contestRegister({ commit, rootState }, registered) {
        return new Promise((resolve, reject) => {
            commit('contestRegister', { registered: registered })
        })
    }
}

export default {
    state,
    mutations,
    getters,
    actions
}
