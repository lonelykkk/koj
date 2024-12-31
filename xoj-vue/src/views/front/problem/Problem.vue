<template>
    <div :class="bodyClass">
        <div id="problem-main">
            <el-row class="problem-box">
                <el-col :sm="24" :md="24" :lg="12" class="problem-left">
                    <el-tabs v-model="activeName" type="border-card" @tab-click="handleClickTab">
                        <el-tab-pane name="problemDetail" v-loading="loading">
                            <span slot="label">
                                <i class="el-icon-c-scale-to-original">
                                    题目描述
                                </i>
                            </span>
                            <div :padding="10" shadow class="problem-detail">
                                <div slot="header" class="panel-title">
                                    <span>
                                        {{ problemData.problem.title }}
                                    </span><br />
                                    <div v-if="problemData.tags != undefined && problemData.tags.length > 0"
                                        class="problem-tag">
                                        <el-popover placement="right-start" width="60" trigger="hover">
                                            <el-tag slot="reference" size="small" type="primary" style="cursor: pointer;"
                                                effect="plain">
                                                显示标签
                                            </el-tag>
                                            <el-tag v-for="(tag, index) in problemData.tags" :key="index" size="small"
                                                :color="tag.color ? tag.color : '#409eff'" effect="dark"
                                                style="margin-right:5px;margin-top:2px">
                                                {{ tag.name }}
                                            </el-tag>
                                        </el-popover>
                                    </div>
                                    <div v-else-if="problemData.tags == undefined || problemData.tags.length == 0"
                                        class="problem-tag">
                                        <el-tag effect="plain" size="small">
                                            暂无标签
                                        </el-tag>
                                    </div>
                                    <div class="problem-menu">
                                        <!-- <span v-if="isShowProblemDiscussion">
                                            <el-link type="primary" :underline="false" @click="goProblemDiscussion"><i
                                                    class="fa fa-comments" aria-hidden="true"></i>
                                                题目讨论
                                            </el-link>
                                        </span>
                                        <span>
                                            <el-link type="primary" :underline="false"
                                                @click="graphVisible = !graphVisible"><i class="fa fa-pie-chart"
                                                    aria-hidden="true"></i>
                                                题目统计
                                            </el-link>
                                        </span>
                                        <span>
                                            <el-link type="primary" :underline="false" @click="goProblemSubmission"><i
                                                    class="fa fa-bars" aria-hidden="true"></i>
                                                全部提交
                                            </el-link>
                                        </span> -->
                                    </div>
                                    <div class="question-intr">
                                        <template>
                                            <span>
                                                时间限制: {{ problemData.problem.timeLimit }}MS，其他语言
                                                {{ problemData.problem.timeLimit * 2 }} MS
                                            </span>
                                            <br />
                                            <span>
                                                内存限制: {{ problemData.problem.memoryLimit }}MB，其他语言
                                                {{ problemData.problem.memoryLimit * 2 }} MB
                                            </span>
                                            <br />
                                        </template>
                                        <template v-if="problemData.problem.difficulty != null">
                                            <span>
                                                难度:
                                                <span class="el-tag el-tag--small"
                                                    :style="getLevelColor(problemData.problem.difficulty)">
                                                    {{ getLevelName(problemData.problem.difficulty) }}
                                                </span>
                                            </span>
                                            <br />
                                        </template>
                                        <template>
                                            <span>
                                                题目类型: {{ problemData.problem.type == 0 ? 'ACM' : 'OI' }}
                                            </span>
                                            <br />
                                        </template>
                                        <template v-if="problemData.problem.author">
                                            <span>
                                                出题人:
                                                <el-link type="info" class="author-name">
                                                    {{ problemData.problem.author }}
                                                </el-link>
                                            </span>
                                            <br />
                                        </template>
                                    </div>
                                </div>

                                <div id="problem-content">
                                    <template v-if="problemData.description">
                                        <p class="title"> 描述 </p>
                                        <p class="content markdown-body" v-html="problemData.description" v-katex
                                            v-highlight>
                                        </p>
                                    </template>

                                    <template v-if="problemData.problem.input">
                                        <p class="title"> 输入描述 </p>
                                        <p class="content markdown-body" v-html="problemData.problem.input" v-katex
                                            v-highlight></p>
                                    </template>

                                    <template v-if="problemData.problem.output">
                                        <p class="title"> 输出描述 </p>
                                        <p class="content markdown-body" v-html="problemData.problem.output" v-katex
                                            v-highlight></p>
                                    </template>

                                    <template v-if="problemData.problem.examples">
                                        <div v-for="(example, index) of problemData.problem.examples" :key="index">
                                            <div class="flex-container example">
                                                <div class="example-input">
                                                    <p class="title">
                                                        用例输入 {{ index + 1 }}
                                                        <!-- <a class="copy" v-clipboard:copy="example.input"> -->
                                                        <a class="copy">
                                                            <i class="el-icon-document-copy"></i>
                                                        </a>
                                                        <!-- <a class="copy" v-clipboard:copy="example.input"
                                                            v-clipboard:success="onCopy" v-clipboard:error="onCopyError">
                                                            <i class="el-icon-document-copy"></i>
                                                        </a> -->
                                                    </p>
                                                    <pre>{{ example.input }}</pre>
                                                </div>
                                                <div class="example-output">
                                                    <p class="title">
                                                        用例输出 {{ index + 1 }}
                                                        <!-- <a class="copy" v-clipboard:copy="example.output"> -->
                                                        <a class="copy">
                                                            <i class="el-icon-document-copy"></i>
                                                        </a>
                                                    </p>
                                                    <pre>{{ example.output }}</pre>
                                                </div>
                                            </div>
                                        </div>
                                    </template>

                                    <template v-if="problemData.problem.hint">
                                        <p class="title"> 提示 </p>
                                        <el-card dis-hover>
                                            <p class="hint-content markdown-body" v-html="problemData.problem.hint" v-katex
                                                v-highlight></p>
                                        </el-card>
                                    </template>
                                </div>
                            </div>
                        </el-tab-pane>
                        <el-tab-pane name="mySubmission">
                            <span slot="label">
                                <i class="el-icon-time">
                                    我的提交
                                </i>
                            </span>
                            <template v-if="!isAuthenticated">
                                <div style="margin:20px 0px;margin-left:-20px;" id="js-submission">
                                    <el-alert title="请先登录!" type="warning" center :closable="false"
                                        description="登录以查看您的提交记录" show-icon>
                                    </el-alert>
                                </div>
                            </template>
                            <template v-else>
                                <div style="margin-right:10px;" id="js-submission">
                                    <vxe-table align="center" :data="mySubmissions" stripe auto-resize border="inner"
                                        :loading="loadingTable">
                                        <vxe-table-column title="提交时间" min-width="96">
                                            <template v-slot="{ row }">
                                                <span>
                                                    <el-tooltip :content="row.submitTime | localtime" placement="top">
                                                        <span>{{ row.submitTime | fromNow }}</span>
                                                    </el-tooltip>
                                                </span>
                                            </template>
                                        </vxe-table-column>
                                        <vxe-table-column field="status" title="状态" min-width="160">
                                            <template v-slot="{ row }">
                                                <span :class="getStatusColor(row.status)">
                                                    {{ JUDGE_STATUS[row.status].name }}
                                                </span>
                                            </template>
                                        </vxe-table-column>
                                        <vxe-table-column title="运行时间" min-width="96">
                                            <template v-slot="{ row }">
                                                <span>{{ submissionTimeFormat(row.time) }}</span>
                                            </template>
                                        </vxe-table-column>
                                        <vxe-table-column title="运行内存" min-width="96">
                                            <template v-slot="{ row }">
                                                <span>{{ submissionMemoryFormat(row.memory) }}</span>
                                            </template>
                                        </vxe-table-column>
                                        <vxe-table-column field="language" title="语言" show-overflow min-width="130">
                                            <template v-slot="{ row }">
                                                <el-tooltip class="item" effect="dark" content="查看提交详情" placement="top">
                                                    <el-button type="text" @click="showSubmitDetail(row)">
                                                        {{ row.language }}
                                                    </el-button>
                                                </el-tooltip>
                                            </template>
                                        </vxe-table-column>
                                    </vxe-table>
                                    <Pagination :total="mySubmission_total" :page-size="mySubmission_limit"
                                        @on-change="getMySubmission" :current.sync="mySubmission_currentPage">
                                    </Pagination>
                                </div>
                            </template>
                        </el-tab-pane>
                    </el-tabs>
                </el-col>

                <!-- <div class="problem-resize hidden-md-and-down" title="收缩侧边栏">
                    <span>⋮</span>
                </div> -->

                <el-col :sm="24" :md="24" :lg="12" class="problem-right">
                    <el-card :padding="10" id="submit-code" shadow="always" class="submit-detail">
                        <CodeMirror :value.sync="code" :language.sync="language" :theme.sync="theme" :height.sync="height"
                            :fontSize.sync="fontSize" :tabSize.sync="tabSize" :openTestCaseDrawer.sync="openTestCaseDrawer"
                            :isAuthenticated="isAuthenticated" @resetCode="onResetToTemplate" @changeTheme="onChangeTheme"
                            @changeLang="onChangeLang" :pid="problemData.problem.id">

                        </CodeMirror>

                        <el-row>
                            <el-col :sm="24" :md="10" :lg="10" style="margin-top:4px;">
                                <div v-if="!isAuthenticated">
                                    <el-alert type="info" show-icon effect="dark" :closable="false">
                                        请先登录
                                    </el-alert>
                                </div>
                                <div class="status" v-if="statusVisible">
                                    <template>
                                        <span>评测状态:</span>
                                        <el-tooltip class="item" effect="dark" content="查看提交详情" placement="top">
                                            <el-tag effect="dark" class="submission-status" :color="submissionStatus.color">
                                                <i class="fa fa-circle" aria-hidden="true"></i>
                                                {{ submissionStatus.text }}
                                            </el-tag>
                                        </el-tooltip>
                                    </template>
                                </div>
                            </el-col>
                            <el-col :sm="24" :md="14" :lg="14" style="margin-top:4px;">
                                <el-button type="primary" icon="el-icon-edit-outline" size="small" :loading="submitting"
                                    @click.native="submitCode" :disabled="submitted" class="fl-right">
                                    <span v-if="submitting">提交中</span>
                                    <span v-else>提交评测</span>
                                </el-button>
                                <el-tag type="success" :class="openTestCaseDrawer ? 'tj-btn active' : 'tj-btn non-active'"
                                    @click.native="openTestJudgeDrawer" effect="plain">
                                    <svg t="1653665263421" class="icon" viewBox="0 0 1024 1024" version="1.1"
                                        xmlns="http://www.w3.org/2000/svg" p-id="1656" width="12" height="12"
                                        style="vertical-align: middle;">
                                        <path
                                            d="M1022.06544 583.40119c0 11.0558-4.034896 20.61962-12.111852 28.696576-8.077979 8.077979-17.639752 12.117992-28.690436 12.117992L838.446445 624.215758c0 72.690556-14.235213 134.320195-42.718941 184.89915l132.615367 133.26312c8.076956 8.065699 12.117992 17.634636 12.117992 28.690436 0 11.050684-4.034896 20.614503-12.117992 28.691459-7.653307 8.065699-17.209964 12.106736-28.690436 12.106736-11.475356 0-21.040199-4.041036-28.690436-12.106736L744.717737 874.15318c-2.124384 2.118244-5.308913 4.88424-9.558703 8.283664-4.259 3.3984-13.180184 9.463536-26.78504 18.171871-13.598716 8.715499-27.415396 16.473183-41.439808 23.276123-14.029528 6.797823-31.462572 12.966313-52.289923 18.49319-20.827351 5.517667-41.446971 8.28571-61.842487 8.28571L552.801776 379.38668l-81.611739 0 0 571.277058c-21.668509 0-43.250036-2.874467-64.707744-8.615215-21.473057-5.734608-39.960107-12.749372-55.476499-21.039175-15.518438-8.289804-29.541827-16.572444-42.077328-24.867364-12.541641-8.290827-21.781072-15.193027-27.739784-20.714787l-9.558703-8.93244L154.95056 998.479767c-8.500605 8.921183-18.699897 13.386892-30.606065 13.386892-10.201339 0-19.335371-3.40454-27.409257-10.202363-8.079002-7.652284-12.437264-17.10968-13.080923-28.372188-0.633427-11.263531 2.659573-21.143553 9.893324-29.647227l128.787178-144.727219c-24.650423-48.464805-36.980239-106.699114-36.980239-174.710091L42.738895 624.207571c-11.057847 0-20.61655-4.041036-28.690436-12.111852-8.079002-8.082072-12.120039-17.640776-12.120039-28.696576 0-11.050684 4.041036-20.61962 12.120039-28.689413 8.073886-8.072863 17.632589-12.107759 28.690436-12.107759l142.81466 0L185.553555 355.156836l-110.302175-110.302175c-8.074909-8.077979-12.113899-17.640776-12.113899-28.691459 0-11.04966 4.044106-20.61962 12.113899-28.690436 8.071839-8.076956 17.638729-12.123109 28.691459-12.123109 11.056823 0 20.612457 4.052293 28.692482 12.123109l110.302175 110.302175 538.128077 0 110.303198-110.302175c8.070816-8.076956 17.632589-12.123109 28.690436-12.123109 11.050684 0 20.617573 4.052293 28.689413 12.123109 8.077979 8.070816 12.119015 17.640776 12.119015 28.690436 0 11.050684-4.041036 20.614503-12.119015 28.691459l-110.302175 110.302175 0 187.448206 142.815683 0c11.0558 0 20.618597 4.034896 28.690436 12.113899 8.076956 8.069793 12.117992 17.638729 12.117992 28.683273l0 0L1022.06544 583.40119 1022.06544 583.40119zM716.021162 216.158085 307.968605 216.158085c0-56.526411 19.871583-104.667851 59.616796-144.414087 39.733956-39.746236 87.88256-59.611679 144.411017-59.611679 56.529481 0 104.678084 19.865443 144.413064 59.611679C696.156742 111.48921 716.021162 159.631674 716.021162 216.158085L716.021162 216.158085 716.021162 216.158085 716.021162 216.158085z"
                                            p-id="1657" :fill="openTestCaseDrawer ? '#ffffff' : '#67c23a'">
                                        </path>
                                    </svg>
                                    <span style="vertical-align: middle;">
                                        在线自测
                                    </span>
                                </el-tag>
                            </el-col>
                            <el-col :sm="24" :md="24" :lg="24" style="margin-top:8px;">
                                <div>
                                    <template v-if="statusVisible && result.errorMessage != null">
                                        <el-card>
                                            <div style="color: #f90;font-weight: 600;">
                                                <pre>{{ result.errorMessage }}</pre>
                                            </div>
                                        </el-card>
                                    </template>
                                </div>
                            </el-col>
                        </el-row>
                    </el-card>
                </el-col>
            </el-row>
        </div>
    </div>
