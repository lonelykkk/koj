<template>
    <div class="category-container">
        <el-card class="box-card">
            <div slot="header" class="clearfix" shadow="always">
                <span class="panel-title">贴子分类</span>
                <br>
                <el-button style="margin-top: 10px;" type="primary" size="small" @click="openCategoryDialog('add', null)"
                    icon="el-icon-plus">添加分类</el-button>
            </div>
            <div v-if="categoryList.length == 0">
                <el-empty description="暂无分类"></el-empty>
            </div>
            <div v-else>
                <el-tag v-for="item in categoryList" :key="item.index" closable 
                    effect="dark" :disable-transitions="false" @click="openCategoryDialog('update', item)"
                    @close="deleteCategory(item)">
                    {{ item.name }}
                </el-tag>
            </div>
        </el-card>

        <el-dialog :title="addCategoryDialogTitle" :visible.sync="addCategoryDialogVisible" width="30%" center>
            <el-form>
                <el-form-item label="分类名称" prop="name">
                    <el-input v-model="category.name"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="upsertCategory()">{{ addCategoryDialogTitle }}</el-button>
                    <el-button @click="addCategoryDialogVisible = false">取消</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>

    </div>
</template>
  
<script>
import categoryApi from '@/api/discussion'
import myMessage from '@/utils/message'

export default {
    created() {
        console.log('discussion category created')
        this.init()
    },
    data() {
        return {
            category: {
                id: '',
                name: ''
            },
            categoryList: [],
            addCategoryDialogVisible: false,
            addCategoryDialogTitle: '',
            upsertCategoryLoading: false
        }
    },
    methods: {
        init() {
            this.getCategoryList()
        },
        getCategoryList() {
            categoryApi.getCategoryList().then(
                (res) => {
                    this.categoryList = res.data.data
                },
                (err) => {

                }
            )
        },
        upsertCategory() {
            if (this.category.id) {
                this.upsertCategoryLoading = true
                categoryApi.updateCategory(this.category).then(
                    (res) => {
                        this.upsertCategoryLoading = false
                        myMessage.success(res.data.message)
                        this.addCategoryDialogVisible = false
                        this.getCategoryList()
                    },
                    (err) => {
                        this.upsertCategoryLoading = false
                        myMessage.error('更新失败')
                        this.addCategoryDialogVisible = false
                    }
                )
            } else {
                this.upsertCategoryLoading = true
                categoryApi.addCategory(this.category).then(
                    (res) => {
                        this.upsertCategoryLoading = false
                        myMessage.success(res.data.message)
                        this.addCategoryDialogVisible = false
                        this.getCategoryList()
                    },
                    (err) => {
                        this.upsertCategoryLoading = false
                        myMessage.error('添加失败')
                        this.addCategoryDialogVisible = false
                    }
                )
            }
        },
        deleteCategory(category) {
            this.$confirm('确认删除？', 'Tips', {
                type: 'warning',
            }).then(
                () => {
                    categoryApi.deleteCategory(category.id)
                        .then((res) => {
                            myMessage.success('删除成功');
                            this.getCategoryList()
                        })
                        .catch(() => { });
                },
                () => { }
            );
        },
        openCategoryDialog(action, category) {
            if (action == 'add') {
                this.addCategoryDialogTitle = '添加分类'
                this.category = {}
            } else {
                this.addCategoryDialogTitle = '修改分类'
                this.category.id = category.id;
                this.category.name = category.name;
            }
            this.addCategoryDialogVisible = true
        }
    }
}

</script>
  
<style lang="scss">
.category-container {
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