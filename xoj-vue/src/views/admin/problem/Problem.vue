<template>
    <div class="problem-container">
        <el-card class="box-card">
            <div slot="header" class="clearfix" shadow="always">
                <span class="panel-title">{{ title }}</span>
            </div>
            <div>
                <el-steps :active="active" finish-status="success" align-center>
                    <el-step title="步骤 1" description="填写题目基本信息" icon="el-icon-edit"></el-step>
                    <el-step title="步骤 2" description="上传题目测试用例" icon="el-icon-upload"></el-step>
                    <el-step title="步骤 3" description="确认题目信息" icon="el-icon-document-checked"></el-step>
                </el-steps>

                <el-form label-position="top" style="margin-left: 50px; padding: 10px;">
                    <div v-show="active == 0">
                        <el-row :gutter="20">
                            <el-form-item label="题目展示ID">
                                <el-input v-model="problem.problemId"></el-input>
                            </el-form-item>
                        </el-row>

                        <el-row :gutter="20">
                            <el-form-item label="标题">
                                <el-input v-model="problem.title"></el-input>
                            </el-form-item>
                        </el-row>

                        <el-row :gutter="20">
                            <el-form-item label="题目描述">
                                <Editor :value.sync="description"></Editor>
                            </el-form-item>
                        </el-row>

                        <el-row :gutter="20">
                            <el-col :md="6" :xs="24">
                                <el-form-item label="内存限制(mb)" required>
                                    <el-input type="Number" placeholder="内存限制" v-model="problem.memoryLimit"></el-input>
                                </el-form-item>
                            </el-col>
                            <el-col :md="6" :xs="24">
                                <el-form-item label="时间限制(ms)" required>
                                    <el-input type="Number" placeholder="时间限制" v-model="problem.timeLimit"></el-input>
                                </el-form-item>
                            </el-col>
                            <el-col :md="6" :xs="24">
                                <el-form-item label="栈限制(mb)" required>
                                    <el-input type="Number" placeholder="栈限制" v-model="problem.stackLimit"></el-input>
                                </el-form-item>
                            </el-col>
                            <el-col :md="6" :xs="24">
                                <el-form-item label="难度" required>
                                    <el-select class="difficulty-select" placeholder="Enter the level of problem"
                                        v-model="problem.difficulty">
                                        <el-option :label="getLevelName(key)" :value="parseInt(key)"
                                            v-for="(value, key, index) in PROBLEM_LEVEL" :key="index"></el-option>
                                    </el-select>
                                </el-form-item>
                            </el-col>
                        </el-row>

                        <el-row :gutter="20">
                            <el-col :span="24">
                                <el-form-item label="输入描述">
                                    <Editor :value.sync="problem.input"></Editor>
                                </el-form-item>
                            </el-col>
                            <el-col :span="24">
                                <el-form-item label="输出描述">
                                    <Editor :value.sync="problem.output"></Editor>
                                </el-form-item>
                            </el-col>
                            <el-col :span="24">
                                <el-form-item label="提示">
                                    <Editor :value.sync="problem.hint"></Editor>
                                </el-form-item>
                            </el-col>
                        </el-row>

                        <el-row :gutter="20">
                            <el-col :md="8" :xs="24">
                                <el-form-item label="权限">
                                    <el-select v-model="problem.auth">
                                        <el-option label="公开题目" :value="1"></el-option>
                                        <el-option label="私有题目" :value="2"></el-option>
                                        <el-option label="考核题目" :value="3"></el-option>
                                    </el-select>
                                </el-form-item>
                            </el-col>
                            <el-col :md="8" :xs="24">
                                <el-form-item label="类型">
                                    <el-radio-group v-model="problem.type">
                                        <el-radio :label="0">ACM</el-radio>
                                        <el-radio :label="1">OI</el-radio>
                                    </el-radio-group>
                                </el-form-item>
                            </el-col>
                            <el-col :md="8" :xs="24">
                                <el-form-item label="题目标签">
                                    <el-select v-model="tagList" multiple placeholder="请选择" value-key="name"
                                        @change="addTag" @remove-tag="removeTag">
                                        <el-option v-for="item in orgTagList" :key="item.index" :label="item.name"
                                            :value="item">
                                        </el-option>
                                    </el-select>
                                </el-form-item>
                            </el-col>
                        </el-row>

                        <div>
                            <span class="panel-title">添加题目样例</span>
                            <el-form-item v-for="(example, index) in problem.examples" :key="index"
                                class="examples-container">
                                <Accordion :title='"样例" + (index + 1)' :isOpen="true" :index="index"
                                    @changeVisible="changeExampleVisible">
                                    <el-button type="danger" size="small" icon="el-icon-delete" slot="header"
                                        @click="deleteExample(index)">
                                        删除
                                    </el-button>
                                    <el-row :gutter="20">
                                        <el-col :xs="24" :md="12">
                                            <el-form-item label="样例输入" required>
                                                <el-input :rows="5" type="textarea" placeholder="样例输入"
                                                    v-model="example.input" style="white-space: pre-line">
                                                </el-input>
                                            </el-form-item>
                                        </el-col>
                                        <el-col :xs="24" :md="12">
                                            <el-form-item label="样例输出" required>
                                                <el-input :rows="5" type="textarea" placeholder="样例输出"
                                                    v-model="example.output">
                                                </el-input>
                                            </el-form-item>
                                        </el-col>
                                    </el-row>
                                </Accordion>
                            </el-form-item>
                        </div>

                        <div class="add-example-btn">
                            <el-button class="add-examples" @click="addExample()" icon="el-icon-plus" type="small">
                                添加样例
                            </el-button>
                        </div>

                        <el-form-item label="题目来源">
                            <el-input placeholder="题目来源" v-model="problem.source"></el-input>
                        </el-form-item>

                        <el-form-item label="自动去除代码每行末尾空白符">
                            <el-switch v-model="problem.isRemoveEndBlank" active-text="" inactive-text="">
                            </el-switch>
                        </el-form-item>

                        <el-form-item label="公开评测点数据结构">
                            <el-switch v-model="problem.openCaseResult" active-text="" inactive-text="">
                            </el-switch>
                        </el-form-item>

                    </div>
                    <div v-show="active == 1">
                        <el-row :gutter="20">
                            <el-form-item required>
                                <el-switch v-model="problem.isUploadCase" active-text="使用上传文件" inactive-text="使用手动输入"
                                    style="margin: 10px 0">
                                </el-switch>
                            </el-form-item>
                            <div v-show="problem.isUploadCase">
                                <el-col :span="24">
                                    <el-form-item>
                                        <el-upload :action="uploadFileUrl" name="file" :show-file-list="true"
                                            :on-success="uploadSucceeded" :on-error="uploadFailed">
                                            <el-button size="small" type="primary" icon="el-icon-upload">
                                                选择文件
                                            </el-button>
                                        </el-upload>
                                    </el-form-item>
                                </el-col>
                            </div>
                            <div v-show="!problem.isUploadCase">
                                <el-form-item v-for="(item, index) in problemSamples" :key="index">
                                    <Accordion :title="'评测数据' + (index + 1)" :isOpen="true" :index="index"
                                        @changeVisible="changeSampleVisible">
                                        <el-button type="danger" size="small" icon="el-icon-delete" slot="header"
                                            @click="deleteSample(index)">
                                            删除
                                        </el-button>
                                        <el-row :gutter="20">
                                            <el-col :xs="24" :md="12">
                                                <el-form-item label="评测数据输入" required>
                                                    <el-input :rows="5" type="textarea" placeholder="评测数据输入"
                                                        v-model="item.input">
                                                    </el-input>
                                                </el-form-item>
                                            </el-col>
                                            <el-col :xs="24" :md="12">
                                                <el-form-item label="评测数据输出" required>
                                                    <el-input :rows="5" type="textarea" placeholder="评测数据输出"
                                                        v-model="item.output">
                                                    </el-input>
                                                </el-form-item>
                                            </el-col>
                                        </el-row>
                                    </Accordion>
                                </el-form-item>
                                <div class="add-sample-btn">
                                    <el-button class="add-samples" @click="addSample()" icon="el-icon-plus" type="small">
                                        添加评测数据
                                    </el-button>
                                </div>
                            </div>
                        </el-row>
                    </div>
                    <div v-show="active == 2">
                        <el-row class="problem-box">
                            <div :padding="10" shadow class="problem-detail">
                                <div slot="header">
                                    <span class="panel-title" style="margin-left=5px;">标题：{{ problem.title }}</span><br />
                                    <div v-if="tagList.length > 0" class="problem-tag">
                                        <el-tag v-for="(tag, index) in tagList" :key="index" size="small"
                                            :color="tag.color ? tag.color : '#409eff'" effect="dark" style="margin:5px;">
                                            {{ tag.name }}
                                        </el-tag>
                                    </div>
                                    <div v-else class="problem-tag">
                                        <el-tag effect="plain" size="small" style="margin:5px;">
                                            暂无标签
                                        </el-tag>
                                    </div>
                                    <div class="question-intr">

                                        <div class="question-info">
                                            时间限制：C/C++ {{ problem.timeLimit }}MS，
                                            其他语言：{{ problem.timeLimit * 2 }}MS
                                        </div>
                                        <div class="question-info">
                                            内存限制：C/C++ {{ problem.memoryLimit }}MB，
                                            其他语言：{{ problem.memoryLimit * 2 }}MB
                                        </div>
                                        <div class="question-info">
                                            栈&nbsp;限&nbsp;制&nbsp;：C/C++ {{ problem.stackLimit }}MB，
                                            其他语言：{{ problem.stackLimit * 2 }}MB
                                        </div>

                                        <div class="question-info" v-if="problem.difficulty != null">
                                            <span>
                                                难度：
                                                <span class="el-tag el-tag--small"
                                                    :style="getLevelColor(problem.difficulty)">
                                                    {{ getLevelName(problem.difficulty) }}
                                                </span>
                                            </span>
                                        </div>

                                        <div class="question-info">
                                            <span>
                                                题目类型：{{ problem.type == 0 ? 'ACM' : 'OI' }}
                                            </span>
                                        </div>

                                        <div class="question-info" v-if="problem.author">
                                            <span>
                                                出题人：{{ problem.author }}
                                            </span>
                                        </div>
                                    </div>
                                </div>

                                <div id="problem-content">
                                    <template v-if="description">
                                        <p class="title">描述</p>
                                        <p class="content markdown-body" v-html="description" v-katex v-highlight></p>
                                    </template>

                                    <template v-if="problem.input">
                                        <p class="title">输入描述</p>
                                        <p class="content markdown-body" v-html="problem.input" v-katex v-highlight></p>
                                    </template>

                                    <template v-if="problem.output">
                                        <p class="title">输出描述</p>
                                        <p class="content markdown-body" v-html="problem.output" v-katex v-highlight></p>
                                    </template>

                                    <template v-if="problem.examples">
                                        <div v-for="(example, index) of problem.examples" :key="index">
                                            <div class="flex-container example">
                                                <div class="example-input">
                                                    <p class="title">
                                                        样例输入 {{ index + 1 }}
                                                        <a class="copy">
                                                            <i class="el-icon-document-copy"></i>
                                                        </a>
                                                    </p>
                                                    <pre class="example-text">{{ example.input }}</pre>
                                                </div>
                                                <div class="example-output">
                                                    <p class="title">
                                                        样例输出 {{ index + 1 }}
                                                        <a class="copy">
                                                            <i class="el-icon-document-copy"></i>
                                                        </a>
                                                    </p>
                                                    <pre class="example-text">{{ example.output }}</pre>
                                                </div>
                                            </div>
                                        </div>
                                    </template>

                                    <template v-if="problem.hint">
                                        <p class="title">提示</p>
                                        <el-card dis-hover>
                                            <p class="hint-content markdown-body" v-html="problem.hint" v-katex v-highlight>
                                            </p>
                                        </el-card>
                                    </template>

                                    <template v-if="problem.source">
                                        <p class="title">来源</p>
                                        <p class="content" v-html="problem.source"></p>
                                    </template>
                                </div>
                            </div>
                        </el-row>

                    </div>
                </el-form>

                <div>
                    <el-button v-if="active > 0" style="margin-top: 12px;" @click="pre">上一步</el-button>
                    <el-button v-if="active < 2" style="margin-top: 12px;" @click="next">下一步</el-button>
                    <el-button v-else style="margin-top: 12px;" @click="commit">提交</el-button>
                </div>


            </div>
        </el-card>
    </div>
