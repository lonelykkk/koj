<template>
    <div>
        <vue-particles 
            color="#dedede" 
            :particleOpacity="0.7" 
            :particlesNumber="80" 
            shapeType="circle" 
            :particleSize="4"
            linesColor="#dedede" 
            :linesWidth="1" 
            :lineLinked="true" 
            :lineOpacity="0.4" 
            :linesDistance="150" 
            :moveSpeed="3"
            :hoverEffect="true" 
            hoverMode="grab" 
            :clickEffect="true" 
            clickMode="push">
        </vue-particles>
        <div class="form">
            <el-form :model="ruleForm" :rules="rules" ref="ruleForm" label-position="left" label-width="0px"
                class="demo-ruleForm login-container">
                <h1 class="title">欢迎登录后台管理系统</h1>
                <el-form-item prop="username">
                    <el-input type="text" v-model="ruleForm.username" auto-complete="off"
                        placeholder="用户名" @keyup.enter.native="handleLogin">
                    </el-input>
                </el-form-item>
                <el-form-item prop="password">
                    <el-input type="password" v-model="ruleForm.password" auto-complete="off"
                        placeholder="密码" @keyup.enter.native="handleLogin">
                    </el-input>
                </el-form-item>
                <el-form-item style="width: 100%">
                    <el-button type="primary" style="width: 100%" @click.native.prevent="handleLogin" :loading="logining">
                        登录
                    </el-button>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>
  
<script>
import api from '@/api/login'
import mMessage from '@/utils/message'
export default {
    data() {
        return {
            logining: false,
            ruleForm: {
                username: '',
                password: '',
            },
            rules: {
                username: [
                    {
                        required: true,
                        trigger: 'blur',
                        message: '用户名不能为空',
                    },
                ],
                password: [
                    {
                        required: true,
                        trigger: 'blur',
                        message: '密码不能为空',
                    },
                ],
            },
            checked: true,
        };
    },
    methods: {
        handleLogin(ev) {
            this.$refs.ruleForm.validate((valid) => {
                if (valid) {
                    this.logining = true;
                    api.adminLogin(this.ruleForm).then(
                        (res) => {
                            this.logining = false
                            const jwt = res.headers['authorization']
                            this.$store.commit('changeUserToken', jwt)
                            this.$store.dispatch('setUserInfo', res.data.data)
                            mMessage.success('登录成功')
                            this.$router.push({ name: 'admin-dashboard' })
                        },
                        () => {
                            this.logining = false;
                        }
                    );
                } else {
                    mMessage.error('请检查您的用户名或密码');
                }
            });
        },
    },
};
</script>
  
<style scoped>
.login-container {
    -webkit-border-radius: 5px;
    border-radius: 5px;
    -moz-border-radius: 5px;
    background-clip: padding-box;
    margin: 180px auto;
    width: 350px;
    padding: 35px 35px 15px 35px;
    background: #fff;
    border: 1px solid #eaeaea;
    box-shadow: 0 0 25px #cac6c6;
}

.login-container .title {
    margin: 0px auto 40px auto;
    text-align: center;
    color: #1e9fff;
    font-size: 25px;
    font-weight: bold;
}

.login-container .remember {
    margin: 0px 0px 35px 0px;
}

.form {
    position: relative;
    z-index: 9999;
}
</style>
  