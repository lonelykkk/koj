<template>
    <div style="text-align:center">
        <div style="margin-bottom:10px" v-if="contest.type != undefined">
            <span class="tips">
                {{
                    contest.type == 0
                    ? '当前比赛为ACM赛制：只支持导入ACM类型的题目'
                    : '当前比赛为OI赛制：只支持导入OI类型的题目'
                }}
            </span>
        </div>
        <vxe-input v-model="keyword" placeholder="请输入关键字" type="search" size="medium" @search-click="filterByKeyword"
            @keyup.enter.native="filterByKeyword" style="margin-bottom:10px">
        </vxe-input>
        <vxe-table :data="problems" :loading="loading" auto-resize stripe align="center">
            <vxe-table-column title="ID" min-width="100" field="problemId">
            </vxe-table-column>
            <vxe-table-column min-width="150" title="标题" field="title">
            </vxe-table-column>
            <vxe-table-column title="操作" align="center" min-width="100">
                <template v-slot="{ row }">
                    <el-tooltip effect="dark" content="添加" placement="top">
                        <el-button icon="el-icon-plus" size="mini" @click.native="handleAddProblem(row.id, row.problemId)"
                            type="primary">
                        </el-button>
                    </el-tooltip>
                </template>
            </vxe-table-column>
        </vxe-table>

        <el-pagination class="page" layout="prev, pager, next" @current-change="getPublicProblem" :page-size="limit"
            :current-page.sync="page" :total="total">
        </el-pagination>
    </div>
</template>
<script>
import contestApi from '@/api/contest'
import problemApi from '@/api/problem'
import myMessage from '@/utils/message'
export default {
    name: 'add-problem-from-public',
    props: ['contestID'],
    data() {
        return {
            page: 1,
            limit: 10,
            total: 0,
            loading: false,
            problems: [],
            contest: {},
            keyword: '',
        };
    },
    mounted() {
        contestApi.getContest(this.contestID).then(
            (res) => {
                this.contest = res.data.data
                this.getPublicProblem(1)
            }
        ).catch(() => { })
    },
    methods: {
        getPublicProblem(page) {
            this.loading = true;
            let params = {
                limit: this.limit,
                currentPage: page,
                keyword: this.keyword,
                auth: 1,
                contestId: this.contestID
            }

            problemApi.getAllProblem(params).then(
                (res) => {
                    this.loading = false;
                    this.total = res.data.data.total
                    this.problems = res.data.data.records
                }).catch(() => {
                    this.loading = false;
                });
        },
        handleAddProblem(id, problemId) {
            if (this.contestID) {
                this.$prompt('请输入该题目在比赛中展示ID', 'Tips')
                    .then(
                        ({ value }) => {
                            let data = {
                                pid: id,
                                cid: this.contestID,
                                displayId: value,
                            };

                            contestApi.addProblemFromPublic(data).then(
                                (res) => {
                                    this.$emit('on-change')
                                    myMessage.success('添加成功')
                                    this.getPublicProblem(this.page)
                                },
                                () => { }
                            );
                        },
                        () => { }
                    );
            }
        },
        filterByKeyword() {
            this.page = 1;
            this.getPublicProblem(this.page);
        },
    },
};
</script>
<style scoped>
.page {
    margin-top: 20px;
    text-align: right;
}

.tips {
    color: red;
    font-weight: bolder;
    font-size: 1rem;
}
</style>
  