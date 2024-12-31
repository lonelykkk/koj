<template>
    <div class="container">
        <el-row type="flex" justify="space-around">
            <el-col :span="24">
                <el-card shadow>
                    <div slot="header">
                        <span class="panel-title">
                            全部考核
                        </span>
                        <div class="filter-row">
                            <span>
                                <vxe-input v-model="query.keyword" placeholder="输入关键字" type="search" size="medium"
                                    @keyup.enter.native="onKeywordChange" @search-click="onKeywordChange">
                                </vxe-input>
                            </span>
                        </div>
                    </div>
                    <div v-loading="loading">
                        <p id="no-contest" v-show="contests.length == 0">
                            <el-empty description="暂无考核"></el-empty>
                        </p>
                        <ol id="contest-list">
                            <li v-for="contest in contests" :key="contest.title" :style="getborderColor(contest)">
                                <el-row type="flex" justify="space-between" align="middle">
                                    <el-col :xs="10" :sm="4" :md="3" :lg="2">
                                        <template v-if="contest.type == 0">
                                            <el-image :src="acmSrc" class="trophy" style="width: 100px;"
                                                :preview-src-list="[acmSrc]">
                                            </el-image>
                                        </template>
                                        <template v-else>
                                            <el-image :src="oiSrc" class="trophy" style="width: 100px;"
                                                :preview-src-list="[oiSrc]">
                                            </el-image>
                                        </template>
                                    </el-col>
                                    <el-col :xs="10" :sm="16" :md="19" :lg="20" class="contest-main">
                                        <p class="title">
                                            <a class="entry" @click.stop="toContest(contest)">
                                                {{ contest.title }}
                                            </a>
                                            <template v-if="contest.auth == 1">
                                                <i class="el-icon-lock" size="20" style="color:#d9534f"></i>
                                            </template>
                                            <template v-if="contest.auth == 2">
                                                <i class="el-icon-lock" size="20" style="color:#f0ad4e"></i>
                                            </template>
                                        </p>
                                        <ul class="detail">
                                            <li>
                                                <i class="fa fa-calendar" aria-hidden="true" style="color: #3091f2"></i>
                                                {{ contest.startTime | localtime }}
                                            </li>
                                            <li>
                                                <i class="fa fa-clock-o" aria-hidden="true" style="color: #3091f2"></i>
                                                {{ getDuration(contest.startTime, contest.endTime) }}
                                            </li>
                                            <li>
                                                <template v-if="contest.type == 0">
                                                    <el-button size="mini" round :type="'primary'">
                                                        <i class="fa fa-trophy"></i>
                                                        {{ contest.type | parseContestType }}
                                                    </el-button>
                                                </template>
                                                <template v-else>
                                                    <el-button size="mini" round :type="'warning'">
                                                        <i class="fa fa-trophy"></i>
                                                        {{ contest.type | parseContestType }}
                                                    </el-button>
                                                </template>
                                            </li>
                                            <li>
                                                <el-tag :type="CONTEST_TYPE_REVERSE[contest.auth]['color']" effect="plain">
                                                    {{ contest.auth === 0 ? '公开' : '私有' }}
                                                </el-tag>
                                            </li>
                                            <li v-if="contest.count != null">
                                                <i class="el-icon-user-solid" style="color:rgb(48, 145, 242);"></i>
                                                x{{ contest.count }}
                                            </li>
                                        </ul>
                                    </el-col>
                                    <el-col :xs="4" :sm="4" :md="2" :lg="2" style="text-align: center">
                                        <el-tag effect="dark" :color="CONTEST_STATUS_REVERSE[contest.status]['color']"
                                            size="medium">
                                            <i class="fa fa-circle" aria-hidden="true"></i>
                                            {{
                                                CONTEST_STATUS_REVERSE[contest.status]['name2']
                                            }}
                                        </el-tag>
                                    </el-col>
                                </el-row>
                            </li>
                        </ol>
                    </div>
                </el-card>
                <Pagination :total="total" :pageSize="limit" @on-change="onCurrentPageChange" :current.sync="currentPage">
                </Pagination>
            </el-col>
        </el-row>
    </div>
</template>
  
<script>
import myMessage from '@/utils/message'
import contestApi from '@/api/contest'
import { mapGetters } from 'vuex'
import utils from '@/utils/utils'
import time from '@/common/time'
import {
    CONTEST_STATUS_REVERSE,
    CONTEST_TYPE_REVERSE,
    CONTEST_STATUS,
} from '@/common/constants'

