<template>
    <div class="setting-main">
        <el-row :gutter="20">
            <el-col :sm="24" :md="10" :lg="10">
                <div class="left">
                    <p class="section-title">更改密码</p>
                    <el-form class="setting-content" ref="formPassword" :model="formPassword" :rules="rulePassword">
                        <el-form-item label="当前密码" prop="oldPassword">
                            <el-input v-model="formPassword.oldPassword" type="password" />
                        </el-form-item>
                        <el-form-item label="新密码" prop="newPassword">
                            <el-input v-model="formPassword.newPassword" type="password" />
                        </el-form-item>
                        <el-form-item label="确认新密码" prop="againPassword">
                            <el-input v-model="formPassword.againPassword" type="password" />
                        </el-form-item>
                    </el-form>
                    <el-popover placement="top" width="350" v-model="visible.passwordSlideBlock" trigger="click">
                        <el-button type="primary" slot="reference" :loading="loading.btnPassword"
                            :disabled="disabled.btnPassword">
                            更新密码
                        </el-button>
                        <slide-verify :l="42" :r="10" :w="325" :h="100" :accuracy="3" @success="changePassword"
                            @again="onAgain('password')" slider-text="请向右滑动验证" ref="passwordSlideBlock"
                            v-show="!verify.passwordSuccess">
                        </slide-verify>
                        <el-alert title="验证成功" type="success" :description="verify.passwordMsg"
                            v-show="verify.passwordSuccess" :center="true" :closable="false" show-icon>
                        </el-alert>
                    </el-popover>
                </div>
                <el-alert v-show="visible.passwordAlert.show" :title="visible.passwordAlert.title"
                    :type="visible.passwordAlert.type" :description="visible.passwordAlert.description" :closable="false"
                    effect="dark" style="margin-top:15px" show-icon>
                </el-alert>
            </el-col>
            <el-col :md="4" :lg="4">
                &nbsp;
                <div class="separator hidden-md-and-down"></div>
                <p></p>
            </el-col>
            <el-col :sm="24" :md="10" :lg="10">
                <div class="right">
                    <p class="section-title">更改邮箱</p>
                    <el-form class="setting-content" ref="formEmail" :model="formEmail" :rules="ruleEmail">
                        <el-form-item label="当前密码" prop="password">
                            <el-input v-model="formEmail.password" type="password" />
                        </el-form-item>
                        <el-form-item label="当前邮箱">
                            <el-input v-model="formEmail.oldEmail" disabled />
                        </el-form-item>
                        <el-form-item label="新邮箱" prop="newEmail">
                            <el-input v-model="formEmail.newEmail">
                                <el-button slot="append" @click="getChangeEmailCode" :loading="loading.btnSendEmail"
                                    icon="el-icon-message">
                                    获取验证码
                                </el-button>
                            </el-input>
                        </el-form-item>
                        <el-form-item label="验证码" prop="code">
                            <el-input v-model="formEmail.code" />
                        </el-form-item>
                    </el-form>
                    <el-popover placement="top" width="350" v-model="visible.emailSlideBlock" trigger="click">
                        <el-button type="primary" slot="reference" :loading="loading.btnEmailLoading"
                            :disabled="disabled.btnEmail">
                            更新邮箱
                        </el-button>
                        <slide-verify :l="42" :r="10" :w="325" :h="100" :accuracy="3" @success="changeEmail"
                            @again="onAgain('email')" slider-text="请向右滑动验证" ref="emailSlideBlock"
                            v-show="!verify.emailSuccess">
                        </slide-verify>
                        <el-alert title="验证成功" type="success" :description="verify.emailMsg" v-show="verify.emailSuccess"
                            :center="true" :closable="false" show-icon>
                        </el-alert>
                    </el-popover>
                </div>
                <el-alert v-show="visible.emailAlert.show" :title="visible.emailAlert.title" :type="visible.emailAlert.type"
                    :description="visible.emailAlert.description" :closable="false" effect="dark" style="margin-top:15px"
                    show-icon>
                </el-alert>
            </el-col>
        </el-row>
    </div>
