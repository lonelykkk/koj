<template>
    <div style="margin: 0px 0px 15px 0px;font-size: 14px;position: relative">
        <el-row class="header">
            <el-col :xs="24" :sm="15" :md="15" :lg="15">
                <div class="select-row">
                    <span>语言:</span>
                    <span>
                        <el-select :value="this.language" @change="onLangChange" class="left-select left-adjust"
                            size="small">
                            <el-option v-for="item in languages" :key="item" :value="item">
                                {{ item }}
                            </el-option>
                        </el-select>
                    </span>
                    <span>
                        <el-tooltip content="重置代码" placement="top">
                            <el-button icon="el-icon-refresh" @click="onResetClick" class="left-adjust"
                                size="small"></el-button>
                        </el-tooltip>
                    </span>
                </div>
            </el-col>
            <el-col :xs="2" :sm="9" :md="9" :lg="9">
                <div class="select-row fl-right">
                    <el-tooltip content="代码编辑器设置" placement="top">
                        <el-popover placement="bottom" width="300" trigger="click">
                            <el-button slot="reference" icon="el-icon-s-tools" size="small">
                            </el-button>
                            <div class="setting-title">设置</div>
                            <div class="setting-item">
                                <span class="setting-item-name">
                                    <i class="fa fa-tachometer"></i>
                                    主题
                                </span>
                                <el-select :value="this.theme" @change="onThemeChange" class="setting-item-value"
                                    size="small">
                                    <el-option v-for="item in themes" :key="item.label" :label="item.label"
                                        :value="item.value">
                                        {{ item.label }}
                                    </el-option>
                                </el-select>
                            </div>
                            <div class="setting-item">
                                <span class="setting-item-name">
                                    <i class="fa fa-font"></i>
                                    字体大小
                                </span>
                                <el-select :value="fontSize" @change="onFontSizeChange" class="setting-item-value"
                                    size="small">
                                    <el-option v-for="item in fontSizes" :key="item" :label="item" :value="item">{{ item }}
                                    </el-option>
                                </el-select>
                            </div>
                            <div class="setting-item">
                                <span class="setting-item-name">
                                    <svg focusable="false" viewBox="0 0 1024 1024" fill="currentColor" width="1.2em"
                                        height="1.2em" style="vertical-align: text-bottom;" aria-hidden="true">
                                        <g transform="translate(101.57 355.48)">
                                            <rect width="812.53" height="152.35" x="0" y="0" rx="50.78"></rect>
                                            <rect width="812.53" height="50.78" x="0" y="253.92" rx="25.39"></rect>
                                            <rect width="50.78" height="203.13" x="0" y="177.74" rx="25.39"></rect>
                                            <rect width="50.78" height="203.13" x="761.75" y="177.74" rx="25.39"></rect>
                                        </g>
                                    </svg>
                                    Tab长度
                                </span>
                                <el-select :value="tabSize" @change="onTabSizeChange" class="setting-item-value"
                                    size="small">
                                    <el-option label="2个空格" :value="2">
                                        2个空格
                                    </el-option>
                                    <el-option label="4个空格" :value="4">
                                        4个空格
                                    </el-option>
                                    <el-option label="8个空格" :value="8">
                                        8个空格
                                    </el-option>
                                </el-select>
                            </div>

                        </el-popover>
                    </el-tooltip>
                </div>
            </el-col>
        </el-row>
        <div :style="'line-height: 1.5;font-size:' + fontSize">
            <codemirror class="js-right" :value="value" :options="options" @change="onEditorCodeChange" ref="myEditor">
            </codemirror>
        </div>
        <el-drawer :visible.sync="openTestCaseDrawer" style="position: absolute;" :modal="false" size="40%"
            :with-header="false" @close="closeDrawer" direction="btt">
            <el-tabs v-model="testJudgeActiveTab" type="border-card" style="height: auto; width: 100%;">
                <el-tab-pane label="测试用例" name="input" style="margin-right: 15px;margin-top: 8px;">
                    <!-- <div class="mt-10">
                        <el-tag type="primary" class="tj-test-tag" size="samll" v-for="(example, index) of problemTestCase"
                            :key="index" @click="addTestCaseToTestJudge(example.input, example.output, index)"
                            :effect="example.active ? 'dark' : 'plain'">
                            填充用例 {{ index + 1 }}
                        </el-tag>
                    </div> -->
                    <el-input type="textarea" class="mt-10" :rows="5" show-word-limit resize="none" maxlength="1000"
                        v-model="userInput">
                    </el-input>
                </el-tab-pane>
                <el-tab-pane label="运行结果" name="result">
                    <div v-loading="testJudgeLoding">
                        <template v-if="testJudgeRes.status == -10">
                            <div class="tj-res-tab mt-10">
                                <el-alert title="输入测试用例后，点击运行自测，这里将会显示运行结果" type="info" center :closable="false" show-icon>
                                </el-alert>
                            </div>
                        </template>
                        <template v-else-if="testJudgeRes.status != -2">
                            <div class="tj-res-tab">
                                <el-alert class="mt-10"
                                    :type="getResultStausType(testJudgeRes.problemJudgeMode, testJudgeRes.status)"
                                    :closable="false" show-icon>
                                    <template slot="title">
                                        <span class="status-title">
                                            {{ getResultStatusName(testJudgeRes.problemJudgeMode,
                                                testJudgeRes.status, testJudgeRes.expectedOutput != null) }}
                                        </span>
                                    </template>
                                    <template slot>
                                        <div style="display:flex">
                                            <div style="margin-right:15px">
                                                <span class="color-gray mr-5"><i class="el-icon-time"></i></span>
                                                <span class="color-gray mr-5">运行时间</span>
                                                <span v-if="testJudgeRes.time != null">{{ testJudgeRes.time }}ms</span>
                                                <span v-else>--ms</span>
                                            </div>
                                            <div>
                                                <span style="vertical-align: sub;" class="color-gray mr-5">
                                                    <svg data-v-79a9c93e="" focusable="false" viewBox="0 0 1025 1024"
                                                        fill="currentColor" width="1.2em" height="1.2em" aria-hidden="true">
                                                        <path
                                                            d="M448.98 92.52V92.67l.37 40.46v62.75h125.5V92.52h81.22v103.36h33.12c76.08 0 137.9 61.05 139.12 136.84l.02 2.3v33.12h103.36v80.84l-40.6.37h-62.76v91.05h103.36v81.21H828.33v67.58c0 76.08-61.06 137.9-136.84 139.12l-2.3.02h-33.12v103.36h-80.84l-.37-40.6v-62.76H449.25l-.27 103.36h-80.84V828.33h-33.12c-76.08 0-137.9-61.06-139.12-136.84l-.02-2.3v-67.58H92.52v-80.83l40.6-.38h62.76v-91.15l-103.36-.27v-80.47l40.6-.37h62.76v-33.12c0-76.08 61.05-137.9 136.84-139.12l2.3-.02h33.12V92.52h80.84zM689.2 277.1H335.02c-32 0-57.93 25.93-57.93 57.93v354.17c0 32 25.93 57.93 57.93 57.93h354.17c32 0 57.93-25.94 57.93-57.93V335.02c0-32-25.94-57.93-57.93-57.93zm-73.73 91.05a40.6 40.6 0 0 1 40.6 40.6v206.72a40.6 40.6 0 0 1-40.6 40.6H408.75a40.6 40.6 0 0 1-40.6-40.6V408.75a40.6 40.6 0 0 1 40.6-40.6zm-40.6 81.16H449.3v125.55h125.55V449.3z"
                                                            transform="translate(1)">
                                                        </path>
                                                    </svg>
                                                </span>
                                                <span class="color-gray mr-5">运行内存</span>
                                                <span v-if="testJudgeRes.memory != null">{{ testJudgeRes.memory }}KB</span>
                                                <span v-else>--KB</span>
                                            </div>
                                        </div>
                                        <div v-if="testJudgeRes.stderr">
                                            {{ testJudgeRes.stderr }}
                                        </div>
                                    </template>
                                </el-alert>
                            </div>
                            <div class="tj-res-tab">
                                <div class="tj-res-item">
                                    <span class="name">自测输入</span>
                                    <span class="value">
                                        <el-input type="textarea" class="textarea" :readonly="true" resize="none"
                                            :autosize="{ minRows: 1, maxRows: 4 }" v-model="testJudgeRes.userInput">
                                        </el-input>
                                    </span>
                                </div>
                                <div class="tj-res-item" v-if="testJudgeRes.expectedOutput != null">
                                    <span class="name">预期输出</span>
                                    <span class="value">
                                        <el-input type="textarea" :readonly="true" resize="none"
                                            :autosize="{ minRows: 1, maxRows: 4 }" class="textarea"
                                            v-model="testJudgeRes.expectedOutput">
                                        </el-input>
                                    </span>
                                </div>
                                <div class="tj-res-item">
                                    <span class="name">实际输出</span>
                                    <span class="value">
                                        <el-input type="textarea" class="textarea" :readonly="true" resize="none"
                                            :autosize="{ minRows: 1, maxRows: 4 }" v-model="testJudgeRes.userOutput">
                                        </el-input>
                                    </span>
                                </div>
                            </div>
                        </template>
                        <template v-else>
                            <div class="tj-res-tab mt-10">
                                <el-card>
                                    <div slot="header">
                                        <span class="ce-title">编译失败</span>
                                    </div>
                                    <div style="color: #f90;font-weight: 600;">
                                        <pre>{{ testJudgeRes.stderr }}</pre>
                                    </div>
                                </el-card>
                            </div>
                        </template>
                    </div>
                </el-tab-pane>
                <el-tab-pane>
                    <span slot="label">
                        <el-tag type="success" @click="submitTestJudge" effect="plain">
                            <i class="el-icon-video-play"> 运行自测 </i>
                        </el-tag>
                    </span>
                    <template v-if="!isAuthenticated">
                        <div class="tj-res-tab mt-10">
                            <el-alert title="请登录" type="warning" center :closable="false" show-icon>
                            </el-alert>
                        </div>
                    </template>
                </el-tab-pane>
            </el-tabs>
        </el-drawer>
    </div>
