<template>
    <div>
        <el-card>
            <div slot="header">
                <span class="panel-title home-title">贴子管理</span>
                <div class="filter-row">
                    <span>
                        <el-button type="danger" icon="el-icon-delete-solid" @click="deleteDiscussion(null)" size="small">
                            删除
                        </el-button>
                    </span>
                    <span>
                        <vxe-input v-model="keyword" placeholder="请输入" type="search" size="medium"
                            @search-click="filterByKeyword" @keyup.enter.native="filterByKeyword">
                        </vxe-input>
                    </span>
                </div>
            </div>
            <vxe-table stripe auto-resize :data="discussionList" ref="xTable" align="center"
                :loading="discussionLoadingTable" :checkbox-config="{ highlight: true, range: true }"
                @checkbox-change="handleSelectionChange" @checkbox-all="handlechangeAll">
                <vxe-table-column type="checkbox" width="60"></vxe-table-column>
                <vxe-table-column field="id" title="ID" width="60"></vxe-table-column>
                <vxe-table-column field="title" title="标题" show-overflow min-width="150"></vxe-table-column>
                <vxe-table-column field="author" title="发布者" min-width="150" show-overflow></vxe-table-column>
                <vxe-table-column field="likeNum" title="点赞数" min-width="96"></vxe-table-column>
                <vxe-table-column field="viewNum" title="浏览数" min-width="96"></vxe-table-column>
                <vxe-table-column field="gmtCreate" title="发布时间" min-width="150">
                    <template v-slot="{ row }">
                        {{ row.gmtCreate | localtime }}
                    </template>
                </vxe-table-column>
                <vxe-table-column field="isDisabled" title="状态" min-width="100">
                    <template v-slot="{ row }">
                        <el-select v-model="row.isDisabled" @change="changeDiscussionStatus(row)" size="small">
                            <el-option label="正常" :value="false" :key="0"></el-option>
                            <el-option label="禁用" :value="true" :key="1"></el-option>
                        </el-select>
                    </template>
                </vxe-table-column>
                <vxe-table-column min-width="100" field="topPriority" title="是否置顶">
                    <template v-slot="{ row }">
                        <el-switch v-model="row.topPriority" active-text="" inactive-text="" :active-value="true"
                            :inactive-value="false" @change="handleTopSwitch(row)">
                        </el-switch>
                    </template>
                </vxe-table-column>
                <vxe-table-column title="操作" min-width="130">
                    <template v-slot="{ row }">
                        <el-tooltip effect="dark" content="删除" placement="top">
                            <el-button icon="el-icon-delete-solid" size="mini" @click.native="deleteDiscussion([row.id])"
                                type="danger">
                            </el-button>
                        </el-tooltip>
                        <el-tooltip effect="dark" content="查看讨论详情" placement="top">
                            <el-button icon="el-icon-search" size="mini" @click.native="toDiscussion(row.id)"
                                type="primary">
                            </el-button>
                        </el-tooltip>
                    </template>
                </vxe-table-column>
            </vxe-table>
            <div class="panel-options">
                <el-pagination class="page" layout="prev, pager, next" @current-change="discussionCurrentChange"
                    :page-size="pageSize" :total="discussionTotal">
                </el-pagination>
            </div>
        </el-card>

        <el-card style="margin-top:20px">
            <div slot="header">
                <span class="panel-title home-title">
                    举报管理
                </span>
            </div>
            <vxe-table :loading="discussionReportLoadingTable" ref="table" align="center" :data="discussionReportList"
                auto-resize stripe>
                <vxe-table-column min-width="60" field="id" title="ID">
                </vxe-table-column>
                <vxe-table-column min-width="100" field="discussionId" title="贴子ID">
                </vxe-table-column>
                <vxe-table-column field="discussionTitle" title="标题" show-overflow min-width="150">
                </vxe-table-column>
                <vxe-table-column field="discussionAuthor" title="发布者" min-width="150" show-overflow>
                </vxe-table-column>
                <vxe-table-column min-width="150" field="reporter" show-overflow title="举报者">
                </vxe-table-column>
                <vxe-table-column min-width="150" field="gmtCreate" title="举报时间">
                    <template v-slot="{ row }">
                        {{ row.gmtCreate | localtime }}
                    </template>
                </vxe-table-column>
                <vxe-table-column min-width="100" field="status" title="是否检查">
                    <template v-slot="{ row }">
                        <el-switch v-model="row.status" active-text="" inactive-text="" :active-value="true"
                            :inactive-value="false" @change="handleCheckedSwitch(row)">
                        </el-switch>
                    </template>
                </vxe-table-column>
                <vxe-table-column title="操作" min-width="150">
                    <template v-slot="{ row }">
                        <el-tooltip class="item" effect="dark" content="查看举报内容" placement="top">
                            <el-button icon="el-icon-document" @click.native="openReportDialog(row.content)" size="mini" type="success">
                            </el-button>
                        </el-tooltip>
                        <el-tooltip effect="dark" content="查看贴子" placement="top">
                            <el-button icon="el-icon-search" size="mini" @click.native="toDiscussion(row.did)" type="primary">
                            </el-button>
                        </el-tooltip>
                    </template>
                </vxe-table-column>
            </vxe-table>

            <div class="panel-options">
                <el-pagination class="page" layout="prev, pager, next" @current-change="discussionReportCurrentChange"
                    :page-size="pageSize" :total="discussionReportTotal">
                </el-pagination>
            </div>
        </el-card>
    </div>
