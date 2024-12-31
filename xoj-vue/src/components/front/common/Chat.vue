<template>
    <div id="chat">
        <div v-bind:class="left" :style="[{ height: divHei + 'px' }]">

            <div style=" display: flex; position: relative ">
                <div style="margin-left: 20px">
                    <div style="color:black;font-family: 微软雅黑; font-size: 24px">{{ userInfo.username }}</div>
                </div>
            </div>


            <div class="addct" :style="[{ height: lm + 'px' }]" style="overflow: auto; ">
                <div v-if="chatList.length == 0" class="xin">
                    暂无对话
                </div>
                <div class="li onli" v-for="chat in chatList" :key="chat.id" @click="goChat(chat)">
                    <div class="xin">
                        {{ chat.title }}
                    </div>
                    <div style="display: flex; justify-content: space-between">
                        <div class="yitiao">{{ chat.chatCount }}条对话</div>
                        <div class="time">{{ chat.gmtCreate | fromNow }}</div>
                    </div>
                </div>
            </div>

            <div
                style="cursor:pointer; height: 1px; width: 258px; border: #e7f8ff 2px solid; border-radius: 10px;margin: 0 auto;margin-top: 20px;margin-left: 20px;margin-right: 20px;background-color: #e7f8ff; ">
            </div>


            <div style="position: absolute; bottom: 10px;width:200px; height: 50px; overflow: hidden">
                <div style="display: flex; justify-content: space-between">
                    <div class="szlt">
                        <div style="padding-top: 10px;margin-left: 14px">
                            <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="16"
                                height="16" fill="none">
                                <defs>
                                    <path id="settings_svg__a" d="M0 0h16v16H0z"></path>
                                </defs>
                                <g>
                                    <mask id="settings_svg__b" fill="#fff">
                                        <use xlink:href="#settings_svg__a"></use>
                                    </mask>
                                    <g mask="url(#settings_svg__b)">
                                        <path transform="translate(1.333 2.333)"
                                            d="M13.33 5.67 10 0H3.33L0 5.67l3.33 5.66H10l3.33-5.66Z"
                                            style="stroke: rgb(51, 51, 51); stroke-width: 1.33333; stroke-opacity: 1; stroke-dasharray: 0, 0;">
                                        </path>
                                        <path transform="translate(6.333 6.333)"
                                            d="M3.33 1.67C3.33.75 2.59 0 1.67 0 .75 0 0 .75 0 1.67c0 .92.75 1.66 1.67 1.66.92 0 1.66-.74 1.66-1.66Z"
                                            style="stroke: rgb(51, 51, 51); stroke-width: 1.33333; stroke-opacity: 1; stroke-dasharray: 0, 0;">
                                        </path>
                                    </g>
                                </g>
                            </svg>
                        </div>
                        <div style="margin-top: 10px" class="add" @click="addConversation">&nbsp;新的聊天</div>
                    </div>
                </div>
            </div>
        </div>

        <div v-bind:class="right" :style="[{ height: he + 'px' }]">

            <div :style="[{ height: he + 'px' }]" style="border-left: #4f4f4f 3px solid;">

            </div>
            <div class="dom1 ow" style="width: 100%">
                <div :style="[{ height: he + 'px' }]" class="message">

                    <div class="messagetop">
                        <div v-if="nowChat === null" style="padding-left: 5px; font-size: 20px;">暂无对话</div>
                        <div v-else>
                            <div class="messagetoptitle"><strong>{{ this.nowChat.title }}</strong></div>
                            <div style="display: flex; justify-content: space-between">
                                <div class="messagetopnum">与AI {{ this.nowChat.chatCount }} 条对话</div>
                                <div class="time">{{ this.nowChat.gmtCreate | fromNow }}</div>
                            </div>
                        </div>
                    </div>

                    <div ref="chatContainer" class="onmessage" style="overflow: auto; " :style="[{ height: ct + 'px' }]">

                        <div v-for="chatInfo in chatInfoList" :key="chatInfo.id">
                            <div class="messageright" style="text-align: right;">
                                <div style="margin-top:20px;border-radius: 10px; margin-right: 20px">
                                    <img :src="userInfo.avatar" width="30">
                                </div>
                                <div class="messagerightct">
                                    {{ chatInfo.question }}
                                </div>
                            </div>
                            <div class="messageleft">
                                <div class="messageleftimg">
                                    <svg xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink"
                                        width="30" height="30" fill="none" class="home_user-avtar__3QksJ">
                                        <defs>
                                            <path id="bot_svg__a" d="M0 0h30v30H0z"></path>
                                            <path id="bot_svg__c" d="M0 0h20.455v20.455H0z"></path>
                                        </defs>
                                        <g>
                                            <g mask="url(#bot_svg__b)">
                                                <g transform="translate(4.773 4.773)">
                                                    <mask id="bot_svg__d" fill="#fff">
                                                        <use xlink:href="#bot_svg__c"></use>
                                                    </mask>
                                                    <g mask="url(#bot_svg__d)">
                                                        <path fill-rule="evenodd"
                                                            d="M19.11 8.37c.17-.52.26-1.06.26-1.61 0-.9-.24-1.79-.71-2.57a5.24 5.24 0 0 0-4.53-2.59c-.37 0-.73.04-1.09.11A5.201 5.201 0 0 0 9.17 0h-.04C6.86 0 4.86 1.44 4.16 3.57A5.11 5.11 0 0 0 .71 6.04C.24 6.83 0 7.72 0 8.63c0 1.27.48 2.51 1.35 3.45-.18.52-.27 1.07-.27 1.61 0 .91.25 1.8.71 2.58 1.13 1.94 3.41 2.94 5.63 2.47a5.18 5.18 0 0 0 3.86 1.71h.05c2.26 0 4.27-1.44 4.97-3.57a5.132 5.132 0 0 0 3.45-2.47c.46-.78.7-1.67.7-2.58 0-1.28-.48-2.51-1.34-3.46ZM8.947 18.158c-.04.03-.08.05-.12.07.7.58 1.57.89 2.48.89h.01c2.14 0 3.88-1.72 3.88-3.83v-4.76c0-.02-.02-.04-.04-.05l-1.74-.99v5.75c0 .23-.13.45-.34.57l-4.13 2.35Zm-.67-1.153 4.17-2.38c.02-.01.03-.03.03-.05v-1.99l-5.04 2.87c-.21.12-.47.12-.68 0l-4.13-2.35c-.04-.02-.09-.06-.12-.07-.04.21-.06.43-.06.65 0 .67.18 1.33.52 1.92v-.01c.7 1.19 1.98 1.92 3.37 1.92.68 0 1.35-.18 1.94-.51ZM3.903 5.168v-.14c-.85.31-1.57.9-2.02 1.68a3.78 3.78 0 0 0-.52 1.91c0 1.37.74 2.64 1.94 3.33l4.17 2.37c.02.01.04.01.06 0l1.75-1-5.04-2.87a.64.64 0 0 1-.34-.57v-4.71Zm13.253 3.337-4.18-2.38c-.02 0-.04 0-.06.01l-1.74.99 5.04 2.87c.21.12.34.34.34.58v4.85c1.52-.56 2.54-1.99 2.54-3.6 0-1.37-.74-2.63-1.94-3.32ZM8.014 5.83c-.02.01-.03.03-.03.05v1.99L13.024 5a.692.692 0 0 1 .68 0l4.13 2.35c.04.02.08.05.12.07.03-.21.05-.43.05-.65 0-2.11-1.74-3.83-3.88-3.83-.68 0-1.35.18-1.94.51l-4.17 2.38Zm1.133-4.492c-2.15 0-3.89 1.72-3.89 3.83v4.76c0 .02.02.03.03.04l1.75 1v-5.75c0-.23.13-.45.34-.57l4.13-2.35c.04-.03.09-.06.12-.07-.7-.58-1.58-.89-2.48-.89ZM7.983 11.51l2.24 1.27 2.25-1.27V8.95l-2.25-1.28-2.24 1.28v2.56Z"
                                                            style="fill: rgb(31,148,140);"></path>
                                                    </g>
                                                </g>
                                            </g>
                                        </g>
                                    </svg>
                                </div>

                                <div v-if="chatInfo.answer === ''" class="messageleftct">
                                    加载中...
                                </div>
                                <div v-else class="messageleftct">
                                    <Markdown :content="chatInfo.answer" style="padding: 10px; width: 500px;">
                                    </Markdown>
                                </div>
                                <div class="time" style="margin-left:20px;">{{ chatInfo.gmtCreate | fromNow }}</div>

                            </div>
                        </div>

                    </div>
                </div>

                <textarea placeholder="请输入您的问题" class="answer" style="resize:none !important;"
                    @keydown.ctrl.enter="handleKeyPress" v-model="nowContent">
                </textarea>
                <div class="send" @click="send()">
                    Enter 发送
                </div>
            </div>
        </div>

        <el-dialog title="输入对话标题" :visible.sync="dialogVisible" width="30%" append-to-body>
            <el-input v-model="newChatTitle" placeholder="请输入内容"></el-input>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="addChat()">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>
  
