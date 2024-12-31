<template>
    <div class="view">
        <el-card>
            <div slot="header">
                <span class="panel-title home-title">用户管理</span>
                <div class="filter-row">
                    <span>
                        <el-button type="danger" icon="el-icon-delete-solid" @click="deleteUsers(null)" size="small">
                            删除
                        </el-button>
                    </span>
                    <span>
                        <vxe-input v-model="keyword" placeholder="请输入关键字" type="search" size="medium"
                            @search-click="filterByKeyword" @keyup.enter.native="filterByKeyword">
                        </vxe-input>
                    </span>
                    <span>
                        <el-switch v-model="onlyAdmin" active-text="管理员" :width="40" @change="filterByAdmin"
                            inactive-text="所有">
                        </el-switch>
                    </span>
                </div>
            </div>
            <vxe-table stripe auto-resize :data="userList" ref="xTable" :loading="loadingTable"
                @checkbox-change="handleSelectionChange" @checkbox-all="handlechangeAll">

                <vxe-table-column type="checkbox" width="60"></vxe-table-column>
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
                <vxe-table-column field="email" title="邮箱" min-width="140" show-overflow></vxe-table-column>
                <vxe-table-column field="gmtCreate" title="注册时间" min-width="100">
                    <template v-slot="{ row }">
                        {{ row.gmtCreate | localtime }}
                    </template>
                </vxe-table-column>
                <vxe-table-column field="role" title="用户角色" min-width="50">
                    <template v-slot="{ row }">
                        {{ getRole(row.roles) | parseRole }}
                    </template>
                </vxe-table-column>
                <vxe-table-column field="status" title="状态" min-width="50">
                    <template v-slot="{ row }">
                        <el-tag effect="dark" color="#19be6b" v-if="row.isDisabled == 0">
                            正常
                        </el-tag>
                        <el-tag effect="dark" color="#ed3f14" v-else>
                            封禁
                        </el-tag>
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
                            <el-button icon="el-icon-delete-solid" size="mini" @click.native="deleteUsers([row.uid])"
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
        <el-dialog title="用户" :visible.sync="showUserDialog" width="350px">
            <el-form :model="selectUser" label-width="100px" label-position="left" :rules="updateUserRules"
                ref="updateUser">
                <el-row :gutter="10">
                    <el-col :span="24">
                        <el-form-item label="用户名" required prop="username">
                            <el-input v-model="selectUser.username" size="small"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="真实姓名" prop="realname">
                            <el-input v-model="selectUser.realname" size="small"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="学号" prop="number">
                            <el-input v-model="selectUser.number" size="small"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="班级" prop="classe">
                            <el-input v-model="selectUser.classe" size="small"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="性别">
                            <el-switch :active-value="1" :inactive-value="2" active-text="女" inactive-text="男"
                                v-model="selectUser.sex">
                            </el-switch>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="邮箱" prop="email">
                            <el-input v-model="selectUser.email" size="small"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="设置新密码">
                            <el-switch :active-value="true" :inactive-value="false" v-model="selectUser.setNewPwd">
                            </el-switch>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-if="selectUser.setNewPwd == 1">
                        <el-form-item label="输入新密码" required prop="password">
                            <el-input v-model="selectUser.password" placeholder="输入新密码" size="small">
                            </el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="用户类型">
                            <el-select v-model="selectUser.type" size="small">
                                <el-option label="超级管理员" :value="1" :key="1"></el-option>
                                <el-option label="普通管理员" :value="2" :key="2"></el-option>
                                <el-option label="用户(默认)" :value="3" :key="3"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="状态">
                            <el-switch :active-value="false" :inactive-value="true" active-text="正常" inactive-text="封禁"
                                v-model="selectUser.isDisabled">
                            </el-switch>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="danger" @click.native="showUserDialog = false">
                    取消
                </el-button>
                <el-button type="primary" @click.native="saveUser">
                    确定
                </el-button>
            </span>
        </el-dialog>
    </div>
</template>
  
<script>
import api from '@/api/userinfo'
import loingApi from '@/api/login'
import utils from '@/utils/utils'
import myMessage from '@/utils/message'

