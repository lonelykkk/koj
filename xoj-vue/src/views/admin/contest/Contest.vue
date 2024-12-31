<template>
    <div>
        <el-card>
            <div slot="header">
                <span class="panel-title home-title">
                    {{ title }}
                </span>
            </div>
            <el-form label-position="top">
                <el-row :gutter="20">
                    <el-col :span="24">
                        <el-form-item label="考核标题" required>
                            <el-input v-model="contest.title" placeholder="比赛标题"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="考核描述" required>
                            <Editor :value.sync="contest.description"></Editor>
                        </el-form-item>
                    </el-col>
                    <el-col :md="8" :xs="24">
                        <el-form-item label="开始时间" required>
                            <el-date-picker v-model="contest.startTime" @change="changeDuration" type="datetime"
                                placeholder="开始时间">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :md="8" :xs="24">
                        <el-form-item label="结束时间" required>
                            <el-date-picker v-model="contest.endTime" @change="changeDuration" type="datetime"
                                placeholder="结束时间">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>

                    <el-col :md="8" :xs="24">
                        <el-form-item label="考核时长" required>
                            <el-input v-model="durationText" disabled> </el-input>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row>
                    <el-col :md="8" :xs="24">
                        <el-form-item label="考核类型" required>
                            <el-radio class="radio" v-model="contest.type" :label="0">ACM</el-radio>
                            <el-radio class="radio" v-model="contest.type" :label="1">OI</el-radio>
                        </el-form-item>
                    </el-col>
                </el-row>

                <el-row :gutter="20">

                    <el-col :md="8" :xs="24">
                        <el-form-item label="考核权限" required>
                            <el-select v-model="contest.auth">
                                <el-option label="公开" :value="0"></el-option>
                                <el-option label="私有" :value="1"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :md="8" :xs="24">
                        <el-form-item label="考核密码" v-show="contest.auth != 0" :required="contest.auth != 0">
                            <el-input v-model="contest.pwd" placeholder="考核密码"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :md="8" :xs="24">
                        <el-form-item label="账号限制（登录用户名）" v-show="contest.auth != 0" :required="contest.auth != 0">
                            <el-switch v-model="contest.openAccountLimit"> </el-switch>
                        </el-form-item>
                    </el-col>

                    <template v-if="contest.openAccountLimit">
                        <el-form :model="formRule">
                            <el-col :md="6" :xs="24">
                                <el-form-item label="前缀" prop="prefix">
                                    <el-input v-model="formRule.prefix" placeholder="Prefix"></el-input>
                                </el-form-item>
                            </el-col>
                            <el-col :md="6" :xs="24">
                                <el-form-item label="后缀" prop="suffix">
                                    <el-input v-model="formRule.suffix" placeholder="Suffix"></el-input>
                                </el-form-item>
                            </el-col>
                            <el-col :md="6" :xs="24">
                                <el-form-item label="开始数字" prop="number_from">
                                    <el-input-number v-model="formRule.number_from" style="width: 100%"></el-input-number>
                                </el-form-item>
                            </el-col>
                            <el-col :md="6" :xs="24">
                                <el-form-item label="结束数字" prop="number_to">
                                    <el-input-number v-model="formRule.number_to" style="width: 100%"></el-input-number>
                                </el-form-item>
                            </el-col>

                            <div class="userPreview" v-if="formRule.number_from <= formRule.number_to">
                                允许参加比赛的用户名是：
                                {{ formRule.prefix + formRule.number_from + formRule.suffix }},
                                <span v-if="formRule.number_from + 1 < formRule.number_to">
                                    {{
                                        formRule.prefix +
                                        (formRule.number_from + 1) +
                                        formRule.suffix +
                                        '...'
                                    }}
                                </span>
                                <span v-if="formRule.number_from + 1 <= formRule.number_to">
                                    {{ formRule.prefix + formRule.number_to + formRule.suffix }}
                                </span>
                            </div>

                            <el-col :md="24" :xs="24">
                                <el-form-item label="额外允许参加比赛的账号列表" prop="prefix">
                                    <el-input type="textarea" placeholder="额外允许参加比赛的账号列表" :rows="8"
                                        v-model="formRule.extra_account">
                                    </el-input>
                                </el-form-item>
                            </el-col>
                        </el-form>
                    </template>
                </el-row>
            </el-form>
            <el-button type="primary" @click.native="saveContest">
                保存
            </el-button>
        </el-card>
    </div>