const Pagination = () => import('@/components/front/common/Pagination')
const limit = 10      
export default {
    name: 'ContestList',
    components: {
        Pagination,
    },
    data() {
        return {
            currentPage: 1,
            query: {
                keyword: ''
            },
            limit: limit,
            total: 0,
            rows: '',
            contests: [],
            CONTEST_STATUS_REVERSE: {},
            CONTEST_STATUS: {},
            CONTEST_TYPE_REVERSE: {},
            acmSrc: require('@/assets/acm.jpg'),
            oiSrc: require('@/assets/oi.jpg'),
            loading: true
        }
    },
    created() {
        let route = this.$route.query
        this.currentPage = parseInt(route.currentPage) || 1
    },
    mounted() {
        this.CONTEST_STATUS_REVERSE = Object.assign({}, CONTEST_STATUS_REVERSE)
        this.CONTEST_TYPE_REVERSE = Object.assign({}, CONTEST_TYPE_REVERSE)
        this.CONTEST_STATUS = Object.assign({}, CONTEST_STATUS)
        this.init()
    },
    methods: {
        init() {
            let route = this.$route.query
            this.query.keyword = route.keyword || ''
            this.currentPage = parseInt(route.currentPage) || 1
            this.getContestList();
        },
        getContestList() {
            this.loading = true
            contestApi.getContestPage(this.limit, this.currentPage, this.query.keyword).then(
                (res) => {
                    this.contests = res.data.data.records;
                    this.total = res.data.data.total;
                    this.loading = false;
                },
                (err) => {
                    this.loading = false;
                }
            );
        },
        toContest(contest) {
            // todo 
            if (!this.isAuthenticated) {
                myMessage.warning('请先登录！');
            } else {
                this.$router.push({
                    name: 'ContestDetail',
                    params: { contestID: contest.id },
                });
            }
        },
        onCurrentPageChange(page) {
            this.currentPage = page
            this.filterByChange()
        },
        onKeywordChange() {
            this.currentPage = 1
            this.filterByChange()
        },
        filterByChange() {
            console.log('filterByChange')
            // let query = Object.assign({}, this.query);
            // query.currentPage = this.currentPage;
            // this.$router.push({
            //     name: 'ContestList',
            //     query: utils.filterEmptyValue(query),
            // });
        },
        getborderColor(contest) {
            return (
                'border-left: 4px solid ' +
                CONTEST_STATUS_REVERSE[contest.status]['color']
            );
        },
        parseContestType(type) {
            if (type == 0) {
                return 'ACM';
            } else if (type == 1) {
                return 'OI';
            }
        },
        getDuration(startTime, endTime) {
            return time.formatSpecificDuration(startTime, endTime);
        },
    },
    computed: {
        ...mapGetters(['isAuthenticated', 'userInfo']),
    },
    watch: {
        $route(newVal, oldVal) {
            if (newVal !== oldVal) {
                this.init();
            }
        },
    },
}

</script>
<style lang="scss" scoped>
.container {
    padding: 0 2%;
}

#no-contest {
    text-align: center;
    font-size: 16px;
    padding: 20px;
}

.filter-row {
    float: right;
}

#contest-list {
    padding: 0 15px;
}

#contest-list>li {
    padding: 5px;
    margin-left: -20px;
    margin-top: 10px;
    width: 100%;
    border-bottom: 1px solid rgba(187, 187, 187, 0.5);
    list-style: none;
}

#contest-list .trophy {
    height: 70px;
    margin-left: 10px;
    margin-right: -20px;
}


#contest-list .contest-main {
    text-align: left;
}

#contest-list .contest-main .title {
    font-size: 1.25rem;
    padding-left: 8px;
    margin-bottom: 0;
}

#contest-list .contest-main .title a.entry {
    color: #495060;
}

#contest-list .contest-main .title a:hover {
    color: #2d8cf0;
    border-bottom: 1px solid #2d8cf0;
}

#contest-list .contest-main .detail {
    font-size: 0.875rem;
    padding-left: 0;
    padding-bottom: 10px;
}

#contest-list .contest-main li {
    display: inline-block;
    padding: 10px 0 0 10px;
}
</style>
  