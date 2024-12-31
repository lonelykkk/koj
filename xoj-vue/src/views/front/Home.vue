<template>
  <div class="home-container">
    <el-row :gutter="20">
      <el-col :md="15" :sm="24">
        <el-card>
          <div slot="header" class="content-center">
            <span class="panel-title home-title welcome-title">
              Welcome
            </span>
          </div>
          <el-carousel :interval="interval" :height="srcHight" class="img-carousel" arrow="always"
            indicator-position="outside">
            <el-carousel-item v-for="(item, index) in carouselImgList" :key="index">
              <el-image :src="item.url" fit="fill">
                <div slot="error" class="image-slot">
                  <i class="el-icon-picture-outline"></i>
                </div>
              </el-image>
            </el-carousel-item>
          </el-carousel>
        </el-card>
        <Announcements class="card-top"></Announcements>
        <SubmissionStatistic class="card-top"></SubmissionStatistic>
      </el-col>
      <el-col :md="9" :sm="24" class="phone-margin">
        <el-card>
          <div slot="header" class="clearfix title content-center">
            <div class="home-title home-contest">
              <i class="el-icon-trophy"></i>
              近期考核
            </div>
          </div>
          <template v-if="!contests.length">
            <div class="no-contest" key="no-contest">
              <el-empty description="暂无考核"></el-empty>
            </div>
          </template>
          <template v-else>
            <el-card shadow="hover" v-for="(contest, index) in contests" :key="index" class="contest-card" :class="contest.status == 0
              ? 'contest-card-running'
              : 'contest-card-schedule'
              ">
              <div slot="header" class="clearfix contest-header">
                <a class="contest-title" @click="goContest(contest.id)">
                  {{ contest.title }}
                </a>
                <div class="contest-status">
                  <el-tag effect="dark" size="medium" :color="CONTEST_STATUS_REVERSE[contest.status]['color']">
                    <i class="fa fa-circle" aria-hidden="true"></i>
                    {{
                      CONTEST_STATUS_REVERSE[contest.status]['name2']
                    }}
                  </el-tag>
                </div>
              </div>
              <div class="contest-type-auth">
                <template v-if="contest.type == 0">
                  <el-button :type="'primary'" round @click="goContestList(contest.type)" size="mini"
                    style="margin-right: 10px;">
                    <i class="fa fa-trophy"></i>
                    {{ contest.type | parseContestType }}
                  </el-button>
                </template>
                <el-tag :type="CONTEST_TYPE_REVERSE[contest.auth]['color']" size="medium" effect="plain">
                  {{ CONTEST_TYPE_REVERSE[contest.auth]['name'] }}
                </el-tag>
              </div>
              <ul class="contest-info">
                <li>
                  <el-button type="primary" round size="mini" style="margin-top: 4px;"><i class="fa fa-calendar"></i>
                    {{
                      contest.startTime | localtime((format = 'MM-DD HH:mm'))
                    }}
                  </el-button>
                </li>
                <li>
                  <el-button type="success" round size="mini" style="margin-top: 4px;"><i class="fa fa-clock-o"></i>
                    {{ getDuration(contest.startTime, contest.endTime) }}
                  </el-button>
                </li>
                <li>
                  <el-button size="mini" round plain v-if="contest.count != null">
                    <i class="el-icon-user-solid" style="color:rgb(48, 145, 242);"></i>x{{ contest.count }}
                  </el-button>
                </li>
              </ul>
            </el-card>
          </template>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import time from "@/common/time"
import api from "@/api/home"
import {
  CONTEST_STATUS_REVERSE,
  CONTEST_TYPE_REVERSE,
} from "@/common/constants"
import { mapState, mapGetters } from "vuex"
import Avatar from "vue-avatar"
import myMessage from "@/utils/message"
const Announcements = () => import("@/components/front/common/Announcements.vue")
const SubmissionStatistic = () => import("@/components/front/home/SubmissionStatistic.vue")
export default {
  name: 'Home',
  components: {
    Announcements,
    SubmissionStatistic,
    Avatar,
  },
  data() {
    return {
      interval: 5000,
      CONTEST_STATUS_REVERSE: {},
      CONTEST_TYPE_REVERSE: {},
      contests: [],
      loading: {
        recentContests: false,
      },
      carouselImgList: [
        {
          url: "https://x-o-j.oss-cn-hangzhou.aliyuncs.com/welcome01.jpg",
        },
        {
          url: "https://x-o-j.oss-cn-hangzhou.aliyuncs.com/welcome02.jpg",
        },
      ],
      srcHight: "440px",
    };
  },
  mounted() {
    let screenWidth = window.screen.width;
    if (screenWidth < 768) {
      this.srcHight = "200px";
    } else {
      this.srcHight = "440px";
    }
    this.CONTEST_STATUS_REVERSE = Object.assign({}, CONTEST_STATUS_REVERSE);
    this.CONTEST_TYPE_REVERSE = Object.assign({}, CONTEST_TYPE_REVERSE);
    // this.getHomeCarousel();
    this.getRecentContests();
    // this.getRecent7ACRank();
    // this.getRecentUpdatedProblemList();
  },
  methods: {

    getRecentContests() {
      this.loading.recentContests = true;
      api.getRecentContest().then(
        (res) => {
          this.contests = res.data.data;
          this.loading.recentContests = false;
        },
        (err) => {
          this.loading.recentContests = false;
        }
      );
    },
    goContest(cid) {
      if (!this.isAuthenticated) {
        myMessage.warning('请先登录！');
        this.$store.dispatch("changeModalStatus", { visible: true });
      } else {
        this.$router.push({
          name: "ContestDetail",
          params: { contestID: cid },
        });
      }
    },
    goContestList(type) {
      this.$router.push({
        name: "ContestList",
        query: {
          type,
        },
      });
    },
    getDuration(startTime, endTime) {
      return time.formatSpecificDuration(startTime, endTime);
    },
  },
  computed: {
    ...mapState(["websiteConfig"]),
    ...mapGetters(["isAuthenticated"]),
  },

}

