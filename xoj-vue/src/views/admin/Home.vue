<template>
    <div class="admin-container">
        <div v-if="!mobileNar">
            <el-container>
                <el-aside width="200px">
                    <div class="logo">
                        <img :src="imgUrl" />
                    </div>
                    <el-menu default-active="/admin/" class="el-menu-vertical-demo" :router="true" @open="handleOpen"
                        @close="handleClose" background-color="#FFF">
                        <el-menu-item index="/admin/">
                            <i class="el-icon-menu fa-size" aria-hidden="true"></i>
                            仪表盘
                        </el-menu-item>
                        <el-submenu index="general">
                            <template slot="title">
                                <i class="el-icon-menu fa-size"></i>
                                <span slot="title">常用设置</span>
                            </template>
                            <el-menu-item index="/admin/user">
                                <i class="el-icon-menu fa-size"></i>
                                <span slot="title">用户管理</span>
                            </el-menu-item>
                            <el-menu-item index="/admin/announcement">
                                <i class="el-icon-menu fa-size"></i>
                                <span slot="title">通告管理</span>
                            </el-menu-item>
                            <el-menu-item index="/admin/studio-info">
                                <i class="el-icon-menu fa-size"></i>
                                <span slot="title">成员信息</span>
                            </el-menu-item>
                            <el-menu-item index="/admin/studio-award">
                                <i class="el-icon-menu fa-size"></i>
                                <span slot="title">奖项管理</span>
                            </el-menu-item>
                        </el-submenu>
                        <el-submenu index="problem">
                            <template slot="title">
                                <i class="el-icon-document fa-size"></i>
                                <span slot="title">问题管理</span>
                            </template>
                            <el-menu-item index="/admin/problem">
                                题目列表
                            </el-menu-item>
                            <el-menu-item index="/admin/problem/create">
                                题目详情
                            </el-menu-item>
                            <el-menu-item index="/admin/problem/tag">
                                题目标签
                            </el-menu-item>
                        </el-submenu>
                        <el-submenu index="discussion">
                            <template slot="title">
                                <i class="el-icon-document"></i>
                                <span slot="title">贴子管理</span>
                            </template>
                            <el-menu-item index="/admin/discussion">
                                贴子管理
                            </el-menu-item>
                            <el-menu-item index="/admin/discussion/category">
                                贴子分类
                            </el-menu-item>
                        </el-submenu>
                        <el-submenu index="contest">
                            <template slot="title">
                                <i class="el-icon-document"></i>
                                <span slot="title">考核管理</span>
                            </template>
                            <el-menu-item index="/admin/contest">
                                考核列表
                            </el-menu-item>
                            <el-menu-item index="/admin/contest/create">
                                创建考核
                            </el-menu-item>
                        </el-submenu>
                        <el-submenu index="resource">
                            <template slot="title">
                                <i class="el-icon-document"></i>
                                <span slot="title">资源管理</span>
                            </template>
                            <el-menu-item index="/admin/resource">
                                资源列表
                            </el-menu-item>
                            <el-menu-item index="/admin/resource/tag">
                                资源分类
                            </el-menu-item>
                        </el-submenu>
                    </el-menu>
                </el-aside>
                <el-container>
                    <el-header>
                        <el-row>
                            <el-col :span="20">
                                <el-breadcrumb separator-class="el-icon-arrow-right">
                                    <div class="breadcrumb-container">
                                        <el-breadcrumb-item :to="{ path: '/admin/' }">首页</el-breadcrumb-item>
                                        <el-breadcrumb-item v-for="item in routeList" :key="item.index">
                                            {{ item.meta.title }}
                                        </el-breadcrumb-item>
                                    </div>
                                </el-breadcrumb>
                            </el-col>

                            <el-col :span="4" v-show="isAuthenticated">
                                <div class="user-info">
                                    <avatar :username="userInfo.username" :inline="true" :size="30" color="#FFF"
                                        :src="userInfo.avatar" class="drop-avatar">
                                    </avatar>

                                    <el-dropdown @command="handleCommand" style="vertical-align: middle;">
                                        <span class="el-dropdown-link">
                                            {{ userInfo.username }}
                                            <i class="el-icon-caret-bottom el-icon--right"></i>
                                        </span>
                                        <el-dropdown-menu slot="dropdown">
                                            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
                                        </el-dropdown-menu>
                                    </el-dropdown>
                                </div>
                            </el-col>

                        </el-row>
                    </el-header>
                    <el-main>
                        <router-view></router-view>
                    </el-main>
                    <el-footer>Footer</el-footer>
                </el-container>
            </el-container>
        </div>

        <div v-else>

        </div>
    </div>
