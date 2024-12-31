<template></template>

<script>
import api from '@/api/login'

export default {
    mounted() {
        api.logout().then(res => {
            this.$store.dispatch('clearUserInfoAndToken')
            this.$router.replace({
                path: '/home'
            })
        })
    }
}
</script>
