import request from '@/utils/request'

const admin_api_name = '/xoj/system'

// 仪表盘相关api
export default {

    // 获取系统运行信息
    getSystemInfo() {
        return request({
            url: `/api${admin_api_name}/get-system-info`,
            method: 'get'
        })
    }

}