<script>

import { mapGetters, mapActions } from 'vuex'
import myMessage from '@/utils/message'
import chatApi from '@/api/chat'
import Markdown from './Markdown.vue'
import { addCodeBtn } from '@/utils/codeblock'
export default {
    props: {
        command: {
            type: String,
            default: 'list'
        }
    },
    components: {
        Markdown
    },
    data() {
        return {
            left: {
                active: true
            },
            right: {
                leftcla: true
            },
            divHei: 0,
            he: 0,
            ct: 0,
            lm: 0,
            dialogVisible: false,
            newChatTitle: '',
            chatList: [],
            nowChat: null,
            nowContent: '',
            chatInfoList: []
        }
    },
    mounted() {
        this.getHtmlHeight();
        this.getChatList();
    },
    methods: {
        getHtmlHeight() {
            this.he = window.innerHeight * 0.60;
            this.ct = (window.innerHeight - 270) * 0.60;
            this.divHei = (window.innerHeight - 50) * 0.60;
            this.lm = (window.innerHeight - 270) * 0.60;
        },
        getChatList() {
            chatApi.getChatList().then(
                (res) => {
                    this.chatList = res.data.data
                    this.dialogVisible = false
                    if (chatList.length > 0) {
                        this.nowChat = this.chatList[0]
                    }
                }
            )
        },
        addConversation() {
            this.dialogVisible = true
            this.newChatTitle = ''
        },
        addChat() {
            console.log('add chat')
            chatApi.addChat(this.newChatTitle).then(
                (res) => {
                    this.getChatList()
                    this.dialogVisible = false
                },
                (err) => {
                    this.dialogVisible = false
                }
            )
        },
        send() {
            if (this.nowContent.trim() == '') {
                myMessage.warning('请输入内容')
                this.nowContent = ''
                return
            }
            console.log('send')
            const data = {
                id: '-1',
                question: this.nowContent,
                answer: '',
                gmtCreate: '',
            }
            this.chatInfoList.push(data)
            // 使用 Vue.nextTick() 等待 Vue 实例更新 DOM
            this.$nextTick(() => {
                // 获取滚动容器元素
                const chatContainer = this.$refs.chatContainer;
                // 将滚动条位置设置为最底部
                chatContainer.scrollTop = chatContainer.scrollHeight;
            });
            this.nowContent = ''
            chatApi.sendAsk(this.nowChat.id, data.question).then(
                (res) => {
                    // this.chatInfoList[this.chatInfoList.length - 1] = res.data.data
                    let index = this.chatInfoList.length - 1
                    this.chatInfoList.splice(index, 1, res.data.data)
                    this.nowChat.chatCount++;
                    this.$nextTick((_) => {
                        addCodeBtn()
                    })
                },
                (err) => {

                }
            )
            this.$nextTick(() => {
                // 获取滚动容器元素
                const chatContainer = this.$refs.chatContainer;
                // 将滚动条位置设置为最底部
                chatContainer.scrollTop = chatContainer.scrollHeight;
            });
        },
        goChat(chat) {
            this.nowChat = Object.assign({}, chat)
            console.log('goChat chatId = ' + this.nowChat.id)
            chatApi.getChatRecord(this.nowChat.id).then(
                (res) => {
                    this.chatInfoList = res.data.data
                    this.$nextTick((_) => {
                        addCodeBtn()
                    })
                },
                (err) => {

                }
            )
        },
        handleKeyPress(event) {
            if ((event.which === 13 && event.ctrlKey) || (event.which === 10 && event.ctrlKey)) {
                // 这里实现换行
                console.log(this.nowContent)
            }
        }
    },
    computed: {
        ...mapGetters(['isAuthenticated', 'isAdminRole', 'userInfo']),
    },
    watch: {

    },
}

