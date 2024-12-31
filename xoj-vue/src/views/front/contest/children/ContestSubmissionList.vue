<template>
    <el-row class="flex-container">
        <div id="main">
            <el-card shadow>
                <div slot="header">
                    <el-row :gutter="18">
                        <el-col :md="4" :lg="2">
                            <span class="panel-title hidden-md-and-down">
                                状态
                            </span>
                        </el-col>
                        <el-col :xs="10" :sm="8" :md="4" :lg="4">
                            <el-switch style="display: block" v-model="formFilter.onlyMine" active-text="我的" :width="40"
                                @change="handleOnlyMine" inactive-text="全部">
                            </el-switch>
                        </el-col>
                    </el-row>
                </div>
                <div class="no-submission" v-if="!submissions.length">
                    <el-empty description="暂无提交"></el-empty>
                </div>
                <div v-else>
                    <vxe-table border="inner" stripe keep-source ref="xTable" highlight-current-row highlight-hover-row
                        align="center" auto-resize :row-class-name="tableRowClassName" :data="submissions"
                        :loading="loadingTable">
                        <vxe-table-column field="submitId" title="Run ID" width="80"></vxe-table-column>
                        <vxe-table-column field="pid" title="题目" min-width="150" show-overflow>
                            <template v-slot="{ row }">
                                <span v-if="contestID" @click="getProblemUri(row.pid)" style="color: rgb(87, 163, 243)">
                                    {{ row.displayId + ': ' + row.title }}
                                </span>
                            </template>
                        </vxe-table-column>
                        <vxe-table-column field="status" title="状态" min-width="200">
                            <template v-slot="{ row }">
                                <span :class="getStatusColor(row.status)" slot="reference">
                                    <i class="el-icon-loading" v-if="row.status == JUDGE_STATUS_RESERVE['Pending'] ||
                                        row.status == JUDGE_STATUS_RESERVE['Compiling'] ||
                                        row.status == JUDGE_STATUS_RESERVE['Judging']
                                        ">
                                    </i>
                                    {{ JUDGE_STATUS[row.status].name }}
                                </span>
                            </template>
                        </vxe-table-column>
                        <vxe-table-column field="score" :title="$t('m.Score')" width="64" v-if="scoreColumnVisible">
                            <template v-slot="{ row }">
                                <template v-if="contestID && row.score != null">
                                    <el-tag effect="plain" size="medium" :type="JUDGE_STATUS[row.status]['type']">
                                        {{ row.score }}
                                    </el-tag>
                                </template>
                                <template v-else-if="row.status == JUDGE_STATUS_RESERVE['Pending'] ||
                                    row.status == JUDGE_STATUS_RESERVE['Compiling'] ||
                                    row.status == JUDGE_STATUS_RESERVE['Judging']
                                    ">
                                    <el-tag effect="plain" size="medium" :type="JUDGE_STATUS[row.status]['type']">
                                        <i class="el-icon-loading"></i>
                                    </el-tag>
                                </template>
                                <template v-else>
                                    <el-tag effect="plain" size="medium"
                                        :type="JUDGE_STATUS[row.status]['type']">--</el-tag>
                                </template>
                            </template>
                        </vxe-table-column>
                        <vxe-table-column field="time" title="运行时间" min-width="96">
                            <template v-slot="{ row }">
                                <span>{{ submissionTimeFormat(row.time) }}</span>
                            </template>
                        </vxe-table-column>
                        <vxe-table-column field="memory" title="运行内存" min-width="96">
                            <template v-slot="{ row }">
                                <span>{{ submissionMemoryFormat(row.memory) }}</span>
                            </template>
                        </vxe-table-column>

                        <vxe-table-column field="length" title="代码长度" min-width="80">
                            <template v-slot="{ row }">
                                <span>{{ submissionLengthFormat(row.length) }}</span>
                            </template>
                        </vxe-table-column>

                        <vxe-table-column field="language" title="语言" show-overflow min-width="96">
                            <template v-slot="{ row }">
                                <el-tooltip class="item" effect="dark" content="查看提交详情" placement="top">
                                    <span @click="showSubmitDetail(row)"
                                        style="color: rgb(87, 163, 243); font-size: .8125rem;">{{ row.language }}
                                    </span>
                                </el-tooltip>
                            </template>
                        </vxe-table-column>
                        <vxe-table-column field="judger" title="判题源" min-width="96" show-overflow>
                            <template v-slot="{ row }">
                                <span v-if="row.judger">{{ row.judger }}</span>
                                <span v-else>--</span>
                            </template>
                        </vxe-table-column>
                        <vxe-table-column field="username" title="用户" min-width="96" show-overflow>
                            <template v-slot="{ row }">
                                <a @click="goUserHome(row.username, row.uid)" style="color: rgb(87, 163, 243)">
                                    {{ row.username }}
                                </a>
                            </template>
                        </vxe-table-column>
                        <vxe-table-column field="submitTime" title="提交时间" min-width="96">
                            <template v-slot="{ row }">
                                <span>
                                    <el-tooltip :content="row.submitTime | localtime" placement="top">
                                        <span>{{ row.submitTime | fromNow }}</span>
                                    </el-tooltip>
                                </span>
                            </template>
                        </vxe-table-column>
                    </vxe-table>
                    <Pagination :total="total" :page-size="limit" @on-change="getSubmissions" :current.sync="currentPage"
                        @on-page-size-change="onPageSizeChange" :layout="'prev, pager, next, sizes'"></Pagination>
                </div>
            </el-card>
        </div>
    </el-row>
