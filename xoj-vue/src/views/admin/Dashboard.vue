<template>
    <div>
        <el-row :gutter="20">
            <el-col :span="12">
                <div class="system-info">
                    <el-card class="box-card">
                        <el-descriptions title="系统运行信息" :column="2">
                            <el-descriptions-item label="系统名称">
                                {{ systemInfo.systemName }}
                            </el-descriptions-item>
                            <el-descriptions-item label="系统版本">
                                {{ systemInfo.systemVersion }}
                            </el-descriptions-item>
                            <el-descriptions-item label="系统核心数">
                                {{ systemInfo.systemProcessors }}
                            </el-descriptions-item>
                            <el-descriptions-item label="总内存">
                                {{ systemInfo.totalMemory }} MB
                            </el-descriptions-item>
                            <el-descriptions-item label="已使用内存">
                                {{ systemInfo.useMemory }} MB
                            </el-descriptions-item>
                            <el-descriptions-item label="空闲内存">
                                {{ systemInfo.freeMemory }} MB
                            </el-descriptions-item>
                            <el-descriptions-item label="CPU使用率">
                                <el-progress type="circle" :percentage="systemInfo.cpuUsage"></el-progress>
                            </el-descriptions-item>
                            <el-descriptions-item label="内存使用率">
                                <el-progress type="circle" :percentage="systemInfo.memoryUsage"></el-progress>
                            </el-descriptions-item>
                        </el-descriptions>


                    </el-card>
                </div>
            </el-col>
            <el-col :span="12">
                <div class="other-info">
                    <el-card class="box-card">
                        <el-descriptions title="其他信息" :column="2">
                        </el-descriptions>
                        <el-empty description="暂无信息"></el-empty>
                    </el-card>
                </div>
            </el-col>
        </el-row>
    </div>
</template>
  
<script>
import api from '@/api/dashboard'
import utils from '@/utils/utils'
import myMessage from '@/utils/message'

export default {
    created() {
        this.init()
    },
    data() {
        return {
            systemInfo: null
        }
    },
    methods: {
        init() {
            this.getSystemInfo()
        },
        getSystemInfo() {
            api.getSystemInfo().then(
                (res) => {
                    this.systemInfo = res.data.data
                },
                (error) => {

                }
            )
        }
    }
}

</script>
  
<style lang="scss"></style>
  