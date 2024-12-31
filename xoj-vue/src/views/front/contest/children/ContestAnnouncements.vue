<template>
    <el-card shadow :padding="10">
        <div slot="header">
            <span class="panel-title" v-if="isContest">考核公告</span>
            <span v-else class="home-title panel-title"><i class="el-icon-data-board"></i>公告</span>
            <span style="float: right">
                <el-button v-if="listVisible" type="primary" @click="init" size="small" icon="el-icon-refresh"
                    :loading="btnLoading">刷新</el-button>
                <el-button v-else type="primary" icon="el-icon-back" @click="goBack" size="small">返回</el-button>
            </span>
        </div>
        <transition-group name="el-zoom-in-bottom">
            <div class="no-announcement" v-if="!announcements.length" key="no-announcement">
                <el-empty description="暂无通告"></el-empty>
            </div>
            <template v-if="listVisible">
                <ul class="announcements-container" key="list">
                    <li v-for="announcement in announcements" :key="announcement.title">
                        <div class="flex-container">
                            <div class="title">
                                <a class="entry" @click="goAnnouncement(announcement)">
                                    {{ announcement.title }}
                                </a>
                            </div>

                            <div class="info">
                                <span class="date">
                                    <i class="el-icon-edit"></i>
                                    {{ announcement.gmtCreate | localtime }}
                                </span>
                                <span class="creator">
                                    <i class="el-icon-user"></i>
                                    {{ announcement.username }}
                                </span>
                            </div>
                        </div>
                    </li>
                </ul>
                <Pagination v-if="!isContest" key="page" :total="total" :page-size="limit" @on-change="getAnnouncementList">
                </Pagination>
            </template>

            <template v-else>
                <div v-katex v-highlight v-html="announcement.content" key="content"
                    class="content-container markdown-body"></div>
            </template>
        </transition-group>
    </el-card>
</template>
  
<script>
import contestApi from '@/api/contest'
import Pagination from '@/components/front/common/Pagination'

export default {
    name: 'Announcements',
    components: {
        Pagination
    },
    data() {
        return {
            total: 0,
            limit: 5,
            btnLoading: false,
            announcements: [],
            announcement: '',
            listVisible: true,
        }
    },
    created() {

    },
    mounted() {
        this.init();
    },
    methods: {
        init() {
            this.getContestAnnouncementList();
        },
        getContestAnnouncementList(page = 1) {
            this.btnLoading = true;
            let query = {
                limit: this.limit,
                currentPage: page,
                cid: this.$route.params.contestID
            }
            contestApi.getContestAnnouncement(query).then(
                (res) => {
                    this.btnLoading = false;
                    this.announcements = res.data.data.records;
                    this.total = res.data.data.total;
                },
                () => {
                    this.btnLoading = false;
                }
            );
        },
        goAnnouncement(announcement) {
            this.announcement = announcement;
            this.announcement.content = this.$markDown.render(announcement.content);
            this.listVisible = false;
        },
        goBack() {
            this.listVisible = true;
            this.announcement = '';
        },
    },
    computed: {
        isContest() {
            return !!this.$route.params.contestID;
        },
    },
    watch: {

    },
}

</script>
<style lang="scss" scoped>
.announcements-container {
    margin-top: -10px;
    margin-bottom: 10px;
}

.announcements-container li {
    padding-top: 15px;
    list-style: none;
    padding-bottom: 15px;
    margin-left: 20px;
    margin-top: 10px;
    font-size: 16px;
    border: 1px solid rgba(187, 187, 187, 0.5);
    border-left: 2px solid #409eff;
}

/* .announcements-container li:last-child {
  border-bottom: none;
} */
.flex-container {
    text-align: center;
}

.flex-container .info {
    margin-top: 5px;
}

.flex-container .title .entry {
    color: #495060;
    font-style: oblique;
}

.flex-container .title a:hover {
    color: #2d8cf0;
    border-bottom: 1px solid #2d8cf0;
}

.creator {
    width: 200px;
    text-align: center;
}

.date {
    width: 200px;
    text-align: center;
    margin-right: 5px;
}

.content-container {
    padding: 0 20px 20px 20px;
}

.no-announcement {
    text-align: center;
    font-size: 16px;
}

.announcement-animate-enter-active {
    animation: fadeIn 1s;
}

ul {
    list-style-type: none;
    padding-inline-start: 0px;
}
</style>
  