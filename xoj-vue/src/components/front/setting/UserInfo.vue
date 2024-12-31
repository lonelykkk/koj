<template>
    <div>
        <div class="section-title">头像设置</div>
        <div class="section-main">
            <avatar :username="formProfile.username" :inline="true" :size="130" color="#FFF" style="margin-bottom:15px"
                :src="avatar"></avatar>
            <template v-if="!avatarOption.imgSrc">
                <el-upload class="upload-container" action="" drag :before-upload="handleSelectFile">
                    <div style="padding: 20px 0">
                        <i class="el-icon-upload" style="color: #3399ff;font-size:52px"></i>
                        <p>将头像拖放到此处，或单击此处</p>
                    </div>
                </el-upload>
            </template>

            <template v-else>
                <el-row :gutter="20">
                    <el-col :xs="24" :md="12">
                        <div class="cropper-main inline">
                            <vueCropper ref="cropper" autoCrop fixed :autoCropWidth="200" :autoCropHeight="200"
                                :img="avatarOption.imgSrc" :outputSize="avatarOption.size"
                                :outputType="avatarOption.outputType" :info="true" @realTime="realTime">
                            </vueCropper>
                        </div>
                        <div class="cropper-btn">
                            <el-tooltip class="item" effect="dark" content="向左旋转90°" trigger="hover" placement="bottom">
                                <el-button @click="rotate('left')" icon="el-icon-refresh-left" size="mini"></el-button>
                            </el-tooltip>
                            <el-tooltip class="item" effect="dark" content="向右旋转90°" trigger="hover" placement="bottom">
                                <el-button @click="rotate('right')" icon="el-icon-refresh-right" size="mini"></el-button>
                            </el-tooltip>
                            <el-tooltip class="item" effect="dark" content="关闭图像截取" trigger="hover" placement="bottom">
                                <el-button @click="reselect" icon="el-icon-refresh" size="mini"></el-button>
                            </el-tooltip>
                            <el-tooltip class="item" effect="dark" trigger="hover" content="确定图像截取" placement="bottom">
                                <el-button @click="finishCrop" icon="el-icon-check" size="mini"></el-button>
                            </el-tooltip>
                        </div>
                    </el-col>
                    <el-col :xs="24" :md="12">
                        <div class="cropper-preview" :style="previewStyle">
                            <div :style="preview.div">
                                <img :src="avatarOption.imgSrc" :style="preview.img" />
                            </div>
                        </div>
                    </el-col>
                </el-row>
            </template>
            <el-dialog :visible.sync="uploadModalVisible" title="上传" width="350px">
                <div class="upload-modal">
                    <p class="notice">您的新头像：</p>
                    <img :src="uploadImgSrc" />
                </div>
                <div slot="footer">
                    <el-button @click="uploadAvatar" :loading="loadingUploadBtn" type="primary">
                        上传
                    </el-button>
                </div>
            </el-dialog>
        </div>

        <div class="section-title">资料设置</div>
        <el-form ref="formProfile" :model="formProfile">
            <el-row :gutter="30" justify="space-around">
                <el-col :md="10" :xs="24">
                    <el-form-item label="真实姓名">
                        <el-input v-model="formProfile.realname" :maxlength="50" />
                    </el-form-item>
                    <el-form-item label="昵称">
                        <el-input v-model="formProfile.nickname" :maxlength="20" />
                    </el-form-item>
                </el-col>
                <el-col :md="4" :lg="4">
                    &nbsp;
                    <div class="separator hidden-md-and-down"></div>
                    <p></p>
                </el-col>
                <el-col :md="10" :xs="24">
                    <el-form-item label="班级">
                        <el-input v-model="formProfile.classe" :maxlength="50" />
                    </el-form-item>
                    <el-form-item label="学号">
                        <el-input v-model="formProfile.number" :maxlength="20" />
                    </el-form-item>
                    <el-form-item label="性别">
                        <el-radio-group v-model="formProfile.sex">
                            <el-radio label="male" border size="small">
                                男
                            </el-radio>
                            <el-radio label="female" border size="small">
                                女
                            </el-radio>
                        </el-radio-group>
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row>
                <el-col :span="24">
                    <label class="el-form-item__label" style="float: none;">
                        个人简介
                    </label>
                    <Editor :value.sync="formProfile.sign" style="padding: 5px;"></Editor>
                </el-col>
            </el-row>
        </el-form>
        <div style="text-align:center;margin-top:10px">
            <el-button type="primary" @click="updateUserInfo" :loading="loadingSaveBtn">保存</el-button>
        </div>
    </div>
</template>
  
<script>
import api from '@/api/login';
import utils from '@/utils/utils';
import myMessage from '@/utils/message';
import { VueCropper } from 'vue-cropper';
import Avatar from 'vue-avatar';
import 'element-ui/lib/theme-chalk/display.css';
import { mapGetters } from 'vuex';
const Editor = () => import('@/components/admin/Editor.vue');


