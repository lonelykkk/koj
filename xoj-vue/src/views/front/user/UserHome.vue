<template>
    <div class="container" v-loading="loading">
        <div class="avatar-container">
            <avatar :username="profile.username" :inline="true" :size="130" color="#FFF" :src="profile.avatar"></avatar>
        </div>
        <el-card>
            <div class="user-info">
                <p>
                    <span class="emphasis">
                        <i class="fa fa-user-circle-o" aria-hidden="true"></i>
                        {{ profile.username }}
                    </span>
                    <span class="gender-male male" v-if="profile.sex == '2'">
                        <i class="fa fa-mars"></i>
                    </span>
                    <span class="gender-male female" v-else-if="profile.sex == '1'">
                        <i class="fa fa-venus"></i>
                    </span>
                </p>
                <p v-if="profile.nickname">
                    <span>
                        <el-tag effect="plain" size="small" :type="nicknameColor(profile.nickname)">
                            {{ profile.nickname }}
                        </el-tag>
                    </span>
                </p>
                <hr id="split" />
                <el-tabs type="card" style="margin-top:1rem;" @tab-click="handleClick">
                    <el-tab-pane label="个人简介">
                        <div class="signature-body">
                            <Markdown v-if="profile.sign" :isAvoidXss="true" :content="profile.sign">
                            </Markdown>
                            <div class="markdown-body" v-else>
                                <p>这个家伙很懒，什么也没写…</p>
                            </div>
                        </div>
                    </el-tab-pane>
                    <el-tab-pane label="做题情况">
                        <div id="problems">
                            <template>
                                <div>
                                    <el-empty description="暂无数据"></el-empty>
                                </div>
                            </template>
                        </div>
                    </el-tab-pane>
                    <el-tab-pane label="考核情况">
                        <div id="contests">
                            <template v-if="!contestInfoList.length">
                                <p>暂无数据</p>
                            </template>
                            <template v-else>
                                <vxe-table :loading="loading" ref="xTable" :data="contestInfoList" auto-resize stripe
                                    align="center">
                                    <vxe-table-column field="title" min-width="100" title="标题" show-overflow>
                                    </vxe-table-column>
                                    <vxe-table-column title="类型" width="100">
                                        <template v-slot="{ row }">
                                            <el-tag type="gray">{{ row.type | parseContestType }}</el-tag>
                                        </template>
                                    </vxe-table-column>
                                    <vxe-table-column min-width="150" title="开始时间">
                                        <template v-slot="{ row }">
                                            <p>{{ row.startTime | localtime }}</p>
                                        </template>
                                    </vxe-table-column>
                                    <vxe-table-column min-width="150" title="结束时间">
                                        <template v-slot="{ row }">
                                            <p>{{ row.endTime | localtime }}</p>
                                        </template>
                                    </vxe-table-column>
                                    <vxe-table-column title="状态" width="100">
                                        <template v-slot="{ row }">
                                            <el-tag effect="dark" :color="CONTEST_STATUS_REVERSE[row.status].color"
                                                size="medium">
                                                {{ CONTEST_STATUS_REVERSE[row.status].name }}
                                            </el-tag>
                                        </template>
                                    </vxe-table-column>
                                    <vxe-table-column min-width="180" title="操作">
                                        <template v-slot="{ row }">
                                            <template>
                                                <div style="margin-bottom:10px">
                                                    <template v-if="row.isCorrection">
                                                        <el-tooltip effect="dark" content="查看评价" placement="top">
                                                            <el-button icon="el-icon-edit" size="mini"
                                                                @click.native="getCorrectionInfo(row)" type="primary">
                                                            </el-button>
                                                        </el-tooltip>
                                                    </template>
                                                    <el-tooltip effect="dark" content="查看考核" placement="top">
                                                        <el-button icon="el-icon-tickets" size="mini"
                                                            @click.native="goContest(row)" type="success">
                                                        </el-button>
                                                    </el-tooltip>
                                                </div>
                                            </template>
                                        </template>
                                    </vxe-table-column>
                                </vxe-table>
                                <div class="panel-options">
                                    <el-pagination class="page" layout="prev, pager, next" @current-change="currentChange"
                                        :page-size="limit" :current-page.sync="currentPage" :total="contestTotal">
                                    </el-pagination>
                                </div>
                            </template>
                        </div>
                    </el-tab-pane>
                </el-tabs>
            </div>
        </el-card>
        <el-dialog title="您的本次考核评价" :visible.sync="showDialog" :fullscreen="true" center>
            <Markdown v-if="contestCorrection.content" :isAvoidXss="true" :content="contestCorrection.content">
            </Markdown>
            <span slot="footer" class="dialog-footer">
                <el-button type="primary" @click.native="showDialog = false">
                    确定
                </el-button>
            </span>
        </el-dialog>
    </div>
