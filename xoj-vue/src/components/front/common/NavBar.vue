<template>
    <div>
        <template v-if="!mobileNar">
            <div id="header">
                <el-menu :default-active="activeMenuName" mode="horizontal" router active-text-color="#2196f3"
                    text-color="#495060">
                    <div class="logo">
                        <el-image style="width: 139px; height: 50px" :src="imgUrl" fit="scale-down"
                            @click="goHome"></el-image>
                    </div>
                    <el-menu-item index="/home"><i class="el-icon-s-home"></i>首页</el-menu-item>
                    <el-menu-item index="/problem"><i class="el-icon-s-grid"></i>题目</el-menu-item>
                    <el-menu-item index="/discussion"><i class="el-icon-s-comment"></i>讨论</el-menu-item>
                    <el-menu-item index="/contest"><i class="el-icon-s-data"></i>考核</el-menu-item>
                    <el-menu-item index="/resource"><i class="el-icon-box"></i>资源</el-menu-item>
                    <el-menu-item index="/about"><i class="el-icon-info"></i>关于</el-menu-item>

                    <template v-if="!isAuthenticated">
                        <div class="btn-menu">
                            <el-button type="primary" size="medium" round @click="handleBtnClick('Login')">
                                登录
                            </el-button>
                            <el-button size="medium" round @click="handleBtnClick('Register')" style="margin-left: 5px">
                                注册
                            </el-button>
                        </div>
                    </template>
                    <template v-else>
                        <el-dropdown class="drop-menu" @command="handleRoute" placement="bottom" trigger="hover">
                            <span class="el-dropdown-link">
                                {{ userInfo.username }}<i class="el-icon-caret-bottom"></i>
                            </span>

                            <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item command="/user-home">
                                    我的首页
                                </el-dropdown-item>
                                <el-dropdown-item command="/setting">
                                    我的设置
                                </el-dropdown-item>
                                <el-dropdown-item divided command="/logout">
                                    退出登录
                                </el-dropdown-item>
                            </el-dropdown-menu>
                        </el-dropdown>
                        <avatar :username="userInfo.username" :inline="true" :size="30" color="#FFF" :src="avatar"
                            class="drop-avatar">
                        </avatar>
                        <el-dropdown class="drop-chat" @command="handleRoute" placement="bottom">

                            <span class="el-dropdown-link">
                                <i class="el-icon-chat-line-square">AI</i>
                                <i class="el-icon-caret-bottom"></i>
                            </span>
                            <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item command="/ai-chat">
                                    对话
                                </el-dropdown-item>
                                <!-- <el-dropdown-item command="list">
                                    管理对话
                                </el-dropdown-item> -->
                            </el-dropdown-menu>
                        </el-dropdown>


                        <!-- <el-dropdown class="drop-msg" @command="handleRoute" placement="bottom">

                            <span class="el-dropdown-link">
                                <i class="el-icon-message-solid"></i>
                            </span>
                            <el-dropdown-menu slot="dropdown">
                                <el-dropdown-item command="/user-home">
                                    我的消息
                                </el-dropdown-item>
                                <el-dropdown-item divided command="/logout">
                                    未读消息
                                </el-dropdown-item>
                            </el-dropdown-menu>
                        </el-dropdown> -->
                    </template>
                </el-menu>
            </div>
            <div id="header-hidden">
            </div>
        </template>

        <template v-else>

        </template>

        <el-dialog :visible.sync="modalVisible" width="370px" class="dialog" :title="title" :close-on-click-modal="false">
            <component :is="modalStatus.mode" v-if="modalVisible"></component>
            <div slot="footer" style="display: none"></div>
        </el-dialog>

    </div>
</template>
  
<script>
import Login from '@/components/front/common/Login'
import Register from '@/components/front/common/Register'
import { mapGetters, mapActions } from 'vuex'
import Avatar from 'vue-avatar'
import loginApi from '@/api/login'

