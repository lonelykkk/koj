<template>
    <div class="box">
        <el-card class="header">
            <el-row :gutter="20">
                <el-col :span="18">
                    <el-button class="tag-btn" :type="selectedTagId == null ? 'primary' : 'default'" size="mini"
                        @click="getResources(undefined)">
                        全部
                    </el-button>
                    <el-button class="tag-btn" v-for="(tag, index) in tagList" :key="index"
                        :type="selectedTagId === tag.id ? 'primary' : 'default'" size="mini" @click="getResources(tag.id)">
                        {{ tag.name }}
                    </el-button>
                </el-col>
                <el-col :span="6">
                    <span class="search">
                        <vxe-input v-model="keyword" placeholder="输入关键词" type="search" size="mini"
                            @keyup.enter.native="getResources(undefined)" @search-click="getResources(undefined)">
                        </vxe-input>
                    </span>
                </el-col>
            </el-row>
        </el-card>

        <div class="continer">
            <template v-if="resourceList.length > 0">
                <div class="resource-info" v-for="(item, index) in resourceList" :key="index">
                    <div class="resource-tag">
                        <span class="bj"></span>
                        <span>{{ item.resourceTag.name }}</span>
                    </div>



                    <div v-for="(resource, index) in item.resourceList" :key="index" class="resource-item">
                        <div class="resource-img">
                            <img :src="resource.img" class="resource-img">
                        </div>
                        <div style="text-align: center;">
                            <el-tooltip class="item" effect="dark" :content="resource.description" placement="top">
                                <el-link :underline="false" @click="goTarget(resource.target)">
                                    <span>{{ resource.name }}</span>
                                </el-link>
                            </el-tooltip>
                        </div>
                    </div>

                </div>
            </template>
            <template v-else>
                <el-empty description="暂无数据"></el-empty>
            </template>
        </div>
    </div>
</template>
  
<script>
import myMessage from '@/utils/message'
import resourceApi from '@/api/resource'
import { mapGetters } from 'vuex'


export default {
    name: 'ResourceList',
    components: {

    },
    data() {
        return {
            resourceList: [],
            tagList: [],
            keyword: '',
            selectedTag: null,
            selectedTagId: null,
        }
    },
    mounted() {
        this.init()
    },
    methods: {
        init() {
            this.getResources(undefined)
            this.getTags()
        },
        getResources(tid) {
            this.selectedTagId = tid
            resourceApi.getResources(this.keyword, tid).then(
                (res) => {
                    this.resourceList = res.data.data
                }
            )
        },
        getTags() {
            resourceApi.getTags().then(
                (res) => {
                    this.tagList = res.data.data
                }
            )
        },
        goTarget(target) {
            window.open(target)
        }
    },
    computed: {
        ...mapGetters([
            "isAuthenticated",
            "userInfo",
            "isAdminRole"
        ])
    }
}


</script>
<style lang="scss" scoped>
.box {
    padding: 0 4%;
}

.tag-btn {
    height: 100%;
}

::v-deep .el-card__body {
    padding: 15px;
}

.bj {
    background-color: #2196f3;
    width: 6px;
    height: 18px;
    display: inline-block;
    vertical-align: middle;
    border-radius: 0px 2px 2px 0px;
    margin-right: 12px;
}

.search {
    margin-top: 3px;
    margin-right: 6px;
    float: right;
}

.continer {
    margin-top: 20px;
}

.resource-info {
    margin: 10px 0px;
    padding: 10px 0px;
    background-color: #FFF;
    border-radius: 5px;
    font-size: 14px;

    .resource-tag {
        margin-top: 10px;
        margin-bottom: 10px;
    }

    .resource-item {
        margin-left: 50px;
        margin-bottom: 10px;
        width: 125px;
        height: auto;
        display: inline-block;
    }

    .resource-item:hover {
        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
    }

    .resource-img {
        width: 125px;
        height: 125px;
        border-radius: 2px;
    }

}
</style>
  