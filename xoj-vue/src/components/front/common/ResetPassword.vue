<template>
    <div>
        <h2>重置密码</h2>
        <form @submit.prevent="submitResetPassword">
            <div>
                <label for="email">邮箱:</label>
                <input type="email" id="email" v-model="resetForm.email" required>
            </div>
            <div>
                <label for="verificationCode">验证码:</label>
                <input type="text" id="verificationCode" v-model="resetForm.verificationCode" required>
                <button type="button" @click="sendVerificationCode" :disabled="!canSendCode">{{ codeButtonText
                    }}</button>
            </div>
            <div>
                <label for="newPassword">新密码:</label>
                <input type="password" id="newPassword" v-model="resetForm.newPassword" required>
            </div>
            <button type="submit">重置密码</button>
        </form>
    </div>
</template>

<script>
export default {
    data() {
        return {
            resetForm: {
                email: '',
                verificationCode: '',
                newPassword: ''
            },
            canSendCode: true,
            codeButtonText: '发送验证码',
            timer: null,
            countDown: 60
        };
    },
    methods: {
        sendVerificationCode() {
            // 这里应该调用后端API发送验证码，并在发送成功后启动倒计时
            this.canSendCode = false;
            this.countDownTimer();
            // 发送验证码的逻辑，通常需要向后端发送一个请求
            // axios.post('/api/send-verification-code', { email: this.resetForm.email })
            //   .then(response => {
            //     // 处理响应
            //   })
            //   .catch(error => {
            //     // 处理错误
            //   });
        },
        countDownTimer() {
            if (this.countDown > 0) {
                this.timer = setInterval(() => {
                    this.countDown--;
                    this.codeButtonText = `${this.countDown}s后重新发送`;
                }, 1000);
            } else {
                clearInterval(this.timer);
                this.canSendCode = true;
                this.countDown = 60;
                this.codeButtonText = '发送验证码';
            }
        },
        submitResetPassword() {
            // 在这里调用后端API来重置密码
            // axios.post('/api/reset-password', this.resetForm)
            //   .then(response => {
            //     // 处理响应，例如提示用户密码重置成功
            //   })
            //   .catch(error => {
            //     // 处理错误
            //   });
        }
    }
};
</script>
