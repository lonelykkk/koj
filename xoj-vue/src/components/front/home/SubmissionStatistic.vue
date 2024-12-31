<template>
    <el-card>
        <div shadow slot="header" :padding="10">
            <span class="home-title panel-title">
                <i class="el-icon-data-line"></i>
                最近一周提交统计
            </span>
        </div>
        <div class="echarts" v-loading="loading">
            <ECharts :options="options" ref="chart" :autoresize="true"></ECharts>
        </div>
    </el-card>
</template>
<script>
import api from "@/api/home";
import { mapGetters } from 'vuex';
export default {
    name: "SubmissionStatistics",
    // props: {
    //     title: {
    //         type: String,
    //         required: true,
    //     },
    // },
    data() {
        return {
            loading: false,
            options: {
                tooltip: {
                    trigger: "axis",
                    axisPointer: {
                        type: "cross",
                        label: {
                            backgroundColor: "#6a7985",
                        },
                    },
                },
                legend: {
                    data: ['通过', '总数'],
                },
                toolbox: {
                    feature: {
                        saveAsImage: { show: true, title: '保存图片' },
                    },
                },
                grid: {
                    left: "3%",
                    right: "4%",
                    bottom: "3%",
                    containLabel: true,
                },
                xAxis: [
                    {
                        type: "category",
                        boundaryGap: false,
                        data: [],
                    },
                ],
                yAxis: [
                    {
                        type: "value",
                    },
                ],
                series: [
                    {
                        name: '通过',
                        type: "line",
                        stack: "Total",
                        areaStyle: {},
                        emphasis: {
                            focus: "series",
                        },
                        color: "#91cc75",
                        data: [0, 0, 0, 0, 0, 0, 0],
                    },
                    {
                        name: '总数',
                        type: "line",
                        stack: "Total",
                        label: {
                            show: true,
                            position: "top",
                        },
                        areaStyle: {},
                        emphasis: {
                            focus: "series",
                        },
                        color: "#73c0de",
                        data: [0, 0, 0, 0, 0, 0, 0],
                    },
                ],
            },
        };
    },
    mounted() {
        this.getRecentWeekSubmission();
    },
    methods: {
        getRecentWeekSubmission() {
            this.loading = true;
            api.getRecentWeekSubmission().then(
                (res) => {
                    this.options.xAxis[0].data = res.data.data.dateStrList;
                    this.options.series[0].data = res.data.data.acCountList;
                    this.options.series[1].data = res.data.data.totalCountList;
                    this.loading = false;
                },
                (err) => {
                    this.loading = false;
                }
            );
        },
    },
    computed: {
        ...mapGetters(['isSuperAdmin'])
    },
};
</script>
<style scoped>
.echarts {
    height: 400px;
    width: 100%;
}

::v-deep .el-card__body {
    padding: 20px 10px !important;
}
</style>