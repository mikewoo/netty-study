## SSL证书生成步骤
### 1. 生成服务端证书
生成服务端私钥和证书仓库命令：

    keytool -genkey -alias netty -keysize 2048 -validity 365 -keyalg RSA -dname "CN=localhost"
     -keypass gmm123 -storepass gmm123 -keystore server.jks

生成服务端自签名证书：
  
    keytool -export -alias netty -keystore server.jks -storepass gmm123 -file server.cer
    
### 2. 生成客户端证书
生成客户端的密钥对和证书仓库，用于将服务端的证书保存到客户端的授信证书仓库中

    keytool -genkey -alias netty -keysize 2048 -validity 365 -keyalg RSA -dname "CN=localhost" -keypass gmm
    123 -storepass gmm123 -keystore client.jks
 
将服务端的证书导入到客户端的证书仓库中
   
    keytool -import -trustcacerts -alias client -file ../server\server.cer -storepass gmm123 -keystore clie
    nt.jks
    
查看证书信息

    keytool -list -v -keystore client.jks -storepass gmm123

### 3. ssl握手过程log输出配置

    -Djavax.net.debug=ssl,handshake   
    -Ddeployment.security.TLSv1.1=true
    -Ddeployment.security.TLSv1.2=true  
启动服务时，添加以上jvm参数即可