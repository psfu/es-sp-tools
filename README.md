# ES-SP-TOOLS

### About:
* A plugin for Elasticsearch
* SP means special 
* It can provide:
* * auth with cookie to Http request and auth with an ip table to both Http and Transport request. 
* * high speed record the access info include remoteIp, in Http and transport request.
* * include a console, it can input and review "_cat"'s command and result like linux console.
* * the plugin's function can start/stop or define with /_sp, it also can set in the console. 


### Using:
* help info: 
  > in the console (_console) type sp/help or using url _sp/help
* get service info: 
  > EX: in the console (_console) type sp/logger/stat or using url _sp/logger/stat	
* start/stop service: 
  > EX: in the console (_console) type sp/logger/start?key=xxx or using url _sp/logger/stop?key=xxx
* set service: 
  > EX: in the console (_console) type sp/auther/settings?restIpMap=127.0.0.1:true;127.0.0.1:true;&key=xxx or using url _sp/auther/settings?restIpMap=127.0.0.1:true;127.0.0.1:true;&key=xxx
* authentication current browser: 
  > in the console (_console) type sp/auth?key=xxx or using url _sp/auth?key=xxx
* using the kibana head kopf:
  > kibana: iptable, head: auth, kopf: auth


### Installation:
* download zip file in dist
* undeploy zip file in folder ./plugins it will create a folder named "tools-sp".
* using config.properties to config the plugin
* sp.tools.path.name can set in elasticsearch.yml
<pre>
# the role key with the permission, using it like this _sp/auther/stop?key=admin
admin.adminKey=admin
# the role key with the permission, using it like this _sp/auther/stop?key=admin 
admin.userKey=go
#the simple seed using in auth cookie
auth.seed=33024
</pre>

### Details
* the cookie using an simple algorithms, it changes every time when generate, and very fast to validate because very simple :)
* the ES do not have the remote IP info in the request in this version 5.4.x and may get it from netty channel. ... 

### ToDo:
* complete the document
* add validation functions
* add authentication by user
* import the shui utils form the Waterwave project

#### The Console:
 ![00-console.png](https://github.com/psfu/es-sp-tools/raw/master/info/00-console.png)







 