</template>
  
<script>
import problemApi from '@/api/problem'
import myMessage from '@/utils/message'
import utils from '@/utils/utils'
import { PROBLEM_LEVEL } from '@/common/constants'
import tagRequ from '@/api/tag'

const Editor = () => import('@/components/admin/Editor.vue')
const Accordion = () => import('@/components/admin/Accordion.vue')

export default {
    components: {
        Editor,
        Accordion
    },
    created() {
        console.log('problem created')
    },
    data() {
        return {
            routeName: "",
            mode: "", // 该题目是编辑或者创建
            title: "",
            active: 0,
            uploadFileUrl: "/api/xoj/file/upload-testcase-zip", // 文件上传地址
            testCaseUploaded: false,
            problem: {
                id: null,
                problemId: "",
                title: "",
                author: "",
                input: "", // 输入描述
                output: "", // 输出描述
                examples: [], // 题面上的样例输入输出
                source: "", // 题目来源
                timeLimit: 1000,
                memoryLimit: 256,
                stackLimit: 128,
                difficulty: 0,
                auth: 1,
                isUploadCase: true,
                type: 0, // 默认为ACM
                hint: "",
                isRemoveEndBlank: true, // 是否去除用户代码的文末空格
                openCaseResult: true, // 是否开启题目测试样例的查看
            },
            description: '', // 题目描述
            inputSamples: [], // 输入样例数据
            outputSamples: [], // 输出样例数据
            problemSamples: [],
            isUploadZipTestCase: true,
            isUpdateTestcase: false,
            uploadFileDir: "",
            zipFileDir: "",
            tagList: [],
            PROBLEM_LEVEL: {},
            orgTagList: [],
            sampleIndex: 0
            // error: {
            //     tags: "",
            //     testCase: ""
            // },
        }
    },
    mounted() {
        this.routeName = this.$route.name
        this.PROBLEM_LEVEL = Object.assign({}, PROBLEM_LEVEL)
        if (
            this.routeName === "admin-edit-problem" ||
            this.routeName === "admin-edit-contest-problem"
        ) {
            this.mode = "edit"
        } else {
            this.mode = "add"
        }
        this.init()
    },
    methods: {
        init() {
            if (this.mode === 'edit') {
                this.pid = this.$route.params.problemId
                this.backPath = this.$route.query.back
                this.title = '编辑题目'
                problemApi.getProblem(this.pid).then(
                    (res) => {
                        this.problem = res.data.data.problem
                        this.description = res.data.data.description
                        this.orgTagList = res.data.data.tags
                    },
                    (err) => {

                    }
                )
            } else {
                this.title = '创建题目'
                this.addExample()
            }
            this.getTagList()
        },
        getLevelName(level) {
            return utils.getLevelName(level)
        },
        getLevelColor(difficulty) {
            return utils.getLevelColor(difficulty);
        },
        // 添加题目样例
        addExample() {
            this.problem.examples.push({ input: "", output: "", isOpen: true })
        },
        // 删除题目样例
        deleteExample(index) {
            this.problem.examples.splice(index, 1)
        },
        changeExampleVisible(index, isOpen) {
            // this.problem.examples[index]["isOpen"] = isOpen
            this.problem.examples[index].isOpen = isOpen
        },
        addSample() {
            console.log('addSample');
            this.problemSamples.push({
                input: "",
                output: "",
                isOpen: true
            })
        },
        deleteSample(index) {
            console.log('deleteSample = ' + index);
            this.problemSamples.splice(index, 1)
        },
        changeSampleVisible(index, isOpen) {
            this.problemSamples[index].isOpen = isOpen
        },
        commit() {
            console.log('commit')
            // 校验参数
            // 题目展示id
            if (!this.problem.problemId) {
                myMessage.error('题目展示ID不能为空！');
                return;
            }
            // 题面样例
            if (!this.problem.examples.length) {
                myMessage.error('题面样例不能为空！');
                return;
            }
            if (!this.description) {
                myMessage.error('题目描述不能为空！')
            }
            // 题目标签
            let problemTagList = null
            if (this.tagList.length > 0) {
                problemTagList = Object.assign([], this.tagList);
                for (let i = 0; i < problemTagList.length; i++) {
                    //避免后台插入违反唯一性
                    for (let tag2 of this.orgTagList) {
                        if (problemTagList[i].name == tag2.name) {
                            problemTagList[i] = tag2;
                            break;
                        }
                    }
                }
            }
            // 选择手动输入
            this.isUploadZipTestCase = this.problem.isUploadCase
            if (!this.problem.isUploadCase) {
                if (!this.problemSamples.length) {
                    myMessage.error('题目测试用例不能为空！');
                    return;
                }

                for (let sample of this.problemSamples) {
                    if (!sample.input && !sample.output) {
                        myMessage.error('评测输入数据 or 评测输出数据不能为空！');
                        return;
                    }
                    this.inputSamples.push(sample.input)
                    this.outputSamples.push(sample.output)
                }

            } else {
                // 选择上传文件
                if (!this.uploadFileDir || !this.zipFileDir) {
                    myMessage.error('未上传评测数据文件！请上传！')
                }
            }

            let problemDto = {}; // 上传给后台的数据
            problemDto['problem'] = Object.assign({}, this.problem)
            problemDto.problem.examples = utils.examplesToString(
                this.problem.examples
            ); // 需要转换格式
            problemDto['description'] = this.description
            problemDto['inputSamples'] = this.inputSamples
            problemDto['outputSamples'] = this.outputSamples
            problemDto['isUploadZipTestCase'] = this.isUploadZipTestCase
            problemDto['isUpdateTestcase'] = this.isUpdateTestcase
            problemDto['uploadFileDir'] = this.uploadFileDir
            problemDto['zipFileDir'] = this.zipFileDir
            problemDto['tagList'] = problemTagList

            console.log(problemDto);
            problemApi.addProblem(problemDto).then(
                (res) => {
                    if (res.data.code == 20000) {
                        myMessage.success(res.data.message)
                        this.$router.push({ path: '/admin/problem' })
                    } else {
                        myMessage.error(res.data.message)
                    }
                },
                (error) => {
                    myMessage.error('添加失败！')
                }
            )
        },
        getTagList() {
            tagRequ.getAllTag().then(
                (res) => {
                    this.orgTagList = res.data.data
                },
                (err) => { }
            )
        },
        addTag(item) {
        },
        removeTag(item) {
        },
        uploadSucceeded(response) {
            if (response.code != 20000) {
                myMessage.error('测试用例上传失败');
                this.testCaseUploaded = false;
                return;
            }
            myMessage.success('测试用例上传成功');
            this.testCaseUploaded = true;
            this.zipFileDir = response.data.zipFileDir
            this.uploadFileDir = response.data.fileDir
            console.log('zipFileDir = ' + this.zipFileDir)
            console.log('uploadFileDir = ' + this.uploadFileDir)
        },
        uploadFailed() {
            myMessage.error('测试用例上传失败');
        },
        onCopy(event) {
            myMessage.success('复制成功');
        },
        onCopyError(e) {
            myMessage.success('复制失败');
        },
        pre() {
            if (this.active-- == -1) this.active = 0
            // if (this.active == 0) this.active = 0
            // else this.active--
        },
        next() {
            if (this.active++ > 2) this.active = 0
        }
    },

}

