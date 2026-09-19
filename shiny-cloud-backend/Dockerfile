# syntax=docker/dockerfile:1.7
# ↑ 声明使用新版 Dockerfile 语法，才能用 --mount=type=bind / type=cache 这些特性。
#   必须放在文件第一行，且前面不能有空行或注释（这一行本身是特殊注释）。

# ================= 阶段一：builder（构建阶段）=================
FROM maven:3.9-eclipse-temurin-17-alpine AS builder
WORKDIR /build

# ---------- 1. 声明构建参数 ----------
ARG MODULE
# 通过 docker build --build-arg MODULE=xxx 传入要构建的模块路径，
# 例如 gateway、service/service-user（相对仓库根目录，和父 pom 里的 <module> 对应）。
# ARG 只在构建期有效，容器运行时读不到；想运行时也用就 ENV MODULE=${MODULE}。

# ---------- 2. 先只拷贝所有 pom.xml（不拷源码）----------
# 目的：把"依赖声明"和"源码"分成独立的两层。
# Maven 判断要不要重下依赖只看 pom 变没变，不看源码。
# 所以先 COPY pom 单独成层 —— pom 不改时，下面的依赖预下载层永远命中缓存，
# 不必每次构建都重新拉几百 MB 依赖。这是加速的核心技巧。

COPY pom.xml .
# 顶层父 pom，Maven 靠它找到所有 <module> 与 <dependencyManagement>。

COPY common/pom.xml common/
COPY common/common-core/pom.xml common/common-core/
COPY common/common-web/pom.xml common/common-web/
# 被 gateway 依赖的公共模块的 pom。

COPY gateway/pom.xml gateway/
COPY service/pom.xml service/
# 目标模块 gateway，以及同级聚合模块 service（-am 需要沿着模块树往上找父 pom）。
# 注意：此刻这些目录里只有 pom，没有一行 Java 源码。

# ---------- 3. 预下载依赖到本地仓库 ----------
RUN --mount=type=cache,target=/root/.m2 \
    mvn -pl ${MODULE} -am dependency:go-offline -B || true

# --mount=type=cache,target=/root/.m2
#   BuildKit 的缓存挂载：把 Maven 本地仓库 ~/.m2 存成可复用的缓存卷，
#   跨次构建、跨模块共享，比让它固化进镜像层更高效，也不会污染最终镜像。
#
# mvn 参数：
#   -pl ${MODULE}  --projects，只针对传入的模块操作
#   -am            --also-make，同时构建它依赖的模块（common-core 等），
#                  否则解析不到依赖
#   dependency:go-offline
#                  只下载依赖到 ~/.m2，不编译代码
#   -B             --batch-mode，非交互，CI 里必须加，否则可能卡住等输入
#   || true        go-offline 有时会因个别插件/依赖解析失败而报错退出，
#                  加 || true 让它即使失败也不中断构建 —— 反正下一步真正的
#                  package 会重新拉。属"能预下载就赚，不能也别挡路"的容错写法。
#
# 这一层跑完效果：依赖灌进缓存，源码还没进来。

# ---------- 4. 拷入目标模块的真实源码 ----------
RUN --mount=type=bind,source=.,target=/src \
    cp -r /src/${MODULE}/. ./${MODULE}/

# 为什么要绕这一圈：
#   COPY 指令的目标路径不支持变量展开，不能直接写 COPY ${MODULE} ./${MODULE}。
# 解决：用 --mount=type=bind 把整个构建上下文只读挂到 /src，
#      再用 shell 的 cp 按变量把目标模块拷进来。
#      注意用 /src/${MODULE}/. 结尾 —— 拷"内容"而不是"目录本身"，
#      否则会和上一步已有的 pom 目录嵌套成 gateway/gateway。
#
# 这一层单独存在的原因：让"拷源码"和"编译"分层。
# 只改源码不改 pom 时，依赖缓存和预下载层都仍命中。

# ---------- 5. 编译打包 ----------
RUN --mount=type=cache,target=/root/.m2 \
    mvn -pl ${MODULE} -am package -DskipTests -B

# 同样挂 .m2 缓存，避免重复下载。
#   -pl ${MODULE} -am  同上：构建目标模块及其依赖模块
#   package            生命周期目标：编译 + 测试 + 打包成 jar
#                      产出在 <module>/target/*.jar
#   -DskipTests        跳过"运行测试"，但仍会编译测试代码。
#                      想连测试代码都别编译用 -Dmaven.test.skip=true。
#                      镜像构建里跳过测试，是因为测试该在 CI 阶段做。
#   -B                 非交互
#
# 结果：jar 落在 /build/<MODULE>/target/ 下，供下一阶段 COPY。

# ================= 阶段二：runner（运行阶段）=================
FROM bellsoft/liberica-openjre-alpine:17
WORKDIR /app

# ---------- 6. 设置时区 ----------
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# ENV TZ=Asia/Shanghai  声明环境变量 TZ，容器运行时也可读，
#                       JVM、logback 等都会用东八区。
# 第一个 RUN 做两件事（&& 串联）：
#   ln -snf /usr/share/zoneinfo/$TZ /etc/localtime
#       把系统本地时间软链到上海时区文件
#       -s 建软链 / -n 把已存在的软链当普通文件替换 / -f 已存在就覆盖
#   echo $TZ > /etc/timezone
#       写入 /etc/timezone，某些程序（Debian 系工具）读这个文件。
# 两件事都做才保险：不同程序读时区的方式不同。
# 不设的话 alpine 默认 UTC，日志时间、@Scheduled 定时任务会差 8 小时。

# ---------- 7. 从 builder 阶段拷出 jar ----------
ARG MODULE
COPY --from=builder /build/${MODULE}/target/*.jar app.jar

# 关键点：
#   ① runner 阶段要重新声明 ARG MODULE —— ARG 的作用域只在自己那个 FROM 块内，
#      跨阶段不会自动继承，不重新声明这里会展开成空字符串。
#   ② COPY --from=builder 的【源路径】支持变量展开，所以 ${MODULE} 在这里能用
#      （本地 COPY 的源/目标都支持变量，唯独"从上下文拷目录"那种不受变量控制）。
#   ③ --from=builder 从上一阶段拷，最终镜像里没有 Maven、没有源码，
#      体积从几百 MB 降到几十 MB。
#   ④ 用 * 通配 jar，因为 Spring Boot 打包产物通常带版本号；
#      重命名成固定的 app.jar，CMD 里就不用跟着版本改。
#   ⑤ 若一个模块产出多个 jar，改用具体名字避免 COPY 到多个文件报错。

# ---------- 8. 端口与启动命令 ----------
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]