import Home from '@/views/front/Home'
import Problem from '@/views/front/problem/Problem'
import ProblemList from '@/views/front/problem/ProblemList'
import Discussion from '@/views/front/discussion/Discussion'
import DiscussionList from '@/views/front/discussion/DiscussionList'
import ContestDetail from '@/views/front/contest/ContestDetail'
import ContestList from '@/views/front/contest/ContestList'
import ContestProblemList from '@/views/front/contest/children/ContestProblemList'
import ContestSubmissionList from '@/views/front/contest/children/ContestSubmissionList'
import ContestAnnouncements from "@/views/front/contest/children/ContestAnnouncements"
import AIChat from '@/views/front/chat/AIChat'
import UserHome from '@/views/front/user/UserHome'
import Setting from '@/views/front/user/Setting'
import Logout from '@/views/front/user/Logout'
import ResourceList from '@/views/front/resource/ResourceList'
import AboutOur from '@/views/front/about/AboutOur'
import NotFound from '@/views/404.vue'

const frontRoutes = [
    {
        path: '/',
        redirect: '/home',
        component: Home,
        meta: { title: '首页' }
    },
    {
        path: '/home',
        name: 'Home',
        component: Home,
        meta: { title: '首页' }
    },
    {
        path: '/problem',
        name: 'ProblemList',
        component: ProblemList,
        meta: { title: '题目' }
    },
    {
        path: '/problem/:problemID',
        name: 'ProblemDetail',
        component: Problem,
        meta: { title: '题目详情' }
    },
    {
        path: '/discussion',
        name: 'DiscussionList',
        component: DiscussionList,
        meta: { title: '讨论' }
    },
    {
        path: '/discussion/:discussionID',
        name: 'DiscussionDetail',
        component: Discussion,
        meta: { title: '讨论详情' }
    },
    {
        path: '/contest',
        name: 'ContestList',
        component: ContestList,
        meta: { title: '考核' }
    },
    {
        path: '/resource',
        name: 'ResourceList',
        component: ResourceList,
        meta: { title: '资源' }
    },
    {
        path: '/about',
        name: 'AboutOur',
        component: AboutOur,
        meta: { title: '关于' }
    },
    {
        path: '/contest/:contestID',
        name: 'ContestDetail',
        component: ContestDetail,
        meta: { title: '考核详情' },
        children: [
            {
                name: 'ContestSubmissionList',
                path: 'submissions',
                component: ContestSubmissionList,
                meta: { title: '考核提交' }
            },
            {
                name: 'ContestProblemList',
                path: 'problems',
                component: ContestProblemList,
                meta: { title: '题目列表' }
            },
            {
                name: 'ContestProblemDetail',
                path: 'problem/:displayId/',
                component: Problem,
                meta: { title: '考核题目详情' }
            },
            {
                name: 'ContestAnnouncementList',
                path: 'announcements',
                component: ContestAnnouncements,
                meta: { title: '考核公告' }
            },
        ]
    },
    {
        name: 'UserHome',
        path: '/user-home',
        component: UserHome,
        meta: { requireAuth: true, title: '我的主页' }
    },
    {
        name: 'AIChat',
        path: '/ai-chat',
        component: AIChat,
        meta: { requireAuth: true, title: 'AI对话' }
    },
    {
        name: 'Setting',
        path: '/setting',
        component: Setting,
        meta: { requireAuth: true, title: '我的设置' }
    },
    {
        name: 'Logout',
        path: '/logout',
        component: Logout,
        meta: { requireAuth: true, title: '退出登录' }
    },
    {
        name: 'NotFound',
        path: '*',
        component: NotFound,
        meta: { title: '404' }
    },
]

export default frontRoutes