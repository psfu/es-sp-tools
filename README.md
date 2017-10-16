# ES-SP-TOOLS

### About:
* A plugin for Elasticsearch
* SP means special 
* It is able to provide:
* * authencation with cookie to Http request and authencation with an ip table to both Http and Transport request. 
* * high speed to record the access info include remoteIp with a variation of lock free queue which be described in the paper, it record both Http and transport request.
* * including a console, it can input and review command and result like linux console of "_cat"'s command.
* * the service and functions, which can start and stop or define with /_sp/xxx, and the service and functions can be seted in the console. 


### Using:
* help info: 
  > in the console (_console) type sp/help or using url _sp/help
* get service info: 
  > EX: in the console (_console) type sp/logger/stat or using url _sp/logger/stat	
* start/stop service: 
  > EX: in the console (_console) type sp/logger/start?key=xxx or using url _sp/logger/stop?key=xxx
* set service: 
  > EX: in the console (_console) type sp/auther/settings?restIpMap=127.0.0.1:true;127.0.0.1:true;&key=xxx or using url _sp/auther/settings?restIpMap=127.0.0.1:true;127.0.0.1:true;&key=xxx
* authenticate current browser: 
  > in the console (_console) type sp/auth?key=xxx or using url _sp/auth?key=xxx
* use the kibana head kopf:
  > kibana: using the authencation of iptable , head: using the authencation of browser, kopf: using the authencation of browser


### Installation:
* download the zip file in the dist folder.
* undeploy the zip file in ./plugins in folder of Elasticsearch, and then it will create a folder named "sp-tools".
* config the plugin with editing the config.properties.(sp.tools.path.name can set in elasticsearch.yml)
* config.properties:
<pre>
# the key of the roles for the permissions, using it like this: _sp/auther/stop?key=admin
admin.adminKey=admin
# the key of the roles for the permissions, using it like this: _sp/auther/stop?key=admin 
admin.userKey=go
#the simple seed, which using for authentication with cookie
auth.seed=33024
#switch the logger
logger.using=true;
#switch the auther
auther.using=true;
</pre>

### Details
* the cookie is using an simple algorithm, which is changes every time when be generated by the seed, and the algorithm is very fast to validate, since it's very simple :)
* the ES do not add the remote IP info in the Http request in the version of 5.4.x and previous, and we may get it from the channel of Netty. ... 

### ToDo:
* complete the document
* add validation functions
* add authentication fonction with user
* separate the console to a single project
* import the utils of Shui from the project of Waterwave


### The Console:
Sp-tools:
[download](https://raw.githubusercontent.com/psfu/es-sp-tools/master/dist/0.1.1.alpha/sp-tools.zip)

be able to download the console only(If using the full Sp-tools, please remove the console and install the Sp-tools.):
[download](https://raw.githubusercontent.com/psfu/es-sp-console/master/dist/0.1.1.alpha/sp-tools.zip)  

![00-console.png](https://raw.githubusercontent.com/psfu/es-sp-tools/master/info/00-console.png)







 