</template>
  
<script>
import { mapGetters, mapActions } from "vuex"
import storage from "@/common/storage"
import problemApi from '@/api/problem'
import contestApi from "@/api/contest"
import tagApi from '@/api/tag'
import judgeApi from '@/api/judge'
import {
    PROBLEM_LEVEL,
    JUDGE_STATUS,
    JUDGE_STATUS_RESERVE,
    RULE_TYPE
} from '@/common/constants'
import { pie, largePie } from "./chartData"
import myMessage from '@/utils/message'
import utils from '@/utils/utils'
import Pagination from "@/components/front/common/Pagination"
const CodeMirror = () => import('@/components/front/common/CodeMirror.vue')
// 只显示这些状态的图形占用
const filtedStatus = ["wa", "ce", "ac", "pa", "tle", "mle", "re", "pe"]

export default {
    name: 'ProblemDetail',
    components: {
        CodeMirror,
        Pagination
    },
    data() {
        return {
            statusVisible: false,
            PROBLEM_LEVEL: {},
            JUDGE_STATUS_RESERVE: {},
            JUDGE_STATUS: {},
            RULE_TYPE: {},
            bodyClass: '',
            activeName: 'problemDetail',
            code: '',
            language: 'C++',
            theme: 'solarized',
            fontSize: '14px',
            tabSize: 4,
            height: 550,
            openTestCaseDrawer: false,
            submitting: false,
            submitted: false,
            submissionExists: false,
            submissionId: '',
            result: {
                status: 9,
                errorMessage: '',
            },
            problemData: {
                problem: {},
                tags: [],
                description: '',
                ac: '',
                total: ''
            },
            problemID: '',
            contestID: '',
            pie: pie,
            largePie: largePie,
            // echarts 无法获取隐藏dom的大小，需手动指定
            largePieInitOpts: {
                width: "380",
                height: "380",
            },
            loading: false,
            loadingTable: false,
            mySubmission_total: 0,
            mySubmission_limit: 10,
            mySubmission_currentPage: 1,
            mySubmissions: []
        }
    },
    created() {
        // this.initProblemCodeAndSetting();
        this.JUDGE_STATUS_RESERVE = Object.assign({}, JUDGE_STATUS_RESERVE);
        this.JUDGE_STATUS = Object.assign({}, JUDGE_STATUS);
        this.PROBLEM_LEVEL = Object.assign({}, PROBLEM_LEVEL);
        this.RULE_TYPE = Object.assign({}, RULE_TYPE);
        if (this.$route.name === 'ProblemDetail') {
            this.bodyClass = 'problem-body';
            this.problemID = this.$route.params.problemID
        }
        if (this.$route.params.contestID) {
            this.contestID = this.$route.params.contestID
        }
    },
    mounted() {
        this.init()
    },
    methods: {
        init() {
            this.problemID = this.$route.params.problemID;
            this.loading = true
            if (this.$route.name === 'ContestProblemDetail') {
                contestApi.getContestProblemDetails(this.contestID, this.problemID).then(
                    (res) => {
                        let result = res.data.data
                        // result["myStatus"] = -10  // 设置默认值
                        result.problem.examples = utils.stringToExamples(
                            result.problem.examples
                        )
                        this.problemData = result
                        this.loading = false
                    },
                    (error) => {
                        this.loading = false
                    }
                )
            } else {
                problemApi.getProblemDetail(this.problemID).then(
                    (res) => {
                        let result = res.data.data
                        // result["myStatus"] = -10  // 设置默认值
                        result.problem.examples = utils.stringToExamples(
                            result.problem.examples
                        )
                        this.problemData = result
                        this.loading = false
                    },
                    (error) => {
                        this.loading = false
                    }
                )
            }
        },
        submitCode() {
            console.log('submit code')
            if (this.code.trim() === '') {
                myMessage.error('代码不能为空')
                return
            }

            if (this.code.length > 65535) {
                myMessage.error('代码的字符长度不能超过65535！')
                return
            }

            this.result = { status: 9 }
            this.submitting = true
            let data = {
                pid: this.problemID, // 如果是比赛题目就为display_id
                language: this.language,
                code: this.code,
                cid: this.contestID
            }

            // 显示评测状态
            this.statusVisible = true
            judgeApi.submitProblemJudge(data).then(
                (res) => {
                    this.submissionId = res.data.data.submitId
                    this.submitting = false // 提交成功，取消按钮的加载状态
                    this.submissionExists = true // 提交成功，submitId已经存在
                    myMessage.success("提交代码成功")
                    this.submitted = true // 禁用提交按钮
                    this.checkSubmissionStatus(); // 进行评测状态检查
                },
                (err) => {
                    this.submitting = false
                    this.statusVisible = false
                }
            )

        },
        checkSubmissionStatus() {
            console.log("checkSubmissionStatus");
            let submitId = this.submissionId;
            // 设置一个定时器，2秒钟查询一次评测状态
            let timerId = setInterval(() => {
                judgeApi.checkSubmissionStatus(submitId).then(
                    (res) => {
                        this.result.status = res.data.data.status
                        if (
                            res.data.data.status != JUDGE_STATUS_RESERVE["Pending"] &&
                            res.data.data.status != JUDGE_STATUS_RESERVE["Compiling"] &&
                            res.data.data.status != JUDGE_STATUS_RESERVE["Judging"]
                        ) {
                            // 评测结束
                            console.log('res1 = ' + this.result.status);
                            this.result.errorMessage = res.data.data.errorMessage
                            this.submitting = false;
                            this.submitted = false;
                            // this.init(); // 初始化页面相关数据
                            clearInterval(timerId); // 停止定时器
                            console.log('定时器停止');
                        } else {
                            // 评测未结束，继续获取评测状态
                        }
                    },
                    (err) => { }
                );
            }, 2000);

        },
        getMySubmission() {
            this.loadingTable = true
            if (this.contestID) {
                let query = {
                    limit: this.mySubmission_limit,
                    currentPage: this.mySubmission_currentPage,
                    onlyMine: true,
                    contestID: this.contestID
                }
                contestApi.getContestSubmissionList(query).then(
                    (res) => {
                        this.mySubmissions = res.data.data.records
                        this.mySubmission_total = res.data.data.total
                        this.loadingTable = false
                    },
                    (err) => {
                        this.loadingTable = false
                    }
                )
            } else {
                let query = {
                    onlyMine: true,
                    currentPage: this.mySubmission_currentPage,
                    problemID: this.problemID,
                    limit: this.mySubmission_limit,
                }
                judgeApi.getSubmissionList(query).then(
                    (res) => {
                        let data = res.data.data
                        this.mySubmissions = data.records
                        this.mySubmission_total = data.total
                        this.loadingTable = false
                    },
                    (err) => {
                        this.loadingTable = false
                    }
                )
            }

        },
        showSubmitDetail(row) {
            // todo 
            console.log("showSubmitDetail")
        },
        handleClickTab({ name }) {
            if (name == 'mySubmission' && this.isAuthenticated) {
                this.getMySubmission()
            }
        },
        getStatusColor(status) {
            return "el-tag el-tag--medium status-" + JUDGE_STATUS[status].color;
        },
        submissionTimeFormat(time) {
            return utils.submissionTimeFormat(time);
        },
        submissionMemoryFormat(memory) {
            return utils.submissionMemoryFormat(memory);
        },
        onResetToTemplate() {
            this.code = ''
        },
        onChangeTheme(newTheme) {
            this.theme = newTheme
        },
        onChangeLang(newLang) {
            this.language = newLang
        },
        getLevelName(difficulty) {
            return utils.getLevelName(difficulty);
        },
        getLevelColor(difficulty) {
            return utils.getLevelColor(difficulty);
        },
        openTestJudgeDrawer() {
            this.statusVisible = false
            this.openTestCaseDrawer = !this.openTestCaseDrawer;
        }
    },
    computed: {
        ...mapGetters([
            "isAuthenticated",
            "contestStatus",
            "contestRuleType",
        ]),
        submissionStatus() {
            return {
                text: JUDGE_STATUS[this.result.status]['name'],
                color: JUDGE_STATUS[this.result.status]['rgb'],
            };
        },
        contest() {
            return this.$store.state.contest.contest;
        },
        contestEnded() {
            return this.contestStatus === CONTEST_STATUS.ENDED;
        },
    },
    watch: {
        $route() {
            // this.initProblemCodeAndSetting();
            this.init();
        },
        isAuthenticated(newVal) {
            if (newVal === true) {
                this.init();
            }
        }
    },
}

