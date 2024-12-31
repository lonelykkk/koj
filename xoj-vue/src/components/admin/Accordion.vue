<template>
    <div class="accordion">
        <header>
            <span class="title">{{ title }}</span>
            <span class="header_right">
                <slot name="header"></slot>
            </span>
        </header>
        <div class="body" v-show="isOpened">
            <slot></slot>
        </div>
        <footer @click="changeVisible">
            <i :class="{ rotate: !isOpened }" class="el-icon-caret-top" style="color:#2d8cf0"></i>
        </footer>
    </div>
</template>

<script>
export default {
    name: 'Accordion',
    props: {
        title: {
            type: String,
            required: true
        },
        isOpen: {
            type: Boolean,
            required: true,
            default: true
        },
        index: {
            type: Number,
            required: true
        }
    },
    data() {
        return {
            isOpened: this.isOpen
        }
    },
    methods: {
        changeVisible() {
            // 不能直接修改传入的参数：isOpen，会报错（Avoid mutating a prop directly since the value will be overwritten whenever the parent）
            this.isOpened = !this.isOpened
            this.$emit('changeVisible', this.index, this.isOpened) // 调用父组件
        }
    }
}
</script>

<style lang="scss">
.accordion {
    box-shadow: 0 1px 2px 0 rgb(34 36 38 / 15%), 0 0 0 1px rgb(34 36 38 / 15%);
}

.accordion header {
    position: relative;
}

.title {
    font-size: 16px;
    margin: 0 0 0 10px;
    line-height: 50px;
    font-weight: bolder;
}

.header_right {
    float: right;
}

.body {
    background-color: #f9fafc;
    border-top: 1px solid rgb(34 36 38 / 15%);
    clear: both;
    overflow: hidden;
    padding: 15px 10px;
}

footer {
    border-top: 1px solid rgb(34 36 38 / 15%);
    height: 36px;
    box-sizing: border-box;
    background-color: #fff;
    border-bottom-left-radius: 4px;
    border-bottom-right-radius: 4px;
    text-align: center;
    margin-top: -1px;
    color: #d3dce6;
    cursor: pointer;
    transition: 0.2s;
}

footer:hover {
    background-color: #ebeef5;
}

.rotate {
    transform: rotate(180deg);
}
</style>