</script>
  
<style lang="scss">
.katex .katex-mathml {
    display: none;
}

.problem-container {
    background-color: #FFF;

    .panel-title {
        font-size: 25px;
        color: #409EFF;
    }

    .examples-container {
        margin-top: 10px;
    }

    .add-example-btn {
        margin: 10px 0px;

        .add-examples {
            width: 100%;
            background-color: #fff;
            border: 1px dashed #2d8cf0;
            outline: none;
            cursor: pointer;
            color: #2d8cf0;
            height: 35px;
            font-size: 14px;

        }

        .add-examples i {
            margin-right: 10px;
        }

        .add-examples:hover {
            border: 0px;
            background-color: #2d8cf0 !important;
            color: #fff;
        }
    }

    .add-sample-btn {
        margin-bottom: 10px;

        .add-samples {
            width: 100%;
            background-color: #fff;
            border: 1px dashed #19be6b;
            outline: none;
            cursor: pointer;
            color: #19be6b;
            height: 35px;
            font-size: 14px;
        }

        .add-samples i {
            margin-right: 10px;
        }

        .add-samples:hover {
            border: 0px;
            background-color: #19be6b !important;
            color: #fff;
        }
    }

    .problem-detail {
        .problem-detail {

            color: black;

            .problem-tag {
                display: inline;
            }

            .question-intr {
                margin-top: 10px 0;
                // margin-top: 30px;
                // border-radius: 4px;
                // border: 1px solid #ddd;
                // border-left: 2px solid #3498db;
                // background: #fafafa;
                // padding: 10px;
                // line-height: 1.8;
                // margin-bottom: 10px;
                // font-size: 14px;
            }

        }
    }
}

.question-info {
    margin: 5px 0px;
}

#problem-content {
    .content {
        margin-left: 25px;
        margin-right: 20px;
        font-size: 15px;
    }

    .title {
        font-size: 16px;
        font-weight: 600;
        margin: 20px 0 8px 0;
        color: #3091f2;
    }

    .copy {
        padding-left: 8px;
    }
}

.example-text {
    background-color: rgb(246, 248, 250);
    padding: 2px;
    height: 20px;
    font-size: 16px;
    border-radius: 3px;
}
</style>