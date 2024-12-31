<template>
    <div class="mavonEditor">
        <mavon-editor 
            ref="md" 
            @imgAdd="$imgAdd" 
            @imgDel="$imgDel" 
            :ishljs="true" 
            :html="openHtml" 
            :autofocus="false"
            :toolbars="toolbars" 
            v-model="currentValue" 
            codeStyle="arduino-light">
        </mavon-editor>
    </div>
</template>
<script>
import { mapGetters } from 'vuex';
import { addCodeBtn } from '@/utils/codeblock';
export default {
    name: 'Editor',
    props: {
        value: {
            type: String,
            default: '',
        },
        openHtml: {
            type: Boolean,
            default: true,
        },
    },
    data() {
        return {
            currentValue: this.value,
            img_file: {},
            toolbars: {
                bold: true, // 粗体
                italic: true, // 斜体
                header: true, // 标题
                underline: true, // 下划线
                strikethrough: true, // 中划线
                mark: true, // 标记
                superscript: true, // 上角标
                subscript: true, // 下角标
                quote: true, // 引用
                ol: true, // 有序列表
                ul: true, // 无序列表
                link: true, // 链接
                imagelink: true, // 图片链接
                code: true, // code
                table: true, // 表格
                fullscreen: true, // 全屏编辑
                readmodel: true, // 沉浸式阅读
                htmlcode: true, // 展示html源码
                help: true, // 帮助
                /* 1.3.5 */
                undo: true, // 上一步
                redo: true, // 下一步
                trash: true, // 清空
                save: true, // 保存（触发events中的save事件）
                /* 1.4.2 */
                navigation: true, // 导航目录
                /* 2.1.8 */
                alignleft: true, // 左对齐
                aligncenter: true, // 居中
                alignright: true, // 右对齐
                /* 2.2.1 */
                subfield: true, // 单双栏模式
                preview: true, // 预览
            },
        };
    },
    created() {

    },
    methods: {
        // 将图片上传到服务器，返回地址替换到md中
        $imgAdd(pos, $file) {
            var formdata = new FormData();
            formdata.append('file', $file);
            this.img_file[pos] = $file;
            //将下面上传接口替换为你自己的服务器接口
            this.$axios({
                url: '/api/xoj/file/uploadImg',
                method: 'post',
                data: formdata,
                headers: { 'Content-Type': 'multipart/form-data' },
            }).then((res) => {
                // 将返回的url替换到文本原位置![...](0) -> ![...](url)
                this.$refs.md.$img2Url(pos, res.data.data);
                // this.img_file[res.data.data.link] = res.data.data.fileId;
            });
        },
        $imgDel(pos) {
            // 删除文件
            // this.$http({
            //     url: '/api/file/delete-md-img',
            //     method: 'get',
            //     params: {
            //         fileId: this.img_file[pos[0]],
            //     },
            // });
        }
    },
    computed: {

    },
    watch: {
        value(val) {
            if (this.currentValue !== val) {
                this.currentValue = val;
            }
        },
        currentValue(newVal, oldVal) {
            if (newVal !== oldVal) {
                this.$emit('update:value', newVal);
                this.$nextTick((_) => {
                    addCodeBtn();
                });
            }
        }
    },
};
</script>
<style lang="scss" scoped>
.auto-textarea-wrapper .auto-textarea-block {
    white-space: pre-wrap !important;
}
</style>
  