import Login from '@/views/admin/Login'
import Home from '@/views/admin/Home'
import Dashboard from '@/views/admin/Dashboard'
import User from '@/views/admin/general/User'
import Announcement from '@/views/admin/general/Announcement'
import StudioAward from '@/views/admin/general/StudioAward'
import StudioInfo from '@/views/admin/general/StudioInfo'
import Problem from '@/views/admin/problem/Problem'
import ProblemList from '@/views/admin/problem/ProblemList'
import Tag from '@/views/admin/problem/Tag'
import Discussion from '@/views/admin/discussion/Discussion'
import Category from '@/views/admin/discussion/Category'
import Contest from '@/views/admin/contest/Contest'
import ContestList from '@/views/admin/contest/ContestList'
import ContestUserList from '@/views/admin/contest/children/ContestUserList'
import ResourceList from '@/views/admin/resource/ResourceList'
import ResourceTag from '@/views/admin/resource/ResourceTag'


const adminRoutes = [
    {
        path: '/admin/login',
        name: 'admin-login',
        component: Login,
        meta: { title: 'login' }
    },
    {
        path: '/admin/',
        component: Home,
        meta: { requireAuth: true, requireAdmin: true },
        children: [
            {
                path: '',
                redirect: 'dashboard',
                component: Dashboard,
                meta: { title: 'Dashboard' }
            },
            {
                path: 'dashboard',
                name: 'admin-dashboard',
                component: Dashboard,
                meta: { title: '仪表盘' }
            },
            {
                path: 'user',
                name: 'admin-user',
                component: User,
                meta: { title: '用户管理' }
            },
            {
                path: 'announcement',
                name: 'admin-announcement',
                component: Announcement,
                meta: { title: '通告管理' }
            },
            {
                path: 'studio-info',
                name: 'admin-studio-info',
                component: StudioInfo,
                meta: { title: '成员信息' }
            },
            {
                path: 'studio-award',
                name: 'admin-studio-award',
                component: StudioAward,
                meta: { title: '奖项管理' }
            },
            {
                path: 'resource',
                name: 'admin-resource',
                component: ResourceList,
                meta: { title: '资源列表' }
            },
            {
                path: 'resource/tag',
                name: 'admin-resource-tag',
                component: ResourceTag,
                meta: { title: '资源标签' }
            },
            {
                path: 'problem',
                name: 'admin-problem-list',
                component: ProblemList,
                meta: { title: '题目列表' }
            },
            {
                path: 'problem/create',
                name: 'admin-create-problem',
                component: Problem,
                meta: { title: '题目详情' }
            },
            {
                path: 'problem/edit/:problemId',
                name: 'admin-edit-problem',
                component: Problem,
                meta: { title: '编辑题目' },
            },
            {
                path: 'problem/tag',
                name: 'admin-problem-tag',
                component: Tag,
                meta: { title: '题目标签' }
            },
            {
                path: 'discussion',
                name: 'admin-discussion',
                component: Discussion,
                meta: { title: '贴子管理' }
            },
            {
                path: 'discussion/category',
                name: 'admin-discussion-category',
                component: Category,
                meta: { title: '贴子分类' }
            },
            {
                path: 'contest/create',
                name: 'admin-create-contest',
                component: Contest,
                meta: { title: '创建考核' }
            },
            {
                path: 'contest',
                name: 'admin-contest-list',
                component: ContestList,
                meta: { title: '考核列表' }
            },
            {
                path: 'contest/:contestId/edit',
                name: 'admin-edit-contest',
                component: Contest,
                meta: { title: '编辑考核' }
            },
            {
                path: 'contest/:contestId/problems',
                name: 'admin-contest-problem-list',
                component: ProblemList,
                meta: { title: '考核题目列表' }
            },
            {
                path: 'contest/:contestId/problem/:problemId/edit',
                name: 'admin-edit-contest-problem',
                component: Problem,
                meta: { title: '编辑考核题目' }
            },
            {
                path: 'contest/:contestId/user',
                name: 'admin-contest-user-list',
                component: ContestUserList,
                meta: { title: '参加考核用户列表' }
            },
        ]
    },
    {
        path: '/admin/*', redirect: '/admin/login'
    }
]

export default adminRoutes