</template>
  
<script>
import api from '@/api/login'
import myMessage from '@/utils/message'
import 'element-ui/lib/theme-chalk/display.css'

export default {
    data() {
        const oldPasswordCheck = [
            {
                required: true,
                trigger: 'blur',
                message: '当前密码不能为空',
            },
            {
                trigger: 'blur',
                min: 6,
                max: 20,
                message: '请输入长度为6~20位的密码',
            },
        ];
        const CheckAgainPassword = (rule, value, callback) => {
            if (value !== this.formPassword.newPassword) {
                callback(new Error('两次输入密码不一致'));
            }
            callback();
        };
        const CheckNewPassword = (rule, value, callback) => {
            if (this.formPassword.oldPassword !== '') {
                if (this.formPassword.oldPassword === this.formPassword.newPassword) {
                    callback(
                        new Error('新密码未变动')
                    );
                } else {
                    // 对第二个密码框再次验证
                    this.$refs.formPassword.validateField('again_password');
                }
            }
            callback();
        };
        const CheckEmail = (rule, value, callback) => {
            if (this.formEmail.oldEmail !== '') {
                if (this.formEmail.oldEmail === this.formEmail.newEmail) {
                    callback(new Error('新邮箱未变动'));
                }
            }
            callback();
        };
        return {
            loading: {
                btnPassword: false,
                btnEmail: false,
                btnSendEmail: false,
            },
            disabled: {
                btnPassword: false,
                btnEmail: false,
            },
            verify: {
                passwordSuccess: false,
                passwordMsg: '',
                emailSuccess: false,
                emailMsg: '',
            },
            visible: {
                passwordAlert: {
                    type: 'success',
                    show: false,
                    title: '',
                    description: '',
                },
                emailAlert: {
                    type: 'success',
                    show: false,
                    title: '',
                    description: '',
                },
                passwordSlideBlock: false,
                emailSlideBlock: false,
            },
            formPassword: {
                oldPassword: '',
                newPassword: '',
                againPassword: '',
            },
            formEmail: {
                password: '',
                oldEmail: '',
                newEmail: '',
            },
            rulePassword: {
                oldPassword: oldPasswordCheck,
                newPassword: [
                    {
                        required: true,
                        trigger: 'blur',
                        message: '新密码不能为空',
                    },
                    {
                        trigger: 'blur',
                        min: 6,
                        max: 20,
                        message: '请输入长度为6~20位的密码',
                    },
                    { validator: CheckNewPassword, trigger: 'blur' },
                ],
                againPassword: [
                    {
                        required: true,
                        trigger: 'blur',
                        message: '请再次输入密码',
                    },
                    { validator: CheckAgainPassword, trigger: 'blur' },
                ],
            },
            ruleEmail: {
                password: oldPasswordCheck,
                newEmail: [
                    {
                        required: true,
                        message: '邮箱不能为空',
                        trigger: 'blur',
                    },
                    {
                        type: 'email',
                        trigger: 'change',
                        message: '邮箱格式不正确',
                    },
                    { validator: CheckEmail, trigger: 'blur' },
                ],
                code: [
                    {
                        required: true,
                        message: '验证码不能为空',
                        trigger: 'blur',
                    },
                ]
            },
        };
    },
    mounted() {
        this.formEmail.oldEmail = this.$store.getters.userInfo.email || '';
    },
    methods: {
        changePassword(times) {
            this.verify.passwordSuccess = true;
            let time = (times / 1000).toFixed(1);
            this.verify.passwordMsg = 'Total time ' + time + 's';
            setTimeout(() => {
                this.visible.passwordSlideBlock = false;
                this.verify.passwordSuccess = false;
                // 无论后续成不成功，验证码滑动都要刷新
                this.$refs.passwordSlideBlock.reset();
            }, 1000);

            this.$refs['formPassword'].validate((valid) => {
                if (valid) {
                    this.loading.btnPassword = true;
                    let data = Object.assign({}, this.formPassword);
                    delete data.againPassword;
                    api.changePassword(data).then(
                        (res) => {
                            this.loading.btnPassword = false;
                            myMessage.success('更新成功');
                            if (res.data.code == 20000) {
                                myMessage.success('更新成功');
                                this.visible.passwordAlert = {
                                    show: true,
                                    title: '更新成功',
                                    type: 'success',
                                    description: res.data.message,
                                };
                            } else {
                                myMessage.error(res.data.message);
                                this.visible.passwordAlert = {
                                    show: true,
                                    title: '更新失败',
                                    type: 'warning',
                                    description: res.data.message
                                };
                                if (res.data.code == 40003) {
                                    this.visible.passwordAlert.type = 'error';
                                    this.disabled.btnPassword = true;
                                }
                            }
                        },
                        (err) => {
                            this.loading.btnPassword = false;
                        }
                    );
                }
            });
        },
        getChangeEmailCode() {
            if (!this.formEmail.newEmail) {
                myMessage.error('新邮箱不能为空');
            }
            var emailReg = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            if (!emailReg.test(this.formEmail.newEmail)) {
                mMessage.error('邮箱格式不正确');
                return;
            }
            if (this.formEmail.oldEmail === this.formEmail.newEmail) {
                myMessage.error('新邮箱未变动');
            }
            this.loading.btnSendEmail = true;
            api.getChangeCode(this.formEmail.newEmail).then((res) => {
                myMessage.success('发送成功！如果长时间没收到邮件，请检查你的邮箱是否准确！');
                this.$notify.success({
                    title: this.$i18n.t('m.Success'),
                    message: '发送成功！如果长时间没收到邮件，请检查你的邮箱是否准确！',
                    duration: 5000,
                    offset: 50
                });
                this.loading.btnSendEmail = false;
            }, (_) => {
                this.loading.btnSendEmail = false;
            })
        },
        changeEmail(times) {
            this.verify.emailSuccess = true;
            let time = (times / 1000).toFixed(1);
            this.verify.emailMsg = 'Total time ' + time + 's';
            setTimeout(() => {
                this.visible.emailSlideBlock = false;
                this.verify.emailSuccess = false;
                // 无论后续成不成功，验证码滑动都要刷新
                this.$refs.emailSlideBlock.reset();
            }, 1000);
            this.$refs['formEmail'].validate((valid) => {
                if (valid) {
                    this.loading.btnEmail = true;
                    let data = Object.assign({}, this.formEmail);
                    api.changeEmail(data).then(
                        (res) => {
                            this.loading.btnEmail = false;
                            if (res.data.code == 20000) {
                                myMessage.success('更新成功');
                                this.visible.emailAlert = {
                                    show: true,
                                    title: '更新成功',
                                    type: 'success',
                                    description: res.data.data.msg,
                                };
                                // 更新本地缓存
                                this.$store.dispatch('setUserInfo', res.data.data.userInfo);
                                this.$refs['formEmail'].resetFields();
                                this.formEmail.oldEmail = res.data.data.userInfo.email;
                            } else {
                                myMessage.error(res.data.message);
                                this.visible.emailAlert = {
                                    show: true,
                                    title: '更新失败',
                                    type: 'warning',
                                    description: res.data.data.msg,
                                };
                            }
                        },
                        (err) => {
                            this.loading.btnEmail = false;
                        }
                    );
                }
            });
        },
        onAgain(type) {
            if ((type = 'password')) {
                this.$refs.passwordSlideBlock.reset();
            } else {
                this.$refs.emailSlideBlock.reset();
            }
            myMessage.warning('您的操作太快啦，可能是机器操作！请再次验证！');
        },
    },
};
</script>
  
<style lang="scss" scoped>
.section-title {
    font-size: 21px;
    font-weight: 500;
    padding-top: 10px;
    padding-bottom: 20px;
    line-height: 30px;
}

.left {
    text-align: center;
}

.right {
    text-align: center;
}

::v-deep .el-input__inner {
    height: 32px;
}

::v-deep .el-form-item__label {
    font-size: 12px;
    line-height: 20px;
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
  