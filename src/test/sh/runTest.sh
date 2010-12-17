java -cp ../../../target/classes:../../../target/test-classes:\
/home/nate/java/oauth/signpost/1.2.1.1/mySignpost.jar:\
/home/nate/.m2/repository/org/apache/httpcomponents/httpcore/4.0.1/httpcore-4.0.1.jar:\
/home/nate/.m2/repository/org/apache/httpcomponents/httpclient/4.0.1/httpclient-4.0.1.jar:\
/home/nate/.m2/repository/commons-logging/commons-logging/1.1.1/commons-logging-1.1.1.jar:\
/home/nate/.m2/repository/commons-codec/commons-codec/1.3/commons-codec-1.3.jar:\
/home/nate/SMArt/openrdf-sesame-2.3.2/lib/sesame-repository-api-2.3.2.jar:\
/home/nate/SMArt/openrdf-sesame-2.3.2/lib/sesame-model-2.3.2.jar:\
/home/nate/SMArt/openrdf-sesame-2.3.2/lib/sesame-rio-api-2.3.2.jar:\
/home/nate/SMArt/openrdf-sesame-2.3.2/lib/sesame-sail-api-2.3.2.jar \
org.smartplatforms.client.TestClient  \
  developer-sandbox@apps.smartplatforms.org   smartapp-secret \
             http://sandbox-api.smartplatforms.org/oauth/request_token \
             http://sandbox-api.smartplatforms.org/oauth/access_token \
             http://sandbox.smartplatforms.org/oauth/authorize \
             oob


#/home/nate/.m2/repository/oauth/signpost/signpost-core/1.2.1.1/signpost-core-1.2.1.1.jar:\
#/home/nate/.m2/repository/oauth/signpost/signpost-commonshttp4/1.2.1.1/signpost-commonshttp4-1.2.1.1.jar:\
