<template>
    <div class="problem-list-container">
        <el-card class="box-card">
            <div slot="header" class="clearfix" shadow="always">
                <span class="panel-title">
                    {{ query.contestId ? '考核题目列表' : '题目列表' }}
                </span>
                <br>
                <div>
                    <el-row :gutter="20">
                        <span v-if="query.contestId">
                            <el-col :span="3">
                                <el-button style="margin-top: 10px;" type="primary" size="small" icon="el-icon-plus"
                                    @click="addProblemDialogVisible = true">
                                    从题库中添加题目
                                </el-button>
                            </el-col>
                        </span>
                        <span v-else>
                            <el-col :span="2">
                                <router-link :to="'/admin/problem/create'">
                                    <el-button style="margin-top: 10px;" type="primary" size="small" icon="el-icon-plus">
                                        添加题目
                                    </el-button>
                                </router-link>
                            </el-col>
                        </span>
                        <el-col :span="8">
                            <el-input style="margin-top: 10px;" size="medium" v-model="query.keyword" placeholder=""
                                @keyup.enter.native="getByKeyword">
                                <el-button slot="append" icon="el-icon-search" @click="getByKeyword"></el-button>
                            </el-input>
                        </el-col>

                        <el-col :span="5">
                            <el-select v-model="query.auth" @change="getByAuth" size="medium"
                                style="width: 180px; margin-top: 10px;">
                                <el-option label="所有题目" :value="0"></el-option>
                                <el-option label="公开题目" :value="1"></el-option>
                                <el-option label="私有题目" :value="2"></el-option>
                                <el-option label="考核题目" :value="3"></el-option>
                            </el-select>
                        </el-col>
                    </el-row>
                </div>
            </div>
            <div>
                <el-table :data="problemList" style="width: 100%">
                    <el-table-column label="ID" width="80">
                        <template slot-scope="scope">
                            <span>{{ scope.row.id }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="展示ID" width="100">
                        <template slot-scope="scope">
                            <span>{{ scope.row.problemId }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="标题" width="150">
                        <template slot-scope="scope">
                            <span>{{ scope.row.title }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="作者" width="80">
                        <template slot-scope="scope">
                            <span>{{ scope.row.author }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="类型" width="80">
                        <template slot-scope="scope">
                            <span>{{ scope.row.type == 0 ? 'ACM' : 'OI' }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="创建时间" width="180">
                        <template slot-scope="scope">
                            <span>{{ scope.row.gmtCreate }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="题目权限" width="150">
                        <template v-slot="{ row }">
                            <el-select v-model="row.auth" @change="changeProblemAuth(row)" size="small">
                                <el-option label="公开题目" :value="1"></el-option>
                                <el-option label="私有题目" :value="2"></el-option>
                                <el-option label="考核题目" :value="3"></el-option>
                            </el-select>
                        </template>
                    </el-table-column>
                    <el-table-column label="最近修改者" width="100">
                        <template slot-scope="scope">
                            <span>{{ scope.row.modifiedUser != '' ? scope.row.modifiedUser : '无' }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作">
                        <template slot-scope="scope">
                            <el-button size="mini" @click="editProblem(scope.row.id)">编辑</el-button>
                            <el-button size="mini" type="danger"
                                @click="deleteProblem(scope.$index, scope.row)">删除</el-button>
                            <el-button size="mini" type="primary"
                                @click="updateProblemDisplay(scope.row)">修改显示ID</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="page-container">
                    <el-pagination background @current-change="handleCurrentChange" :current-page.sync="query.currentPage"
                        layout="total, prev, pager, next" :total="total">
                    </el-pagination>
                </div>

            </div>
        </el-card>

        <div v-if="addProblemDialogVisible">
            <el-dialog title="添加考核题目" v-if="query.contestId" width="90%" :visible.sync="addProblemDialogVisible"
                :close-on-click-modal="false">
                <AddPublicProblem :contestID="query.contestId" @on-change="getAllProblem"></AddPublicProblem>
            </el-dialog>
        </div>

    </div>
</template>
  
<script>
import problemApi from '@/api/problem'
import contestApi from '@/api/contest'
import myMessage from '@/utils/message'
import AddPublicProblem from '@/components/admin/AddPublicProblem.vue'
import utils from '@/utils/utils'


export default {
    name: 'ProblemList',
    components: {
        AddPublicProblem,
    },
    created() {
        console.log('problem list created')
        this.init()
    },
    data() {
        return {
            routeName: '',
            total: 0,
            query: {
                auth: 0,
                limit: 10,
                keyword: '',
                currentPage: 1,
                contestId: null
            },
            problemList: [],
            addProblemDialogVisible: false,
            loading: false
        }
    },
    methods: {
        init() {
            this.routeName = this.$route.name
            this.getAllProblem()
        },
        changeProblemAuth(row) {
            console.log('changeProblemAuth');
        },
        editProblem(problemId) {
            // console.log(index, row);
            if (this.routeName === 'admin-problem-list') {
                this.$router.push({
                    name: 'admin-edit-problem',
                    params: {problemId},
                    query: {
                        back: this.$route.fullPath
                    }
                })
            } else if (this.routeName === 'admin-contest-problem-list') {
                this.$router.push({
                    name: 'admin-edit-contest-problem',
                    params: {
                        problemId: problemId,
                        contestId: this.query.contestId
                    }
                })
            }
            // this.$router.push({path: '', params: {problemrow}})
        },
        updateProblemDisplay(row) {
            this.$prompt('请输入该题目在考核中展示ID', 'Tips', { confirmButtonText: '确定', cancelButtonText: '取消', inputValue: row.problemId })
                .then(
                    ({ value }) => {
                        let data = {
                            pid: row.id,
                            cid: this.query.contestId,
                            displayId: value,
                        };

                        contestApi.updateProblem(data).then(
                            (res) => {
                                myMessage.success('修改成功')
                                this.query.page = 1
                                this.getAllProblem()
                            },
                            () => { }
                        );
                    },
                    () => { }
                )
        },
        deleteProblem(index, row) {
            this.$confirm('确认删除？', 'Tips', {
                type: 'warning',
            }).then(
                () => {
                    if (this.$route.name === 'admin-contest-problem-list') {
                        this.query.contestId = this.$route.params.contestId
                        contestApi.removeProblem(this.query.contestId, row.id).then(
                            (res) => {
                                myMessage.success(res.data.message)
                                this.getAllProblem()
                            }
                        ).catch(() => { })
                    } else {
                        problemApi.deleteProblem(row.id).then(
                            (res) => {
                                myMessage.success(res.data.message)
                                this.getAllProblem()
                            }).catch(() => { });
                    }
                },
                () => { }
            );
        },
        getByKeyword() {
            this.query.limit = 10
            this.query.currentPage = 1
            problemApi.getAllProblem(this.query).then(
                (res) => {
                    this.problemList = res.data.data.records
                    this.total = res.data.data.total
                }
            )
        },
        getByAuth() {
            this.query.limit = 10
            this.query.currentPage = 1
            problemApi.getAllProblem(this.query).then(
                (res) => {
                    this.problemList = res.data.data.records
                    this.total = res.data.data.total
                }
            )
        },
        getAllProblem() {
            if (this.$route.name === 'admin-contest-problem-list') {
                this.query.contestId = this.$route.params.contestId
                contestApi.getProblemList(this.query).then(
                    (res) => {
                        this.problemList = res.data.data.records
                        this.total = res.data.data.total
                    }
                )
            } else {
                problemApi.getAllProblem(this.query).then(
                    (res) => {
                        this.problemList = res.data.data.records
                        this.total = res.data.data.total
                    }
                )
            }
        },
        handleCurrentChange(page) {
            this.query.currentPage = page
            problemApi.getAllProblem(this.query).then(
                (res) => {
                    this.problemList = res.data.data.records
                    this.total = res.data.data.total
                }
            )
        }
    }
}

</script>
  
<style lang="scss">
.problem-list-container {
    background-color: #FFF;

    .panel-title {
        font-size: 25px;
        color: #409EFF;
    }
}

.page-container {
    margin-top: 15px;
}
</style>