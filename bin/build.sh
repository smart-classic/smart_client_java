#cp  ivy-2.2.0.jar lib-codegen
ant -lib ../lib-codegen -lib ../lib  -f build-with-ivy.xml  -Dsmart_owl_path="/home/nate/SMArt/smart_server/smart/document_processing/schema/smart.owl"

# -D<property>=<value>
