# waterwave

### About:
* A plugin for Elasticsearch 
* It can provide:
* * auth with cookie to Http request and auth with an ip table to both Http and Transport request. 
* * high speed record the access info include remoteIp, in Http and transport request.
* * include a console, it can input and review "_cat"'s command and result like linux console.
* * the plugin's function can start/stop or define with /_sp, it also can set in the console. 


### using:
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



### installation:
* undeploy zip file in folder ./plugins it will create a folder named "tools-sp".
* using config.properties to config the plugin
*  
<pre>
# the role key with the permission, using it like this _sp/auther/stop?key=admin
admin.adminKey=admin
# the role key with the permission, using it like this _sp/auther/stop?key=admin 
admin.userKey=go
#the simple seed using in auth cookie
auth.seed=33024
</pre>

### ToDo:
* complete the document
* add validation functions
* add authentication by user


###







 