</script>
<style lang="scss">
#problem-main {
    flex: auto;
}

.problem-box {
    width: 100%;
    height: 100%;
    overflow: hidden;
    // margin: 0 3%;
}

.problem-left {
    width: calc(50% - 10px);
    /*左侧初始化宽度*/
    height: 100%;
    overflow-y: auto;
    overflow-x: hidden;
    float: left;
}

.problem-resize {
    cursor: col-resize;
    float: left;
    position: relative;
    top: 330px;
    background-color: #d6d6d6;
    border-radius: 5px;
    margin-top: -10px;
    width: 10px;
    height: 50px;
    background-size: cover;
    background-position: center;
    /*z-index: 99999;*/
    font-size: 32px;
    color: white;
}

.problem-resize:hover {
    display: block;
}

.problem-resize:hover {
    content: '';
    position: absolute;
    display: block;
    width: 6px;
    height: 24px;
    left: -6px;
}


/*拖拽区鼠标悬停样式*/
.problem-resize:hover {
    color: #444444;
}

.problem-right {
    height: 100%;
    float: left;
    width: 50%;
}

#submit-code {
    height: auto;
}

#submit-code .status {
    float: left;
}

.submission-status:hover {
    cursor: pointer;
}

#submit-code .status span {
    margin-right: 10px;
    margin-left: 10px;
    font-size: 14px;
    font-weight: bolder;
}