</script>
<style lang="scss" scoped>
* {
    padding: 0;
    font-family: 微软雅黑;
    margin: 0;
}

#chat {
    display: flex;
}

.active {
    display: block;
    background: #e7f8ff;
    border: #e7f8ff 3px solid;
    padding-top: 10px;
}

.leftcla {
    width: 100%;
    background: #ffffff;
    position: relative;
    display: flex;

}

.li {
    cursor: pointer;
    height: 60px;
    width: 258px;
    border: white 2px solid;
    border-radius: 10px;
    margin: 0 auto;
    margin-top: 20px;
    margin-left: 20px;
    margin-right: 20px;
    background-color: white
}

.li:hover {
    cursor: pointer;
    height: 60px;
    width: 258px;
    border: #066946 2px solid;
    border-radius: 10px;
    margin: 0 auto;
    margin-top: 20px;
    background-color: #f3f3f3
}

.onli {
    cursor: pointer;
    height: 60px;
    width: 258px;
    border: #066946 2px solid;
    border-radius: 10px;
    margin: 0 auto;
    margin-top: 20px;
    margin-left: 20px;
    margin-right: 20px;
    background-color: white
}

.xin {
    font-family: 微软雅黑;
    color: black;
    font-size: 14px;
    margin-top: 6px;
    margin-left: 12px
}