export default {
    components: {
        Avatar,
        VueCropper,
        Editor,
    },
    data() {
        return {
            loadingSaveBtn: false,
            loadingUploadBtn: false,
            uploadModalVisible: false,
            preview: {},
            uploadImgSrc: '',
            avatarOption: {
                imgSrc: '',
                size: 0.8,
                outputType: 'png',
            },

            formProfile: {
                realname: '',
                username: '',
                sex: '',
                nickname: '',
                sign: '',
                number: '',
                classe: ''
            },
        };
    },
    mounted() {
        let profile = this.$store.getters.userInfo;
        Object.keys(this.formProfile).forEach((element) => {
            if (profile[element] !== undefined) {
                this.formProfile[element] = profile[element];
            }
        });
    },
    methods: {
        checkFileType(file) {
            if (!/\.(gif|jpg|jpeg|png|bmp|webp|GIF|JPG|PNG|WEBP)$/.test(file.name)) {
                this.$notify.warning({
                    title: '文件类型不支持',
                    message: file.name + '的文件格式不正确，请选择.gif,.jpg,.jpeg,.png,.bmp,.webp的图片文件。',
                });
                return false;
            }
            return true;
        },
        checkFileSize(file) {
            // max size is 2MB
            if (file.size > 2 * 1024 * 1024) {
                this.$notify.warning({
                    title: '超过文件大小限制',
                    message: file.name + '文件大小错误, 您只能上传不大于2MB的图片文件！',
                });
                return false;
            }
            return true;
        },
        handleSelectFile(file) {
            let isOk = this.checkFileType(file) && this.checkFileSize(file);
            if (!isOk) {
                return false;
            }
            let reader = new window.FileReader();
            reader.onload = (e) => {
                this.avatarOption.imgSrc = e.target.result;
            };
            reader.readAsDataURL(file);
            return false;
        },
        realTime(data) {
            this.preview = data;
        },
        rotate(direction) {
            if (direction === 'left') {
                this.$refs.cropper.rotateLeft();
            } else {
                this.$refs.cropper.rotateRight();
            }
        },
        reselect() {
            this.$confirm('您确定取消该图像的截取？', 'Tips', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }).then(() => {
                this.avatarOption.imgSrc = '';
            });
        },
        finishCrop() {
            this.$refs.cropper.getCropData((data) => {
                this.uploadImgSrc = data;
                this.uploadModalVisible = true;
            });
        },
        uploadAvatar() {
            this.$refs.cropper.getCropBlob((blob) => {
                let form = new window.FormData();
                let file = new window.File(
                    [blob],
                    'avatar.' + this.avatarOption.outputType
                );
                form.append('file', file);
                this.loadingUploadBtn = true;
                this.$axios({
                    method: 'post',
                    url: '/api/xoj/file/uploadImg',
                    data: form,
                    headers: { 'content-type': 'multipart/form-data' },
                }).then(
                    (res) => {
                        let updateData = utils.filterEmptyValue(
                            Object.assign({}, this.formProfile)
                        )

                        if (updateData.sex === 'male') {
                            updateData.sex = 2
                        } else {
                            updateData.sex = 1
                        }
                        updateData.avatar = res.data.data
                        api.changeUserInfo(updateData).then(
                            (res) => {
                                this.loadingUploadBtn = false;
                                myMessage.success('上传头像成功');
                                this.uploadModalVisible = false;
                                this.avatarOption.imgSrc = '';
                                let tmp = this.$store.getters.userInfo
                                tmp.avatar = updateData.avatar
                                this.$store.dispatch('setUserInfo', tmp);
                            },
                            (_) => {
                                this.loadingSaveBtn = false;
                            }
                        );
                    },
                    () => {
                        this.loadingUploadBtn = false;
                    }
                );
            });
        },
        updateUserInfo() {
            this.loadingSaveBtn = true;
            let updateData = utils.filterEmptyValue(
                Object.assign({}, this.formProfile)
            )

            if (updateData.sex === 'male') {
                updateData.sex = 2
            } else {
                updateData.sex = 1
            }

            api.changeUserInfo(updateData).then(
                (res) => {
                    myMessage.success('更新成功');
                    this.$store.dispatch('setUserInfo', res.data.data);
                    this.loadingSaveBtn = false;
                },
                (_) => {
                    this.loadingSaveBtn = false;
                }
            );
        },
    },
    computed: {
        avatar() {
            return this.$store.getters.userInfo.avatar;
        },
        previewStyle() {
            return {
                width: this.preview.w + 'px',
                height: this.preview.h + 'px',
                overflow: 'hidden',
            };
        },
    },
};
</script>
  
<style lang="scss" scoped>
::v-deep .el-input__inner {
    height: 32px;
}

::v-deep .el-form-item__label {
    font-size: 12px;
    line-height: 20px;
}

.section-title {
    font-size: 21px;
    font-weight: 500;
    padding-top: 10px;
    padding-bottom: 20px;
    line-height: 30px;
    text-align: center;
}

.section-main {
    text-align: center;
    margin-bottom: 20px;
}

::v-deep .upload-container .el-upload {
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    width: 320px;
}

::v-deep .upload-container .el-upload:hover {
    border-color: #409eff;
}

.inline {
    display: inline-block;
}

.cropper-btn {
    margin: 10px 0;
}

.copper-img {
    width: 400px;
    height: 300px;
}

.cropper-main {
    flex: none;
    width: 400px;
    height: 300px;
}

.section-main .cropper-preview {
    flex: none;
    text-align: center;
    box-shadow: 0 0 1px 0;
}

@media screen and (max-width: 1080px) {
    .section-main .cropper-preview {
        margin: 0 auto;
    }
}

.upload-modal .notice {
    font-size: 16px;
    display: inline-block;
    vertical-align: top;
    padding: 10px;
}

::v-deep .el-dialog__body {
    padding: 0;
}

::v-deep .el-upload-dragger {
    width: 100%;
    height: 100%;
}

.upload-modal img {
    box-shadow: 0 0 1px 0;
    border-radius: 50%;
    width: 250px;
    height: 250px;
}

.separator {
    display: block;
    position: absolute;
    top: 0;
    bottom: 0;
    left: 50%;
    border: 1px dashed #eee;
}
</style>
  