</template>
<script>
import { mapActions } from 'vuex'
import { PROBLEM_LEVEL } from '@/common/constants'
import { addCodeBtn } from '@/utils/codeblock'
import userinfoApi from '@/api/userinfo'
import myMessage from '@/utils/message'
import Avatar from 'vue-avatar'
import utils from '@/utils/utils'
import Markdown from '@/components/front/common/Markdown'

import {
    CONTEST_STATUS_REVERSE,
    CONTEST_TYPE_REVERSE,
} from '@/common/constants'
export default {
    components: {
        Avatar,
        Markdown
    },
    data() {
        return {
            profile: {
                username: '',
                nickname: '',
                sex: '',
                avatar: '',
                sign: '',
            },
            currentPage: 1,
            limit: 10,
            contestInfoList: [],
            contestTotal: 0,
            loading: false,
            contestLoading: false,
            PROBLEM_LEVEL: {},
            showDialog: false,
            contestCorrection: {}
        }
    },
    created() {
        this.PROBLEM_LEVEL = Object.assign({}, PROBLEM_LEVEL)
    },
    mounted() {
        this.init()
    },
    methods: {
        init() {
            this.CONTEST_TYPE_REVERSE = Object.assign({}, CONTEST_TYPE_REVERSE)
            this.CONTEST_STATUS_REVERSE = Object.assign({}, CONTEST_STATUS_REVERSE)
            this.loading = true
            userinfoApi.getUserInfo().then(
                (res) => {
                    this.profile = res.data.data
                    this.loading = false
                },
                (_) => {
                    this.loading = false
                }
            );
        },
        handleClick(tab, event) {
            if (tab.index == 1) {
                this.getUserProblemRecord()
            } else if (tab.index == 2) {
                this.getUserContestRecord(1)
            }
        },
        getUserProblemRecord() {
            console.log('getUserProblemRecord')
        },
        getUserContestRecord(page) {
            console.log('getUserContestRecord')
            this.contestLoading = true
            userinfoApi.getContestList(this.limit, page).then(
                (res) => {
                    this.contestTotal = res.data.data.total
                    this.contestInfoList = res.data.data.records
                    this.contestLoading = false
                },
                (err) => {
                    this.contestLoading = false
                }
            )
        },
        getCorrectionInfo(contestInfo) {
            this.contestCorrection = contestInfo.contestCorrection
            this.$nextTick((_) => {
                addCodeBtn()
            })
            this.showDialog = true
        },
        goContest(contestInfo) {
            console.log('goContest')
            this.$router.push({
                name: "ContestDetail",
                params: { contestID: contestInfo.id },
            });
        },
        currentChange(page) {
            this.currentPage = page
            this.getUserContestRecord(page)
        },
        nicknameColor(nickname) {
            let typeArr = ['', 'success', 'info', 'danger', 'warning']
            let index = nickname.length % 5
            return typeArr[index]
        }
    },
    watch: {
        $route(newVal, oldVal) {
            if (newVal !== oldVal) {
                this.init()
            }
        }
    },
};
</script>
  
<style lang="scss" scoped>
.container p {
    margin-top: 8px;
    margin-bottom: 8px;
}

.container {
    padding: 0 3%;
}

@media screen and (max-width: 1080px) {
    .container {
        position: relative;
        width: 100%;
        margin-top: 110px;
        text-align: center;
    }

    .container .avatar-container {
        position: absolute;
        left: 50%;
        transform: translate(-50%);
        z-index: 1;
        margin-top: -90px;
    }
}

@media screen and (min-width: 1080px) {
    .container {
        position: relative;
        width: 100%;
        margin-top: 160px;
        text-align: center;
    }

    .container .avatar-container {
        position: absolute;
        left: 50%;
        transform: translate(-50%);
        z-index: 1;
        margin-top: -8%;
    }

    .container .user-info {
        margin-top: 50px;
    }
}

.container .avatar {
    width: 140px;
    height: 140px;
    border-radius: 50%;
    box-shadow: 0 1px 1px 0;
}

.container .emphasis {
    font-size: 20px;
    font-weight: 600;
}

.signature-body {
    background: #fff;
    overflow: hidden;
    width: 100%;
    padding: 10px 10px;
    text-align: left;
    font-size: 14px;
    line-height: 1.6;
}

.gender-male {
    font-size: 16px;
    margin-left: 5px;
    color: white;
    border-radius: 4px;
    padding: 2px;
}

.male {
    background-color: #409eff;
}

.female {
    background-color: pink;
}
</style>
  