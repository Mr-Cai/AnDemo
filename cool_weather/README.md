# 小酷天气
MVVM 架构
<img src="https://raw.githubusercontent.com/guolindev/coolweatherjetpack/master/images/architecture.jpg" />
绿色:界面控制层,例:Activity|Fragment

蓝色:视图模型层,用于持有和UI元素相关的数据,以保证这些数据在屏幕旋转时不会丢失,以及负责和仓库之间进行通讯.

灰色:本地数据层,使用LitePal进行数据持久化处理

红色:网络数据层,使用Retrofit请求Web接口数据

箭头:持有一个引用,不能反转且无法跨层持有,每一层的组件都只能和它的相邻层交互

黄色:仓库层,用于自主判断接口请求的数据应该是从数据库中读取还是从网络中获取,并将数据返回给调用方.
仓库扮演中介角色,负责本地和在线数据的分配调度,让调用者更好更快地获取数据,调用者只需获取,不管数据的源头
例: NetWork -> DB : DB (缓存网络数据至本地数据库,下次调用从本地获取,减少重复的网络请求.)
