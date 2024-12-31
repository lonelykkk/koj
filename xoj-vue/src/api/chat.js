import request from '@/utils/request'

const front_api_name = '/xoj/front-chat'
const admin_api_name = '/xoj/announcement'

// AI相关api
export default {

    // 获取对话列表
    getChatList() {
        return request({
            url: `/api${front_api_name}/get-chat-list`,
            method: 'get'
        })
    },
    // 添加对话
    addChat(title) {
        return request({
            url: `/api${front_api_name}/add-chat`,
            method: 'post',
            params: {
                title: title
            }
        })
    },
    // 获取对话记录
    getChatRecord(chatId) {
        return request({
            url: `/api${front_api_name}/get-chat-record`,
            method: 'get',
            params: {
                chatId: chatId
            }
        })
    },
    // 发送对话 send-ask
    sendAsk(chatId, content) {
        return request({
            url: `/api${front_api_name}/send-ask`,
            method: 'post',
            data: {
                chatId: chatId,
                content: content
            }
        })
    },
    // 删除对话
    deleteChat(chatId) {
        return request({
            url: `/api${front_api_name}/delete-chat`,
            method: 'delete',
            params: {
                chatId: chatId
            }
        })
    }
}