.yitiao {
    font-family: 微软雅黑;
    color: #b2b2b2;
    font-size: 10px;
    margin-top: 6px;
    margin-left: 12px;
}

.time {
    font-family: 微软雅黑;
    color: #b2b2b2;
    font-size: 10px;
    margin-top: 6px;
    margin-left: 12px;
    margin-right: 10px
}

.sz {
    font-family: 微软雅黑;
    color: black;
    font-size: 10px;
    width: 68px;
    height: 38px;
    background-color: white;
    border-radius: 10px;
    text-align: center;
    margin-left: 20px;
    cursor: pointer;
    display: flex;

}

.sz:hover {
    font-family: 微软雅黑;
    color: black;
    font-size: 10px;
    width: 68px;
    height: 38px;
    background-color: #dadada;
    border-radius: 10px;
    text-align: center;
    margin-left: 20px;
    cursor: pointer;
    display: flex;
}

.szlt {
    font-family: 微软雅黑;
    color: black;
    font-size: 10px;
    width: 94px;
    height: 38px;
    background-color: white;
    border-radius: 10px;
    text-align: center;
    margin-right: 20px;
    cursor: pointer;
    display: flex;

}

.szlt:hover {
    font-family: 微软雅黑;
    color: black;
    font-size: 10px;
    width: 94px;
    height: 38px;
    background-color: #dadada;
    border-radius: 10px;
    text-align: center;
    margin-left: 20px;
    cursor: pointer;
    display: flex;
}

.message {

    width: 100%;
    background: #ffffff;



}

.tou {
    font-family: 微软雅黑;
    color: black;
    font-size: 10px;
    width: 68px;
    height: 38px;
    background-color: #ffffff;
    border-radius: 10px;
    text-align: center;
    margin-right: 20px;
    margin-top: 20px;
    cursor: pointer;
    display: flex;
    border: #dadada 1px solid;

}

.tou:hover {
    font-family: 微软雅黑;
    color: black;
    font-size: 10px;
    width: 68px;
    height: 38px;
    background-color: #dadada;
    border-radius: 10px;
    text-align: center;
    margin-left: 20px;
    cursor: pointer;
    display: flex;
}


::-webkit-scrollbar {
    width: 7px;
}

::-webkit-scrollbar-track {
    background-color: #f3f3f3;
}

::-webkit-scrollbar-thumb {
    background-color: #f5f5f5;
}

::-webkit-scrollbar-thumb:hover {
    background-color: #f5f5f5;
}

::-webkit-scrollbar-thumb:active {
    background-color: #f5f5f5;
}

.messagetop {
    width: 100%;
    background-color: #ffffff;
    height: 60px;
    display: flex !important;
    justify-content: space-between !important;
    border-bottom: 1px #ecebeb solid;
}

.messagetoptitle {
    font-family: 微软雅黑;
    font-size: 15px;
    margin-left: 20px;
    // margin-top: 20px
}

.messagetopnum {
    font-family: 微软雅黑;
    color: #b2b2b2;
    font-size: 10px;
    margin-top: 6px;
    margin-left: 12px;
    margin-left: 20px;
}

.messageleftimg {
    margin-top: 20px;
    margin-left: 20px;
    border-radius: 10px;
    width: 30px;
    height: 30px;
    background-color: #effcf6;
    border: #ecebeb 1px solid
}

.messageleftct {
    display: inline-block;
    width: auto;
    height: auto;
    font-size: 14px;
    padding: 10px;
    margin-top: 10px;
    margin-left: 10px;
    border-radius: 10px;
    background-color: #f5f5f5;
    border: #ecebeb 1px solid
}


.messagerightct {
    margin-right: 20px;
    display: inline-block;
    width: auto;
    height: auto;
    font-size: 14px;
    padding: 10px;
    margin-top: 10px;
    margin-left: 10px;
    border-radius: 10px;
    background-color: #f5f5f5;
    border: #ecebeb 1px solid
}

.answer {
    outline-color: #64a8b0;
    height: 80px;
    position: absolute;
    width: auto;
    bottom: 10px;
    right: 20px;
    left: 20px;
    border-radius: 10px;
    padding: 10px;
    border: #dadada 1px solid;
}

.send {
    font-size: 10px;
    color: white;
    padding-top: 10px;
    text-align: center;
    position: absolute;
    cursor: pointer;
    z-index: 100;
    bottom: 35px;
    right: 40px;
    width: 83px;
    height: 39px;
    background-color: #4e838b;
    border: #ecebeb 1px solid;
    border-radius: 10px;
}
</style>
  