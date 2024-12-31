<template>
    <div>
        <div class="container" v-loading="loading">
            <div class="title-article" style="text-align: left">
                <h1 class="title" id="sharetitle">
                    <span>{{ discussion.title }}</span>
                    <el-button type="primary" size="mini" style="margin-left:5px;vertical-align:middle;"
                        v-if="discussion.pid && discussion.pid != 0" @click="toProblem(discussion.pid)">
                        前往原题
                    </el-button>
                </h1>
                <div class="title-msg">
                    <span>
                        <a class="c999" @click="getInfoByUsername(discussion.uid)" :title="discussion.author">
                            <avatar :username="discussion.author" :inline="true" :size="26" color="#FFF" class="user-avatar"
                                :src="discussion.avatar">
                            </avatar>
                            <span class="user-name">{{ discussion.author }}</span>
                        </a>
                        <span v-if="discussion.titleName">
                            <el-tag effect="dark" size="small" :color="discussion.titleColor">
                                {{ discussion.titleName }}
                            </el-tag>
                        </span>
                    </span>
                    <span class="c999" style="padding:0 6px;">
                        <i class="el-icon-folder-opened">
                            分类：
                        </i>
                        <a class="c999" @click="toAllDiscussionByCid(discussion.categoryId)">
                            {{ discussion.categoryName }}
                        </a>
                    </span>
                    <span class="c999">
                        <i class="fa fa-thumbs-o-up"></i>
                        <span>
                            点赞：{{ discussion.likeNum }}
                        </span>
                    </span>
                    <span class="c999">
                        <i class="fa fa-eye"></i>
                        <span>
                            浏览：{{ discussion.viewNum }}
                        </span>
                    </span>

                    <a @click="showReportDialog = true" class="report" title="举报">
                        <i class="fa fa-envira"></i>
                        <span>举报</span>
                    </a>
                    <a @click="toLikeDiscussion(discussion.id, true)" class="like" title="点赞" v-if="!discussion.hasLike">
                        <i class="fa fa-thumbs-o-up"></i>
                        <span>点赞</span>
                    </a>
                    <a @click="toLikeDiscussion(discussion.id, false)" class="like" title="已点赞" v-else>
                        <i class="fa fa-thumbs-up"></i>
                        <span>已点赞</span>
                    </a>

                    <span>
                        <i class="fa fa-clock-o"> 发布时间：</i>
                        <span>
                            <el-tooltip :content="discussion.gmtCreate | localtime" placement="top">
                                <span>{{ discussion.gmtCreate | fromNow }}</span>
                            </el-tooltip>
                        </span>
                    </span>

                    <span style="padding:0 6px;" v-show="userInfo.id == discussion.uid || isAdminRole">
                        <a style="color:#8fb0c9" @click="showEditDiscussionDialog = true">
                            <i class="el-icon-edit-outline">编辑</i>
                        </a>
                    </span>
                </div>
            </div>
            <div class="body-article">
                <Markdown :isAvoidXss="discussion.role != 'root' && discussion.role != 'admin'"
                    :content="discussion.content">
                </Markdown>
            </div>
        </div>

        <Comment class="comment" :did="parseInt($route.params.discussionID)"></Comment>

        <el-dialog title="举报" :visible.sync="showReportDialog" width="350px">
            <el-form label-position="top" :model="report">
                <el-form-item label="标签" required>
                    <el-checkbox-group v-model="report.tagList">
                        <el-checkbox label="垃圾广告"></el-checkbox>
                        <el-checkbox label="违法违规"></el-checkbox>
                        <el-checkbox label="色情低俗"></el-checkbox>
                        <el-checkbox label="赌博诈骗"></el-checkbox>
                        <el-checkbox label="恶意骂战"></el-checkbox>
                        <el-checkbox label="恶意抄袭"></el-checkbox>
                    </el-checkbox-group>
                </el-form-item>
                <el-form-item label="举报原因" required>
                    <el-input type="textarea" v-model="report.content" placeholder="举报原因" maxlength="200" show-word-limit
                        :rows="4">
                    </el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="danger" @click.native="showReportDialog = false">
                    取消
                </el-button>
                <el-button type="primary" @click.native="submitReport">
                    确定
                </el-button>
            </span>
        </el-dialog>

        <!--编辑讨论对话框-->
        <el-dialog :title="discussionDialogTitle" :visible.sync="showEditDiscussionDialog" :fullscreen="true"
            @open="onOpenEditDialog">
            <el-form label-position="top" :model="discussion">
                <el-form-item label="标题" required>
                    <el-input v-model="discussion.title" placeholder="标题" class="title-input">
                    </el-input>
                </el-form-item>
                <el-form-item label="描述" required>
                    <el-input v-model="discussion.description" placeholder="描述" class="title-input">
                    </el-input>
                </el-form-item>
                <el-form-item label="分类" required>
                    <el-select v-model="discussion.categoryId" placeholder="---">
                        <el-option v-for="category in categoryList" :key="category.id" :label="category.name"
                            :value="category.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="是否置顶" required v-if="isAdminRole">
                    <el-switch v-model="discussion.topPriority"> </el-switch>
                </el-form-item>
                <el-form-item label="内容" required>
                    <Editor :value.sync="discussion.content"></Editor>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="danger" @click.native="showEditDiscussionDialog = false">取消</el-button>
                <el-button type="primary" @click.native="submitDiscussion">确定</el-button>
            </span>
        </el-dialog>
    </div>
</template>
  
