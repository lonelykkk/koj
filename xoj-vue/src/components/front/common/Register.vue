<template>
    <div>
        <el-form :model="registerForm" :rules="rules" ref="registerForm">
            <el-form-item prop="username">
                <el-input v-model="registerForm.username" prefix-icon="el-icon-user-solid" placeholder="请输入用户名"
                    @keyup.enter.native="handleRegister" width="100%">
                </el-input>
            </el-form-item>

            <el-form-item prop="password">
                <el-input v-model="registerForm.password" prefix-icon="el-icon-lock" placeholder="请输入密码"
                    @keyup.enter.native="handleRegister" type="password">
                </el-input>
            </el-form-item>
            <el-form-item prop="passwordAgain">
                <el-input v-model="registerForm.passwordAgain" prefix-icon="el-icon-lock" placeholder="请再次输入密码"
                    @keyup.enter.native="handleRegister" type="password">
                </el-input>
            </el-form-item>
            <el-form-item prop="email">
                <el-input v-model="registerForm.email" prefix-icon="el-icon-message" placeholder="请输入邮箱，点击右侧发送验证码"
                    @keyup.enter.native="handleRegister">
                    <el-button slot="append" icon="el-icon-message" type="primary" @click.native="sendRegisterEmail"
                        :loading="btnEmailLoading">
                        <span v-show="btnEmailLoading">
                            {{ countdownNum }}
                        </span>
                    </el-button>
                </el-input>
            </el-form-item>
            <el-form-item prop="code">
                <el-input v-model="registerForm.code" prefix-icon="el-icon-s-check" placeholder="请输入邮件中的验证码"
                    @keyup.enter.native="handleRegister">
                </el-input>
            </el-form-item>
        </el-form>
        <div class="footer">
            <el-button type="primary" @click="handleRegister()" :loading="btnRegisterLoading">
                注册
            </el-button>
            <el-link type="primary" @click="switchMode('Login')">
                已有账号？立即登录！
            </el-link>
        </div>
    </div>
</template>
<script>
import { mapGetters, mapActions } from 'vuex'
import api from '@/api/login'
import mMessage from '@/utils/message'


