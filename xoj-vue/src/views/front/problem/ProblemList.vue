<template>
    <div class="container">
        <el-row :gutter="18">
            <el-col :sm="24" :md="18" :lg="18">
                <el-card shadow>
                    <div slot="header">
                        <el-row :gutter="20" style="margin-bottom: 0.5em;">
                            <el-col :xs="24" :sm="4">
                                <span class="problem-list-title">
                                    题目列表
                                </span>
                            </el-col>
                            <el-col :xs="24" :sm="6">
                                <el-input placeholder="请输入关键字" v-model="query.keyword" class="filter-mt"
                                    @keyup.enter.native="filterByKeyword">
                                    <el-button slot="append" icon="el-icon-search" @click="filterByKeyword"></el-button>
                                </el-input>
                            </el-col>
                            <el-col :xs="12" :sm="6" style="text-align: center;" class="filter-mt">
                                <el-button type="primary" icon="el-icon-refresh" round @click="onReset">
                                    重置
                                </el-button>
                            </el-col>
                        </el-row>

                        <section>
                            <b class="problem-filter">题目类型</b>
                            <div>
                                <el-tag size="medium" class="filter-item" :effect="query.type === 'ALL' ? 'dark' : 'plain'"
                                    @click="filterByType('ALL')">全部</el-tag>
                                <el-tag size="medium" class="filter-item"
                                    :effect="query.type === 'ACM' || query.type === '' ? 'dark' : 'plain'"
                                    @click="filterByType('ACM')">ACM</el-tag>
                                <el-tag size="medium" class="filter-item" :effect="query.type === 'OI' ? 'dark' : 'plain'"
                                    @click="filterByType('OI')">OI</el-tag>
                            </div>
                        </section>

                        <section>
                            <b class="problem-filter">难度</b>
                            <div>
                                <el-tag size="medium" class="filter-item"
                                    :effect="query.difficulty === 'ALL' || query.difficulty === '' ? 'dark' : 'plain'"
                                    @click="filterByDifficulty('ALL')">
                                    全部
                                </el-tag>
                                <el-tag size="medium" class="filter-item" v-for="(value, key, index) in PROBLEM_LEVEL"
                                    :effect="query.difficulty == key ? 'dark' : 'plain'" :style="getLevelBlockColor(key)"
                                    :key="index" @click="filterByDifficulty(key)">
                                    {{ getLevelName(key) }}
                                </el-tag>
                            </div>
                        </section>

                        <template v-if="filterTagList.length > 0">
                            <el-row>
                                <b class="problem-filter">标签</b>
                                <el-tag :key="index" v-for="(tag, index) in filterTagList" closable
                                    :color="tag.color ? tag.color : '#409eff'" effect="dark" :disable-transitions="false"
                                    @close="removeTag(tag)" size="medium" class="filter-item">
                                    {{ tag.name }}
                                </el-tag>
                            </el-row>
                        </template>
                    </div>

                    <el-table :data="problemList" stripe style="width: 100%">
                        <el-table-column prop="isAc" width="50" v-if="isAuthenticated">
                            <template v-slot="{ row }">
                                <template v-if="row.isAc">
                                    <i class="el-icon-check" :style="getIconColor(row.isAc)"></i>
                                </template>
                            </template>
                        </el-table-column>
                        <el-table-column prop="problemId" label="题目ID" width="180">
                        </el-table-column>
                        <el-table-column prop="title" label="标题" width="180">
                            <template v-slot="{ row }">
                                <a @click="getProblemUri(row.pid)" class="title-a">
                                    {{ row.title }}
                                </a>
                            </template>
                        </el-table-column>
                        <el-table-column prop="difficulty" label="难度" width="180">
                            <template v-slot="{ row }">
                                <span class="el-tag el-tag--small" :style="getLevelColor(row.difficulty)">
                                    {{ getLevelName(row.difficulty) }}
                                </span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="total" label="提交数" width="180">
                            <template v-slot="{ row }">
                                {{ row.total == undefined ? 0 : row.total }}
                            </template>
                        </el-table-column>
                        <el-table-column prop="ac" label="通过率" width="180">
                            <template v-slot="{ row }">
                                <span>
                                    <el-tooltip effect="dark" :content="row.ac + '/' + row.total" placement="top"
                                        style="margin-top:0">
                                        <el-progress :text-inside="true" :stroke-width="20" :color="customColors"
                                            :percentage="getPassingRate(row.ac, row.total)">
                                        </el-progress>
                                    </el-tooltip>
                                </span>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
                <Pagination :total="total" :page-size="query.limit" @on-change="pushRouter"
                    :current.sync="query.currentPage" @on-page-size-change="onPageSizeChange"
                    :layout="'prev, pager, next, sizes'"></Pagination>
            </el-col>

            <el-col :sm="24" :md="6" :lg="6">
                <el-card :padding="10" style="">
                    <div slot="header" style="text-align: center;">
                        <span class="taglist-title">全部 标签</span>
                        <!-- <div style="margin: 10px 0;">
                        <el-input size="medium" prefix-icon="el-icon-search" :placeholder="$t('m.Search_Filter_Tag')"
                            v-model="searchTag" @keyup.enter.native="filterSearchTag" @input="filterSearchTag" clearable>
                        </el-input>
                    </div> -->
                    </div>
                    <template v-if="searchTagList.length > 0" v-loading="loadings.tag">
                        <el-button v-for="tag in searchTagList" :key="tag.id" @click="addTag(tag)" size="mini"
                            class="tag-btn" :style="'color:#FFF;background-color:' + (tag.color ? tag.color : '#409eff')">
                            {{ tag.name }}
                        </el-button>
                    </template>
                    <template v-else>
                        <el-empty description="暂无标签"></el-empty>
                    </template>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>
  
