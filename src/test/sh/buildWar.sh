rm -r ../../../testWar
mkdir ../../../testWar
mkdir ../../../testWar/WAR
mkdir ../../../testWar/WAR/WEB-INF
mkdir ../../../testWar/WAR/WEB-INF/lib
mkdir ../../../testWar/WAR/WEB-INF/classes
mkdir ../../../testWar/WAR/WEB-INF/classes/org
mkdir ../../../testWar/WAR/WEB-INF/classes/org/smartplatforms
mkdir ../../../testWar/WAR/WEB-INF/classes/org/smartplatforms/client

cp ../../../target/classes/org/smartplatforms/client/DefaultResponseTypeConversion.class ../../../testWar/WAR/WEB-INF/classes/org/smartplatforms/client
cp ../../../target/classes/org/smartplatforms/client/IndivoHttpEntity.class ../../../testWar/WAR/WEB-INF/classes/org/smartplatforms/client
cp ../../../target/classes/org/smartplatforms/client/OAuthProviderListenerForSMArt.class ../../../testWar/WAR/WEB-INF/classes/org/smartplatforms/client
cp ../../../target/classes/org/smartplatforms/client/ResponseTypeConversion.class ../../../testWar/WAR/WEB-INF/classes/org/smartplatforms/client
cp ../../../target/classes/org/smartplatforms/client/SMArtClient.class ../../../testWar/WAR/WEB-INF/classes/org/smartplatforms/client
cp ../../../target/classes/org/smartplatforms/client/SMArtClientException.class ../../../testWar/WAR/WEB-INF/classes/org/smartplatforms/client
cp ../../../target/classes/org/smartplatforms/client/SMArtClientExceptionHttp404.class ../../../testWar/WAR/WEB-INF/classes/org/smartplatforms/client
cp ../../../target/classes/org/smartplatforms/client/Utils.class ../../../testWar/WAR/WEB-INF/classes/org/smartplatforms/client
cp ../../../target/classes/org/smartplatforms/client/Utils.class ../../../testWar/WAR/WEB-INF/classes/org/smartplatforms/client
cp ../../../target/test-classes/org/smartplatforms/client/TestServlet.class ../../../testWar/WAR/WEB-INF/classes/org/smartplatforms/client
cp /home/nate/SMArt/openrdf-sesame-2.3.2/openrdf-sesame-2.3.2-onejar.jar  ../../../testWar/WAR/WEB-INF/lib


cp ../web.xml ../../../testWar/WAR/WEB-INF

# index.html and after_auth.html go to webapps/ROOT
#cp ../index.html ../../../testWar/WAR/WEB-INF
#cp ../after_auth.html ../../../testWar/WAR/WEB-INF

cp /home/nate/.m2/repository/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar ../../../testWar/WAR/WEB-INF/lib
cp /home/nate/.m2/repository/commons-codec/commons-codec/1.3/commons-codec-1.3.jar ../../../testWar/WAR/WEB-INF/lib
#cp /home/nate/.m2/repository/oauth/signpost/signpost-core/1.2.1.1/signpost-core-1.2.1.1.jar ../../../testWar/WAR/WEB-INF/lib
#cp /home/nate/.m2/repository/oauth/signpost/signpost-commonshttp4/1.2.1.1/signpost-commonshttp4-1.2.1.1.jar ../../../testWar/WAR/WEB-INF/lib
cp /home/nate/java/oauth/signpost/1.2.1.1/mySignpost.jar ../../../testWar/WAR/WEB-INF/lib
cp /home/nate/.m2/repository/org/apache/httpcomponents/httpcore/4.0.1/httpcore-4.0.1.jar ../../../testWar/WAR/WEB-INF/lib
cp /home/nate/.m2/repositoryBK1/org/apache/httpcomponents/httpclient/4.0.1/httpclient-4.0.1.jar ../../../testWar/WAR/WEB-INF/lib
jar -cf ../../../SMArtClientJavaOAuthTester.war -C ../../../testWar/WAR WEB-INF 

md5sum ../../../SMArtClientJavaOAuthTester.war > ../../../md5sum
tar -cf ../../../SMArtClientJavaOAuthTester.tar  -C ../../.. SMArtClientJavaOAuthTester.war  md5sum
