<template>
    <div>
        <el-card>
            <div slot="header" class="clearfix" shadow="always">
                <span class="panel-title">资源管理</span>
                <br>
                <el-row :gutter="20">
                    <el-col :span="5">
                        <el-button style="margin-top: 10px;" type="primary" size="small"
                            @click="openResourceDialog('add', null)" icon="el-icon-plus">添加资源信息</el-button>
                    </el-col>
                    <el-col :span="5">
                        <el-input style="margin-top: 10px;" size="medium" v-model="keyword" placeholder=""
                            @keyup.enter.native="getByKeyword">
                            <el-button slot="append" icon="el-icon-search" @click="getByKeyword"></el-button>
                        </el-input>
                    </el-col>
                </el-row>
            </div>
            <div v-if="resourceList.length == 0">
                <el-empty description="暂无资源信息"></el-empty>
            </div>
            <div v-else>
                <el-table :data="resourceList" style="width: 100%">
                    <el-table-column label="名称" width="100">
                        <template slot-scope="scope">
                            <span>{{ scope.row.name }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="描述" width="300">
                        <template slot-scope="scope">
                            <span>{{ scope.row.description }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="资源标签id" width="100">
                        <template slot-scope="scope">
                            <span>{{ getTagName(scope.row.rtId) }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="用户是否可见" width="150">
                        <template slot-scope="scope">
                            <span>{{ scope.row.visible == 0 ? '是' : '否' }}</span>
                        </template>
                    </el-table-column>
                    <el-table-column label="图片" width="200">
                        <template slot-scope="scope">
                            <img :src="scope.row.img" style="width: 50px; height: 50px;" />
                        </template>
                    </el-table-column>

                    <el-table-column label="操作" min-width="150">
                        <template slot-scope="scope">
                            <el-button size="mini" @click="openResourceDialog('edit', scope.row)">编辑</el-button>
                            <el-button size="mini" type="danger" @click="deleteResource(scope.row)">删除</el-button>
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
                <el-form-item label="名称" prop="name">
                    <el-input v-model="resource.name"></el-input>
                </el-form-item>
                <el-form-item label="资源描述" prop="description">
                    <el-input v-model="resource.description"></el-input>
                </el-form-item>
                <el-form-item label="资源地址" prop="target">
                    <el-input v-model="resource.target"></el-input>
                </el-form-item>
                <el-form-item label="资源标签" prop="rtId">
                    <el-select v-model="resource.rtId" placeholder="请选择">
                        <el-option v-for="item in tagList" :key="item.id" :label="item.name" :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="资源封面" prop="img">
                    <el-upload ref="upload" :action="uploadImg" :show-file-list="false" :file-list="photoList"
                        :on-change="changePhotoFile" :on-success="handleAvatarSuccess" :before-upload="beforeAvatarUpload"
                        :auto-upload="false">
                        <img v-show="resource.img" :src="resource.img">
                        <div v-show="!resource.img">
                            <i class="el-icon-plus"></i>
                        </div>
                    </el-upload>
                    <my-cropper ref="myCropper" @getFile="getFile" @upAgain="upAgain"></my-cropper>
                </el-form-item>
                <el-form-item label="普通用户是否可见" prop="visible">
                    <el-switch v-model="resource.visible" active-text="不可见" inactive-text="可见" active-value="1"
                        inactive-value="0">
                    </el-switch>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="upsertResource()">保存</el-button>
                    <el-button @click="showEditDialog = false">取消</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>
    </div>
</template>
  
<script>
import myMessage from '@/utils/message'
import resourceApi from '@/api/resource'
import { mapGetters } from 'vuex'
import MyCropper from '@/components/admin/MyCropper.vue'

export default {
    name: 'ResourceList',
    components: {
        MyCropper
    },
    data() {
        return {
            uploadImg: '/xoj/file/uploadImg', // 图片上传地址
            imageUrl: '',  // 图片地址
            photoList: [], // 文件列表 
            pageSize: 10,
            total: 0,
            currentPage: 0,
            keyword: '',
            resourceList: [],
            tagList: [],
            resource: {
                id: '',
                name: '',
                description: '',
                target: '',
                rtId: '',   // 资源标签id
                img: '',    // 资源封面
                visible: 0, // 普通用户是否可见
            },
            showEditDialog: false,
            dialogTitle: '添加资源信息'
        }
    },
    mounted() {
        this.init()
    },
    methods: {
        init() {
            this.getResourceList()
            this.getTag()
        },
        getTag() {
            resourceApi.getTagList().then(
                (res) => {
                    this.tagList = res.data.data
                },
                (err) => {

                }
            )
        },
        getResourceList() {
            resourceApi.getResourceList(this.pageSize, this.currentPage, this.keyword).then(
                (res) => {
                    this.resourceList = res.data.data.records
                    this.total = res.data.data.total
                },
                (err) => {

                }
            )
        },
        getByKeyword() {
            this.pageSize = 10
            this.currentPage = 1
            this.getResourceList()
        },
        upsertResource() {
            if (this.resource.id) {
                resourceApi.updateResource(this.resource).then(
                    (res) => {
                        myMessage.success('修改成功')
                        this.resource = {}
                        this.keyword = ''
                        this.currentPage = 1
                        this.getResourceList()
                        this.showEditDialog = false
                    },
                    (err) => {
                        myMessage.error('修改失败')
                        this.showEditDialog = false
                    }
                )
            } else {
                resourceApi.addResource(this.resource).then(
                    (res) => {
                        myMessage.success('添加成功')
                        this.resource = {}
                        this.keyword = ''
                        this.currentPage = 1
                        this.getResourceList()
                        this.showEditDialog = false
                    },
                    (err) => {
                        myMessage.error('添加失败')
                        this.showEditDialog = false
                    }
                )
            }
        },
        deleteResource(resource) {
            this.$confirm('确认删除？', 'Tips', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }).then(
                () => {
                    resourceApi.deleteResource(resource.id).then(
                        (res) => {
                            myMessage.success("删除成功！")
                            this.currentPage = 1
                            this.total = 0
                            this.getResourceList()
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
            this.getResourceList()
        },
        getTagName(tid) {
            for (const item of this.tagList) {
                if (item.id === tid) {
                    return item.name;
                }
            }
        },
        openResourceDialog(action, resource) {
            if (action == 'add') {
                this.dialogTitle = '添加资源信息'
                this.resource = {}
            } else {
                this.dialogTitle = '修改资源信息'
                this.resource.id = resource.id
                this.resource.name = resource.name
                this.resource.description = resource.description
                this.resource.target = resource.target
                this.resource.img = resource.img
                this.resource.rtId = resource.rtId
                this.resource.visible = resource.visible
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
                this.resource.img = res.url;
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
                    this.resource.img = res.data.data
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
  