export default {
    data() {
        const CheckUsernameNotExist = (rule, value, callback) => {
            let data = {
                username: value,
                email: undefined
            }
            api.checkUsernameOrEmail(data).then(
                (res) => {
                    if (res.data.data.username === true) {
                        callback(new Error('用户名已存在'));
                    } else {
                        callback();
                    }
                },
                (_) => callback()
            );
        };
        const CheckEmailNotExist = (rule, value, callback) => {
            let data = {
                username: undefined,
                email: value
            }
            api.checkUsernameOrEmail(data).then(
                (res) => {
                    if (res.data.data.email === true) {
                        callback(new Error('邮箱已存在'));
                    } else {
                        callback();
                    }
                },
                (_) => callback()
            );
        };
        const CheckPassword = (rule, value, callback) => {
            if (this.registerForm.password !== '') {
                // 对第二个密码框再次验证
                this.$refs.registerForm.validateField('passwordAgain');
            }
            callback();
        };

        const CheckAgainPassword = (rule, value, callback) => {
            if (value !== this.registerForm.password) {
                callback(new Error('两次输入密码不一致'))
            }
            callback();
        };
        return {
            btnRegisterLoading: false,
            btnEmailLoading: false,
            countdownNum: null,
            registerForm: {
                username: '',
                password: '',
                passwordAgain: '',
                email: '',
                code: '',
            },
            sendEmailError: false,
            rules: {
                username: [
                    {
                        required: true,
                        message: '用户名不能为空',
                        trigger: 'blur',
                    },
                    {
                        validator: CheckUsernameNotExist,
                        trigger: 'blur',
                        message: '用户名已存在',
                    },
                    {
                        max: 20,
                        message: '用户名长度不能超过20位',
                        trigger: 'blur',
                    },
                ],

                email: [
                    {
                        required: true,
                        message: '邮箱不能为空',
                        trigger: 'blur',
                    },
                    {
                        type: 'email',
                        message: '邮箱格式不正确',
                        trigger: 'blur',
                    },
                    {
                        validator: CheckEmailNotExist,
                        message: '邮箱已存在',
                        trigger: 'blur',
                    },
                ],
                password: [
                    {
                        required: true,
                        message: '密码不能为空',
                        trigger: 'blur',
                    },
                    {
                        min: 6,
                        max: 20,
                        message: '请输入长度为6~20位的密码',
                        trigger: 'blur',
                    },
                    { validator: CheckPassword, trigger: 'blur' },
                ],
                passwordAgain: [
                    {
                        required: true,
                        message: '请再次输入密码',
                        trigger: 'blur',
                    },
                    { validator: CheckAgainPassword, trigger: 'change' },
                ],
                code: [
                    {
                        required: true,
                        message: '验证码不能为空',
                        trigger: 'blur',
                    },
                    {
                        min: 6,
                        max: 6,
                        message: '请输入6位数字的验证码',
                        trigger: 'blur',
                    },
                ],
            },
        };
    },
    methods: {
        ...mapActions([
            'changeModalStatus',
            'startTimeOut',
            'changeRegisterTimeOut',
        ]),
        switchMode(mode) {
            this.changeModalStatus({
                mode,
                visible: true,
            });
        },
        countDown() {
            let i = this.time;
            if (i == 0) {
                this.btnEmailLoading = false;
                return;
            }
            this.countdownNum = i;
            setTimeout(() => {
                this.countDown();
            }, 1000);
        },
        sendRegisterEmail() {
            var emailReg = /^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
            if (!emailReg.test(this.registerForm.email)) {
                mMessage.error(this.$i18n.t('m.Email_Check_Format'));
                return;
            }
            this.btnEmailLoading = true;
            this.countdownNum = 'Waiting...';
            if (this.registerForm.email) {
                mMessage.info('请稍等... 系统正在处理...')
                api.getRegisterCode(this.registerForm.email).then(
                    (res) => {
                        if (res.data.msg != null) {
                            mMessage.message(
                                'success',
                                '发送成功！如果长时间没收到邮件，请检查你的邮箱是否准确',
                                5000
                            );
                            this.$notify.success({
                                title: this.$i18n.t('m.Success'),
                                message: '发送成功！如果长时间没收到邮件，请检查你的邮箱是否准确',
                                duration: 5000,
                                offset: 50
                            });
                            this.countDown()
                            this.startTimeOut({ name: 'registerTimeOut' })
                        }
                    },
                    (res) => {
                        this.btnEmailLoading = false
                        this.countdownNum = null
                    }
                );
            }
        },
        handleRegister() {
            this.$refs['registerForm'].validate((valid) => {
                if (valid) {
                    const _this = this;
                    let formData = Object.assign({}, this.registerForm);
                    delete formData['passwordAgain'];
                    this.btnRegisterLoading = true;
                    api.register(formData).then(
                        (res) => {
                            mMessage.success('注册成功！请登录！');
                            this.switchMode('Login');
                            this.btnRegisterLoading = false;
                        },
                        (res) => {
                            this.registerForm.code = '';
                            this.btnRegisterLoading = false;
                        }
                    );
                }
            });
        },
    },
    computed: {
        ...mapGetters(['registerTimeOut', 'modalStatus']),
        time: {
            get() {
                return this.registerTimeOut;
            },
            set(value) {
                this.changeRegisterTimeOut({ time: value });
            },
        },
    },
    created() {
        if (this.time != 60 && this.time != 0) {
            this.btnEmailLoading = true;
            this.countDown();
        }
    },
};
</script>
<style scoped>
.footer {
    overflow: auto;
    margin-top: 20px;
    margin-bottom: -15px;
    text-align: center;
}

::v-deep .el-input-group__append {
    color: #fff;
    background: #25bb9b;
}

::v-deep .footer .el-button--primary {
    margin: 0 0 15px 0;
    width: 100%;
}

::v-deep .el-form-item__content {
    margin-left: 0px !important;
}
</style>
  