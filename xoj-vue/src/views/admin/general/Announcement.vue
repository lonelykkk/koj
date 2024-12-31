<template>
    <div>
        <el-card>
            <div slot="header">
                <span class="panel-title home-title">
                    {{this.contestID ? '考核通告管理' : '通告管理'}}
                </span>
            </div>
            <div class="create">
                <el-button type="primary" size="small" @click="openAnnouncementDialog(null)" icon="el-icon-plus">
                    创建通告
                </el-button>
            </div>
            <div class="list">
                <vxe-table :loading="loading" ref="table" :data="announcementList" auto-resize stripe>
                    <vxe-table-column min-width="50" field="id" title="ID">
                    </vxe-table-column>
                    <vxe-table-column min-width="150" field="title" show-overflow title="标题">
                    </vxe-table-column>
                    <vxe-table-column min-width="150" field="gmtCreate" title="创建时间">
                        <template v-slot="{ row }">
                            {{ row.gmtCreate | localtime }}
                        </template>
                    </vxe-table-column>
                    <vxe-table-column min-width="150" field="gmtModified" title="修改时间">
                        <template v-slot="{ row }">
                            {{ row.gmtModified | localtime }}
                        </template>
                    </vxe-table-column>
                    <vxe-table-column min-width="150" field="username" show-overflow title="创建者">
                    </vxe-table-column>
                    <vxe-table-column title="Option" min-width="150">
                        <template v-slot="row">
                            <el-tooltip class="item" effect="dark" content="编辑" placement="top">
                                <el-button icon="el-icon-edit-outline" @click.native="openAnnouncementDialog(row.row)"
                                    size="mini" type="primary">
                                </el-button>
                            </el-tooltip>
                            <el-tooltip class="item" effect="dark" content="删除" placement="top">
                                <el-button icon="el-icon-delete-solid" @click.native="deleteAnnouncement(row.row.id)"
                                    size="mini" type="danger">
                                </el-button>
                            </el-tooltip>
                        </template>
                    </vxe-table-column>
                </vxe-table>

                <div class="panel-options">
                    <el-pagination v-if="!contestID" class="page" layout="prev, pager, next" @current-change="currentChange"
                        :page-size="pageSize" :total="total">
                    </el-pagination>
                </div>
            </div>
        </el-card>

        <!--编辑公告对话框-->
        <el-dialog :title="announcementDialogTitle" :visible.sync="showEditAnnouncementDialog" :fullscreen="true"
            @open="onOpenEditDialog">
            <el-form label-position="top" :model="announcement">
                <el-form-item label="标题" required>
                    <el-input v-model="announcement.title" placeholder="请输入标题" class="title-input">
                    </el-input>
                </el-form-item>
                <el-form-item label="请输入内容" required>
                    <Editor :value.sync="announcement.content"></Editor>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="danger" @click.native="showEditAnnouncementDialog = false">取消</el-button>
                <el-button type="primary" @click.native="submitAnnouncement">确定</el-button>
            </span>
        </el-dialog>
    </div>
</template>
  
<script>
import announcementApi from '@/api/announcement'
import contestApi from '@/api/contest'
import myMessage from '@/utils/message'
import { mapGetters } from 'vuex'
const Editor = () => import('@/components/admin/Editor.vue')