export default {
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
            selectedUsers: [],
            userList: [],
            // 搜索关键字
            keyword: '',
            // 是否显示用户对话框
            showUserDialog: false,
            onlyAdmin: false,

            // 当前用户model
            selectUser: {
                id: '',
                username: '',
                realname: '',
                email: '',
                password: '',
                type: 3,
                number: '',
                classe: '',
                sex: '',
                isDisabled: false,
                setNewPwd: false,
            },
            updateUserRules: {
                username: [
                    { required: true, message: 'Username is required', trigger: 'blur' },
                    {
                        max: 20,
                        message: '用户名长度不能超过20位',
                        trigger: 'blur',
                    },
                ],
                realname: [
                    {
                        max: 255,
                        trigger: 'blur',
                    },
                ],
                email: [
                    {
                        type: 'email',
                        message: '邮箱格式不正确',
                        trigger: 'blur',
                    },
                ],
            },
            loadingTable: false
        };
    },
    mounted() {
        this.getUserList(1);
    },
    methods: {
        // 切换页码回调
        currentChange(page) {
            this.currentPage = page;
            this.getUserList(page);
        },
        onPageSizeChange(pageSize) {
            this.pageSize = pageSize;
            this.getUserList(this.currentPage);
        },
        // 提交修改用户的信息
        saveUser() {
            this.$refs['updateUser'].validate((valid) => {
                if (valid) {
                    api.editUser(this.selectUser).then(
                        (res) => {
                            // 更新列表
                            myMessage.success('修改成功');
                            this.getUserList(this.currentPage);
                        })
                        .then(() => {
                            this.showUserDialog = false;
                        })
                        .catch(() => { });
                }
            });
        },
        filterByKeyword() {
            this.currentChange(1);
        },
        filterByAdmin() {
            this.currentChange(1);
        },
        getRole(roles) {
            return roles[0]['id'];
        },
        // 打开用户对话框
        openUserDialog(row) {
            this.showUserDialog = true
            this.selectUser.id = row.id
            this.selectUser.username = row.username
            this.selectUser.realname = row.realname
            this.selectUser.email = row.email
            this.selectUser.classe = row.classe
            this.selectUser.number = row.number
            this.selectUser.sex = row.sex
            this.selectUser.setNewPwd = false
            this.selectUser.password = ''
            this.selectUser.type = this.getRole(row.roles)
            this.selectUser.isDisabled = row.isDisabled
        },
        // 获取用户列表
        getUserList(page) {
            this.loadingTable = true;
            api.getUserList(page, this.pageSize, this.keyword, this.onlyAdmin).then(
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
        deleteUsers(ids) {
            if (!ids) {
                ids = this.selectedUsers;
            }
            if (ids.length > 0) {
                this.$confirm('确认删除？', 'Tips', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }).then(
                    () => {
                        api.deleteUsers(ids).then(
                            (res) => {
                                myMessage.success('删除成功');
                                this.selectedUsers = [];
                                this.getUserList(this.currentPage);
                            })
                            .catch(() => {
                                this.selectedUsers = [];
                                this.getUserList(this.currentPage);
                            });
                    },
                    () => { }
                );
            } else {
                myMessage.warning('选择的用户不能为空')
            }
        },
        // 用户表部分勾选 改变选中的内容
        handleSelectionChange({ records }) {
            this.selectedUsers = [];
            for (let num = 0; num < records.length; num++) {
                this.selectedUsers.push(records[num].uid);
            }
        },
        checCheckboxkMethod({ row }) {
            return row.uid != this.userInfo.uid;
        },
        // 一键全部选中，改变选中的内容列表
        handlechangeAll() {
            let userList = this.$refs.xTable.getCheckboxRecords();
            this.selectedUsers = [];
            for (let num = 0; num < userList.length; num++) {
                this.selectedUsers.push(userList[num].uid);
            }
        },
    },
    computed: {
        selectedUserIDs() {
            let ids = [];
            for (let user of this.selectedUsers) {
                ids.push(user.id);
            }
            return ids;
        },
        userInfo() {
            return this.$store.getters.userInfo;
        },
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
  