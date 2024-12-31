import moment from 'moment'
import utils from '@/utils/utils'
import time from './time'
import { PROBLEM_LEVEL } from './constants'

// 友好显示时间
function fromNow(time) {
    return moment(time).fromNow()
}

function parseRole(num) {
    if (num == 1) {
        return '超级管理员'
    } else if (num == 2) {
        return '普通管理员'
    } else if (num == 3) {
        return '用户(默认)'
    }
}

function parseContestType(num) {
    if (num == 0) {
        return 'ACM'
    } else if (num == 1) {
        return 'OI'
    }
}

function parseProblemLevel(num) {
    return PROBLEM_LEVEL[num].name;
}

export default {
    submissionMemory: utils.submissionMemoryFormat,
    submissionTime: utils.submissionTimeFormat,
    localtime: time.utcToLocal,
    fromNow: fromNow,
    parseContestType: parseContestType,
    parseRole: parseRole,
    parseProblemLevel: parseProblemLevel,
}