export default {
    components: {
        Login,
        Register,
        Avatar
    },
    data() {
        return {
            mode: 'defalut',
            mobileNar: false,
            imgUrl: require('@/assets/logo.png'),
            avatarStyle: 'display: inline-flex;width: 30px;height: 30px;border-radius: 50%;align-items: center;justify-content: center;text-align: center;user-select: none;',
            chatDialogVisible: false,
            command: 'new'
        }
    },
    created() {
        this.page_width()
        window.onresize = () => {
            this.page_width()
            this.setHiddenHeaderHeight()
        }
    },
    mounted() {
        this.page_width()
        this.switchMode()
        this.setHiddenHeaderHeight()
        if (this.isAuthenticated) {
            // this.getUnreadMsgCount()
            // this.msgTimer = setInterval(() => {
            //     this.getUnreadMsgCount()
            // }, 120 * 1000)
        }
    },
    methods: {
        ...mapActions(['changeModalStatus']),
        page_width() {
            let screenWidth = window.screen.width;
            if (screenWidth < 992) {
                this.mobileNar = true
            } else {
                this.mobileNar = false
            }
        },
        setHiddenHeaderHeight() {
            if (!this.mobileNar) {
                try {
                    let headerHeight = document.getElementById('header').offsetHeight;
                    document.getElementById('header-hidden').setAttribute('style', 'height:' + headerHeight + 'px')
                } catch (e) { }
            }
        },
        switchMode() {
            if (this.$route.meta.fullScreenSource) {
                this.mode = this.$route.meta.fullScreenSource
            } else {
                this.mode = 'defalut'
            }
        },
        handleBtnClick(mode) {
            this.changeModalStatus({
                mode,
                visible: true,
            });
        },
        handleRoute(route) {
            //电脑端导航栏路由跳转事件
            if (route && route.split('/')[1] != 'admin') {
                this.$router.push(route);
            } else {
                window.open('/admin/');
            }
        },
        goHome() {
            this.$router.push({
                name: "Home"
            })
        }
    },
    computed: {
        ...mapGetters([
            'modalStatus',
            'userInfo',
            'isAuthenticated',
            'isAdminRole',
            'token'
        ]),
        avatar() {
            return this.$store.getters.userInfo.avatar
        },
        activeMenuName() {
            return '/' + this.$route.path.split('/')[1]
        },
        modalVisible: {
            get() {
                return this.modalStatus.visible;
            },
            set(value) {
                this.changeModalStatus({ visible: value });
            },
        },
        title: {
            get() {
                let ojName = "XOJ"
                if (this.modalStatus.mode == 'ResetPwd') {
                    return '重置密码' + ' - ' + ojName;
                } else {
                    if (this.modalStatus.mode === 'Login') {
                        return (
                            '登录' + ' - ' + ojName
                        );
                    } else {
                        return (
                            '注册' + ' - ' + ojName
                        )
                    }
                }
            },
        },
    },
    watch: {
        isAuthenticated() {
            // if (this.isAuthenticated) {
            //     if (this.msgTimer) {
            //         clearInterval(this.msgTimer);
            //     }
            //     this.getUnreadMsgCount();
            //     this.msgTimer = setInterval(() => {
            //         this.getUnreadMsgCount();
            //     }, 120 * 1000);
            // } else {
            //     clearInterval(this.msgTimer);
            // }
        },
        $route() {
            this.switchMode()
        }
    },
}

</script>
<style lang="scss" scoped>
#header {
    min-width: 300px;
    position: fixed;
    top: 0;
    left: 0;
    height: auto;
    width: 100%;
    z-index: 2000;
    background-color: #fff;
    box-shadow: 0 1px 5px 0 rgba(0, 0, 0, 0.1);
}

.logo {
    cursor: pointer;
    margin-left: 2%;
    margin-right: 2%;
    float: left;
    width: 139px;
    height: 42px;
    margin-top: 5px;
}

.btn-menu {
    font-size: 16px;
    float: right;
    margin-right: 20px;
    margin-top: 10px;
}

.el-dropdown-link {
    cursor: pointer;
    color: #409eff !important;
}

.drop-menu {
    float: right;
    margin-right: 30px;
    position: relative;
    font-weight: 500;
    right: 10px;
    margin-top: 18px;
    font-size: 18px;
}


.drop-avatar {
    float: right;
    margin-right: 15px;
    position: relative;
    margin-top: 16px;
}

.drop-chat {
    float: right;
    font-size: 20px;
    margin-right: 20px;
    position: relative;
    margin-top: 16px;
}

.drop-msg {
    float: right;
    font-size: 25px;
    margin-right: 10px;
    position: relative;
    margin-top: 13px;
}

.el-submenu__title i {
    color: #495060 !important;
}

::v-deep .el-dialog {
    border-radius: 10px !important;
    text-align: center;
}

::v-deep .el-dialog__header .el-dialog__title {
    font-size: 22px;
    font-weight: 600;
    font-family: Arial, Helvetica, sans-serif;
    line-height: 1em;
    color: #4e4e4e;
}
</style>
  