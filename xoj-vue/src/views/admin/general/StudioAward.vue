<template>
    <div>
        <el-card>
            <div slot="header" class="clearfix" shadow="always">
                <span class="panel-title">奖项管理</span>
                <br>
                <el-row :gutter="20">
                    <el-col :span="5">
                        <el-button style="margin-top: 10px;" type="primary" size="small" @click="openDialog('add', null)"
                            icon="el-icon-plus">添加奖项信息</el-button>
                    </el-col>
                    <el-col :span="5">
                        <el-input style="margin-top: 10px;" size="medium" v-model="keyword" placeholder=""
                            @keyup.enter.native="getByKeyword">
                            <el-button slot="append" icon="el-icon-search" @click="getByKeyword"></el-button>
                        </el-input>
                    </el-col>
                </el-row>
            </div>
            <div v-if="studioAwardList.length == 0">
                <el-empty description="暂无奖项信息"></el-empty>
            </div>
            <div v-else>
                <el-table :data="studioAwardList" style="width: 100%">
                    <el-table-column label="奖项名称" width="100">
                        <template slot-scope="scope">
                            <span>{{ scope.row.name }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="获奖者" width="150">
                        <template slot-scope="scope">
                            <span>{{ scope.row.author }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="获奖时间" width="200">
                        <template slot-scope="scope">
                            <span>{{ scope.row.getTime | localtime }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="图片" width="200">
                        <template slot-scope="scope">
                            <img :src="scope.row.img" style="width: 50px; height: 50px;"/>
                        </template>
                    </el-table-column>

                    <el-table-column label="操作" min-width="150">
                        <template slot-scope="scope">
                            <el-button size="mini" @click="openDialog('edit', scope.row)">编辑</el-button>
                            <el-button size="mini" type="danger"
                                @click="deleteAward(scope.row)">删除</el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="page-container">
                    <el-pagination background @current-change="handleCurrentChange" :current-page.sync="currentPage"
                        layout="total, prev, pager, next" :total="total" :page-size="pageSize">
                    </el-pagination>
                </div>
            </div>
        </el-card>

        <el-dialog :title="dialogTitle" :visible.sync="showEditDialog" width="35%" center>
            <el-form>
                <el-form-item label="奖项名称" prop="name">
                    <el-input v-model="studioAward.name"></el-input>
                </el-form-item>
                <el-form-item label="获奖者" prop="author">
                    <el-input v-model="studioAward.author"></el-input>
                </el-form-item>
                <el-form-item label="获奖时间" prop="getTime">
                    <el-date-picker v-model="studioAward.getTime" type="date" placeholder="选择日期">
                    </el-date-picker>
                </el-form-item>
                <el-form-item label="获奖证书" prop="img">
                    <el-upload ref="upload" :action="uploadImg" :show-file-list="false" :file-list="photoList"
                        :on-change="changePhotoFile" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload"
                        :auto-upload="false">
                        <img v-if="studioAward.img" :src="studioAward.img">
                        <div v-else>
                            <i class="el-icon-plus"></i>
                        </div>
                    </el-upload>
                    <my-cropper ref="myCropper" @getFile="getFile" @upAgain="upAgain"></my-cropper>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="upsertStudioAwards()">保存</el-button>
                    <el-button @click="showEditDialog = false">取消</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>
    </div>
</template>
  
<script>
import myMessage from '@/utils/message'
import studioInfoApi from '@/api/studioInfo'
import { mapGetters} from 'vuex'
import MyCropper from '@/components/admin/MyCropper.vue'
export default {
    name: 'StudioAward',
    components: {
        MyCropper
    },
    data() {
        return {
            uploadImg: '/api/xoj/file/uploadImg', // 图片上传地址
            imageUrl: '',  // 图片地址
            photoList: [], // 文件列表 
            pageSize: 10,
            total: 0,
            currentPage: 0,
            keyword: '',
            studioAwardList: [],
            studioAward: {
                id: '',
                name: '',
                author: '',
                getTime: '',
                img: ''
            },
            showEditDialog: false,
            dialogTitle: '添加奖项信息'
        }
    },
    mounted() {
        
        this.init()
    },
    methods: {
        init() {
            this.getStudioAwardList()
        },
        getStudioAwardList() {
            studioInfoApi.getAwardsList(this.pageSize, this.currentPage, this.keyword).then(
                (res) => {
                    this.studioAwardList = res.data.data.records
                    this.total = res.data.data.total
                },
                (err) => {

                }
            )
        },
        getByKeyword() {
            this.pageSize = 10
            this.currentPage = 1
            this.getStudioAwardList()
        },
        upsertStudioAwards() {
            console.log(this.studioAward)
            if (this.studioAward.id) {
                studioInfoApi.updateAwards(this.studioAward).then(
                    (res) => {
                        myMessage.success('修改成功')
                        this.studioAward = {}
                        this.keyword = ''
                        this.currentPage = 1
                        this.getStudioAwardList()
                        this.showEditDialog = false
                    },
                    (err) => {
                        myMessage.error('修改失败')
                        this.showEditDialog = false
                    }
                )
            } else {
                studioInfoApi.addAwards(this.studioAward).then(
                    (res) => {
                        myMessage.success('添加成功')
                        this.studioAward = {}
                        this.keyword = ''
                        this.currentPage = 1
                        this.getStudioAwardList()
                        this.showEditDialog = false
                    },
                    (err) => {
                        myMessage.error('添加失败')
                        this.showEditDialog = false
                    }
                )
            }
        },
        deleteAward(studioAward) {
            this.$confirm('确认删除？', 'Tips', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }).then(
                () => {
                    studioInfoApi.deleteAwards(studioAward.id).then(
                        (res) => {
                            myMessage.success("删除成功！")
                            this.currentPage = 1
                            this.total = 0
                            this.getStudioAwardList()
                        },
                        (err) => {
                            myMessage.error("删除失败！")
                        }
                    )
                },
                () => { }
            );
        },
        handleCurrentChange(page) {
            this.currentPage = page
            this.getStudioAwardList()
        },
        openDialog(action, studioAward) {
            if (action == 'add') {
                this.dialogTitle = '添加奖项信息'
                this.studioAward = {}
            } else {
                this.dialogTitle = '修改奖项信息'
                this.studioAward.id = studioAward.id
                this.studioAward.name = studioAward.name
                this.studioAward.author = studioAward.author
                this.studioAward.getTime = studioAward.getTime
                this.studioAward.img = studioAward.img
            }
            this.showEditDialog = true
        },


        changePhotoFile(file, fileList) {
            if (fileList.length > 0) {
                this.photoList = [fileList[fileList.length - 1]]
            }
            this.handleCrop(file);
        },
        handleCrop(file) {
            this.$nextTick(() => {
                this.$refs.myCropper.open(file.raw || file)
            })
        },
        // 头像上传成功之后的方法,进行回调
        handleAvatarSuccess(res, file) {
            if (res.code === 0) {
                // this.companyInfo.logo = res.filename;
                this.studioAward.img = res.url;
                this.imageUrl = res.url;
            } else {
                myMessage.error('上传出错');
            }
        },
        // 头像上传之前的方法
        beforeAvatarUpload(file) {
            const isJPG =
                file.type === 'image/jpeg' ||
                'image/jpg' ||
                'image/gif' ||
                'image/png';
            const isLt6M = file.size / 1024 / 1024 < 6;

            if (!isJPG) {
                myMessage.error('上传头像图片只能是 JPG、JPEG、GIF或PNG 格式!')
            }
            if (!isLt6M) {
                myMessage.error('上传头像图片大小不能超过 6MB!');
            }
            console.log("275==", file)

            return isJPG && isLt6M;
        },

        // 点击弹框重新时触发
        upAgain() {
            this.$refs['upload'].$refs["upload-inner"].handleClick();
        },
        getFile(file) {
            const formData = new FormData()
            formData.append("file", file)
            this.$axios({
                method: 'post',
                url: '/api/xoj/file/uploadImg',
                data: formData,
                headers: { 'content-type': 'multipart/form-data' },
            }).then(
                (res) => {
                    myMessage.success('上传成功')
                    this.imageUrl = res.data.data
                    this.studioAward.img = res.data.data
                    //上传成功后，关闭弹框组件
                    this.$refs.myCropper.close()
                },
                () => {
                    myMessage.success('上传失败')
                }
            );

        },

    },
    computed: {
        ...mapGetters(['userInfo']),
    },
}

</script>
<style lang="scss" scoped></style>
  