export default {
    name: 'announcement',
    components: {
        Editor,
    },
    data() {
        return {
            contestID: '',
            // 显示编辑公告对话框
            showEditAnnouncementDialog: false,
            // 公告列表
            announcementList: [],
            // 一页显示的公告数
            pageSize: 15,
            // 总公告数
            total: 0,
            mode: 'create',
            // 公告 (new | edit) model

            announcement: {
                id: null,
                title: '',
                content: '',
                userId: '',
            },
            // 对话框标题
            announcementDialogTitle: '编辑通告',
            // 是否显示loading
            loading: false,
            // 当前页码
            currentPage: 0,
        };
    },
    mounted() {
        this.init();
    },
    methods: {
        init() {
            this.contestID = this.$route.params.contestId;
            if (this.contestID) {
                this.getContestAnnouncementList(1);
            } else {
                this.getAnnouncementList(1);
            }
        },
        // 切换页码回调
        currentChange(page) {
            this.currentPage = page;
            if (this.contestID) {
                this.getContestAnnouncementList(page);
            } else {
                this.getAnnouncementList(page);
            }
        },

        getAnnouncementList(page) {
            this.loading = true;
            announcementApi.getAnnouncementList(this.pageSize, page).then(
                (res) => {
                    this.loading = false;
                    this.total = res.data.data.total;
                    this.announcementList = res.data.data.records;
                },
                (res) => {
                    this.loading = false;
                }
            );
        },
        getContestAnnouncementList(page) {
            this.loading = true;
            contestApi.getAnnouncementList(this.pageSize, page, this.contestID).then(
                (res) => {
                    this.loading = false;
                    this.total = res.data.data.total;
                    this.announcementList = res.data.data.records;
                })
                .catch(() => {
                    this.loading = false;
                });
        },
        // 打开编辑对话框的回调
        onOpenEditDialog() {
            // todo 优化
            // 暂时解决 文本编辑器显示异常bug
            setTimeout(() => {
                if (document.createEvent) {
                    let event = document.createEvent('HTMLEvents');
                    event.initEvent('resize', true, true);
                    window.dispatchEvent(event);
                } else if (document.createEventObject) {
                    window.fireEvent('onresize');
                }
            }, 0);
        },
        // 提交编辑
        // 默认传入MouseEvent
        submitAnnouncement(data = undefined) {
            let funcName = '';
            if (!data.id) {
                data = this.announcement;
            }
            let requestData;
            if (this.contestID) {
                let announcement = {
                    announcement: data,
                    cid: this.contestID,
                };
                requestData = announcement;
                funcName =
                    this.mode === 'edit'
                        ? 'updateAnnouncement'
                        : 'addAnnouncement';
                contestApi[funcName](requestData).then(
                    (res) => {
                        this.showEditAnnouncementDialog = false;
                        myMessage.success(res.data.message);
                        this.init();
                    }
                ).catch();
            } else {
                funcName =
                    this.mode === 'edit'
                        ? 'updateAnnouncement'
                        : 'addAnnouncement';
                requestData = data;
                announcementApi[funcName](requestData).then(
                    (res) => {
                        this.showEditAnnouncementDialog = false;
                        myMessage.success(res.data.message);
                        this.init();
                    }
                ).catch();
            }
        },

        // 删除公告
        deleteAnnouncement(announcementId) {
            this.$confirm('确认删除？', 'Warning', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            })
                .then(() => {
                    this.loading = true;
                    if (this.contestID) {
                        // 删除考核通告
                        contestApi.deleteAnnouncement(announcementId).then(
                            (res) => {
                                this.loading = false;
                                myMessage.success('删除成功');
                                this.init();
                            }
                        );
                    } else {
                        // 删除普通通告
                        announcementApi.deleteAnnouncement(announcementId).then(
                            (res) => {
                                this.loading = false;
                                myMessage.success('删除成功');
                                this.init();
                            }
                        );
                    }
                })
                .catch(() => {
                    this.loading = false;
                });
        },

        openAnnouncementDialog(row) {
            this.showEditAnnouncementDialog = true;
            if (row !== null) {
                this.announcementDialogTitle = '编辑通告';
                this.announcement = Object.assign({}, row);
                this.mode = 'edit';
            } else {
                this.announcementDialogTitle = '创建通告';
                this.announcement.title = '';
                this.announcement.content = '';
                this.announcement.userId = this.userInfo.id;
                this.announcement.username = this.userInfo.username;
                this.mode = 'create';
            }
        },
        handleVisibleSwitch(row) {
            this.mode = 'edit';
            this.submitAnnouncement({
                id: row.id,
                title: row.title,
                content: row.content,
                userId: row.uid,
            });
        },
    },
    watch: {
        $route() {
            this.init();
        },
    },
    computed: {
        ...mapGetters(['userInfo']),
    },
};
</script>
  
<style lang="scss" scoped>
.title-input {
    margin-bottom: 20px;
}

.visible-box {
    margin-top: 10px;
    width: 205px;
    float: left;
}

.visible-box span {
    margin-right: 10px;
}

.el-form-item {
    margin-bottom: 2px !important;
}

::v-deep .el-dialog__body {
    padding-top: 0 !important;
}

.create {
    margin-bottom: 5px;
}
</style>
  