</template>
  
<script>
import problemApi from '@/api/problem'
import judgeApi from '@/api/judge'
import myMessage from '@/utils/message'
import utils from '@/utils/utils'
import {
    JUDGE_STATUS,
    JUDGE_STATUS_RESERVE
} from '@/common/constants'
import { codemirror, CodeMirror } from 'vue-codemirror-lite';

// 风格对应的样式
import 'codemirror/theme/monokai.css';
import 'codemirror/theme/solarized.css';
import 'codemirror/theme/material.css';
import 'codemirror/theme/idea.css';
import 'codemirror/theme/eclipse.css';
import 'codemirror/theme/base16-dark.css';
import 'codemirror/theme/cobalt.css';
import 'codemirror/theme/dracula.css';

// highlightSelectionMatches
import 'codemirror/addon/scroll/annotatescrollbar.js';
import 'codemirror/addon/search/matchesonscrollbar.js';
import 'codemirror/addon/dialog/dialog.js';
import 'codemirror/addon/dialog/dialog.css';
import 'codemirror/addon/search/searchcursor.js';
import 'codemirror/addon/search/search.js';
import 'codemirror/addon/search/match-highlighter.js';

// mode
import 'codemirror/mode/clike/clike.js';
import 'codemirror/mode/python/python.js';
import 'codemirror/mode/pascal/pascal.js'; //pascal
import 'codemirror/mode/go/go.js'; //go
import 'codemirror/mode/d/d.js'; //d
import 'codemirror/mode/haskell/haskell.js'; //haskell
import 'codemirror/mode/mllike/mllike.js'; //OCaml
import 'codemirror/mode/perl/perl.js'; //perl
import 'codemirror/mode/php/php.js'; //php
import 'codemirror/mode/ruby/ruby.js'; //ruby
import 'codemirror/mode/rust/rust.js'; //rust
import 'codemirror/mode/javascript/javascript.js'; //javascript
import 'codemirror/mode/fortran/fortran.js'; //fortran