<script>
import myMessage from '@/utils/message'
import discussionApi from '@/api/discussion'
import Avatar from 'vue-avatar'
import { addCodeBtn } from '@/utils/codeblock'
import { mapGetters, mapActions } from 'vuex'
const Editor = () => import('@/components/admin/Editor.vue')
const Comment = () => import('@/components/front/comment/Comment')
import Markdown from '@/components/front/common/Markdown'
export default {
    components: {
        Comment,
        Avatar,
        Markdown,
        Editor,
    },
    created() {
        this.init()
    },
    data() {
        return {
            discussion: {
                author: '',
                avatar: '',
            },
            query: {
                currentPage: 1,
                limit: 10,
                did: null,
            },
            report: {
                tagList: [],
                content: '',
            },
            categoryList: [],
            discussionID: 0,
            discussionDialogTitle: '编辑',
            showEditDiscussionDialog: false,
            showReportDialog: false,
            loading: false,
        }
    },
    methods: {
        init() {
            this.routeName = this.$route.name
            this.discussionID = this.$route.params.discussionID || '';
            this.loading = true
            discussionApi.getDiscussionDetail(this.discussionID).then(
                (res) => {
                    this.discussion = res.data.data
                    this.$nextTick((_) => {
                        addCodeBtn()
                    })
                    this.loading = false
                },
                (err) => {
                    this.loading = false
                }
            )
            this.getCategoryList()
        },
        getCategoryList() {
            discussionApi.getCategoryList().then(
                (res) => {
                    this.categoryList = res.data.data
                },
                (err) => {

                }
            )
        },
        submitDiscussion() {
            // 默认为题目的讨论添加题号格式
            let discussion = Object.assign({}, this.discussion);
            // 不要影响动态数据
            delete discussion.viewNum;
            delete discussion.likeNum;
            discussionApi.updateDiscussion(discussion).then(
                (res) => {
                    myMessage.success('修改成功');
                    this.showEditDiscussionDialog = false;
                    this.init();
                });
        },
        getInfoByUsername(uid) {
            // console.log('getInfoByUsername')
        },
        toLikeDiscussion(did, toLike) {
            if (!this.isAuthenticated) {
                myMessage.warning('请先登录！');
                return;
            }
            discussionApi.addDiscussionLike(did, toLike).then(
                (res) => {
                    if (toLike) {
                        this.discussion.likeNum++;
                        this.discussion.hasLike = true;
                        myMessage.success('点赞成功！');
                    } else {
                        myMessage.success('取消成功！');
                        this.discussion.likeNum--;
                        this.discussion.hasLike = false;
                    }
                }
            );
        },
        onOpenEditDialog() {

        },
        toAllDiscussionByCid(categoryId) {
            // console.log('toAllDiscussionByCid')
        },
        toProblem(pid) {
            // console.log('toProblem')
        },
        submitReport() {
            if (!this.isAuthenticated) {
                myMessage.warning('请先登录！');
                return;
            }
            if (this.report.tagList.length == 0 && !this.report.content) {
                myMessage.warning('举报标签不能为空');
                return;
            }
            var reportMsg = '';
            for (let i = 0; i < this.report.tagList.length; i++) {
                reportMsg += '#' + this.report.tagList[i] + '# ';
            }
            reportMsg += this.report.content;
            let discussionReport = {
                userId: this.userInfo.id,
                content: reportMsg,
                discussionId: this.discussionID,
            };
            discussionApi.addDiscussionReport(discussionReport).then(
                (res) => {
                    myMessage.success('举报成功！');
                    this.showReportDialog = false;
                }
            );
        },
    },
    computed: {
        ...mapGetters(['isAuthenticated', 'isAdminRole', 'userInfo']),
    },
    watch: {
        isAuthenticated(newVal, oldVal) {
            if (newVal != oldVal) {
                this.init();
            }
        },
    },
}

</script>
  
<style lang="scss" scoped>
.container {
    box-sizing: border-box;
    background-color: #fff;
    box-shadow: 0 2px 12px 0 rgb(0 0 0 / 10%);
    border: 1px solid #ebeef5;
    margin: 0 100px 20px 100px;
}

.comment {
    margin: 0 100px;
}

.title-article {
    background: #fff;
    overflow: hidden;
    padding: 10px 20px;
    position: relative;
    text-align: center;
}

.title-article h1.title {
    font-size: 25px;
    font-weight: 600;
    color: #34495e;
    padding: 0 0 10px;
    width: 80%;
    line-height: 32px;
    word-break: break-all;
}

.title-article .title-msg {
    margin-bottom: 0px;
    font-size: 12px;
    color: #999;
}

.title-article .title-msg span {
    margin-right: 2px;
}

.title-article .title-msg span a.c999 {
    color: #999 !important;
}

.title-article .title-msg span a.c999:hover {
    color: #007bff !important;
    text-decoration: none;
}

.user-avatar {
    vertical-align: middle;
}

.user-name {
    margin: 0 0.25rem !important;
}

.title-article .title-msg a.report {
    position: absolute;
    top: 20px;
    right: 5px;
    color: #4caf50 !important;
    font-weight: bold;
    font-size: 14px;
}

.title-article .title-msg a.like {
    position: absolute;
    top: 20px;
    right: 68px;
    color: #ff6700 !important;
    font-weight: bold;
    font-size: 14px;
}

@media screen and (max-width: 768px) {
    .title-article .title-msg a.report {
        top: 50px !important;
        right: 12px !important;
    }

    .title-article .title-msg a.like {
        top: 24px !important;
        right: 12px !important;
    }
}

.body-article {
    background: #fff;
    overflow: hidden;
    width: 100%;
    padding: 20px 20px;
    text-align: left;
    font-size: 14px;
    line-height: 1.6;
}
</style>