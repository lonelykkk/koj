<template>
    <div class="container">
        <div class="about-us">
            <el-row>
                <el-col :span="12">
                    <img :src="sutdioInfoImg" width="650px" class="imgs1" />
                </el-col>
                <el-col :span="12">
                    <div>
                        <div class="title">磐石工作室</div>
                        <br />
                        <div class="info">
                            磐石工作室成立于2011年，是隶属于软件学院的一个学习组织，由邓泓老师负责并指导。工作室的主线为：农业信息化及图像处理。团队成员由本科生组成，成立至今共有成员40多名。工作室重选拔、重品德，以肯学好学为重，才思敏捷为次，努力为学校的信息化、多元化做出自己的贡献，尽自己所能服务于大众。
                        </div>
                    </div>
                </el-col>
            </el-row>
        </div>
        <div class="teacher-info">
            <el-row>
                <el-col :span="12">
                    <div>
                        <div class="title2">指导老师：邓泓</div>
                        <br />
                        <div class="info2">teacher info</div>
                    </div>
                </el-col>
                <el-col :span="12">
                    <img :src="teacherImg" width="200px" class="imgs2" />
                </el-col>
            </el-row>
        </div>
        <div class="about-us">
            <el-card class="box-card">
                <div>
                    <el-row>
                        <el-col :span="12">
                            <div style="text-align: center;">
                                <el-button type="text" @click="changeVisible(true)">往届成员信息</el-button>
                            </div>
                        </el-col>
                        <el-col :span="12">
                            <div style="text-align: center;">
                                <el-button type="text" @click="changeVisible(false)">成员获奖信息</el-button>
                            </div>
                        </el-col>

                        <div>
                            <div v-show="visible" class="member-info">
                                <div class="no-info" v-if="studioInfoList.length == 0">
                                    <el-empty description="暂无成员信息"></el-empty>
                                </div>
                                <div v-else>
                                    <el-table :data="studioInfoList" style="width: 100%">
                                        <el-table-column label="姓名" width="200">
                                            <template slot-scope="scope">
                                                <span>{{ scope.row.name }}</span>
                                            </template>
                                        </el-table-column>
                                        <el-table-column label="届数" width="200">
                                            <template slot-scope="scope">
                                                <span>{{ scope.row.whichSession }}</span>
                                            </template>
                                        </el-table-column>
                                        <el-table-column label="班级" width="200">
                                            <template slot-scope="scope">
                                                <span>{{ scope.row.classes }}</span>
                                            </template>
                                        </el-table-column>
                                        <el-table-column label="QQ" width="250">
                                            <template slot-scope="scope">
                                                <span>{{ scope.row.qq }}</span>
                                            </template>
                                        </el-table-column>
                                        <el-table-column label="考研院校" width="250">
                                            <template slot-scope="scope">
                                                <span>{{ scope.row.school }}</span>
                                            </template>
                                        </el-table-column>
                                        <el-table-column label="就业公司" width="250">
                                            <template slot-scope="scope">
                                                <span>{{ scope.row.company }}</span>
                                            </template>
                                        </el-table-column>
                                    </el-table>
                                </div>
                            </div>
                            <div v-show="!visible" class="awards-info">
                                <div v-if="studioAwardsList.length == 0">
                                    <el-empty description="暂无奖项信息"></el-empty>
                                </div>
                                <div class="no-awards" v-else>
                                    <el-empty style="text-align: center;" description="暂无奖项信息"></el-empty>
                                </div>
                                <!-- <el-card v-for="(awards, index) in studioAwardsList" :key="index">
                                    <img :src="awards.img"  style="width: 50px; height: 50px;"/>
                                    <div style="padding: 14px; text-align: center;">
                                        <span>{{ awards.name }}</span> 
                                        <br />
                                        <span>{{ awards.author }}</span>
                                        <br />
                                        <span>{{ awards.getTime | localtime }}</span>
                                    </div>
                                </el-card> -->
                            </div>
                        </div>
                    </el-row>
                </div>
            </el-card>
        </div>
    </div>
</template>
  
<script>
import myMessage from '@/utils/message'
import studioInfo from '@/api/studioInfo'

export default {
    name: 'AboutOur',
    components: {

    },
    data() {
        return {
            sutdioInfoImg: require('@/assets/studioInfo.jpg'),
            teacherImg: require('@/assets/teacherImg.png'),
            visible: true,
            studioInfoList: [],
            studioAwardsList: []
        }
    },
    created() {

    },
    mounted() {
        this.init()
    },
    methods: {
        init() {
            this.getStudioInfo()
            // this.getStudioAwards()
        },
        getStudioInfo() {
            studioInfo.getMember().then(
                (res) => {
                    this.studioInfoList = res.data.data
                }
            )
        },
        getStudioAwards() {
            // studioInfo.getAwards().then(
            //     (res) => {
            //         this.studioAwardsList = res.data.data.records
            //     }
            // )
        },
        changeVisible(flag) {
            this.visible = flag
        }
    },
    computed: {

    }
}

</script>
<style lang="scss" scoped>
// .container {
//     padding: 0 4%;
// }

.imgs1 {
    border-radius: 5px;
    float: right;
    margin-right: 20px;
}

.imgs2 {
    border-radius: 5px;
    float: left;
}

.about-us {
    margin-bottom: 200px;
}

.teacher-info {
    margin-bottom: 200px;
}

.title {
    font-weight: 600;
    font-size: 30px;
    line-height: 30px;
    color: #1d2127;
    margin-top: 70px;
}

.title2 {
    font-weight: 600;
    font-size: 25px;
    line-height: 30px;
    color: #1d2127;
    margin-top: 25px;
    text-align: center;
}


.info {
    font-size: 20px;
    padding-top: 10px;
}

.info2 {
    font-size: 20px;
    padding-top: 10px;
    margin-left: 200px;
}

.member-info {
    margin-top: 10px;
}

.awards-info {
    margin-top: 10px;
    text-align: center;
}

.no-awards {
    text-align: center;
    font-size: 16px;
}

.no-info {
    text-align: center;
    font-size: 16px;
}
</style>
  