</template>

<script>
import { mapGetters } from 'vuex'
import api from '@/api/login'
import mMessage from '@/utils/message'
import Avatar from 'vue-avatar'

export default {
    components: {
        Avatar,
    },
    data() {
        return {
            mobileNar: false,
            currentPath: '',
            routeList: [],
            imgUrl: require('@/assets/backstage.png'),
            routeList: []
        }
    },
    mounted() {
        this.currentPath = this.$route.path;
        this.getBreadcrumb();
        window.onresize = () => {
            this.page_width();
        };
        this.page_width();
    },
    methods: {
        init() {
            this.getBreadcrumb();
        },
        handleCommand(command) {
            if (command === 'logout') {
                api.adminLogout().then(
                    (res) => {
                        this.$router.push({ path: '/admin/login' })
                        mMessage.success('登出成功')
                        this.$store.commit('clearUserInfoAndToken')
                    }
                );
            }
        },
        page_width() {
            let screenWidth = window.screen.width;
            if (screenWidth < 992) {
                this.mobileNar = true;
            } else {
                this.mobileNar = false;
            }
        },
        getBreadcrumb() {
            let matched = this.$route.matched.filter((item) => item.meta.title); // 获取路由信息，并过滤保留路由标题信息存入数组
            this.routeList = matched;
        },
        handleOpen(key, keyPath) {
            console.log(key, keyPath);
        },
        handleClose(key, keyPath) {
            console.log(key, keyPath);
        }
    },
    computed: {
        ...mapGetters([
            'userInfo',
            'isSuperAdmin',
            'isAuthenticated'
        ]),
        'window.screen.width'(newVal, oldVal) {
            if (newVal < 992) {
                this.mobileNar = true;
            } else {
                this.mobileNar = false;
            }
        },
    },
    watch: {
        $route() {
            this.getBreadcrumb(); // 监听路由变化
        }
    }
}

</script>

<style lang="scss" scoped>
.admin-container {

    overflow: auto;
    font-weight: 400;
    height: 100%;
    -webkit-font-smoothing: antialiased;
    background-color: #eff3f5;
    overflow-y: auto;

    .el-container {
        .el-container {
            margin-left: 202px;
        }
    }
}

.el-aside {
    overflow: auto;
    width: 15%;
    height: 100%;
    position: fixed !important;
    z-index: 100;
    top: 0;
    bottom: 0;
    left: 0;

    .logo {
        text-align: center;
        cursor: pointer;

        img {
            background-color: #fff;
            border: 3px solid #fff;
            width: 180;
            height: 180px;
        }
    }

    background-color: #FFF;
}

.fa-size {
    text-align: center;
    font-size: 18px;
    vertical-align: middle;
    margin-right: 5px;
    width: 24px;
}

* {
    box-sizing: border-box;
}

a:active,
a:hover {
    outline-width: 0;
}

a {
    background-color: transparent;
}

img {
    border-style: none;
}


.el-header {
    width: 100%;
    line-height: 60px;
    background-color: #FFF;

    .breadcrumb-container {
        padding: 20px;
        background-color: #fff;
        // float: left;
    }

    .user-info {
        float: right;
        margin-right: 20px;
    }

    .drop-avatar {
        vertical-align: middle;
        margin-right: 10px;
    }
}

.el-main {
    padding: 20px;
    width: 100%;
}

.el-footer {
    text-align: center;
    background-color: #FFF;
    width: 100%;
}
</style>