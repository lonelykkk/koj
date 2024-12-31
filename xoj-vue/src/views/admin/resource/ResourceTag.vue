<template>
    <div class="container">
        <el-card class="box-card">
            <div slot="header" class="clearfix" shadow="always">
                <span class="panel-title">资源分类</span>
                <br>
                <el-button style="margin-top: 10px;" type="primary" size="small" @click="openTagDialog('add', null)"
                    icon="el-icon-plus">添加分类</el-button>
            </div>
            <div v-if="tagList.length == 0">
                <el-empty description="暂无分类"></el-empty>
            </div>
            <div v-else>
                <el-tag v-for="item in tagList" :key="item.index" closable 
                    effect="dark" :disable-transitions="false" @click="openTagDialog('update', item)"
                    @close="deleteTag(item)">
                    {{ item.name }}
                </el-tag>
            </div>
        </el-card>

        <el-dialog :title="addTagDialogTitle" :visible.sync="addTagDialogVisible" width="30%" center>
            <el-form>
                <el-form-item label="分类名称" prop="name">
                    <el-input v-model="tag.name"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="upsertTag()">{{ addTagDialogTitle }}</el-button>
                    <el-button @click="addTagDialogVisible = false">取消</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>

    </div>
</template>
  
<script>
import resourceApi from '@/api/resource'
import myMessage from '@/utils/message'

export default {
    created() {
        this.init()
    },
    data() {
        return {
            tag: {
                id: '',
                name: ''
            },
            tagList: [],
            addTagDialogVisible: false,
            addTagDialogTitle: '',
            upsertTagLoading: false
        }
    },
    methods: {
        init() {
            this.getTagList()
        },
        getTagList() {
            resourceApi.getTagList().then(
                (res) => {
                    this.tagList = res.data.data
                },
                (err) => {

                }
            )
        },
        upsertTag() {
            if (this.tag.id) {
                this.upsertTagLoading = true
                resourceApi.updateResourceTag(this.tag).then(
                    (res) => {
                        this.upsertTagLoading = false
                        myMessage.success(res.data.message)
                        this.addTagDialogVisible = false
                        this.getTagList()
                    },
                    (err) => {
                        this.upsertTagLoading = false
                        myMessage.error('更新失败')
                        this.addTagDialogVisible = false
                    }
                )
            } else {
                this.upsertTagLoading = true
                resourceApi.addTag(this.tag).then(
                    (res) => {
                        this.upsertTagLoading = false
                        myMessage.success(res.data.message)
                        this.addTagDialogVisible = false
                        this.getTagList()
                    },
                    (err) => {
                        this.upsertTagLoading = false
                        myMessage.error('添加失败')
                        this.addTagDialogVisible = false
                    }
                )
            }
        },
        deleteTag(tag) {
            this.$confirm('确认删除？', 'Tips', {
                type: 'warning',
            }).then(
                () => {
                    resourceApi.deleteResourceTag(tag.id)
                        .then((res) => {
                            myMessage.success('删除成功');
                            this.getTagList()
                        })
                        .catch(() => { });
                },
                () => { }
            );
        },
        openTagDialog(action, tag) {
            if (action == 'add') {
                this.addTagDialogTitle = '添加分类'
                this.tag = {}
            } else {
                this.addTagDialogTitle = '修改分类'
                this.tag.id = tag.id;
                this.tag.name = tag.name;
            }
            this.addTagDialogVisible = true
        }
    }
}

</script>
  
<style lang="scss" scoped>
.container {
    background-color: #FFF;

    .panel-title {
        font-size: 25px;
        color: #409EFF;
    }

    .el-tag {
        margin-left: 10px;
        margin-top: 10px;
    }
}
</style>