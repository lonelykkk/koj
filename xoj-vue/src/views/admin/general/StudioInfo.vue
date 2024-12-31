<template>
    <div>
        <el-card>
            <div slot="header" class="clearfix" shadow="always">
                <span class="panel-title">成员信息管理</span>
                <br>
                <el-row :gutter="20">
                    <el-col :span="3">
                        <el-button style="margin-top: 10px;" type="primary" size="small" @click="openTagDialog('add', null)"
                            icon="el-icon-plus">添加成员信息</el-button>
                    </el-col>
                    <el-col :span="5">
                        <el-input style="margin-top: 10px;" size="medium" v-model="keyword" placeholder=""
                            @keyup.enter.native="getByKeyword">
                            <el-button slot="append" icon="el-icon-search" @click="getByKeyword"></el-button>
                        </el-input>
                    </el-col>
                </el-row>
            </div>
            <div v-if="studioInfoList.length == 0">
                <el-empty description="暂无成员信息"></el-empty>
            </div>
            <div v-else>
                <el-table :data="studioInfoList" style="width: 100%">
                    <el-table-column label="姓名" width="100">
                        <template slot-scope="scope">
                            <span>{{ scope.row.name }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="届数" width="150">
                        <template slot-scope="scope">
                            <span>{{ scope.row.whichSession }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="班级" width="150">
                        <template slot-scope="scope">
                            <span>{{ scope.row.classes }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="QQ" width="150">
                        <template slot-scope="scope">
                            <span>{{ scope.row.qq }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="考研院校" width="150">
                        <template slot-scope="scope">
                            <span>{{ scope.row.school }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="就业公司" width="180">
                        <template slot-scope="scope">
                            <span>{{ scope.row.company }}</span>
                        </template>
                    </el-table-column>

                    <el-table-column label="操作" min-width="150">
                        <template slot-scope="scope">
                            <el-button size="mini" @click="openTagDialog('edit', scope.row)">编辑</el-button>
                            <el-button size="mini" type="danger"
                                @click="deleteStudioInfo(scope.$index, scope.row)">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="page-container">
                    <el-pagination background @current-change="handleCurrentChange" :current-page.sync="currentPage"
                        layout="total, prev, pager, next" :total="total" :page-size="pageSize">
                    </el-pagination>
                </div>
            </div>
        </el-card>

        <el-dialog :title="dialogTitle" :visible.sync="showEditDialog" width="40%" center>
            <el-form>
                <el-form-item label="姓名" prop="name">
                    <el-input v-model="studioInfo.name"></el-input>
                </el-form-item>
                <el-form-item label="届数" prop="whichSession">
                    <el-input v-model="studioInfo.whichSession"></el-input>
                </el-form-item>
                <el-form-item label="班级" prop="classes">
                    <el-input v-model="studioInfo.classes"></el-input>
                </el-form-item>
                <el-form-item label="QQ" prop="qq">
                    <el-input v-model="studioInfo.qq"></el-input>
                </el-form-item>
                <el-form-item label="考研院校" prop="school">
                    <el-input v-model="studioInfo.school"></el-input>
                </el-form-item>
                <el-form-item label="就业公司" prop="company">
                    <el-input v-model="studioInfo.company"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="upsertStudioInfo()">添加</el-button>
                    <el-button @click="showEditDialog = false">取消</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>
    </div>
</template>
  
<script>
import myMessage from '@/utils/message'
import studioInfoApi from '@/api/studioInfo'
import { mapGetters } from 'vuex'

export default {
    name: 'StudioInfo',
    components: {

    },
    data() {
        return {
            pageSize: 10,
            total: 0,
            currentPage: 0,
            keyword: '',
            studioInfoList: [],
            studioInfo: {
                id: '',
                name: '',
                whichSession: 0,
                classes: '',
                qq: '',
                school: '',
                company: '',
                isTeacher: 0,
            },
            showEditDialog: false,
            dialogTitle: '添加成员信息',
        }
    },
    mounted() {
        this.init()
    },
    methods: {
        init() {
            this.getStudioInfoList()
        },
        getStudioInfoList() {
            studioInfoApi.getMemberList(this.pageSize, this.currentPage, this.keyword).then(
                (res) => {
                    this.studioInfoList = res.data.data.records
                    this.total = res.data.data.total
                },
                (err) => {

                }
            )
        },
        getByKeyword() {
            this.pageSize = 10
            this.currentPage = 1
            this.getStudioInfoList()
        },
        upsertStudioInfo() {
            if (this.studioInfo.id) {
                this.studioInfo.whichSession = parseInt(this.studioInfo.whichSession)
                studioInfoApi.updateStudioInfo(this.studioInfo).then(
                    (res) => {
                        myMessage.success('修改成功')
                        this.studioInfo = {}
                        this.keyword = ''
                        this.currentPage = 1
                        this.getStudioInfoList()
                        this.showEditDialog = false
                    },
                    (err) => {
                        myMessage.error('修改失败')
                        this.showEditDialog = false
                    }
                )
            } else {
                studioInfoApi.addStudioInfo(this.studioInfo).then(
                    (res) => {
                        myMessage.success('添加成功')
                        this.studioInfo = {}
                        this.keyword = ''
                        this.currentPage = 1
                        this.getStudioInfoList()
                        this.showEditDialog = false
                    },
                    (err) => {
                        myMessage.error('添加失败')
                        this.showEditDialog = false
                    }
                )
            }
        },
        deleteStudioInfo(index, row) {
            this.$confirm('确认删除？', 'Tips', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }).then(
                () => {
                    studioInfoApi.deleteStudioInfo(row.id).then(
                        (res) => {
                            myMessage.success("删除成功！")
                            this.currentPage = 1
                            this.total = 0
                            this.getStudioInfoList()
                        },
                        (err) => {
                            myMessage.error("删除失败！")
                        }
                    )
                },
                () => { }
            );
        },
        handleCurrentChange(page) {
            this.currentPage = page
            this.getStudioInfoList()
        },
        openTagDialog(action, studioInfo) {
            if (action == 'add') {
                this.dialogTitle = '添加成员信息'
                this.studioInfo = {}
                this.studioInfo.isTeacher = 0
            } else {
                this.dialogTitle = '修改成员信息'

                this.studioInfo.id = studioInfo.id
                this.studioInfo.name = studioInfo.name
                this.studioInfo.whichSession = studioInfo.whichSession
                this.studioInfo.classes = studioInfo.classes
                this.studioInfo.qq = studioInfo.qq
                this.studioInfo.school = studioInfo.school
                this.studioInfo.company = studioInfo.company
                this.studioInfo.isTeacher = studioInfo.isTeacher
            }
            this.showEditDialog = true
        }
    },
    computed: {
        ...mapGetters(['userInfo']),
    },
}

</script>
<style lang="scss" scoped>


</style>
  