<template>
    <div class="view">
        <el-card>
            <div slot="header">
                <span class="panel-title home-title">参加考核用户</span>
                <div class="filter-row">
                    <span>
                        <vxe-input v-model="keyword" placeholder="请输入关键字" type="search" size="medium"
                            @search-click="filterByKeyword" @keyup.enter.native="filterByKeyword">
                        </vxe-input>
                    </span>
                </div>
            </div>
            <vxe-table stripe auto-resize :data="userList" ref="xTable" :loading="loadingTable">

                <vxe-table-column field="username" title="用户" min-width="50" show-overflow>
                    <template v-slot="{ row }">
                        <span>{{ row.username }}</span>
                    </template>
                </vxe-table-column>
                <vxe-table-column field="realname" title="真实姓名" min-width="50" show-overflow>
                </vxe-table-column>
                <vxe-table-column field="number" title="学号" min-width="70" show-overflow>
                </vxe-table-column>
                <vxe-table-column field="classe" title="班级" min-width="50" show-overflow>
                </vxe-table-column>
                <vxe-table-column field="sex" title="性别" min-width="10" show-overflow>
                    <template v-slot="{ row }">
                        {{ row.sex === 1 ? '女' : '男' }}
                    </template>
                </vxe-table-column>
                <vxe-table-column field="isCorrection" title="批改状态" min-width="50" show-overflow>
                    <template v-slot="{ row }">
                        {{ row.isCorrection == 1 ? '已批改' : '未批改' }}
                    </template>
                </vxe-table-column>

                <vxe-table-column title="操作" min-width="150">
                    <template v-slot="{ row }">
                        <el-tooltip effect="dark" content="编辑" placement="top">
                            <el-button icon="el-icon-edit-outline" size="mini" @click.native="openUserDialog(row)"
                                type="primary">
                            </el-button>
                        </el-tooltip>
                        <el-tooltip effect="dark" content="删除" placement="top">
                            <el-button icon="el-icon-delete-solid" size="mini" @click.native="deleteUserCorrection(row)"
                                type="danger">
                            </el-button>
                        </el-tooltip>
                    </template>
                </vxe-table-column>
            </vxe-table>
            <div class="panel-options">
                <el-pagination class="page" layout="prev, pager, next, sizes" @current-change="currentChange"
                    :page-size="pageSize" :total="total" @size-change="onPageSizeChange" :page-sizes="[10, 30, 50, 100]">
                </el-pagination>
            </div>
        </el-card>


        <!--编辑用户的对话框-->
        <el-dialog :title="this.nowUsername" :visible.sync="showUserDialog" :fullscreen="true">
            <el-row :gutter="10">
                <el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12">
                    <template v-if="!codes.length">
                        该用户未提交代码
                    </template>
                    <el-card v-for="(judge, index) in codes" :key="index" shadow="hover">
                        <span>题目：{{ judge.displayId }}</span>
                        <el-divider></el-divider>
                        <Markdown v-if="judge.code" :isAvoidXss="true" :content="judge.code">
                        </Markdown>
                    </el-card>
                    <!-- <pre v-highlight="code"><code></code></pre> -->
                </el-col>
                <el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12">
                    <Editor :value.sync="content"></Editor>
                </el-col>
            </el-row>
            <span slot="footer" class="dialog-footer">
                <el-button type="danger" @click.native="showUserDialog = false">
                    取消
                </el-button>
                <el-button type="primary" @click.native="saveUser">
                    保存
                </el-button>
            </span>
        </el-dialog>
    </div>
</template>
  
