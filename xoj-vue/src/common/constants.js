export const PROBLEM_LEVEL = {
    '0': {
        name: '简单',
        color: '#19be6b'
    },
    '1': {
        name: '中等',
        color: '#2d8cf0'
    },
    '2': {
        name: '困难',
        color: '#ed3f14'
    }
}

export const JUDGE_STATUS = {
    '-10': {
        name: 'Not Submitted',
        short: 'NS',
        color: 'gray',
        type: 'info',
        rgb: '#909399'
    },
    '-5': {
        name: 'Submitted Unknown Result',
        short: 'SNR',
        color: 'gray',
        type: 'info',
        rgb: '#909399'
    },
    '-4': {
        name: 'Cancelled',
        short: 'CA',
        color: 'purple',
        type: 'info',
        rgb: '#676fc1'
    },
    '-3': {
        name: 'Presentation Error',
        short: 'PE',
        color: 'yellow',
        type: 'warning',
        rgb: '#f90'
    },
    '-2': {
        name: 'Compile Error',
        short: 'CE',
        color: 'yellow',
        type: 'warning',
        rgb: '#f90'
    },
    '-1': {
        name: 'Wrong Answer',
        short: 'WA',
        color: 'red',
        type: 'error',
        rgb: '#ed3f14'
    },
    '0': {
        name: 'Accepted',
        short: 'AC',
        color: 'green',
        type: 'success',
        rgb: '#19be6b'
    },
    '1': {
        name: 'Time Limit Exceeded',
        short: 'TLE',
        color: 'red',
        type: 'error',
        rgb: '#ed3f14'
    },
    '2': {
        name: 'Memory Limit Exceeded',
        short: 'MLE',
        color: 'red',
        type: 'error',
        rgb: '#ed3f14'
    },
    '3': {
        name: 'Runtime Error',
        short: 'RE',
        color: 'red',
        type: 'error',
        rgb: '#ed3f14'
    },
    '4': {
        name: 'System Error',
        short: 'SE',
        color: 'gray',
        type: 'info',
        rgb: '#909399'
    },
    '5': {
        name: 'Pending',
        color: 'yellow',
        type: 'warning',
        rgb: '#f90'
    },
    '6': {
        name: 'Compiling',
        short: 'CP',
        color: 'green',
        type: 'info',
        rgb: '#25bb9b'
    },
    '7': {
        name: 'Judging',
        color: 'blue',
        type: '',
        rgb: '#2d8cf0'
    },
    '8': {
        name: 'Partial Accepted',
        short: 'PAC',
        color: 'blue',
        type: '',
        rgb: '#2d8cf0'
    },
    '9': {
        name: 'Submitting',
        color: 'yellow',
        type: 'warning',
        rgb: '#f90'
    },
    '10': {
        name: "Submitted Failed",
        color: 'gray',
        short: 'SF',
        type: 'info',
        rgb: '#909399',
    }
}

export const JUDGE_STATUS_RESERVE = {
    'ns': -10,      // 未提交
    'snr': -5,      // 提交没有结果
    'ca': -4,       // 评测取消
    'pe': -3,       // 输出格式错误
    'ce': -2,       // 编译错误
    'wa': -1,       // 答案错误
    'ac': 0,        // 评测通过
    'tle': 1,       // 时间超限
    'mle': 2,       // 空间超限
    're': 3,        // 运行错误
    'se': 4,        // 系统错误
    'Pending': 5,   // 排队中
    'Compiling': 6, // 编译中
    'Judging': 7,   // 评测中
    'pa': 8,        // 部分通过
    'sf': 10,       // 提交失败
}

export const CONTEST_STATUS = {
    'SCHEDULED': -1,
    'RUNNING': 0,
    'ENDED': 1
}

export const CONTEST_STATUS_REVERSE = {
    '-1': {
        name: 'Scheduled',
        name2: '未开始',
        color: '#f90'
    },
    '0': {
        name: 'Running',
        name2: '进行中',
        color: '#19be6b'
    },
    '1': {
        name: 'Ended',
        name2: '已结束',
        color: '#ed3f14'
    }
}

export const RULE_TYPE = {
    ACM: 0,
    OI: 1
}

export const CONTEST_TYPE_REVERSE = {
    '0': {
        name: 'Public',
        color: 'success',   
        tips: 'Public_Tips',
        submit: true,              // 公开赛可看可提交
        look: true,
    },
    '1': {
        name: 'Private',
        color: 'danger',
        tips: 'Private_Tips',
        submit: false,         // 私有赛 必须要密码才能看和提交
        look: false,
    }
}

export const CONTEST_TYPE = {
    PUBLIC: 0,
    PRIVATE: 1
}

export const USER_TYPE = {
    REGULAR_USER: 'user',
    ADMIN: 'admin',
    SUPER_ADMIN: 'root'
}