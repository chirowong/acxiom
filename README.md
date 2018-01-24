# 环境
    elasticsearch:2.4.6
    jdk:1.8
    maven:3.3.3
    
# 初始化数据
    curl -XPOST 'http://localhost:9200/acxiom/user/1?pretty' -H 'Content-Type: application/json'  -d'{"name":"汪志亮","age":34,"city":"南通","gender":"男"}';
    curl -XPOST 'http://localhost:9200/acxiom/user/2?pretty' -H 'Content-Type: application/json'  -d'{"name":"李蕾","age":28,"city":"杭州","gender":"女"}';
    curl -XPOST 'http://localhost:9200/acxiom/user/3?pretty' -H 'Content-Type: application/json'  -d'{"name":"刘欣雨","age":32,"city":"成都","gender":"女"}';
    curl -XPOST 'http://localhost:9200/acxiom/user/4?pretty' -H 'Content-Type: application/json'  -d'{"name":"孔丘","age":25,"city":"重庆","gender":"男"}';
    curl -XPOST 'http://localhost:9200/acxiom/user/5?pretty' -H 'Content-Type: application/json'  -d'{"name":"大兵","age":40,"city":"包头","gender":"男"}';
    curl -XPOST 'http://localhost:9200/acxiom/user/6?pretty' -H 'Content-Type: application/json'  -d'{"name":"刘畅","age":36,"city":"哈尔滨","gender":"女"}';
    curl -XPOST 'http://localhost:9200/acxiom/user/7?pretty' -H 'Content-Type: application/json'  -d'{"name":"李芳菲","age":27,"city":"吉林","gender":"女"}';
    curl -XPOST 'http://localhost:9200/acxiom/user/8?pretty' -H 'Content-Type: application/json'  -d'{"name":"张斌","age":28,"city":"天津","gender":"男"}';
    curl -XPOST 'http://localhost:9200/acxiom/user/9?pretty' -H 'Content-Type: application/json'  -d'{"name":"邱少云","age":30,"city":"南昌","gender":"男"}';
    curl -XPOST 'http://localhost:9200/acxiom/user/10?pretty' -H 'Content-Type: application/json'  -d'{"name":"邵琪","age":29,"city":"北京","gender":"女"}';
 
# 启动

### 源码运行
 
项目为Spring Boot 项目
修改resources中 application.properties文件
    
    server.port=8080 端口号
    
    # ES
    spring.data.elasticsearch.cluster-nodes = 127.0.0.1:9300 elasticsearch节点地址
    
修改完成之后直接运行com.acxiom.interview.WebApplication即可启动
    
### jar包执行
deploy目录中的jar可以执行执行 执行命令：java -jar -Dfile.encoding=UTF-8 acxiom-interview-1.0.0-SNAPSHOT.jar
默认端口8080 
默认elasticsearch节点 127.0.0.1:9300
    
http://localhost:8080 可访问表示启动成功

# 测试用例
    curl -XPOST 'http://localhost:8080/user/search'  -d'query={"filter":[{"OR":[{"city":"南通"},{"city":"北京"}]},{"AND":[{"GTE":{"age":20}},{"LTE":{"age":41}}]},{"NOT":[{"gender":"男"}]}],"sort":[{"age":"asc"}],"offset":0,"rows":10}';
    
# 补充说明
    not 操作 在elasticsearch支持一个条件，因此虽然请求参数中not为数组，实际生效的只有第一个条件。