</template>
  
<script>
import myMessage from '@/utils/message'
import discussionApi from '@/api/discussion'
export default {
    name: 'discussion',
    data() {
        return {
            pageSize: 10,
            discussionTotal: 0,
            discussionList: [],
            discussionCurrentPage: 1,
            selectedDiscussions: [],
            keyword: '',
            discussionLoadingTable: false,

            discussionReportTotal: 0,
            discussionReportList: [],
            discussionReportCurrentPage: 1,
            discussionReportLoadingTable: false,
        }
    },
    mounted() {
        this.getDiscussionList(1);
        this.getDiscussionReportList();
    },
    methods: {
        discussionCurrentChange(page) {
            this.discussionCurrentPage = page;
            this.getDiscussionList(page);
        },
        discussionReportCurrentChange(page) {
            this.discussionReportCurrentPage = page;
            this.getDiscussionReportList();
        },
        getDiscussionList(page) {
            this.discussionLoadingTable = true;
            let searchParams = {
                limit: this.pageSize,
                currentPage: page,
                keyword: this.keyword,
                isAdmin: true
            };
            discussionApi.getDiscussionList(searchParams).then(
                (res) => {
                    this.discussionLoadingTable = false;
                    this.discussionTotal = res.data.data.total;
                    this.discussionList = res.data.data.records;
                },
                (res) => {
                    this.discussionLoadingTable = false;
                }
            );
        },
        getDiscussionReportList() {
            this.discussionReportLoadingTable = true;
            discussionApi.adminGetReportList(
                    this.discussionReportCurrentPage,
                    this.pageSize
                ).then(
                    (res) => {
                        this.discussionReportLoadingTable = false;
                        this.discussionReportList = res.data.data.records;
                        this.discussionReportTotal = res.data.data.total;
                    },
                    (err) => {
                        this.discussionReportLoadingTable = false;
                    }
                );
        },
        filterByKeyword() {
            this.discussionCurrentChange(1);
            this.keyword = '';
        },
        // 用户表部分勾选 改变选中的内容
        handleSelectionChange({ records }) {
            this.selectedDiscussions = [];
            for (let num = 0; num < records.length; num++) {
                this.selectedDiscussions.push(records[num].id);
            }
        },
        // 一键全部选中，改变选中的内容列表
        handlechangeAll() {
            let discussion = this.$refs.xTable.getCheckboxRecords();
            this.selectedDiscussions = [];
            for (let num = 0; num < discussion.length; num++) {
                this.selectedDiscussions.push(discussion[num].id);
            }
        },
        changeDiscussionStatus(row) {
            let discussion = {
                id: row.id,
                isDisabled: row.isDisabled,
            };
            discussionApi.adminUpdateDiscussion(discussion).then(
                (res) => {
                    myMessage.success('修改成功');
                }
            );
        },
        handleTopSwitch(row) {
            let discussion = {
                id: row.id,
                topPriority: row.topPriority,
            };
            discussionApi.adminUpdateDiscussion(discussion).then(
                (res) => {
                    myMessage.success('修改成功');
                }
            );
        },

        handleCheckedSwitch(row) {
            let discussionReport = {
                id: row.id,
                status: row.status,
            };
            discussionApi.adminupdateDiscussionReport(discussionReport).then(
                (res) => {
                    myMessage.success('修改成功');
                }
            );
        },

        toDiscussion(did) {
            window.open('/discussion/' + did);
        },

        deleteDiscussion(didList) {
            if (!didList) {
                didList = this.selectedDiscussions;
            }
            if (didList.length > 0) {
                this.$confirm('确认删除？', 'Tips', {
                    type: 'warning',
                }).then(
                    () => {
                        discussionApi.adminDeleteDiscussion(didList).then(
                            (res) => {
                                myMessage.success('删除成功');
                                this.selectedDiscussions = [];
                                this.getDiscussionList(this.currentPage);
                            })
                            .catch(() => {
                                this.selectedDiscussions = [];
                                this.getDiscussionList(this.currentPage);
                            });
                    },
                    () => { }
                );
            } else {
                myMessage.warning('请选择需要删除的数据！');
            }
        },
        openReportDialog(content) {
            let reg = '#(.*?)# ';
            let re = RegExp(reg, 'g');
            let tmp;
            let showContent = '<strong>' + '标签' + '</strong>：';
            while ((tmp = re.exec(content))) {
                showContent += tmp[1] + ' ';
            }
            showContent +=
                '<br><br><strong>' +
                '内容' +
                '</strong>：' +
                content.replace(/#(.*?)# /g, '');
            this.$alert(showContent, '举报内容', {
                confirmButtonText: '确认',
                dangerouslyUseHTMLString: true,
            });
        },
    }
}

</script>
  
<style lang="scss" scoped>
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