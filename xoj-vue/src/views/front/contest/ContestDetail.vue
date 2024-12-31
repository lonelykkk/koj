<template>
    <div class="contest-body">
        <el-row>
            <el-col :xs="24" :md="24" :lg="24">
                <el-card shadow>
                    <div class="contest-title">
                        <div slot="header">
                            <span class="panel-title">{{ contest.title }}</span>
                        </div>
                    </div>
                    <el-row style="margin-top: 10px;">
                        <el-col :span="14" class="text-align:left">
                            <el-tag :type="CONTEST_TYPE_REVERSE[contest.auth].color" effect="plain">
                                <i class="el-icon-collection-tag"></i>
                                {{ contest.auth === 0 ? '公开考核' : '私有考核' }}
                            </el-tag>
                        </el-col>
                        <el-col :span="10" style="text-align:right">
                            <el-button size="small" plain v-if="contest.count != null && isRegistered">
                                <i class="el-icon-user-solid" style="color:rgb(48, 145, 242);"></i>
                                x{{ contest.count }}
                            </el-button>
                            <el-button @click="registerContest" size="small" plain v-else>
                                <i class="el-icon-user-solid" style="color:rgb(48, 145, 242);"></i>
                                报名
                            </el-button>
                            <template v-if="contest.type == 0">
                                <el-button size="small" :type="'primary'">
                                    <i class="fa fa-trophy"></i>
                                    {{ contest.type | parseContestType }}
                                </el-button>
                            </template>
                            <template v-else>
                                <el-button size="small" :type="'warning'">
                                    <i class="fa fa-trophy"></i>
                                    {{ contest.type | parseContestType }}
                                </el-button>
                            </template>
                        </el-col>
                    </el-row>
                    <div class="contest-time">
                        <el-row>
                            <el-col :xs="24" :md="12" class="left">
                                <p>
                                    <i class="fa fa-hourglass-start" aria-hidden="true"></i>
                                    开始时间：{{ contest.startTime | localtime }}
                                </p>
                            </el-col>
                            <el-col :xs="24" :md="12" class="right">
                                <p>
                                    <i class="fa fa-hourglass-end" aria-hidden="true"></i>
                                    结束时间：{{ contest.endTime | localtime }}
                                </p>
                            </el-col>
                        </el-row>
                    </div>
                    <div class="slider">
                        <el-slider v-model="progressValue" :format-tooltip="formatTooltip" :step="timeStep"></el-slider>
                    </div>
                    <el-row>
                        <el-col :span="24" style="text-align:center">
                            <el-tag effect="dark" size="medium" :style="countdownColor">
                                <i class="fa fa-circle" aria-hidden="true"></i>
                                {{ countdown }}
                            </el-tag>
                        </el-col>
                    </el-row>
                </el-card>
            </el-col>
        </el-row>
        <div class="sub-menu">
            <el-tabs @tab-click="tabClick" v-model="route_name">
                <el-tab-pane name="ContestDetail" lazy>
                    <span slot="label">
                        <i class="el-icon-s-home"></i>
                        &nbsp;考核简介
                    </span>
                    <el-card class="box-card">
                        <Markdown :content="contest.description">
                        </Markdown>
                    </el-card>
                </el-tab-pane>

                <el-tab-pane name="ContestProblemList" lazy :disabled="contestMenuDisabled">
                    <span slot="label">
                        <i class="fa fa-list" aria-hidden="true"></i>
                        &nbsp;题目
                    </span>
                    <transition name="el-zoom-in-bottom">
                        <router-view v-if="route_name === 'ContestProblemList'"></router-view>
                    </transition>
                </el-tab-pane>

                <el-tab-pane name="ContestSubmissionList" lazy :disabled="contestMenuDisabled">
                    <span slot="label">
                        <i class="el-icon-menu"></i>
                        &nbsp;状态
                    </span>
                    <transition name="el-zoom-in-bottom">
                        <router-view v-if="route_name === 'ContestSubmissionList'"></router-view>
                    </transition>
                </el-tab-pane>

                <el-tab-pane name="ContestAnnouncementList" lazy :disabled="contestMenuDisabled">
                    <span slot="label">
                        <i class="fa fa-bullhorn" aria-hidden="true"></i>
                        &nbsp;公告
                    </span>
                    <transition name="el-zoom-in-bottom">
                        <router-view v-if="route_name === 'ContestAnnouncementList'"></router-view>
                    </transition>
                </el-tab-pane>
            </el-tabs>
        </div>

        <el-dialog :visible.sync="registerDialogVisible" width="30%" center>
            <div slot="title">
                <span class="panel-title" style="color: #e6a23c;">
                    <i class="el-icon-warning">
                        输入密码
                    </i>
                </span>
            </div>
            <el-input v-model="contestPassword" type="password" placeholder="请输入考核密码"
                @keydown.enter.native="checkPassword" />
            <span slot="footer" class="dialog-footer">
                <el-button @click="registerDialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="checkPassword">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>
  