.problem-detail {
    padding-right: 15px;
}

.problem-tag {
    display: inline;
}

.question-intr {
    margin-top: 30px;
    border-radius: 4px;
    border: 1px solid #ddd;
    border-left: 2px solid #3498db;
    background: #fafafa;
    padding: 10px;
    line-height: 1.8;
    margin-bottom: 10px;
    font-size: 14px;
}

p.content {
    margin-left: 25px;
    margin-right: 20px;
    font-size: 15px;
}

.flex-container {
    display: flex;
    width: 100%;
    max-width: 100%;
    justify-content: space-around;
    align-items: flex-start;
    flex-flow: row nowrap;
}

.example {
    align-items: stretch;
}

.example-input,
.example-output {
    width: 50%;
    flex: 1 1 auto;
    display: flex;
    flex-direction: column;
}

.example pre {
    flex: 1 1 auto;
    align-self: stretch;
    border-style: solid;
    background: transparent;
    padding: 5px 10px;
    white-space: pre;
    margin-top: 10px;
    margin-bottom: 10px;
    background: #f1f1f1;
    border: 1px dashed #e9eaec;
    overflow: auto;
    font-size: 0.9em;
    margin-right: 7%;
}

.submit-detail {
    height: 100%;
}


.fl-right {
    float: right;
}

.tj-btn {
    margin-right: 15px;
    float: right;
    cursor: pointer;
}

.tj-btn.non-active {
    border: 1px solid #32ca99;
}

.tj-btn.non-active:hover {
    background-color: #d5f1eb;
}

.tj-btn.active {
    background-color: #67c23a;
    border-color: #67c23a;
    color: #fff;
}
</style>
  