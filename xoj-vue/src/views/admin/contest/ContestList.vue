<template>
    <div>
        <el-card>
            <div slot="header">
                <span class="panel-title home-title">考核列表</span>
                <div>
                    <el-row :gutter="20">
                        <el-col :span="3">
                            <router-link :to="'/admin/contest/create'">
                                <el-button style="margin-top: 10px;" type="primary" size="small" icon="el-icon-plus">
                                    创建考核
                                </el-button>
                            </router-link>
                        </el-col>

                        <el-col :span="5">
                            <el-input style="margin-top: 10px;" size="medium" v-model="keyword" placeholder="请输入关键字"
                                @keyup.enter.native="filterByKeyword">
                                <el-button slot="append" icon="el-icon-search" @click="filterByKeyword"></el-button>
                            </el-input>
                        </el-col>
                    </el-row>
                </div>
            </div>
            <vxe-table :loading="loading" ref="xTable" :data="contestList" auto-resize stripe align="center">
                <vxe-table-column field="id" width="80" title="ID"> </vxe-table-column>
                <vxe-table-column field="title" min-width="100" title="标题" show-overflow></vxe-table-column>
                <vxe-table-column title="类型" width="100">
                    <template v-slot="{ row }">
                        <el-tag type="gray">{{ row.type | parseContestType }}</el-tag>
                    </template>
                </vxe-table-column>
                <vxe-table-column title="权限" width="100">
                    <template v-slot="{ row }">
                        <el-tag :type="CONTEST_TYPE_REVERSE[row.auth].color" effect="plain">
                            {{ CONTEST_TYPE_REVERSE[row.auth].name }}
                        </el-tag>
                    </template>
                </vxe-table-column>
                <vxe-table-column title="状态" width="100">
                    <template v-slot="{ row }">
                        <el-tag effect="dark" :color="CONTEST_STATUS_REVERSE[row.status].color" size="medium">
                            {{ CONTEST_STATUS_REVERSE[row.status].name }}
                        </el-tag>
                    </template>
                </vxe-table-column>
                <vxe-table-column min-width="210" title="信息">
                    <template v-slot="{ row }">
                        <p>Start Time: {{ row.startTime | localtime }}</p>
                        <p>End Time: {{ row.endTime | localtime }}</p>
                        <p>Created Time: {{ row.gmtCreate | localtime }}</p>
                        <p>Creator: {{ row.username }}</p>
                    </template>
                </vxe-table-column>
                <vxe-table-column min-width="180" title="操作">
                    <template v-slot="{ row }">
                        <template>
                            <div style="margin-bottom:10px">
                                <el-tooltip effect="dark" content="编辑" placement="top">
                                    <el-button icon="el-icon-edit" size="mini" @click.native="goEdit(row.id)"
                                        type="primary">
                                    </el-button>
                                </el-tooltip>
                                <el-tooltip effect="dark" content="查看考核题目列表" placement="top">
                                    <el-button icon="el-icon-tickets" size="mini"
                                        @click.native="goContestProblemList(row.id)" type="success">
                                    </el-button>
                                </el-tooltip>
                                <el-tooltip effect="dark" content="查看通告列表" placement="top">
                                    <el-button icon="el-icon-info" size="mini" @click.native="goContestAnnouncement(row.id)"
                                        type="info">
                                    </el-button>
                                </el-tooltip>
                                <el-tooltip effect="dark" content="删除" placement="top">
                                    <el-button icon="el-icon-delete" size="mini" @click.native="deleteContest(row.id)"
                                        type="danger">
                                    </el-button>
                                </el-tooltip>
                                <el-tooltip effect="dark" content="查看学生列表" placement="top">
                                    <el-button icon="el-icon-tickets" size="mini"
                                        @click.native="goContestStudentInfo(row.id)" type="success">
                                    </el-button>
                                </el-tooltip>
                            </div>
                        </template>
                    </template>
                </vxe-table-column>
            </vxe-table>
            <div class="panel-options">
                <el-pagination class="page" layout="prev, pager, next" @current-change="currentChange" :page-size="pageSize"
                    :current-page.sync="currentPage" :total="total">
                </el-pagination>
            </div>
        </el-card>
    </div>
</template>
  
<script>
import myMessage from '@/utils/message'
import utils from '@/utils/utils'
import contestApi from '@/api/contest'
import {
    CONTEST_STATUS_REVERSE,
    CONTEST_TYPE_REVERSE,
} from '@/common/constants'

export default {
    name: 'ContestList',
    components: {
    },
    data() {
        return {
            total: 0,
            pageSize: 10,
            currentPage: 1,
            currentId: 1,
            contestList: [],
            keyword: '',
            loading: false,
            CONTEST_TYPE_REVERSE: {},
            CONTEST_STATUS_REVERSE: {}
        }
    },
    created() {

    },
    mounted() {
        this.CONTEST_TYPE_REVERSE = Object.assign({}, CONTEST_TYPE_REVERSE)
        this.CONTEST_STATUS_REVERSE = Object.assign({}, CONTEST_STATUS_REVERSE)
        this.getContestList()
    },
    methods: {
        currentChange(page) {
            this.currentPage = page
            this.getContestList()
        },
        getContestList() {
            this.loading = true
            contestApi.getContestList(this.pageSize, this.currentPage, this.keyword).then(
                (res) => {
                    this.loading = false
                    this.total = res.data.data.total
                    this.contestList = res.data.data.records
                },
                (err) => {
                    this.loading = false
                }
            )
        },
        goEdit(contestId) {
            this.$router.push({ name: 'admin-edit-contest', params: { contestId } })
        },
        goContestAnnouncement(contestId) {
            this.$router.push({
                name: 'admin-announcement',
                params: { contestId },
            });
        },
        goContestProblemList(contestId) {
            this.$router.push({
                name: 'admin-contest-problem-list',
                params: { contestId },
            })
        },
        goContestStudentInfo(contestId) {
            this.$router.push({
                name: 'admin-contest-user-list',
                params: { contestId },
            })
        },
        deleteContest(contestId) {
            this.$confirm('确认删除？', 'Tips', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }).then(
                () => {
                contestApi.deleteContest(contestId).then(
                    (res) => {
                        myMessage.success('删除成功');
                        this.currentChange(1);
                    });
                },
                () => {}
            );
        },
        filterByKeyword() {
            this.currentChange(1);
        },
    },
    computed: {

    }
}

</script>
<style lang="scss" scoped>
.panel-title {
    font-size: 25px;
    color: #409EFF;
}


.el-tag--dark {
    border-color: #fff;
}
</style>
  