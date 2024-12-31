<template>
    <div class="problem-list">
        <div class="no-problem" v-if="!problems.length">
            <el-empty description="暂无题目"></el-empty>
        </div>
        <div v-else>
            <vxe-table border="inner" stripe auto-resize highlight-hover-row :data="problems" align="center"
                @cell-click="goContestProblem">
                <!-- OI赛制的最近提交显示 -->
                <vxe-table-column field="oi-status" title="" width="50"
                    v-if="isAuthenticated && contestRuleType == RULE_TYPE.OI">
                    <template v-slot="{ row }">
                        <template v-if="row.score">
                            <span :class="getScoreColor(row.score)" v-if="row.score != null">
                                {{ row.score }}
                            </span>
                        </template>
                    </template>
                </vxe-table-column>

                <!-- ACM赛制的最近提交显示 -->
                <vxe-table-column field="acm-status" title="" width="50"
                    v-if="isAuthenticated && contestRuleType == RULE_TYPE.ACM">
                    <template v-slot="{ row }">
                        <template v-if="row.isAc">
                            <i class="el-icon-check" :style="getIconColor(row.isAc)"></i>
                        </template>
                        <!-- <template v-if="row.isAc">
                            <el-tooltip :content="JUDGE_STATUS[row.myStatus]['name']" placement="top">
                                <i class="el-icon-check" :style="getIconColor(row.myStatus)"></i>
                            </el-tooltip>
                        </template> -->
                    </template>
                </vxe-table-column>
                <vxe-table-column field="displayId" width="80" title="#">
                    <template v-slot="{ row }">
                        <span style="vertical-align: top;" v-if="row.color">
                            <svg t="1633685184463" class="icon" viewBox="0 0 1088 1024" version="1.1"
                                xmlns="http://www.w3.org/2000/svg" p-id="5840" width="25" height="25">
                                <path
                                    d="M575.872 849.408c-104.576 0-117.632-26.56-119.232-31.808-6.528-22.528 32.896-70.592 63.744-96.768l-1.728-2.624c137.6-42.688 243.648-290.112 243.648-433.472A284.544 284.544 0 0 0 478.016 0a284.544 284.544 0 0 0-284.288 284.736c0 150.4 116.352 415.104 263.744 438.336-25.152 29.568-50.368 70.784-39.104 108.928 12.608 43.136 62.72 63.232 157.632 63.232 7.872 0 11.52 9.408 4.352 19.52-21.248 29.248-77.888 63.424-167.68 63.424V1024c138.944 0 215.936-74.816 215.936-126.528a46.72 46.72 0 0 0-16.32-36.608 56.32 56.32 0 0 0-36.416-11.456zM297.152 297.472c0 44.032-38.144 25.344-38.144-38.656 0-108.032 85.248-195.712 190.592-195.712 62.592 0 81.216 39.232 38.08 39.232-105.152 0.064-190.528 87.04-190.528 195.136z"
                                    :fill="row.color" p-id="5841"></path>
                            </svg>
                        </span>
                        <span>{{ row.displayId }}</span>
                    </template>
                </vxe-table-column>
                <vxe-table-column field="displayTitle" title="标题" min-width="200"></vxe-table-column>

                <!-- 以下列只有在实时刷新榜单的情况下才显示 -->
                <vxe-table-column field="ac" title="通过" min-width="80">
                    <template v-slot="{ row }">
                        {{ row.ac }}
                    </template>
                </vxe-table-column>
                <vxe-table-column field="total" title="总数" min-width="80">
                    <template v-slot="{ row }">
                        {{ row.total }}
                    </template>
                </vxe-table-column>
                <vxe-table-column field="ACRate" title="通过率" min-width="120">
                    <template v-slot="{ row }">
                        <span>
                            <el-tooltip effect="dark" :content="row.ac + '/' + row.total" placement="top">
                                <el-progress :text-inside="true" :stroke-width="20"
                                    :percentage="getPassingRate(row.ac, row.total)"></el-progress>
                            </el-tooltip>
                        </span>
                    </template>
                </vxe-table-column>
            </vxe-table>
        </div>
    </div>
</template>
  
<script>
import contestApi from '@/api/contest'
import { mapState, mapGetters } from 'vuex'
import {
    CONTEST_STATUS,
    JUDGE_STATUS,
    RULE_TYPE
} from "@/common/constants"
export default {
    name: 'ContestProblemList',
    components: {
    },
    data() {
        return {
            loadingTable: false,
            JUDGE_STATUS: {},
            RULE_TYPE: {},
            isGetStatusOk: false,
            testcolor: 'rgba(0, 206, 209, 1)'
        }
    },
    created() {
    },
    mounted() {
        this.RULE_TYPE = Object.assign({}, RULE_TYPE);
        this.JUDGE_STATUS = Object.assign({}, JUDGE_STATUS);
        this.getContestProblems();
    },
    methods: {
        getContestProblems() {
            this.$store.dispatch('getContestProblems')
        },
        goContestProblem(event) {
            if (this.contestStatus === CONTEST_STATUS.RUNNING) {
                // 考核未结束
                this.$router.push({
                    name: 'ContestProblemDetail',
                    params: {
                        contestID: this.$route.params.contestID,
                        problemID: event.row.id,
                        displayId: event.row.displayId
                    },
                });
            } else {
                // 考核已结束
                this.$router.push({
                    name: 'ProblemDetail',
                    params: {
                        problemID: event.row.pid
                    },
                });
            }
        },
        getPassingRate(ac, total) {
            if (!total) {
                return 0;
            }
            return ((ac / total) * 100).toFixed(2);
        },
        getIconColor(status) {
            return (
                'font-weight: 600;font-size: 16px;color:' + JUDGE_STATUS[0].rgb
            );
        },
        getScoreColor(score) {
            if (score == 0) {
                return 'el-tag el-tag--small oi-0';
            } else if (score > 0 && score < 100) {
                return 'el-tag el-tag--small oi-between';
            } else if (score == 100) {
                return 'el-tag el-tag--small oi-100';
            }
        },
    },
    computed: {
        ...mapState({
            problems: (state) => state.contest.contestProblems,
        }),
        ...mapGetters([
            'isAuthenticated',
            'contestRuleType',
            'contestStatus'
        ]),
    },
}

</script>
<style lang="scss" scoped>
.problem-list {
    background-color: #FFF;
}
</style>
  