</script>
<style lang="scss" scoped>
// .home-container {
//   // padding: 0 3%;
// }

::v-deep .el-card__header {
  padding: 0.6rem 1.25rem !important;
}

.card-top {
  margin-top: 20px;
}

.home-contest {
  text-align: left;
  font-size: 21px;
  font-weight: 500;
  line-height: 30px;
}


.el-carousel__item h3 {
  color: #475669;
  font-size: 14px;
  opacity: 0.75;
  line-height: 200px;
  margin: 0;
}

.contest-card {
  margin-bottom: 20px;
}

.contest-title {
  font-size: 1.15rem;
  font-weight: 600;
}

.contest-type-auth {
  text-align: center;
  margin-top: -10px;
  margin-bottom: 5px;
}

ul,
li {
  padding: 0;
  margin: 0;
  list-style: none;
}

.contest-info {
  text-align: center;
}

.contest-info li {
  display: inline-block;
  padding-right: 10px;
}

::v-deep .contest-card-running .el-card__header {
  border-color: rgb(25, 190, 107);
  background-color: rgba(94, 185, 94, 0.15);
}

.contest-card-running .contest-title {
  color: #5eb95e;
}

::v-deep .contest-card-schedule .el-card__header {
  border-color: #f90;
  background-color: rgba(243, 123, 29, 0.15);
}

.contest-card-schedule .contest-title {
  color: #f37b1d;
}

.content-center {
  text-align: center;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both;
}

.welcome-title {
  font-weight: 600;
  font-size: 25px;
  font-family: "Raleway";
}

.contest-status {
  float: right;
}

.img-carousel {
  height: 490px;
}

@media screen and (max-width: 768px) {
  .contest-status {
    text-align: center;
    float: none;
    margin-top: 5px;
  }

  .contest-header {
    text-align: center;
  }

  .img-carousel {
    height: 220px;
    overflow: hidden;
  }

  .phone-margin {
    margin-top: 20px;
  }
}

.title .el-link {
  font-size: 21px;
  font-weight: 500;
  color: #444;
}

.clearfix h2 {
  color: #409eff;
}

.el-link.el-link--default:hover {
  color: #409eff;
  transition: all 0.28s ease;
}

.contest .content-info {
  padding: 0 70px 40px 70px;
}

.contest .contest-description {
  margin-top: 25px;
}

span.rank-tag.no1 {
  line-height: 24px;
  background: #bf2c24;
}

span.rank-tag.no2 {
  line-height: 24px;
  background: #e67225;
}

span.rank-tag.no3 {
  line-height: 24px;
  background: #e6bf25;
}

span.rank-tag {
  font: 16px/22px FZZCYSK;
  min-width: 14px;
  height: 22px;
  padding: 0 4px;
  text-align: center;
  color: #fff;
  background: #000;
  background: rgba(0, 0, 0, 0.6);
}

.user-avatar {
  margin-right: 5px !important;
  vertical-align: middle;
}

.cite {
  display: block;
  width: 14px;
  height: 0;
  margin: 0 auto;
  margin-top: -3px;
  border-right: 11px solid transparent;
  border-bottom: 0 none;
  border-left: 11px solid transparent;
}

.cite.no0 {
  border-top: 5px solid #bf2c24;
}

.cite.no1 {
  border-top: 5px solid #e67225;
}

.cite.no2 {
  border-top: 5px solid #e6bf25;
}

@media screen and (min-width: 1050px) {
  ::v-deep .vxe-table--body-wrapper {
    overflow-x: hidden !important;
  }
}

.no-contest {
  text-align: center;
  font-size: 16px;
}
</style>
  