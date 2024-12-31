<template>
    <div class="login-container">

        <el-form :model="formLogin" :rules="rules" ref="formLogin" label-width="100px">
            <el-form-item prop="username">
                <el-input v-model="formLogin.username" prefix-icon="el-icon-user-solid" placeholder="用户名" width="100%"
                    @keyup.enter.native="enterHandleLogin">
                </el-input>
            </el-form-item>
            <el-form-item prop="password">
                <el-input v-model="formLogin.password" prefix-icon="el-icon-lock" placeholder="密码" type="password"
                    @keyup.enter.native="enterHandleLogin">
                </el-input>
            </el-form-item>
        </el-form>
        <div class="footer">
            <el-button type="primary" @click="handleLogin" :loading="btnLoginLoading">
                登录
            </el-button>
            <el-link type="primary" @click="switchMode('Register')">
                没有账号？立即注册!
            </el-link>
            <el-link type="primary" @click="switchMode('ResetPassword')" style="float: right">
                忘记密码
            </el-link>
        </div>
        <reset-password v-if="mode === 'ResetPassword'"></reset-password>
    </div>

</template>
  
<script>
import ResetPassword from '@/components/front/common/ResetPassword.vue';
import { mapGetters, mapActions } from 'vuex'
import loginApi from '@/api/login'
import myMessage from '@/utils/message'

export default {
    components: {
        ResetPassword
    },
    data() {
        return {
            showResetPassword: false,
            btnLoginLoading: false,
            formLogin: {
                username: '',
                password: '',
            },
            loginSlideBlockVisible: false,
            rules: {
                username: [
                    {
                        required: true,
                        message: '用户名不能为空',
                        trigger: 'blur',
                    },
                    {
                        max: 20,
                        message: '用户名长度不能超过20位',
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
                ],
            },
        };
    },
    methods: {
        ...mapActions(['changeModalStatus']),
        switchMode(mode) {
            this.changeModalStatus({
                mode,
                visible: true,
            });
        },
        enterHandleLogin() {
            if (this.needVerify) {
                this.visible.loginSlideBlock = true;
            } else {
                this.handleLogin();
            }
        },
        handleLogin() {
            this.$refs['formLogin'].validate((valid) => {
                if (valid) {
                    this.btnLoginLoading = true;
                    let formData = Object.assign({}, this.formLogin);
                    loginApi.login(formData).then(
                        (res) => {
                            this.btnLoginLoading = false;
                            this.changeModalStatus({ visible: false });
                            const jwt = res.headers['authorization'];
                            this.$store.commit('changeUserToken', jwt);
                            this.$store.dispatch('setUserInfo', res.data.data);
                            this.$store.dispatch('incrLoginFailNum', true);
                            myMessage.success('登录成功');
                        },
                        (err) => {
                            this.$store.dispatch('incrLoginFailNum', false);
                            this.btnLoginLoading = false;
                        }
                    );
                }
            });
        }
    },
    computed: {
        ...mapGetters(['modalStatus', 'loginFailNum']),
        visible: {
            get() {
                return this.modalStatus.visible;
            },
            set(value) {
                this.changeModalStatus({ visible: value });
            },
        },
    },
    watch: {
        loginFailNum(newVal, oldVal) {
            if (newVal >= 5) {
                this.needVerify = true;
            } else {
                this.needVerify = false;
            }
        },
    },
}

</script>
<style lang="scss" scoped>
.footer {
    overflow: auto;
    margin-top: 20px;
    margin-bottom: -15px;
    text-align: left;
}

.el-button {
    margin: 0 0 15px 0;
    width: 100%;
}

::v-deep .el-form-item__content {
    margin-left: 0px !important;
}
</style>
  