<template>
  <div id="app">
    <div v-if="!isAdminView" class="full-height flex-column">
      <NavBar></NavBar>
      <div class="content">
        <transition name="el-zoom-in-bottom">
          <router-view></router-view>
        </transition>
      </div>
      <footer>
        <div v-if="showFooter" class="fix-to-bottom">
          <div class="mundb-footer">
            <img src="https://www.beian.gov.cn/img/new/gongan.png">
            <a style="color:#1E9FFF" href="https://beian.miit.gov.cn/" target="_blank">
              赣ICP备2022006267号
            </a>
            Powered by
            <a style="color:#1E9FFF" target="_blank">磐石工作室</a>
          </div>
        </div>
      </footer>
    </div>
    <div v-else>
      <div id="admin-content">
        <transition name="el-zoom-in-bottom">
          <router-view></router-view>
        </transition>
      </div>
    </div>
  </div>
</template>
<script>
import NavBar from '@/components/front/common/NavBar'
import { mapActions, mapState, mapGetters } from "vuex"
export default {
  name: "app-content",
  components: {
    NavBar
  },
  data() {
    return {
      isAdminView: false,
      showFooter: true,
    }
  },
  methods: {
    openChat() {
      console.log('open chat')
    }
  },
  computed: {
    ...mapGetters(["token", "isAuthenticated"]),
  },
  created: function () {
    this.$nextTick(function () {
      try {
        document.body.removeChild(document.getElementById("app-loader"));
      } catch (e) { }
    });

    if (this.$route.path.split("/")[1] != "admin") {
      this.isAdminView = false;
    } else {
      this.isAdminView = true;
    }
  },
  watch: {
    $route(newVal, oldVal) {
      if (newVal !== oldVal && newVal.path.split("/")[1] == "admin") {
        this.isAdminView = true;
      } else {
        this.isAdminView = false;
      }
    },
  },
}

</script>
<style lang="scss">
.content {
  margin-top: 20px;
  padding: 0 3%;
}

.footer-info {
  text-align: center;
}

.panel-title {
  font-size: 21px;
  font-weight: 500;
  padding-top: 10px;
  padding-bottom: 20px;
  line-height: 30px;
}

// 清除基本样式，消除不同浏览器之间的差异
/* Reset all elements */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: inherit;
  font-size: inherit;
  line-height: inherit;
}

/* Reset specific elements */
html,
body,
p,
h1,
h2,
h3,
ul,
ol,
li {
  margin: 0;
  padding: 0;
}

/* Add custom styles */
body {
  background-color: #eff3f5 !important;
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB',
    'Microsoft YaHei', '微软雅黑', Arial, sans-serif !important;
  color: #495060 !important;
  font-size: 12px !important;
}

code,
kbd,
pre,
samp {
  font-family: Consolas, Menlo, Courier, monospace;
}

.markdown-body img {
  max-width: 100%;
}

a {
  text-decoration: none;
  background-color: transparent;
  color: #495060;
  outline: 0;
  cursor: pointer;
  transition: color 0.2s ease;
}

a:hover {
  color: #2196f3 !important;
}

.markdown-body a {
  color: #2196f3;
  cursor: pointer;
  text-decoration: none;
  transition: all 0.28s ease;
  -moz-transition: all 0.28s ease;
  -webkit-transition: all 0.28s ease;
  -o-transition: all 0.28s ease;
}

.markdown-body a:hover {
  color: #ff5722 !important;
  text-decoration: underline;
}

.fix-to-bottom {
  margin-top: auto;
  text-align: center;
}

.mundb-footer {
  padding: 1rem 2.5rem;
  width: 100%;
  font-weight: 400;
  font-size: 1rem;
  line-height: 1;
}
</style>
<style>
.markdown-body pre {
  display: block;
  border-radius: 3px !important;
  border: 1px solid #c3ccd0;
  padding: 0 16px 0 50px !important;
  position: relative !important;
  overflow-y: hidden !important;
  font-size: 1rem !important;
  background: #f8f8f9 !important;
  white-space: pre !important;
}

.markdown-body pre code {
  line-height: 26px !important;
}

.markdown-body pre ol.pre-numbering {
  position: absolute;
  top: 0;
  left: 0;
  line-height: 26px;
  margin: 0;
  padding: 0;
  list-style-type: none;
  counter-reset: sectioncounter;
  background: #f1f1f1;
  color: #777;
  font-size: 12px;
}

.markdown-body pre ol.pre-numbering li {
  margin-top: 0 !important;
}

.markdown-body pre ol.pre-numbering li:before {
  content: counter(sectioncounter) "";
  counter-increment: sectioncounter;
  display: inline-block;
  width: 40px;
  text-align: center;
}

.markdown-body pre i.code-copy {
  position: absolute;
  top: 0;
  right: 0;
  background-color: #2196f3;
  display: none;
  padding: 5px;
  margin: 5px 5px 0 0;
  font-size: 11px;
  border-radius: inherit;
  color: #fff;
  cursor: pointer;
  transition: all 0.3s ease-in-out;
}

.markdown-body pre:hover i.code-copy {
  display: block;
}

.markdown-body pre i.code-copy:hover i.code-copy {
  display: block;
}

.markdown-body blockquote {
  color: #666;
  border-left: 4px solid #8bc34a;
  padding: 10px;
  margin-left: 0;
  font-size: 14px;
  background: #f8f8f8;
}

.markdown-body h1,
.markdown-body h2,
.markdown-body h3,
.markdown-body h4,
.markdown-body h5,
.markdown-body h6 {
  position: relative;
  margin-top: 1em;
  margin-bottom: 16px;
  font-weight: bold;
  line-height: 1.4;
}

.markdown-body h1 {
  padding-bottom: 0.3em;
  font-size: 1.86em;
  line-height: 1.2;
  border-bottom: 1px solid #eee;
}

.markdown-body h2 {
  font-size: 1.45em;
  line-height: 1.425;
  border-bottom: 1px solid #eee;
  background: #cce5ff;
  padding: 8px 10px;
  color: #545857;
  border-radius: 3px;
}

.markdown-body h3 {
  font-size: 1.3em;
  line-height: 1.43;
}

.markdown-body h3:before {
  content: "";
  border-left: 4px solid #03a9f4;
  padding-left: 6px;
}

.markdown-body h4 {
  font-size: 1.12em;
}

.markdown-body h4:before {
  content: "";
  border-left: 4px solid #bbb;
  padding-left: 6px;
}

.markdown-body img {
  border: 0;
  background: #ffffff;
  padding: 15px;
  margin: 5px 0;
  box-shadow: inset 0 0 12px rgb(219 219 219);
}

.markdown-body p {
  font-size: 15px;
  word-wrap: break-word;
  word-break: break-word;
  line-height: 1.8;
}

.hljs {
  padding: 0 !important;
}
</style>