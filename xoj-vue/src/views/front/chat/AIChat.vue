<template>
    <div class="box">
        <div>
            <el-row>
                <el-col :span="5">
                    <!-- 侧边栏 -->
                    <el-card shadow="never">
                        <div style="display: flex; justify-content: center">
                            <el-button style="width: 80%" @click="dialogVisible = true">新建对话</el-button>
                        </div>
                        <div class="custom-scroll" style="height: 435px; overflow-y: auto; margin-top: 15px">
                            <div v-for="chat in chatList" :key="chat.id"
                                style="margin-top: 10px; display: flex; justify-content: center">
                                <div :class="['btn-style', 'function', { selected: chat.id === nowChat.id },]">
                                    <div class="container">
                                        <svg class="icon" aria-hidden="true">
                                            <use xlink:href="#icon-aiduihua"></use>
                                        </svg>
                                        <span class="textCenter" @click="selectChat(chat)">
                                            {{ chat.title }}
                                        </span>
                                        <!-- <div style="display: flex; justify-content: space-between">
                                            <div class="yitiao">{{ chat.chatCount }}条对话</div>
                                        </div> -->
                                        <svg class="icon" aria-hidden="true" @click="deleteChat(chat.id)">
                                            <use xlink:href="#icon-cangpeitubiao_shanchu"></use>
                                        </svg>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div style="width: 100%; height: 30px">
                            <el-row>
                                <el-col :span="8">
                                    <img class="avatar" :src="userInfo.avatar" alt="Avatar" /></el-col>
                                <el-col :span="8">
                                    <span>{{ userInfo.username }}</span>
                                </el-col>
                            </el-row>
                        </div>
                    </el-card>
                </el-col>
                <!-- 对话页面 -->
                <el-col :span="19">
                    <div class="chat-bot">
                        <el-card ref="chatContainer" class="chat-window" shadow="never">
                            <welcome @quick="quickstart" v-if="this.chatList.length === 0"></welcome>
                            <div v-for="chatInfo in chatInfoList" :key="chatInfo.id">
                                <div class="message user-message message">
                                    <div class="message-content" style="backgroundColor:rgb(123, 229, 155);overflow:hidden">
                                        <Markdown :content="chatInfo.question">
                                        </Markdown>
                                    </div>
                                    <img class="avatar-right" :src="userInfo.avatar" alt="Avatar" />
                                </div>

                                <div class="message bot-message">
                                    <img class="avatar-left" :src="require(`@/assets/AI.jpg`)" alt="Avatar" />

                                    <div v-if="chatInfo.answer" class="message-content"
                                        style="backgroundColor:rgb(240, 241, 242);overflow:hidden">
                                        <Markdown :content="chatInfo.answer" style="padding-left: 5px;">
                                        </Markdown>
                                    </div>
                                    <div v-else class="loading-effect">
                                        <span class="dot dot-1"></span>
                                        <span class="dot dot-2"></span>
                                        <span class="dot dot-3"></span>
                                        <span class="dot dot-4"></span>
                                        <span class="dot dot-5"></span>
                                        <span class="dot dot-6"></span>
                                    </div>
                                </div>
                            </div>
                        </el-card>

                        <div class="input-area">
                            <el-input type="textarea" class="input-field" v-model="nowContent"
                                placeholder="请输入消息...(Shift+Enter 换行, Enter发送消息)" clearable
                                @keydown.enter.native.prevent="handleKeyDown" />
                            <el-button class="send-button" type="primary" @click="send">发送</el-button>
                        </div>
                    </div>
                </el-col>
            </el-row>
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
import Markdown from '@/components/front/common/Markdown'
import { addCodeBtn } from '@/utils/codeblock'
import iconfont from "@/assets/js/iconfont"
import welcome from "@/views/front/chat/Hello";
export default {
    name: 'AIChat',
    components: {
        Markdown,
        welcome
    },
    data() {
        return {
            dialogVisible: false,
            newChatTitle: '',
            chatList: [],
            nowChat: null,
            nowContent: '',
            chatInfoList: [],
            loading: false,
        }
    },
    mounted() {
        this.getChatList()
    },
    methods: {
        getChatList() {
            chatApi.getChatList().then(
                (res) => {
                    this.chatList = res.data.data
                    if (this.chatList.length > 0) {
                        if (this.nowChat === null) {
                            this.nowChat = this.chatList[0]
                        }
                        this.selectChat(this.nowChat)
                    }
                }
            )
        },
        addChat() {
            console.log('add chat')
            chatApi.addChat(this.newChatTitle).then(
                (res) => {
                    this.nowContent = null
                    this.getChatList()
                    this.dialogVisible = false
                },
                (err) => {
                    this.dialogVisible = false
                }
            )
        },
        selectChat(chat) {
            this.nowChat = chat
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
        deleteChat(chatId) {
            console.log('delete chat')
            this.$confirm('确认删除？', 'Tips', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }).then(
                () => {
                    chatApi.deleteChat(chatId).then(
                        (res) => {
                            myMessage.success("删除成功！")
                            this.nowChat = null
                            this.getChatList()
                        },
                        (err) => {
                            myMessage.error("删除失败！")
                        }
                    )
                },
                () => { }
            );
        },
        quickstart(text) {
            this.nowContent = text;
            // this.send();
            console.log(this.nowContent)
        },
        send() {
            if (this.chatList.length === 0) {
                myMessage.warning('请先创建对话')
                return
            }
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
        handleKeyDown(event) {
            if (event.key === "Enter" && event.shiftKey) {
                this.nowContent += "\n"; // 在输入框中添加换行符
            } else if (event.key === "Enter") {
                this.send();
                event.preventDefault(); // 阻止默认的换行行为
            }
            this.$nextTick(() => {
                // 获取滚动容器元素
                const chatContainer = this.$refs.chatContainer;
                // 将滚动条位置设置为最底部
                chatContainer.scrollTop = chatContainer.scrollHeight;
            });
            // this.scrollToBottom(); // 滚动到底部
        },
        // 滚动到最底部
        scrollToBottom() {
            this.$nextTick(() => {
                const inputField = this.$refs.inputField.$refs.input.$refs.textarea.$el;
                inputField.scrollTop = inputField.scrollHeight;
            });
        },
    },
    computed: {
        ...mapGetters(['isAuthenticated', 'isAdminRole', 'userInfo']),
    },
}


</script>
<style lang="scss" scoped>
.box {
    margin: 0 4%;
}

.icon {
    width: 1em;
    height: 1em;
    vertical-align: -0.15em;
    fill: currentColor;
    overflow: hidden;
    flex-shrink: 0;
}

.container {
    display: flex;
    align-items: center;
}

.textCenter {
    flex-grow: 1;
    text-align: center;
}

.btn-style {
    width: 80%;
    padding: 10px;
    background-color: #ffffff;
    color: rgb(66, 66, 66);
    border: 1px solid #eae9e9;
    border-radius: 4px;
    cursor: pointer;
}

.btn-style.selected {
    width: 80%;
    padding: 10px;
    background-color: #fafffa;
    color: rgb(0, 128, 28);
    border: 1px solid #2d8a04;
    border-radius: 4px;
    cursor: pointer;
}

.text-center {
    display: flex;
    justify-content: center;
    align-items: center;
    font-family: 宋体, SimSun, STSong, Songti SC, serif;
}

.function:hover {
    background-color: rgb(236, 245, 255);
    color: rgb(64, 158, 255);
}

.custom-scroll::-webkit-scrollbar {
    width: 4px;
    background-color: #f5f5f5;
    opacity: 0;
    /* 默认隐藏滚动条 */
}

.custom-scroll:hover::-webkit-scrollbar {
    opacity: 1;
    /* 鼠标悬停时显示滚动条 */
}

.custom-scroll::-webkit-scrollbar-thumb {
    border-radius: 4px;
    background-color: #c0c0c0;
}

.custom-scroll::-webkit-scrollbar-thumb:hover {
    background-color: #a9a9a9;
}

.custom-scroll::-webkit-scrollbar-track {
    background-color: #f5f5f5;
}

.chat-bot {
    width: 90%;
    margin: 0 auto;
    margin-left: 0;
}

.chat-window {
    height: 510px;
    padding: 10px;
    overflow-y: auto;
}

.message {
    display: flex;
    align-items: flex-start;
}

.user-message {
    justify-content: flex-end;
}

.bot-message {
    justify-content: flex-start;
}

.avatar {
    width: 42px;
    height: 42px;
    margin-right: 8px;
    border-radius: 50%;
    margin-top: 15px;
}

.avatar-left {
    width: 32px;
    height: 32px;
    margin-right: 8px;
    border-radius: 50%;
}

.avatar-right {
    width: 32px;
    height: 32px;
    margin-left: 8px;
    border-radius: 50%;
}

.message-content {
    max-width: 70%;
    padding: 8px;
    border-radius: 8px;
    word-break: break-word;
    margin-top: 10px;
}

.message-content>>>p {
    margin-top: 0;
    margin-bottom: 0;
}

.input-area {
    margin-top: 10px;
    display: flex;
    align-items: center;
}

.input-field {
    flex: 1px;
    resize: none;
    max-height: 60px;
    /* 设置输入框的最大高度 */
}

.send-button {
    margin-left: 10px;
}

.loading-effect {
    display: flex;
    align-items: center;
}

.dot {
    display: inline-block;
    width: 8px;
    height: 8px;
    margin: 0 2px;
    border-radius: 50%;
    background-color: #000;
    animation: dotAnimation 1s infinite linear;
}

@keyframes dotAnimation {

    0%,
    80%,
    100% {
        opacity: 0.5;
    }

    40% {
        opacity: 1;
    }
}

.dot-1 {
    animation-delay: 0s;
    background-color: red;
}

.dot-2 {
    animation-delay: 0.2s;
    background-color: rgb(10, 116, 255);
}

.dot-3 {
    animation-delay: 0.4s;
    background-color: rgb(10, 209, 33);
}

.dot-4 {
    animation-delay: 0.1s;
    background-color: rgb(186, 171, 6);
}

.dot-5 {
    animation-delay: 0.3s;
    background-color: rgb(248, 41, 227);
}

.dot-6 {
    animation-delay: 0.5s;
    background-color: rgb(95, 9, 243);
}

.el-textarea__inner {
    overflow-y: auto !important;
    /* 设置为自动滚动 */
}
</style>
  