<script>
import myMessage from '@/utils/message'
import contestApi from '@/api/contest'
import utils from '@/utils/utils'
import time from '@/common/time'
import moment from 'moment'
import { mapState, mapGetters, mapActions } from 'vuex'
import { addCodeBtn } from '@/utils/codeblock'
import {
    CONTEST_STATUS_REVERSE,
    CONTEST_TYPE_REVERSE,
    CONTEST_STATUS,
    RULE_TYPE
} from '@/common/constants'
import Markdown from "@/components/front/common/Markdown"

export default {
    name: 'ContestDetail',
    components: {
        Markdown
    },
    data() {
        return {
            route_name: 'ContestDetail',
            timer: null,
            CONTEST_STATUS: {},
            CONTEST_STATUS_REVERSE: {},
            CONTEST_TYPE_REVERSE: {},
            RULE_TYPE: {},
            btnLoading: false,
            contestPassword: '',
            registerDialogVisible: false,
        }
    },
    created() {
        this.contestID = this.$route.params.contestID
        this.route_name = this.$route.name
        this.CONTEST_TYPE_REVERSE = Object.assign({}, CONTEST_TYPE_REVERSE)
        this.CONTEST_STATUS = Object.assign({}, CONTEST_STATUS)
        this.CONTEST_STATUS_REVERSE = Object.assign({}, CONTEST_STATUS_REVERSE)
        this.RULE_TYPE = Object.assign({}, RULE_TYPE)

        this.$store.dispatch('getContest').then(
            (res) => {
                let data = res.data.data.contest
                let endTime = moment(data.endTime);
                // 如果当前时间还是在比赛结束前的时间，需要计算倒计时，同时开启获取比赛公告的定时器
                if (endTime.isAfter(moment(data.now))) {
                    // 实时更新时间
                    this.timer = setInterval(() => {
                        this.$store.commit('nowAdd1s');
                    }, 1000);
                }

                this.$nextTick((_) => {
                    addCodeBtn();
                });
            });
    },
    methods: {
        formatTooltip(val) {
            if (this.contest.status == -1) {
                // 还未开始
                return '00:00:00';
            } else if (this.contest.status == 0) {
                return time.secondFormat(this.BeginToNowDuration); // 格式化时间
            } else {
                return time.secondFormat(this.contest.duration);
            }
        },
        registerContest() {
            if (this.contest.auth == 1) {
                this.registerDialogVisible = true
            } else {
                let data = {
                    cid: this.contestID + '',
                    password: ''
                }
                contestApi.registerContest(data).then(
                    (res) => {
                        myMessage.success('报名成功');
                        this.$store.commit('contestRegister', { registered: true })
                    }
                );
            }
        },
        checkPassword() {
            if (this.contestPassword === '') {
                myMessage.warning('请输入考核密码');
                return;
            }
            this.btnLoading = true;
            let data = {
                cid: this.contestID + '',
                password: this.contestPassword
            }
            contestApi.registerContest(data).then(
                (res) => {
                    myMessage.success('报名成功')
                    this.$store.commit('contestRegister', { registered: true })
                    this.btnLoading = false
                    this.registerDialogVisible = false
                },
                (res) => {
                    this.btnLoading = false
                    this.registerDialogVisible = false
                }
            );
        },
        tabClick(tab) {
            let name = tab.name
            if (name !== this.$route.name) {
                this.$router.push({ name: name });
            }
        },
    },
    computed: {
        ...mapState({
            contest: (state) => state.contest.contest,
            now: (state) => state.contest.now,
        }),
        ...mapGetters([
            'contestMenuDisabled',
            'contestRuleType',
            'isRegistered',
            'contestStatus',
            'countdown',
            'BeginToNowDuration',
            'isContestAdmin',
            'isSuperAdmin',
            'ContestRealTimePermission',
            'passwordFormVisible',
            'userInfo'
        ]),
        progressValue: {
            get: function () {
                return this.$store.getters.progressValue;
            },
            set: function () { },
        },
        timeStep() {
            // 时间段平分滑条长度
            return 100 / this.contest.duration;
        },
        countdownColor() {
            if (this.contestStatus) {
                return 'color:' + CONTEST_STATUS_REVERSE[this.contestStatus].color;
            }
        },
        contestEnded() {
            return this.contestStatus === CONTEST_STATUS.ENDED;
        },
    },
    watch: {
        $route(newVal) {
            this.route_name = newVal.name;
            if (newVal.name == 'ContestProblemDetail') {
                this.route_name = 'ContestProblemList';
            } else if (this.route_name == 'ContestSubmissionDetail') {
                this.route_name = 'ContestSubmissionList';
            }
            this.contestID = newVal.params.contestID;
        },
    },
    beforeDestroy() {
        clearInterval(this.timer)
        this.$store.commit('clearContest')
    },
}