</template>
  
<script>
import myMessage from '@/utils/message'
import contestApi from '@/api/contest'
import utils from '@/utils/utils'
import time from '@/common/time'
import { mapActions, mapGetters } from "vuex"
import {
    JUDGE_STATUS,
    JUDGE_STATUS_RESERVE,
    CONTEST_STATUS,
    RULE_TYPE,
} from "@/common/constants"
import Pagination from '@/components/front/common/Pagination'

export default {
    name: 'ContestSubmissionList',
    components: {
        Pagination
    },
    data() {
        return {
            formFilter: {
                onlyMine: false
            },
            loadingTable: false,
            submissions: [],
            total: 0,
            limit: 15,
            currentPage: 1,
            contestID: null,
            routeName: "",
            JUDGE_STATUS_RESERVE: {},
            CONTEST_STATUS: {},
            RULE_TYPE: {},

        }
    },
    created() {
        this.init()
    },
    mounted() {
        this.JUDGE_STATUS = Object.assign({}, JUDGE_STATUS);
        this.JUDGE_STATUS_RESERVE = Object.assign({}, JUDGE_STATUS_RESERVE);
        this.CONTEST_STATUS = Object.assign({}, CONTEST_STATUS);
        this.RULE_TYPE = Object.assign({}, RULE_TYPE);
    },
    methods: {
        init() {
            this.contestID = this.$route.params.contestID
            this.getSubmissions()
        },
        getSubmissions() {
            if (this.formFilter.onlyMine && !this.isAuthenticated) {
                // 需要判断是否为登陆状态
                this.formFilter.onlyMine = false
                myMessage.error('请先登录！')
                return
            }

            let query = {
                limit: this.limit,
                currentPage: this.currentPage,
                onlyMine: this.formFilter.onlyMine,
                contestID: this.contestID
            }
            this.loadingTable = true
            contestApi.getContestSubmissionList(query).then(
                (res) => {
                    this.submissions = res.data.data.records
                    this.total = res.data.data.total
                    this.currentPage = res.data.data.pages
                    this.loadingTable = false
                },
                (err) => {
                    this.loadingTable = false
                }
            )
        },
        tableRowClassName({ row, rowIndex }) {
            if (row.username == this.userInfo.username && this.isAuthenticated) {
                return "own-submit-row";
            }
        },
        handleOnlyMine() {
            if (this.formFilter.onlyMine) {
                // 需要判断是否为登陆状态
                if (!this.isAuthenticated) {
                    this.formFilter.onlyMine = false
                    myMessage.error('请先登录！')
                    return
                }
            }
            this.currentPage = 1
            this.getSubmissions()
        },
        onPageSizeChange(pageSize) {
            this.limit = pageSize;
            this.getSubmissions();
        },
        goUserHome(username, uid) {
            console.log('goUserHome')
            // this.$router.push({
            //     path: "/user-home",
            //     query: { uid, username },
            // });
        },
        getProblemUri(pid) {
            console.log('getProblemUri')
            // if (this.contestID) {
            //     this.$router.push({
            //         name: "ContestProblemDetails",
            //         params: {
            //             contestID: this.$route.params.contestID,
            //             problemID: pid,
            //         },
            //     });
            // }
        },
        showSubmitDetail(row) {
            console.log('showSubmitDetail')
            // if (this.contestID != null) {
            //     // 比赛提交详情
            //     this.$router.push({
            //         name: "ContestSubmissionDetails",
            //         params: {
            //             contestID: this.contestID,
            //             problemID: row.displayId,
            //             submitID: row.submitId,
            //         },
            //     });
            // } else {
            //     this.$router.push({
            //         name: "SubmissionDetails",
            //         params: { submitID: row.submitId },
            //     });
            // }
        },
        submissionTimeFormat(time) {
            return utils.submissionTimeFormat(time);
        },

        submissionMemoryFormat(memory) {
            return utils.submissionMemoryFormat(memory);
        },

        submissionLengthFormat(length) {
            return utils.submissionLengthFormat(length);
        },
        getStatusColor(status) {
            return "el-tag el-tag--medium status-" + JUDGE_STATUS[status]["color"];
        },
        ...mapActions(["changeModalStatus"])
    },
    computed: {
        ...mapGetters([
            "isAuthenticated",
            "userInfo",
            "isSuperAdmin",
            "isAdminRole",
            "contestRuleType",
            "contestStatus"
        ]),
        scoreColumnVisible() {
            return (
                (this.contestID && this.contestRuleType == this.RULE_TYPE.OI)
            );
        },
    },
    watch: {

    },
}

</script>
<style lang="scss" scoped>
.flex-container #main {
    flex: auto;
}
</style>
  