<script>
import utils from '@/utils/utils'
import myMessage from '@/utils/message'
import contestApi from '@/api/contest'
import Markdown from '@/components/front/common/Markdown'
import contest from '@/store/contest'
import { addCodeBtn } from '@/utils/codeblock'
const Editor = () => import('@/components/admin/Editor.vue')
export default {
    components: {
        Editor,
        Markdown
    },
    name: 'user',
    data() {
        return {
            // 一页显示的用户数
            pageSize: 10,
            // 用户总数
            total: 0,
            // 数据库查询的用户列表
            // 当前页码
            currentPage: 1,
            userList: [],
            // 搜索关键字
            keyword: '',
            // 是否显示用户对话框
            showUserDialog: false,
            contestId: 0,
            // 当前用户model
            nowUsername: '',
            nowUserId: '',
            codes: [],
            content: '',
            correction: {},
            loadingTable: false,
            saveOrUpdate: 'save',
        };
    },
    mounted() {
        this.init()
    },
    methods: {
        init() {
            this.contestId = this.$route.params.contestId
            this.getUserList(1)
        },
        // 提交修改用户的信息
        saveUser() {
            if (this.saveOrUpdate === 'save') {
                let data = {
                    userId: this.nowUserId,
                    cid: this.contestId,
                    content: this.content
                }
                contestApi.addContestCorrection(data).then(
                    (res) => {
                        this.showUserDialog = false
                        myMessage.success('保存成功')
                        this.keyword = ''
                        this.getUserList(1)
                    },
                    (err) => {

                    }
                )
            } else {
                this.correction.content = this.content
                contestApi.updateContestCorrection(this.correction).then(
                    (res) => {
                        this.showUserDialog = false
                        myMessage.success('修改成功')
                        this.keyword = ''
                        this.getUserList(1)
                    },
                    (err) => {

                    }
                )
            }
        },
        filterByKeyword() {
            this.currentChange(1);
        },
        openUserDialog(row) {
            contestApi.getContestCorrectionInfo(this.contestId, row.id).then(
                (res) => {
                    this.codes = res.data.data.codes
                    for (let i = 0; i < this.codes.length; i ++) {
                        let tmp = this.codes[i].code
                        this.codes[i].code = `\`\`\` ${tmp}`
                    }
                    this.$nextTick((_) => {
                        addCodeBtn()
                    })
                    if (res.data.data.contestCorrection != undefined) {
                        this.saveOrUpdate = 'update'
                        this.content = res.data.data.contestCorrection.content
                        this.correction = res.data.data.contestCorrection
                    }
                },
                (err) => {

                }
            )
            this.nowUsername = row.classe + ' ' + row.realname + '的代码'
            this.nowUserId = row.id
            this.showUserDialog = true
        },
        // 获取用户列表
        getUserList(page) {
            this.loadingTable = true;
            let query = {
                limit: this.pageSize,
                currentPage: page,
                keyword: this.keyword,
                contestId: this.contestId
            }
            contestApi.getContestStudentInfo(query).then(
                (res) => {
                    this.loadingTable = false;
                    this.total = res.data.data.total;
                    this.userList = res.data.data.records;
                },
                (res) => {
                    this.loadingTable = false;
                }
            );
        },
        deleteUserCorrection(row) {
            if (row.isCorrection) {
                this.$confirm('确认删除？', 'Tips', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }).then(
                    () => {
                        contestApi.deleteContestCorrectionInfo(row.contestCorrection.id).then(
                            (res) => {
                                myMessage.success('删除成功');
                                this.content = ''
                                this.codes = []
                                this.getUserList(this.currentPage);
                            })
                            .catch(() => {
                                this.getUserList(this.currentPage);
                            });
                    },
                    () => { }
                );
            } else {
                myMessage.warning('未批改该用户考核！无法删除！')
            }
        },
        // 切换页码回调
        currentChange(page) {
            this.currentPage = page;
            this.getUserList(page);
        },
        onPageSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.getUserList(this.currentPage);
        },
    },
    computed: {

    }
}

</script>
  
<style lang="scss" scoped>
.import-user-icon {
    color: #555555;
    margin-left: 4px;
}

.userPreview {
    padding-left: 10px;
}

::v-deep .el-tag--dark {
    border-color: #fff;
}

::v-deep .el-dialog__body {
    padding-bottom: 0;
}

::v-deep .el-form-item {
    margin-bottom: 10px !important;
}

.notification p {
    margin: 0;
    text-align: left;
}

.filter-row {
    margin-top: 10px;
}

@media screen and (max-width: 768px) {
    .filter-row span {
        margin-right: 5px;
    }
}

@media screen and (min-width: 768px) {
    .filter-row span {
        margin-right: 20px;
    }
}
</style>
  