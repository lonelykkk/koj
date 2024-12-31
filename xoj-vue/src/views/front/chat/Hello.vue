<template>
  <div class="welcome">
    <div class="tips-list">
      <div class="column">
        <div class="h-item">
          <img src="@/assets/ic_write.png" />
          <div class="tit">AI 创作</div>
        </div>
        <ul>
          <li @click="quickMessage('写一首赞美祖国的诗')">写一首赞美祖国的诗 →</li>
          <li @click="quickMessage('写一篇科幻小说')">写一篇科幻小说 →</li>
          <li @click="quickMessage('写一首宋词')">写一首宋词 →</li>
        </ul>
      </div>
      <div class="column">
        <div class="h-item">
          <img src="@/assets/ic_question.png" />
          <div class="tit">有趣的提问</div>
        </div>
        <ul>
          <li @click="quickMessage('有哪些有趣的科学实验')">有哪些有趣的科学实验 →</li>
          <li @click="quickMessage('问一个AI也答不出的问题')">问一个AI也答不出的问题 →</li>
          <li @click="quickMessage('AI会替代人类工作吗')">AI会替代人类工作吗 →</li>
        </ul>
      </div>
      <div class="column">
        <div class="h-item">
          <img src="@/assets/ic_baike.png" />
          <div class="tit">AI 百科</div>
        </div>
        <ul>
          <li @click="quickMessage('用简单的术语来解释人工智能')">简单解释一下人工智能 →</li>
          <li @click="quickMessage('请介绍一下Chatgpt')">请介绍一下Chatgpt →</li>
          <li @click="quickMessage('请介绍一下百度文心')">请介绍一下百度文心 →</li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  computed: {
    message() {
      const text = this.text.replace(/(^\s*)|(\s*$)/g, '')
      return mdi.render(text)
    }
  },
  methods: {
    quickMessage(text) {
      this.$emit('quick', text)
    }
  }
}
</script>
<style lang="scss" scoped>
.welcome {
  width: 768px;
  max-width: 100%;
  height: 400px;
  margin: 0 auto;
  margin-top: 80px;

  .title {
    font-size: 40px;
    color: #333;
    text-align: center;
    line-height: 48px;
    margin: 30px 0;
    font-weight: bold;
    letter-spacing: 2px;
  }

  .tips-list {
    display: flex;
    justify-content: space-around;
    padding: 10px;

    .column {
      width: 33%;
      padding: 0 10px;
      text-align: center;

      ul, li {
        list-style: none;
        margin: 0;
        padding: 0;
      }

      .h-item {
        margin-bottom: 20px;

        img {
          width: 24px;
          height: 24px;
        }

        .tit {
          font-size: 16px;
          line-height: 44px;
        }
      }

      li {
        background: #f7f7f8;
        border-radius: 4px;
        margin-bottom: 15px;
        padding: 12px 15px;
        font-size: 13px;
        color: #444;
        line-height: 18px;
        cursor: default;

        &:active {
          background: #e5e7eb;
          color: #222;
        }
      }
    }
  }
}
</style>
