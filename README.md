spring-cloud-study：这个工程是spring boot 和 spring cloud学习工程，此工程学习了didispace/SpringBoot-Learning内容。<br/>
<b>工程介绍</b><br/>
在此工程的基础上增加了基于spring cloud的TCC分布式事务功能。工程正在开发中，还不完善，
工程中目前只实现了基于TCC的提交方式。<br/>
<b>eureka-server：</b><br/>
&nbsp;&nbsp;此工程为注册中心<br/>
<b>compute-service：</b><br/>
&nbsp;&nbsp;&nbsp;&nbsp;此工程为服务提供端。<br/>
<b>eureka-feign：</b><br/>
&nbsp;&nbsp;此工程为基于feign的客户端（消费端），具有客户端负载均衡机制。<br/>
<b>eureka-feign-hystrix：</b><br/>
&nbsp;&nbsp;此工程为基于feign和hystrix的客户端（消费端），具有客户端负载均衡机制。<br/>
<b>eureka-ribbon：</b><br/>
&nbsp;&nbsp;此工程为基于ribbon的客户端（消费端），具有客户端负载均衡机制。<br/>
<b>eureka-ribbon-hystrix：</b><br/>
&nbsp;&nbsp;此工程为基于ribbon和hystrix的客户端（消费端），具有客户端负载均衡机制。<br/>
<b>tcc-transaction：</b><br/>
&nbsp;&nbsp;此工程为TCC分布式事务的核心逻辑。<br/>
<b>tcc-transaction-manager：</b><br/>
&nbsp;&nbsp;此工程为TCC分布式事务的管理端，作用为处理分布事务出现异常提交或回滚等的重试策略。<br/>