// active-line.js
import 'codemirror/addon/selection/active-line.js';

// foldGutter
import 'codemirror/addon/fold/foldgutter.css';
import 'codemirror/addon/fold/foldgutter.js';

import 'codemirror/addon/edit/matchbrackets.js';
import 'codemirror/addon/edit/matchtags.js';
import 'codemirror/addon/edit/closetag.js';
import 'codemirror/addon/edit/closebrackets.js';
import 'codemirror/addon/fold/brace-fold.js';
import 'codemirror/addon/fold/indent-fold.js';
import 'codemirror/addon/hint/show-hint.css';
import 'codemirror/addon/hint/show-hint.js';
import 'codemirror/addon/hint/anyword-hint.js';
import 'codemirror/addon/selection/mark-selection.js';

export default {
    name: 'CodeMirror',
    components: {
        codemirror,
    },
    props: {
        value: {
            type: String,
            default: '',
        },
        language: {
            type: String,
            default: 'C++',
        },
        theme: {
            type: String,
            default: 'solarized',
        },
        fontSize: {
            type: String,
            default: '14px',
        },
        tabSize: {
            type: Number,
            default: 4,
        },
        height: {
            type: Number,
            default: 550
        },
        openTestCaseDrawer: {
            type: Boolean,
            default: false
        },
        pid: {
            type: Number,
        },
        isAuthenticated: {
            type: Boolean,
            default: false,
        },
    },
    data() {
        return {
            options: {
                // codemirror options
                tabSize: this.tabSize,
                mode: 'text/x-c++src',
                theme: 'solarized',
                // 显示行号
                lineNumbers: true,
                line: true,
                // 代码折叠
                foldGutter: true,
                gutters: ['CodeMirror-linenumbers', 'CodeMirror-foldgutter'],
                lineWrapping: true,
                // 选中文本自动高亮，及高亮方式
                styleSelectedText: true,
                showCursorWhenSelecting: true,
                highlightSelectionMatches: { showToken: /\w/, annotateScrollbar: true },
                // extraKeys: { Ctrl: 'autocomplete' }, //自定义快捷键
                matchBrackets: true, //括号匹配
                indentUnit: this.tabSize, //一个块（编辑语言中的含义）应缩进多少个空格
                styleActiveLine: true,
                autoCloseBrackets: true,
                autoCloseTags: true,
                hintOptions: {
                    // 当匹配只有一项的时候是否自动补全
                    completeSingle: false,
                },
            },
            mode: {
                // C: 'text/x-csrc',
                'C++': 'text/x-c++src',
                Java: 'text/x-java',
                Python2: 'text/x-python',
                Python3: 'text/x-python'
            },
            themes: [
                { label: 'monokai', value: 'monokai' },
                { label: 'solarized', value: 'solarized' },
                { label: 'material', value: 'material' },
                { label: 'idea', value: 'idea' },
                { label: 'eclipse', value: 'eclipse' },
                { label: 'base16_dark', value: 'base16-dark' },
                { label: 'cobalt', value: 'cobalt' },
                { label: 'dracula', value: 'dracula' },
            ],
            fontSizes: ['12px', '14px', '16px', '18px', '20px'],
            languages: ['C++', 'Java', 'Python3', 'Python2'],
            testJudgeActiveTab: 'input',
            userInput: '',
            expectedOutput: null,
            testJudgeRes: {
                status: -10,
                problemJudgeMode: 'default'
            },
            testJudgeKey: null,
            testJudgeLoding: false,
        }
    },
    mounted() {
        this.init()
    },
    methods: {
        init() {

        },
        submitTestJudge() {
            console.log("submit test judge")
            if (!this.isAuthenticated) {
                myMessage.warning("请先登录!");
                // this.$store.dispatch("changeModalStatus", { visible: true });
                return;
            }

            if (this.value.trim() === "") {
                myMessage.error("代码不能为空");
                return;
            }

            if (this.value.length > 65535) {
                myMessage.error("代码的字符长度不能超过65535！");
                return;
            }

            let data = {
                pid: this.pid,
                language: this.language,
                code: this.value,
                type: this.type,
                userInput: this.userInput,
                expectedOutput: this.expectedOutput,
                mode: this.mode[this.language]
            }

            judgeApi.submitProblemTestJudge(data).then(
                (res) => {
                    this.testJudgeKey = res.data.data
                    this.testJudgeActiveTab = 'result'
                    this.testJudgeLoding = true
                    this.checkTestJudgeStatus()
                },
                (err) => {
                    this.testJudgeActiveTab = 'input'
                }
            )
        },
        checkTestJudgeStatus() {
            console.log('checkTestJudgeStatus')
            let timerId = setInterval(() => {
                judgeApi.getTestJudgeResult(this.testJudgeKey).then(
                    (res) => {
                        let resData = res.data.data
                        console.log('resData = ' + resData);
                        if (resData.status != JUDGE_STATUS_RESERVE["Pending"]) {
                            this.testJudgeRes = resData
                            clearInterval(timerId); // 停止定时器
                        }
                        this.testJudgeLoding = false
                    },
                    (err) => {
                        this.testJudgeLoding = false
                    }
                )
            }, 1500)
        },
        getResultStausType(problemJudgeMode, status) {
            if (problemJudgeMode == "spj" && (status == 0 || status == -1)) {
                return "success";
            }
            return this.status.type;
        },
        getResultStatusName(problemJudgeMode, status, hasExpectedOutput) {
            if (problemJudgeMode == "spj" && (status == 0 || status == -1)) {
                return "Success";
            }
            if (status == 0 && !hasExpectedOutput) {
                return "Success";
            }
            return this.status.statusName;
        },
        onEditorCodeChange(newCode) {
            this.$emit('update:value', newCode)
        },
        onLangChange(newVal) {
            this.editor.setOption('mode', this.mode[newVal])
            this.$emit('changeLang', newVal)
        },
        onThemeChange(newTheme) {
            this.editor.setOption('theme', newTheme)
            this.$emit('changeTheme', newTheme)
        },
        onFontSizeChange(fontSize) {
            this.fontSize = fontSize;
            this.$emit('update:fontSize', fontSize);
        },
        onTabSizeChange(tabSize) {
            this.tabSize = tabSize;
            this.$emit('update:tabSize', tabSize);
            this.editor.setOption('tabSize', tabSize);
            // this.editor.setOption('indentUnit', tabSize);
        },
        onResetClick() {
            this.$emit('resetCode')
        },
        closeDrawer() {
            this.$emit('update:openTestCaseDrawer', false);
        }
    },
    computed: {
        editor() {
            // get current editor object
            return this.$refs.myEditor.editor;
        },
        status() {
            return {
                type: JUDGE_STATUS[this.testJudgeRes.status].type,
                statusName: JUDGE_STATUS[this.testJudgeRes.status].name,
                color: JUDGE_STATUS[this.testJudgeRes.status].rgb,
            };
        },
        currentLanguage() {
            return this.language;
        },
    },
    watch: {
        theme(newVal, oldVal) {
            this.editor.setOption('theme', newVal);
        },
        fontSize(newVal, oldVal) {
            this.editor.refresh();
        }
    },
    beforeDestroy() {
        // 防止切换组件后仍然不断请求
        // clearInterval(this.refreshStatus);
    }
}

