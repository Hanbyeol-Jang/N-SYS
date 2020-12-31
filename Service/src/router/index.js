import Vue from "vue";
import VueRouter from "vue-router";

Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    name: "Home",
    component: () => import("../components/Main/MainPage"),
    // component:()=> import('@/components/Main/new')
  },
  {
    path: "/users",
    name: "users",
    component: () =>
      import(/* webpackChunkName: "users" */ "../views/Users.vue"),
  },
  {

    path: '/game1',
    name: 'game1',
    component: () => import(/* webpackChunkName: "games" */ '../components/BrainWall/BrainWall.vue')
  },
  {
    path: '/game2',
    name: 'game2',
    component : ()=>import('@/components/BrainWall/BrainWall2.vue')

  },
  {
    path: '/2048',
    name: '2048',
    component : ()=>import('@/components/Game/game2048.vue')
  },
  {
    path: '/game3',
    name: 'game3',
    component : ()=>import('@/components/SnakeGame/Snake.vue')
  },
  {
    path: "/myprofile",
    name: "myprofile",
    component: () =>
      import(/* webpackChunkName: "myprofile" */ "../views/Myprofile.vue"),
  },
  {
    path: "/changenickname",
    name: "changenickname",
    component: () =>
      import(
        /* webpackChunkName: "changenickname" */ "../views/changenickname.vue"
      ),
  },
  {
    path: "/changepw",
    name: "changepw",
    component: () =>
      import(/* webpackChunkName: "changepw" */ "../views/changepw.vue"),
  },
  {
    path: "/changepwbyemailjs",
    name: "changepwbyemailjs",
    component: () =>
      import(
        /* webpackChunkName: "changepwbyemailjs" */ "../views/changepwbyemailjs.vue"
      ),
  },
];


export default new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes,
});