</script>
<style lang="scss" scoped>
// .contest-body {
//     // padding: 0 20px;
//     padding: 0 3%;
// }

.panel-title {
    font-size: 1.5rem !important;
    font-weight: 500;
}

.contest-time {
    margin-top: 20px;
}

@media screen and (min-width: 768px) {
    .contest-time .left {
        text-align: left;
    }

    .contest-time .right {
        text-align: right;
    }

    .password-form-card {
        width: 400px;
        margin: 0 auto;
    }
}

@media screen and (max-width: 768px) {

    .contest-time {
        text-align: center;

        .left {
            text-align: center;
        }

        .right {
            text-align: center;
        }
    }
}

::v-deep .el-slider__button {
    width: 20px !important;
    height: 20px !important;
    background-color: #409eff !important;
}

::v-deep .el-slider__button-wrapper {
    z-index: 500;
}

::v-deep .el-slider__bar {
    height: 10px !important;
    background-color: #09be24 !important;
}

::v-deep .el-card__header {
    border-bottom: 0px;
    padding-bottom: 0px;
}

::v-deep .el-tabs__nav-wrap {
    background: #fff;
    border-radius: 3px;
}

::v-deep .el-tabs--top .el-tabs__item.is-top:nth-child(2) {
    padding-left: 20px;
}

.contest-title {
    text-align: center;
}

.contest-time {
    width: 100%;
    font-size: 16px;
}

.slider {
    margin: 15px 0 10px 0;
}

.el-tag--dark {
    border-color: #fff;
}

.el-tag {
    color: rgb(25, 190, 107);
    background: #fff;
    border: 1px solid #e9eaec;
    font-size: 18px;
}

.sub-menu {
    margin-top: 15px;
}

.password-form-tips {
    text-align: center;
    font-size: 14px;
}
</style>
  