</script>
<style lang="scss">
.header {
    margin-bottom: 10px;
    margin-right: 5px;
    margin-left: 5px;
}

.header {
    .left-select {
        width: 170px;
    }

    .left-adjust {
        margin-left: 5px;
    }

    .fl-right {
        float: right;
    }
}

.mt-10 {
    margin-top: 10px;
}

.tj-btn {
    // font-size: 13px;
    // font-weight: 600;
    // border: 1px solid #32ca99;
}

.tj-btn:hover {
    // background-color: #d5f1eb;
}

.tj-test-tag {
    margin-right: 15px;
    cursor: pointer;
}

.tj-test-tag:hover {
    font-weight: 600;
}

.tj-res-tab {
    padding-right: 15px;
}

.tj-res-item {
    display: flex;
    margin-top: 10px;
}

.tj-res-item .name {
    flex: 2;
    text-align: center;
    line-height: 34px;
    font-size: 12px;
}

.tj-res-item .value {
    flex: 10;
}

.setting-title {
    border-bottom: 1px solid #f3f3f6;
    color: #000;
    font-weight: 700;
    padding: 10px 0;
}

.setting-item {
    display: flex;
    padding: 15px 0 0;
}

.setting-item-name {
    flex: 2;
    color: #333;
    font-weight: 700;
    font-size: 13px;
    margin-top: 7px;
}

.setting-item-value {
    width: 140px;
    margin-left: 15px;
    flex: 5;
}

.select-row {
    margin-top: 4px;
}
</style>
  