<script>
import { mapGetters } from 'vuex'
import problemApi from '@/api/problem'
import tagApi from '@/api/tag'
import {
    PROBLEM_LEVEL,
    JUDGE_STATUS,
    JUDGE_STATUS_RESERVE,
} from '@/common/constants'
import myMessage from '@/utils/message'
import utils from '@/utils/utils'
import Pagination from '@/components/front/common/Pagination'
export default {
    name: 'ProblemList',
    components: {
        Pagination,
    },
    data() {
        return {
            PROBLEM_LEVEL: {},
            problemList: [],
            JUDGE_STATUS: {},
            JUDGE_STATUS_RESERVE: {},
            total: 0,
            query: {
                limit: 20,
                currentPage: 1,
                keyword: '',
                tagId: '',
                difficulty: 'ALL',
                type: 'ALL'
            },
            filterTagList: [],
            searchTagList: [],
            loadings: {
                tag: true
            },
            customColors: [
                { color: '#909399', percentage: 20 },
                { color: '#f56c6c', percentage: 40 },
                { color: '#e6a23c', percentage: 60 },
                { color: '#1989fa', percentage: 80 },
                { color: '#67c23a', percentage: 100 },
            ]
        }
    },
    created() {
        this.init();
    },
    mounted() {
        this.PROBLEM_LEVEL = Object.assign({}, PROBLEM_LEVEL)
        this.JUDGE_STATUS_RESERVE = Object.assign({}, JUDGE_STATUS_RESERVE)
        this.JUDGE_STATUS = Object.assign({}, JUDGE_STATUS)
        this.getSearchTagList()
        this.getProblemList()
    },
    methods: {
        init() {
            this.routeName = this.$route.name;
            let query = this.$route.query;
            this.query.difficulty = query.difficulty || ''
            this.query.keyword = query.keyword || ''
            try {
                this.query.tagId = JSON.parse(query.tagId)
            } catch (error) {
                this.query.tagId = [];
            }
            this.query.currentPage = parseInt(query.currentPage) || 1;
            this.query.limit = parseInt(query.limit) || 10;
            if (this.query.currentPage < 1) {
                this.query.currentPage = 1;
            }
        },
        getProblemList() {
            let queryParams = Object.assign({}, this.query);
            if (this.query.difficulty === 'ALL')
                queryParams.difficulty = ''
            if (this.query.type === 'ALL')
                queryParams.type = ''
            else if (this.query.type === 'ACM')
                queryParams.type = 0
            else if (this.query.type === 'OI')
                queryParams.type = 1

            queryParams.tagId = queryParams.tagId + ''
            problemApi.getProblemList(queryParams).then(
                (res) => {
                    this.problemList = res.data.data.records
                    this.total = res.data.data.total
                }
            )
        },
        onPageSizeChange(pageSize) {
            this.query.limit = pageSize;
            this.pushRouter();
        },
        getPassingRate(ac, total) {
            if (!total) {
                return 0;
            }
            return Number(((ac / total) * 100).toFixed(2));
        },
        getProblemUri(problemId) {
            this.$router.push({
                name: 'ProblemDetail',
                params: {
                    problemID: problemId,
                },
            });
        },
        filterByKeyword() {
            this.query.currentPage = 1
            this.pushRouter()
        },
        filterByType(type) {
            this.query.type = type
            this.query.currentPage = 1
            this.pushRouter()
        },
        filterByDifficulty(difficulty) {
            this.query.difficulty = difficulty
            this.query.currentPage = 1
            this.pushRouter()
        },
        onReset() {
            this.filterTagList = [];
            if (JSON.stringify(this.$route.query) != '{}') {
                this.$router.push({ name: 'ProblemList' });
            }
        },
        pushRouter() {
            this.query.tagId = JSON.stringify(
                this.filterTagList.map((tagJson) => tagJson.id)
            )
            console.log(this.query.tagId)
            this.$router.push({
                path: '/problem',
                query: this.query,
            });
        },
        removeTag(tag) {
            this.filterTagList.splice(this.filterTagList.indexOf(tag), 1);
            this.filterByTag();
        },
        addTag(tag) {
            let len = this.filterTagList.length;
            for (let i = 0; i < len; i++) {
                if (this.filterTagList[i].id == tag.id) {
                    return;
                }
            }
            this.filterTagList.push(tag);
            this.filterByTag();
        },
        filterByTag() {
            this.query.currentPage = 1;
            this.pushRouter();
        },
        getSearchTagList() {
            tagApi.getAllTag().then(
                (res) => {
                    this.searchTagList = res.data.data
                }
            )
        },
        getIconColor(status) {
            return (
                'font-weight: 600;font-size: 16px;color:' +
                this.JUDGE_STATUS['0'].rgb
            );
        },
        getLevelName(difficulty) {
            return utils.getLevelName(difficulty);
        },
        getLevelColor(difficulty) {
            return utils.getLevelColor(difficulty);
        },
        getLevelBlockColor(difficulty) {
            if (difficulty == this.query.difficulty) {
                return this.getLevelColor(difficulty);
            }
        },
    },
    computed: {
        ...mapGetters(['isAuthenticated']),

    },
    watch: {
        $route(newVal, oldVal) {
            if (newVal !== oldVal) {
                this.init()
                this.getProblemList()
            }
        },
        isAuthenticated(newVal) {
            if (newVal === true) {
                this.init()
                this.getProblemList()
            }
        },
    },
}

</script>
<style lang="scss" scoped>
// .container {
//     padding: 0 4%;
// }

.problem-list-title {
    font-size: 2em;
    font-weight: 500;
    line-height: 30px;
}

section {
    display: flex;
    align-items: baseline;
    margin-bottom: 0.8em;
}

.problem-filter {
    margin-right: 1em;
    font-weight: bolder;
    white-space: nowrap;
    font-size: 16px;
    margin-top: 8px;
}

.filter-item {
    margin-right: 1em;
    margin-top: 0.5em;
    font-size: 13px;
}

.filter-item:hover {
    cursor: pointer;
}

.taglist-title {
    font-size: 21px;
    font-weight: 500;
}

.tag-btn {
    margin-left: 5px !important;
    margin-top: 5px !important;
}

.title-a {
    color: #495060;
    font-family: inherit;
    font-size: 14px;
    font-weight: 500;
}
</style>
  