</template>
  
<script>
import myMessage from '@/utils/message'
import contestApi from '@/api/contest'
import time from '@/common/time'
import Editor from '@/components/admin/Editor.vue'
export default {
    name: 'CreateContest',
    components: {
        Editor
    },
    data() {
        return {
            title: "创建考核",
            durationText: "", // 比赛时长文本表示
            contest: {
                title: "",
                description: "",
                startTime: "",
                endTime: "",
                duration: 0,
                type: 0,
                pwd: "",
                auth: 0,
                openAccountLimit: false,
                accountLimitRule: ""
            },
            formRule: {
                prefix: "",
                suffix: "",
                number_from: 0,
                number_to: 10,
                extra_account: "",
            }
        }
    },
    created() {

    },
    mounted() {
        if (this.$route.name === "admin-edit-contest") {
            this.title = "编辑考核"
            this.getContestByCid();
        } else {
            this.title = "创建考核"
        }
    },
    watch: {
        $route() {
            if (this.$route.name === "admin-edit-contest") {
                this.title = "编辑考核"
                this.getContestByCid();
            } else {
                this.title = "创建考核"
            }
        },
    },
    methods: {
        getContestByCid() {
            console.log('get contest by cid = ' + this.$route.params.contestId)
            contestApi.getContest(this.$route.params.contestId).then(
                (res) => {
                    let data = res.data.data;
                    this.contest = data;
                    this.changeDuration();
                    if (this.contest.accountLimitRule) {
                        this.formRule = this.changeStrToAccountRule(
                            this.contest.accountLimitRule
                        );
                    }
                })
                .catch(() => { });
        },
        saveContest() {
            console.log('save contest')
            if (!this.contest.title) {
                myMessage.error('标题不能为空！')
                return;
            }
            if (!this.contest.description) {
                myMessage.error('描述不能为空！')
                return;
            }
            if (!this.contest.startTime) {
                myMessage.error('开始时间不能为空！')
                return;
            }
            if (!this.contest.endTime) {
                myMessage.error('结束时间不能为空！')
                return;
            }
            if (!this.contest.duration || this.contest.duration <= 0) {
                myMessage.error('比赛时长不能小于0')
                return;
            }
            if (this.contest.auth != 0 && !this.contest.pwd) {
                myMessage.error('私有比赛请设置密码！')
                return;
            }

            if (this.contest.openAccountLimit) {
                this.contest.accountLimitRule = this.changeAccountRuleToStr(
                    this.formRule
                );
            }

            let funcName =
                this.$route.name === "admin-edit-contest"
                    ? "updateContest"
                    : "addContest"

            let data = Object.assign({}, this.contest)

            contestApi[funcName](data).then(
                (res) => {
                    myMessage.success("success")
                    this.$router.push({
                        name: "admin-contest-list"
                    })
                })
                .catch(() => { })
        },
        changeDuration() {
            let start = this.contest.startTime
            let end = this.contest.endTime
            let durationMS = time.durationMs(start, end)
            if (durationMS < 0) {
                this.durationText = '开始时间应该早于结束时间'
                this.contest.duration = 0
                return
            }
            if (start != "" && end != "") {
                this.durationText = time.formatSpecificDuration(start, end)
                this.contest.duration = durationMS
            }
        },
        changeAccountRuleToStr(formRule) {
            let result =
                "<prefix>" +
                formRule.prefix +
                "</prefix><suffix>" +
                formRule.suffix +
                "</suffix><start>" +
                formRule.number_from +
                "</start><end>" +
                formRule.number_to +
                "</end><extra>" +
                formRule.extra_account +
                "</extra>"
            return result
        },
        changeStrToAccountRule(value) {
            let reg =
                "<prefix>([\\s\\S]*?)</prefix><suffix>([\\s\\S]*?)</suffix><start>([\\s\\S]*?)</start><end>([\\s\\S]*?)</end><extra>([\\s\\S]*?)</extra>";
            let re = RegExp(reg, "g");
            let tmp = re.exec(value);
            return {
                prefix: tmp[1],
                suffix: tmp[2],
                number_from: tmp[3],
                number_to: tmp[4],
                extra_account: tmp[5],
            };
        },
    },
    computed: {

    }
}

</script>
<style lang="scss" scoped>
.panel-title {
    font-size: 25px;
    color: #409EFF;
}
</style>
  