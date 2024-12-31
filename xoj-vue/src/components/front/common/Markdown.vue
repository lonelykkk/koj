<template>
    <div v-if="isAvoidXss" v-dompurify-html="html" v-highlight v-katex class="markdown-body">
    </div>
    <div v-else v-html="html" v-highlight v-katex class="markdown-body">
    </div>
</template>
<script>
export default {
    name: "Markdown",
    props: {
        isAvoidXss: {
            default: false,
            type: Boolean,
        },
        content: {
            require: true,
            type: String,
        },
    },
    data() {
        return {
            pdfLogo: require('@/assets/pdf-logo.svg'),
        }
    },
    computed: {
        html: function () {
            if (this.content == null || this.content == undefined) {
                return "";
            }
            let res = this.$markDown.render(this.content);

            return res;
        },
    },
};
</script>
<style scoped>
file-card .pdf-svg {
    padding: 0 !important;
    margin: 0 !important;
    box-shadow: none !important;
}

file-card {
    margin: 1rem 0;
    display: flex;
    align-items: center;
    max-width: 100%;
    border-radius: 4px;
    transition: 0.2s ease-out 0s;
    color: #7a8e97;
    background: #fff;
    padding: 0.6rem;
    position: relative;
    border: 1px solid rgba(0, 0, 0, 0.15);
}

file-card>div:first-of-type {
    display: flex;
    align-items: center;
    padding-right: 1rem;
    width: 5rem;
    height: 5rem;
    flex-shrink: 0;
    flex-grow: 0;
}

file-card .filename {
    overflow: hidden;
    text-overflow: ellipsis;
    font-size: 1.2rem;
    margin-bottom: 0.5rem !important;
    font-family: "Roboto";
    font-weight: 400 !important;
    line-height: 1.2 !important;
    color: #000;
    word-wrap: break-word;
    word-break: break-all;
    white-space: normal !important;
    -webkit-line-clamp: 1;
    display: -webkit-box;
    -webkit-box-orient: vertical;
}

file-card p {
    margin: 0;
    line-height: 1;
    font-family: "Roboto";
}
</style>